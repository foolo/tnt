package tnt.util;

import java.util.Iterator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeItr implements Iterator<Node> {

	private final NodeList nodeList;
	private int next_index = 0;

	public NodeItr(NodeList nodeList) {
		this.nodeList = nodeList;
	}

	@Override
	public boolean hasNext() {
		int length = nodeList.getLength();
		return next_index < length;
	}

	@Override
	public Node next() {
		Node n = nodeList.item(next_index);
		next_index++;
		return n;
	}
}
