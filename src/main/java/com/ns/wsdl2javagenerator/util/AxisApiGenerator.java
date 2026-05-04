package com.ns.wsdl2javagenerator.util;

import org.jspecify.annotations.NonNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class AxisApiGenerator {

    private static final int TIMEOUT_SECONDS = 60;

    public static void execute(@NonNull Path wsdlFile, @NonNull Path outputDir) throws Exception {
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome + "/bin/java";
        String classpath = System.getProperty("java.class.path");

        ProcessBuilder pb = new ProcessBuilder(
                javaBin,
                "-cp", classpath,
                "org.apache.axis2.wsdl.WSDL2Java",
                "-uri", wsdlFile.toAbsolutePath().toString(),
                "-o", outputDir.toAbsolutePath().toString(),
                "-d", "adb",
                "-s", "-u"
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append('\n');
            }
        }

        boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            throw new RuntimeException("Axis2 timed out after " + TIMEOUT_SECONDS + "s");
        }

        int exitCode = process.exitValue();
        if (exitCode != 0) {
            throw new RuntimeException("Axis2 failed. Exit=" + exitCode + "\nOutput:\n" + output);
        }
    }
}