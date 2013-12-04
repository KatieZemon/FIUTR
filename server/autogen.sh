#!/bin/sh -e

# standard sanity-check for GNOME projects
srcdir=`dirname $0`
test -z "$srcdir" && srcdir=.
(test -f $srcdir/src/connection_manager.cc) || {
    echo -n "**Error**: Directory "\`$srcdir\'" does not look like the"
    echo " top-level groupgd directory"
    exit 1
}

autoreconf --force --install --verbose

echo
echo "=========================================================================="
echo "Now you can call ./configure. For a good development configuration, try"
echo
echo "./configure CXXFLAGS='-Wall -Wextra -pedantic -O0 -g' --localstatedir=/var"
echo "=========================================================================="
echo
