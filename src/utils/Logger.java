package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A simple, thread-safe Logger that writes log messages to a file.
 * This Logger is implemented as a singleton to ensure a single point of
 * logging.
 *
 * Log entry format:
 * [YYYY-MM-DD HH:MM:SS] [Thread-Name] [LEVEL] Message
 */
public class Logger {
  // Singleton instance of the Logger.
  private static Logger instance = null;
  // Lock object for thread-safe initialization.
  private static final Object lock = new Object();

  private PrintWriter writer;
  private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  /**
   * Private constructor to initialize the Logger.
   * Clears the log file on initialization.
   */
  private Logger() {
    try {
      // Open the file in non-append mode to clear previous logs.
      writer = new PrintWriter(new BufferedWriter(new FileWriter("petri_net.log", false)));
    } catch (IOException e) {
      System.err.println("Failed to initialize Logger: " + e.getMessage());
    }
  }

  /**
   * Returns the singleton instance of the Logger.
   *
   * @return the Logger instance.
   */
  public static Logger getInstance() {
    if (instance == null) {
      synchronized (lock) {
        if (instance == null) {
          instance = new Logger();
        }
      }
    }
    return instance;
  }

  /**
   * Logs a message with the given level.
   *
   * @param level   the log level (e.g., INFO, DEBUG, WARN, ERROR).
   * @param message the message to log.
   */
  public synchronized void log(String level, String message) {
    String timestamp = LocalDateTime.now().format(dtf);
    // Log format: [timestamp] [Thread-Name] [LEVEL] Message
    String logLine = String.format("[%s] [%s] [%s] %s", timestamp, Thread.currentThread().getName(), level, message);
    writer.println(logLine);
    writer.flush();
  }

  /**
   * Logs an informational message.
   *
   * @param message the message to log.
   */
  public void info(String message) {
    log("INFO", message);
  }

  /**
   * Logs a debug message.
   *
   * @param message the message to log.
   */
  public void debug(String message) {
    log("DEBUG", message);
  }

  /**
   * Logs a warning message.
   *
   * @param message the message to log.
   */
  public void warn(String message) {
    log("WARN", message);
  }

  /**
   * Logs an error message.
   *
   * @param message the message to log.
   */
  public void error(String message) {
    log("ERROR", message);
  }

  /**
   * Closes the Logger and releases any associated resources.
   */
  public synchronized void close() {
    if (writer != null) {
      writer.close();
    }
  }
}

