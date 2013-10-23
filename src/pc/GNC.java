package pc;

/**
 * interface for generic GridNavigationController
 * 
 * @author glassey
 */
public interface GNC {
	public void setMessage(String s);

	public void drawRobotPath(int x, int y);

	public void drawObstacle(int x, int y);
	
	public void incomingMessage(int header, int x, int y);
}
