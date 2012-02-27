package rs2;

final class MouseDetection implements Runnable {

	public void run() {
		while (running) {
			synchronized (syncObject) {
				if (coordsIndex < 500) {
					coordsX[coordsIndex] = client.mouseX;
					coordsY[coordsIndex] = client.mouseY;
					coordsIndex++;
				}
			}
			try {
				Thread.sleep(50L);
			} catch (Exception _ex) {
			}
		}
	}

	public MouseDetection(Client c) {
		syncObject = new Object();
		coordsY = new int[500];
		running = true;
		coordsX = new int[500];
		client = c;
	}

	private Client client;
	public final Object syncObject;
	public final int[] coordsY;
	public boolean running;
	public final int[] coordsX;
	public int coordsIndex;
}