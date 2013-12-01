/* -*- indent-tabs-mode: nil; c-basic-offset: 2; tab-width: 2 -*-  */
/*
 * database.cc
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

#include "database.h"

#include <errno.h>
#include <sys/stat.h>
#include <cstring>
#include <sstream>
#include <stdexcept>
#include <string>

#include <boost/property_tree/ptree.hpp>
#include <sqlite3.h>
#include <systemd/sd-daemon.h>

#include "network.h"
#include "utility.h"

namespace groupgd {

Database::Database()
{
  open_database();

  try
    {
      ensure_network_table_exists();
    }
  catch (std::exception& e)
    {
      sqlite3_close(db_);
      safe_journal(SD_ERR, std::string{"Can't close: "} + sqlite3_errmsg(db_));
      throw;
    }
}

void
Database::open_database()
{
  if (mkdir(LOCALSTATEDIR "/groupgd/", 0755) && errno != EEXIST)
    throw std::runtime_error{std::string{"Can't mkdir: "}
                             + std::strerror(errno)};

  if (sqlite3_open(LOCALSTATEDIR "/groupgd/networks.db", &db_) != SQLITE_OK)
    {
      std::string reason = sqlite3_errmsg(db_);
      sqlite3_close(db_);
      throw std::runtime_error{"Can't open database: " + reason};
    }
}

void
Database::ensure_network_table_exists()
{
  char* errmsg;
  if (sqlite3_exec(db_, "CREATE TABLE IF NOT EXISTS Networks ("
                        "name varchar(32), "
                        "lat real, "
                        "lon real, "
                        "strength real);",
                   NULL, NULL, &errmsg) != SQLITE_OK)
    {
      std::string reason{errmsg};
      sqlite3_free(errmsg);
      throw std::runtime_error{"Can't create table: " + reason};
    }
}

void
Database::add_network(const Network& network)
{
  // FIXME what about duplicate networks (also nearby networks?)
  // FIXME should be a prepared statement to prevent injections
  std::ostringstream oss;
  oss << "INSERT INTO Networks VALUES ('" << network.name << "', "
      << network.lat << ", " << network.lon << ", " << network.strength << ");";
  char* errmsg = nullptr;
  if (sqlite3_exec(db_, oss.str().c_str(),
                   nullptr, nullptr, &errmsg) != SQLITE_OK)
    {
      std::string reason{errmsg};
      sqlite3_free(errmsg);
      throw std::runtime_error{"Can't insert: " + reason};
    }
  safe_journal(SD_DEBUG, "Executed query " + oss.str());
}

void
Database::remove_network(const Network& network)
{
  // FIXME should be a prepared statement to prevent injections
  std::ostringstream oss;
  oss << "REMOVE FROM Networks VALUES ('" << network.name << "', "
      << network.lat << ", " << network.lon << ", " << network.strength << ");";
  char* errmsg = nullptr;
  if (sqlite3_exec(db_, oss.str().c_str(),
                   nullptr, nullptr, &errmsg) != SQLITE_OK)
    {
      std::string reason{errmsg};
      sqlite3_free(errmsg);
      throw std::runtime_error{"Can't remove: " + reason};
    }
  safe_journal(SD_DEBUG, "Executed query " + oss.str());
}

static int
process_result_row(void* ptree, int rows,
                   char** column_text, char** column_name)
{
  try
    {
      auto all_networks = reinterpret_cast<boost::property_tree::ptree*>(ptree);
      auto current_network = boost::property_tree::ptree{};
      for (int i = 0; i < rows; ++i, ++column_text, ++column_name)
        {
          current_network.put(*column_name, *column_text);
        }
      all_networks->add_child("networks.network", current_network);
     }
   catch (std::exception& e)
     {
       safe_journal(SD_ERR, std::string{"Processing row: "} + e.what());
       return -1;
     }
  return 0;
}

boost::property_tree::ptree
Database::all_networks() const
{
  char* errmsg = nullptr;
  auto result = boost::property_tree::ptree{};
  result.add("networks", "");
  if (sqlite3_exec(db_,
                   "SELECT * FROM Networks",
                   &process_result_row,
                   reinterpret_cast<void*>(&result),
                   &errmsg) != SQLITE_OK)
    {
      std::string reason{errmsg};
      sqlite3_free(errmsg);
      throw std::runtime_error{"Can't get networks: " + reason};
    }
  safe_journal(SD_DEBUG, "Executed query SELECT * FROM Networks");
  return result;
}

Database::~Database()
{
  if (sqlite3_close(db_) != SQLITE_OK)
    safe_journal(SD_ERR,
                 std::string{"Error closing database: "} + sqlite3_errmsg(db_));
}

}
