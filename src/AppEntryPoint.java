import java.io.IOException;
import java.net.MalformedURLException;

public class AppEntryPoint {

  public static void main(String[] args) throws MalformedURLException {
    Joogle app = new Joogle(new String[]{
            "JAVA",
            "Oracle"
    }, "./urls-1000.txt", 10);
    try {
      app.start();
    } catch (IOException e) {
      System.out.println("An error occurred while running.");
      System.out.println(e.getMessage());
    }
  }

}

