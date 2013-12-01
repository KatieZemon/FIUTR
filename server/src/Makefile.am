bin_PROGRAMS = groupgd

noinst_LTLIBRARIES = libgroupgd.la

libgroupgd_la_SOURCES = \
	network.cc \
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
	$(builddir)/libgroupgd.la

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

check_PROGRAMS = db-manager test-groupgd test-network-cc

db_manager_CPPFLAGS = -DLOCALSTATEDIR=\"$(localstatedir)\"

db_manager_SOURCES = db-manager.cc

test_groupgd_CPPFLAGS = $(BOOST_CPPFLAGS)

test_groupgd_LDADD = \
	$(BOOST_DATE_TIME_LIBS) \
	$(BOOST_REGEX_LIBS) \
	$(BOOST_SYSTEM_LIBS) \
	$(BOOST_UNIT_TEST_FRAMEWORK_LIBS) \
	$(builddir)/libgroupgd.la

test_groupgd_LDFLAGS = \
	$(BOOST_DATE_TIME_LDFLAGS) \
	$(BOOST_REGEX_LDFLAGS) \
	$(BOOST_SYSTEM_LDFLAGS) \
	$(BOOST_UNIT_TEST_FRAMEWORK_LDFLAGS)

test_groupgd_SOURCES = test-groupgd.cc

test_network_cc_CPPFLAGS = $(BOOST_CPPFLAGS)

test_network_cc_LDADD = \
	$(BOOST_UNIT_TEST_FRAMEWORK_LIBS) \
	$(builddir)/libgroupgd.la

test_network_cc_LDFLAGS = $(BOOST_UNIT_TEST_FRAMEWORK_LDFLAGS)

test_network_cc_SOURCES = test-network.cc

check-local: $(check_PROGRAMS)
	$(builddir)/test-network-cc
	sudo $(builddir)/db-manager stash || true
	$(builddir)/test-groupgd \
	sudo $(builddir)/db-manager restore

-include $(top_srcdir)/git.mk
