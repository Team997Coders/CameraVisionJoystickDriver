import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Class which sends commands to the CameraVision application over
 * network sockets.
 */
public class CameraVisionClient implements Closeable {
  private final Socket socket;
  private final PrintWriter client;
  private boolean lastPanWasZero;
  private boolean lastTiltWasZero;

  /**
   * Constructor that assumes the client is running on localhost and 
   * the default port (2222).
   * @throws UnknownHostException
   * @throws IOException
   */
  public CameraVisionClient() throws UnknownHostException, IOException {
    this(null);
  }

  /**
   * Constructor requiring a host name running the CameraVision application.
   * Assume that the default port is used (2222).
   * @param host                  The name of the host running the application.
   * @throws UnknownHostException IP address cannot be found.
   * @throws IOException          Thrown if...who knows.
   */
  public CameraVisionClient(String host) throws UnknownHostException, IOException {
    this(host, 2222);
  }

  /**
   * Constructor requiring a host name running the CameraVision application.
   * Assume that the default port is used (2222).
   * @param host                  The name of the host running the application.
   * @param port                  The port that the application is listening for commands.
   * @throws UnknownHostException IP address cannot be found.
   * @throws IOException          Thrown if...who knows.
   */
  public CameraVisionClient(String host, int port) throws UnknownHostException, IOException {
    // Socket to talk to the CameraVision application
    this.socket = new Socket(host, port);
    // PrintWriter through which we will write commands to
    this.client = new PrintWriter(socket.getOutputStream(), true);
    lastPanWasZero = true;
    lastTiltWasZero = true;
  }

  public void close() throws IOException {
    socket.close();
  }

  /**
   * Slew (both pan and tilt) the camera with values between -100..100,
   * which represents the percentage rate to slew to maximum.
   * 
   * @param panPct  Percentage of maximum rate to pan
   * @param tiltPct Percentage of maximum rate to tilt
   */
  public void slew(int panPct, int tiltPct) {
    // Keep from flooding pipe with zero pan commands
    // Also treat -1..1 as zero to deal with rounding issues from joystick
    if (panPct >= -2 && panPct <= 2) {
      if (!lastPanWasZero) {
        client.printf("%dp", 0);
        lastPanWasZero = true;
      }
    } else {
      client.printf("%dp", panPct);
      lastPanWasZero = false;
    }
    // Keep from flooding pipe with zero tilt commands
    // Also treat -1..1 as zero to deal with rounding issues from joystick
    if (tiltPct >= -2 && tiltPct <= 2) {
      if (!lastTiltWasZero) {
        client.printf("%dt", 0);
        lastTiltWasZero = true;
      }
    } else {
      client.printf("%dt", tiltPct);
      lastTiltWasZero = false;
    }
  }

  /**
   * Center the camera on both axes.
   */
  public void center() {
    client.printf("c");
  }

  /**
   * Press A.
   */
  public void pressA() {
    client.printf("A");
  }

  /**
   * Press B.
   */
  public void pressB() {
    client.printf("B");
  }

  /**
   * Press X.
   */
  public void pressX() {
    client.printf("X");
  }

  /**
   * Press Y.
   */
  public void pressY() {
    client.printf("Y");
  }
}