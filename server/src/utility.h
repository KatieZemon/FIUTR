/* -*- indent-tabs-mode: nil; c-basic-offset: 2; tab-width: 2 -*-  */
/*
 * utility.h
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
 
#ifndef GROUPGD_UTILITY_H_
#define GROUPGD_UTILITY_H_

#include <cmath>
#include <streambuf>
#include <string>

#include <boost/property_tree/ptree.hpp>

namespace groupgd {

std::string
read_line_from_streambuf(std::streambuf* streambuf);

std::string
read_all_from_streambuf(std::streambuf* streambuf);

boost::property_tree::ptree
streambuf_to_ptree(std::streambuf* streambuf);

std::string
ptree_to_string(const boost::property_tree::ptree& ptree);

void
safe_journal(const char* priority, std::string message);

// http://stackoverflow.com/a/17341
template <typename T>
  bool
  nearly_equal(T a, T b)
  {
    return std::fabs(a - b) < 0.000001;
  }

}

#endif
