package rs2;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.util.Collections;

import javax.swing.JFrame;

@SuppressWarnings("all")
final class RSFrame extends JFrame
{

	public RSFrame(RSApplet rsapplet, int width, int height, boolean undecorative, boolean resizable) {
		rsApplet = rsapplet;
		setTitle("Rune-Server");
		setUndecorated(undecorative);
		setResizable(resizable);
		setVisible(true);
		Insets insets = this.getInsets();
		setSize(width + insets.left + insets.right, height + insets.top + insets.bottom);//28
		setLocation((screenWidth - width) / 2, (screenHeight - height) / 2);
		setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
		setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);
		requestFocus();
		toFront();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public int getFrameWidth() {
		Insets insets = this.getInsets();
		return getWidth() - (insets.left + insets.right);
	}
	
	public int getFrameHeight() {
		Insets insets = this.getInsets();
		return getHeight() - (insets.top + insets.bottom);
	}
	
	public RSFrame(RSApplet rsapplet, int width, int height) {
		this(rsapplet, width, height, false,false);
	}

	public Graphics getGraphics() {
		Graphics g = super.getGraphics();
		Insets insets = this.getInsets();
		g.translate(insets.left ,insets.top);
		return g;
	}

	public void update(Graphics g)
	{
		rsApplet.update(g);
	}

	public void paint(Graphics g)
	{
		rsApplet.paint(g);
	}

	private final RSApplet rsApplet;
	public Toolkit toolkit = Toolkit.getDefaultToolkit();
	public Dimension screenSize = toolkit.getScreenSize();
	public int screenWidth = (int)screenSize.getWidth();
	public int screenHeight = (int)screenSize.getHeight();
}
