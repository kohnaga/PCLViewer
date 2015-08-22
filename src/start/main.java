package start;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.*;

public class main implements ActionListener {

	static String name;
	static String command;
	static JFrame frame;
	static JLabel ilabel;
	static ImageIcon iicon;
	static int page;
	static int max_page;
	static boolean IsViewed;
	static double scaling;

	/**
	 * Create the GUI and show it. 
	 */
	private static  void createAndShowGUI() {
		page =1;
		scaling = 1.0;
		IsViewed = false;
		// Create and set up the window.
		frame = new JFrame("Pcl Viewer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(100, 100, 600, 800);
		frame.setBackground(Color.gray);

		// Create and set up menu bar
		JMenuBar menubar = new JMenuBar();
		menubar.setBackground(Color.BLUE);
		JMenu menu1 = new JMenu("File");
		menubar.add(menu1);
		JMenuItem menuitem1 = new JMenuItem("Open PCL");
		menuitem1.addActionListener(new ActionListener()
		{
		    public void actionPerformed(ActionEvent e) {
				JFileChooser filechooser = new JFileChooser();
				int selected = filechooser.showOpenDialog(filechooser);
				if (selected == JFileChooser.APPROVE_OPTION) {
					File file = filechooser.getSelectedFile();
					if (IsPclFile(file)) {
						try {
							Runtime rt = Runtime.getRuntime();
							command = "./pcl6 -dSAFER -dBATCH -dNOPAUSE -sDEVICE=png16m -r150 -dGraphicsalphaBits=4 -sOutputFile="
									+ name + "-%d.png " + file;
							Process p = rt.exec(command);
							try {
								p.waitFor();
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							max_page = get_max_page(name);
							page = 1;
							scaling = 1.0;
							iicon = new ImageIcon("./" + name + "-" + page + ".png");
							ilabel.setIcon(iicon);
							IsViewed = true;
							frame.setTitle(name + "-" + page);
							frame.setVisible(true);
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					} else {
						System.out.println("Cannot find PCL file.");
					}
				}
			}

			private int get_max_page(String filename) {
				int idx = 1;
				String cur_path = System.getProperty("user.dir");
				while(true){
					File fptr = new File(cur_path + "/" + filename + "-" + (idx + 1) + ".png");
					boolean flag = fptr.exists();
					if(flag){
						idx++;
					}else{
						break;
					}					
				};
				return idx;
			}
			
		});
		menu1.add(menuitem1);
		
		JMenu menu2 = new JMenu("Page");
		menubar.add(menu2);
		JMenuItem menuitem2 = new JMenuItem("NEXT");
		menuitem2.addActionListener(new ActionListener()
		{
		    public void actionPerformed(ActionEvent e) {
		    	if((page < max_page) && IsViewed){
		    		page++;
		    		iicon = new ImageIcon(name + "-" + page + ".png");
		    		Image smallImg = iicon.getImage().getScaledInstance((int) (iicon.getIconWidth() * scaling), -1, Image.SCALE_SMOOTH);
		    		iicon = new ImageIcon(smallImg);
					ilabel.setIcon(iicon);
					frame.setTitle(name + "-" + page);
					frame.setVisible(true);
		    	}
			}
		});
		menu2.add(menuitem2);
		
		JMenuItem menuitem3 = new JMenuItem("BACK");
		menuitem3.addActionListener(new ActionListener()
		{
		    public void actionPerformed(ActionEvent e) {
		    	if((page > 1) && IsViewed){
		    		page--;
		    		iicon = new ImageIcon(name + "-" + page + ".png");
		    		Image smallImg = iicon.getImage().getScaledInstance((int) (iicon.getIconWidth() * scaling), -1, Image.SCALE_SMOOTH);
		    		iicon = new ImageIcon(smallImg);
					ilabel.setIcon(iicon);
					frame.setTitle(name + "-" + page );
					frame.setVisible(true);
		    	}
			}
		});
		menu2.add(menuitem3);
		
		JMenu menu3 = new JMenu("SCALING");
		menubar.add(menu3);
		JMenuItem menuitem4 = new JMenuItem("BIGGER");
		menuitem4.addActionListener(new ActionListener()
		{
		    public void actionPerformed(ActionEvent e) {
		    	if(IsViewed){
		    		scaling = scaling * 1.5; 
		    		iicon = new ImageIcon(name + "-" + page + ".png"); 
		    		Image smallImg = iicon.getImage().getScaledInstance((int) (iicon.getIconWidth() * scaling), -1, Image.SCALE_SMOOTH);
		    		iicon = new ImageIcon(smallImg);
		    		ilabel.setIcon(iicon);
					frame.setVisible(true);
		    	}
			}
		});
		menu3.add(menuitem4);
		
		JMenuItem menuitem5 = new JMenuItem("SMALLER");
		menuitem5.addActionListener(new ActionListener()
		{
		    public void actionPerformed(ActionEvent e) {
		    	if(IsViewed){
		    		scaling = scaling / 1.5; 
		    		iicon = new ImageIcon(name + "-" + page + ".png"); 
		    		Image smallImg = iicon.getImage().getScaledInstance((int) (iicon.getIconWidth() * scaling), -1, Image.SCALE_SMOOTH);
		    		iicon = new ImageIcon(smallImg);
		    		ilabel.setIcon(iicon);
					frame.setVisible(true);
		    	}
			}
		});
		menu3.add(menuitem5);
		
		frame.setJMenuBar(menubar);

		// Add the Image label.
		ilabel = new JLabel(iicon);
		JPanel p = new JPanel();
		JScrollPane scrollpane = new JScrollPane(ilabel);
		frame.getContentPane().add(scrollpane, BorderLayout.CENTER);

		// Display the window.
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}


	public static boolean IsPclFile(File file) {
		if (file == null) {
			return false;
		}
		if (file.exists()) {
			if (file.isFile() && file.canRead() && isPCL(file.getName())) {
				return true;
			}
		}
		return false;
	}

	public static boolean isPCL(String filename) {
		int point = filename.lastIndexOf(".");
		String tmp;
		if (point != -1) {
			name = filename.substring(0, point);
			tmp = filename.substring(point + 1);
			return tmp.equals("prn");
		}
		return false;
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub	
	}
}