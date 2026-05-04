package com.ns.wsdl2javagenerator.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.stream.Stream;

public class ZipUtil {

    public static byte[] zipDirectory(Path sourceDir) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos);
             Stream<Path> paths = Files.walk(sourceDir)) {

            paths.filter(Files::isRegularFile)
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(sourceDir.relativize(path).toString());
                        try {
                            zos.putNextEntry(zipEntry);
                            Files.copy(path, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            throw new RuntimeException("Error zipping file: " + path, e);
                        }
                    });

            zos.finish();
            return baos.toByteArray();
        }
    }
}