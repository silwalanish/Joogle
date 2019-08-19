package webcrawler.domparsing;

import java.util.*;

public class HTMLNode implements INode, IDocument {

  protected String name;
  protected List<INode> children;
  protected Map<String, String> attributes;
  protected INode parent;

  public HTMLNode(String name) {
    this.name = name;
    this.children = new LinkedList<>();
    this.attributes = new HashMap<>();
    this.parent = null;
  }

  public void traverse(int level) {
    System.out.println("-".repeat(level) + name);
    for (INode node: children) {
      if (node instanceof HTMLNode) {
        ((HTMLNode) node).traverse(level + 1);
      } else if(node instanceof TextNode) {
        System.out.println("-".repeat(level) + node.getName() + "(" + ((TextNode) node).getText().strip() + ")");
      }
    }
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public List<INode> getChildren() {
    return children;
  }

  @Override
  public INode getParent() {
    return parent;
  }

  @Override
  public void setParent(INode parent) {
    this.parent = parent;
  }

  @Override
  public IDocument createElement(String elementName) {
    return new HTMLNode(elementName);
  }

  @Override
  public void appendChild(INode child) {
    this.children.add(child);
    child.setParent(this);
  }

  @Override
  public void setAttribute(String attributeName, String value) {
    attributes.put(attributeName, value);
  }

  @Override
  public String getAttribute(String attributeName) {
    return attributes.getOrDefault(attributeName, "");
  }

  @Override
  public Map<String, String> getAttributes() {
    return attributes;
  }

  @Override
  public INode getElementById(String id) {
    INode result = null;
    for (INode node: children) {
      if (children instanceof IDocument) {
        if(((IDocument) children).getAttribute("id").equalsIgnoreCase(id)){
          return (INode) children;
        }else {
          result = ((IDocument) children).getElementById(id);
          if (result != null) {
            return result;
          }
        }
      }
    }

    return null;
  }

  @Override
  public List<INode> getElementsByTagName(String tagName) {
    List<INode> elements = new ArrayList<>();
    for (INode node: children) {
      if (node instanceof IDocument) {
        if(node.getName().equalsIgnoreCase(tagName)){
          elements.add(node);
          return elements;
        }
        elements.addAll(((IDocument) node).getElementsByTagName(tagName));
      }
    }
    return elements;
  }

  @Override
  public List<INode> getElementsByClassName(String className) {
    List<INode> elements = new ArrayList<>();
    for (INode node: children) {
      if (node instanceof IDocument) {
        if(((IDocument) node).getAttribute("class").equalsIgnoreCase(className)){
          elements.add(node);
        }
        elements.addAll(((IDocument) node).getElementsByClassName(className));
      }
    }
    return elements;
  }

  @Override
  public String getTextContent() {
    String textContent = "";
    if (!DOMParser.CONTENT_ESCAPABLE_TAGS.contains(name)) {
      for (INode node: children) {
        if (node instanceof TextNode) {
          textContent += ((TextNode) node).getText() + " ";
        }else if(node instanceof IDocument) {
          textContent += ((IDocument) node).getTextContent();
        }
      }
    }
    return textContent;
  }
}

