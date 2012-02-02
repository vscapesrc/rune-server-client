package rs2;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import rs2.graphics.RSImageProducer;
import rs2.graphics.RSInterface;

@SuppressWarnings("serial")
public class RSApplet extends Applet
	implements Runnable, MouseListener, MouseMotionListener, KeyListener, FocusListener, WindowListener, MouseWheelListener
{

	public void recreateClientFrame(boolean framed, int width, int height, boolean resizable) {
		boolean createdByApplet = (isApplet && width == 765);
		myWidth = width;
		myHeight = height;
		if(mainFrame != null) {
			mainFrame.dispose();
		}
		if (!createdByApplet){
			mainFrame = new RSFrame(this, width, height, framed, resizable);
			mainFrame.addWindowListener(this);
		}
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		graphics = (createdByApplet ? this : mainFrame).getGraphics();
		getGameComponent().addMouseWheelListener(this);
		getGameComponent().addMouseListener(this);
		getGameComponent().addMouseMotionListener(this);
		getGameComponent().addKeyListener(this);
		getGameComponent().addFocusListener(this);
	}

	final void createClientFrame(int w, int h) {
		isApplet = false;
		myWidth = w;
		myHeight = h;
		mainFrame = new RSFrame(this, myWidth, myHeight);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		graphics = getGameComponent().getGraphics();
		fullGameScreen = new RSImageProducer(myWidth, myHeight, getGameComponent());
		startRunnable(this, 1);
	}

	final void initClientFrame(int w, int h) {
		isApplet = true;
		myWidth = w;
		myHeight = h;
		graphics = getGameComponent().getGraphics();
		fullGameScreen = new RSImageProducer(myWidth, myHeight, getGameComponent());
		startRunnable(this, 1);
	}

	public void run()
	{
		getGameComponent().addMouseWheelListener(this);
		getGameComponent().addMouseListener(this);
		getGameComponent().addMouseMotionListener(this);
		getGameComponent().addKeyListener(this);
		getGameComponent().addFocusListener(this);
		if(mainFrame != null)
			mainFrame.addWindowListener(this);
		displayProgress("Loading...", 0);
		startUp();
		int i = 0;
		int j = 256;
		int k = 1;
		int l = 0;
		int i1 = 0;
		for(int j1 = 0; j1 < 10; j1++)
			aLongArray7[j1] = System.currentTimeMillis();

		do
		{
			if(anInt4 < 0)
				break;
			if(anInt4 > 0)
			{
				anInt4--;
				if(anInt4 == 0)
				{
					exit();
					return;
				}
			}
			int k1 = j;
			int i2 = k;
			j = 300;
			k = 1;
			long l2 = System.currentTimeMillis();
			if(aLongArray7[i] == 0L)
			{
				j = k1;
				k = i2;
			} else if(l2 > aLongArray7[i])
				j = (int)((long)(2560 * delayTime) / (l2 - aLongArray7[i]));
			if(j < 25)
				j = 25;
			if(j > 256)
			{
				j = 256;
				k = (int)((long)delayTime - (l2 - aLongArray7[i]) / 10L);
			}
			if(k > delayTime)
				k = delayTime;
			aLongArray7[i] = l2;
			i = (i + 1) % 10;
			if(k > 1)
			{
				for(int j2 = 0; j2 < 10; j2++)
					if(aLongArray7[j2] != 0L)
						aLongArray7[j2] += k;

			}
			if(k < minDelay)
				k = minDelay;
			try
			{
				Thread.sleep(k);
			}
			catch(InterruptedException interruptedexception)
			{
				i1++;
			}
			for(; l < 256; l += j)
			{
				clickMode3 = clickMode1;
				saveClickX = clickX;
				saveClickY = clickY;
				aLong29 = clickTime;
				clickMode1 = 0;
				processGameLoop();
				readIndex = writeIndex;
			}

			l &= 0xff;
			if(delayTime > 0)
				fps = (1000 * j) / (delayTime * 256);
			processDrawing();
			if(shouldDebug)
			{
				System.out.println("ntime:" + l2);
				for(int k2 = 0; k2 < 10; k2++)
				{
					int i3 = ((i - k2 - 1) + 20) % 10;
					System.out.println("otim" + i3 + ":" + aLongArray7[i3]);
				}

				System.out.println("fps:" + fps + " ratio:" + j + " count:" + l);
				System.out.println("del:" + k + " deltime:" + delayTime + " mindel:" + minDelay);
				System.out.println("intex:" + i1 + " opos:" + i);
				shouldDebug = false;
				i1 = 0;
			}
		} while(true);
		if(anInt4 == -1)
			exit();
	}

	private void exit()
	{
		anInt4 = -2;
		cleanUpForQuit();
		if(mainFrame != null)
		{
			try
			{
				Thread.sleep(1000L);
			}
			catch(Exception exception) { }
			try
			{
				System.exit(0);
			}
			catch(Throwable throwable) { }
		}
	}

	final void method4(int i)
	{
		delayTime = 1000 / i;
	}

	public final void start()
	{
		if(anInt4 >= 0)
			anInt4 = 0;
	}

	public final void stop()
	{
		if(anInt4 >= 0)
			anInt4 = 4000 / delayTime;
	}

	public final void destroy()
	{
		anInt4 = -1;
		try
		{
			Thread.sleep(5000L);
		}
		catch(Exception exception) { }
		if(anInt4 == -1)
			exit();
	}

	public final void update(Graphics g)
	{
		if(graphics == null)
			graphics = g;
		shouldClearScreen = true;
		raiseWelcomeScreen();
	}

	public final void paint(Graphics g)
	{
		if(graphics == null)
			graphics = g;
		shouldClearScreen = true;
		raiseWelcomeScreen();
	}
	
	public void mouseWheelMoved(MouseWheelEvent event) {
		int rotation = event.getWheelRotation();
		handleInterfaceScrolling(event);
		if(mouseX > 0 && mouseX < 512 && mouseY > 503 - 165 && mouseY < 503 - 25) {
			int scrollPos = Client.anInt1089;
			scrollPos -= rotation * 30;		
			if(scrollPos < 0) {
				scrollPos = 0;
			}
			if(scrollPos > Client.anInt1211 - 110) {
				scrollPos = Client.anInt1211 - 110;
			}
			if(Client.anInt1089 != scrollPos) {
				Client.anInt1089 = scrollPos;
				Client.inputTaken = true;
			}
		}
	}

	public void handleInterfaceScrolling(MouseWheelEvent event) {
		int rotation = event.getWheelRotation();
		int positionX = 0;
		int positionY = 0;
		int width = 0;
		int height = 0;
		int offsetX = 0;
		int offsetY = 0;
		int childID = 0;
		/* Tab interface scrolling */
		int tabInterfaceID = Client.tabInterfaceIDs[Client.tabID];
		if (tabInterfaceID != -1) {
			RSInterface tab = RSInterface.cache[tabInterfaceID];
			offsetX = 765 - 218;
			offsetY = 503 - 298;
			for (int index = 0; index < tab.children.length; index++) {
				if (RSInterface.cache[tab.children[index]].scrollMax > 0) {
					childID = index;
					positionX = tab.childX[index];
					positionY = tab.childY[index];
					width = RSInterface.cache[tab.children[index]].width;
					height = RSInterface.cache[tab.children[index]].height;
					break;
				}
			}
			if (mouseX > offsetX + positionX && mouseY > offsetY + positionY && mouseX < offsetX + positionX + width && mouseY < offsetY + positionY + height) {
				if (RSInterface.cache[tab.children[childID]].scrollPosition > 0) {
					RSInterface.cache[tab.children[childID]].scrollPosition += rotation * 30;
					return;
				} else {
					if (rotation > 0) {
						RSInterface.cache[tab.children[childID]].scrollPosition += rotation * 30;
						return;
					}
				}
			}
		}
		/* Main interface scrolling */
		if (Client.openInterfaceID != -1) {
			RSInterface rsi = RSInterface.cache[Client.openInterfaceID];
			offsetX = 4;
			offsetY = 4;
			for (int index = 0; index < rsi.children.length; index++) {
				if (RSInterface.cache[rsi.children[index]].scrollMax > 0) {
					childID = index;
					positionX = rsi.childX[index];
					positionY = rsi.childY[index];
					width = RSInterface.cache[rsi.children[index]].width;
					height = RSInterface.cache[rsi.children[index]].height;
					break;
				}
			}
			if (mouseX > offsetX + positionX && mouseY > offsetY + positionY && mouseX < offsetX + positionX + width && mouseY < offsetY + positionY + height) {
				if (RSInterface.cache[rsi.children[childID]].scrollPosition > 0) {
					RSInterface.cache[rsi.children[childID]].scrollPosition += rotation * 30;
					return;
				} else {
					if (rotation > 0) {
						RSInterface.cache[rsi.children[childID]].scrollPosition += rotation * 30;
						return;
					}
				}
			}
		}
	}
	
	public final void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		if(mainFrame != null) {
			Insets insets = mainFrame.getInsets();
			x -= insets.left;//4
			y -= insets.top;//22
		}
		idleTime = 0;
		clickX = x;
		clickY = y;
		clickTime = System.currentTimeMillis();
		if(e.isMetaDown()) {
			clickType = RIGHT;
			clickMode1 = 2;
			clickMode2 = 2;
		} else {
			clickType = LEFT;
			clickMode1 = 1;
			clickMode2 = 1;
		}
	}

	public int clickType;
	public final int LEFT = 0;
	public final int RIGHT = 1;
	public final int DRAG = 2;
	public final int RELEASED = 3;
	public final int MOVE = 4;
	public int releasedX;
	public int releasedY;

	public int getClickType() {
		return clickType;
	}

	public final void mouseReleased(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		if(mainFrame != null) {
			Insets insets = mainFrame.getInsets();
			x -= insets.left;//4
			y -= insets.top;//22
		}
		releasedX = x;
		releasedY = y;
		idleTime = 0;
		clickMode2 = 0;
		clickType = RELEASED;
	}

	public final void mouseClicked(MouseEvent mouseevent) {
	}

	public final void mouseEntered(MouseEvent mouseevent)
	{
	}

	public final void mouseExited(MouseEvent mouseevent) {
		idleTime = 0;
		mouseX = -1;
		mouseY = -1;
	}

	public final void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		if(mainFrame != null) {
			Insets insets = mainFrame.getInsets();
			x -= insets.left;//4
			y -= insets.top;//22
		}
		idleTime = 0;
		mouseX = x;
		mouseY = y;
		clickType = DRAG;
	}

	public final void mouseMoved(MouseEvent mouseevent) {
		int i = mouseevent.getX();
		int j = mouseevent.getY();
		if(mainFrame != null) {
			Insets insets = mainFrame.getInsets();
			i -= insets.left;//4
			j -= insets.top;//22
		}
		idleTime = 0;
		mouseX = i;
		mouseY = j;
		clickType = MOVE;
	}

	public final void keyPressed(KeyEvent keyevent) {
		idleTime = 0;
		int i = keyevent.getKeyCode();
		int keyPressed = keyevent.getKeyChar();
		if(keyPressed < 30)
			keyPressed = 0;
		if(i == 37)
			keyPressed = 1;
		if(i == 39)
			keyPressed = 2;
		if(i == 38)
			keyPressed = 3;
		if(i == 40)
			keyPressed = 4;
		if(i == 17)
			keyPressed = 5;
		if(i == 8)
			keyPressed = 8;
		if(i == 127)
			keyPressed = 8;
		if(i == 9)
			keyPressed = 9;
		if(i == 10)
			keyPressed = 10;
		if(i >= 112 && i <= 123)
			keyPressed = (1008 + i) - 112;
		if(i == 36)
			keyPressed = 1000;
		if(i == 35)
			keyPressed = 1001;
		if(i == 33)
			keyPressed = 1002;
		if(i == 34)
			keyPressed = 1003;
		if(keyPressed > 0 && keyPressed < 128)
			keyArray[keyPressed] = 1;
		if(keyPressed > 4)
		{
			charQueue[writeIndex] = keyPressed;
			writeIndex = writeIndex + 1 & 0x7f;
		}
	}

	public final void keyReleased(KeyEvent keyevent)
	{
		idleTime = 0;
		int i = keyevent.getKeyCode();
		char c = keyevent.getKeyChar();
		if(c < '\036')
			c = '\0';
		if(i == 37)
			c = '\001';
		if(i == 39)
			c = '\002';
		if(i == 38)
			c = '\003';
		if(i == 40)
			c = '\004';
		if(i == 17)
			c = '\005';
		if(i == 8)
			c = '\b';
		if(i == 127)
			c = '\b';
		if(i == 9)
			c = '\t';
		if(i == 10)
			c = '\n';
		if(c > 0 && c < '\200')
			keyArray[c] = 0;
	}

	public final void keyTyped(KeyEvent keyevent)
	{
	}

	final int readCharacter() {
		int k = -1;
		if(writeIndex != readIndex) {
			k = charQueue[readIndex];
			readIndex = readIndex + 1 & 0x7f;
		}
		return k;
	}

	public final void focusGained(FocusEvent focusevent)
	{
		awtFocus = true;
		shouldClearScreen = true;
		raiseWelcomeScreen();
	}

	public final void focusLost(FocusEvent focusevent)
	{
		awtFocus = false;
		for(int i = 0; i < 128; i++)
			keyArray[i] = 0;

	}

	public final void windowActivated(WindowEvent windowevent)
	{
	}

	public final void windowClosed(WindowEvent windowevent)
	{
	}

	public final void windowClosing(WindowEvent windowevent)
	{
		destroy();
	}

	public final void windowDeactivated(WindowEvent windowevent)
	{
	}

	public final void windowDeiconified(WindowEvent windowevent)
	{
	}

	public final void windowIconified(WindowEvent windowevent)
	{
	}

	public final void windowOpened(WindowEvent windowevent)
	{
	}

	void startUp()
	{
	}

	void processGameLoop()
	{
	}

	void cleanUpForQuit()
	{
	}

	void processDrawing()
	{
	}

	void raiseWelcomeScreen()
	{
	}

	Component getGameComponent()
	{
		if(mainFrame != null)
			return mainFrame;
		else
			return this;
	}

	public void startRunnable(Runnable runnable, int i)
	{
		Thread thread = new Thread(runnable);
		thread.start();
		thread.setPriority(i);
	}

	void displayProgress(String loadingText, int percentage) {
		boolean createdByApplet = (isApplet && myWidth == 765);
		while(graphics == null) {
			graphics = (createdByApplet ? this : mainFrame).getGraphics();
			try {
				getGameComponent().repaint();
			} catch(Exception exception) { }
			try {
				Thread.sleep(1000L);
			} catch(Exception exception1) { }
		}
		Font font = new Font("Helvetica", 1, 13);
		FontMetrics fontmetrics = getGameComponent().getFontMetrics(font);
		Font font1 = new Font("Helvetica", 0, 13);
		FontMetrics fontmetrics1 = getGameComponent().getFontMetrics(font1);
		if(shouldClearScreen) {
			graphics.setColor(Color.black);
			graphics.fillRect(0, 0, myWidth, myHeight);
			shouldClearScreen = false;
		}
		Color color = new Color(140, 17, 17);
		int y = myHeight / 2 - 18;
		graphics.setColor(color);
		graphics.drawRect(myWidth / 2 - 152, y, 304, 34);
		graphics.fillRect(myWidth / 2 - 150, y + 2, percentage * 3, 30);
		graphics.setColor(Color.black);
		graphics.fillRect((myWidth / 2 - 150) + percentage * 3, y + 2, 300 - percentage * 3, 30);
		graphics.setFont(font);
		graphics.setColor(Color.white);
		graphics.drawString(loadingText,(myWidth - fontmetrics.stringWidth(loadingText)) / 2, y + 22);
		graphics.drawString(titleText, (myWidth - fontmetrics1.stringWidth(titleText)) / 2, y - 8);
	}

	RSApplet()
	{
		delayTime = 20;
		minDelay = 1;
		shouldDebug = false;
		shouldClearScreen = true;
		awtFocus = true;
	}

	public String titleText = "";
	public static int hotKey = 508;
	private int anInt4;
	private int delayTime;
	int minDelay;
	private final long aLongArray7[] = new long[10];
	int fps;
	boolean shouldDebug;
	int myWidth;
	int myHeight;
	Graphics graphics;
	RSImageProducer fullGameScreen;
	RSFrame mainFrame;
	private boolean shouldClearScreen;
	private boolean isApplet;
	boolean awtFocus;
	int idleTime;
	int clickMode2;
	public int mouseX;
	public int mouseY;
	private int clickMode1;
	private int clickX;
	private int clickY;
	private long clickTime;
	int clickMode3;
	int saveClickX;
	int saveClickY;
	long aLong29;
	final int keyArray[] = new int[128];
	private final int charQueue[] = new int[128];
	private int readIndex;
	private int writeIndex;
	public static int anInt34;
	public int resizeWidth, resizeHeight;
	
}
