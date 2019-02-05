package ratpack.example.module;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import ratpack.example.service.DataService;
import ratpack.example.service.DataServiceImpl;
import ratpack.example.service.RemoteDataService;
import ratpack.example.service.RemoteDataServiceImpl;

public class DependencyModule extends AbstractModule {

  @Override
  public void configure() {
    bind(RemoteDataService.class).to(RemoteDataServiceImpl.class)
      .in(Scopes.SINGLETON);
    bind(DataService.class).to(DataServiceImpl.class)
      .in(Scopes.SINGLETON);
  }
}
