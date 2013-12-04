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
#include <boost/lexical_cast.hpp>
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

static boost::property_tree::ptree
get_networks(boost::asio::ip::tcp::socket* socket)
{
  request_networks(socket);
  return receive_networks(socket);
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
network_in_ptree(const Network& needle,
                 const boost::property_tree::ptree& haystack)
{
  int count = 0;
  for (const auto& pair : haystack)
    {
      if (pair.second.get<std::string>("name") == needle.name
          && nearly_equal(pair.second.get<double>("lat"),
                          boost::lexical_cast<double>(needle.lat))
          && nearly_equal(pair.second.get<double>("lon"),
                          boost::lexical_cast<double>(needle.lon))
          && nearly_equal(pair.second.get<double>("strength"),
                          boost::lexical_cast<double>(needle.strength)))
        {
          ++count;
        }
    }
  BOOST_CHECK(count == 0 || count == 1);
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
  auto ptree = get_networks(&socket_);
  BOOST_CHECK(ptree.empty());
}

BOOST_AUTO_TEST_CASE(add_valid_network)
{
  add_network({"Test", "135", "34.54", "7"}, &socket_);
  auto ptree = get_networks(&socket_);
  BOOST_CHECK(network_in_ptree({"Test", "135", "34.54", "7"}, ptree));
}

BOOST_AUTO_TEST_CASE(add_exact_duplicate_network)
{
  add_network({"Test", "135", "34.54", "7"}, &socket_);
  auto ptree = get_networks(&socket_);
  BOOST_CHECK(network_in_ptree({"Test", "135", "34.54", "7"}, ptree));
}

BOOST_AUTO_TEST_CASE(add_network_different_name)
{
  add_network({"TestTwo", "135", "34.54", "7"}, &socket_);
  auto ptree = get_networks(&socket_);
  BOOST_CHECK(network_in_ptree({"Test", "135", "34.54", "7"}, ptree));
  BOOST_CHECK(network_in_ptree({"TestTwo", "135", "34.54", "7"}, ptree));
}

BOOST_AUTO_TEST_CASE(add_valid_nearby_network_lower_strength)
{
  add_network({"Test", "135", "34.540003", "6"}, &socket_);
  auto ptree = get_networks(&socket_);
  BOOST_CHECK(network_in_ptree({"Test", "135", "34.54", "7"}, ptree));
  BOOST_CHECK(!network_in_ptree({"Test", "135", "34.540003", "6"}, ptree));
}

BOOST_AUTO_TEST_CASE(add_valid_nearby_network_equal_strength)
{
  add_network({"Test", "135", "34.540003", "7"}, &socket_);
  auto ptree = get_networks(&socket_);
  // Original network is retained
  BOOST_CHECK(network_in_ptree({"Test", "135", "34.54", "7"}, ptree));
  BOOST_CHECK(!network_in_ptree({"Test", "135", "34.540003", "7"}, ptree));
}

BOOST_AUTO_TEST_CASE(add_valid_nearby_network_higher_strength)
{
  add_network({"Test", "135", "34.540003", "8"}, &socket_);
  auto ptree = get_networks(&socket_);
  BOOST_CHECK(!network_in_ptree({"Test", "135", "34.54", "7"}, ptree));
  BOOST_CHECK(network_in_ptree({"Test", "135", "34.540003", "8"}, ptree));
}

BOOST_AUTO_TEST_CASE(add_valid_nearby_network_previous_strength)
{
  add_network({"Test", "135", "34.54003", "7"}, &socket_);
  auto ptree = get_networks(&socket_);
  BOOST_CHECK(!network_in_ptree({"Test", "135", "34.54", "7"}, ptree));
  BOOST_CHECK(!network_in_ptree({"Test", "135", "34.540003", "7"}, ptree));
  BOOST_CHECK(network_in_ptree({"Test", "135", "34.540003", "8"}, ptree));
}

// https://github.com/ktacos/FIUTR/issues/17
// Should also test to make sure network names can contain null characters.
BOOST_AUTO_TEST_CASE_EXPECTED_FAILURES(network_name_includes_whitespace, 1)
BOOST_AUTO_TEST_CASE(network_name_includes_whitespace)
{
  add_network({"A Space", "135", "34.54", "7"}, &socket_);
  auto ptree = get_networks(&socket_);
  BOOST_CHECK(network_in_ptree({"A Space", "135", "34.54", "7"}, ptree));
}

BOOST_AUTO_TEST_CASE(network_name_includes_arabic)
{
  add_network({"‎شبكة‎", "135", "34.54", "7"}, &socket_);
  auto ptree = get_networks(&socket_);
  BOOST_CHECK(network_in_ptree({"‎شبكة‎", "135", "34.54", "7"}, ptree));
}
/*
BOOST_AUTO_TEST_CASE(one_connection_multiple_requests)
{
  add_network({"Gary", "135", "34.54", "7"}, &socket_);
  add_network({"Leanne", "120", "53", "94.4"}, &socket_);
  auto ptree = get_networks(&socket_);
  BOOST_CHECK(network_in_ptree({"Gary", "135", "34.54", "7"}, ptree));
  BOOST_CHECK(network_in_ptree({"Leanne", "120", "53", "94.4"}, ptree));
}*/

BOOST_AUTO_TEST_SUITE_END()

  }
}
