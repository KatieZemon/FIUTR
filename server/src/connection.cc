/* -*- indent-tabs-mode: nil; c-basic-offset: 2; tab-width: 2 -*-  */
/*
 * connection.cc
 * Copyright (C) 2013 Michael Catanzaro <michael.catanzaro@mst.edu>
 *
 * This file is part of groupgd.
 *
 * groupgd is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * groupgd is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

#include "connection.h"

#include <functional>
#include <sstream>
#include <string>

#include <boost/asio.hpp>
#include <boost/property_tree/xml_parser.hpp>
#include <boost/system/error_code.hpp>
#include <boost/system/system_error.hpp>
#include <systemd/sd-daemon.h>

#include "network.h"
#include "utility.h"

namespace groupgd {

/**
 * Construct a new Connection.
 *
 * @param io_service the io_service that will schedule all the operations of
 *                   this connection
 */
Connection::Connection(boost::asio::io_service* io_service)
: deadline_timer_(*io_service), socket_(*io_service)
{ }

/**
 * Attempts to close the socket before the Connection is destroyed.
 */
Connection::~Connection()
{
  try
  {
    safe_journal(SD_DEBUG, "Client "
        + socket_.remote_endpoint().address().to_string() + " disconnected");
    stop();
  }
  catch (std::exception& e)
  {
    // FIXME catching lots of "bad file descriptor" errors :(
    safe_journal(SD_ERR, e.what());
  }
}

/**
 * Call after a client has been accepted to initiate the client/server protocol.
 */
void
Connection::async_run()
{
  safe_journal(SD_DEBUG,
               "Accepted " + socket_.remote_endpoint().address().to_string());
  deadline_timer_.async_wait(std::bind(&Connection::on_deadline_timer_expired,
                                       shared_from_this(),
                                       std::placeholders::_1));
  async_await_client_query();
}

/**
 * Start waiting for a request from the client.
 */
void
Connection::async_await_client_query()
{
  deadline_timer_.expires_from_now(TIMEOUT);
  boost::asio::async_read_until(socket_, streambuf_, "\r\n",
                                std::bind(&Connection::on_read_completed,
                                          shared_from_this(),
                                          std::placeholders::_1));
}

/**
 * FIXME see BUGS
 */
void
Connection::on_deadline_timer_expired(const boost::system::error_code&)
{
}

/**
 * After a read has been completed, respond to the request.
 *
 * @param ec indicates an error in performing the read
 */
void
Connection::on_read_completed(const boost::system::error_code& ec)
{
  if (!ec)
    {
      auto query = read_line_from_streambuf(&streambuf_);
      safe_journal(SD_DEBUG, "Read from client: " + query);
      // These operations are themselves responsible for calling
      // async_await_client_query when they are finished.
      if (query == "GET NETWORKS")
        async_send_networks_to_client_as_ptree();
      // FIXME ugly and undocumented
      else if (query == "GET NETWORKS SUPER SPECIAL")
        async_send_networks_to_client_as_string();
      else if (query.find("ADD NETWORK") == 0)
        async_add_network_to_database(query);
      else if (query.find("ERROR") == 0)
        handle_error_claim(query);
      else
        safe_journal(SD_WARNING, std::string("Unexpected query: ") + query);
    }
  else if (ec != boost::asio::error::operation_aborted
           && ec != boost::asio::error::eof)
    {
      throw boost::system::system_error{ec};
    }
}

/**
 * After a write has completed, await another request from the client.
 *
 * @param ec indicates an error in performing the write
 */
void
Connection::on_write_completed(const boost::system::error_code& ec,
                               std::size_t /*bytes_transferred*/)
{
  if (!ec)
    {
      async_await_client_query();
    }
  else if (ec != boost::asio::error::operation_aborted)
    {
      throw boost::system::system_error{ec};
    }
}

/**
 * Interprates an ADD_NETWORK command from the client and adds the network
 * to the database.
 *
 * @param query the complete ADD NETWORK command, minus trailing CRLF
 */
void
Connection::async_add_network_to_database(std::string query)
{
  // TODO not very async
  // TODO handle absurd values
  std::istringstream iss{query};
  // Discard the command ADD NETWORK
  std::string trash;
  iss >> trash >> trash;
  Network network;
  iss >> network.name;
  iss >> network.lat;
  iss >> network.lon;
  iss >> network.strength;
  database_.add_network(network);
  async_await_client_query();
}

/**
 * Logs an error reported by the client, then stops this Connection.
 *
 * @param query the complete ERROR command, minus trailing CRLF
 */
void
Connection::handle_error_claim(std::string query)
{
  // Discard the command ERROR
  query.erase(0, 6);
  safe_journal(SD_ERR, query);
  stop();
}

/**
 * Sends all networks to the client as a ptree, then awaits further commands
 * from the client.
 */
void
Connection::async_send_networks_to_client_as_ptree()
{
  deadline_timer_.expires_from_now(TIMEOUT);
  auto ptree = database_.all_networks_as_ptree();
  safe_journal(SD_DEBUG, "Sending to client: " + ptree_to_string(ptree));
  std::ostream ostream{&streambuf_};
  boost::property_tree::write_xml(ostream, ptree);
  boost::asio::async_write(socket_, streambuf_,
                           std::bind(&Connection::on_write_completed,
                                     shared_from_this(),
                                     std::placeholders::_1,
                                     std::placeholders::_2));
}

/**
 * Sends all networks to the client as a string, then awaits further commands
 * from the client.
 */
void
Connection::async_send_networks_to_client_as_string()
{
  deadline_timer_.expires_from_now(TIMEOUT);
  auto string = database_.all_networks_as_string();
  safe_journal(SD_DEBUG, "Sending to client: " + string);
  boost::asio::write(socket_, boost::asio::buffer(string));
  async_await_client_query();
}

/**
 * Cancels the deadline timer and closes the socket.
 */
void
Connection::stop()
{
  deadline_timer_.cancel();

  if (socket_.is_open())
    {
      socket_.shutdown(boost::asio::ip::tcp::socket::shutdown_both);
      socket_.close();
    }
}

}
