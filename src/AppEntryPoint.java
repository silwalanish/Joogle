import java.io.IOException;

public class AppEntryPoint {

  public static void main(String[] args){
    Joogle app = new Joogle(new String[] {
            "JAVA",
            "Oracle"
    }, "./urls.txt", 10);
    try {
      app.start();
    } catch (IOException e) {
      System.out.println("An error occurred while running.");
      System.out.println(e.getMessage());
    }
  }

}

