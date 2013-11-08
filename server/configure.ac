AC_PREREQ([2.69])
AC_INIT([groupgd],[0.1])
AC_CONFIG_SRCDIR([src/main.cc])
AC_CONFIG_MACRO_DIR([m4])

AM_INIT_AUTOMAKE([1.13 foreign -Wall])
AM_SILENT_RULES([yes])
AM_MAINTAINER_MODE([enable])

LT_PREREQ([2.4])
LT_INIT([disable-static])

# Checks for programs.
AC_PROG_CXX
AC_PROG_INSTALL

# Checks for libraries.
PKG_CHECK_MODULES(GROUPGD, [libsystemd-daemon])

BOOST_REQUIRE([1.53], [
  AC_MSG_ERROR([Boost is required])
])
BOOST_DATE_TIME
BOOST_SYSTEM
BOOST_TEST

# Checks for header files.
BOOST_ASIO

# Checks for typedefs, structures, and compiler characteristics.
AX_CXX_COMPILE_STDCXX_11([noext], [mandatory])

# Checks for library functions.

AC_CONFIG_FILES([
  Makefile
  data/Makefile
  src/Makefile
])
AC_OUTPUT
