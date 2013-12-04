/* -*- indent-tabs-mode: nil; c-basic-offset: 2; tab-width: 2 -*-  */
/*
 * db-manager.cc
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

#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <iostream>

#define DB_FILENAME LOCALSTATEDIR "/groupgd/networks.db"
#define DB_BACKUPNAME LOCALSTATEDIR "/groupgd/backup.db"

int
main(int argc, char* argv[])
{
  if (argc != 2)
    {
      std::cerr << "Usage: " << argv[0] << " COMMAND" << std::endl;
      std::cerr << "Commands: delete stash restore" << std::endl;
      std::exit(1);
    }

  if (std::strcmp(argv[1], "delete") == 0)
    {
      if (std::remove(DB_FILENAME) == -1)
        {
          std::perror("delete_db");
          std::exit(1);
        }
    }
  else if (std::strcmp(argv[1], "stash") == 0)
    {
      if (std::rename(DB_FILENAME, DB_BACKUPNAME) == -1)
        {
          std::perror("stash_db");
          std::exit(1);
        }
    }
  else if (std::strcmp(argv[1], "restore") == 0)
    {
      if (std::rename(DB_BACKUPNAME, DB_FILENAME) == -1)
        {
          std::perror("restore_db");
          std::exit(1);
        }
    }
  else
    {
      std::cerr << "Unknown command: " << argv[1] << std::endl;
      std::exit(1);
    }
}
