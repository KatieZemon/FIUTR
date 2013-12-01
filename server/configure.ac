AC_PREREQ([2.69])
AC_INIT([groupgd],[0.1])
AC_CONFIG_SRCDIR([src/main.cc])
AC_CONFIG_MACRO_DIR([m4])

AM_INIT_AUTOMAKE([1.13 foreign dist-xz no-dist-gzip -Wall])
AM_SILENT_RULES([yes])
AM_MAINTAINER_MODE([enable])

AM_PROG_AR

LT_PREREQ([2.4])
LT_INIT([disable-static])

AC_PROG_CXX
AX_CXX_COMPILE_STDCXX_11([noext], [mandatory])

PKG_CHECK_MODULES(GROUPGD, [
  libsystemd-daemon
  sqlite3 >= 3.3
])

BOOST_REQUIRE([1.53], [
  AC_MSG_ERROR([Boost is required])
])

BOOST_DATE_TIME
BOOST_REGEX
BOOST_SYSTEM
BOOST_TEST

AC_CONFIG_FILES([
  Makefile
  data/Makefile
  src/Makefile
  tools/Makefile
])

AC_OUTPUT
