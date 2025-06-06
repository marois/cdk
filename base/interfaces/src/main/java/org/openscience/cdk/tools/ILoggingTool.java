/* Copyright (C) 2002-2003  Christoph Steinbeck <steinbeck@users.sf.net>
 *               2002-2009  Egon Willighagen <egonw@users.sf.net>
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.tools;

/**
 * Useful for logging messages.
 * Recommended to be used as a private static variable instantiated using
 * {@link LoggingToolFactory#createLoggingTool(Class)}:
 * <pre>
 * public class SomeClass {
 *   private static ILoggingTool logger = LoggingToolFactory.createLoggingTool(SomeClass.class);
 * }
 * </pre>
 * <p>
 * The logger has six logging levels:
 * <dl>
 *  <dt>TRACE
 *  <dd>Emits the largest number of log entries.
 *  <dt>DEBUG
 *  <dd>Default mode. Used for information you might need to track down the
 *      cause of a bug in the source code, or to understand how an algorithm
 *      works.
 *  <dt>INFO
 *  <dd>For reporting informative information to the user that he might easily
 *      disregard. Real important information should be given to the user using
 *      a GUI element.
 *  <dt>WARNING
 *  <dd>This indicates a special situation which is unlike to happen, but for
 *      which no special actions need to be taken. E.g. missing information in
 *      files, or an unknown atom type. The action is normally something user
 *      friendly.
 *  <dt>ERROR
 *  <dd>This level is used for situations that should not have happened *and*
 *      thus indicate a bug.
 *  <dt>FATAL
 *  <dd>This level is used for situations that should not have happened *and*
 *      that lead to a situation where this program can no longer function
 *      (rare in Java).
 * </dl>
 * <p>
 * Logging library might implement less logging levels than the six levels
 * defined here. This may result in logging levels being merged.
 * For example, slf4j has five logging levels. Logging level fatal is not supported
 * so that calls to error and fatal are both logged with slf4j logging level error.
 * <p>
 * Consider that debugging will not always be turned on. Therefore, it is
 * better not to concatenate string in the logger.debug() call, but have the
 * ILoggingTool do this when appropriate. In other words, use:
 * <pre>
 * logger.debug("The String X has this value: ", someString);
 * logger.debug("The int Y has this value: ", y);
 * </pre>
 * instead of:
 * <pre>
 * logger.debug("The String X has this value: " + someString);
 * logger.debug("The int Y has this value: " + y);
 * </pre>
 * <p>
 * For logging calls that require even more computation you can use the
 * <code>isDebugEnabled()</code> method:
 * <pre>
 * if (logger.isDebugEnabled()) {
 *   logger.info("The 1056389822th prime that is used is: ",
 *     calculatePrime(1056389822));
 * }
 * </pre>
 * <p>
 * In addition to the methods specific in the interface, implementations
 * must also implement the static method {@code create(Class<?>)} which
 * is called by {@link LoggingToolFactory} to instantiate the
 * implementation.
 *
 */
public interface ILoggingTool {

    /** Trace, Debug, Info, Warn, Error, and Fatal messages will be emitted. */
    int TRACE = 0;
    /** Debug, Info, Warn, Error, and Fatal messages will be emitted. */
    int DEBUG = 1;
    /** Info, Warn, Error, and Fatal messages will be emitted. */
    int INFO  = 2;
    /** Warn, Error, and Fatal messages will be emitted. */
    int WARN  = 3;
    /** Error, and Fatal messages will be emitted. */
    int ERROR = 4;
    /** Only Fatal messages will be emitted. */
    int FATAL = 5;
    /** Logging is OFF */
    int OFF = 6;

    /**
     * Default number of StackTraceElements to be printed by debug(Exception).
     */
    int DEFAULT_STACK_LENGTH = 5;

    /**
     * Outputs system properties for the operating system and the java
     * version. More specifically: os.name, os.version, os.arch, java.version
     * and java.vendor.
     */
    void dumpSystemProperties();

    /**
     * Sets the number of StackTraceElements to be printed in DEBUG mode when
     * calling <code>debug(Throwable)</code>.
     * The default value is DEFAULT_STACK_LENGTH.
     *
     * @param length the new stack length
     *
     * @see #DEFAULT_STACK_LENGTH
     */
    void setStackLength(int length);

    /**
     * Outputs the system property for java.class.path.
     */
    void dumpClasspath();

    /**
     * Shows DEBUG output for the Object. If the object is an instanceof
     * {@link Throwable} it will output the trace. Otherwise it will use the
     * toString() method.
     *
     * @param object Object to apply toString() to and output
     */
    void debug(Object object);

    /**
     * Shows DEBUG output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param object  Object to apply toString() to and output
     * @param objects Object... to apply toString() to and output
     */
    void debug(Object object, Object... objects);

    /**
     * Shows ERROR output for the Object. It uses the toString() method.
     *
     * @param object Object to apply toString() to and output
     */
    void error(Object object);

    /**
     * Shows ERROR output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param object  Object to apply toString() to and output
     * @param objects Object... to apply toString() to and output
     */
    void error(Object object, Object... objects);

    /**
     * Shows FATAL output for the Object. It uses the toString() method.
     *
     * @param object Object to apply toString() to and output
     */
    void fatal(Object object);

    /**
     * Shows INFO output for the Object. It uses the toString() method.
     *
     * @param object Object to apply toString() to and output
     */
    void info(Object object);

    /**
     * Shows INFO output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param object  Object to apply toString() to and output
     * @param objects Object... to apply toString() to and output
     */
    void info(Object object, Object... objects);

    /**
     * Shows WARN output for the Object. It uses the toString() method.
     *
     * @param object Object to apply toString() to and output
     */
    void warn(Object object);

    /**
     * Shows WARN output for the given Object's. It uses the
     * toString() method to concatenate the objects.
     *
     * @param object Object to apply toString() to and output
     * @param objects Object... to apply toString() to and output
     */
    void warn(Object object, Object... objects);

    /**
     * Use this method for computational demanding debug info.
     * For example:
     * <pre>
     * if (logger.isDebugEnabled()) {
     *   logger.info("The 1056389822th prime that is used is: ",
     *                calculatePrime(1056389822));
     * }
     * </pre>
     *
     * @return true, if debug is enabled
     */
    boolean isDebugEnabled();

    /**
     * Set the level of this logger. The level is provided in one of the
     * constants: {@link #TRACE}, {@link #DEBUG}, {@link #INFO}, {@link #WARN}
     * , {@link #ERROR}, {@link #FATAL}. After setting the level only messages
     * above the specified level will be emitted.
     *
     * @param level the level
     */
    void setLevel(int level);

    /**
     * Get the current level of this logger. The level is provided in one of the
     * constants: {@link #TRACE}, {@link #DEBUG}, {@link #INFO}, {@link #WARN}
     * , {@link #ERROR}, {@link #FATAL}.
     * @return the current level
     */
    int getLevel();
}
