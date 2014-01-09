/*
 WikipediaFRVandalism class for CryptoDerk's Vandal Fighter
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
 * Created on 14-apr-2006 TODO Beren
 * author: Finne Boonen 
 * email: finne@cassia.be
 * http://en.wikipedia.org/wiki/User:Henna
 * 
 * Changed by Beren, 12-nov-2006
 * http://cs.wikipedia.org/wiki/User:Beren
 */

package IRC.parsers;

import java.util.StringTokenizer;

import org.jibble.pircbot.Colors;

import data.Edit;

public class WikipediaFRVandalism extends AbstractIRCParser {
  public WikipediaFRVandalism() {
  }

  public Edit parse(String channel, String sender, String login,
      String hostname, String line) {
    String editsummary = "", username = "", pagename = "", url = "";

    boolean minor = false, newpage = false, moveflag = false;

    String projname = "fr.wikipedia";

    long time = System.currentTimeMillis();

    // start parsing
    // remove colors

    line = Colors.removeFormattingAndColors(line);

    // set flags
    if (line.contains("created")) {
      newpage = true;
      line = line.replaceFirst(" created ", "#");
    }
    if (line.contains("Move from")) {
      moveflag = true;
    }

    // replace human talk
    line = line.replaceFirst("^New  ", "");
    // System.out.println("test1");
    line = line.replaceFirst("^user ", "");
    // System.out.println("test2");
    line = line.replaceFirst("^IP ", "");
    // System.out.println("test3");
    line = line.replaceFirst("^Admin ", "");
    // System.out.println("test4");
    line = line.replaceFirst("^Blacklist ", "");
    // System.out.println("test4a");
    line = line.replaceFirst("^Greylist ", "");
    // System.out.println("test4b");
    line = line.replaceFirst("^User ", "");
    // System.out.println("test4c");
    line = line.replaceFirst("^Blacklist ", "");
    line = line.replaceFirst("^Whitelist ", "");

    line = line.replaceFirst("Edited watched page", "#");
    line = line.replaceFirst(", Possible gibberish? ", "#");
    line = line.replaceFirst(", Large removal, blanking? ", "#");

    line = line.replaceFirst("tiny create", "#");
    line = line.replaceFirst("Large edit ", "#");

    line = line.replaceFirst(", Large removal, Blanking?", "#");
    line = line.replaceFirst(", Large removal, blanking?", "#");

    line = line.replaceFirst("Large removal, Blanking?", "#");
    line = line.replaceFirst("Large removal, blanking?", "#");

    line = line.replaceFirst("Large removal", "#");
    line = line.replaceFirst("edited ", "#");
    line = line.replaceFirst("copyvio?  ", "#");
    line = line.replaceFirst(", blanking?", "#");
    line = line.replaceFirst(", Possible gibberish? ", "#");

    line = line.replaceFirst(" Diff: ", "#");

    line = line.replace(" #", "#");
    line = line.replace("# ", "#");
    line = line.replace("##", "#");
    line = line.replace("#?", "#");

    StringTokenizer tk;
    tk = new StringTokenizer(line, "#");
    int nrTokens = tk.countTokens();

    if (nrTokens != 3) {
      return null;
    } else {
      username = tk.nextToken();
      username = username.substring(7, username.length() - 2);

      StringTokenizer tk2;
      String token2 = tk.nextToken();
      tk2 = new StringTokenizer(token2, "(");
      pagename = tk2.nextToken();
      String changeS = tk2.nextToken();
      pagename = removeBraces(pagename);
      changeS = changeS.substring(0, changeS.length() - 1);

      String token3 = tk.nextToken();
      int space = token3.indexOf(" ");
      url = token3.substring(0, space);
      editsummary = token3.substring(space, token3.length());
      Short special = moveflag?Edit.SPECIAL_MOVE:Edit.SPECIAL_NONE;
      Edit e = new Edit(pagename, url, username, editsummary, changeS, minor,
          newpage, projname, time, special, null, true, false);
      return e;
    }
  }

  private String removeBraces(String pagename) {
    int last = pagename.lastIndexOf("]]");
    int first = pagename.indexOf("[[");
    return pagename.substring(first + 2, last);
  }
}