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

#include <boost/asio/ip/tcp.hpp>
#include <boost/asio/streambuf.hpp>

namespace groupgd {

class Connection : public std::enable_shared_from_this<Connection>
{
public:
  Connection(boost::asio::io_service* io_service) noexcept;

  ~Connection();

  void
  async_await_client_query();

  boost::asio::ip::tcp::socket*
  mutable_socket() noexcept { return &socket_; }

  const boost::asio::ip::tcp::socket&
  socket() const noexcept { return socket_; }

private:
  void
  on_read_completed(const boost::system::error_code& ec);

  void
  on_write_completed(const boost::system::error_code& ec, std::size_t);
  
  void
  async_add_network_to_database(std::string query);

  void
  async_send_networks_to_client();

  boost::asio::ip::tcp::socket socket_;
  boost::asio::streambuf streambuf_;
};

}

#endif
