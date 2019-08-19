package webcrawler.domparsing;

import java.util.List;
import java.util.Map;

public interface IDocument {

  IDocument createElement(String elementName);

  void appendChild(INode child);

  void setAttribute(String attributeName, String value);

  String getAttribute(String attributeName);

  Map<String, String> getAttributes();

  INode getElementById(String id);

  List<INode> getElementsByTagName(String tagName);

  List<INode> getElementsByClassName(String className);

  String getTextContent();

}
