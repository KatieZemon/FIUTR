/* -*- indent-tabs-mode: nil; c-basic-offset: 2; tab-width: 2 -*-  */
/*
 * main.cc
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
#include <stdexcept>

#include <systemd/sd-daemon.h>

#include "connection_manager.h"
#include "utility.h"

int
main()
{
  if (sd_booted() <= 0)
    {
      groupgd::safe_journal(SD_ALERT, "Not booted with systemd");
      std::exit(EXIT_FAILURE);
    }

  try
    {
      groupgd::ConnectionManager::instance().run();
    }
  catch (std::exception& e)
    {
      groupgd::safe_journal(SD_CRIT, e.what());
      std::exit(EXIT_FAILURE);
    }
}
