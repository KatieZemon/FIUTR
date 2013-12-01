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

#define BOOST_TEST_DYN_LINK
#define BOOST_TEST_MAIN

#include <iostream>
#include <string>

#include <boost/asio.hpp>
#include <boost/property_tree/ptree.hpp>
#include <boost/regex.hpp>
#include <boost/test/output_test_stream.hpp>
#include <boost/test/unit_test.hpp>

#include "network.h"
#include "utility.h"

namespace groupgd {
  namespace test {

static void
request_networks(boost::asio::ip::tcp::socket* socket)
{
  std::string query{"GET NETWORKS\r\n"};
  boost::asio::write(*socket, boost::asio::buffer(query));
}

static boost::property_tree::ptree
receive_networks(boost::asio::ip::tcp::socket* socket)
{
  boost::asio::streambuf sb;
  boost::asio::read_until(*socket, sb, boost::regex{"</networks>|<networks/>"});
  return streambuf_to_ptree(&sb).get_child("networks");
}

static void
add_network(const Network& network, boost::asio::ip::tcp::socket* socket)
{
  std::ostringstream oss;
  oss << "ADD NETWORK " << network.name << " " << network.lat
      << " " << network.lon << " " << network.strength << "\r\n";
  boost::asio::write(*socket, boost::asio::buffer(oss.str()));
}

static void
ensure_network_exists(const Network& needle,
                      const boost::property_tree::ptree& haystack)
{
  for (const auto& pair : haystack)
    {
      auto network = pair.second;
      if (network.get<std::string>("name") == needle.name
          && nearly_equal(network.get<double>("lat"), needle.lat)
          && nearly_equal(network.get<double>("lon"), needle.lon)
          && nearly_equal(network.get<float>("strength"), needle.strength))
        return;
    }
  BOOST_ERROR("Valid network not found in database");
}

struct Fixture
{
  Fixture()
  : io_service_(), socket_(io_service_)
  {
    socket_.connect(boost::asio::ip::tcp::endpoint(
            boost::asio::ip::address_v4::from_string("127.0.0.1"), 50000));
  }

  ~Fixture()
  {
    socket_.shutdown(boost::asio::ip::tcp::socket::shutdown_both);
    socket_.close();
  }

  boost::asio::io_service io_service_;
  boost::asio::ip::tcp::socket socket_;
};

BOOST_FIXTURE_TEST_SUITE(groupgd, Fixture)

BOOST_AUTO_TEST_CASE(get_zero_networks)
{
  request_networks(&socket_);
  auto ptree = receive_networks(&socket_);
  BOOST_CHECK_EQUAL(ptree.empty(), true);
}

BOOST_AUTO_TEST_CASE(add_valid_network)
{
  add_network({"Test", 135, 34.54, 7}, &socket_);
  request_networks(&socket_);
  auto ptree = receive_networks(&socket_);
  ensure_network_exists({"Test", 135, 34.54, 7}, ptree);
}

BOOST_AUTO_TEST_SUITE_END()

  }
}
