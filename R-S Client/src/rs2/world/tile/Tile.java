package rs2.world.tile;

import rs2.Node;
import rs2.world.GroundDecoration;
import rs2.world.InteractableObject;
import rs2.world.WallDecoration;
import rs2.world.WallObject;

public final class Tile extends Node {

	public Tile(int i, int j, int k) {
		interactableObjects = new InteractableObject[5];
		anIntArray1319 = new int[5];
		anInt1310 = tileZ = i;
		anInt1308 = j;
		anInt1309 = k;
	}

	public int tileZ;
	public final int anInt1308;
	public final int anInt1309;
	public final int anInt1310;
	public PlainTile plainTile;
	public ShapedTile shapedTile;
	public WallObject wallObject;
	public WallDecoration wallDecoration;
	public GroundDecoration groundDecoration;
	public GroundItemTile groundItemTile;
	public int count;
	public final InteractableObject[] interactableObjects;
	public final int[] anIntArray1319;
	public int anInt1320;
	public int logicHeight;
	public boolean aBoolean1322;
	public boolean aBoolean1323;
	public boolean aBoolean1324;
	public int anInt1325;
	public int anInt1326;
	public int anInt1327;
	public int anInt1328;
	public Tile tileBelow0;
}
