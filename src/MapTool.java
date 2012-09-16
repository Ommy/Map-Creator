import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.border.Border;
import javax.swing.event.*;
import java.util.ArrayList;
import javax.imageio.*;
import java.io.*;


public class MapTool extends JFrame
{
	public static void main (String[] args)
	{
		//Set up the window
		int x = 1000;
		int y = 680;
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		try {
			x = Integer.parseInt(bf.readLine())*32;
			y = Integer.parseInt(bf.readLine())*32;

		} catch (NumberFormatException e) {

		} catch (IOException e) {

		}
		JFrame window = new JFrame ("Map Tool");
		window.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		window.setContentPane (new ToolGUI (x,y));
		window.pack ();
		window.setSize(x,y);
		window.setVisible (true);
	}
}
class Load
{
	String textName;
	int lengthRow,lengthCol;
	public String[][]qq;
	public Load()
	{

	}
	public void loadToMap(){
		FileReader fReader = null;
		try{
			ArrayList<String> loadedLines = new ArrayList<String>();
			String tempString,fHold;
			int count = 0;
			int index;
			fReader = new FileReader("out.txt");
			BufferedReader in = new BufferedReader(fReader);
			int rows = Integer.parseInt(in.readLine());
			int cols = Integer.parseInt(in.readLine());
			lengthRow = rows;
			lengthCol = cols;
			qq = new String[rows][cols];
			while((tempString = in.readLine()) != null){
				loadedLines.add(tempString);
			}
			for (int  i = 0;i<lengthCol;i++){
				for(int j = 0; j<lengthRow; j++){
					fHold = loadedLines.get(i);
					String replaced = fHold.substring(fHold.indexOf('|')+1);
					if (fHold.indexOf('|') != -1)
						fHold = fHold.substring(0,fHold.indexOf('|'));
					loadedLines.set(i, replaced);
					qq[i][j] = fHold;
				}
			}
		}catch (IOException e){}
	}
	public String[][] getQQ(){
		return qq;
	}
}
class Save
{
	public String[][] qq;
	int x,y;
	public Save(String[][] g,int a, int b)
	{
		qq = g;
		x = a/32;
		y = b/32;
	}
	public Save()
	{

	}
	public void updateQQ(String[][] gg){
		this.qq = gg;
	}
	public void saveToFile()
	{
		FileWriter fstream = null;
		try {
			fstream = new FileWriter("out.txt");
			BufferedWriter out = new BufferedWriter(fstream);
			out.append(y+"");
			out.newLine();
			out.append(x+"");
			out.newLine();
			for (int i = 0;i<y;i++)
			{
				for (int j = 0;j<x;j++)
				{
					if (qq[i][j].equals("x"))
						continue;
					out.append(qq[i][j]);
					out.append("|");
				}
				out.newLine();
			}
			out.close();
		}catch (Exception e1){e1.printStackTrace();}
	}
}
class ToolGUI extends JPanel
{
	public Save sv;
	public int count = 0;
	public boolean inDrag = true;
	public MapGrid mg;
	public JPanel top = new JPanel();//panel for buttons 
	public JPanel buttonPanel = new JPanel();
	public JPanel test = new JPanel();
	public JButton setSize = new JButton ("Set Size");
	public JButton insert = new JButton ("Insert");
	public JButton save = new JButton ("Save Map");
	public JButton load = new JButton ("Load Map");
	public JCheckBox enableGrid = new JCheckBox();
	public JLabel text = new JLabel("Enable Grid?");
	public ImageIcon icon = new ImageIcon("images/im2.png");
	public JLabel imgLabel = new JLabel();
	public Image img = icon.getImage();
	public JComboBox imgList;
	public int sizeX =0,sizeY=0;
	public String[] list;
	public ImageIcon[] listOfImages;
	BorderLayout bord = new BorderLayout();
	FlowLayout flow = new FlowLayout();//layout for buttons
	GridBagLayout grid = new GridBagLayout();//main window layout
	String[][] qq;

	//use drop down for selecting tiles
	//have a button for accepting the selection
	//have a grid layout for Images to be drag-able and drop-able
	//have an option in the beginning for selecting map size (?)
	public ToolGUI (int xSize,int ySize)
	{
		qq = new String[ySize/32][xSize/32];
		sizeX = xSize/32;
		sizeY = ySize/32;
		mg = new MapGrid(xSize,ySize);
		///System.out.println(xSize +"::"+ySize);
		////add buttons and stuff to the "top" layout
		list = loadImageNames();
		listOfImages = new ImageIcon[list.length];
		populateListOfImages();
		imgList = new JComboBox(listOfImages);
		imgLabel.setIcon(icon);
		top.setLayout(bord);

		//Adding to the JPanels
		buttonPanel.setLayout(flow);
		buttonPanel.add(load);
		buttonPanel.add(imgList);
		buttonPanel.add(save);
		enableGrid.setEnabled(true);
		buttonPanel.add(enableGrid);
		buttonPanel.add(text);
		test.add(mg);
		MyListener mL = new MyListener();
		test.addMouseListener(mL);
		test.addMouseMotionListener(mL);
		/////////////////////////////////////

		//Action Listener calls
		save.addActionListener(new SaveListener());//used to save map
		enableGrid.addActionListener(new EnableListener());//used to enable a grid overlay
		load.addActionListener(new LoadListener());//loads map
		imgList.addActionListener(new ListListener());//list of image blocks
		///////////////////////////////////

		//Layout for window
		this.setLayout(new BorderLayout());
		this.add(buttonPanel, BorderLayout.NORTH);
		this.add(test      , BorderLayout.CENTER);

		for (int i = 0; i < qq.length; i++)
		{
			for (int j =0; j<qq[i].length;j++)
			{
				qq[i][j] = "x";
			}    
		}
		sv = new Save(qq,xSize,ySize);
		mg.q(qq);

	}
	public class MyListener extends MouseInputAdapter{
		@Override
		public void mousePressed(MouseEvent e){
			inDrag = true;
		}
		@Override
		public void mouseDragged (MouseEvent e){
			Point p = e.getPoint();
			int x = p.x / 32;
			int y = p.y / 32;
			String imgName = list[imgList.getSelectedIndex()];//what image was selected
			qq[y][x] = imgName.substring(imgName.lastIndexOf('\\')+1);
			mg.drawIMG(imgName,x,y,count);
			mg.loadedFromFile();

		}

	}
	class PanelListener implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent arg0) {
			count++;
			int x = arg0.getX() / 32;
			int y = arg0.getY() / 32;
			String imgName = list[imgList.getSelectedIndex()];//what image was selected
			qq[y][x] = imgName.substring(imgName.lastIndexOf('\\')+1);
			mg.drawIMG(imgName,x,y,count);

		}

		@Override
		public void mouseEntered(MouseEvent arg0) {

		}
		public void mouseDragged (MouseEvent e){
			Point p = e.getPoint();
			int x = p.x;
			int y = p.y;
			if (inDrag){
				String imgName = list[imgList.getSelectedIndex()];//what image was selected
				qq[y][x] = imgName.substring(imgName.lastIndexOf('\\')+1);
				mg.drawIMG(imgName,x,y,count);
			}
		}
		@Override
		public void mouseExited(MouseEvent arg0) {
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			inDrag = true;
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			inDrag = false;

		}

	}
	public void populateListOfImages(){
		for(int i = 0; i<listOfImages.length;i++){
			listOfImages[i] = new ImageIcon(loadImage(list[i]));
		}
	}
	public Image loadImage(String name)
	{
		Image img = null;
		try
		{
			img = ImageIO.read(new File (name));
		}
		catch (IOException e)
		{
			System.out.println("NOPE1");
		}
		return img;
	}
	class ListListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0) {
			int index = imgList.getSelectedIndex();
			icon = new ImageIcon(list[index]);
			imgLabel.setIcon(icon);
		}

	}
	class LoadListener implements ActionListener
	{
		public void actionPerformed (ActionEvent e)
		{
			Load load = new Load();
			load.loadToMap();
			mg.q(load.getQQ());
			mg.loadedFromFile();
			sv.updateQQ(load.getQQ());
			qq = load.getQQ();

		}
	}
	class EnableListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			mg.setGrid(enableGrid.isSelected());
		}
	}
	public String[] loadImageNames()
	{
		String userHome=System.getProperty("user.home");
		File folder = new File(userHome+"/workspace/MapTool/images");//this just loads all the file names under images folder, used for drop-down menu
		File[] fileNames = folder.listFiles();
		String[] s = new String[fileNames.length];
		for (int i = 0;i<fileNames.length;i++)
		{
			if (fileNames[i].length() != 0)
				s[i] = (fileNames[i].toString());
		}
		return s;
	}
	class InsertListener implements ActionListener
	{
		public void actionPerformed (ActionEvent e)
		{
			count++;
			JFrame frame = new JFrame();
			String imgName = list[imgList.getSelectedIndex()];//what image was selected
			String x = (String)JOptionPane.showInputDialog(frame,"X Coord?","X",//promt for X and Y coordinates for the image about to be inserted
					JOptionPane.PLAIN_MESSAGE,null,null,"");
			String y = (String)JOptionPane.showInputDialog(frame,"Y Coord?","Y",
					JOptionPane.PLAIN_MESSAGE,null,null,"");


		}
	}
	class SaveListener implements ActionListener
	{
		public void actionPerformed (ActionEvent e)
		{
			sv.updateQQ(qq);
			sv.saveToFile();
			BufferedImage bI = null;
			try {
				bI = mg.buffIMG(test);
			} catch (AWTException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			mg.writeIMG(bI, "test");
		}
	}


}
class MapGrid extends JPanel
{
	public int Xcoord,Ycoord;
	public String tempName;
	String[][] qq;
	int xSize,ySize;
	boolean isEnabled = false;
	public MapGrid(int a, int b)
	{
		setPreferredSize(new Dimension(800,800));
		setBorder(BorderFactory.createLineBorder(Color.black));
		this.setBackground(Color.WHITE);
		xSize = a;
		ySize = b;
	}
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		draw(g,tempName,Xcoord*32,Ycoord*32);

	}
	public void q(String[][] a){
		qq = a;
	}
	public void setGrid(boolean en)
	{
		isEnabled = en;
		repaint();
	}
	public void drawIMG(String name,int x,int y,int c)
	{
		if (c >= 1){
			tempName = name;//call this method first, which will set the name of the img so we can load it
			Xcoord = x;
			Ycoord = y;
			repaint();}
	}
	public void loadedFromFile(){
		repaint();
	}
	public void draw(Graphics g,String name,int x, int y)
	{
		for (int i = 0; i <qq.length; i++)
		{
			for (int j=0;j<qq[i].length;j++)
			{
				if (!qq[j][i].equals("x"))
				{
					Image img = loadImage(qq[j][i]);
					g.drawImage(img, i*32, j*32, null);
				}
			}
		}
		if (isEnabled)
		{
			g.setColor(Color.BLACK);
			for (int i = 0; i<xSize;i+=32)
			{
				g.drawLine(0,i,ySize,i);//horizontal lines
			}
			for (int j = 0; j<ySize;j+=32)
			{
				g.drawLine(j,0,j,xSize);//vertical lines
			}
		}
		else
		{
			g.setColor(Color.WHITE);
			for (int i = 0; i<ySize;i+=32)
				g.drawLine(0,i,ySize,i);//horizontal lines
			for (int j = 0; j<xSize; j+= 32)
				g.drawLine(j,0,j,xSize);//vertical lines
		}

	}
	public BufferedImage buffIMG(Component component) throws AWTException
	{
		Point p = new Point(0, 0);
		SwingUtilities.convertPointToScreen(p, component);
		Rectangle region = component.getBounds();
		region.x = p.x;
		region.y = p.y;
		return createImage(region);


	}
	public static BufferedImage createImage(Rectangle region)
			throws AWTException
			{
		BufferedImage image = new Robot().createScreenCapture( region );
		return image;
			}
	public void writeIMG(BufferedImage img, String name)
	{
		try {
			ImageIO.write(img, "png", new File(name+".png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public Image loadImage(String name)
	{
		Image img = null;
		try
		{
			img = ImageIO.read(new File (name));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return img;
	}
}



