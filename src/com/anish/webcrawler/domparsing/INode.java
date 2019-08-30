package com.anish.webcrawler.domparsing;

import java.util.List;

public interface INode {

  String getName();

  List<INode> getChildren();

  INode getParent();

  void setParent(INode parent);

}
