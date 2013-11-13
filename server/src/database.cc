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
#include <string.h>
#include <sys/stat.h>
#include <sstream>
#include <stdexcept>
#include <string>

#include <sqlite3.h>
#include <systemd/sd-daemon.h>

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
      throw;
    }
}

void
Database::open_database()
{
  if (mkdir(LOCALSTATEDIR "/groupgd/", 0755) && errno != EEXIST)
    throw std::runtime_error{std::string{"Can't mkdir: "} + strerror(errno)};

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
Database::add_network(std::string name, float lat, float lon, float strength)
{
  // FIXME should be a prepared statement to prevent injections
  std::ostringstream oss;
  oss << "INSERT INTO Networks VALUES ('" << name << "', "
      << lat << ", " << lon << ", " << strength << ");";
  char* errmsg = nullptr;
  if (sqlite3_exec(db_, oss.str().c_str(), NULL, NULL, &errmsg) != SQLITE_OK)
    {
      std::string reason{errmsg};
      sqlite3_free(errmsg);
      throw std::runtime_error{"Can't insert: " + reason};
    }
  safe_journal(SD_DEBUG, std::string{"Executed query "} + oss.str());
}

Database::~Database()
{
  if (sqlite3_close(db_) != SQLITE_OK)
    safe_journal(SD_ERR,
                 std::string{"Error closing database: "} + sqlite3_errmsg(db_));
}

}
