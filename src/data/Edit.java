/*
 Edit class for CryptoDerk's Vandal Fighter
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
 * Created in 2006 as class edit
 * author: Finne Boonen
 * email: finne@cassia.be
 * http://en.wikipedia.org/wiki/User:Henna
 *
 * Changed by Beren, 12-nov-2006
 * http://cs.wikipedia.org/wiki/User:Beren
 */

package data;

public class Edit
{
  public static final Short SPECIAL_NONE = new Short((short)0);
  public static final Short SPECIAL_NEWUSER = new Short((short)1);
  public static final Short SPECIAL_UPLOAD = new Short((short)2);
  public static final Short SPECIAL_MOVE = new Short((short)3);
  public static final Short SPECIAL_DELETE = new Short((short)4);
  public static final Short SPECIAL_UNDELETE = new Short((short)5);
  public static final Short SPECIAL_BLOCK = new Short((short)6);
  public static final Short SPECIAL_UNBLOCK = new Short((short)7);
  public static final Short SPECIAL_PROTECT = new Short((short)8);
  public static final Short SPECIAL_UNPROTECT = new Short((short)9);
  public static final Short SPECIAL_MODIFY_PROTECT = new Short((short)10);
  public static final Short SPECIAL_RENAME_USER = new Short((short)11);

  public static final Short SPECIAL_REVISION = new Short((short)12);
  public static final Short SPECIAL_REBLOCK = new Short((short)13);

  public static final Short SPECIAL_COLLABORATION_OK = new Short((short)100);
  public static final Short SPECIAL_COLLABORATION_WARNING = new Short((short)101);

	public String pagename;
	String url;
	String username;
	public String editsummary;
	int change;
	boolean minor;
	boolean newpage;
	public String projname;
	long time;
	int risk;
	public String subject;
	private boolean vandalismchannel;
  private boolean collaborationchannel;
  public Short special = SPECIAL_NONE;
	public String toString()
	{
		return "******"+projname+":"+pagename+"*******\n"+
		       url +"\n"+username+"-"+change+"\n"+editsummary+
		       minor+"-"+newpage+"-"+time;
	}

  public Edit(String pagename, String url, String username,
      String editsummary, int change, boolean minor, boolean newpage,
      String projname, long time, Short special, String subject,
      boolean vc,boolean cc)
  {
    this.pagename = pagename;
    this.url = url;
    this.username = username;
    this.editsummary = editsummary;
    this.change = change;
    this.minor = minor;
    this.newpage = newpage;
    this.projname = projname;
    this.time = time;
    this.vandalismchannel=vc;
    this.collaborationchannel=cc;
    this.special = special;
    this.subject = subject;
    risk = calcRisk();
  }

  public Edit(String pagename, String url, String username,
      String editsummary, String changeS, boolean minor, boolean newpage,
      String projname, long time, Short special, String subject,
      boolean vc, boolean cc) {
    this(pagename, url, username, editsummary, parseChange(changeS), minor, newpage,
        projname, time, special, subject, vc, cc);
  }

  protected static int parseChange(String changeS) {
    int changeI = 0;
    try {
      // int change=Integer.parseInt(changeS.substring(1,changeS.length()));
      if (changeS.charAt(0) == '+')
        changeS = changeS.substring(1);

      changeI = Integer.parseInt(changeS);
    } catch (Exception e) {
      System.out.println("problem parsing change: >" + changeS + "<");
    }
    return changeI;
  }

	public int calcRisk()
	{
		int r=0;
		if (isIP())
		{
			r+=1;
		}
		if (change>2000)
		{
			r+=2;
		}
		if (change<-500)
		{
			r+=2;
		}
		if (usernamespace()&&username!=articlename())
		{
			r+=1;
		}
		if (editsummary.length() == 0)
		{
			r+=1;
		}
		if (newpage && change<25)
		{
			r+=2;
		}
		if (vandalismchannel)
		{
			r+=99;
		}
		return r;
	}

	private String articlename()
	{
		String[] strs;
		if (0<=(pagename.indexOf(':')))
		{
			strs=pagename.split(":");
			return strs[1];
		}
		else
			return pagename;
	}

	private String 	namespace()
	{
		String[] strs;
		if (0<=(pagename.indexOf(':')))
		{
			strs=pagename.split(":");
			return strs[0];
		}
		else
			return null;
	}



	private boolean usernamespace()
	{
		String nsp=namespace();
		return nsp=="User";
	}

	private boolean isIP()
	{
		return Character.isDigit(username.charAt(0));

	}

	public boolean isVandalismEdit()
	{
		return vandalismchannel;
	}

  public boolean isCollaborationEdit()
  {
    return collaborationchannel;
  }
}
