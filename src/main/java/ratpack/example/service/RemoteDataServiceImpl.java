package ratpack.example.service;

import brave.ScopedSpan;
import brave.http.HttpTracing;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import ratpack.exec.Promise;
import ratpack.exec.util.ParallelBatch;
import ratpack.http.client.HttpClient;
import ratpack.zipkin.Zipkin;

public class RemoteDataServiceImpl implements RemoteDataService {

  @Inject
  private HttpTracing httpTracing;

  @Inject
  @Zipkin
  private HttpClient client;

  @Override
  public Promise<String> generate() throws Exception {
    ScopedSpan local = httpTracing.tracing().tracer().startScopedSpan("remote generate");
    return client.get(new URI("http://localhost:5050/generate"))
      .wiretap(result -> {
        if (result.isError()) {
          local.error(result.getThrowable());
        } else if (result.getValue().getStatusCode() != 200) {
          local.tag("error", "remote failed with status " + result.getValue().getStatusCode());
        }
        local.tag("test", "local test tag");
        local.finish();
      })
      .map(response -> response.getBody().getText());
  }

  @Override
  public Promise<List<String>> generateMany(int count) throws Exception {
    List<Promise<String>> promises = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      promises.add(generate());
    }
    return ParallelBatch.of(promises).yield();
  }
}
