package tools;
/*
 Importer class for CryptoDerk's Vandal Fighter
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
 * Standalone file was created on 14-apr-2006
 * author: Finne Boonen
 * email: finne@cassia.be
 * http://en.wikipedia.org/wiki/User:Henna
 *
 * Changed by Beren, 12-nov-2006
 * http://cs.wikipedia.org/wiki/User:Beren
 */

import gui.StatusListener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ResourceBundle;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.ListModel;

import data.Configuration;
import data.Data;


public class Importer extends Thread {
  public static final short WHITELIST = 0;

  public static final short GREYLIST = 1;

  String l, webpage, tmp, oldstatus;

  Configuration config;

  ResourceBundle messages;

  Data data;

  StringTokenizer parser;

  private String baseurl;

  StatusListener statusL;

  short type;

  JButton button;

  JTextField textField;

  public Importer(String lang, String burl, StatusListener lst, short type,
      JButton button, JTextField textField) {
    data = Data.getDataObject();
    config = Configuration.getConfigurationObject();
    messages = ResourceBundle.getBundle("MessagesBundle", config.currentLocale);
    statusL = lst;
    this.type = type;
    this.button = button;
    this.textField = textField;
    // just in case someone puts the .org on there
    if (lang.substring(lang.length() - 4).equals(".org"))
      l = lang.substring(0, lang.length() - 4);
    else
      l = lang;
    baseurl = burl;
    start();
  }

  void cleanup(String status) {
    statusL.updateStatus(status);
    // This sleep is here to give the person time to read the status
    // message
    try {
      Thread.sleep(3000);
      // TODO fix this with new connected code
      /*
       * if (connected) chanstatus(); else statusLabel.setText(oldstatus);
       */
    } catch (InterruptedException e) {

      System.out
          .println("cleanup failed, most likely not a problem: Importer.java");
      System.out.println("stacktrace: ");
      e.printStackTrace();
    }
  }

  public void run() {
    webpage = "";
    tmp = "";

    String urlStr = "http://" + l + ".org/w" + baseurl;

    cleanup(messages.getString("StatusUpdateTryingToImport") + urlStr);
    try {
      URL wholeUrl = new URL(urlStr);

      try {
        // HttpURLConnection conn = new HttpURLConnection(adminURL);
        // conn.setRequestProperty ( "User-agent", "my agent name");
        // conn.setRequestProperty ( "From", "my name");
        // conn.connect();
        // InputStream stream = conn.getInputStream();

        HttpURLConnection urlconn = (HttpURLConnection) wholeUrl
            .openConnection();
        urlconn.disconnect();
        urlconn.setRequestProperty("User-agent", Configuration
            .getConfigurationObject().version);
        urlconn.connect();
        // (HttpURLConnect)urlconn;
        InputStreamReader reader = new InputStreamReader(
            new BufferedInputStream(urlconn.getInputStream()), "UTF-8");

        int i = 0;
        char c[] = new char[1024];
        while ((i = reader.read(c)) != -1) {

          webpage = webpage + new String(c, 0, i);

          Pattern p = Pattern
              .compile("<li><a href=\"/wiki/[^\"]*\" title=\"[^:]*:([^\"]*)\"");
          Matcher m = p.matcher(webpage);

          int offset = 0;
          while (m.find()) {
            try {
              offset = m.end();
            } catch (IllegalStateException e) {
              offset = 0;
            }
            if (type == WHITELIST)
              config.whitelistimport(m.group(1) + "#" + l);
            else if (type == GREYLIST)
              config.greylistimport(m.group(1) + "#" + l);
          }
          webpage = webpage.substring(offset);
        }

        DefaultListModel model = null;
        if (type == WHITELIST)
          model = config.whitelistModel;
        else if (type == GREYLIST)
          model = config.greylistModel;

        int tmp = data.editstableModel.getRowCount();
        for (int j = tmp - 1; j >= 0; j--)
          if (model.contains((String) data.editstableModel.getValueAt(j,
              config.editorcol)
              + "#"
              + (String) data.editstableModel.getValueAt(j, config.projectcol)))
            data.editstableModel.removeRow(j);

        cleanup(messages.getString("StatusUpdateSuccessfullyImported") + l);
      } catch (IOException e) {
        System.out.println("IOException: ");
        e.printStackTrace();
        cleanup("IOException: ");
      }
    } catch (MalformedURLException e) {
      System.out.println("malformed url: " + urlStr);
      e.printStackTrace();
      cleanup("malformed url: " + urlStr);
    } finally {
      button.setEnabled(true);
      textField.setEditable(true);
    }
  }
}
