package ratpack.example.config;

import brave.sampler.Sampler;
import ratpack.zipkin.ServerTracingModule;
import zipkin2.Span;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Reporter;
import zipkin2.reporter.urlconnection.URLConnectionSender;

public class BraveConfig {
  private String reporter;
  private String serviceName;

  private BraveHttpConfig http;

  public String getReporter() {
    return this.reporter;
  }

  public void setReporter(String reporter) {
    this.reporter = reporter;
  }

  public String getServiceName() {
    return this.serviceName;
  }

  public void setServiceName(String name) {
    this.serviceName = name;
  }

  public BraveHttpConfig getHttp() {
    return this.http;
  }

  public void setHttp(BraveHttpConfig http) {
    this.http = http;
  }

  public ServerTracingModule.Config toModuleConfig() {
    return new ServerTracingModule.Config()
      .serviceName(serviceName)
      .spanReporterV2(getSpanReporter())
      .sampler(Sampler.ALWAYS_SAMPLE);
  }

  private Reporter<Span> getSpanReporter() {
    if ("console".equals(reporter.toLowerCase())) {
      return Reporter.CONSOLE;
    } else if ("http".equals(reporter.toLowerCase())) {
      return AsyncReporter.create(URLConnectionSender.create(this.http.getEndpoint()));
    } else {
      return Reporter.NOOP;
    }
  }
}
