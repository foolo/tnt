module Tnt {
	requires java.desktop;
	requires java.logging;
	requires java.prefs;
	requires java.sql;
	requires dtd;
	requires json;
	requires jsoup;
	requires jna;
	requires openxliff;
	requires mapdb;
	requires junit;
	requires hamcrest.core;

	exports tnt.qc;
	exports tnt.util;
}
