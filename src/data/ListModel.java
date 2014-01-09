/*
 ListModel class - table model representation for CryptoDerk's Vandal Fighter
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
 * by Criptoderk as inner class vf$newModel
 * 
 * Standalone class ListModel was created in 2006
 * author: Finne Boonen 
 * email: finne@cassia.be
 * http://en.wikipedia.org/wiki/User:Henna
 * 
 * Changed by Beren, 12-nov-2006
 * http://cs.wikipedia.org/wiki/User:Beren
 */

package data;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class ListModel extends DefaultTableModel
{

	/**
   * 
   */
  private static final long serialVersionUID = -2816876882341179550L;

	public ListModel(Object[] columns, int x)
	{
		super(columns, x);
	}

	public boolean isCellEditable(int row, int col)
	{
	  return false;
	}

	public Class getColumnClass(int column)
	{
		Vector v = (Vector) dataVector.elementAt(0);
		return v.elementAt(column).getClass();
  }
  
  public Object[] getDataVector(int row) {
    Vector v = (Vector) dataVector.elementAt(row);
    return v.toArray();
  }
} 
