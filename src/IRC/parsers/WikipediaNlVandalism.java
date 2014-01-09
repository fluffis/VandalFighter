/*
 WikipediaNLVandalism class for CryptoDerk's Vandal Fighter
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
 * Created on Feb 27, 2006
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

public class WikipediaNlVandalism extends AbstractIRCParser {

  public WikipediaNlVandalism() {
  }

  public Edit parse(String channel, String sender, String login,
      String hostname, String line) {
    String editsummary = "", username = "", pagename = "", url = "";

    boolean minor = false, newpage = false, newuser = false;

    String projname = "nl.wikipedia";
    long time = System.currentTimeMillis();

    // start parsing
    // remove colors

    line = Colors.removeFormattingAndColors(line);
    // set flags
    if (line.contains("maakt een grote pagina aan")) {
      newpage = true;
      line = line.replaceFirst("maakt een grote pagina aan - Pagina:", "#");
    }
    if (line.contains("maakt een pagina aan")) {
      newpage = true;
      line = line.replaceFirst("maakt een pagina aan - Pagina:", "#");
    }
    if (line.contains("maakt een kleine pagina aan - Pagina:")) {
      newpage = true;
      line = line.replaceFirst("maakt een kleine pagina aan - Pagina:", "#");
    }
    if (line.contains("Nieuwe Gebruiker")) {
      newuser = true;

      line = line.replaceFirst("^Nieuwe Gebruiker", "Gebruiker");
    }
    // replace human talk

    line = line.replaceFirst("^Waarschuwing! ", "");
    line = line.replaceFirst("^Blacklisted ", "");
    line = line.replaceFirst("^IP-adres", "#");
    line = line.replaceFirst("^Moderator", "#");

    line = line.replaceFirst("^Gebruiker", "#");
    line = line.replaceFirst("voegt veel tekst toe - Pagina:", "#");
    line = line.replaceFirst("bewerkt een pagina - Pagina:", "#");
    line = line.replaceFirst("Link:", "#");
    line = line.replaceFirst("Veranderd:", "#");
    line = line.replaceFirst("bytes  Samenvatting:", "#");
    line = line.replaceFirst("bytes", "#");
    line = line.replaceFirst("Samenvatting:", "#");
    line = line.replaceFirst("haalt mogelijk pagina leeg - Pagina:", "#");
    line = line.replaceFirst("bewerkt een gevolgde pagina - Pagina:", "#");
    line = line.replace(" #", "#");
    line = line.replace("# ", "#");

    StringTokenizer tk;
    tk = new StringTokenizer(line, "#");
    int nrTokens = tk.countTokens();

    if (nrTokens < 4) {
      return null;
    } else {
      username = tk.nextToken();
      pagename = tk.nextToken();
      url = tk.nextToken();
      String changeS = tk.nextToken();
      if (tk.hasMoreTokens()) {
        editsummary = tk.nextToken();
      }

      Short special = newuser?Edit.SPECIAL_NEWUSER:Edit.SPECIAL_NONE;
      Edit e = new Edit(pagename, url, username, editsummary, changeS, minor,
          newpage, projname, time, special, null, true, false);
      return e;
    }
  }

}