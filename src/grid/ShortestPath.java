package grid;

import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.Sound;
import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.util.ButtonCounter;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.nxt.UltrasonicSensor;

public class ShortestPath {

	/**
	 * Instance and static variables that controls the robot's movement
	 */
	public static final int MIN_DIST = 25;
	private Tracker tracker;
	private UltrasonicSensor usensor;
	
	/**
	 * The robot's heading, current position, the grid, and a ButtonCounter
	 * to set the grid's destination
	 */
	private int heading = 0;
	private Node currentPosition;
	private Grid grid;
	ButtonCounter bc = new ButtonCounter();
	
	/**
	 * Constructor for the ShortestPath class that takes 4 parameters
	 * @param tracker - the tracker reused from earlier milestones
	 * @param usensor - ultrasonic sensor for detecting objects/blocked paths
	 * @param x - the horizontal length of the track
	 * @param y - the vertical length of the track
	 */
	public ShortestPath(Tracker tracker, UltrasonicSensor usensor, int x, int y) {
		this.tracker = tracker;
		this.usensor = usensor;
		grid = new Grid(x, y);
		currentPosition = grid.nodes[0][0];
		heading = 0;
	}

	/**
	 * Carries out the mission
	 */
	public void go() {
		tracker.calibrate();
		heading = 0;
		while (true) {
			setDestination();
			toDestination();
			Sound.beepSequence();
			LCD.drawString("(" + currentPosition.getX() + "," + currentPosition.getY() + ")", 0, 0);
		}
	}
	
	/**
	 * Sets the destination using ButtonCounter
	 */
	private void setDestination() {
		LCD.clear();
		bc.count("Set Dest x, y");
		grid.setDestination(bc.getLeftCount(), bc.getRightCount());
		
		// if destination is blocked, set destination to current location
		if (grid.getDestination().isBlocked()) {
			Sound.buzz();
			grid.setDestination(currentPosition.getX(), currentPosition.getY());
		}
		grid.recalc(); // recalculate the shortest path distance to destination
	}
	
	/**
	 * Drives the tracker to the destination set on the grid
	 */
	private void toDestination() {
		grid.recalc();
		while (currentPosition.getDistance() > 0) { // while we're not at destination
			do {
				turnToBestDirection();
			} while (isBlocked()); // try finding the best direction while we're still blocked
			tracker.trackLine();
			currentPosition = currentPosition.neighbor(heading);
		}
	}
	
	/**
	 * Finds any unblocked node by the ultrasonic sensor, and blocks them
	 * @return true if node is blocked, false otherwise
	 */
	private boolean isBlocked() {
		int distance = usensor.getDistance();
		Node inFront = currentPosition.neighbor(heading);
		if (distance > MIN_DIST || inFront == null || inFront.isBlocked()) {
			return false;
		}
		inFront.blocked();
		grid.recalc();
		return true;
	}
	
	/**
	 * Picks the best direction out of the four neighbors
	 * @return the best direction to turn to for the shortest path
	 */
	private int bestDirection() {
		int best = heading;
		int minDist = Grid.BIG;
		Node currentNeighbor;
		
		for (int i = 0; i < 4; i++) {
			currentNeighbor = currentPosition.neighbor(i);
			if (currentNeighbor != null && !currentNeighbor.isBlocked() &&
					currentNeighbor.getDistance() < minDist) {
				best = i;
				minDist = currentNeighbor.getDistance();
			}
		}
		return best;
	}
	
	/**
	 * Turns to the best direction found above, and updates the heading
	 */
	private void turnToBestDirection() {
		int newHeading = bestDirection();
		tracker.turn(correctAngle(newHeading - heading));
		heading = newHeading;
	}
	
	/**
	 * Corrects the angle/heading if it goes out of bound
	 * @param angle - the angle to be corrected
	 * @return - the corrected angle
	 */
	private int correctAngle(int angle) {
		if (angle < -2) {
			angle += 4;
		} else if (angle > 2) {
			angle -= 4;
		}
		return angle;
	}
	
	/**
	 * Main Test code for Milestone 4
	 * @param args - command line arguments
	 */
	public static void main(String[] args) {
		float wheelDiameter = 5.38f;
		float trackWidth = 11.2f;
		DifferentialPilot pilot = new DifferentialPilot(wheelDiameter,
				trackWidth, Motor.A, Motor.C);
		LightSensor left = new LightSensor(SensorPort.S1);
		LightSensor right = new LightSensor(SensorPort.S4);
		UltrasonicSensor ussensor = new UltrasonicSensor(SensorPort.S3);
		Tracker tracker = new Tracker(pilot, left, right);
		ShortestPath robot = new ShortestPath(tracker, ussensor, 6, 8);
		robot.go();
	}
}
