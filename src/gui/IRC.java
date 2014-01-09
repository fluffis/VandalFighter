/*
 IRC class for CryptoDerk's Vandal Fighter
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
 * Standalone class IRC was created on 7-mrt-2006
 * author: Finne Boonen
 * email: finne@cassia.be
 * http://en.wikipedia.org/wiki/User:Henna
 *
 * Changed by Beren, 12-nov-2006
 * http://cs.wikipedia.org/wiki/User:Beren
 */

package gui;


import IRC.bots.FreenodeBot;
import IRC.bots.MediawikiBot;
import data.Configuration;
import data.Data;
import java.util.ResourceBundle;


/**
 * @author Finne
 *
 */
public class IRC implements Runnable {

  public Thread startThread;

  MediawikiBot wikimediaBot;

  FreenodeBot freenodeBot;

  Configuration config;

  ResourceBundle messages;

  String wikimediaChannels, server, port;

  String mediawikiEnc = "UTF-8";

  String freenodeEnc = "UTF-8";

  String nick;

  Data data;

  StatusListener listener;

  public void setWikimediaChannels(String wikimediaChannels) {
    this.wikimediaChannels = wikimediaChannels;
  }

  private String createNick() {
    /*            if (config.containsKey("collaborationNick"))
     return "vf_"+config.getProperty("collaborationNick");
     else
     {*/
    String tstr = "" + System.currentTimeMillis();
    String nick = "vf" + config.verint + tstr.substring(8);

    return nick;
  }//}

  public void sendMsg(String proj, String msg) {
    freenodeBot.sendMsg(proj, msg);
  }

  public IRC(String c, String se, String p, Data d, StatusListener l) {
    super();
    data = d;
    config = Configuration.getConfigurationObject();
    messages = ResourceBundle.getBundle("MessagesBundle", config.currentLocale);
    listener = l;
    nick = createNick();

    wikimediaChannels = c;
    server = se;
    port = p;
  }

  public void printStatus() {
    String[] chs;
    if (wikimediaBot != null) {
      System.out.println("wikimediaBot connected    : "
          + wikimediaBot.isConnected());
      System.out.println("wikimediaBot connected to : "
          + wikimediaBot.getServer());
      chs = wikimediaBot.getChannels();
      for (int j = 0; j < chs.length; j++) {
        System.out.println("wikimediaBot at           : " + chs[j]);
      }
    } else {
      System.out.println("wikimediaBot not connected");
    }

    System.out.println("\n");
    if (freenodeBot != null) {
      System.out.println("freenodeBot connected    : "
          + freenodeBot.isConnected());
      System.out.println("freenodeBot connected to : "
          + freenodeBot.getServer());
      chs = freenodeBot.getChannels();
      for (int j = 0; j < chs.length; j++) {
        System.out.println("freenodeBot at           : " + chs[j]);
      }
    } else {
      System.out.println("freenodeBot not connected");
    }
  }

  public void joinFreenodeChannels() {
    freenodeBot.clearChannels();
    freenodeBot.addChannelToResolve("#vandalism-en-wp-2", config.getBooleanProp("vandalism_en_box"));
    freenodeBot.addChannelToResolve("#wikipedia-nl-vandalism", config.getBooleanProp("vandalism_nl_box"));
    freenodeBot.addChannelToResolve("#wikipedia-it-vandalism", config.getBooleanProp("vandalism_it_box"));
    freenodeBot.addChannelToResolve("#vandalism-fr-wp", config.getBooleanProp("vandalism_fr_box"));
    freenodeBot.addChannelToResolve("#wikipedia-cs-vandalfighter", config.getBooleanProp("vandalism_cs_box"));
  }

  // TODO Beren it must be completely rewritten.
  // If there are connections to servers and user press connect button,
  // no changes are propagated, no channels or servers are replaced/(re)joined
  public void connect() {
    int iPort = 6667;
    if (wikimediaBot == null || !wikimediaBot.isConnected()) {
      try {
        iPort = Integer.parseInt(port);
      } catch (Exception e) {
        System.out.println("IRC.java, problem parsing integer: "
            + e.getMessage());
      }

      String[] channels = wikimediaChannels.split(",");

      if (wikimediaBot == null)
        wikimediaBot = new MediawikiBot(server, iPort, nick, channels, mediawikiEnc, data, listener);
      else
        wikimediaBot.setValues(server, iPort, nick, channels, mediawikiEnc, data, listener);
      wikimediaBot.connect();
    }

    if ((freenodeBot == null || !freenodeBot.isConnected()) && checkFreenodeBoxes()) {
      String freenodeNick = config.getProperty("FreenodeNick");
      if (freenodeNick == null || freenodeNick.trim().length() == 0)
        freenodeNick = createNick();
      freenodeBot = new FreenodeBot("irc.freenode.org", iPort, freenodeNick, freenodeEnc, data, listener);
      joinFreenodeChannels();
      freenodeBot.connect();
    }
  }

  /**
   */
  private boolean checkFreenodeBoxes() {
    return config.getBooleanProp("vandalism_en_box") ||
           config.getBooleanProp("vandalism_nl_box") ||
           config.getBooleanProp("vandalism_it_box") ||
           config.getBooleanProp("vandalism_fr_box") ||
           config.getBooleanProp("vandalism_cs_box");
  }

  public void run() {
    connect();
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    printStatus();
  }

  public void quit(String string) {
    if (freenodeBot != null)
      freenodeBot.quit(string);
    if (wikimediaBot != null)
      wikimediaBot.quit(string);

    freenodeBot = null;
    wikimediaBot = null;

    listener.updateStatus(messages.getString("StatusUpdateQuitIRC"));
  }

  public boolean isConnected() {

    return (wikimediaBot != null && wikimediaBot.isConnected())
        || (freenodeBot != null && freenodeBot.isConnected());
  }

  public String getFreenodeUserName() {
    return (freenodeBot != null)?freenodeBot.getNick():null;
  }

  public void updateConfig() {
    if (wikimediaBot != null) {
      String[] channels = wikimediaBot.getChannels();
      String channelStr = "";
      for (int i = 0; i < channels.length; i++)
        if (i == 0)
          channelStr += channels[i];
        else
          channelStr += ", " + channels[i];
      config.setProperty("channel", channelStr);
    }

    if (freenodeBot != null) {
      String[] vchannels = freenodeBot.getChannels();
      String vchannelStr = "";
      for (int i = 0; i < vchannels.length; i++)
        if (i == 0)
          vchannelStr += vchannels[i];
        else
          vchannelStr += ", " + vchannels[i];

      config.setProperty("vandalismchannel", vchannelStr);
    }
  }
}
