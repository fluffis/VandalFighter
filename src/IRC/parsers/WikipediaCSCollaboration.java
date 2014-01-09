/*
 WikipediaCSCollaboration class for CryptoDerk's Vandal Fighter
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
 * Standalone file WikipediaENVandalism was created on 14-apr-2006 TODO Beren
 * author: Finne Boonen 
 * email: finne@cassia.be
 * http://en.wikipedia.org/wiki/User:Henna
 * 
 * The code was used to create WikipediaCSCollaboration class by Beren, 12-nov-2006
 * http://cs.wikipedia.org/wiki/User:Beren
 */

package IRC.parsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jibble.pircbot.Colors;

import data.Edit;

public class WikipediaCSCollaboration extends AbstractIRCParser {
  
  // TODO remove ? used only for compatibility purposes
  private static final Pattern okPattern = Pattern.compile("^Checked OK ([a-z\\.]*) \\[\\[([^\\]]*)\\]\\] (http://[^ ]*)");
  private static final Pattern warningPattern = Pattern.compile("^Checked Warning ([a-z\\.]*) \\[\\[([^\\]]*)\\]\\] (http://[^ ]*)");
  
  public WikipediaCSCollaboration() {
  }

  public Edit parse(String channel, String sender, String login,
      String hostname, String line) {
    String editsummary = "", username = " ", pagename = "", url = "", subject = "";

    boolean minor = false, newpage = false;

    String projname = "";
    long time = System.currentTimeMillis();

    // start parsing
    // remove colors

    line = Colors.removeFormattingAndColors(line);
    
    Short special = Edit.SPECIAL_NONE;
    // set flags
    Matcher m = null;
    
    m = okPattern.matcher(line);
    if (m.find()) {
      special = Edit.SPECIAL_COLLABORATION_OK;
    } else {
      m = warningPattern.matcher(line);
      if (m.find()) {
        special = Edit.SPECIAL_COLLABORATION_WARNING;
        
      } else {
        return null;
      }
    }
    projname = m.group(1);
    pagename = m.group(2);
    url = m.group(3);
//    
//    if (line.contains("Checked OK [[")) {
//      newpage = true;
//      line = line.replaceFirst("Checked OK \\[\\[", "");
//      special = Edit.SPECIAL_COLLABORATION_OK;
//    } else if (line.contains("Checked Warning [[")) {
//      newpage = true;
//      line = line.replaceFirst("Checked Warning \\[\\[", "");
//      special = Edit.SPECIAL_COLLABORATION_WARNING;
//    } else {
//      return null;
//    }
//
//    int position = line.indexOf("]]");
//    pagename = line.substring(0, position);
//    url = line.substring(position + 3);

    return new Edit(pagename, url, username, editsummary, 0, minor, newpage,
        projname, time, special, sender, false, true);
  }

//  private String removeBraces(String pagename) {
//    int last = pagename.lastIndexOf("]]");
//    int first = pagename.indexOf("[[");
//    return pagename.substring(first + 2, last);
//  }
}