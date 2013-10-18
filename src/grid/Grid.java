package grid;
import java.util.ArrayList;

/**
 * Represents the grid corresponding to a warehouse layout of aisles;<br>
 * holds an array of Nodes (intersections); uses ArrayList to calculate shortest
 * path length.
 * 
 * @author Roger Glassey, Khoa Tran
 */
public class Grid {
	/**
	 * Static variables
	 */
	static final int BIG = 99;  // infinity in the network
	
	/**
	 * Instance variables
	 */
	public static int bigX;     // Maximum x coordinate used by constructor, recalc, neighbor
	public static int bigY;     // Maximum y coordinate used by constructor, recalc, neighbor
	public Node nodes[][];      // holds the Nodes, indexed by grid coordinates
	private Node destination;   // set by setDestination(), used by recalc()
	private ArrayList<Node> list = new ArrayList<Node>();

	/**
	 * Builds the nodes array
	 * @param lengthX, lengthy are the size of the array
	 */
	public Grid(int lengthX, int lengthY) {
		bigX = lengthX;
		bigY = lengthY;
		nodes = new Node[bigX][bigY];
		
		// Fills the nodes array
		for (int x = 0; x < bigX; x++) {
			for (int y = 0; y < bigY; y++)
				nodes[x][y] = new Node(x, y);
		}
		
		// Sets the neighbors
		for (int x = 0; x < bigX; x++) {
			for (int y = 0; y < bigY; y++) {
				Node n = nodes[x][y];
				for (int i = 0; i < 4; i++) {
					n.neighbor[i] = neighbor(n, i);
				}
			}
		}
	}
	
	/**
	 * Returns the neighbor of aNode; the direction is a number (0,1,2,3) <BR>
	 * multiple of 90 degrees from the X axis. direction 0 is +X, direction 1 is
	 * +Y etc. If out of bounds, the neighbor is null
	 * @param node - the node
	 * @param direction of the neighbor
	 */
	public Node neighbor(Node aNode, int direction) {
		Node node = aNode;
		Node neighbor = null;
		
		// Makes sure direction is a number in [0,3]
		direction %= 4;
		if (direction < 0) {
			direction += 4;
		}
		int x = node.getX();
		int y = node.getY();
		
		// Calculates the neighbor of node in direction counterclockwise
		if (direction == 0 && x < bigX-1) {         // right
			neighbor = nodes[x + 1][y];
		} else if (direction == 2 && x > 0) {       // left
			neighbor = nodes[x - 1][y]; 
		} else if (direction == 1 && y < bigY-1) {  // up
			neighbor = nodes[x][y + 1];
	    } else if (direction == 3 && y > 0) {       // down
			neighbor = nodes[x][y - 1];
	    }
		return neighbor;
	}

	/**
	 * Calculates shortest distance from all nodes to destination;<br>
	 * uses list, node. Calls neighbor(), node.newDistance(),list.add((),
	 * list.remove(0)
	 */
	public void recalc() {
		// Reset all nodes distance
		for (int x = 0; x < bigX; x++) {
			for (int y = 0; y < bigY; y++) {
				nodes[x][y].reset();
			}
		}
		
		// Sets distance of destination to 0, and add the destination node to the fringe
		destination.newDistance(0);
		list.add(destination);
		
		// For every node, iterate over neighbors and calculate the new distance
		while (!list.isEmpty()) {
			Node currentNode = list.remove(0);
			int distance = 1 + currentNode.getDistance();
			for (int direction = 0; direction < 4; direction++) { 
				Node n = currentNode.neighbor[direction];
				if (n != null && n.newDistance(distance)) {
					list.add(n);
				}
			}
		}
	}

	/**
	 * Sets where the robot is trying to go
	 */
	public void setDestination(int x, int y) {
		if (x < bigX && x >= 0 && y < bigY && y >=0) {
			destination = nodes[x][y];
			recalc();
		}
	}

	/**
	 * @return the destination node
	 */
	public Node getDestination() {
		return destination;
	}
}
