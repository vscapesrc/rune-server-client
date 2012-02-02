package rs2.resource;

import rs2.NodeSub;

public final class Resource extends NodeSub {

	public Resource() {
		incomplete = true;
	}

	public int type;
	public byte data[];
	public int id;
	public boolean incomplete;
	public int requestAge;
}
