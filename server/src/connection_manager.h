/* -*- indent-tabs-mode: nil; c-basic-offset: 2; tab-width: 2 -*-  */
/*
 * connection_manager.h
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

#ifndef GROUPGD_CONNECTION_MANAGER_H_
#define GROUPGD_CONNECTION_MANAGER_H_

#include <boost/asio/io_service.hpp>
#include <boost/asio/ip/tcp.hpp>
#include <boost/asio/signal_set.hpp>

#include "connection.h"

namespace groupgd {

class ConnectionManager
{
public:
  static ConnectionManager&
  instance();

  ~ConnectionManager();

  void
  run();

private:
  ConnectionManager();

  ConnectionManager(const Connection&) = delete;

  ConnectionManager&
  operator=(const ConnectionManager&) = delete;

  void
  accept_initial_connection();
  
  void
  async_accept_additional_connections();

  void
  handle_signal(const boost::system::error_code& ec, int signal_number);

  boost::asio::io_service connection_io_service_;
  boost::asio::io_service acceptor_io_service_;
  boost::asio::ip::tcp::acceptor acceptor_;
  boost::asio::signal_set signal_set_;
  int exit_status_ = 0;
  int rethrow_signal_ = 0;
};

}

#endif
