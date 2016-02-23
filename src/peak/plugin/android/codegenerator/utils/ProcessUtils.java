package peak.plugin.android.codegenerator.utils;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class for performing process related functions such as command line processing.
 */
public class ProcessUtils {
    static Log log = LogFactory.getLog(ProcessUtils.class);

    /**
     * Thread class to be used as a worker
     */
    private static class Worker
            extends Thread {
        private final Process process;
        private Integer exitValue;

        Worker(final Process process) {
            this.process = process;
        }

        public Integer getExitValue() {
            return exitValue;
        }

        @Override
        public void run() {
            try {
                exitValue = process.waitFor();
            } catch (InterruptedException ignore) {
                return;
            }
        }
    }

    /**
     * Executes a command.
     *
     * @param command
     * @param printOutput
     * @param printError
     * @param timeOut
     * @return
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    public static int executeCommandWithExecutors(final String command,
                                                  final boolean printOutput,
                                                  final boolean printError,
                                                  final long timeOut) {
        // validate the system and command line and get a system-appropriate command line
        String massagedCommand = validateSystemAndMassageCommand(command);
        try {
            // create the process which will run the command
            Runtime runtime = Runtime.getRuntime();
            final Process process = runtime.exec(massagedCommand);
            // consume and display the error and output streams
            StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "OUTPUT", printOutput);
            StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR", printError);
            outputGobbler.start();
            errorGobbler.start();
            // create a Callable for the command's Process which can be called by an Executor
            Callable<Integer> call = new Callable<Integer>() {
                public Integer call()
                        throws Exception {
                    process.waitFor();
                    return process.exitValue();
                }
            };
            // submit the command's call and get the result from a
            Future<Integer> futureResultOfCall = Executors.newSingleThreadExecutor().submit(call);
            try {
                int exitValue = futureResultOfCall.get(timeOut, TimeUnit.MILLISECONDS);
                return exitValue;
            } catch (TimeoutException ex) {
                String errorMessage = "The command [" + command + "] timed out.";
                log.error(errorMessage, ex);
                throw new RuntimeException(errorMessage, ex);
            } catch (ExecutionException ex) {
                String errorMessage = "The command [" + command + "] did not complete due to an execution error.";
                log.error(errorMessage, ex);
                throw new RuntimeException(errorMessage, ex);
            }
        } catch (InterruptedException ex) {
            String errorMessage = "The command [" + command + "] did not complete due to an unexpected interruption.";
            log.error(errorMessage, ex);
            throw new RuntimeException(errorMessage, ex);
        } catch (IOException ex) {
            String errorMessage = "The command [" + command + "] did not complete due to an IO error.";
            log.error(errorMessage, ex);
            throw new RuntimeException(errorMessage, ex);
        }
    }

    /**
     * Executes a command.
     *
     * @param command
     * @param printOutput
     * @param printError
     * @param timeOut
     * @return
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    public static int executeCommandWithSleep(final String command,
                                              final boolean printOutput,
                                              final boolean printError,
                                              final long timeOut) {
        // validate the system and command line and get a system-appropriate command line
        String massagedCommand = validateSystemAndMassageCommand(command);
        try {
            // create the process which will run the command
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(massagedCommand);
            // consume and display the error and output streams
            StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "OUTPUT", printOutput);
            StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR", printError);
            outputGobbler.start();
            errorGobbler.start();
            // run a thread which will set a flag once it has slept for the timeout period
            final boolean[] flags = {true};
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(timeOut);
                    } catch (InterruptedException ex) {
                        String errorMessage = "Timeout loop thread unexpectedly interrupted.";
                        log.error(errorMessage, ex);
                        throw new RuntimeException(errorMessage, ex);
                    }
                    flags[0] = false;
                }
            }.start();
            // execute the command and wait
            int returnValue = -1;
            while (flags[0] && (returnValue < 0)) {
                returnValue = process.waitFor();
            }
            // if the command timed out then log it
            if (returnValue < 0) {
                log.warn("The command [" + command + "] did not complete before the timeout period expired (timeout: " +
                        timeOut + " ms)");
            }
            return returnValue;
        } catch (InterruptedException ex) {
            String errorMessage = "The command [" + command + "] did not complete due to an unexpected interruption.";
            log.error(errorMessage, ex);
            throw new RuntimeException(errorMessage, ex);
        } catch (IOException ex) {
            String errorMessage = "The command [" + command + "] did not complete due to an IO error.";
            log.error(errorMessage, ex);
            throw new RuntimeException(errorMessage, ex);
        }
    }

    /**
     * Executes a command.
     *
     * @param command
     * @param printOutput
     * @param printError
     * @param timeOut
     * @return
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    public static int executeCommandWithWorker(final String command,
                                               final boolean printOutput,
                                               final boolean printError,
                                               final long timeOut) {
        // validate the system and command line and get a system-appropriate command line
        String massagedCommand = validateSystemAndMassageCommand(command);
        try {
            // create the process which will run the command
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(massagedCommand);
            // consume and display the error and output streams
            StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "OUTPUT", printOutput);
            StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR", printError);
            outputGobbler.start();
            errorGobbler.start();
            // create and start a Worker thread which this thread will join for the timeout period
            Worker worker = new Worker(process);
            worker.start();
            try {
                worker.join(timeOut);
                Integer exitValue = worker.getExitValue();
                if (exitValue != null) {
                    // the worker thread completed within the timeout period
                    return exitValue;
                }
                // if we get this far then we never got an exit value from the worker thread as a result of a timeout
                String errorMessage = "The command [" + command + "] timed out.";
                log.error(errorMessage);
                throw new RuntimeException(errorMessage);
            } catch (InterruptedException ex) {
                worker.interrupt();
                Thread.currentThread().interrupt();
                throw ex;
            }
        } catch (InterruptedException ex) {
            String errorMessage = "The command [" + command + "] did not complete due to an unexpected interruption.";
            log.error(errorMessage, ex);
            throw new RuntimeException(errorMessage, ex);
        } catch (IOException ex) {
            String errorMessage = "The command [" + command + "] did not complete due to an IO error.";
            log.error(errorMessage, ex);
            throw new RuntimeException(errorMessage, ex);
        }
    }

    /**
     * Validates that the system is running a supported OS and returns a system-appropriate command line.
     *
     * @param originalCommand
     * @return
     */
    private static String validateSystemAndMassageCommand(final String originalCommand) {
        // make sure that we have a command
        if (originalCommand.isEmpty() || (originalCommand.length() < 1)) {
            String errorMessage = "Missing or empty command line parameter.";
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
        // make sure that we are running on a supported system, and if so set the command line appropriately
        String massagedCommand;
        String osName = System.getProperty("os.name");
        if (osName.equals("Windows XP")) {
            //massagedCommand = "cmd.exe /C " + originalCommand;
        } else if (osName.equals("Solaris") || osName.equals("SunOS") || osName.equals("Linux")) {
            //massagedCommand = originalCommand;
        } else {
            String errorMessage = "Unable to run on this system which is not Solaris, Linux, or Windows XP (actual OS type: \'" +
                    osName + "\').";
            //log.error(errorMessage);
            //throw new RuntimeException(errorMessage);
        }
        return originalCommand;
    }
}