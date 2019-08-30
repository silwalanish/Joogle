package com.anish.webcrawler.domparsing;

import java.util.*;

/**
 * DISCLAMIER use at your own risk
 * A failed attempt to make a HTML DOM parser
 * Failed but not given up. Keep an eye out for this in future
 */
public class DOMParser {

  public static final List<String> SINGLETON_TAGS = new ArrayList<>(
    Arrays.asList(
      "area",
      "base",
      "br",
      "col",
      "embed",
      "hr",
      "img",
      "input",
      "link",
      "meta",
      "param",
      "source",
      "track",
      "wbr",
      "command",
      "keygen",
      "menuitem"
    )
  );

  public static final List<String> CONTENT_ESCAPABLE_TAGS = new ArrayList<>(
    Arrays.asList(
      "script",
      "style",
      "canvas",
      "noscript",
      "audio",
      "video",
      "iframe"
    )
  );

  private boolean inTag = false;
  private boolean inTagName = false;
  private boolean closeTag = false;
  private boolean inComment = false;
  private boolean inQuotes = false;
  private boolean append = false;

  private String textContent = "";
  private String tagName = "";

  private HTMLNode document;
  private HTMLNode currentNode;

  public void tagBegining() {
    if (!inTag) {
      inTag = true;
      inTagName = true;
      closeTag = false;
      inComment = false;
    }
  }

  public void tagNameEnd() {
    if (inTag && inTagName) {
      inTagName = false;
      if (!closeTag && !inComment) {
        HTMLNode childNode = (HTMLNode) document.createElement(tagName);
        currentNode.appendChild(childNode);
        if (!SINGLETON_TAGS.contains(tagName)) {
          currentNode = childNode;
        }
      }
      tagName = "";
    }
  }

  public void tagEnding() {
    if (inTag) {
      inTag = false;
      if (closeTag) {

        if (currentNode != null) {
          textContent = textContent.strip();
          if (!textContent.isEmpty() || !textContent.isBlank()) {
            TextNode textNode = new TextNode(textContent);
            currentNode.appendChild((INode) textNode);
          }
          currentNode = (HTMLNode) currentNode.getParent();
          textContent = "";
        }else{
          currentNode = document;
        }
        closeTag = false;
      }
    }
  }

  public void space() {
    if (inTag) {
      inTagName = false;
    }
  }

  public void comment() {
    if (inTag && inTagName) {
      inComment = true;
    }
  }

  public void quotes() {
    inQuotes = !inQuotes;
  }

  public void closingTag() {
    if (inTag && inTagName) {
      closeTag = true;
    }
  }


  public String parse(String html) {
    int textLen = html.length();
    char chr;
    int i = 0;

    document = new HTMLNode("document");
    currentNode = document;
    HTMLNode childNode = null;

    while (i < textLen) {
      chr = html.charAt(i);

      switch (chr) {
        case '<':
          tagBegining();
          break;
        case '!':
          comment();
          append = true;
          break;
        case '/':
          closingTag();
          break;
        case ' ':
          tagNameEnd();
          append = true;
          break;
        case '>':
          tagNameEnd();
          tagEnding();
          break;
        case '\'':
        case '\"':
          quotes();
          append = true;
          break;
        default:
          append = true;
      }
      if(append) {
        if (!inTag) {
          textContent += chr;
        } else if (inTagName) {
          tagName += chr;
        }
        append = false;
      }

      i++;
    }

    HTMLNode body = (HTMLNode) document.getElementsByTagName("body").get(0);
    System.out.println(body.getParent().getName());

    return document.getTextContent();
  }

}
