/* -*- indent-tabs-mode: nil; c-basic-offset: 2; tab-width: 2 -*-  */
/*
 * connection.h
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

#ifndef GROUPGD_CONNECTION_H_
#define GROUPGD_CONNECTION_H_

#include <memory>
#include <string>

#include <boost/asio/deadline_timer.hpp>
#include <boost/asio/ip/tcp.hpp>
#include <boost/asio/streambuf.hpp>

#include "database.h"

namespace groupgd {

/**
 * How long to wait for a request from the client before disconnecting him.
 */
const auto TIMEOUT = boost::posix_time::seconds{30};

/**
 * Represents an implementation of the client/server protocol between the server
 * and one individual client. After instantiating a new Connection, the user
 * must manually accept a client on the Connection's socket (using
 * mutable_socket()). After accepting a client, call async_run to start the
 * Connection, then stop worrying about it. The Connection will stay alive by
 * posting shared_ptrs to itself onto its io_service. (The io_service is
 * to remain alive for the duration of the program.)
 */
class Connection : public std::enable_shared_from_this<Connection>
{
public:
  Connection(boost::asio::io_service* io_service);

  ~Connection();

  void
  async_run();

  boost::asio::ip::tcp::socket*
  mutable_socket() noexcept { return &socket_; }

  const boost::asio::ip::tcp::socket&
  socket() const noexcept { return socket_; }

private:
  Connection(const Connection&) = delete;

  Connection&
  operator=(const Connection&) = delete;

  void
  async_await_client_query();

  void
  on_deadline_timer_expired(const boost::system::error_code& ec);

  void
  on_read_completed(const boost::system::error_code& ec);

  void
  on_write_completed(const boost::system::error_code& ec, std::size_t);

  // FIXME haha async?
  void
  async_add_network_to_database(std::string query);

  void
  async_send_networks_to_client_as_ptree();

  void
  async_send_networks_to_client_as_string();

  void
  handle_error_claim(std::string query);

  void
  stop();

  boost::asio::deadline_timer deadline_timer_;
  boost::asio::ip::tcp::socket socket_;
  boost::asio::streambuf streambuf_;
  Database database_;
};

}

#endif
