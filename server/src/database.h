/* -*- indent-tabs-mode: nil; c-basic-offset: 2; tab-width: 2 -*-  */
/*
 * database.h
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

#ifndef GROUPGD_DATABASE_H_
#define GROUPGD_DATABASE_H_

#include <string>

class sqlite3;

namespace groupgd {

class Database
{
public:
  Database();

  ~Database();

  void
  add_network(std::string name, float lat, float lon, float strength);

private:
  Database(const Database&) = delete;

  Database&
  operator=(const Database&) = delete;

  void
  open_database();

  void
  ensure_network_table_exists();

  sqlite3* db_;
};

}

#endif
