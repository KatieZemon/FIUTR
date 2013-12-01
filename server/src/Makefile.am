bin_PROGRAMS = groupgd

noinst_LTLIBRARIES = libgroupgd.la

libgroupgd_la_SOURCES = \
	network.h \
	utility.cc \
	utility.h

libgroupgd_la_CPPFLAGS = $(BOOST_CPPFLAGS)

groupgd_CPPFLAGS = \
	$(BOOST_CPPFLAGS) \
	$(GROUPGD_CFLAGS) \
	-DLOCALSTATEDIR=\"$(localstatedir)\"

groupgd_LDADD = \
	$(BOOST_DATE_TIME_LIBS) \
	$(BOOST_SYSTEM_LIBS) \
	$(GROUPGD_LIBS) \
	libgroupgd.la

groupgd_LDFLAGS = \
	$(BOOST_DATE_TIME_LDFLAGS) \
	$(BOOST_SYSTEM_LDFLAGS)

groupgd_SOURCES = \
	main.cc \
	connection.h \
	connection.cc \
	connection_manager.h \
	connection_manager.cc \
	database.h \
	database.cc

check_PROGRAMS = test-groupgd

test_groupgd_CPPFLAGS = $(BOOST_CPPFLAGS)

test_groupgd_LDADD = \
	$(BOOST_DATE_TIME_LIBS) \
	$(BOOST_SYSTEM_LIBS) \
	$(BOOST_UNIT_TEST_FRAMEWORK_LIBS) \
	libgroupgd.la

test_groupgd_LDFLAGS = \
	$(BOOST_DATE_TIME_LDFLAGS) \
	$(BOOST_SYSTEM_LDFLAGS) \
	$(BOOST_UNIT_TEST_FRAMEWORK_LDFLAGS)

test_groupgd_SOURCES = test-groupgd.cc

check-local: $(check_PROGRAMS)
	./test-groupgd

-include $(top_srcdir)/git.mk
