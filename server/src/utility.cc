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

#include "utility.h"

#include <iostream>
#include <mutex>
#include <streambuf>
#include <string>

#include <boost/property_tree/ptree.hpp>
#include <boost/property_tree/xml_parser.hpp>

namespace groupgd {

std::string
read_line_from_streambuf(std::streambuf* streambuf)
{
  std::istream is{streambuf};
  std::string response;
  std::getline(is, response);
  // Prune delimiter
  if (response.length() > 0)
    response.resize(response.length() - 1);
  return response;
}

std::string
read_all_from_streambuf(std::streambuf* streambuf)
{
  std::istream is{streambuf};
  std::string response;
  std::string temp;
  do
    {
      temp.clear();
      std::getline(is, temp);
      response += temp;
    } while (!temp.empty());
  return response;
}

boost::property_tree::ptree
streambuf_to_ptree(std::streambuf* streambuf)
{
  std::istream is{streambuf};
  boost::property_tree::ptree result;
  boost::property_tree::read_xml(is, result);
  return result;
}

std::string
ptree_to_string(const boost::property_tree::ptree& ptree)
{
  std::stringstream ss;
  boost::property_tree::write_xml(ss, ptree);
  return read_all_from_streambuf(ss.rdbuf());
}

void
safe_journal(const char* priority, std::string message)
{
  static std::mutex mutex;
  std::lock_guard<std::mutex> lock{mutex};
  std::clog << priority << message << std::endl;
}

}
