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

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.StringTokenizer;

import data.Data;
import data.Configuration;

public class PagenameLoader extends Thread {
  String l, webpage, tmp, oldstatus;

  Configuration config;

  Data data;

  private String baseUrl;

  private String pagename;

  PagenameHandler handler;

  public PagenameLoader(String lang, String pagename, PagenameHandler handler) {
    this.pagename = pagename;
    this.handler = handler;
    data = Data.getDataObject();
    config = Configuration.getConfigurationObject();
    // just in case someone puts the .org on there
    if (lang.substring(lang.length() - 4).equals(".org"))
      l = lang.substring(0, lang.length() - 4);
    else
      l = lang;
    baseUrl = "/wiki/" + pagename;
    start();
  }

  void cleanup(String status) {
    // This sleep is here to give the person time to read the status
    // message
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {

      System.out
          .println("cleanup failed, most likely not a problem: PagenameLoader.java");
      System.out.println("stacktrace: ");
      e.printStackTrace();
    }
  }

  public void run() {
    webpage = "";
    tmp = "";
    
    String location = "http://" + l + ".org" + baseUrl;
    
    cleanup("trying to read from " + location);
    try {
      java.net.URL url = new java.net.URL(location);
      java.net.HttpURLConnection con = (java.net.HttpURLConnection) url.openConnection();
      con.setInstanceFollowRedirects(false);
      con.connect();
     
      while (String.valueOf(con.getResponseCode()).startsWith("3")) {
        location = con.getHeaderField("Location");
        con.disconnect();
        url = new java.net.URL(location);
        con = (java.net.HttpURLConnection) url.openConnection();
        con.setInstanceFollowRedirects(false);
        con.connect();
      } 
      int index = location.lastIndexOf('/');
      String localPagename = location.substring(index+1);

      handler.handlePagename(l, pagename, urlDecode(localPagename));
    } catch (IOException e) {
      System.out.println("IOException, reading the stream: ");
      e.printStackTrace();
      cleanup("IOException, reading the stream: ");
    }
  }
  
  private static String urlDecode(String value) {
    value = value.replace('_', ' ');
    try {
      value = URLDecoder.decode(value, "UTF-8");
    } catch (UnsupportedEncodingException ex) {
    }
    return value;
  }
}