# CameraVisionJoystickDriver Project

This project will connect to a joystick and drive the [CameraVision](https://github.com/Team997Coders/2019DSHatchFindingVision/tree/master/CameraVision) project.
It communicates over network sockets to the CameraVision project listening on port 2222.

The CameraVision project will attempt to connect to a [MiniPanTiltTeensy](https://github.com/Team997Coders/MiniPanTiltTeensy) so that joystick commands can pan and tilt an [Adafruit mini pan tilt servo assembly](https://www.adafruit.com/product/1967). Further, the CameraVision project will target lock on a hatch target by automatically controlling pan/tilt.

## Usage

Connect a joystick, start up the CameraVision project, have a [Teensy](https://www.pjrc.com/store/teensy35.html) flashed with firmware from project above and make sure it is plugged in (both servos and USB) to the same host that is running the CameraVision app.<p>

Then, ```gradlew run```.
