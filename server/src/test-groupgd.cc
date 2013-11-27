/* -*- indent-tabs-mode: nil; c-basic-offset: 2; tab-width: 2 -*-  */
/*
 * test-groupgd.cc
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

#define BOOST_TEST_MODULE groupgd

#include <iostream>
#include <string>

#include <boost/asio.hpp>
#include <boost/test/output_test_stream.hpp>
#include <boost/test/unit_test.hpp>

#include "utility.h"

namespace groupgd {
  namespace test {

struct Fixture
{
  Fixture();
  
  ~Fixture();
  
  std::string
  get_response(boost::asio::ip::tcp::socket* socket);
  
  boost::asio::io_service io_service_;
  boost::asio::ip::tcp::socket socket_;
};

Fixture::Fixture()
: io_service_(), socket_(io_service_)
{
  socket_.connect(boost::asio::ip::tcp::endpoint(
          boost::asio::ip::address_v4::from_string("127.0.0.1"), 50000));
}

Fixture::~Fixture()
{
  socket_.shutdown(boost::asio::ip::tcp::socket::shutdown_both);
  socket_.close();
}

std::string
Fixture::get_response(boost::asio::ip::tcp::socket* socket)
{
  boost::asio::streambuf sb;
  boost::asio::read_until(*socket, sb, "</networks>");
  return read_line_from_streambuf(&sb);
}

BOOST_FIXTURE_TEST_SUITE(groupgd, Fixture)

BOOST_AUTO_TEST_CASE(get_networks)
{
  auto query = std::string{"GET NETWORKS\r\n"};
  boost::asio::write(socket_, boost::asio::buffer(query));
  boost::test_tools::output_test_stream output;
  output << get_response(&socket_);
  BOOST_CHECK(output.is_equal("I've got no networks for you yet.", false));
}

BOOST_AUTO_TEST_CASE(add_valid_network)
{
  auto query = std::string{"ADD NETWORK freedm-cluster 111.111111 99 4.\r\n"};
  boost::asio::write(socket_, boost::asio::buffer(query));
}

BOOST_AUTO_TEST_SUITE_END()

  }
}
