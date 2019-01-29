import joystick.JInputJoystick;

import net.java.games.input.*;

/**
 * CameraVisionJoystickDriver Main class<p>
 * This program will search for a joystick and a then send commands to the CameraVision application command
 * port (currently 2222). Assumes that the CameraVision app is running on localhost. Change me to accept a host name.
 */
public class Main {
  private static final int A_BUTTON = 1;
  private static final int B_BUTTON = 2;
  private static final int X_BUTTON = 0;
  private static final int Y_BUTTON = 3;
  private static final int LEFT_THUMBSTICK_BUTTON = 10;
  private static final int RIGHT_THUMBSTICK_BUTTON = 11;
  private static final int LEFT_SHOULDER_BUTTON = 4;
  private static final int RIGHT_SHOULDER_BUTTON = 5;
  private static final int LEFT_TRIGGER_BUTTON = 6;
  private static final int RIGHT_TRIGGER_BUTTON = 7;
  private static long lastClickedMillis = 0;
  private static final long DEBOUNCE_TIME = 250;

  interface Debouncer {
    void call();
  } 

  /**
   * @param args the command line arguments
   */
  public static void main(String ... argv) {
    Main main = new Main();
    RuntimeSettings runtimeSettings = new RuntimeSettings(argv);
    if (runtimeSettings.parse()) {
      if (runtimeSettings.getHelp()) {
        // print out the usage to sysout
        runtimeSettings.printUsage();
      } else {
        // run the app
        main.run(runtimeSettings);
        System.exit(0);
      }
    } else {
      // print the parameter error, show the usage, and bail
      System.err.println(runtimeSettings.getParseErrorMessage());
      runtimeSettings.printUsage();
      System.exit(1);
    }
  }  

  /**
   * The meat of the application.
   * 
   * @param runtimeSettings   Wired up settings from the command line.
   */
  public void run(RuntimeSettings runtimeSettings) {
    // First create a joystick object.
    JInputJoystick joystick = new JInputJoystick(Controller.Type.STICK, Controller.Type.GAMEPAD);

    // Check if a joystick was found.
    if( !joystick.isControllerConnected() ){
      System.out.println("No controller found!");
      System.exit(0);
    }

    // Create a teensy object.
    try(CameraVisionClient cameraVisionClient = new CameraVisionClient(runtimeSettings.getHost(), runtimeSettings.getPort())) {
      while(true) {
        // Get current state of joystick and check if joystick is disconnected.
        if( !joystick.pollController() ) {
          // Bail out
          System.out.println("Controller disconnected!");
          System.exit(1);
        }
        
        // Get left controller joystick values
        float xValueLeftJoystick = joystick.getXAxisValue();
        float yValueLeftJoystick = joystick.getYAxisValue();
        
        // Read gamepad button states and forward states and joystick
        // values to CameraVisionClient.
        try {
          if (joystick.getButtonValue(LEFT_THUMBSTICK_BUTTON)) {
            debounce(cameraVisionClient::pressLeftThumbstick);
          } else if (joystick.getButtonValue(RIGHT_THUMBSTICK_BUTTON)) {
            debounce(cameraVisionClient::pressRightThumbstick);
          } else if (joystick.getButtonValue(LEFT_SHOULDER_BUTTON)) {
            debounce(cameraVisionClient::pressLeftShoulder);
          } else if (joystick.getButtonValue(RIGHT_SHOULDER_BUTTON)) {
            debounce(cameraVisionClient::pressRightShoulder);
          } else if (joystick.getButtonValue(LEFT_TRIGGER_BUTTON)) {
            debounce(cameraVisionClient::pressLeftTrigger);
          } else if (joystick.getButtonValue(RIGHT_TRIGGER_BUTTON)) {
            debounce(cameraVisionClient::pressRightTrigger);
          } else if (joystick.getButtonValue(A_BUTTON)) {
            debounce(cameraVisionClient::pressA);
          } else if (joystick.getButtonValue(B_BUTTON)) {
            debounce(cameraVisionClient::pressB);
          } else if (joystick.getButtonValue(X_BUTTON)) {
            debounce(cameraVisionClient::pressX);
          } else if (joystick.getButtonValue(Y_BUTTON)) {
            debounce(cameraVisionClient::pressY);
          }
          // Always slew
          cameraVisionClient.slew(Math.round(xValueLeftJoystick * 100) * -1, Math.round(yValueLeftJoystick * 100));
        } catch (IndexOutOfBoundsException e) {
          System.out.println("Expected buttons not found...continuing.");
        }
        try {
          // Don't spin the CPUs
          Thread.sleep(20);
        } catch (InterruptedException e) {
          System.exit(0);
        }
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
      System.exit(1);
    }
  }

  public static void debounce(Debouncer func) {
    long nowMillis = System.currentTimeMillis();
    if ((nowMillis - lastClickedMillis) > DEBOUNCE_TIME) {
      lastClickedMillis = nowMillis;
      func.call();
    } else {
      // Do nothing
    }
  }
}