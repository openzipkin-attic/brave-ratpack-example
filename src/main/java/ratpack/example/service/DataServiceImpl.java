package ratpack.example.service;

import java.util.UUID;

public class DataServiceImpl implements DataService {

  @Override
  public String generate() {
    return UUID.randomUUID().toString();
  }

}
