package com.leidos.ode.util;

import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: LAMDE
 * Date: 10/9/14
 * Time: 1:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class MongoUtils {

    private static final String TAG = "MongoUtils";
    private static final String MONGO_EXE = "mongod.exe";
    private static final Logger logger = Logger.getLogger(TAG);

    public static boolean isMongoDBRunning() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return isMongoDBRunningInWindows();
        }
        if (SystemUtils.IS_OS_LINUX) {
            return isMongoDBRunningInLinux();
        }
        return false;
    }

    private static boolean isMongoDBRunningInWindows() {
        try {
            String command = new StringBuilder()
                    .append(System.getenv("windir"))
                    .append("\\system32\\tasklist.exe")
                    .toString();

            Process p = Runtime.getRuntime().exec(command);
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            while ((line = input.readLine()) != null) {
                if (line.contains(MONGO_EXE)) {
                    return true;
                }
            }
            input.close();
        } catch (IOException e) {
            getLogger().error(e.getLocalizedMessage());
        }
        return false;
    }

    private static boolean isMongoDBRunningInLinux() {
        String logFile = "/var/log/mongodb/mongod.log";
        String findStr = "[initandlisten] waiting for connections on port ";
        String command = new StringBuilder()
                .append("grep -Fxq")
                .append(" ")
                .append("\"").append(findStr).append("\"")
                .append(" ")
                .append(logFile)
                .toString();
        Process process = executeCommand(command);
        return process != null && process.exitValue() == 0;
    }

    private static Process executeCommand(String... command) {
        try {
            ProcessBuilder ps = new ProcessBuilder(command);

            //From the DOC:  Initially, this property is false, meaning that the
            //standard output and error output of a subprocess are sent to two
            //separate streams
            ps.redirectErrorStream(true);

            Process pr = ps.start();

            BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                getLogger().debug(line);
            }
            pr.waitFor();
            getLogger().debug("Successfully executed command.");

            in.close();
            System.exit(0);
            return pr;
        } catch (IOException e) {
            getLogger().error(e.getLocalizedMessage());
        } catch (InterruptedException e) {
            getLogger().error(e.getLocalizedMessage());
        }
        return null;
    }

    //    public static boolean startMongoDB() {
//        if (SystemUtils.IS_OS_WINDOWS) {
//
//        }
//        if (SystemUtils.IS_OS_LINUX) {
//            String command = "sudo service mongod start";
//            Process process = executeCommand(command);
//            return process != null && process.exitValue() == 0;
//        }
//        return false;
//    }
//
//    public static void stopMongoDB() {
//
//    }

    private static Logger getLogger() {
        return logger;
    }
}
