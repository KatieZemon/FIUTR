# systemd upstream recommends using their pkg-config variable to find this
# directory, but that could result in files installed outside of prefix.
systemdsystemunitdir = $(libdir)/systemd/system
dist_noinst_DATA = groupgd.service.in
systemdsystemunit_DATA = groupgd.service
dist_systemdsystemunit_DATA = groupgd.socket

groupgd.service: groupgd.service.in
	$(AM_V_GEN) sed -e "s|\@bindir\@|$(bindir)|" $< > $@.tmp && mv $@.tmp $@

CLEANFILES = groupgd.service

-include $(top_srcdir)/git.mk
