# BasicHTTPFileAndImageServer

A small multithreaded Java HTTP file server that serves files from host-specific folders using simple `GET` request parsing.

## Current Features

- Listens for TCP connections on port `51235` (`HttpServer.port`).
- Spawns a dedicated `HttpServerSession` thread per accepted connection.
- Parses basic HTTP request lines with `HttpServerRequest`:
  - Accepts `GET /path HTTP/*` format.
  - Supports `Host: <value>` extraction.
  - Maps `/` or `/folder/` to `index.html`.
- Virtual-host style file lookup:
  - Uses `Host` header folder when present.
  - Falls back to `localhost:51235/<file>` when `Host` is absent.
- Returns:
  - `HTTP/1.1 200 OK` + raw file bytes when found.
  - `HTTP/1.1 404 FileNotFound` for missing or invalid requests.
- Includes a starter JUnit 5 test file for request parsing (`HttpServerRequestTest.java`).

## Technology Stack

- Java (standard library networking and I/O)
- JUnit 5 (used by `HttpServerRequestTest.java`, dependency not vendored in this repo)

## Repository Structure

- `HttpServer.java` - server entry point and accept loop
- `HttpServerSession.java` - per-connection request/response handling
- `HttpServerRequest.java` - request parsing state machine
- `HttpServerRequestTest.java` - request parser tests (partially implemented)
- `localhost:51235/index.html` - sample host folder + default page

## Prerequisites

- Java JDK (for `javac` and `java`)
- (Optional, for tests) `junit-platform-console-standalone` JAR matching JUnit 5

## Build / Run

Current repository status (as of 2026-10-04):

- `javac HttpServerRequest.java` succeeds.
- Full server build currently fails because `HttpServerSession.java` references an undefined variable (`in`) in `run()`.

Parser compile command:

```bash
javac HttpServerRequest.java
```

When the server compile issue is fixed, the intended entry point remains:

```bash
java HttpServer
```

## Usage

1. Ensure content exists under a host folder in the project root, for example:
   - `localhost:51235/index.html` (already present)
2. After resolving the current server compile issue, request files from a client/browser:

```bash
curl -i http://localhost:51235/
curl -i http://localhost:51235/missing-file.txt
```

If `Host` is not provided by the client, the server resolves files under `localhost:51235/`.

## Testing

There is no build tool wrapper in this repository (no Maven/Gradle).  
To run parser tests, provide the JUnit Console Standalone JAR yourself, then run:

```bash
javac -cp junit-platform-console-standalone-1.9.0.jar HttpServerRequest.java HttpServerRequestTest.java
java -jar junit-platform-console-standalone-1.9.0.jar -cp . -c HttpServerRequestTest
```

Note: `HttpServerRequestTest.java` currently contains several placeholder test methods with no assertions.

## Configuration

- Port is hardcoded to `51235` in `HttpServer`.
- File root is effectively `<host-header-or-localhost:51235>/<requested-file>`.
- `Host:` parsing keeps only the token after `Host: ` (space-delimited).

## Limitations / Current Status

- Supports only basic `GET` requests.
- Does not send standard HTTP headers such as `Content-Type` or `Content-Length`.
- Uses a non-standard 404 reason phrase (`FileNotFound`).
- Handles one request per connection session and then closes socket.
- No package manager/build automation or CI config is included in this repository.

## Attribution

This project appears to originate from an HTTP server programming assignment (the previous README contained the original assignment brief and lab context).
