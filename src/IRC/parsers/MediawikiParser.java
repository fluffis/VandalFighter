/*
 MediawikiParser class for CryptoDerk's Vandal Fighter
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

package IRC.parsers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;

import org.jibble.pircbot.Colors;

import data.Configuration;
import data.Edit;
import data.MessageMatcher;

public class MediawikiParser extends AbstractIRCParser {

  private Configuration config;

  public MediawikiParser() {
    config = Configuration.getConfigurationObject();
  }

  public Edit parse(String channel, String sender, String login,
      String hostname, String line) {
    String editsummary = "", username = "", pagename = "", url = "";
    String changeS = "";

    Short special = Edit.SPECIAL_NONE;
    boolean minor = false, newpage = false, bot = false, moveflag = false, unapproved = false;

    // project name
    String projname = channel.substring(1, channel.length());

    // time
    long time = System.currentTimeMillis();

    // pagename
    int i = 8;
    while (line.charAt(i) != ']') {
      pagename += line.charAt(i);
      i++;
    }
    pagename = pagename.substring(0, pagename.length() - 3);

    String specialPage = config.getPagename(projname+".Special:Log");
    if (specialPage == null)
      specialPage = "Special:Log";

    // HACK: it.wikipedia has IRC records in form "Speciale:Log"
    // but name of page is "Speciale:Registri"
    int idot = specialPage.indexOf(':');
    String specialPage2 = specialPage.substring(0, idot) + ":Log";

    //if (pagename.contains(":Log/"))
    if (pagename.startsWith(specialPage) || pagename.startsWith(specialPage2))
      return parseSpecial(line, channel);

    // flags
    i += 5;
    {
      char ch = line.charAt(i);
      while (ch == 'N' || ch == 'M' || ch == 'B' || ch == '!') {
        if (ch == 'N') {
          i++;
          newpage = true;
        } else if (ch == 'M') {
          i++;
          minor = true;
        } else if (ch == 'B') {
          i++;
          bot = true;
        } else if (ch == '!') {
          i++;
          unapproved = true;
        } else
          break;
        ch = line.charAt(i);
      }
    }

    // URL
    i += 7;
    while (line.charAt(i) != ' ') {
      url += line.charAt(i);
      i++;
    }
    url = Colors.removeFormattingAndColors(url);

    // user name
    i += 9;
    while (i < line.length()) {
      if (line.charAt(i) == '5' && line.charAt(i + 1) == '*') {
        username = username.substring(0, username.length() - 3);
        break;
      }
      username += line.charAt(i);
      i++;
    }

    // change +/-
    i += 5;
    while (line.charAt(i) != ')') {
      changeS += line.charAt(i);
      i++;
    }
    changeS = Colors.removeFormattingAndColors(changeS);

    // edit summary
    i += 5;
    while (i < line.length()) {
      editsummary += line.charAt(i);
      i++;
    }
    editsummary = editsummary.substring(0, editsummary.length() - 1);
//    if (url.startsWith("2"))
//      System.out.println("Url:"+url);

    Edit e = new Edit(pagename, url, username, editsummary, changeS, minor,
        newpage, projname, time, special, null, false, false);
    return e;
  }

  private Edit parseSpecial(String line, String channel) {
    int i = line.indexOf('/', 8);
    i++;
    if (line.charAt(i) == 'n')
      return parseNewuser(line, channel, i);
    else if (line.charAt(i) == 'd')
      return parseDelete(line, channel, i);
    else if (line.charAt(i) == 'u')
      return parseUpload(line, channel, i);
    else if (line.charAt(i) == 'b')
      return parseBlock(line, channel, i);
    else if (line.charAt(i) == 'p')
      return parseProtect(line, channel, i);
    else if (line.charAt(i) == 'm')
      return parseMove(line, channel, i);
    else if (line.charAt(i) == 'r')
      return parseRenameUser(line, channel, i);
    else
      System.out.println(line);

    return null;
  }

  private Edit parseMove(String line, String channel, int i) {
    return parseSpecials(line, channel, Edit.SPECIAL_MOVE);

    /*
     * String username="",move1="",move2="", editsummary="";
     * System.out.println(line); while (line.charAt(i) != '3') {
     * System.out.println("to beginning: "+line.charAt(i)); i++; } i++; while (i<line.length()) {
     * if (line.charAt(i)=='5' && line.charAt(i+1)=='*') break;
     *
     * username += line.charAt(i); System.out.println("username:
     * "+line.charAt(i)); i++; } username =
     * username.substring(0,username.length()-3);
     *
     * while (line.charAt(i) !='2') { System.out.println("inbetween
     * "+line.charAt(i)); i++; } i++; while (line.charAt(i)!=']') {
     * System.out.println("move1: "+line.charAt(i)); move1+=line.charAt(i); i++;
     *  } move1 = move1.substring(0,move1.length()-3);
     *
     * while (line.charAt(i)!='[') { System.out.println("inbetween:
     * "+line.charAt(i)); i++; } i++; i++; while (line.charAt(i)!=']') {
     * System.out.println("move2: "+line.charAt(i)); move2+=line.charAt(i); i++; }
     * move2 = move2.substring(0,move2.length()-1);
     *
     * i++; i++; while (i < line.length()-1) { System.out.println("summary:
     * "+line.charAt(i)); editsummary += line.charAt(i); i++; }
     *
     * return new edit(move2, "http://"+getProj(channel)+".org/wiki/"+move2,
     * username, editsummary, 0, false, false, getProj(channel), true,
     * getTime());
     */
  }

  private Edit parseProtect(String line, String channel, int i) {
    // System.out.println(line);
    //return parseSpecials(line, channel, Edit.SPECIAL_PROTECT);
    // return new edit(pagename, url, username, editsummary, changeS, minor,
    // newpage, projname, moveflag, time);

    // System.out.println(line);
    int protectIndex = line.indexOf("protect", i+1);
    int unprotectIndex = line.indexOf("unprotect", i+1);
    int modifyIndex = line.indexOf("modify", i+1);
    int moveProtIndex = line.indexOf("move_prot", i+1);
    int starIndex = line.indexOf("*", i+1);
    if (unprotectIndex != -1 && unprotectIndex < starIndex)
      return parseSpecials(line, channel, Edit.SPECIAL_UNPROTECT);
    if (protectIndex != -1 && protectIndex < starIndex)
      return parseSpecials(line, channel, Edit.SPECIAL_PROTECT);
    if (modifyIndex != -1 && modifyIndex < starIndex)
      return parseSpecials(line, channel, Edit.SPECIAL_MODIFY_PROTECT);
    if (moveProtIndex != -1 && moveProtIndex < starIndex)
      return parseSpecials(line, channel, Edit.SPECIAL_MODIFY_PROTECT);

    System.out.println("Strange write to protect log:" + line);
    return null;
  }

  private Edit parseBlock(String line, String channel, int i) {
    // System.out.println(line);
    int blockIndex = line.indexOf("block", i+1);
    int unblockIndex = line.indexOf("unblock", i+1);
    int starIndex = line.indexOf("*", i+1);
    if (line.contains("changed"))
      blockIndex = line.indexOf("block", i+1);
    if (unblockIndex != -1 && unblockIndex < starIndex)
      return parseSpecials(line, channel, Edit.SPECIAL_UNBLOCK);
    if (blockIndex != -1 && blockIndex < starIndex)
      return parseSpecials(line, channel, Edit.SPECIAL_BLOCK);

    System.out.println("Strange write to block log:" + line);
    return null;
  }

  private Edit parseUpload(String line, String channel, int i) {
    // System.out.println(line);
    return parseSpecials(line, channel, Edit.SPECIAL_UPLOAD);
    // return new edit(pagename, url, username, editsummary, changeS, minor,
    // newpage, projname, moveflag, time);
  }

  private Edit parseDelete(String line, String channel, int i) {
    // System.out.println(line);
    int deleteIndex = line.indexOf("delete", i+1);
    int restoreIndex = line.indexOf("restore", i+1);
    int revisionIndex = line.indexOf("revision", i+1);
    int starIndex = line.indexOf("*", i+1);
    if (deleteIndex != -1 && deleteIndex < starIndex)
      return parseSpecials(line, channel, Edit.SPECIAL_DELETE);
    if (restoreIndex != -1 && restoreIndex < starIndex)
      return parseSpecials(line, channel, Edit.SPECIAL_UNDELETE);
    if(revisionIndex != -1 && revisionIndex < starIndex)
      return parseSpecials(line, channel, Edit.SPECIAL_REVISION);

    System.out.println("Strange write to delete log:" + line);
    return null;
  }

  // TODO Beren rewrite, other starts with Special:Log/ ... and this starts only with Log/
  private Edit parseNewuser(String line, String channel, int i) {
    return parseSpecials(line, channel, Edit.SPECIAL_NEWUSER);

//    i += 39;
//    String username = "";
//    while (i < line.length()) {
//      if (line.charAt(i) == '5' && line.charAt(i + 1) == '*')
//        break;
//
//      username += line.charAt(i);
//      i++;
//    }
//    username = username.substring(0, username.length() - 3);
//
//    String url = "http://" + getProj(channel) + ".org/wiki/User:"
//        + urlEncode(username);
//
//    Edit e = new Edit("Log/newusers " + username, url, username,
//        "New usercreation", 0, false, false, getProj(channel), System.currentTimeMillis());
//    e.special = Edit.SPECIAL_NEWUSER;
//    return e;
  }

  private Edit parseRenameUser(String line, String channel, int i) {
    // System.out.println(line);
    return parseSpecials(line, channel, Edit.SPECIAL_RENAME_USER);
    // return new edit(pagename, url, username, editsummary, changeS, minor,
    // newpage, projname, moveflag, time);
  }

  private Edit parseSpecials(String line, String channel, Short special) {
    String username;

    int star1, star2;
    line = Colors.removeFormattingAndColors(line);

    star1 = line.indexOf('*');
    star2 = line.indexOf('*', star1 + 1);

    username = line.substring(star1 + 2, star2 - 1);

    String projname = channel.substring(1, channel.length());
    int haak = line.indexOf("]]");
    String pagename = line.substring(2, haak);
    boolean minorflag = false;
    boolean moveflag = false;
    boolean newflag = false;
    String summary = line.substring(star2+3);
    //String summary = line.substring(haak + 2, star1 - 1)
    //    + line.substring(star2, line.length());
    //  String url = "http://" + projname + ".org/wiki/" + pagename;
    String url = "http://" + projname + ".org/w/index.php?title=" + urlEncode(pagename);
    long time = System.currentTimeMillis();
    String subject = null;

    if (special == Edit.SPECIAL_NEWUSER) {
      subject = parseSpecialParameter(projname, summary, "Newuserlog-create2-entry");
      if (subject == null) {
        subject = username;
      }
      pagename = pagename + " '" + subject + "'";

      url = "http://" + projname + ".org/w/index.php?title=Special:Contributions&target=" + urlEncode(subject);
      // [[Special:Log/newusers]] newusers * Yurpy * New user ([[User
      // talk:Yurpy|Talk]] | [[Special:Contributions/Yurpy|contribs]] |
      // [[Special:Blockip/Yurpy|block]])
      // public edit(String pagename, String url, String username, String
      // editsummary, int change, boolean minor, boolean newpage, String
      // projname, boolean moveflag, String time)
      // e= new
      // edit("Special:Log/newusers","http://"+projname+".org/wiki/User:"+username,username,"Special:Log/newuser",0,false,true,projname,
      // false,getTime());
    }
    if (special == Edit.SPECIAL_UPLOAD) {
      subject = parseSpecialParameter(projname, summary, "Uploadedimage");
      pagename = pagename + " '" + subject + "'";
      url = "http://" + projname + ".org/wiki/" + urlEncode(subject);
    }
    if (special == Edit.SPECIAL_MOVE) {
      // http://cs.wikipedia.org/w/index.php?title=Speci%C3%A1ln%C3%AD%3ALog&type=move&user=Radek+Barto%C5%A1&page=Lineage
      // [[Special:Log/move]] move_redir * Commander Keane * moved [[Pearse
      // (disambiguation)]] to [[Pearse]] over redirect: " (disambiguation)" is
      // superfluous, see [[Wikipedia:WikiProject Disambiguation/Malplaced
      // disambiguation pages|Malplaced disambiguation pages]]
      // [[Special:Log/move]] move * Commander Keane * moved [[Peek
      // (disambiguation)]] to [[Peek]]: " (disambiguation)" is superfluous, see
      // [[Wikipedia:WikiProject Disambiguation/Malplaced disambiguation
      // pages|Malplaced disambiguation pages]]
      subject = parseSpecialParameter(projname, summary, "1movedto2");
      if (subject == null)
        subject = parseSpecialParameter(projname, summary, "1movedto2_redir");
      pagename = pagename + " '" + subject + "'";
      url = "http://" + projname + ".org/wiki/" + urlEncode(subject);
      moveflag = true;
      // e= new
      // edit("Special:Log/move","http://"+projname+".org/wiki/Special:Log/move",username,"Special:Log/move",0,false,false,projname,
      // true,getTime());
    }
    if (special == Edit.SPECIAL_DELETE) {
      // [[Special:Log/delete]] delete * Vegaswikian * deleted "Tactical ops
      // assualt on terror": content was: '{{db|no meaningful content}}'''Bold
      // text'''Tactical ops assualt on Terror'
      // [[Speciaal:Log/delete]] delete * Henna * [[Minichums]] is verwijderd:
      // verwijderlijst, mag verwijderd worden 11/3
      // [[Special:Log/delete]] restore  * Mailer diablo *  restored "[[Image:Taiwan-parliament-chew.jpg]]": 3 revisions and 2 file(s) restored
      // e=new
      // edit("Special:Log/delete","http://"+projname+".org/wiki/Special:Log/delete",username,"deleted
      // for some reason",0,false,false,projname, false,getTime());

      subject = parseSpecialParameter(projname, summary, "Deletedarticle");
      pagename = pagename + " '" + subject + "'";
      url = "http://" + projname + ".org/w/index.php?title=Special%3ALog/delete&page=" + urlEncode(subject);
    }
    if (special == Edit.SPECIAL_UNDELETE) {
      // [[Special:Log/delete]] restore  * Mailer diablo *  restored "[[Image:Taiwan-parliament-chew.jpg]]": 3 revisions and 2 file(s) restored

      subject = parseSpecialParameter(projname, summary, "Undeletedarticle");
      pagename = pagename + " '" + subject + "'";
      url = "http://" + projname + ".org/wiki/" + urlEncode(subject);
    }
    if(special == Edit.SPECIAL_REVISION) {
     // [[Special:Log/delete]] revision  * Fluff *  Fluff ändrade synligheten för versioner på sidan [[Fleece]]: Opassande personupplysningar
     subject = parseSpecialParameter(projname, summary, "Revdelete-logentry");
      pagename = pagename + " '" + subject + "'";
      url = "https://" + projname + ".org/wiki/" + urlEncode(subject);
    }
    if (special == Edit.SPECIAL_BLOCK) {
      // e= new edit("Special:Log/block", line, line, line, _port, pause, pause,
      // line, pause, line);
      subject = parseSpecialParameter(projname, summary, "Blocklogentry");
      pagename = pagename + " '" + subject + "'";
      url = "http://" + projname
          + ".org/w/index.php?title=Special%3AContributions&target="
          + urlEncode(subject.substring(subject.indexOf(":")+1));
    }
    if (special == Edit.SPECIAL_UNBLOCK) {
      // e= new edit("Special:Log/block", line, line, line, _port, pause, pause,
      // line, pause, line);
      subject = parseSpecialParameter(projname, summary, "Unblocklogentry");
      pagename = pagename + " '" + subject + "'";
      url = "http://" + projname
          + ".org/w/index.php?title=Special%3AContributions&target="
          + urlEncode(subject.substring(subject.indexOf(":")+1));
    }
    if (special == Edit.SPECIAL_PROTECT) {
      // e= new edit("Special:Log/protect", line, line, line, _port, pause,
      // pause, line, pause, line);
      int limit;
      int limit1 = summary.indexOf(" [edit=");
      int limit2 = summary.indexOf(" [move=");
      int limit3 = summary.indexOf(" [create=");
      if (limit1 == -1)
        limit1 = summary.length();
      if (limit2 == -1)
        limit2 = summary.length();
      if (limit3 == -1)
        limit3 = summary.length();
      limit = Math.min(limit1, Math.min(limit2, limit3));
      String smallSummary = summary.substring(0, limit);
      subject = parseSpecialParameter(projname, smallSummary, "Protectedarticle");
      pagename = pagename + " '" + subject + "'";
      url = "http://" + projname + ".org/w/index.php?title=" + urlEncode(subject)+"&action=history";
    }

    if (special == Edit.SPECIAL_MODIFY_PROTECT) {
      // e= new edit("Special:Log/protect", line, line, line, _port, pause,
      // pause, line, pause, line);
      int limit;
      int limit1 = summary.indexOf(" [edit=");
      int limit2 = summary.indexOf(" [move=");
      int limit3 = summary.indexOf(" [create=");
      if (limit1 == -1)
        limit1 = summary.length();
      if (limit2 == -1)
        limit2 = summary.length();
      if (limit3 == -1)
        limit3 = summary.length();
      limit = Math.min(limit1, Math.min(limit2, limit3));
      String smallSummary = summary.substring(0, limit);
      subject = parseSpecialParameter(projname, smallSummary, "Modifiedarticleprotection");
      pagename = pagename + " '" + subject + "'";
      url = "http://" + projname + ".org/w/index.php?title=" + urlEncode(subject)+"&action=history";
    }

    if (special == Edit.SPECIAL_UNPROTECT) {
      subject = parseSpecialParameter(projname, summary, "Unprotectedarticle");
      pagename = pagename + " '" + subject + "'";
      url = "http://" + projname + ".org/w/index.php?title=" + urlEncode(subject)+"&action=history";
    }

    if (special == Edit.SPECIAL_RENAME_USER) {
      subject = parseSpecialParameter(projname, summary, "Renameuserlog");
      pagename = pagename + " '" + subject + "'";
      url = "http://" + projname + ".org/w/index.php?title=Special%3ALog&type=renameuser&user="
          + urlEncode(username) + "&page=User%3A" + urlEncode(subject);
    }

    Edit e = new Edit(pagename, url, username, summary, 0, minorflag, newflag,
        projname, time, special, subject, false, false);
    return e;
  }

  private String parseSpecialParameter(String projName, String summary,
      String messageName) {
    MessageMatcher matcher = config.getMessageMatcher(projName + "." + messageName);
    //TODO what to do when matcher is null?
    Properties result = matcher.match(summary);
    if (result == null || !result.containsKey("1"))
      return null;
    return result.getProperty("1");
//    String first = config.getMessage(projName + "." + messageName + ".1");
//    String second = config.getMessage(projName + "." + messageName + ".2");
//    if (second.length() == 0 && ("Protectedarticle".equals(messageName) || "Unprotectedarticle".equals(messageName)))
//      second = ":";
//    int startIndex = 0;
//    int endIndex = summary.length();
//    if (first != null && first.length() != 0) {
//      int index = summary.indexOf(first);
//      if (index == -1)
//        return null;
//      startIndex = index + first.length();
//    }
//    if (second != null && second.length() != 0) {
//      endIndex = summary.indexOf(second, startIndex);
//      if (endIndex == -1)
//        return null;
//    }
//    return summary.substring(startIndex, endIndex);
  }

  private static String urlEncode(String value) {
    value = value.replace(' ', '_');
    try {
      value = URLEncoder.encode(value, "UTF-8");
    } catch (UnsupportedEncodingException ex) {
    }
    return value;
  }
}
