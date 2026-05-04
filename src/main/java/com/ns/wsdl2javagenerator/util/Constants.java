package com.ns.wsdl2javagenerator.util;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
public class Constants {

    public static String METHOD_GET = "GET";
    public static long MAX_WSDL_SIZE_BYTES = 10 * 1024 * 1024;
    public static int CONNECT_TIMEOUT_MS = 5_000;
    public static int READ_TIMEOUT_MS    = 15_000;
    public static ArrayList<String> unwanted = new ArrayList<>(Arrays.asList("build.xml", "build.properties", "pom.xml"));
    public static Set<String> ALLOWED_SCHEMES = new HashSet<>(Arrays.asList("http", "https"));
    public static String ZIP_NAME = "wsdlFiles.zip";
}
