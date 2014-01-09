/*
 FreenodeBot class for CryptoDerk's Vandal Fighter
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Vector;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;

import IRC.parsers.AbstractIRCParser;
import IRC.parsers.WikipediaCSCollaboration;
import IRC.parsers.WikipediaENVandalism;
import IRC.parsers.WikipediaFRVandalism;
import IRC.parsers.WikipediaITVandalism;
import IRC.parsers.WikipediaNlVandalism;

import data.Data;
import data.Configuration;
import data.Edit;
import data.Feeder;

public class FreenodeBot extends PircBot {
  String server;

  int port;

  private Properties props;

  private Configuration config;

  ResourceBundle messages;

  Data data;

  StatusListener listener;

  boolean pause = false;

  private boolean quited = false;

  private Vector pausedEdits;

  private AbstractIRCParser nl, en, it, fr, cs;

  private List channelsToResolve = new ArrayList();

  public FreenodeBot(String server, int port, String nick, String encoding,
      Data d, StatusListener listener) {
    this.setName(nick);
    this.server = server;
    this.port = port;
    config = Configuration.getConfigurationObject();
    messages = ResourceBundle.getBundle("MessagesBundle", config.currentLocale);
    data = d;
    this.listener = listener;
    nl = new WikipediaNlVandalism();
    en = new WikipediaENVandalism();
    it = new WikipediaITVandalism();
    fr = new WikipediaFRVandalism();
    cs = new WikipediaCSCollaboration();
    try {
      setEncoding(encoding);
    } catch (UnsupportedEncodingException e) {
      System.out.println("Wrong encoding at FreenodeBot: " + encoding);
    }

    props = new Properties();
    props.setProperty("#nl.wikipedia_Special", "Speciaal");
    props.setProperty("#en.wikipedia_del1", "deleted \"");
    props.setProperty("#nl.wikipedia_del1", "\\[\\[");
    props.setProperty("#nl.wikipedia_del2", "\\]\\] is verwijderd:");
    props.setProperty("#en.wikipedia_del2", "\"\\:");
    pausedEdits = new Vector();

    setVersion(messages.getString("SoftwareName")+config.version);
  }

  public void sendMsg(String proj, String msg) {

    if (proj.startsWith("nl")) {
      sendMessage("#wikipedia-vandalism-nl", msg);
    }
    if (proj.startsWith("cs")) {
      sendMessage("#wikipedia-cs-vandalfighter", msg);
    }
  }

  public void onMessage(String channel, String sender, String login,
      String hostname, String line) {
    Edit e = parse(channel, sender, login, hostname, line);
    Feeder.getFeeder().addEdit(e);
  }

  public void addChannelToResolve(String channel, boolean status) {
    Object[] array = { channel, new Boolean(status) };
    channelsToResolve.add(array);
  }

  public void clearChannels() {
    channelsToResolve = new ArrayList();
  }

  public void resolveChannels() {
    for (Iterator iter = channelsToResolve.iterator(); iter.hasNext();) {
      Object[] array = (Object[]) iter.next();
      String channel = (String) array[0];
      Boolean status = (Boolean) array[1];
      if (status.booleanValue())
        joinChannel(channel);
      else
        partChannel(channel);
    }
  }

  private Edit parse(String channel, String sender, String login,
      String hostname, String line) {
    if (channel.contains("wikipedia-nl-vandalism"))
      return nl.parse(channel, sender, login, hostname, line);
    else if (channel.contains("vandalism-en-wp-2"))
      return en.parse(channel, sender, login, hostname, line);
    else if (channel.contains("vandalism-fr-wp"))
      return fr.parse(channel, sender, login, hostname, line);
    else if (channel.contains("wikipedia-it-vandalism"))
      return it.parse(channel, sender, login, hostname, line);
    else if (channel.contains("wikipedia-cs-vandalfighter"))
      return cs.parse(channel, sender, login, hostname, line);
    else
      System.out.println("weird channel: " + channel);
    return null;
  }

  public synchronized void connect() {
    if (!isConnected()) {
      try {
        // freenodeBot.changeNick(createNick());
        System.out.println("FreenodeBot: connecting to " + server);

        connect(server, port);
        listener.updateStatus(messages.getString("StatusUpdateConnectedToServer"));

        resolveChannels();
        listener.updateStatus(messages.getString("StatusUpdateJoined"));
      } catch (NickAlreadyInUseException e) {
        // TODO nicyfi
        String freenodeNick = config.getProperty("FreenodeNick");
        if (freenodeNick == null || freenodeNick.trim().length() == 0)
          freenodeNick = createNick();
        else
          freenodeNick = getName() + "_";

        changeNick(freenodeNick);
        setName(freenodeNick);

        disconnect();
        listener.updateStatus(messages.getString("StatusUpdateNickUsed"));
      } catch (IOException e) {
        System.out.println("FreenodeBot: IOException during connecting: "+e.getMessage());
      } catch (IrcException e) {
        System.out.println("FreenodeBot: IrcException during connecting: "+e.getMessage());
      }
    } else {
      System.out.println("FreenodeBot is already connected.");
    }
  }

  protected void onDisconnect() {
    super.onDisconnect();
    int delay = 15000;
    while (!this.isConnected() && !quited) {
      System.out.println("Freenode: reconnecting... time: "
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

  public void quit(String reason) {
    quited = true;
    super.quitServer(reason);
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
}
