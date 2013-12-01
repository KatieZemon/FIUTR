/* -*- indent-tabs-mode: nil; c-basic-offset: 2; tab-width: 2 -*-  */
/*
 * test-network.cc
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

#include <boost/test/unit_test.hpp>

#include "network.h"

namespace groupgd {
  namespace test {

BOOST_AUTO_TEST_SUITE(network_cc)

BOOST_AUTO_TEST_CASE(nearby_networks_different_ssids)
{
  Network n1 = {"n1", 42, 69.000000, 13};
  Network n2 = {"n2", 42, 69.000010, 13};
  BOOST_CHECK_EQUAL(n1.lon == n2.lon, false);
  BOOST_CHECK_EQUAL(distance(n1, n2) <= IDENTICAL_NETWORK_METERS, true);
  BOOST_CHECK_EQUAL(n1 == n2, false);
  BOOST_CHECK_EQUAL(n1 != n2, true);

  Network n3 = {"n3", 135, 34.540000, 7};
  Network n4 = {"n4", 135, 34.540003, 7};
  BOOST_CHECK_EQUAL(n3.lon == n4.lon, false);
  BOOST_CHECK_EQUAL(distance(n3, n4) <= IDENTICAL_NETWORK_METERS, true);
  BOOST_CHECK_EQUAL(n3 == n4, false);
  BOOST_CHECK_EQUAL(n3 != n4, true);
}

BOOST_AUTO_TEST_CASE(nearby_networks_identical_ssids)
{
  Network n1 = {"network", 42, 69.000000, 13};
  Network n2 = {"network", 42, 69.000010, 13};
  BOOST_CHECK_EQUAL(n1.lon == n2.lon, false);
  BOOST_CHECK_EQUAL(distance(n1, n2) <= IDENTICAL_NETWORK_METERS, true);
  BOOST_CHECK_EQUAL(n1 == n2, true);
  BOOST_CHECK_EQUAL(n1 != n2, false);

  Network n3 = {"Test", 135, 34.540000, 7};
  Network n4 = {"Test", 135, 34.540003, 7};
  BOOST_CHECK_EQUAL(n3.lon == n4.lon, false);
  BOOST_CHECK_EQUAL(distance(n3, n4) <= IDENTICAL_NETWORK_METERS, true);
  BOOST_CHECK_EQUAL(n3 == n4, true);
  BOOST_CHECK_EQUAL(n3 != n4, false);
}

BOOST_AUTO_TEST_CASE(identical_ssids_not_nearby)
{
  Network n1 = {"network", 42, 69.000, 13};
  Network n2 = {"network", 42, 69.001, 13};
  BOOST_CHECK_EQUAL(n1.lon == n2.lon, false);
  BOOST_CHECK_EQUAL(distance(n1, n2) <= IDENTICAL_NETWORK_METERS, false);
  BOOST_CHECK_EQUAL(n1 == n2, false);
  BOOST_CHECK_EQUAL(n1 != n2, true);
}

BOOST_AUTO_TEST_SUITE_END()

  }
}
