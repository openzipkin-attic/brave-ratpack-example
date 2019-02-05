package ratpack.example.service;

import brave.ScopedSpan;
import brave.Span;
import brave.Tracer;
import brave.http.HttpTracing;
import ratpack.exec.Promise;
import ratpack.http.client.HttpClient;
import ratpack.zipkin.TracedParallelBatch;
import ratpack.zipkin.Zipkin;

import javax.inject.Inject;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class RemoteDataServiceImpl implements RemoteDataService {

  @Inject
  private HttpTracing httpTracing;

  @Inject
  @Zipkin
  private HttpClient client;


  @Override
  public Promise<String> generate() throws Exception {
    final Span local = httpTracing.tracing().tracer()
      .nextSpan()
      .name("remote generate")
      .start();
    final Tracer.SpanInScope ws = httpTracing.tracing().tracer()
      .withSpanInScope(local);
    return client.get(new URI("http://localhost:5050/generate"))
      .wiretap(result -> {
        if (result.isError()) {
          local.error(result.getThrowable());
        } else if (result.getValue().getStatusCode() != 200) {
          local.tag("error", "remote failed with status " + result.getValue().getStatusCode());
        }
        local.tag("test", "local test tag");
        local.finish();
        ws.close();
      })
      .map(response -> response.getBody().getText());
  }

  @Override
  public Promise<List<String>> generateMany(int count) throws Exception {
    List<Promise<String>> promises = new ArrayList<>();
    for (int i=0; i<count; i++) {
      promises.add(generate());
    }
    return TracedParallelBatch.of(promises)
      .withContext(httpTracing.tracing().currentTraceContext().get())
      .yield();
  }

}
