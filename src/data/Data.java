/*
 Data class for CryptoDerk's Vandal Fighter
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
 * by Criptoderk as methods vf.addnewtableentry(..), vf.watched(..),
 * vf.whitelisted(..), vf.isValidIP(..)
 * 
 * Standalone file was created on Feb 27, 2006
 * author: Finne Boonen 
 * email: finne@cassia.be
 * http://en.wikipedia.org/wiki/User:Henna
 * 
 * Changed by Beren, 12-nov-2006
 * http://cs.wikipedia.org/wiki/User:Beren
 */

package data;

import gui.ListIndicator;
import gui.TypeIndicator;
import gui.WarningIndicator;

import java.awt.Toolkit;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;
import java.util.regex.Pattern;


/**
 * @author Finne Boonen
 * 
 */
public class Data {
  
  private static final Pattern PATTERN_IP
    = Pattern.compile("\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.)"
        + "{3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b");
  
  boolean instance_flag = false;

  Configuration config;

  public ListModel editstableModel;

  public ListModel backendModel;

  Listener l;

  public Vector pausededits;

  private static Data ref;
  
  Set newUsers = Collections.synchronizedSet(new LinkedHashSet());

  private Data() {
    config = Configuration.getConfigurationObject();
  }

  public static Data getDataObject() {
    if (ref == null)
      // it's ok, we can call this constructor
      ref = new Data();
    return ref;
  }

  
  
  public void addListener(Listener li) {
    l = li;
  }

  public void addnewtableentry(Edit e) {
    if (e == null)
      return;
    // System.out.println("Entry: " + e.toString());
    int cols = config.totalcols;

    Object tablerow[] = new Object[cols];

    synchronized (config.editstableTable.getTreeLock()) {
      if (e.isCollaborationEdit()) {
        if (e.special == Edit.SPECIAL_COLLABORATION_OK) {
          int a = editstableModel.getRowCount();
          for (int i = 0; i < a; i++) {
            if (((String) (editstableModel.getValueAt(i, config.urlcol)))
                .startsWith(e.url)) {
              editstableModel.removeRow(i);
              break;
            }
          }
          return;
        } else if (e.special == Edit.SPECIAL_COLLABORATION_WARNING) {
          String elem = e.pagename + "#" + e.projname;
          if (config.getBooleanProp("watchwarned")) {
            if (config.getBooleanProp("watchwarnedpermButton")) {
              if (!config.watchlistModel.contains(elem))
                config.watchlistModel.addElement(elem);
            } else {
              if (!config.tempwatchlistModel.contains(elem))
                config.tempwatchlistModel.addElement(elem);
            }
          }
            
          int a = editstableModel.getRowCount();
          for (int i = 0; i < a; i++) {
            if (e.projname.equals(editstableModel.getValueAt(i, config.projectcol))
                && e.pagename.equals(editstableModel.getValueAt(i, config.articlecol))) {
              if (config.getBooleanProp("watchwarned")) {
                if (config.getBooleanProp("watchwarnedpermButton")) {
                  ListIndicator li = (ListIndicator)editstableModel.getValueAt(i, config.listcol);
                  editstableModel.setValueAt(li.addList(ListIndicator.WATCH),i, config.listcol);
                } else {
                  ListIndicator li = (ListIndicator)editstableModel.getValueAt(i, config.listcol);
                  editstableModel.setValueAt(li.addList(ListIndicator.TWATCH),i, config.listcol);
                }
              }
              if (e.url.equals(editstableModel.getValueAt(i, config.urlcol))) {
                editstableModel.setValueAt(new WarningIndicator("!", e.subject),i, config.xcol);
                if (config.getBooleanProp("beepcollaborationwarning"))
                  Toolkit.getDefaultToolkit().beep(); 
              }
            }
          }
          return;
        }
      }

      int listIndicator = 0;
      // ADDING new data to table
      // START ROW SETUP
      tablerow[config.sectimecol] = new Long(e.time);
      tablerow[config.urlcol] = e.url;
      tablerow[config.projectcol] = e.projname;
      tablerow[config.xcol] = WarningIndicator.NONE;
      tablerow[config.timecol] = getTime(e.time);
      tablerow[config.articlecol] = e.pagename;

      tablerow[config.editorcol] = e.username;
      if (config.getBooleanProp("stripformatting")) {

        tablerow[config.summarycol] = e.editsummary
            .replaceAll(
                "\\u005B\\u005B([^\\u007C\\u005B\\u005D]*)\\u007C([^\\u007C\\u005B\\u005D]*)\\u005D\\u005D",
                "$2")
            .replaceAll(
                "\\u005B\\u005B([^\\u007C\\u005B\\u005D]*)\\u005D\\u005D", "$1");
      } else {
        tablerow[config.summarycol] = e.editsummary;
      }

      tablerow[config.typecol] = TypeIndicator.create(e.minor, e.newpage);
      tablerow[config.specialcol] = e.special;
      tablerow[config.subjectcol] = e.subject;
      tablerow[config.changedcol] = new Integer(e.change);
      tablerow[config.riskcol] = new Integer(e.risk);
      if (config.blacklistModel.contains(e.username + "#" + e.projname))
        listIndicator = listIndicator | ListIndicator.BLACK;
      else {
        int a = config.regexpblackModel.getSize();
        for (int j = 0; j < a; j++)
          if (config.getBooleanProp("rblackuser")
              && e.username.matches((String) config.regexpblackModel
                  .getElementAt(j))) {
            listIndicator = listIndicator | ListIndicator.RBLACK;
            break;
          }
      }
      // TODO hoe werkt dit?
      // watchlisted page -- either in the explicit list or covered by .regexp
      // blacklist on page or summary
      if (config.watchlistModel.contains(e.pagename + "#" + e.projname))
        listIndicator = listIndicator | ListIndicator.WATCH;
      else {
        int a = config.regexpblackModel.getSize();
        for (int j = 0; j < a; j++)
          if ((config.getBooleanProp("rblackpage") && e.pagename
              .matches((String) config.regexpblackModel.getElementAt(j)))
              || (config.getBooleanProp("rblacksummary") && e.editsummary
                  .matches((String) config.regexpblackModel.getElementAt(j)))
              || (config.getBooleanProp("rblackuser") && e.username
                  .matches((String) config.regexpblackModel.getElementAt(j)))    ) {
            listIndicator = listIndicator | ListIndicator.RBLACK;
            break;
          }
      }

      // temp blacklist
      if (config.tempblacklistModel.contains(e.username + "#" + e.projname))
        listIndicator = listIndicator | ListIndicator.TBLACK;
      
      // temp watchlist
      if (config.tempwatchlistModel.contains(e.pagename + "#" + e.projname))
        listIndicator = listIndicator | ListIndicator.TWATCH;
      
      tablerow[config.listcol] = ListIndicator.create(listIndicator);
      // END ROW SETUP
      backendModel.insertRow(0, tablerow);

      // *
      // * This is to keep the program from eating up ungodly amounts of memory.
      // * People can easily clear the displayed table by clicking a button, but
      // * since this backend table will only realistically be used for short
      // * periods of time, it can stay "small". It could likely be dropped down
      // * to 1000 entries and still have the same effect.
      //
      if (backendModel.getRowCount() > 5000)
        backendModel.setRowCount(4000);

      if (e.special == Edit.SPECIAL_NEWUSER) {
        synchronized (newUsers) {
          newUsers.add(e.subject + "#" + e.projname);
          if (newUsers.size() > 5000) {
            List l = new ArrayList(newUsers).subList(1000,newUsers.size());
            newUsers.clear();
            newUsers.addAll(l);
          }
        }
        
        if (config.getBooleanProp("registerdeleted")) {
          return;
        }
      }

      // If a page is deleted then it removes it from the list, if the user
      // has the option turned on
      if (e.special == Edit.SPECIAL_DELETE
          && config.getBooleanProp("olddeleted")) {
        String tempstr = e.subject;
        int a = editstableModel.getRowCount();
        for (int i = 0; i < a; i++) {
          if (e.projname.equals(editstableModel
              .getValueAt(i, config.projectcol))
              && (tempstr.equals(editstableModel.getValueAt(i,
                  config.articlecol)) || (editstableModel.getValueAt(i,
                  config.specialcol) == Edit.SPECIAL_UPLOAD && tempstr
                  .equals(editstableModel.getValueAt(i, config.subjectcol))))) {
            editstableModel.removeRow(i);
            break;
          }
        }
        if (config.getBooleanProp("watchdeleted")
            && !config.watchlistModel.contains(tempstr + "#" + e.projname)
            && !config.tempwatchlistModel.contains(tempstr + "#" + e.projname)) {
          if (config.getBooleanProp("watchdelpermButton")) {
            config.watchlistModel.addElement(tempstr + "#" + e.projname);
            a = editstableModel.getRowCount();
            for (int i = 0; i < a; i++) {
              if (e.projname.equals(editstableModel.getValueAt(i, config.projectcol))
                  && e.pagename.equals(editstableModel.getValueAt(i, config.articlecol))) {
                ListIndicator li = (ListIndicator)editstableModel.getValueAt(i, config.listcol);
                editstableModel.setValueAt(li.addList(ListIndicator.WATCH),i, config.listcol);
              }
            }
          } else {
            config.tempwatchlistModel.addElement(tempstr + "#" + e.projname);
            a = editstableModel.getRowCount();
            for (int i = 0; i < a; i++) {
              if (e.projname.equals(editstableModel.getValueAt(i, config.projectcol))
                  && e.pagename.equals(editstableModel.getValueAt(i, config.articlecol))) {
                ListIndicator li = (ListIndicator)editstableModel.getValueAt(i, config.listcol);
                editstableModel.setValueAt(li.addList(ListIndicator.TWATCH),i, config.listcol);
              }
            }
          }
        }
        a = backendModel.getRowCount();
        if (config.getBooleanProp("blackdeleted")) {
          for (int i = 0; i < a; i++) {
            if (((TypeIndicator) backendModel.getValueAt(i, config.typecol))
                .isNewpage()
                && // new page
                tempstr.equals((String) backendModel.getValueAt(i,
                    config.articlecol))
                && // same article
                e.projname.equals((String) backendModel.getValueAt(i,
                    config.projectcol))
                && // same project
                !config.whitelistModel.contains((String) backendModel
                    .getValueAt(i, config.editorcol)
                    + "#" + e.projname)
                && // don't blacklist a whitelisted editor
                !config.blacklistModel.contains((String) backendModel
                    .getValueAt(i, config.editorcol)
                    + "#" + e.projname)
                && // don't blacklist an already blacklisted editor
                !config.tempwhitelistModel.contains((String) backendModel
                    .getValueAt(i, config.editorcol)
                    + "#" + e.projname)
                && // don't blacklist a whitelisted editor
                !config.tempblacklistModel.contains((String) backendModel
                    .getValueAt(i, config.editorcol)
                    + "#" + e.projname)) // don't blacklist an
            // already blacklisted
            // editor
            {
              String myeditor = (String) backendModel.getValueAt(i, config.editorcol);
              if (config.getBooleanProp("delpagepermButton")) {
                config.blacklistModel.addElement(myeditor
                    + "#" + e.projname);
                int b = editstableModel.getRowCount();
                for (int j = 0; j < b; j++) {
                  if (myeditor.equals(editstableModel.getValueAt(j, config.editorcol))
                      && e.projname.equals(editstableModel.getValueAt(j, config.projectcol))) {
                    ListIndicator li = (ListIndicator)editstableModel.getValueAt(j, config.listcol);
                    editstableModel.setValueAt(li.addList(ListIndicator.BLACK),j, config.listcol);
                  }
                }
              } else {
                config.tempblacklistModel.addElement((String) backendModel
                    .getValueAt(i, config.editorcol)
                    + "#" + e.projname);
                int b = editstableModel.getRowCount();
                for (int j = 0; j < b; j++) {
                  if (myeditor.equals(editstableModel.getValueAt(j, config.editorcol))
                      && e.projname.equals(editstableModel.getValueAt(j, config.projectcol))) {
                    ListIndicator li = (ListIndicator)editstableModel.getValueAt(j, config.listcol);         
                    editstableModel.setValueAt(li.addList(ListIndicator.TBLACK),j, config.listcol);
                  }
                }
              }
            }
          }
        }
      }
      int a = editstableModel.getRowCount();
      // This throws out other special edits, like Special:Log/upload and
      // Special:Log/block (though I'm not even sure if that shows up in the
      // RC feed)
      /*
       * if (e.pagename.length() > 10 && e.pagename.substring(0,
       * 8).equals("Special:")) { // return; }
       */
      if (config.whitelistModel.contains(e.username + "#" + e.projname)
          && config.getBooleanProp("blackreverted")) {

        // Pattern p = Pattern
        // .compile("^Reverted edits by
        // \\u005B\\u005B([^\\u007C\\u005B\\u005D]*)\\u007C([^\\u007C\\u005B\\u005D]*)\\u005D\\u005D.*");
        // Matcher m = p.matcher(e.editsummary);
        // String s = new String();
        //
        // if (m.find()) {
        // s = m.group(2);

        MessageMatcher m = config.getMessageMatcher(e.projname + ".Revertpage");
        Properties results = m.match(e.editsummary);
        if (results != null && results.containsKey("2")) {
          // second parameter
          String s = results.getProperty("2");
          // No penalty for reverting yourself -- cannot be moved from the
          // whitelist to the blacklist
          if (!s.equals(e.username)
              && !config.whitelistModel.contains(s + "#" + e.projname)
              && !config.tempwhitelistModel.contains(s + "#" + e.projname)
              && !config.blacklistModel.contains(s + "#" + e.projname)
              && !config.tempblacklistModel.contains(s + "#" + e.projname)) {
            if (config.getBooleanProp("revertpermButton"))
              config.blacklistModel.addElement(s + "#" + e.projname);
            else
              config.tempblacklistModel.addElement(s + "#" + e.projname);
          }
        }
      }

      // Don't care about greylisted users, drop edit
      if (config.greylistModel.contains(e.username + "#" + e.projname))
        return;

      a = editstableModel.getRowCount();
      // If this edit is on a page already in the list, remove the old one (if
      // the option is turned on)
      if (config.getBooleanProp("olddeleted"))
        for (int i = 0; i < a; i++) {
          if (e.projname.equals(editstableModel.getValueAt(i, config.projectcol))
              && (e.pagename.equals(editstableModel.getValueAt(i, config.articlecol))
                  || (Edit.SPECIAL_UPLOAD.equals(editstableModel.getValueAt(i, config.specialcol))
                      && e.pagename.equals(editstableModel.getValueAt(i, config.subjectcol))))) {
              //&& XWarning.NONE != editstableModel.getValueAt(i, config.xcol)) {
            editstableModel.removeRow(i);
            break;
          }
        }

      // Don't care about whitelisted users, drop edit
      if (whitelisted(e.pagename, e.username, e.editsummary, e.projname))
        return;

      // anything that matches any of the watchlist/blacklist criteria will be
      // processed, regardless of the "show only" sections
      if (((config.getBooleanProp("showips") && !isValidIP(e.username))
          || (config.getBooleanProp("newpages") && !e.newpage) || config
          .getBooleanProp("showwatch"))
          && config.getBooleanProp("listprecedence")
          && !watched(e.pagename, e.username, e.editsummary, e.projname,
              e.change, tablerow[config.specialcol] == Edit.SPECIAL_MOVE,
              e.newpage))
        return;

      // beeping
      if ((config.getBooleanProp("beeptempblack") && ((listIndicator&ListIndicator.TBLACK) != 0))
       || (config.getBooleanProp("beeppermblack") && ((listIndicator&ListIndicator.BLACK) != 0))  
       || (config.getBooleanProp("beeptempwatch") && ((listIndicator&ListIndicator.TWATCH) != 0))
       || (config.getBooleanProp("beeppermwatch") && ((listIndicator&ListIndicator.WATCH) != 0))
       || (config.getBooleanProp("beepregexpblack") && ((listIndicator&ListIndicator.RBLACK) != 0))) {
         Toolkit.getDefaultToolkit().beep();    
      }
      
      // Without this +32 in here, the scrollbar gets lost.. getvalue +
      // getvisibleamount should = getmaximum, but it doesn't, hence the need
      // for this "buffer"
      if (config.getBooleanProp("reversetable")) {
        editstableModel.insertRow(0, tablerow);
      } else {
        editstableModel.addRow(tablerow);
      }
    }
  }

  boolean whitelisted(String page, String user, String summary, String project) {
    if (config.whitelistModel.contains(user + "#" + project)
        || config.tempwhitelistModel.contains(user + "#" + project))
      return (true);
    else {
      int size = config.regexpwhiteModel.getSize(), i;

      for (i = 0; i < size; i++)
        if ((config.getBooleanProp("rwhiteuser") && user
            .matches((String) config.regexpwhiteModel.getElementAt(i)))
            || (config.getBooleanProp("rwhitepage") && page
                .matches((String) config.regexpwhiteModel.getElementAt(i)))
            || (config.getBooleanProp("rwhitesummary") && summary
                .matches((String) config.regexpwhiteModel.getElementAt(i))))
          return (true);
    }
    return (false);

  }

  /*
   * A page is considered "Watched" and thus immune from removal (if list
   * precedence is on) IF: 1) The page is on the article watchlist 2) The page
   * was edited by a user on the blacklist 3) The page is on the temporary
   * article watchlist 4) The page was edited by a user on the temporary
   * blacklist 5) .regexp application to users is turned on and a user matches
   * any .regexp 6) .regexp application to pages is turned on and a page matches
   * any .regexp -- 7) Watch user page edits not by user turned on 8) Watch mass
   * additions/deletion turned on 9) Watch page moves turned on 10) Watch new
   * pages turned on
   */
  boolean watched(String page, String user, String summary, String project,
      int change, boolean move, boolean newpage) {
    String userNamespace = config.getNamespace(project+".2");
    if (userNamespace == null || userNamespace.length() == 0) 
      userNamespace = "User";
    if (config.watchlistModel.contains(page + "#" + project)
        || config.blacklistModel.contains(user + "#" + project)
        || config.tempwatchlistModel.contains(page + "#" + project)
        || config.tempblacklistModel.contains(user + "#" + project))
      return (true);
    else if (config.getBooleanProp("watchuserpages")
         && page.matches("^"+userNamespace+":.*") && !page.matches("^"+userNamespace+":" + user + ".*"))
      return (true);
    else if (config.getBooleanProp("watchpagemoves") && move)
      return (true);
    else if (config.getBooleanProp("watchnewpages") && newpage)
      return (true);
    else if (config.getBooleanProp("watchmasschanges")) {
      try {
        if (change < 0)
          change *= -1;
        int j = config.getIntProp("changed");
        if (j < 0)
          j *= -1;
        if (change > j)
          return (true);
      } catch (Exception e) {
      }
    } else {
      int size = config.regexpblackModel.getSize(), i;

      for (i = 0; i < size; i++)
        if ((config.getBooleanProp("rblackuser") && user
            .matches((String) config.regexpblackModel.getElementAt(i)))
            || (config.getBooleanProp("rblackpage") && page
                .matches((String) config.regexpblackModel.getElementAt(i)))
            || (config.getBooleanProp("rblacksummary") && summary
                .matches((String) config.regexpblackModel.getElementAt(i))))
          return (true);
    }
    return (false);
  }
  
  public boolean isNewUser(String user, String project) {
    return newUsers.contains(user+"#"+project);
  }
  
  private static final boolean isValidIP(String ip) {
    return PATTERN_IP.matcher(ip).matches();
  }

  protected String getTime(long millis) {
    // TODO Beren at se DateFormat nevytvari pokazde
    Date date = new Date(millis);
    DateFormat df = SimpleDateFormat.getTimeInstance(DateFormat.MEDIUM,
        config.currentLocale);
    df.setTimeZone(config.timeZone);
    return df.format(date);
  }
  
  public void setTimeZone(TimeZone timeZone) {
    config.timeZone = timeZone;
    synchronized (config.editstableTable.getTreeLock()) {
      // TODO Beren check type
      int a = editstableModel.getRowCount();
      for (int i = 0; i < a; i++) {
        Long time = (Long)editstableModel.getValueAt(i, config.sectimecol);
        editstableModel.setValueAt(getTime(time.longValue()),i, config.timecol);
      }   
    }
  }   
}
