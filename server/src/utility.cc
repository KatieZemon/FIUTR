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

namespace groupgd {

std::string
read_line_from_streambuf(std::streambuf* streambuf)
{
  std::istream is{streambuf};
  auto response = std::string{};
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
  auto response = std::string{};
  auto temp = std::string{};
  do
    {
      temp.clear();
      std::getline(is, temp);
      response += temp;
    } while (!temp.empty());
  return response;
}

void
safe_journal(const char* priority, std::string message)
{
  static std::mutex mutex;
  std::lock_guard<std::mutex> lock{mutex};
  std::clog << priority << message << std::endl;
}

}
