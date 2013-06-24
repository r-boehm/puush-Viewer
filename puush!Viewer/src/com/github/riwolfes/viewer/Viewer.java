package com.github.riwolfes.viewer;

import java.awt.Canvas;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.RenderedImage;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class Viewer extends Canvas{	
	
	private static Random r = new Random();
	private static JFrame frame;
	private static List<String> history;
	private static URL url = null;
	private static int current = 0;
	private static JLabel label;
	private static Image image = null;
	private static Image oimage= null;
		
    public static void main( String[] args ) throws Exception{
    	new Viewer();
    }
    
    private static Image scaleImage(Image cImage) {
    	if(image.getWidth(frame)>=frame.getWidth()||image.getHeight(frame)>=frame.getHeight()){
    		if(image.getWidth(frame)>image.getHeight(frame)){
    			cImage = image.getScaledInstance(frame.getWidth(),(int)(((double)image.getHeight(frame)/(double)image.getWidth(frame))*(double)frame.getWidth()), 1);
    		}else{
    			cImage = image.getScaledInstance((int)(((double)image.getWidth(frame)/(double)image.getHeight(frame))*(double)frame.getHeight()-75),frame.getHeight()-75 , 1);
            }
        }
    	return cImage;
	}

	private static String getLink() {
    	String pref = "http://puu.sh/3";
		String link = "";
		for(int i = 0; i < 4; i++){
			link = link + Character.toString(mkRandomChar());
		}
		return pref+link;
    }

	private static char mkRandomChar() {
		int i = 48;
		i += r.nextInt(74);
		char c = (char) i;
		if(i > 57 && i < 65) c = mkRandomChar();
		if(i > 90 && i < 97) c = mkRandomChar();
		if(i > 122) c = mkRandomChar();
		return c;
	}
	
	private static Image getImage(String link) throws Exception{
		image = null;
		do{
			url = new URL(link);
			URLConnection connection = url.openConnection();
	        connection.connect();
	        if ( connection instanceof HttpURLConnection){
	        	HttpURLConnection httpConnection = (HttpURLConnection) connection;
	        	int code = httpConnection.getResponseCode();
	        	if(code!=404){
	        		frame.setTitle("Getting image..");
	        		image = ImageIO.read(url);
	        	}
	        }
	        link = getLink();
		}while(image==null);
        System.gc();
		return image;
	}
	
	public Viewer() throws Exception{
		frame = new JFrame();
	    frame.setSize(750,750);
	    history = new ArrayList<String>();
	    image = getImage(getLink());
	    oimage = scaleImage(image);
	    label = new JLabel(new ImageIcon(oimage));
	    label.setHorizontalTextPosition(JLabel.CENTER);
	    label.setVerticalTextPosition(JLabel.BOTTOM);
	    label.setText(url.toString());
	    history.add(url.toString());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.add(label);
	    frame.setVisible(true);
	    frame.setTitle("by Riwolfes");
	    frame.setIconImage(Toolkit.getDefaultToolkit().getImage("res//logo.png"));
	    frame.requestFocus();
	    frame.addComponentListener(new ComponentListener() {

			@Override
			public void componentHidden(ComponentEvent arg0) {}

			@Override
			public void componentMoved(ComponentEvent arg0) {}

			@Override
			public void componentResized(ComponentEvent arg0) {
				oimage = scaleImage(image);
				label.setIcon(new ImageIcon(oimage));
			}

			@Override
			public void componentShown(ComponentEvent arg0) {}
	    	
	    });
	    frame.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			    int keyCode = e.getKeyCode();
			    if(keyCode == KeyEvent.VK_W ||keyCode == KeyEvent.VK_UP){
			    	Toolkit toolkit = Toolkit.getDefaultToolkit();
					Clipboard clip = toolkit.getSystemClipboard();
					StringSelection str = new StringSelection(url.toString());
					clip.setContents(str, null);
					frame.setTitle("Copied!");
			    }
			    if(keyCode == KeyEvent.VK_S ||keyCode == KeyEvent.VK_DOWN)
					try {
						saveImage();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
			    if(keyCode == KeyEvent.VK_A ||keyCode == KeyEvent.VK_LEFT){
			    	image = null;
			    	frame.setTitle("Searching image..");
			    	if(current>0){
			    		try {
							image = getImage(history.get(current-1));
						} catch (Exception e1) {
							e1.printStackTrace();
						}
			    		oimage = scaleImage(image);
			    		label.setIcon(new ImageIcon(oimage));
			    		label.setText(url.toString());
			    		current -= 1;
			    		frame.setTitle("puush!Viewer");
			    		frame.repaint();
			    	}
			    }
			    if(keyCode == KeyEvent.VK_D ||keyCode ==  KeyEvent.VK_RIGHT){
			    	image = null;
			    	frame.setTitle("Searching image..");
		    		try {
		    			if(current+1<history.size())image = getImage(history.get(current+1));
		    			else {
		    				image = getImage(getLink());
		    				history.add(url.toString());
		    			}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
		    		oimage = scaleImage(image);
		    		label.setIcon(new ImageIcon(oimage));
		    		label.setText(url.toString());
		    		current += 1;
		    		frame.setTitle("puush!Viewer");
		    		frame.repaint();
			    }
			}

			@Override
			public void keyReleased(KeyEvent e) {}

			@Override
			public void keyTyped(KeyEvent e) {}
	});
	    label.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.isMetaDown())
					try {
						saveImage();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				if(!e.isMetaDown()){
					Toolkit toolkit = Toolkit.getDefaultToolkit();
					Clipboard clip = toolkit.getSystemClipboard();
					StringSelection str = new StringSelection(url.toString());
					clip.setContents(str, null);
					frame.setTitle("Copied!");
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {}

			@Override
			public void mouseExited(MouseEvent arg0) {}

			@Override
			public void mousePressed(MouseEvent arg0) {}

			@Override
			public void mouseReleased(MouseEvent arg0) {}
	    	
	    });
	}

	protected void saveImage() throws Exception {
		String str = url.toString();
		str = str.substring(14);
		File outputfile = new File("puush!Viewer/"+str+".png");
		outputfile.mkdirs();
		ImageIO.write((RenderedImage) image, "png", outputfile);
		frame.setTitle("Saved!");
		
	}
}