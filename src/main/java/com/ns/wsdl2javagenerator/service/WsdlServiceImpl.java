package com.ns.wsdl2javagenerator.service;

import com.ns.wsdl2javagenerator.exception.WsdlDownloadException;
import com.ns.wsdl2javagenerator.exception.WsdlProcessingException;
import com.ns.wsdl2javagenerator.exception.WsdlValidationException;
import com.ns.wsdl2javagenerator.model.WsdlRequest;
import com.ns.wsdl2javagenerator.util.AxisApiGenerator;
import com.ns.wsdl2javagenerator.util.Constants;
import com.ns.wsdl2javagenerator.util.ZipUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class WsdlServiceImpl implements WsdlService {

    private static final Logger log = LoggerFactory.getLogger(WsdlServiceImpl.class);

    @Override
    public byte[] processWsdl(WsdlRequest request) {
        validateRequest(request);

        Path tempDir = null;

        try {
            tempDir = Files.createTempDirectory("wsdl-gen-");

            Path srcDir = Files.createDirectories(tempDir.resolve("src"));
            Path wsdlFile = tempDir.resolve("Service.wsdl");

            downloadWsdl(request.getWsdlUrl(), wsdlFile);

            AxisApiGenerator.execute(wsdlFile, srcDir);

            cleanUnwantedFiles(srcDir);

            if (!containsJavaFiles(srcDir)) {
                throw new WsdlProcessingException("Generated output contains no Java files. Possibly invalid WSDL.");
            }

            return ZipUtil.zipDirectory(srcDir);
        } catch (Exception e) {
            throw new WsdlProcessingException("Internal error processing WSDL", e);

        } finally {
            deleteDirectoryQuietly(tempDir);
        }
    }

    private void validateRequest(WsdlRequest request) {
        try {
            URI uri = new URI(request.getWsdlUrl().trim());
            String scheme = uri.getScheme();
            if (StringUtils.isBlank(scheme) || !Constants.ALLOWED_SCHEMES.contains(scheme.toLowerCase())) {
                throw new WsdlValidationException("Protocol not permitted: " + scheme);
            }

            String host = uri.getHost();
            boolean isLocal = StringUtils.isBlank(host) || host.equals("localhost") || host.startsWith("127.") || host.startsWith("10.");

            if (isLocal) throw new WsdlValidationException("Invalid or restricted host");
        } catch (Exception e) {
            throw new WsdlValidationException("Invalid WSDL URL");
        }
    }

    private void downloadWsdl(String wsdlUrl, Path target) {
        try {
            URL url = new URI(wsdlUrl.trim()).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setConnectTimeout(Constants.CONNECT_TIMEOUT_MS);
            conn.setReadTimeout(Constants.READ_TIMEOUT_MS);
            conn.setRequestMethod(Constants.METHOD_GET);
            conn.setRequestProperty("User-Agent", "wsdl2java-generator/1.0");

            int status = conn.getResponseCode();
            if (status < 200 || status >= 300) throw new WsdlDownloadException("HTTP error: " + status);

            long contentLength = conn.getContentLengthLong();
            if (contentLength > Constants.MAX_WSDL_SIZE_BYTES) {
                throw new WsdlDownloadException("WSDL too large: " + contentLength);
            }

            try (InputStream in = conn.getInputStream()) {
                long copied = Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);

                if (copied > Constants.MAX_WSDL_SIZE_BYTES) {
                    Files.deleteIfExists(target);
                    throw new WsdlDownloadException("WSDL exceeded size limit during download");
                }
            } finally {
                conn.disconnect();
            }
        } catch (Exception e) {
            throw new WsdlDownloadException("Failed to download WSDL", e);
        }
    }

    private boolean containsJavaFiles(Path dir) throws Exception {
        try (Stream<Path> stream = Files.walk(dir)) {
            return stream.filter(Files::isRegularFile)
                    .anyMatch(p -> p.toString().endsWith(".java"));
        }
    }

    private void cleanUnwantedFiles(Path srcDir) {
        if (!Files.exists(srcDir)) return;

        List<String> unwanted = Constants.unwanted;

        try (Stream<Path> stream = Files.walk(srcDir)) {
            stream.filter(Files::isRegularFile).filter(file -> unwanted.contains(file.getFileName().toString()))
                    .forEach(file -> {
                        try {
                            Files.deleteIfExists(file);
                        } catch (Exception e) {
                            log.warn("Could not delete file: {}", file, e);
                        }
                    });

        } catch (Exception e) {
            log.warn("Error cleaning unwanted files", e);
        }
    }

    private void deleteDirectoryQuietly(Path root) {
        if (root == null) return;

        try (Stream<Path> walk = Files.walk(root)) {
            walk.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (Exception e) {
                            log.warn("Could not delete temp file: {}", path, e);
                        }
                    });

        } catch (Exception e) {
            log.error("Error cleaning temp directory: {}", root, e);
        }
    }
}