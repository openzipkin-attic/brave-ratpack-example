package ratpack.example.service;

import ratpack.exec.Promise;

import java.util.List;

public interface RemoteDataService {

  Promise<String> generate() throws Exception;

  Promise<List<String>> generateMany(int count) throws Exception;

}
