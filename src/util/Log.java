package util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

	public static Formatter formatter = new Formatter() {

		String getMessage(LogRecord record) {
			String pattern = record.getMessage();
			if (pattern != null) {
				return MessageFormat.format(pattern, record.getParameters());
			}
			return "null";
		}

		String getThrowableMessage(LogRecord record) {
			Throwable throwable = record.getThrown();
			if (throwable != null) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				throwable.printStackTrace(pw);
				pw.println();
				pw.close();
				return sw.toString();
			}
			return "";
		}

		@Override
		public synchronized String format(LogRecord lr) {
			ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(lr.getMillis()), ZoneId.systemDefault());
			String timestamp = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
			String level = lr.getLevel().getLocalizedName();
			String msg = getMessage(lr);
			String throwable = getThrowableMessage(lr);
			return String.format("%s : %s : %s%n%s", timestamp, level, msg, throwable);
		}
	};

	static ArrayList<LogRecord> records = new ArrayList<>();

	static class MemoryLogHandler extends Handler {

		@Override
		public void publish(LogRecord record) {
			records.add(record);
		}

		@Override
		public void flush() {
		}

		@Override
		public void close() throws SecurityException {
		}
	}

	public static Logger getLogger() {
		return logger;
	}

	public static ArrayList<LogRecord> getAllLogs() {
		return records;
	}

	static String tempfilePrefix() {
		String timestamp = ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss"));
		return "tnt_log_" + timestamp + "_";
	}

	public static void initializeLogger() {
		logger.setUseParentHandlers(false);
		ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setFormatter(formatter);
		logger.addHandler(consoleHandler);

		logger.addHandler(new MemoryLogHandler());
		try {
			File tmpfile = File.createTempFile(tempfilePrefix(), ".txt");
			FileHandler fileHandler = new FileHandler(tmpfile.getPath());
			fileHandler.setFormatter(formatter);
			logger.addHandler(fileHandler);
		}
		catch (IOException ex) {
			Logger.getLogger(Log.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
		}
	}

	public static void err(Throwable t) {
		logger.log(Level.SEVERE, null, t);
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
