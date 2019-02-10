package ratpack.example;

import ratpack.example.config.BraveConfig;
import ratpack.example.module.DependencyModule;
import ratpack.example.service.DataService;
import ratpack.example.service.RemoteDataService;
import ratpack.guice.Guice;
import ratpack.server.RatpackServer;
import ratpack.server.ServerConfig;
import ratpack.zipkin.ServerTracingModule;

public class Main {

  public static void main(String[] args) throws Exception {

    ServerConfig serverConfig = ServerConfig
      .embedded()
      .yaml(Main.class.getResource("/application.yaml"))
      .require("/brave", BraveConfig.class)
      .port(5050)
      .build();

    RatpackServer
      .start(server -> server
        .serverConfig(serverConfig)
        .registry(Guice
          .registry(bindings -> bindings
            .moduleConfig(ServerTracingModule.class, serverConfig.get("/brave", BraveConfig.class).toModuleConfig())
            .module(DependencyModule.class)
          )
        )
        .handlers(chain -> chain
          .get("generate", ctx -> {
            DataService service = ctx.get(DataService.class);
            ctx.render(service.generate());
          })
          .get("remote/generate", ctx -> {
            ctx.get(RemoteDataService.class).generate().then(s -> ctx.render(s));
          })
          .get("remote/generatemany", ctx -> {
            ctx.get(RemoteDataService.class).generateMany(10).then(s -> ctx.render(String.join(", ", s)));
          })
        )
      );
  }

}
