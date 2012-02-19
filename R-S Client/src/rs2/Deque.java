package rs2;

public final class Deque {

	public Deque() {
		head = new Node();
		head.prev = head;
		head.next = head;
	}

	public void append(Node node) {
		if (node.next != null)
			node.remove();
		node.next = head.next;
		node.prev = head;
		node.next.prev = node;
		node.prev.next = node;
	}

	public void insertTail(Node node) {
		if (node.next != null)
			node.remove();
		node.next = head;
		node.prev = head.prev;
		node.next.prev = node;
		node.prev.next = node;
	}

	public Node popFront() {
		Node node = head.prev;
		if (node == head) {
			return null;
		} else {
			node.remove();
			return node;
		}
	}

	public Node head() {
		Node node = head.prev;
		if (node == head) {
			current = null;
			return null;
		} else {
			current = node.prev;
			return node;
		}
	}

	public Node getFirst() {
		Node node = head.next;
		if (node == head) {
			current = null;
			return null;
		} else {
			current = node.next;
			return node;
		}
	}

	public Node next() {
		Node node = current;
		if (node == head) {
			current = null;
			return null;
		} else {
			current = node.prev;
			return node;
		}
	}

	public Node getNext() {
		Node node = current;
		if (node == head) {
			current = null;
			return null;
		}
		current = node.next;
		return node;
	}

	public void removeAll() {
		if (head.prev == head)
			return;
		do {
			Node node = head.prev;
			if (node == head)
				return;
			node.remove();
		} while (true);
	}

	private final Node head;
	private Node current;
}
