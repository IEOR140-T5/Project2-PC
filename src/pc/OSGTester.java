package pc;
import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;


public class OSGTester extends JFrame
{

	private JPanel contentPane;
	private JTextField xField;
	private JTextField yField;
	OffScreenGrid oSGrid = new OffScreenGrid();
	static OSGTester frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					frame = new OSGTester();
					frame.setVisible(true);
					frame.runTest();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	  
	}

	/**
	 * Create the frame.
	 */
	public OSGTester()
	{
		setTitle("Off Screen Grid Tester");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 391, 542);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		
		xField = new JTextField();
		panel.add(xField);
		xField.setColumns(5);
		
		yField = new JTextField();
		panel.add(yField);
		yField.setColumns(5);
		
		contentPane.add(oSGrid, BorderLayout.CENTER);
		oSGrid.textX = xField;
		oSGrid.textY = yField;
		
		
	}
  private void runTest()
  {
	 System.out.println("run test ");
	oSGrid.makeImage();
	oSGrid.drawRobotPath(0,1);
	oSGrid.drawRobotPath(1,1);
	oSGrid.drawObstacle(1,2);
	
  }
}
