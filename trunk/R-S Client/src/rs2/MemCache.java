package rs2;

import rs2.sign.signlink;

public final class MemCache {

	public MemCache(int i) {
		emptyNodeSub = new NodeSub();
		queue = new Queue();
		initialCount = i;
		spaceLeft = i;
		hashTable = new HashTable();
	}

	public NodeSub get(long l) {
		NodeSub nodeSub = (NodeSub) hashTable.findNodeByID(l);
		if (nodeSub != null) {
			queue.insertHead(nodeSub);
		}
		return nodeSub;
	}

	public void put(NodeSub nodeSub, long l) {
		try {
			if (spaceLeft == 0) {
				NodeSub nodeSub_1 = queue.popTail();
				nodeSub_1.remove();
				nodeSub_1.unlinkSub();
				if (nodeSub_1 == emptyNodeSub) {
					NodeSub nodeSub_2 = queue.popTail();
					nodeSub_2.remove();
					nodeSub_2.unlinkSub();
				}
			} else {
				spaceLeft--;
			}
			hashTable.removeFromCache(nodeSub, l);
			queue.insertHead(nodeSub);
			return;
		} catch (RuntimeException runtimeexception) {
			signlink.reportError("47547, " + nodeSub + ", " + l + ", "
					+ (byte) 2 + ", " + runtimeexception.toString());
		}
		throw new RuntimeException();
	}

	public void unlinkAll() {
		do {
			NodeSub nodeSub = queue.popTail();
			if (nodeSub != null) {
				nodeSub.remove();
				nodeSub.unlinkSub();
			} else {
				spaceLeft = initialCount;
				return;
			}
		} while (true);
	}

	private final NodeSub emptyNodeSub;
	private final int initialCount;
	private int spaceLeft;
	private final HashTable hashTable;
	private final Queue queue;
}
