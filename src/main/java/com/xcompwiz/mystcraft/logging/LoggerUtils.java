package com.xcompwiz.mystcraft.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LoggerUtils {
	private static Logger	log	= null;

	/**
	 * Configure the logger
	 */
	private static void configureLogging() {
		log = LogManager.getLogger("Mystcraft");
	}

	public static void log(Level level, String message, Object... params) {
		if (log == null) {
			configureLogging();
		}
		log.log(level, String.format(message, params));
	}

	public static void info(String message, Object... params) {
		log(Level.INFO, message, params);
	}

	public static void warn(String message, Object... params) {
		log(Level.WARN, message, params);
	}

	public static void error(String message, Object... params) {
		log(Level.ERROR, message, params);
	}
}
