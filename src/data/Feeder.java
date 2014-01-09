/*
 Feeder thread for CryptoDerk's Vandal Fighter
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
 * Created on Nov 29, 2006
 * author: Beren 
 * http://cs.wikipedia.org/wiki/User:Beren
 */

package data;

import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

import javax.swing.SwingUtilities;

public class Feeder extends Thread {
  
  public static Feeder createFeeder(Data d) {
    if (feeder == null)
      feeder = new Feeder(d);
    return feeder;
  }
  public static Feeder getFeeder() {
    return feeder;
  }
  private static Feeder feeder;

  protected Data data = null;

  protected Vector edits = new Vector();

  protected boolean pause = false;

  public synchronized void setPause(boolean b) {   
    if (!b && pause) {
      pause = b;
      if (!data.config.getBooleanProp("queueedits"))
        edits.removeAllElements();
      
      notify();
    } else {
      pause = b;
    }  
  }

  public boolean isPause() {
    return pause;
  }

  public Feeder(Data data) {
    super();

    this.data = data;
    setDaemon(true);
    start();
  }

  public void addEdit(Edit e) {
    edits.add(e);
  }

  public void run() {
    while (true) {
      if (pause) {
        try {
          synchronized (this) {
            wait();
          }
        } catch (InterruptedException e) {
        }
      }

      if (!pause) {
        if (edits.size() != 0)
          try {
          SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
              int i = 0;
              
              data.l.updateBefore();
              while (edits.size() != 0) {
                Edit edit = null;
                synchronized(edits) {
                  if (edits.size() != 0) {
                    edit = (Edit) edits.get(0);
                    edits.remove(0);
                  }   
                } 
                if (edit != null) {
                  data.addnewtableentry(edit);
                  i++;
                }
              }
              data.l.updateAfter();
            }
          });
          } catch (InterruptedException e) {
          } catch (InvocationTargetException e) {
            e.printStackTrace();
          }
      }

      try {
        sleep((int) (70 + Math.random() * 50));
      } catch (InterruptedException e) {
      }
    }
  }
}
