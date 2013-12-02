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
#include <vector>

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
                        "name VARCHAR(32), "
                        "lat VARCHAR(32), "
                        "lon VARCHAR(32), "
                        "strength VARCHAR(32));",
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
  for (const auto& similar_network : networks_with_ssid(network.name))
    if (similar_network == network)
      {
        if (network.strength > similar_network.strength)
          {
            remove_network(similar_network);
            break;
          }
        else
          {
            return;
          }
      }

  // FIXME should be a prepared statement to prevent injections
  std::ostringstream oss;
  oss << "INSERT INTO Networks VALUES ('" << network.name << "', '"
      << network.lat << "', '" << network.lon << "', '" << network.strength
      << "');";
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
  oss << "DELETE FROM Networks WHERE name='" << network.name << "' AND lat='"
      << network.lat << "' AND lon='" << network.lon << "' AND strength='"
      << network.strength << "';";
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
add_row_to_ptree(void* ptree, int rows, char** column_text, char** column_name)
{
  try
    {
      auto all_networks = reinterpret_cast<boost::property_tree::ptree*>(ptree);
      boost::property_tree::ptree current_network;
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
  boost::property_tree::ptree result;
  result.add("networks", "");
  if (sqlite3_exec(db_,
                   "SELECT * FROM Networks",
                   &add_row_to_ptree,
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

static int
append_network_from_row(void* list, int rows,
                        char** column_text, char** column_name)
{
  try
    {
      Network network;

      for (int i = 0; i < rows; ++i, ++column_text, ++column_name)
        if (std::strcmp(*column_name, "name") == 0)
          network.name = *column_text;
        else if (std::strcmp(*column_name, "lat") == 0)
          network.lat = *column_text;
        else if (std::strcmp(*column_name, "lon") == 0)
          network.lon = *column_text;
        else if (std::strcmp(*column_name, "strength") == 0)
          network.strength = *column_text;
        else
          throw std::runtime_error{
              std::string{"Unexpected column: "} + *column_name};

       reinterpret_cast<std::vector<Network>*>(list)->push_back(network);
     }
   catch (std::exception& e)
     {
       safe_journal(SD_ERR, std::string{"Processing row: "} + e.what());
       return -1;
     }
  return 0;
}

std::vector<Network>
Database::networks_with_ssid(std::string name)
{
  // FIXME should be a prepared statement to prevent injections
  std::ostringstream oss;
  oss << "SELECT * FROM Networks WHERE name='" << name << "';";
  char* errmsg = nullptr;
  std::vector<Network> networks;
  if (sqlite3_exec(db_,
                   oss.str().c_str(),
                   &append_network_from_row,
                   reinterpret_cast<void*>(&networks),
                   &errmsg) != SQLITE_OK)
    {
      std::string reason{errmsg};
      sqlite3_free(errmsg);
      throw std::runtime_error{"Can't get network: " + reason};
    }
  safe_journal(SD_DEBUG, "Executed query " + oss.str());
  return networks;
}

Database::~Database()
{
  if (sqlite3_close(db_) != SQLITE_OK)
    safe_journal(SD_ERR,
                 std::string{"Error closing database: "} + sqlite3_errmsg(db_));
}

}
