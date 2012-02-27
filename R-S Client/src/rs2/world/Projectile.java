package rs2.world;

import rs2.Animable;
import rs2.FrameReader;
import rs2.Model;
import rs2.config.SpotAnim;

public final class Projectile extends Animable {

	public void method455(int i, int j, int k, int l) {
		if (!aBoolean1579) {
			double d = l - anInt1580;
			double d2 = j - anInt1581;
			double d3 = Math.sqrt(d * d + d2 * d2);
			aDouble1585 = (double) anInt1580 + (d * (double) radius) / d3;
			aDouble1586 = (double) anInt1581 + (d2 * (double) radius) / d3;
			aDouble1587 = drawHeight;
		}
		double d1 = (speedTime + 1) - i;
		aDouble1574 = ((double) l - aDouble1585) / d1;
		aDouble1575 = ((double) j - aDouble1586) / d1;
		aDouble1576 = Math.sqrt(aDouble1574 * aDouble1574 + aDouble1575 * aDouble1575);
		if (!aBoolean1579)
			aDouble1577 = -aDouble1576 * Math.tan((double) slope * 0.02454369D);
		aDouble1578 = (2D * ((double) k - aDouble1587 - aDouble1577 * d1)) / (d1 * d1);
	}

	public Model getRotatedModel() {
		Model model = graphics.getModel();
		if (model == null)
			return null;
		int j = -1;
		if (graphics.sequence != null)
			j = graphics.sequence.frames[anInt1593];
		Model model_1 = new Model(true, FrameReader.method532(j), false, model);
		if (j != -1) {
			model_1.method469();
			model_1.method470(j);
			model_1.anIntArrayArray1658 = null;
			model_1.anIntArrayArray1657 = null;
		}
		if (graphics.modelScaleX != 128 || graphics.modelScaleY != 128)
			model_1.scaleModel(graphics.modelScaleX, graphics.modelScaleY, graphics.modelScaleX);
		model_1.method474(anInt1596);
		model_1.doLighting(64 + graphics.modelBrightness, 850 + graphics.modelShadowing, -30, -50, -30, true);
		return model_1;
	}

	public Projectile(int slope, int endHeight, int delayTime, int speedTime, int radius, int plane, int drawHeight, int i2, int j2, int lockOn, int id) {
		this.aBoolean1579 = false;
		this.graphics = SpotAnim.cache[id];
		this.plane = plane;
		this.anInt1580 = j2;
		this.anInt1581 = i2;
		this.drawHeight = drawHeight;
		this.delayTime = delayTime;
		this.speedTime = speedTime;
		this.slope = slope;
		this.radius = radius;
		this.lockOn = lockOn;
		this.endHeight = endHeight;
		this.aBoolean1579 = false;
	}

	public void method456(int timePassed) {
		aBoolean1579 = true;
		aDouble1585 += aDouble1574 * (double) timePassed;
		aDouble1586 += aDouble1575 * (double) timePassed;
		aDouble1587 += aDouble1577 * (double) timePassed + 0.5D * aDouble1578 * (double) timePassed * (double) timePassed;
		aDouble1577 += aDouble1578 * (double) timePassed;
		anInt1595 = (int) (Math.atan2(aDouble1574, aDouble1575) * 325.94900000000001D) + 1024 & 0x7ff;
		anInt1596 = (int) (Math.atan2(aDouble1577, aDouble1576) * 325.94900000000001D) & 0x7ff;
		if (graphics.sequence != null) {
			for (anInt1594 += timePassed; anInt1594 > graphics.sequence.method258(anInt1593);) {
				anInt1594 -= graphics.sequence.method258(anInt1593) + 1;
				anInt1593++;
				if (anInt1593 >= graphics.sequence.totalFrames) {
					anInt1593 = 0;
				}
			}
		}
	}

	public final int delayTime;
	public final int speedTime;
	private double aDouble1574;
	private double aDouble1575;
	private double aDouble1576;
	private double aDouble1577;
	private double aDouble1578;
	private boolean aBoolean1579;
	private final int anInt1580;
	private final int anInt1581;
	private final int drawHeight;
	public final int endHeight;
	public double aDouble1585;
	public double aDouble1586;
	public double aDouble1587;
	private final int slope;
	private final int radius;
	public final int lockOn;
	private final SpotAnim graphics;
	private int anInt1593;
	private int anInt1594;
	public int anInt1595;
	private int anInt1596;
	public final int plane;
}
