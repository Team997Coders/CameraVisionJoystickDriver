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
  private static final int CENTER_BUTTON = 10;

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    // First create a joystick object.
    JInputJoystick joystick = new JInputJoystick(Controller.Type.STICK, Controller.Type.GAMEPAD);

    // Check if a joystick was found.
    if( !joystick.isControllerConnected() ){
      System.out.println("No controller found!");
      System.exit(0);
    }

    // Create a teensy object.
    try(CameraVisionClient cameraVisionClient = new CameraVisionClient()) {
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
          if (joystick.getButtonValue(CENTER_BUTTON)) {
            cameraVisionClient.center();
          } else if (joystick.getButtonValue(A_BUTTON)) {
            cameraVisionClient.pressA();
          } else if (joystick.getButtonValue(B_BUTTON)) {
            cameraVisionClient.pressB();
          } else if (joystick.getButtonValue(X_BUTTON)) {
            cameraVisionClient.pressX();
          } else if (joystick.getButtonValue(Y_BUTTON)) {
            cameraVisionClient.pressY();
          }
          // Always slew
          cameraVisionClient.slew(Math.round(xValueLeftJoystick * 100) * -1, Math.round(yValueLeftJoystick * 100));
        } catch (IndexOutOfBoundsException e) {
          System.out.println("Center button not found...continuing.");
        }
        try {
          // Don't spin the CPUs
          Thread.sleep(100);
        } catch (InterruptedException e) {
          System.exit(0);
        }
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
      System.exit(1);
    }
  }    
}