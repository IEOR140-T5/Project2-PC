package grid;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.Delay;
import lejos.nxt.*;

import java.io.*;

/**
 * This class needs a higher level controller to implement the navigtion logic<br>
 * Responsibility: keep robot on the line till it senses a marker, then stop <br>
 * also controls turning to a new line at +- 90 or 180 deg<br>
 * Hardware: Two light sensors , shielded, 2 LU above floor. Classes used:
 * Pilot, LightSensors<br>
 * Control Algorithm: proportional control. estimate distance from centerline<br>
 * Calibrates both sensors to line, background Updated 9/10/2007 NXT hardware
 * 
 * @author Roger Glassey, Phuoc Nguyen, Khoa Tran, Trevor Davenport
 */
public class Tracker {

	/**
	 * controls the motors
	 */
	public DifferentialPilot pilot;
	/**
	 * set by constructor , used by trackline()
	 */
	private LightSensor leftEye;
	/**
	 * set by constructor , used by trackline()
	 */
	private LightSensor rightEye;

	private int _turnDirection = 1;

	private int markerValue = -10;

	/**
	 * Constructor specifies which sensor ports are left and right
	 * 
	 * @param thePilot
	 *            - pilot for the tracker
	 * @param leftEye
	 *            - left sensor
	 * @param rightEye
	 *            - right sensor
	 */
	public Tracker(DifferentialPilot thePilot, LightSensor leftEye,
			LightSensor rightEye) {
		pilot = thePilot;
		pilot.setTravelSpeed(20);
		pilot.setRotateSpeed(180);
		pilot.setAcceleration(200);
		this.leftEye = leftEye;
		this.leftEye.setFloodlight(true);
		this.rightEye = rightEye;
		this.rightEye.setFloodlight(true);
	}

	/**
	 * follow line till intersection is detected uses proportional control <br>
	 * Error signal is supplied by CLdistance()<br>
	 * uses CLdistance(), pilot.steer() loop execution about 65 times per second
	 * in 1 sec.<br>
	 */
	public void trackLine() {
		int error = 0; // approximate offset from center of line
		int lval = leftEye.getLightValue();
		int rval = rightEye.getLightValue();
		error = CLDistance(lval, rval);
		int control = 0; // do better
		boolean atMarker = false;

		while (!atMarker) {
			lval = leftEye.getLightValue();
			rval = rightEye.getLightValue();
			LCD.drawInt(lval, 4, 0, 5);
			LCD.drawInt(rval, 4, 4, 5);
			LCD.drawInt(CLDistance(lval, rval), 4, 12, 5);
			LCD.refresh();

			if ((lval < markerValue) | (rval < markerValue))
				atMarker = true;

			if (!atMarker) {
				error = CLDistance(lval, rval);
				pilot.steer(error * 0.4);
			} else {
				Sound.playTone(1000, 100);
				pilot.travel(11, false);
				Delay.msDelay(400);
			}
		}

	}

	/**
	 * helper method for Tracker; calculates distance from centerline, used as
	 * error by trackLine()
	 * 
	 * @param left
	 *            light reading
	 * @param right
	 *            light reading
	 * @return distance
	 */
	int CLDistance(int left, int right) {
		return (left - right);
	}

	/**
	 * stop the robot
	 ** 
	 * @param: void
	 **/
	public void stop() {
		pilot.stop();
	}

	/**
	 * Turn the Robot at direction * 90
	 * 
	 * @param: multiples of 90
	 */
	public void turn(double direction) {
		pilot.rotate(direction * 110);
	}

	/**
	 * Sleep the robot by delaying the while loop for a ms = milisecond
	 * 
	 * @param: miliseconds to sleep
	 */
	public void sleepRobot(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {}
	}

	/**
	 * get value of leftEye.getLightValue()
	 * 
	 * @return leftEye.getLightValue()
	 */
	public int getlval() {
		return this.leftEye.getLightValue();
	}

	/**
	 * get value of rightEye.getLightValue()
	 * 
	 * @return rightEye.getLightValue()
	 */
	public int getrval() {
		return this.rightEye.getLightValue();
	}

	/**
	 * Decide whether the robot is moving or not
	 * 
	 * @param lval
	 * @param rval
	 * @return
	 */
	public boolean isMoving() {
		return pilot.isMoving();
	}

	/**
	 * calibrates for line first, then background, then marker with left sensor.
	 * displays light sensor readings on LCD (percent)<br>
	 * Then displays left sensor (scaled value). Move left sensor over marker,
	 * press Enter to set marker value to sensorRead()/2
	 */
	public void calibrate() {
		System.out.println("Calibrate Tracker");

		for (byte i = 0; i < 3; i++) {
			while (0 == Button.readButtons()) { // wait for press
				LCD.drawInt(leftEye.getLightValue(), 4, 6, 1 + i);
				LCD.drawInt(rightEye.getLightValue(), 4, 12, 1 + i);
				if (i == 0) {
					LCD.drawString("LOW", 0, 1 + i);
				} else if (i == 1) {
					LCD.drawString("HIGH", 0, 1 + i);
				}
			}
			Sound.playTone(1000 + 200 * i, 100);
			if (i == 0) {
				leftEye.calibrateLow();
				rightEye.calibrateLow();
			} else if (i == 1) {
				rightEye.calibrateHigh();
				leftEye.calibrateHigh();
			} else {
				// markerValue = leftEye.getLightValue() / 2;
			}
			while (0 < Button.readButtons()) {
				Thread.yield(); // button released
			}
		}

		// reflect real-time CLDistance bases on lval, rval
		while (0 == Button.readButtons()) {
			int lval = leftEye.getLightValue();
			int rval = rightEye.getLightValue();
			LCD.drawInt(lval, 4, 0, 5);
			LCD.drawInt(rval, 4, 4, 5);
			LCD.drawInt(CLDistance(lval, rval), 4, 12, 5);
			LCD.refresh();
		}
	}

}
