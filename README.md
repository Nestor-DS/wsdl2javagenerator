# wsdl2javagenerator

Spring Boot REST service that downloads a WSDL from a given URL, runs Apache Axis2 WSDL2Java code generation, and returns the generated Java stubs as a ZIP file.

---

>[!IMPORTANT]
> Known limitations
> - One WSDL URL per request; no batch mode.
> - Generated stubs use Axis2 ADB (Axis Data Binding) only (`-d adb`). XMLBeans, JiBX, and other databinding options are not exposed.
> - The service does not cache results. Every request triggers a full download and code generation cycle.
> - Axis2 classpath dependency must be bundled in the fat JAR or available at runtime; there is no automatic download.

---

## API

### Generate Java stubs from WSDL

```
POST /wsdl/generate
Content-Type: application/json
```

**Request body:**

```json
{
  "wsdlUrl": "https://example.com/service?wsdl"
}
```

**Response:**

```
200 OK
Content-Type: application/octet-stream
Content-Disposition: attachment; filename="wsdlFiles.zip"
```

Binary ZIP containing the generated `.java` source files, with build artifacts (`build.xml`, `build.properties`, `pom.xml`) stripped out.

**Error responses:**

| Scenario | Exception type |
|---|---|
| Malformed or restricted URL | `WsdlValidationException` |
| HTTP error or WSDL too large (> 10 MB) | `WsdlDownloadException` |
| Axis2 failure or no `.java` files generated | `WsdlProcessingException` |

---

## Configuration

The following values are defined in `Constants.java` and can be changed at compile time:

| Constant | Default | Description |
|---|---|---|
| `MAX_WSDL_SIZE_BYTES` | `10485760` (10 MB) | Maximum allowed WSDL size |
| `CONNECT_TIMEOUT_MS` | `5000` | HTTP connection timeout |
| `READ_TIMEOUT_MS` | `15000` | HTTP read timeout |
| `ALLOWED_SCHEMES` | `http`, `https` | Permitted URL schemes |
| `ZIP_NAME` | `wsdlFiles.zip` | Name of the returned ZIP file |


---

## Security notes

- Only `http` and `https` URL schemes are accepted.
- Requests to `localhost`, `127.x.x.x`, and `10.x.x.x` ranges are rejected to mitigate SSRF.
- WSDL size is validated both from `Content-Length` header and from actual bytes copied, to prevent partial bypasses.
- Axis2 runs as a subprocess; it does not share the application heap.
- Temporary files are always deleted in a `finally` block regardless of outcome.

> **Warning:** The current SSRF filter does not cover `172.16.0.0/12` or `192.168.0.0/16` private ranges. If this service is deployed in an environment where those ranges are reachable, you must add those checks to `WsdlServiceImpl.validateRequest()`.
