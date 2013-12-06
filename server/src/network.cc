/* -*- indent-tabs-mode: nil; c-basic-offset: 2; tab-width: 2 -*-  */
/*
 * network.cc
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

#include "network.h"

#include <cmath>

#include <boost/lexical_cast.hpp>

#include "utility.h"

namespace groupgd {

/**
 * Tests two Networks for equivalence. Networks are equivalent if they have the
 * same SSID and are within a small distance of each other.
 *
 * @param n1 one Network to test
 * @param n2 the other Network to test
 *
 * @return true if both Networks are equivalent
 */
bool
operator==(Network n1, Network n2)
{
  auto lat1 = boost::lexical_cast<double>(n1.lat);
  auto lat2 = boost::lexical_cast<double>(n2.lat);
  auto lon1 = boost::lexical_cast<double>(n1.lon);
  auto lon2 = boost::lexical_cast<double>(n2.lon);

  return n1.name == n2.name
          && ((nearly_equal(lat1, lat2) && nearly_equal(lon1, lon2))
              || distance(n1, n2) <= IDENTICAL_NETWORK_METERS);
}

/**
 * Tests two Networks for equivalence.
 *
 * @param n1 one Network to test
 * @param n2 the other Network to test
 *
 * @return false if both Networks are equivalent
 */
bool
operator!=(Network n1, Network n2)
{
  return !(n1 == n2);
}

/**
 * The haversin function f(n) = sin^2(n/2)
 *
 * @param angle in radians
 *
 * @return real number
 */
static double
haversin(double theta)
{
  return std::pow(std::sin(theta/2), 2);
}

/**
 * Calculates the distance between two Networks using the Haversine formula [1].
 * Assumes a perfectly spherical Earth.
 *
 * [1] http://en.wikipedia.org/wiki/Haversine_formula
 *
 * @param n1 the first Network
 * @param n2 the second Network
 *
 * @return distance between n1 and n2, in meters
 */
float
distance(Network n1, Network n2)
{
  const static int EQUATORIAL_RADIUS = 6378137;

  auto lat1 = boost::lexical_cast<double>(n1.lat);
  auto lat2 = boost::lexical_cast<double>(n2.lat);
  auto lon1 = boost::lexical_cast<double>(n1.lon);
  auto lon2 = boost::lexical_cast<double>(n2.lon);

  return 2 * EQUATORIAL_RADIUS * std::asin(std::sqrt(haversin(lat2-lat1)
      + std::cos(lat1)*std::cos(lat2)*haversin(lon2-lon1)));
}

}
