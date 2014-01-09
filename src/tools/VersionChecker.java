/*
 VersionChecker class for CryptoDerk's Vandal Fighter
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
 * by Criptoderk as inner class vf$versionchecker
 * 
 * Standalone file was created on 14-mrt-2006
 * author: Finne Boonen 
 * email: finne@cassia.be
 * http://en.wikipedia.org/wiki/User:Henna
 */

package tools;

import java.io.BufferedInputStream;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import data.Configuration;

	// If the option is set, this class is instantiated once to check the
	// current version
	public class VersionChecker extends Thread
	{
		private URL versionURL;
		private Configuration config;
		private BufferedInputStream versionbuf;
		
		int i;
		
		String verstr = new String("");
		
		private JFrame parent;
		
		public VersionChecker(JFrame p)
		{
			config=Configuration.getConfigurationObject();
			parent = p;
			start();
		}
		
		public void run()
		{
			try
			{
				versionURL = new URL(
				"http://en.wikipedia.org/w/index.php?title=User:Henna/VF/version&action=raw");
				versionbuf = new BufferedInputStream(versionURL
						.openConnection().getInputStream());
				i = versionbuf.available();
				
				while (i > 0)
				{
					byte b[] = new byte[i];
					versionbuf.read(b);
					verstr = new String(verstr + new String(b));
					Thread.sleep(100);
					i = versionbuf.available();
				}
				if (Integer.parseInt(verstr.trim()) > config.verint)
					JOptionPane
					.showMessageDialog(
							parent,
					"New version is available!\nDownload at http://en.wikipedia.org/wiki/User:Henna/VF");
			}
			catch (Exception e)
			{}
		}
		
	}
