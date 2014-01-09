/*
 Vandalfighter class - launcher for CryptoDerk's Vandal Fighter
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
 * Created on 17-mrt-2006
 * original author: Finne Boonen
 * email: finne@cassia.be
 * http://en.wikipedia.org/wiki/User:Henna
 *
 * Changed by Beren, 21-nov-2006
 * http://cs.wikipedia.org/wiki/User:Beren
 */

import gui.Vf;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import data.Configuration;

public class Vandalfighter {

  /**
   * @param args
   */
  public static void main(String args[]) {

    System.out.println("Vandalfighter");
    String language = "en";
    String country = null;
    language = System.getProperty("user.language");
    Configuration config = Configuration.getConfigurationObject();
    if (config.containsKey("lang"))
      language = config.getProperty("lang");
    if (config.containsKey("country"))
      country = config.getProperty("country");

    if (args.length != 2) {
      if (country != null)
        country = new String("US");
    } else {
      language = new String(args[0]);
      country = new String(args[1]);
    }
    String feel = (String)config.getProperty("feel");
    if (feel == null) {
    	feel = UIManager.getSystemLookAndFeelClassName() ;
      //feel = UIManager.getCrossPlatformLookAndFeelClassName();
    }
    try {

      //"com.sun.java.swing.plaf.gtk.GTKLookAndFeel"
      //"com.sun.java.swing.plaf.motif.MotifLookAndFeel";
      //"com.sun.java.swing.plaf.windows.WindowsLookAndFeel"
      //LookAndFeel[] feels = UIManager.getAuxiliaryLookAndFeels();
      //System.out.println(feels[0]+"");
      //javax.swing.plaf.multi.MultiLookAndFeel
      Class lnfClass = Class.forName(feel);
      LookAndFeel newLAF = (LookAndFeel) (lnfClass.newInstance());
      if (newLAF.isSupportedLookAndFeel())
        UIManager.setLookAndFeel(feel);

      //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
      //UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
      //UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
    } catch (Exception e) {
    }

    new Vf(language, country, feel);

  }

}
