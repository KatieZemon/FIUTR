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

#define BOOST_TEST_DYN_LINK
#define BOOST_TEST_MAIN

#include <iostream>
#include <string>

#include <boost/asio.hpp>
#include <boost/property_tree/ptree.hpp>
#include <boost/regex.hpp>
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
  auto result = streambuf_to_ptree(&sb);
  BOOST_TEST_MESSAGE("Received: " + ptree_to_string(result));
  return result.get_child("networks");
}

static void
add_network(const Network& network, boost::asio::ip::tcp::socket* socket)
{
  std::ostringstream oss;
  oss.precision(6);
  oss << std::fixed << "ADD NETWORK " << network.name << " " << network.lat
      << " " << network.lon << " " << network.strength;
  BOOST_TEST_MESSAGE("Sending: " + oss.str());
  oss << "\r\n";
  boost::asio::write(*socket, boost::asio::buffer(oss.str()));
}

static bool
network_in(const Network& needle, const boost::property_tree::ptree& haystack)
{
  int count = 0;
  for (const auto& pair : haystack)
    {
      auto network = pair.second;
      if (network.get<std::string>("name") == needle.name
          && nearly_equal(network.get<float>("lat"), needle.lat)
          && nearly_equal(network.get<float>("lon"), needle.lon)
          && nearly_equal(network.get<float>("strength"), needle.strength))
        ++count;
    }
  BOOST_CHECK_EQUAL(count == 0 || count == 1, true);
  return count;
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
  BOOST_CHECK_EQUAL(network_in({"Test", 135, 34.54, 7}, ptree), true);
}

BOOST_AUTO_TEST_CASE(add_exact_duplicate_network)
{
  add_network({"Test", 135, 34.54, 7}, &socket_);
  request_networks(&socket_);
  auto ptree = receive_networks(&socket_);
  BOOST_CHECK_EQUAL(network_in({"Test", 135, 34.54, 7}, ptree), true);
}

BOOST_AUTO_TEST_CASE(add_network_different_name)
{
  add_network({"TestTwo", 135, 34.54, 7}, &socket_);
  request_networks(&socket_);
  auto ptree = receive_networks(&socket_);
  BOOST_CHECK_EQUAL(network_in({"Test", 135, 34.54, 7}, ptree), true);
  BOOST_CHECK_EQUAL(network_in({"TestTwo", 135, 34.54, 7}, ptree), true);
}

BOOST_AUTO_TEST_CASE(add_valid_nearby_network_lower_strength)
{
  add_network({"Test", 135, 34.54003, 6}, &socket_);
  BOOST_CHECK_EQUAL(34.54 == 34.54003, false);
  request_networks(&socket_);
  auto ptree = receive_networks(&socket_);
  BOOST_CHECK_EQUAL(network_in({"Test", 135, 34.54, 7}, ptree), true);
  BOOST_CHECK_EQUAL(network_in({"Test", 135, 34.54003, 6}, ptree), false);
}

BOOST_AUTO_TEST_CASE(add_valid_nearby_network_equal_strength)
{
  add_network({"Test", 135, 34.54003, 7}, &socket_);
  BOOST_CHECK_EQUAL(34.54 == 34.54003, false);
  request_networks(&socket_);
  auto ptree = receive_networks(&socket_);
  // Original network is retained
  BOOST_CHECK_EQUAL(network_in({"Test", 135, 34.54, 7}, ptree), true);
  BOOST_CHECK_EQUAL(network_in({"Test", 135, 34.54003, 7}, ptree), false);
}

BOOST_AUTO_TEST_CASE(add_valid_nearby_network_higher_strength)
{
  add_network({"Test", 135, 34.54003, 8}, &socket_);
  BOOST_CHECK_EQUAL(34.54 == 34.54003, false);
  request_networks(&socket_);
  auto ptree = receive_networks(&socket_);
  BOOST_CHECK_EQUAL(network_in({"Test", 135, 34.54, 7}, ptree), false);
  BOOST_CHECK_EQUAL(network_in({"Test", 135, 34.54003, 8}, ptree), true);
}

BOOST_AUTO_TEST_CASE(add_valid_nearby_network_previous_strength)
{
  add_network({"Test", 135, 34.54003, 7}, &socket_);
  BOOST_CHECK_EQUAL(34.54 == 34.54003, false);
  request_networks(&socket_);
  auto ptree = receive_networks(&socket_);
  BOOST_CHECK_EQUAL(network_in({"Test", 135, 34.54, 7}, ptree), false);
  BOOST_CHECK_EQUAL(network_in({"Test", 135, 34.54003, 7}, ptree), false);
  BOOST_CHECK_EQUAL(network_in({"Test", 135, 34.54003, 8}, ptree), true);
}

// https://github.com/ktacos/FIUTR/issues/17
BOOST_AUTO_TEST_CASE_EXPECTED_FAILURES(network_name_includes_whitespace, 1)
BOOST_AUTO_TEST_CASE(network_name_includes_whitespace)
{
  add_network({"A Space", 135, 34.54, 7}, &socket_);
  request_networks(&socket_);
  auto ptree = receive_networks(&socket_);
  BOOST_CHECK_EQUAL(network_in({"A Space", 135, 34.54, 7}, ptree), true);
}

BOOST_AUTO_TEST_SUITE_END()

  }
}
