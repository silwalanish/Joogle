package webcrawler.domparsing;

import java.util.List;

public class TextNode implements INode {

  protected String text;
  protected INode parent;

  public TextNode(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }

  @Override
  public String getName() {
    return "TextNode";
  }

  @Override
  public List<INode> getChildren() {
    return null;
  }

  @Override
  public INode getParent() {
    return parent;
  }

  @Override
  public void setParent(INode parent) {
    this.parent = parent;
  }
}
