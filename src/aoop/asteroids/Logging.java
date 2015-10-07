package aoop.asteroids;

import java.util.logging.Level;
import java.util.logging.Logger;


public class Logging {

	
	public final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	/**
	 * Change the loggerLevel to have the program display more shit.
	 * @see java.util.logging.Level
	 */
	public final static Level loggerLevel = Level.WARNING;
	
}