bin_PROGRAMS = groupgd

groupgd_CPPFLAGS = \
	$(BOOST_CPPFLAGS) \
	$(GROUPGD_CPPFLAGS) \
	-DLOCALSTATEDIR=\"$(localstatedir)\"

groupgd_CXXFLAGS = \
	$(GROUPGD_CXXFLAGS)

groupgd_LDADD = \
	$(BOOST_DATE_TIME_LIBS) \
	$(BOOST_SYSTEM_LIBS) \
	$(GROUPGD_LIBS)

groupgd_LDFLAGS = \
	$(BOOST_DATE_TIME_LDFLAGS) \
	$(BOOST_SYSTEM_LDFLAGS) \
	$(GROUPGD_LDFLAGS)

groupgd_SOURCES = \
	main.cc \
	connection.h \
	connection.cc \
	connection_manager.h \
	connection_manager.cc \
	database.h \
	database.cc \
	network.h \
	utility.h \
	utility.cc

check_PROGRAMS = test-groupgd

test_groupgd_CPPFLAGS = \
	$(BOOST_CPPFLAGS) \
	$(GROUPGD_CPPFLAGS)

test_groupgd_CXXFLAGS = \
	$(GROUPGD_CXXFLAGS) \
	-DBOOST_TEST_DYN_LINK \
	-DBOOST_TEST_MAIN

test_groupgd_LDADD = \
	$(BOOST_SYSTEM_LIBS) \
	$(BOOST_UNIT_TEST_FRAMEWORK_LIBS) \
	$(GROUPGD_LIBS)

test_groupgd_LDFLAGS = \
	$(BOOST_SYSTEM_LDFLAGS) \
	$(BOOST_UNIT_TEST_FRAMEWORK_LDFLAGS) \
	$(GROUPGD_LDFLAGS) \
	-DBOOST_TEST_DYN_LINK \
	-DBOOST_TEST_MAIN

test_groupgd_SOURCES = \
	test-groupgd.cc \
	network.h \
	utility.h \
	utility.cc

check-local: $(check_PROGRAMS)
	./test-groupgd

-include $(top_srcdir)/git.mk
