/*
 Configuration class for CryptoDerk's Vandal Fighter
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
 * First concept was written in 2005
 * by Criptoderk as methods vf.savefiles() and vf.loadfiles()
 *
 * Completely rewritten configuration
 * standalone file was created on Feb 18, 2006
 * original author: Finne Boonen
 * email: finne@cassia.be
 * http://en.wikipedia.org/wiki/User:Henna
 *
 * Changed by Beren, 12-nov-2006
 * http://cs.wikipedia.org/wiki/User:Beren
 */

package data;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.InvalidPropertiesFormatException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.swing.DefaultListModel;
import javax.swing.JTable;

/**
 * @author Finne Boonen
 *
 */

// reference:
public class Configuration {

  private static final String HEADER_WATCHLIST = "==Watchlist==";

  private static final String HEADER_BLACKLIST = "==Blacklist==";

  private static final String HEADER_WHITELIST = "==Whitelist==";

  private static final String HEADER_GREYLIST = "==Greylist==";

  private static final String HEADER_TEMPWATCHLIST = "==tempWatchlist==";

  private static final String HEADER_TEMPBLACKLIST = "==tempBlacklist==";

  private static final String HEADER_TEMPWHITELIST = "==tempWhitelist==";

  private static final String HEADER_REGEXPBLACKLIST = "==RegexBlacklist==";

  private static final String HEADER_REGEXPWHITELIST = "==RegexWhitelist==";

  private static final String HEADER_MESSAGES = "==Messages==";

  private static final String HEADER_NAMESPACES = "==Namespaces==";

  private static final String HEADER_PAGENAMES = "==Pagenames==";

  public String filename = "vfdata.dat";

  public int verint = 54;

  public String version = " 3.6b (" +verint +")";//b means beta

  public int iplength = 10, appwidth = 768, lcb = 45, appheight = 576, appLeft = 100, appTop = 100, minheight = 360, minwidth = 480;
  //public int iplength = 10, appwidth = 1024, lcb = 45, appheight = 768, minheight = 480, minwidth = 640;

  public boolean pausevar;

  public int specialcol, subjectcol, sectimecol, urlcol, projectcol, xcol,
      timecol, articlecol, editorcol, summarycol;

  //public int minorcol, newecol,

  public int typecol, changedcol, riskcol;

  public int totalcols;

  //public int wlistcol;

  //public int blistcol;

  public Properties props;

  //public int twhitecol;

  //public int watchcol;

  public JTable editstableTable;

  public ResourceBundle bundle;

  //public int tblackcol;

  //public int twatchcol;

  public int listcol;

  public DefaultListModel watchlistModel;

  public DefaultListModel blacklistModel;

  public DefaultListModel whitelistModel;

  public DefaultListModel greylistModel;

  // public DefaultListModel whitelistModel;
  // public DefaultListModel blacklistModel;
  // public DefaultListModel watchlistModel;

  public DefaultListModel regexpblackModel;

  public DefaultListModel regexpwhiteModel;

  public DefaultListModel tempblacklistModel;

  public DefaultListModel tempwhitelistModel;

  public DefaultListModel tempwatchlistModel;

  public Map messages;
  public Map messageMatchers;

  public Map namespaces;

  public Map pagenames;

  public boolean quitvar;

  public Locale currentLocale;

  public TimeZone timeZone;

  public boolean containsKey(String key) {
    return props.containsKey(key);
  }

  public void whitelistimport(String toadd) {
    if (!whitelistModel.contains(toadd)) {
      if (greylistModel.contains(toadd)) {
        greylistModel.removeElement(toadd);
      }
      if (blacklistModel.contains(toadd)) {
        blacklistModel.removeElement(toadd);
      }
      whitelistModel.addElement(toadd);
    }
  }

  public void greylistimport(String toadd) {
    if (!greylistModel.contains(toadd)) {
      if (whitelistModel.contains(toadd)) {
        whitelistModel.removeElement(toadd);
      }
      if (blacklistModel.contains(toadd)) {
        blacklistModel.removeElement(toadd);
      }

      greylistModel.addElement(toadd);
    }
  }

  public void setProperty(String propname, String val) {
    props.setProperty(propname, val);
  }

  public void setProperty(String propname, boolean bval) {
    String val;
    if (bval)
      val = "true";
    else
      val = "false";
    props.setProperty(propname, val);
  }

  public String getProperty(String propname) {
    return props.getProperty(propname);
  }

  public Color getColorProp(String propname) {
    return new Color(getIntProp(propname + "R"), getIntProp(propname + "G"),
        getIntProp(propname + "B"));
  }

  public void setColorProp(String propname, Color c) {
    if (c != null) {
      props.setProperty(propname + "R", "" + c.getRed());
      props.setProperty(propname + "G", "" + c.getGreen());
      props.setProperty(propname + "B", "" + c.getBlue());
    }
  }

  public int getIntProp(String propname) {
    // System.out.println("intprop: "+propname);
    return Integer.parseInt(getProperty(propname));
  }

  public String getMessage(String key) {
    return (String) messages.get(key);
  }

  public void setMessage(String key, String value) {
    messages.put(key, value);
    messageMatchers.put(key, new MessageMatcher(key, value,
        !key.contains("Revertpage")));
  }

  public MessageMatcher getMessageMatcher(String key) {
    return (MessageMatcher) messageMatchers.get(key);
  }

  public String getNamespace(String key) {
    return (String) namespaces.get(key);
  }

  public void setNamespaces(String key, String value) {
    namespaces.put(key, value);
  }

  public String getPagename(String key) {
    return (String) pagenames.get(key);
  }

  public void setPagename(String key, String value) {
    pagenames.put(key, value);
  }

  public void storeToXML(OutputStream os) throws IOException {
    props.storeToXML(os, null);
  }

  public void loadFromXML(InputStream os) throws IOException {
    props.loadFromXML(os);
  }

  public void loadFromXML(String filename) {
    try {
      File inputfile = new File(filename);
      FileInputStream is = new FileInputStream(inputfile);
      loadFromXML(is);
    } catch (InvalidPropertiesFormatException e) {
      System.out.println("Error: error loading vfdata.xml, invalid format");
    } catch (IOException e) {
      System.out
          .println("Error: error loading vfdata.xml, File does not exist");

    }
  }

  public boolean getBooleanProp(String propname) {
    Boolean b;
    b = new Boolean(props.getProperty(propname));
    // System.out.println("finding "+propname+": "+b.booleanValue());
    return b.booleanValue();
  }

  public String[] getArrayProp(String propname) {
    String[] result = null;
    String prop = props.getProperty(propname);
    if (prop != null)
      result = prop.split(" ");
    else
      result = new String[0];
    return result;
  }

  public void export() {
    /*
     * File whitelistFile = new File("vflists.dat"); FileOutputStream fos;
     * ObjectOutputStream oos;
     */
  }

  private Configuration() {
    props = new Properties();
    regexpwhiteModel = new DefaultListModel();
    regexpblackModel = new DefaultListModel();
    tempwhitelistModel = new DefaultListModel();
    tempblacklistModel = new DefaultListModel();
    tempwatchlistModel = new DefaultListModel();
    watchlistModel = new DefaultListModel();
    whitelistModel = new DefaultListModel();
    blacklistModel = new DefaultListModel();
    greylistModel = new DefaultListModel();
    messages = new TreeMap();
    messageMatchers = new Hashtable();
    namespaces = new TreeMap();
    pagenames = new TreeMap();
    setDefaultConfig();
    readfiles();

    //version = "Vandal Fighter 19";
  }

  public static Configuration getConfigurationObject() {
    if (ref == null)
      ref = new Configuration();
    return ref;
  }

  private static Configuration ref;

  public void exportLists(String filename) {
    try {

      // write out whitelist
      File wFile = new File(filename);
      FileOutputStream wfos;
      ObjectOutputStream woos;

      wfos = new FileOutputStream(wFile);
      woos = new ObjectOutputStream(wfos);
      woos.writeObject(whitelistModel);
      woos.writeObject(blacklistModel);
      woos.writeObject(greylistModel);
      woos.writeObject(watchlistModel);
      woos.writeObject(regexpwhiteModel);
      woos.writeObject(regexpblackModel);
      woos.close();
    } catch (Exception e) {
    }
  }

  public void importLists(String filename) {
    File whitelistFile = new File(filename);
    FileInputStream fis;
    ObjectInputStream ois;

    try {
      fis = new FileInputStream(whitelistFile);
      ois = new ObjectInputStream(fis);
      DefaultListModel m = new DefaultListModel();
      m = (DefaultListModel) ois.readObject();
      for (Enumeration e = m.elements(); e.hasMoreElements();) {
        whitelistModel.addElement(e.nextElement());
      }
      m = (DefaultListModel) ois.readObject();
      for (Enumeration e = m.elements(); e.hasMoreElements();) {
        blacklistModel.addElement(e.nextElement());
      }
      m = (DefaultListModel) ois.readObject();
      for (Enumeration e = m.elements(); e.hasMoreElements();) {
        greylistModel.addElement(e.nextElement());
      }

      m = (DefaultListModel) ois.readObject();
      for (Enumeration e = m.elements(); e.hasMoreElements();) {
        watchlistModel.addElement(e.nextElement());
      }

      m = (DefaultListModel) ois.readObject();
      for (Enumeration e = m.elements(); e.hasMoreElements();) {
        regexpwhiteModel.addElement(e.nextElement());
      }

      m = (DefaultListModel) ois.readObject();
      for (Enumeration e = m.elements(); e.hasMoreElements();) {
        regexpblackModel.addElement(e.nextElement());
      }

      ois.close();
      fis.close();
    } catch (Exception e) {
    }
  }

  private void setDefaultVal(String key, boolean val) {
    props.setProperty(key, "" + val);
  }

  private void setDefaultVal(String key, int val) {
    props.setProperty(key, "" + val);
  }

  private void setDefaultVal(String key, String val) {
    props.setProperty(key, val);
  }

  private void setDefaultVal(String key, Color val) {
    setColorProp(key, val);
  }

  public void setDefaultConfig() {
    setDefaultVal("showips", false);
    setDefaultVal("newpages", false);
    setDefaultVal("olddeleted", false);
    setDefaultVal("registerdeleted", true);
    setDefaultVal("autoscroll", true);
    setDefaultVal("singleclick", true);
    setDefaultVal("colorips", true);
    setDefaultVal("colornewusers", false);
    setDefaultVal("colornew", true);
    setDefaultVal("colorblist", true);
    setDefaultVal("colorwatch", true);
    setDefaultVal("showwatch", false);
    setDefaultVal("beeptempblack", false);
    setDefaultVal("beeppermblack", false);
    setDefaultVal("beeptempwatch", false);
    setDefaultVal("beeppermwatch", false);
    setDefaultVal("beepregexpblack", false);
    setDefaultVal("queueedits", true);
    setDefaultVal("newversion", false);
    setDefaultVal("connectatstart", true);//Auto Start
    setDefaultVal("browserpick", true);
    setDefaultVal("removereviewed", false);
    setDefaultVal("stripformatting", true);
    setDefaultVal("colorchanged", true);
    setDefaultVal("colormoves", true);
    setDefaultVal("rwhiteuser", false);
    setDefaultVal("rwhitepage", true);
    setDefaultVal("rwhitesummary", false);
    setDefaultVal("rblackuser", true);
    setDefaultVal("rblackpage", true);
    setDefaultVal("rblacksummary", true);
    setDefaultVal("listprecedence", true);
    setDefaultVal("reversetable", false);
    setDefaultVal("coloruserpage", true);
    setDefaultVal("blackreverted", false);
    setDefaultVal("watchdeleted", false);
    setDefaultVal("watchwarned", false);
    setDefaultVal("blackdeleted", false);
    setDefaultVal("vandalism_nl_box", false);
    setDefaultVal("vandalism_en_box", false);//engilsh project
    setDefaultVal("vandalism_box", false);
    setDefaultVal("watchuserpages", true);
    setDefaultVal("watchmasschanges", true);
    setDefaultVal("watchpagemoves", true);
    setDefaultVal("watchnewpages", true);
    setDefaultVal("delpagepermButton", false);
    setDefaultVal("delpagetempButton", true);
    setDefaultVal("watchdelpermButton", false);
    setDefaultVal("watchdeltempButton", true);
    setDefaultVal("watchwarnedpermButton", false);
    setDefaultVal("watchwarnedtempButton", true);
    setDefaultVal("revertpermButton", false);
    setDefaultVal("reverttempButton", true);
    setDefaultVal("blockButton", false);
    setDefaultVal("vipButton", true);
    setDefaultVal("risk", true);
    setDefaultVal("sizeCol0", 0);
    setDefaultVal("sizeCol1", 0);
    setDefaultVal("sizeCol2", 0);
    setDefaultVal("sizeCol3", 0);
    setDefaultVal("sizeCol4", lcb);
    setDefaultVal("sizeCol5", lcb+25);
    setDefaultVal("sizeCol6", lcb);
    setDefaultVal("sizeCol7", 200);
    setDefaultVal("sizeCol8", 100);
    setDefaultVal("sizeCol9", lcb);
    setDefaultVal("sizeCol10", 300);
    setDefaultVal("sizeCol11", lcb);
    setDefaultVal("sizeCol12", lcb);
    setDefaultVal("sizeCol13", lcb+40);
    setDefaultVal("sizeCol14", lcb);
    setDefaultVal("sizeCol15", lcb);
    setDefaultVal("sizeCol16", lcb);
    setDefaultVal("channel", "en.wikipedia");
    setDefaultVal("lang", "en");
    setDefaultVal("country", "US");
    setDefaultVal("timeZone", TimeZone.getDefault().getID());

    Color blacklistcolor = new Color(255, 255, 0), // Yellow
    watchlistcolor = new Color(153, 255, 255), // Light blue
    changedcolor = new Color(204, 255, 204), // Light green
    movecolor = new Color(255, 102, 102), // Red
    newcolor = new Color(255, 204, 102), // Light orange
    ipcolor = new Color(255, 204, 255), // Light purple
    newusercolor = new Color(255, 224, 255), // Light light purple
    userpagecolor = new Color(205, 201, 201); // Light gray

    setDefaultVal("blacklistcolor", blacklistcolor);
    setDefaultVal("watchlistcolor", watchlistcolor);
    setDefaultVal("changedcolor", changedcolor);
    setDefaultVal("movecolor", movecolor);
    setDefaultVal("newcolor", newcolor);
    setDefaultVal("ipcolor", ipcolor);
    setDefaultVal("newusercolor", newusercolor);
    setDefaultVal("userpagecolor", userpagecolor);

  }

  public void savefiles() {
    System.out.println("saving from config");
    String filename = "vfdata2.txt";
    File of = new File(filename);
    try {
      FileOutputStream os = new FileOutputStream(of);
      try {
        props.store(os, "testcomment");
        PrintStream p = new PrintStream(os);

        p.println(HEADER_WATCHLIST);
        for (int i = watchlistModel.getSize() - 1; i >= 0; i--) {
          p.println(watchlistModel.getElementAt(i) + "");
        }
        p.println(HEADER_BLACKLIST);
        for (int i = blacklistModel.getSize() - 1; i >= 0; i--) {
          p.println(blacklistModel.getElementAt(i) + "");
        }
        p.println(HEADER_WHITELIST);
        for (int i = whitelistModel.getSize() - 1; i >= 0; i--) {
          p.println(whitelistModel.getElementAt(i) + "");
        }
        p.println(HEADER_GREYLIST);
        for (int i = greylistModel.getSize() - 1; i >= 0; i--) {
          p.println(greylistModel.getElementAt(i) + "");
        }
        p.println(HEADER_TEMPWATCHLIST);
        for (int i = tempwatchlistModel.getSize() - 1; i >= 0; i--) {
          p.println(tempwatchlistModel.getElementAt(i) + "");
        }
        p.println(HEADER_TEMPBLACKLIST);
        for (int i = tempblacklistModel.getSize() - 1; i >= 0; i--) {
          p.println(tempblacklistModel.getElementAt(i) + "");
        }
        p.println(HEADER_TEMPWHITELIST);
        for (int i = tempwhitelistModel.getSize() - 1; i >= 0; i--) {
          p.println(tempwhitelistModel.getElementAt(i) + "");
        }
        p.println(HEADER_REGEXPBLACKLIST);
        for (int i = regexpblackModel.getSize() - 1; i >= 0; i--) {
          p.println(regexpblackModel.getElementAt(i) + "");
        }
        p.println(HEADER_REGEXPWHITELIST);
        for (int i = regexpwhiteModel.getSize() - 1; i >= 0; i--) {
          p.println(regexpwhiteModel.getElementAt(i) + "");
        }
        p.println(HEADER_MESSAGES);
        for (Iterator iter = messages.entrySet().iterator(); iter.hasNext();) {
          Map.Entry entry = (Map.Entry) iter.next();
          p.println(entry.getKey() + "=" + entry.getValue());
        }
        p.println(HEADER_NAMESPACES);
        for (Iterator iter = namespaces.entrySet().iterator(); iter.hasNext();) {
          Map.Entry entry = (Map.Entry) iter.next();
          p.println(entry.getKey() + "=" + entry.getValue());
        }
        p.println(HEADER_PAGENAMES);
        for (Iterator iter = pagenames.entrySet().iterator(); iter.hasNext();) {
          Map.Entry entry = (Map.Entry) iter.next();
          p.println(entry.getKey() + "=" + entry.getValue());
        }
        p.close();
        os.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }

    } catch (FileNotFoundException ex) {
      ex.printStackTrace();
    }

  }

  private void readfiles() {
    System.out.println("config.readfiles");
    String filename = "vfdata2.txt";
    // File inf = new File(filename);
    try {
      // FileInputStream is = new FileInputStream(inf);
      // DataInputStream in = new DataInputStream(is);
      BufferedReader bf = new BufferedReader(new FileReader(filename));
      try {
        String tmp = null;
        while (bf.ready()) {
          tmp = bf.readLine();
          // System.out.println("reading: "+tmp);
          if (tmp.charAt(0) == '=') {
            break;

          } else {
            int cut = tmp.indexOf('=');

            if (cut < 0) {
            } else {
              String key;
              String val;

              key = loadConvert(tmp.substring(0, cut));
              val = loadConvert(tmp.substring(cut + 1, tmp.length()));

              props.put(key, val);
            }
          }
        }

        while (bf.ready()) {
          if (HEADER_WATCHLIST.equals(tmp))
            while (bf.ready()) {
              tmp = bf.readLine();
              if (tmp.charAt(0) == '=') {
                break;
              } else {
                watchlistModel.addElement(tmp);
              }
            }
          else if (HEADER_BLACKLIST.equals(tmp))
            while (bf.ready()) {
              tmp = bf.readLine();
              if (tmp.charAt(0) == '=') {
                break;
              } else {
                blacklistModel.addElement(tmp);
              }
            }
          else if (HEADER_WHITELIST.equals(tmp))
            while (bf.ready()) {
              tmp = bf.readLine();
              if (tmp.charAt(0) == '=') {
                break;
              } else {
                whitelistModel.addElement(tmp);
              }
            }
          else if (HEADER_GREYLIST.equals(tmp))
            while (bf.ready()) {
              tmp = bf.readLine();
              if (tmp.charAt(0) == '=') {
                break;
              } else {
                greylistModel.addElement(tmp);
              }
            }

          else if (HEADER_TEMPWATCHLIST.equals(tmp))
            while (bf.ready()) {
              tmp = bf.readLine();
              if (tmp.charAt(0) == '=') {
                break;
              } else {
                tempwatchlistModel.addElement(tmp);
              }
            }
          else if (HEADER_TEMPBLACKLIST.equals(tmp))
            while (bf.ready()) {
              tmp = bf.readLine();
              if (tmp.charAt(0) == '=') {
                break;
              } else {
                tempblacklistModel.addElement(tmp);
              }
            }
          else if (HEADER_TEMPWHITELIST.equals(tmp))
            while (bf.ready()) {
              tmp = bf.readLine();
              if (tmp.charAt(0) == '=') {
                break;
              } else {
                tempwhitelistModel.addElement(tmp);
              }
            }
          else if (HEADER_REGEXPBLACKLIST.equals(tmp))
            while (bf.ready()) {
              tmp = bf.readLine();
              if (tmp.charAt(0) == '=') {
                break;
              } else {
                regexpblackModel.addElement(tmp);
              }
            }
          else if (HEADER_REGEXPWHITELIST.equals(tmp))
            while (bf.ready()) {
              tmp = bf.readLine();
              if (tmp.charAt(0) == '=') {
                break;
              } else {
                regexpwhiteModel.addElement(tmp);
              }
            }
          else if (HEADER_MESSAGES.equals(tmp))
            while (bf.ready()) {
              tmp = bf.readLine();
              if (tmp.charAt(0) == '=') {
                break;
              } else {
                int index = tmp.indexOf('=');
                if (index != 0)
                  setMessage(tmp.substring(0, index), tmp
                      .substring(index + 1));
              }
            }
          else if (HEADER_NAMESPACES.equals(tmp))
            while (bf.ready()) {
              tmp = bf.readLine();
              if (tmp.charAt(0) == '=') {
                break;
              } else {
                int index = tmp.indexOf('=');
                if (index != 0)
                  namespaces.put(tmp.substring(0, index), tmp
                      .substring(index + 1));
              }
            }
          else if (HEADER_PAGENAMES.equals(tmp))
            while (bf.ready()) {
              tmp = bf.readLine();
              if (tmp.charAt(0) == '=') {
                break;
              } else {
                int index = tmp.indexOf('=');
                if (index != 0)
                  pagenames.put(tmp.substring(0, index), tmp
                      .substring(index + 1));
              }
            }
          else {
            while (bf.ready()) {
              tmp = bf.readLine();
              // ignore unknown, until next section
              if (tmp.charAt(0) == '=') {
                break;
              }
            }
          }
        }

      } catch (IOException ex) {
        ex.printStackTrace();
      }

    } catch (FileNotFoundException ex) {
      System.out.println("File not found");
    }

    if (!props.containsKey("channel"))
      setProperty("channel", getProperty("lang") + ".wikipedia");

  }

  /**
   * Convert encoded &#92;uxxxx to unicode chars and changes special
   * saved chars to their original forms.
   */
  public static String loadConvert(String str) {
    char ch;
    char[] a = str.toCharArray();
    int len = a.length, i = 0;
    StringBuffer outBuffer = new StringBuffer(len);

    while (i < len) {
      ch = a[i++];
      if (ch == '\\') {
        ch = a[i++];
        if (ch == 'u') {
          // Convert 4 values into one character
          int value = 0;
          for (int j = 0; j < 4; j++) {
            ch = a[i++];
            if ('0' <= ch && ch <= '9')
              value = value*16 + ch - '0';
            else if ('a' <= ch && ch <= 'f')
              value = value*16 + 10 + ch - 'a';
            else if ('A' <= ch && ch <= 'F')
              value = value*16 + 10 + ch - 'A';
            else
              throw new IllegalArgumentException(
                 "Malformed \\uxxxx encoding.");
          }
          ch = (char)value;
        }
        else {
          if (ch == 'f')
            ch = '\f';
          else if (ch == 't')
            ch = '\t';
          else if (ch == 'n')
            ch = '\n';
          else if (ch == 'r')
            ch = '\r';
        }
      }
      outBuffer.append(ch);
    }
    return outBuffer.toString();
  }
}
