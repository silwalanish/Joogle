package webcrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequest {

  protected URL url;
  protected HttpURLConnection connection;

  public HttpRequest(URL url) {
    this.url = url;
  }

  public void connect() throws IOException {
    connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
  }

  public String getResponse() throws IOException {
    StringBuffer buffer = new StringBuffer();
    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

    String line;
    while ((line = reader.readLine()) != null) {
      buffer.append(line);
    }

    reader.close();

    return buffer.toString();
  }

  public void disconnect() {
    connection.disconnect();
  }

}
