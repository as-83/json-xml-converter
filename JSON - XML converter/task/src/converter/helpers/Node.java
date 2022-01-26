package converter.helpers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Node {
    private String nodeName;
    private String value;
    private LinkedHashMap<String, String> attributes = new LinkedHashMap<>();
    private List<Node> children;
    private Node parent;
    private NodeType nodeType;

    public Node() {
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public LinkedHashMap<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(LinkedHashMap<String, String> attributes) {
        this.attributes = attributes;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public void addChild(Node newNode) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(newNode);
    }

    public void addAttribute(String key, String value) {
         attributes.put(key, value);
    }
}
