package util;

import java.util.Iterator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeArray implements Iterable<Node> {

	NodeList nodeList;

	public NodeArray(NodeList nodeList) {
		this.nodeList = nodeList;
	}

	@Override
	public Iterator<Node> iterator() {
		return new NodeItr(nodeList);
	}
}
