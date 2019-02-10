# Basic example showing distributed tracing with a Ratpack app
This is a single [Ratpack](https://ratpack.io) example service with 2 endpoints that show usage of
simple request/response timing, http client call timing, local spans and context
propagation with [Ratpack ParallelBatch](https://ratpack.io/manual/current/api/ratpack/exec/util/ParallelBatch.html).

The 3 endpoints are:
- /generate
  
  Generates a single UUID with request/response tracing
  
- /remote/generate

  Generates a single UUID by performing an HTTP client request to /generate.
  
- /remote/generatemany

  Generates many UUIDs by calling /generate with an HTTP client request.
  Before making a remote HTTP client call a local span is created.  All
  client calls are executed concurrently using a ParallelBatch.

Here's an example of what /remote/generatemany looks like

TODO add screenshot

#Implementation Overview
Web request are served by [Ratpack](https://ratpack.io), which traces request by using [Brave Ratpack](https://github.com/openzipkin-contrib/brave-ratpack) plugin.

These traces are sent out of process over http to Zipkin.

#Running the example
This example has a single service which calls itself for the /remote/* endpoints.

Once the service is started, open one of the 3 URLs, 
http://localhost:5050/generate; http://localhost:5050/remote/generate; http://localhost:5050/remote/generatemany

Next, you can view traces via http://localhost:9411/zipkin/?serviceName=example

- This is a locally run [Zipkin](https://zipkin.io/) service which keeps traces in memory

##Starting the Services
Run the Ratpack example: 
```bash
$ ./mvnw compile exec:java
```

Next, run [Zipkin](https://zipkin.io/), which stores and queries traces reported by the above service.
```bash
$ curl -sSL https://zipkin.io/quickstart.sh | bash -s
$ java -jar zipkin.jar
```
