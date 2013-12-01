/* -*- indent-tabs-mode: nil; c-basic-offset: 2; tab-width: 2 -*-  */
/*
 * delete-db.cc
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
 
#include <cstdlib>
#include <iostream>
#include <stdexcept>
#include <string>

#include <sqlite3.h>

int
main()
{
  sqlite3* db;

  if (sqlite3_open(LOCALSTATEDIR "/groupgd/networks.db", &db) != SQLITE_OK)
    {
      std::cerr << "Can't open database: " << sqlite3_errmsg(db) << std::endl;
      std::exit(1);
    }

  char* errmsg;
  if (sqlite3_exec(db, "DROP TABLE Networks", nullptr, nullptr, &errmsg) != SQLITE_OK)
    {
      std::cerr << "Can't drop table " << errmsg << std::endl;
      std::exit(1);
    }
}
