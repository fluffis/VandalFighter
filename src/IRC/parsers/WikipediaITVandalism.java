/*
 WikipediaITVandalism class for CryptoDerk's Vandal Fighter
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

public class WikipediaITVandalism extends AbstractIRCParser {
  public WikipediaITVandalism() {
  }

  public Edit parse(String channel, String sender, String login,
      String hostname, String line) {
    String editsummary = "", username = "", pagename = "", url = "";

    boolean minor = false, newpage = false, moveflag = false;

    String projname = "it.wikipedia";
    long time = System.currentTimeMillis();

    // start parsing
    // remove colors

    line = Colors.removeFormattingAndColors(line);

    // set flags
    if (line.contains("Admin rimuove contributo")) {
      newpage = true;
      line = line.replaceFirst(" created ", "#");
    }
    if (line.contains("")) {
      moveflag = true;
    }

    // replace beginning spaces
    line = line.replaceAll("^ ", "");

    line = line
        .replaceFirst("^Warning! Blacklisted IP#watched page Page: ", "");
    line = line.replaceFirst("^Possible vandalismo Page: ", "");
    line = line.replaceFirst(
        "^Possibile problema di vandalismo/cancellazione Page: ", "");
    line = line
        .replaceFirst(
            "^Pagina di grandi dimensioni di utente registrato. Possibile copy vio Page: ",
            "");
    line = line.replaceFirst(
        "^Possibile vandalismo/cancellazione di anonimo  Page: ", "");
    line = line.replaceFirst("^Possibile vandalismo anonimo Page: ", "");
    line = line.replaceFirst("^Admin rimuove contributo Page: ", "");
    line = line
        .replaceFirst(
            "^Anonimo ha creato una pagina di dimensioni troppo piccole per essere un redirect Page: ",
            "");
    line = line.replaceFirst("^Admin aggiunge contributo Page: ", "");
    line = line.replaceFirst(
        "^Pagina di grandi dimensioni anonima. Possibile copy vio Page: ", "");
    line = line.replaceFirst(
        "^Warning! Blacklisted IP edited watched page Page: ", "");
    line = line.replaceFirst("^Possibile vandalismo anonimo Page: ", "");
    line = line.replaceFirst(" By: ", "#");
    line = line.replaceFirst(" Change: ", "#");

    line = line.replaceFirst(" Excuse: ", "#");
    line = line.replaceFirst(" Revert: ", "#");
    line = line.replaceFirst(" Diff: ", "#");
    line = line.replaceFirst(" bytes", "");
    line = line.replace(" #", "#");
    line = line.replace("# ", "#");
    line = line.replace("##", "#");
    line = line.replace("#?", "#");

    StringTokenizer tk;
    tk = new StringTokenizer(line, "#");
    int nrTokens = tk.countTokens();

    if (nrTokens < 5) {
      return null;
    } else {
      pagename = tk.nextToken();
      username = tk.nextToken();
      String changeS = tk.nextToken();
      tk.nextToken(); // revertURL
      url = tk.nextToken();
      if (tk.hasMoreTokens())
        editsummary = tk.nextToken();
      else
        editsummary = "";
      if (editsummary.contains("N/A"))
        editsummary = "";

      Short special = moveflag?Edit.SPECIAL_MOVE:Edit.SPECIAL_NONE;
      Edit e = new Edit(removeBraces(pagename), url, removeBraces(username),
          editsummary, changeS, minor, newpage, projname, time, special, null, true, false);
      return e;
    }
  }

  private String removeBraces(String pagename) {
    int last = pagename.lastIndexOf("]]");
    int first = pagename.indexOf("[[");
    return pagename.substring(first + 2, last);
  }
}