package rs2;

import rs2.config.Sequence;

public class Entity extends Animable {

	public final void setPos(int x, int y, boolean flag) {
		if (forcedAnimation != -1 && Sequence.getSequence(forcedAnimation).priority == 1)
			forcedAnimation = -1;
		if (!flag) {
			int dx = x - pathX[0];
			int dy = y - pathY[0];
			if (dx >= -8 && dx <= 8 && dy >= -8 && dy <= 8) {
				if (pathLength < 9)
					pathLength++;
				for (int index = pathLength; index > 0; index--) {
					pathX[index] = pathX[index - 1];
					pathY[index] = pathY[index - 1];
					pathRun[index] = pathRun[index - 1];
				}
				pathX[0] = x;
				pathY[0] = y;
				pathRun[0] = false;
				return;
			}
		}
		pathLength = 0;
		anInt1542 = 0;
		anInt1503 = 0;
		pathX[0] = x;
		pathY[0] = y;
		currentX = pathX[0] * 128 + tileSize * 64;
		currentY = pathY[0] * 128 + tileSize * 64;
	}

	public final void method446() {
		pathLength = 0;
		anInt1542 = 0;
	}

	public final void updateHitData(int type, int damage, int cycle) {
		for (int index = 0; index < 4; index++) {
			if (hitsLoopCycle[index] <= cycle) {
				hitDamage[index] = damage;
				hitMarkTypes[index] = type;
				hitsLoopCycle[index] = cycle + 70;
				return;
			}
		}
	}

	public final void move(boolean run, int direction) {
		int x = pathX[0];
		int y = pathY[0];
		if (direction == 0) {
			x--;
			y++;
		}
		if (direction == 1) {
			y++;
		}
		if (direction == 2) {
			x++;
			y++;
		}
		if (direction == 3) {
			x--;
		}
		if (direction == 4) {
			x++;
		}
		if (direction == 5) {
			x--;
			y--;
		}
		if (direction == 6) {
			y--;
		}
		if (direction == 7) {
			x++;
			y--;
		}
		if (forcedAnimation != -1 && Sequence.getSequence(forcedAnimation).priority == 1) {
			forcedAnimation = -1;
		}
		if (pathLength < 9) {
			pathLength++;
		}
		for (int index = pathLength; index > 0; index--) {
			pathX[index] = pathX[index - 1];
			pathY[index] = pathY[index - 1];
			pathRun[index] = pathRun[index - 1];
		}
		pathX[0] = x;
		pathY[0] = y;
		pathRun[0] = run;
	}

	public int entScreenX;
	public int entScreenY;
	public final int index = -1;

	public boolean isVisible() {
		return false;
	}

	Entity() {
		pathX = new int[10];
		pathY = new int[10];
		interactingEntity = -1;
		degreesToTurn = 32;
		runAnimIndex = -1;
		height = 200;
		standAnimIndex = -1;
		standTurnAnimIndex = -1;
		hitDamage = new int[4];
		hitMarkTypes = new int[4];
		hitsLoopCycle = new int[4];
		renderAnimation = -1;
		graphicsId = -1;
		forcedAnimation = -1;
		loopCycleStatus = -1000;
		textCycle = 100;
		tileSize = 1;
		aBoolean1541 = false;
		pathRun = new boolean[10];
		walkAnimIndex = -1;
		turn180AnimIndex = -1;
		turn90CWAnimIndex = -1;
		turn90CCWAnimIndex = -1;
	}

	public final int[] pathX;
	public final int[] pathY;
	public int interactingEntity;
	int anInt1503;
	int degreesToTurn;
	int runAnimIndex;
	public String textSpoken;
	public int height;
	public int turnDirection;
	int standAnimIndex;
	int standTurnAnimIndex;
	int textColor;
	final int[] hitDamage;
	final int[] hitMarkTypes;
	final int[] hitsLoopCycle;
	int renderAnimation;
	int anInt1518;
	int animationSpeed;
	int graphicsId;
	int anInt1521;
	int anInt1522;
	int graphicsDelay;
	int graphicsHeight;
	int pathLength;
	public int forcedAnimation;
	int anInt1527;
	int anInt1528;
	int anInt1529;
	int anInt1530;
	int textEffect;
	public int loopCycleStatus;
	public int currentHealth;
	public int maxHealth;
	int textCycle;
	int time;
	int faceX;
	int faceY;
	int tileSize;
	boolean aBoolean1541;
	int anInt1542;
	int anInt1543;
	int anInt1544;
	int anInt1545;
	int anInt1546;
	int anInt1547;
	int anInt1548;
	int turnInfo;
	public int currentX;
	public int currentY;
	int currentRotation;
	final boolean[] pathRun;
	int walkAnimIndex;
	int turn180AnimIndex;
	int turn90CWAnimIndex;
	int turn90CCWAnimIndex;
}
