package rs2;

import rs2.config.ItemDef;

final class Item extends Animable {

	public final Model getRotatedModel() {
		ItemDef itemDef = ItemDef.getItem(ID);
		return itemDef.method201(anInt1559);
	}

	public Item() {
	}

	public int ID;
	public int x;
	public int y;
	public int anInt1559;
}
