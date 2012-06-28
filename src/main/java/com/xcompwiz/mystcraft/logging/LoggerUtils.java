package com.xcompwiz.mystcraft.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class LoggerUtils {
	private static Logger	log	= null;

	/**
	 * Configure the FML logger
	 */
	private static void configureLogging() {
		log = LogManager.getLogger("Mystcraft");
	}

	public static void info(String message, Object... params) {
		if (log == null) {
			configureLogging();
		}
		log.log(Level.INFO, message, params);
	}

	public static void warn(String message, Object... params) {
		if (log == null) {
			configureLogging();
		}
		log.log(Level.WARN, message, params);
	}

	public static void error(String message, Object... params) {
		if (log == null) {
			configureLogging();
		}
		log.log(Level.ERROR, message, params);
	}
}
