package util;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.w3c.dom.Node;

public class Log {

	static final Logger logger = Logger.getLogger(Log.class.getName());

	static Formatter formatter = new Formatter() {
		@Override
		public synchronized String format(LogRecord lr) {
			ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(lr.getMillis()), ZoneId.systemDefault());
			String timestamp = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
			String level = lr.getLevel().getLocalizedName();
			String msg = java.text.MessageFormat.format(lr.getMessage(), lr.getParameters());
			return String.format("%s : %s : %s%n", timestamp, level, msg);
		}
	};

	static String tempfilePrefix() {
		String timestamp = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss"));
		return "tnt_log_" + timestamp + "_";
	}

	public static void initializeLogger() {
		try {
			File tmpfile = File.createTempFile(tempfilePrefix(), ".txt");
			logger.addHandler(new FileHandler(tmpfile.getPath()));
			logger.addHandler(new ConsoleHandler());
			for (Handler handler : logger.getHandlers()) {
				handler.setFormatter(formatter);
			}
			logger.setUseParentHandlers(false);
		}
		catch (IOException ex) {
			// todo log to session log
			Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public static void err(String msg) {
		logger.log(Level.SEVERE, msg);
	}

	public static void warn(String msg) {
		logger.log(Level.WARNING, msg);
	}

	public static void debug(String msg) {
		logger.log(Level.INFO, msg);
	}

	public static void debug(String msg, Node location) {
		logger.log(Level.INFO, "{0} : {1}", new Object[]{msg, XmlUtil.getPath(location)});
	}
}
