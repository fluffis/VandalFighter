/*
 MessageLoader class for CryptoDerk's Vandal Fighter
 Copyright (C) 2005  Derek Williams aka CryptoDerk
 Copyright (c) 2006  Finne Boonen aka henna
 Copyright (c) 2006  Beren
 
 CryptoDerk's Vandal Fighter is a tool for displaying
 a live feed of recent changes on Wikimedia projects

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

 Current maintainer
 Finne Boonen aka henna
 Contact information
 http://en.wikipedia.org/wiki/User:Henna
 http://www.cassia.be

 Old Contact information:
 Program website: http://cdvf.derk.org/
 Author's website: http://www.derk.org/
 */

/*
 * This file contains code for Vandalfighter
 * http://en.wikipedia.org/wiki/User:Henna/VF
 * This code is licenced under the gpl-2.0
 * 
 * History
 * -------
 * 
 * Original code was written in 2005
 * by Criptoderk as inner class vf$adminimporter
 * 
 * Standalone file Importer was created on 14-apr-2006
 * author: Finne Boonen 
 * email: finne@cassia.be
 * http://en.wikipedia.org/wiki/User:Henna
 * 
 * The code was used to create MessageLoder class by Beren, 12-nov-2006
 * http://cs.wikipedia.org/wiki/User:Beren
 */

package IRC.parsers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import data.Configuration;
import data.Data;

public class NamespaceLoader extends Thread {
  String l, webpage, tmp, oldstatus;

  Configuration config;

  Data data;

  StringTokenizer parser;

  private String baseUrl;

  HashMap map;

  MessageHandler handler;

  public NamespaceLoader(String lang, MessageHandler handler) {
    this.handler = handler;
    data = Data.getDataObject();
    config = Configuration.getConfigurationObject();
    // just in case someone puts the .org on there
    if (lang.substring(lang.length() - 4).equals(".org"))
      l = lang.substring(0, lang.length() - 4);
    else
      l = lang;
    baseUrl = "/api.php?action=query&meta=siteinfo&siprop=namespaces&format=xml";
    map = new HashMap();
    start();
  }

  void cleanup(String status) {
    // This sleep is here to give the person time to read the status
    // message
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {

      System.out
          .println("cleanup failed, most likely not a problem: MessageLoader.java");
      System.out.println("stacktrace: ");
      e.printStackTrace();
    }
  }

  public void run() {
    String urlStr = "http://" + l + ".org/w" + baseUrl;

    cleanup("trying to import from " + urlStr);

    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(urlStr);

      // create an object of the NodeIterator implementation class
      NodeList nodeList = document.getElementsByTagName("ns");
      for (int i = 0; i < nodeList.getLength(); i++) {
        Node node = nodeList.item(i);
        String id = node.getAttributes().getNamedItem("id").getNodeValue();
        String text = node.getTextContent();
        map.put(id, text);
      }
      // today only "User" namespace is interesting
      config.setNamespaces(l+".2", (String)map.get("2"));
    } catch (FileNotFoundException e) {
      // handler.handleMessage(l, messageName, null);
    } catch (IOException e) {
      System.out.println("IOException: ");
      e.printStackTrace();
      cleanup("IOException: ");
    } catch (ParserConfigurationException e) {
      System.out.println("ParserConfigurationException: ");
      e.printStackTrace();
      cleanup("ParserConfigurationException: ");
    } catch (SAXException e) {
      System.out.println("SAXException: ");
      e.printStackTrace();
      cleanup("SAXException: ");
    }
  }
}