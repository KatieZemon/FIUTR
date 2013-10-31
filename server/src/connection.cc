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

#include <iostream>

#include <boost/asio.hpp>
#include <boost/system/error_code.hpp>
#include <systemd/sd-daemon.h>

#include "utility.h"

namespace groupgd {

Connection::Connection(boost::asio::io_service* io_service) noexcept
: socket_(*io_service)
{ }

Connection::~Connection()
{
  try
  {
    if (socket_.is_open())
      {
        socket_.shutdown(boost::asio::ip::tcp::socket::shutdown_both);
        socket_.close();
      }
  }
  catch (boost::system::system_error& e)
  {
    std::clog << SD_ERR << e.what() << std::endl;
  }
}

void
Connection::async_run()
{
  send_networks_to_client();
  /* FIXME
  boost::asio::async_read_until(socket_, streambuf_, "\r\n",
                                std::bind(&Connection::handle_read,
                                          shared_from_this(),
                                          std::placeholders::_1));
                                          */
}

boost::asio::ip::tcp::socket*
Connection::mutable_socket() noexcept
{
  return &socket_;
}

const boost::asio::ip::tcp::socket&
Connection::socket() const noexcept
{
  return socket_;
}

void
Connection::handle_read(const boost::system::error_code& ec)
{
  if (!ec)
    {
      auto query = read_line_from_streambuf(&streambuf_);
      // TODO do something realistic with the query
      std::clog << SD_DEBUG << "Client says: " << query << std::endl;
      send_networks_to_client();
    }
  else if (ec != boost::asio::error::operation_aborted)
    {
      throw boost::system::system_error{ec};
    }
}

void
Connection::add_network_to_database(std::string query)
{
  // TODO
}

void
Connection::send_networks_to_client()
{
  // TODO do something realistic here
  auto response = std::string{"Hi there client\r\n"};
  boost::asio::write(socket_, boost::asio::buffer(response));
}

}
