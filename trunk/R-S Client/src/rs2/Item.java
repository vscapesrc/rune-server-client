package rs2;

import rs2.config.ItemDefinitions;

final class Item extends Animable {

	public final Model getRotatedModel() {
		ItemDefinitions itemDef = ItemDefinitions.getDefinition(ID);
		return itemDef.method201(anInt1559);
	}

	public Item() {
	}

	public int ID;
	public int x;
	public int y;
	public int anInt1559;
}
