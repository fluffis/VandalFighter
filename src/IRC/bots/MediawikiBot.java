/*
 MediawikiBot class for CryptoDerk's Vandal Fighter
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
 * by Criptoderk as inner class vf$IRC
 *
 * Standalone class was created on march 2006   TODO Beren
 * author: Finne Boonen
 * email: finne@cassia.be
 * http://en.wikipedia.org/wiki/User:Henna
 *
 * Changed by Beren, 12-nov-2006
 * http://cs.wikipedia.org/wiki/User:Beren
 */

/**
 * @author Finne Boonen
 * finne@cassia.be
 * march 2006
 */

package IRC.bots;

import gui.StatusListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Vector;

import org.jibble.pircbot.Colors;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;

import IRC.parsers.AbstractIRCParser;
import IRC.parsers.MediawikiParser;
import IRC.parsers.MessageHandler;
import IRC.parsers.MessageLoader;
import IRC.parsers.NamespaceLoader;
import IRC.parsers.PagenameHandler;
import IRC.parsers.PagenameLoader;

import data.Data;
import data.Configuration;
import data.Edit;
import data.Feeder;

public class MediawikiBot extends PircBot implements MessageHandler, PagenameHandler {
  private Properties props;

  private Configuration config;

  ResourceBundle messages;

  private boolean quited = false;

  String server;

  int port;

  String[] channels;

  String encoding;

  Data data;

  StatusListener listener;

  private Vector pausedEdits;

  private AbstractIRCParser parser;

  public MediawikiBot(String server, int port, String nick, String[] channels,
      String encoding, Data data, StatusListener listener)

  {
    this.server = server;
    this.port = port;
    this.setName(nick);
    this.channels = channels;
    try {
      setEncoding(encoding);
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    this.data = data;
    this.listener = listener;

    config = Configuration.getConfigurationObject();
    messages = ResourceBundle.getBundle("MessagesBundle", config.currentLocale);

    parser = new MediawikiParser();
    props = new Properties();
    props.setProperty("#nl.wikipedia_Special", "Speciaal");
    props.setProperty("#en.wikipedia_del1", "deleted \"");
    props.setProperty("#nl.wikipedia_del1", "\\[\\[");
    props.setProperty("#nl.wikipedia_del2", "\\]\\] is verwijderd:");
    props.setProperty("#en.wikipedia_del2", "\"\\:");
    pausedEdits = new Vector();

    setVersion(messages.getString("SoftwareName")+config.version);
  }

  public void setValues(String server, int port, String nick,
      String[] channels, String encoding, Data data, StatusListener listener) {
    this.server = server;
    this.port = port;
    this.setName(nick);
    this.channels = channels;
    try {
      setEncoding(encoding);
    } catch (UnsupportedEncodingException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    this.data = data;
  }

  public void onMessage(String channel, String sender, String login,
      String hostname, String line) {
    Edit e = parser.parse(channel, sender, login, hostname, line);
    Feeder.getFeeder().addEdit(e);
  }

  public synchronized void connect() {
    if (!isConnected()) {
      try {
        System.out.println("MediawikiBot: connecting to " + server);
        connect(server, port);
        System.out.println("MediawikiBot connected to server.");
        listener.updateStatus(messages.getString("StatusUpdateConnectedToServer"));

        for (int i = 0; i < channels.length; i++) {
          if (channels[i].charAt(0) != '#') {
            channels[i] = '#' + channels[i].trim();
          }

          joinChannelM(channels[i]);
          listener.updateStatus(messages.getString("StatusUpdateJoined"));
        }
      } catch (NickAlreadyInUseException e) {
        String nick = createNick();
        setName(nick);
        changeNick(nick);
      } catch (IOException e) {
        System.out.println("MediawikiBot: IOException during connecting: "+e.getMessage());
      } catch (IrcException e) {
        System.out.println("MediawikiBot: IrcException during connecting: "+e.getMessage());
      }
    } else {
      System.out.println("MediawikiBot is already connected.");
    }
  }

  private void joinChannelM(String channel) {
    joinChannel(channel);
    checkDelimiters(channel);
  }

  private void checkDelimiters(String channel) {
    String project = channel.substring(1);
    long millis = 0;
    try {
      millis = Long.parseLong(config.getMessage("millis."+project));
    } catch (NumberFormatException e) {
    } catch (NullPointerException e) {
    }

    boolean rewrite = (System.currentTimeMillis() - millis) > 1000*60*60*24;
    checkMessage(project, "Deletedarticle", rewrite);
    checkMessage(project, "Undeletedarticle", rewrite);
    checkMessage(project, "Protectedarticle", rewrite);
    checkMessage(project, "Unprotectedarticle", rewrite);
    checkMessage(project, "Modifiedarticleprotection", rewrite);
    checkMessage(project, "1movedto2", rewrite);
    checkMessage(project, "1movedto2_redir", rewrite);
    checkMessage(project, "Blocklogentry", rewrite);
    checkMessage(project, "Unblocklogentry", rewrite);
    checkMessage(project, "Uploadedimage", rewrite);
    checkMessage(project, "Renameuserlog", rewrite);
    checkMessage(project, "Revertpage", rewrite);
    checkMessage(project, "Newuserlog-create2-entry", rewrite);
    checkMessage(project, "Revdelete-logentry", rewrite);
    checkMessage(project, "Reblock-logentry", rewrite);
    config.setMessage("millis."+project, ""+System.currentTimeMillis());

    if (config.getNamespace(project+".2") == null || rewrite) {
      new NamespaceLoader(project, this);
    }

    String page = "Special:Log";
    if (config.getPagename(project+"." + page) == null || rewrite) {
      new PagenameLoader(project, page, this);
    }
  }

  public void handleMessage(String project, String messageName,
      String messageValue) {
    config.setMessage(project + "." + messageName, messageValue);
  }

  public void handlePagename(String project, String pageName,
      String localPagename) {
    config.setPagename(project + "." + pageName, localPagename);
  }

  private void checkMessage(String project, String messageName, boolean rewrite) {
    String message = config.getMessage(project + "." + messageName);
    if (message == null || message.length() == 0 || rewrite)
      new MessageLoader(project, messageName, this);
  }

  private String createNick() {
    /*
     * if (config.containsKey("collaborationNick")) return
     * "vf_"+config.getProperty("collaborationNick"); else {
     */
    String tstr = "" + System.currentTimeMillis();
    String nick = "vf" + config.verint + tstr.substring(8);

    return nick;
  }// }

  public void quit(String reason) {
    quited = true;
    super.quitServer(reason);
  }


  /*
   * public void myConnect(String s, int p) { // server =s; // port=p; try {
   * super.connect(s,p); System.out.println("connected to irc"); } catch
   * (NickAlreadyInUseException e) { //TODO nicyfi
   * this.changeNick(createNick());
   *
   *  } catch (IOException e) { System.out.println("ioexception"); } catch
   * (IrcException e) { System.out.println("ircexception"); } if
   * (channels!=null) { System.out.println("channels:");
   *
   * for (int i=0; i<channels.length; i++) { System.out.println(channels[i]);
   *
   * joinChannel(channels[i]); }
   *  } else System.out.println("no channels for joining");
   *  } private String createNick() { if
   * (config.containsKey("collaborationNick")) return
   * "vf_"+config.getProperty("collaborationNick"); else { String tstr = "" +
   * System.currentTimeMillis(); String nick ="vf" + config.verint +
   * tstr.substring(8);
   *
   * return nick; }//}
   */
  // private String server;
  // private int port;
  protected void onDisconnect() {
    super.onDisconnect();
    int delay = 15000;
    while (!isConnected() && !quited) {
      System.out.println("MediawikiBot: reconnecting... time: "
          + System.currentTimeMillis());
      connect();
      if (!isConnected())
        try {
          Thread.sleep(delay);
          if (delay < 120000)
            delay += 10000;
        } catch (InterruptedException e) {
        }
    }
    if (quited)
      dispose();
  }

  /*
   * @Override protected void onJoin(String channel, String sender, String
   * login, String hostname) { // TODO Auto-generated method stub
   * System.out.println("==========================joined a channel!"); channels =
   * getChannels(); super.onJoin(channel, sender, login, hostname);
   *  }
   */
}
