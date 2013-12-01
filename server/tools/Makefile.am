noinst_PROGRAMS = delete-db

AM_CPPFLAGS = \
	$(DELETE_DB_CFLAGS) \
	-DLOCALSTATEDIR=\"$(localstatedir)\"

delete_db_LDADD = $(DELETE_DB_LIBS)

delete_db_SOURCES = delete-db.cc

-include $(top_srcdir)/git.mk
