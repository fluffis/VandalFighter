package gui;
/*
 Vf - main UI class for CryptoDerk's Vandal Fighter
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
 * by Criptoderk as class vf
 *
 * Changed by Henna, 2006
 * author: Finne Boonen
 * email: finne@cassia.be
 * http://en.wikipedia.org/wiki/User:Henna
 *
 * Changed by Beren, 16-nov-2006
 * http://cs.wikipedia.org/wiki/User:Beren
 */

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultListSelectionModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import tools.BbrowserLauncher;
import tools.Importer;
import tools.VersionChecker;
import data.Configuration;
import data.Data;
import data.Edit;
import data.Feeder;
import data.ListModel;
import data.Listener;

public class Vf extends JFrame implements ActionListener, MouseListener,
    ListSelectionListener, Listener, gui.StatusListener {

  /**
   *
   */
  private static final long serialVersionUID = 6254904988958257776L;

  public Runnable repainter = new Runnable() {
    public void run() {
      config.editstableTable.repaint();
    }
  };

  Object IRCLOCK = new Object();

  Configuration config;

  Data data;

  private JPanel editsPanel, bottomeditsPanel;

  Icon vfIcon;

  public int i, j;

  // private Container mainContainer;

  private JButton connectButton, disconnectButton, clearButton, pauseButton;

  private JScrollPane editstableScroller;

  private JScrollBar editstableScrollBar;

  // Popup menus

  private JPopupMenu xPopupMenu, userPopupMenu, projectPopupMenu,
      pagePopupMenu, specialPagePopupMenu, specialPage2PopupMenu;

  private JLabel userNamePopupLabel, projectNamePopupLabel, xNamePopupLabel,
      pageNamePopupLabel, specialPageNamePopupLabel, specialPage2NamePopupLabel;

  private JMenuItem userContributionsPopupItem, userTalkPagePopupItem,
      userBlockPopupItem, user2WhitelistPopupItem,
      user2GreylistPopupItem, user2BlacklistPopupItem,
      user2TempWhitelistPopupItem, user2TempBlacklistPopupItem;

  private JMenuItem projectRCPopupItem, projectMainPagePopupItem;

  private JMenuItem xAcceptPopupItem, xSimplyDeletePopupItem,
      xWarningPopupItem, xWarningAndDeletePopupItem;

  private JMenuItem pageDiffPopupItem, pageHistoryPopupItem,
      pageActualPopupItem, pageLockPopupItem, page2WatchlistPopupItem,
      page2TempWatchlistPopupItem;

  private JMenuItem specialPageActionPopupItem, specialPageLogPopupItem;

  private JMenuItem specialPage2LogPopupItem;

  int popupRowNumber;

  Object[] popupRow;

  // private boolean connected = false;

  private IRC ircB;

  private Vector servers;

  // Update this with new release info

  // Column header names

  // private JButton exportButton;
  // private JButton exportchooserButton;

  private JTextField exserverField;

  VersionChecker versioncheck;

  // FROM configuration
  /*
   * public Color blacklistcolor = new Color(255,255,0), // Yellow
   * watchlistcolor = new Color(153,255,255), // Light blue changedcolor = new
   * Color(204,255,204), // Light green movecolor = new Color(255,102,102), //
   * Red newcolor = new Color(255,204,102), // Light orange ipcolor = new
   * Color(255,204,255), // Light purple userpagecolor = new Color(205, 201,
   * 201); // Light gray
   *
   */

  private String language;

  private String feel;

  private String theme;

  private String country;

  Locale currentLocale;

  ResourceBundle messages;

  public String[] columnNames;

  private Importer botimport;

  String[] langStrings;

  String[] langCodes = { "en", "ar", "cs", "de", "fr", "it", "ja", "nl", "pt", "ru" };

  String[] timeZoneStrings;

  String[] timeZoneCodes;

  String[] feelStrings;

  String[] feelCodesDefault = {
      UIManager.getCrossPlatformLookAndFeelClassName(),
      "com.sun.java.swing.plaf.mac.MacLookAndFeel",
      "com.sun.java.swing.plaf.motif.MotifLookAndFeel",
      "com.sun.java.swing.plaf.windows.WindowsLookAndFeel",
      "com.sun.java.swing.plaf.gtk.GTKLookAndFeel" };

  String[] feelCodes;

  String[] themeStrings;

  String[] themeCodes = new String[] { "default", "ocean" };

  DefaultMetalTheme[] themes = new DefaultMetalTheme[] {
      new DefaultMetalTheme(), new OceanTheme() };

  private Importer adminimport;

  private boolean aboutLoaded = false;

  public Vf(String l, String c, String f) {
    super();
    System.out.println("running");
    // setup datastructures&configuration
    config = Configuration.getConfigurationObject();
    data = Data.getDataObject();
    data.addListener(this);

    Feeder.createFeeder(data);

    // setup localisation
    language = l;
    country = c;
    feel = f;
    if (feel == null)
      feel = UIManager.getCrossPlatformLookAndFeelClassName();

    theme = config.getProperty("theme");
    if (theme == null)
      theme = "default";

    config.currentLocale = new Locale(language, country);
    Locale.setDefault(config.currentLocale);
    // TODO Beren better handling when locale does not exist
    config.bundle = messages = ResourceBundle.getBundle("MessagesBundle", config.currentLocale);

    langStrings = new String[langCodes.length];
    for (int i = 0; i < langCodes.length; i++) {
      langStrings[i] = messages.getString("language." + langCodes[i]);
    }

    {
//      timeZone = config.getProperty("timeZone");
//      timeZoneCodes = TimeZone.getAvailableIDs();
//      timeZoneStrings = new String[timeZoneCodes.length];
//      Date today = new Date();
//      for (int i = 0; i < timeZoneCodes.length; i++) {
//        TimeZone zone = TimeZone.getTimeZone(timeZoneCodes[i]);
//        timeZoneStrings[i] = zone.getDisplayName();
//      }
      config.timeZone = TimeZone.getTimeZone(config.getProperty("timeZone"));
      timeZoneCodes = new String[] {TimeZone.getDefault().getID(), "GMT"};
      timeZoneStrings = new String[] {messages.getString("System"), messages.getString("GMT")};

    }

    {
      ArrayList arrayList = new ArrayList();
      for (int i = 0; i < feelCodesDefault.length; i++) {
        if (isAvailableLookAndFeel(feelCodesDefault[i])) {
          arrayList.add(feelCodesDefault[i]);
        }
      }
      feelCodes = new String[arrayList.size()];
      feelCodes = (String[]) arrayList.toArray(feelCodes);
      feelStrings = new String[feelCodes.length];
      for (int i = 0; i < feelCodes.length; i++) {
        feelStrings[i] = messages.getString("feel." + feelCodes[i]);
      }
    }

    themeStrings = new String[themeCodes.length];
    for (int i = 0; i < themeCodes.length; i++) {
      themeStrings[i] = messages.getString("theme." + themeCodes[i]);
    }

    initComponents();

    // Set icon

    this.setIconImage(getImage());

    // color buttons

    colorchangedButton.setBackground(config.getColorProp("changedcolor"));
    colorchangedButton.setContentAreaFilled(false);
    colorchangedButton.setOpaque(true);
    colorblistButton.setBackground(config.getColorProp("blacklistcolor"));
    colorblistButton.setContentAreaFilled(false);
    colorblistButton.setOpaque(true);
    colorwatchButton.setBackground(config.getColorProp("watchlistcolor"));
    colorwatchButton.setContentAreaFilled(false);
    colorwatchButton.setOpaque(true);

    colornewButton.setBackground(config.getColorProp("newcolor"));
    colornewButton.setContentAreaFilled(false);
    colornewButton.setOpaque(true);
    colormovesButton.setBackground(config.getColorProp("movecolor"));
    colormovesButton.setContentAreaFilled(false);
    colormovesButton.setOpaque(true);
    coloripsButton.setBackground(config.getColorProp("ipcolor"));
    coloripsButton.setContentAreaFilled(false);
    coloripsButton.setOpaque(true);
    colornewusersButton.setBackground(config.getColorProp("newusercolor"));
    colornewusersButton.setContentAreaFilled(false);
    colornewusersButton.setOpaque(true);
    coloruserpageButton.setBackground(config.getColorProp("userpagecolor"));
    coloruserpageButton.setContentAreaFilled(false);
    coloruserpageButton.setOpaque(true);

    // super.setTitle(config.version);
    String[] columnNamestmp = {
    	//hidden colmuns cause troubles these should been rewrite
    	messages.getString("sectime"),
        messages.getString("URL"),
        "", //messages.getString("Special"),
        "", //messages.getString("Subject"),
        // not hidden
        messages.getString("highrisk"), messages.getString("Time"), messages.getString("Type"),
        messages.getString("Article"), messages.getString("Editor"),
        messages.getString("+/-"), messages.getString("Summary"),
        messages.getString("Lists"), messages.getString("x"), messages.getString("Project")};
    columnNames = columnNamestmp;
    config.totalcols = columnNames.length;

    servers = new Vector();
    data.pausededits = new Vector();
    config.pausevar = false;
    config.quitvar = false;
    // hidden colmuns 0, 0, 0, 0 cause troubles these should been rewrite
    //int colwidths[] = { 50, 25, 50, 200, 100, config.lcb, config.lcb, 300, config.lcb, config.lcb};
    int colwidths[] = { 0, 0, 0, 0, config.lcb, config.lcb, config.lcb, 200, 100, config.lcb, 300, config.lcb, config.lcb, config.lcb};

    // EDITS PANEL START
    editsPanel = new JPanel(new BorderLayout());

    // This provides an easy way to reference the columns later (and easy
    // way to add new columns)
    i = 0;
    // hidden
    config.sectimecol = i++;
    config.urlcol = i++;
    config.specialcol = i++;
    config.subjectcol = i++;
    // not hidden
    config.riskcol = i++;
    config.timecol = i++;
    config.typecol = i++;
    config.articlecol = i++;
    config.editorcol = i++;
    config.changedcol = i++;
    config.summarycol = i++;
    config.listcol = i++;
    config.xcol = i++;
    config.projectcol = i++;

    //// This sets the summary field to be as wide as possible given the
    //// application width and other column sizes
    // Commet out (because width of config.summarycol becomes negative. strange)
    //for (i = 0; i < config.summarycol; i++)
    //  colwidths[config.summarycol] -= colwidths[i];
    //for (i = config.summarycol + 1; i < config.totalcols; i++)
    //  colwidths[config.summarycol] -= colwidths[i];

    data.editstableModel = new ListModel(columnNames, 0);
    data.backendModel = new ListModel(columnNames, 0);

    config.editstableTable = new JTable() {

      // for colored rows
      public Component prepareRenderer(TableCellRenderer renderer,
          int rowIndex, int vColIndex) {
        synchronized (config.editstableTable.getTreeLock()) {
          if (rowIndex >= data.editstableModel.getRowCount())
            throw new RendererRowException();
          Component c = super.prepareRenderer(renderer, rowIndex, vColIndex);

          String userNamespace = config.getNamespace(data.editstableModel.getValueAt(rowIndex,
              config.projectcol)+".2");
          if (userNamespace == null || userNamespace.length() == 0)
            userNamespace = "User";
          try {
            ListIndicator li = (ListIndicator)data.editstableModel.getValueAt(rowIndex,
                config.listcol);
            TypeIndicator ti = (TypeIndicator)data.editstableModel.getValueAt(rowIndex,
                config.typecol);

            // TODO add bold
            Integer r = (Integer) data.editstableModel.getValueAt(rowIndex,
                config.riskcol);
            if (config.getBooleanProp("risk")) {
              if (r.intValue() > 2) // highlight
              {
                Font f = c.getFont();
                Font nf = new Font(f.getFontName(), Font.BOLD, f.getSize());
                c.setFont(nf);
                // return c;
              }
            }

            if (config.getBooleanProp("colorblist")
                && (li.isInList(ListIndicator.BLACK)
                    || li.isInList(ListIndicator.TBLACK)
                    || li.isInList(ListIndicator.RBLACK))) // blacklist
            {
              c.setBackground(config.getColorProp("blacklistcolor"));

              return c;
            } else if (config.getBooleanProp("colorwatch")
                && (li.isInList(ListIndicator.WATCH)
                    || li.isInList(ListIndicator.TWATCH))) // watchlist
            {
              c.setBackground(config.getColorProp("watchlistcolor"));
              return c;
            } else if (config.getBooleanProp("coloruserpage")
                && // Potential user page vandalism
                ((String) data.editstableModel.getValueAt(rowIndex,
                    config.articlecol)).matches("^"+userNamespace+":.*")
                && !((String) data.editstableModel.getValueAt(rowIndex,
                    config.articlecol)).matches("^"+userNamespace+":"
                    + (String) data.editstableModel.getValueAt(rowIndex,
                        config.editorcol) + ".*")) {

              // System.out.println("potential uservandalism");
              c.setBackground(config.getColorProp("userpagecolor"));
              return c;
            } else if (config.getBooleanProp("colorchanged")) // Massive
            // addition or
            // deletion of
            // text
            {
              try {
                int i = ((Integer) data.editstableModel.getValueAt(rowIndex,
                    config.changedcol)).intValue();
                if (i < 0)
                  i *= -1;
                int j = config.getIntProp("changed");
                if (j < 0)
                  j *= -1;
                if (i > j) {
                  c.setBackground(config.getColorProp("changedcolor"));
                  return c;
                }
              } catch (Exception e) {
              }
            }

            if (config.getBooleanProp("colormoves")
                && (data.editstableModel
                    .getValueAt(rowIndex, config.specialcol) == Edit.SPECIAL_MOVE)) // page
              // moves
              c.setBackground(config.getColorProp("movecolor"));
            else if (config.getBooleanProp("colornew")
                && ti.isNewpage()) // new pages
              c.setBackground(config.getColorProp("newcolor"));
            else if (config.getBooleanProp("colorips")
                && isValidIP((String) data.editstableModel.getValueAt(rowIndex,
                    config.editorcol))) // ip edits
              c.setBackground(config.getColorProp("ipcolor"));
            // TODO Beren
            else if (config.getBooleanProp("colornewusers")
                && data.isNewUser((String)data.editstableModel.getValueAt(rowIndex,
                    config.editorcol), (String) data.editstableModel.getValueAt(rowIndex,
                    config.projectcol)))
              c.setBackground(config.getColorProp("newusercolor"));
            else
              // If not shaded, match the table's background
              c.setBackground(getBackground());

            return c;
          } catch (Exception exc) {
            c.setBackground(getBackground());
            return c;
          }
        }
      }

      public String getToolTipText(MouseEvent e) {
        String tip = null;
        java.awt.Point p = e.getPoint();
        int rowIndex = rowAtPoint(p);
        int colIndex = columnAtPoint(p);
        int realColumnIndex = convertColumnIndexToModel(colIndex);

        if (realColumnIndex == config.timecol) {
          Long sectime = (Long) data.editstableModel.getValueAt(rowIndex,
              config.sectimecol);
          Date date = new Date(sectime.longValue());
          // TODO Beren stop creating DateFormat everytime
          DateFormat df = SimpleDateFormat.getDateTimeInstance(
              DateFormat.MEDIUM, DateFormat.MEDIUM, config.currentLocale);
          df.setTimeZone(config.timeZone);
          tip = df.format(date);
        } else if (realColumnIndex == config.xcol) {
          WarningIndicator warning = (WarningIndicator)data.editstableModel.getValueAt(rowIndex,
              config.xcol);
          String result = null;
          if (warning != WarningIndicator.NONE) {
            String tooltip = warning.getTooltip();
            if (tooltip == null)
              result = messages.getString("XWarningSelf");
            else
              result = MessageFormat.format(messages.getString("XWarningBy"),
                  new Object[] {tooltip});
          }
          return result;
        } else if (realColumnIndex == config.listcol) {
          ListIndicator li = (ListIndicator)data.editstableModel.getValueAt(rowIndex,
              config.listcol);
          return li.getTooltip();
        } else if (realColumnIndex == config.typecol) {
          TypeIndicator ti = (TypeIndicator)data.editstableModel.getValueAt(rowIndex,
              config.typecol);
          return ti.getTooltip();
        } else {
          tip = data.editstableModel.getValueAt(rowIndex, realColumnIndex)
                   .toString();
          if (tip.trim().length() == 0)
            tip = null;
        }

        return tip;
      }
    };

    config.editstableTable.setUI(new javax.swing.plaf.basic.BasicTableUI() {
      public void paint(Graphics g, JComponent c) {
        try {
          super.paint(g, c);
        } catch (RendererRowException e) {
          // simply stop painting if row is removed
        }
      }
    });

    config.editstableTable.setDefaultRenderer(Object.class,
        new DefaultTableCellRenderer() {
          public Component getTableCellRendererComponent(JTable table,
              Object value, boolean isSelected, boolean hasFocus, int row,
              int column) {
            super.getTableCellRendererComponent(table, value, isSelected,
                hasFocus, row, column);

            int columnModel = config.editstableTable.convertColumnIndexToModel(column);
            if (columnModel == config.typecol || columnModel == config.xcol || columnModel == config.listcol)
              setHorizontalAlignment(SwingConstants.CENTER);
            else if (columnModel == config.timecol)
              setHorizontalAlignment(SwingConstants.RIGHT);
            else
              setHorizontalAlignment(SwingConstants.LEFT);
            return this;
          }
        });

    config.editstableTable.setModel(data.editstableModel);
    config.editstableTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

    try {
      int cols = config.totalcols;
      int colorder[] = new int[cols];

      for (i = 0; i < cols; i++)
        colorder[i] = config.getIntProp("colName" + i);

      for (j = 0; j < cols; j++)
        for (i = 0; i < cols; i++) {
          if (colorder[i] != config.editstableTable.getTableHeader()
              .getColumnModel().getColumnIndex(columnNames[i])) {
            config.editstableTable.getTableHeader().getColumnModel()
                .moveColumn(colorder[i], config.editstableTable
                    .getTableHeader().getColumnModel().getColumnIndex(columnNames[i]));
          }
        }
    } catch (NumberFormatException e) {
      // Do nothing if "nameCol" fields are not available
    }

    // Set column widths with the default widths (the loadfiles() function
    // will replace these, if a file is present)
    for (i = 0; i < config.totalcols; i++)
      config.editstableTable.getColumnModel().getColumn(i).setPreferredWidth(
          config.getIntProp("sizeCol" + i));

    // This sets the time and URL columns to be 0 width -- time is currently
    // unused, but I imagine it will be used in the future
    config.editstableTable.getColumnModel().getColumn(config.sectimecol)
    	.setMinWidth(colwidths[config.sectimecol]);
    config.editstableTable.getColumnModel().getColumn(config.sectimecol)
        .setMaxWidth(colwidths[config.sectimecol]);
    config.editstableTable.getColumnModel().getColumn(config.urlcol)
        .setMinWidth(colwidths[config.urlcol]);
    config.editstableTable.getColumnModel().getColumn(config.urlcol)
        .setMaxWidth(colwidths[config.urlcol]);
    config.editstableTable.getColumnModel().getColumn(config.specialcol)
        .setMinWidth(colwidths[config.specialcol]);
    config.editstableTable.getColumnModel().getColumn(config.specialcol)
        .setMaxWidth(colwidths[config.specialcol]);
    config.editstableTable.getColumnModel().getColumn(config.subjectcol)
        .setMinWidth(colwidths[config.subjectcol]);
    config.editstableTable.getColumnModel().getColumn(config.subjectcol)
        .setMaxWidth(colwidths[config.subjectcol]);

    config.editstableTable.getColumnModel().setColumnSelectionAllowed(false);
    config.editstableTable.setRowSelectionAllowed(false);
    config.editstableTable.setCellSelectionEnabled(false);
    config.editstableTable.addMouseListener(this);
    config.editstableTable.getColumnModel().getSelectionModel()
        .setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
    config.editstableTable.getSelectionModel().setSelectionMode(
        DefaultListSelectionModel.SINGLE_SELECTION);

    // Gui ellemetns initialised, load values
    // loadfiles();
    loadConfig();

    editstableScroller = new JScrollPane(config.editstableTable);

    editsPanel.add(editstableScroller, BorderLayout.CENTER);

    // bottom of EDITS PANEL
    bottomeditsPanel = new JPanel(new GridLayout(1, 4));

    connectButton = new JButton(messages.getString("Connect"));
    connectButton.addActionListener(this);

    disconnectButton = new JButton(messages.getString("Disconnect"));
    disconnectButton.setEnabled(false);
    disconnectButton.addActionListener(this);

    clearButton = new JButton(messages.getString("ClearList"));
    clearButton.addActionListener(this);

    pauseButton = new JButton(messages.getString("Pause"));
    pauseButton.setEnabled(false);
    pauseButton.addActionListener(this);
    pauseButton.setMnemonic(KeyEvent.VK_A);

    bottomeditsPanel.add(connectButton);
    bottomeditsPanel.add(pauseButton);
    bottomeditsPanel.add(disconnectButton);
    bottomeditsPanel.add(clearButton);
    editsPanel.add(bottomeditsPanel, BorderLayout.SOUTH);
    // EDITS PANEL END

    editsPanel.setName(messages.getString("LiveRC"));

    tab.add(editsPanel, 0);

    tab.setSelectedComponent(editsPanel);

    this.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.out.println("shutting down");
        if (ircB != null) {
          if (ircB.isConnected()) {
            ircB.quit("quit :" + messages.getString("SoftwareName") + config.version
                + messages.getString("IRCquitMessage"));
            // ircV.quit("quit :" + config.version + ", get it at
            // http://en.wikipedia.org/wiki/User:Henna/VF !");
          }
        }
        // if (started)

        // store uiconfig
        config.setProperty("totalCols", config.totalcols + "");
        for (i = 0; i < config.totalcols; i++)
          config.setProperty("sizeCol" + i, ""
              + new Integer(config.editstableTable.getColumnModel()
                  .getColumn(i).getWidth()));

        for (i = 0; i < config.totalcols; i++)
          config.setProperty("colName" + i, ""
              + new Integer(config.editstableTable.getTableHeader()
                  .getColumnModel().getColumnIndex(columnNames[i])));

        config.setProperty("windowWidth", ""
            + new Integer((int) getSize().getWidth()));
        config.setProperty("windowHeight", ""
            + new Integer((int) getSize().getHeight()));
        config.setProperty("windowLeft", ""
                + new Integer((int) getLocation().getX()));
        config.setProperty("windowTop", ""
                + new Integer((int) getLocation().getY()));

        savefiles("this.AddwindowListener");

        System.exit(0);
      }
    });

    whitelistList.clearSelection();
    blacklistList.clearSelection();
    greylistList.clearSelection();
    watchlistList.clearSelection();
    regexpwhiteList.clearSelection();
    regexpblackList.clearSelection();
    tempwhitelistList.clearSelection();
    tempblacklistList.clearSelection();
    tempwatchlistList.clearSelection();

    pack();

    try {
    	setSize(config.getIntProp("windowWidth"), config.getIntProp("windowHeight"));
      } catch (Exception e) {
    	  setSize(config.appwidth, config.appheight);
      }

    try {
    	setLocation(config.getIntProp("windowLeft"), config.getIntProp("windowTop"));
        } catch (Exception e) {
        	setLocation(config.appLeft, config.appTop);
        }

    setVisible(true);

    if (config.getBooleanProp("newversion"))
      versioncheck = new VersionChecker(this);

    if (config.getBooleanProp("connectatstart"))
      connectIRC();
  }

  public void updatechanstatus(String s, boolean add) {
    if (add && !servers.contains(s))
      servers.add(s);
    else if (!add)
      servers.remove(s);

    chanstatus();
  }

  public void chanstatus() {
    String tmp = new String(messages.getString("StatusUpdateConnectedTo"));

    Enumeration e = servers.elements();
    while (e.hasMoreElements()) {
      tmp = new String(tmp + (String) e.nextElement() + ", ");
    }

    if (servers.size() > 0)
      statusLabel.setText(tmp.substring(0, tmp.length() - 2));
    else
      statusLabel
          .setText(messages.getString("StatusUpdateNoFeed"));
  }

  public void actionPerformed(ActionEvent e) {
    if (e == null)
      return;

    if (false) {
      System.out.println("whoops");
    } else if (e.getSource() == clearButton) {
      data.editstableModel.setNumRows(0);
    } else if (e.getSource() == connectButton) {
      connectIRC();
    } else if (e.getSource() == disconnectButton) {
      // TODO uncomment

      ircB.quit("");
      // ircV.quit("");

      // connected = false;
      pauseButton.setText(messages.getString("Pause"));
      disconnectButton.setEnabled(false);
      pauseButton.setEnabled(false);
      serverField.setEditable(true);
      serverField.setEditable(true);

    } else if (e.getSource() == pauseButton) {
      // TODO internationalize
      if (config.pausevar == false) {
        pauseButton.setText(messages.getString("Unpause"));
        // ircV.pauze();
        Feeder.getFeeder().setPause(true);
        updateStatus(messages.getString("StatusUpdatePaused"));
      } else {
        pauseButton.setText(messages.getString("Pause"));
        Feeder.getFeeder().setPause(false);
        // ircV.unpauze();
        updateStatus(messages.getString("StatusUpdateUnpaused"));
      }
      config.pausevar = !config.pausevar;
    }
    // TODO move out
    /*
     */
    savefiles("actionPerformed");
  }

  // run BEFORE saving new settings
  public void workSettings() {
    int i, tmp;

    if (showips.isSelected() && !config.getBooleanProp("showips")) {
      synchronized (config.editstableTable.getTreeLock()) {
        tmp = data.editstableModel.getRowCount();
        for (i = tmp - 1; i >= 0; i--) {
          if (!isValidIP((String) data.editstableModel.getValueAt(i,
              config.editorcol))
              && !(config.getBooleanProp("listprecedence") && watched(
                  (String) data.editstableModel
                      .getValueAt(i, config.articlecol),
                  (String) data.editstableModel.getValueAt(i, config.editorcol),
                  (String) data.editstableModel
                      .getValueAt(i, config.summarycol),
                  (String) data.editstableModel
                      .getValueAt(i, config.projectcol),
                  ((Integer) data.editstableModel.getValueAt(i,
                      config.changedcol)).intValue(),
                  data.editstableModel.getValueAt(i, config.specialcol) == Edit.SPECIAL_MOVE,

                  ((TypeIndicator) data.editstableModel.getValueAt(i, config.typecol))
                      .isNewpage())))
            data.editstableModel.removeRow(i);
        }
      }

      listprecedence.setEnabled(true);
      if (listprecedence.isSelected()) {
        watchuserpages.setEnabled(true);
        watchmasschanges.setEnabled(true);
        watchpagemoves.setEnabled(true);
        watchnewpages.setEnabled(true);
      }
    }
    if (newpages.isSelected() && !config.getBooleanProp("newpages")) {
      synchronized (config.editstableTable.getTreeLock()) {
        tmp = data.editstableModel.getRowCount();
        for (i = tmp - 1; i >= 0; i--) {
          TypeIndicator ti = (TypeIndicator) data.editstableModel.getValueAt(i, config.typecol);
          if (!ti.isNewpage()
              && !(config.getBooleanProp("listprecedence") && watched(
                  (String) data.editstableModel
                      .getValueAt(i, config.articlecol),
                  (String) data.editstableModel.getValueAt(i, config.editorcol),
                  (String) data.editstableModel
                      .getValueAt(i, config.summarycol),
                  (String) data.editstableModel
                      .getValueAt(i, config.projectcol),
                  ((Integer) data.editstableModel.getValueAt(i,
                      config.changedcol)).intValue(),
                  data.editstableModel.getValueAt(i, config.specialcol) == Edit.SPECIAL_MOVE,
                  ti.isNewpage())))
            data.editstableModel.removeRow(i);
        }
      }
      listprecedence.setEnabled(true);
      if (listprecedence.isSelected()) {
        watchuserpages.setEnabled(true);
        watchmasschanges.setEnabled(true);
        watchpagemoves.setEnabled(true);
        watchnewpages.setEnabled(true);
      }

    }
    if (showwatch.isSelected() && !config.getBooleanProp("showwatch")) {
      tmp = data.editstableModel.getRowCount();
      for (i = tmp - 1; i >= 0; i--) {
        if (!watched((String) data.editstableModel.getValueAt(i,
            config.articlecol), (String) data.editstableModel.getValueAt(i,
            config.editorcol), (String) data.editstableModel.getValueAt(i,
            config.summarycol), (String) data.editstableModel.getValueAt(i,
            config.projectcol), ((Integer) data.editstableModel.getValueAt(i,
            config.changedcol)).intValue(), data.editstableModel.getValueAt(i,
            config.specialcol) == Edit.SPECIAL_MOVE,
            ((TypeIndicator) data.editstableModel.getValueAt(i, config.typecol))
                .isNewpage()))
          data.editstableModel.removeRow(i);
      }
      listprecedence.setEnabled(true);
      if (listprecedence.isSelected()) {
        watchuserpages.setEnabled(true);
        watchmasschanges.setEnabled(true);
        watchpagemoves.setEnabled(true);
        watchnewpages.setEnabled(true);
      }

    }
    if (!queueedits.isSelected() && config.getBooleanProp("queueedits")) {
      data.pausededits.removeAllElements();
    }

    savefiles("workSettings");
  }

  public Object[] bubblesort(Object list[]) {
    int i, j;
    String temp;

    for (i = list.length - 1; i > 0; i--)
      for (j = 0; j < i; j++)
        if (((String) list[j]).compareTo((String) list[j + 1]) > 0) {
          temp = (String) list[j];
          list[j] = (String) list[j + 1];
          list[j + 1] = (String) temp;
        }
    return (list);
  }

  public void valueChanged(ListSelectionEvent e) {

    if (e.getValueIsAdjusting() == false
        && ((JList) e.getSource()).getSelectedIndex() >= 0) {
      if (e.getSource() == whitelistList) {
        userlistField.setText((String) config.whitelistModel
            .getElementAt(whitelistList.getSelectedIndex()));
      } else if (e.getSource() == blacklistList) {
        userlistField.setText((String) config.blacklistModel
            .getElementAt(blacklistList.getSelectedIndex()));
      } else if (e.getSource() == greylistList) {
        userlistField.setText((String) config.greylistModel
            .getElementAt(greylistList.getSelectedIndex()));
      } else if (e.getSource() == tempwhitelistList) {
        templistField.setText((String) config.tempwhitelistModel
              .getElementAt(tempwhitelistList.getSelectedIndex()));
      } else if (e.getSource() == tempblacklistList) {
        templistField.setText((String) config.tempblacklistModel
              .getElementAt(tempblacklistList.getSelectedIndex()));
      } else if (e.getSource() == watchlistList) {
        articleField.setText((String) config.watchlistModel
            .getElementAt(watchlistList.getSelectedIndex()));
      } else if (e.getSource() == tempwatchlistList) {
        articleField.setText((String) config.tempwatchlistModel
            .getElementAt(tempwatchlistList.getSelectedIndex()));
      } else if (e.getSource() == regexpwhiteList) {
        regexpField.setText((String) config.regexpwhiteModel
            .getElementAt(regexpwhiteList.getSelectedIndex()));
      } else if (e.getSource() == regexpblackList) {
        regexpField.setText((String) config.regexpblackModel
            .getElementAt(regexpblackList.getSelectedIndex()));
      }

    }
  }

  private boolean isEditstableScrollBarDown = false;

  public void updateBefore() {
    editstableScrollBar = editstableScroller.getVerticalScrollBar();
    isEditstableScrollBarDown = editstableScrollBar.getValue() > 0
        && editstableScrollBar.getValue()
           + editstableScrollBar.getVisibleAmount()+32
             >= editstableScrollBar.getMaximum();
  }

  // public void addnewtableentry(String pagename, String url, String
  // username, String summary, int change, boolean minor, boolean newpage,
  // String projname, boolean moveflag, String time)
  public void updateAfter() {
    // UPDATING the ui
    editstableScrollBar = editstableScroller.getVerticalScrollBar();

    editstableScroller.validate();

    if (config.getBooleanProp("autoscroll")
        && editstableScrollBar.getValue() > 0
        && isEditstableScrollBarDown) {
      //editstableScrollBar.setValue(editstableScrollBar.getMaximum()
      //    - editstableScrollBar.getVisibleAmount());
      int max = data.editstableModel.getRowCount() * config.editstableTable.getRowHeight();
      int vis = editstableScrollBar.getVisibleAmount();
      editstableScrollBar.setValues(max - vis, vis, 0, max);
    }

    config.editstableTable.repaint();
  }

  GregorianCalendar c;

  private void openBrowser(String url) {
    if (config.getBooleanProp("browserpick")) {
      try {
        BbrowserLauncher.openURL(url);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } else {
      try {
        Runtime.getRuntime().exec(config.getProperty("browser") + " " + url);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

  }

  public void mouseClicked(MouseEvent e) {
    StringTokenizer stt;

    if (e.getSource() == whitelistList) {
      JList jlist = (JList) e.getSource();
      int index = jlist.locationToIndex(e.getPoint());

      if (e.getClickCount() % 2 == 0) {
        showUserContributions((String) config.whitelistModel.elementAt(index));
      }
    } else if (e.getSource() == blacklistList) {
      JList jlist = (JList) e.getSource();
      int index = jlist.locationToIndex(e.getPoint());

      if (e.getClickCount() % 2 == 0) {
        showUserContributions((String) config.blacklistModel.elementAt(index));
      }
    } else if (e.getSource() == greylistList) {
      JList jlist = (JList) e.getSource();
      int index = jlist.locationToIndex(e.getPoint());

      if (e.getClickCount() % 2 == 0) {
        showUserContributions((String) config.greylistModel.elementAt(index));
      }
    } else if (e.getSource() == watchlistList) {
      JList jlist = (JList) e.getSource();
      int index = jlist.locationToIndex(e.getPoint());

      if (e.getClickCount() % 2 == 0) {
        try {
          String str = new String(((String) config.watchlistModel
              .elementAt(index)).replace(' ', '_'));

          stt = new StringTokenizer(str, "#");
          String st1 = stt.nextToken(), st2 = stt.nextToken();

          openBrowser("http://" + st2 + ".org/wiki/" + st1);

        } catch (Exception ex) {
        }
      }
    } else if (e.getSource() == config.editstableTable) {
      if (e.getClickCount() % 2 == 0 || config.getBooleanProp("singleclick")) {
        synchronized (config.editstableTable.getTreeLock()) {
          TableColumnModel columnModel = config.editstableTable
              .getColumnModel();
          int viewColumn = columnModel.getColumnIndexAtX(e.getX());
          int column = config.editstableTable
              .convertColumnIndexToModel(viewColumn);
          int target = e.getY();
          int row = -1, where = 0;
          while ((where < target)
              && (row < config.editstableTable.getRowCount())) {
            row++;
            where += config.editstableTable.getRowHeight(row);
          }
          if (column == config.xcol) {
            if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
              acceptEdit(row);
            } else {
              popupRow = data.editstableModel.getDataVector(row);
              popupRowNumber = row;
              updateXPopupMenu();
              xPopupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
          } else if (column == config.articlecol || column == config.summarycol) // article
            // or
            // summary
            try {
              if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
                openBrowser((String) data.editstableModel.getValueAt(row,
                    config.urlcol));
                if (config.getBooleanProp("removereviewed"))
                  data.editstableModel.removeRow(row);
              } else {
                popupRow = data.editstableModel.getDataVector(row);
                popupRowNumber = row;
                Short special = (Short)popupRow[config.specialcol];
                if (special == Edit.SPECIAL_NONE) {
                  updatePagePopupMenu();
                  pagePopupMenu.show(e.getComponent(), e.getX(), e.getY());
                } else if (special == Edit.SPECIAL_DELETE || special == Edit.SPECIAL_RENAME_USER) {
                  updateSpecialPage2PopupMenu();
                  specialPage2PopupMenu.show(e.getComponent(), e.getX(), e.getY());
                } else {
                  updateSpecialPagePopupMenu();
                  specialPagePopupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
              }
            } catch (Exception ex) {
            }
          else if (column == config.editorcol) // editor
            try {
              if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
                showUserContributions(row);
              } else {
                popupRow = data.editstableModel.getDataVector(row);
                popupRowNumber = row;
                updateUserPopupMenu();
                userPopupMenu.show(e.getComponent(), e.getX(), e.getY());
              }
            } catch (Exception ex) {
              ex.printStackTrace();
            }
          else if (column == config.projectcol) // editor
            try {
              if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
                showProjectRecentChanges(row);
              } else {
                popupRow = data.editstableModel.getDataVector(row);
                popupRowNumber = row;
                updateProjectPopupMenu();
                projectPopupMenu.show(e.getComponent(), e.getX(), e.getY());
              }
            } catch (Exception ex) {
              ex.printStackTrace();
            }
        }
      }
    }
  }

  public void mouseEntered(MouseEvent e) {
  }

  public void mouseExited(MouseEvent e) {
  }

  public void mousePressed(MouseEvent e) {
  }

  public void mouseReleased(MouseEvent e) {
  }

  private Image getImage() {
    Toolkit tk = Toolkit.getDefaultToolkit();
    Image image = tk.getImage("images/valid.png");
    return image;
  }

  public void savefiles(String reason) {
    config.savefiles();
    System.out.println("saving config: " + reason);
  }

  public void whitelistimport(String toadd) {
    if (!config.whitelistModel.contains(toadd)) {
      if (config.blacklistModel.contains(toadd)) {
        config.blacklistModel.removeElement(toadd);
      }
      if (config.greylistModel.contains(toadd)) {
        config.greylistModel.removeElement(toadd);
      }
      config.whitelistModel.addElement(toadd);
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

  private void loadConfig() {
    // 1) default configs hardcoded in instantion of configuration object
    // 2) configuration from vfdata.dat
    // 3) configuration from vfdata.xml

    // assign values to gui ellements
    syncConfig();

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

  private static boolean isValidIP(String IP) {
    Pattern validip = Pattern
        .compile("\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.)"
            + "{3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b");

    return validip.matcher(IP).matches();

  }

  private synchronized void saveSettings() {
    // #System.out.println("saving settings");
    // #System.out.println("ui : "+vandalism_en_box.isSelected());
    // #System.out.println("config : "+config.vandalism_en_box);

    // TODO fix stripformatting?
    // TODO showips broken
    config.setProperty("showips", showips.isSelected());
    config.setProperty("newpages", newpages.isSelected());
    config.setProperty("olddeleted", olddeleted.isSelected());
    config.setProperty("registerdeleted", registerdeleted.isSelected());
    config.setProperty("autoscroll", autoscroll.isSelected());
    config.setProperty("singleclick", singleclick.isSelected());
    config.setProperty("colorips", colorips.isSelected());
    config.setProperty("colornewusers", colornewusers.isSelected());
    config.setProperty("colornew", colornew.isSelected());
    config.setProperty("colorblist", colorblist.isSelected());
    config.setProperty("colorwatch", colorwatch.isSelected());
    config.setProperty("showwatch", showwatch.isSelected());
    config.setProperty("queueedits", queueedits.isSelected());
    config.setProperty("newversion", newversion.isSelected());
    config.setProperty("connectatstart", connectatstart.isSelected());
    config.setProperty("browserpick", browserpick.isSelected());
    config.setProperty("removereviewed", removereviewed.isSelected());
    config.setProperty("beeptempblack", beeptempblack.isSelected());
    config.setProperty("beeppermblack", beeppermblack.isSelected());
    config.setProperty("beeptempwatch", beeptempwatch.isSelected());
    config.setProperty("beeppermwatch", beeppermwatch.isSelected());
    config.setProperty("beepregexpblack", beepregexpblack.isSelected());
    config.setProperty("beepcollaborationwarning", beepcollaborationwarning.isSelected());
    config.setProperty("stripformatting", stripformatting.isSelected());
    config.setProperty("colorchanged", colorchanged.isSelected());
    config.setProperty("colormoves", colormoves.isSelected());
    config.setProperty("rwhiteuser", rwhiteuser.isSelected());
    config.setProperty("rwhitepage", rwhitepage.isSelected());
    config.setProperty("rwhitesummary", rwhitesummary.isSelected());
    config.setProperty("rblackuser", rblackuser.isSelected());
    config.setProperty("rblackpage", rblackpage.isSelected());
    config.setProperty("rblacksummary", rblacksummary.isSelected());
    config.setProperty("listprecedence", listprecedence.isSelected());
    config.setProperty("reversetable", reversetable.isSelected());
    config.setProperty("coloruserpage", coloruserpage.isSelected());
    config.setProperty("blackreverted", blackreverted.isSelected());
    config.setProperty("watchdeleted", watchdeleted.isSelected());
    config.setProperty("watchwarned", watchwarned.isSelected());
    config.setProperty("blackdeleted", blackdeleted.isSelected());
    config.setProperty("vandalism_nl_box", vandalism_nl_box.isSelected());
    config.setProperty("vandalism_fr_box", vandalism_fr_box.isSelected());
    config.setProperty("vandalism_it_box", vandalism_it_box.isSelected());
    config.setProperty("vandalism_en_box", vandalism_en_box.isSelected());
    config.setProperty("vandalism_cs_box", vandalism_cs_box.isSelected());
    config.setProperty("watchuserpages", watchuserpages.isSelected());
    config.setProperty("watchmasschanges", watchmasschanges.isSelected());
    config.setProperty("watchpagemoves", watchpagemoves.isSelected());
    config.setProperty("watchnewpages", watchnewpages.isSelected());
    config.setProperty("delpagepermButton", delpagepermButton.isSelected());
    config.setProperty("delpagetempButton", delpagetempButton.isSelected());
    config.setProperty("watchdelperButton", watchdelpermButton.isSelected());
    config.setProperty("watchdeltempButton", watchdeltempButton.isSelected());
    config.setProperty("watchwarnedperButton", watchwarnedpermButton.isSelected());
    config.setProperty("watchwarnedtempButton", watchwarnedtempButton.isSelected());
    config.setProperty("revertpermButton", revertpermButton.isSelected());
    config.setProperty("reverttempButton", reverttempButton.isSelected());
    config.setProperty("blockButton", blockButton.isSelected());
    config.setProperty("vipButton", vipButton.isSelected());
    config.setProperty("vipeditButton", vipeditButton.isSelected());
    config.setProperty("risk", risk.isSelected());
    //config.setProperty("colorips", colorips.isSelected());
    //config.setProperty("colornewusers", colornewusers.isSelected());
    //config.setProperty("colornew", colornew.isSelected());
    //config.setProperty("colorblist", colorblist.isSelected());
    //config.setProperty("colorwatch", colorwatch.isSelected());
    //config.setProperty("risk", risk.isSelected());
    //config.setProperty("colorchanged", colorchanged.isSelected());
    //config.setProperty("colormoves", colormoves.isSelected());
    //config.setProperty("browserpick", browserpick.isSelected());
    config.setProperty("browser", browserField.getText().trim());
    config.setProperty("lang", langCodes[langList.getSelectedIndex()]);
    config.setProperty("timeZone", timeZoneCodes[timeZoneList
        .getSelectedIndex()]);
    config.setProperty("feel", feelCodes[feelList.getSelectedIndex()]);
    config.setProperty("theme", themeCodes[themeList.getSelectedIndex()]);
    config.setProperty("changed", changedField.getText());
    //config.updateProperties();

    connectionsPanel.saveSettings();
  }

  public void initDefaultSettings() {
    config.setDefaultConfig();
    syncConfig();
  }

  public void syncConfig() {
    langList.setSelectedIndex(getLangInt());
    timeZoneList.setSelectedIndex(getTimeZoneInt());
    feelList.setSelectedIndex(getFeelInt());
    themeList.setEnabled(feelList.getSelectedIndex() == 0);
    themeLabel.setEnabled(feelList.getSelectedIndex() == 0);
    themeList.setSelectedIndex(getThemeInt());
    changedField.setText(config.getProperty("changed"));
    showips.setSelected(config.getBooleanProp("showips"));
    newpages.setSelected(config.getBooleanProp("newpages"));
    olddeleted.setSelected(config.getBooleanProp("olddeleted"));
    registerdeleted.setSelected(config.getBooleanProp("registerdeleted"));
    autoscroll.setSelected(config.getBooleanProp("autoscroll"));
    singleclick.setSelected(config.getBooleanProp("singleclick"));
    colorips.setSelected(config.getBooleanProp("colorips"));
    colornewusers.setSelected(config.getBooleanProp("colornewusers"));
    colornew.setSelected(config.getBooleanProp("colornew"));
    colorblist.setSelected(config.getBooleanProp("colorblist"));
    colorwatch.setSelected(config.getBooleanProp("colorwatch"));
    showwatch.setSelected(config.getBooleanProp("showwatch"));
    queueedits.setSelected(config.getBooleanProp("queueedits"));
    newversion.setSelected(config.getBooleanProp("newversion"));
    connectatstart.setSelected(config.getBooleanProp("connectatstart"));
    browserpick.setSelected(config.getBooleanProp("browserpick"));
    removereviewed.setSelected(config.getBooleanProp("removereviewed"));
    beeptempblack.setSelected(config.getBooleanProp("beeptempblack"));
    beeppermblack.setSelected(config.getBooleanProp("beeppermblack"));
    beeptempwatch.setSelected(config.getBooleanProp("beeptempwatch"));
    beeppermwatch.setSelected(config.getBooleanProp("beeppermwatch"));
    beepregexpblack.setSelected(config.getBooleanProp("beepregexpblack"));
    beepcollaborationwarning.setSelected(config.getBooleanProp("beepcollaborationwarning"));
    stripformatting.setSelected(config.getBooleanProp("stripformatting"));
    colorchanged.setSelected(config.getBooleanProp("colorchanged"));
    colormoves.setSelected(config.getBooleanProp("colormoves"));
    rwhiteuser.setSelected(config.getBooleanProp("rwhiteuser"));
    rwhitepage.setSelected(config.getBooleanProp("rwhitepage"));
    rwhitesummary.setSelected(config.getBooleanProp("rwhitesummary"));
    rblackuser.setSelected(config.getBooleanProp("rblackuser"));
    rblackpage.setSelected(config.getBooleanProp("rblackpage"));
    rblacksummary.setSelected(config.getBooleanProp("rblacksummary"));
    listprecedence.setSelected(config.getBooleanProp("listprecedence"));
    reversetable.setSelected(config.getBooleanProp("reversetable"));
    coloruserpage.setSelected(config.getBooleanProp("coloruserpage"));
    blackreverted.setSelected(config.getBooleanProp("blackreverted"));
    watchdeleted.setSelected(config.getBooleanProp("watchdeleted"));
    watchwarned.setSelected(config.getBooleanProp("watchwarned"));
    blackdeleted.setSelected(config.getBooleanProp("blackdeleted"));
    vandalism_nl_box.setSelected(config.getBooleanProp("vandalism_nl_box"));
    vandalism_en_box.setSelected(config.getBooleanProp("vandalism_en_box"));
    vandalism_fr_box.setSelected(config.getBooleanProp("vandalism_fr_box"));
    vandalism_it_box.setSelected(config.getBooleanProp("vandalism_it_box"));
    vandalism_cs_box.setSelected(config.getBooleanProp("vandalism_cs_box"));
    // vandalism_box.setSelected(config.getBooleanProp("vandalism_box"));

    watchuserpages.setSelected(config.getBooleanProp("watchuserpages"));

    watchmasschanges.setSelected(config.getBooleanProp("watchmasschanges"));
    watchpagemoves.setSelected(config.getBooleanProp("watchpagemoves"));
    watchnewpages.setSelected(config.getBooleanProp("watchnewpages"));
    delpagepermButton.setSelected(config.getBooleanProp("delpagepermButton"));
    delpagetempButton.setSelected(config.getBooleanProp("delpagetempButton"));
    watchdelpermButton.setSelected(config.getBooleanProp("watchdelpermButton"));
    watchdeltempButton.setSelected(config.getBooleanProp("watchdeltempButton"));
    watchwarnedpermButton.setSelected(config.getBooleanProp("watchwarnedpermButton"));
    watchwarnedtempButton.setSelected(config.getBooleanProp("watchwarnedtempButton"));
    revertpermButton.setSelected(config.getBooleanProp("revertpermButton"));
    reverttempButton.setSelected(config.getBooleanProp("reverttempButton"));
    blockButton.setSelected(config.getBooleanProp("blockButton"));
    vipButton.setSelected(config.getBooleanProp("vipButton"));
    vipeditButton.setSelected(config.getBooleanProp("vipeditButton"));
    risk.setSelected(config.getBooleanProp("risk"));

    watchdeletedActionPerformed(null);
    watchwarnedActionPerformed(null);
    blackdeletedActionPerformed(null);
    blackrevertedActionPerformed(null);
    browserpickActionPerformed(null);
  }

  public void updateStatus(String msg) {
    statusLabel.setText(msg);
  }

  private int getLangInt() {
    for (int j = 0; j < langCodes.length; j++) {
      if (language.equalsIgnoreCase(langCodes[j])) {
        return j;
      }
    }
    return 0;
  }

  private int getTimeZoneInt() {
    for (int j = 0; j < timeZoneCodes.length; j++) {
      if (config.timeZone.getID().equalsIgnoreCase(timeZoneCodes[j])) {
        return j;
      }
    }
    return 0;
  }

  private int getFeelInt() {
    for (int j = 0; j < feelCodes.length; j++) {
      if (feel.equalsIgnoreCase(feelCodes[j])) {
        return j;
      }
    }
    return 0;
  }

  private int getThemeInt() {
    for (int j = 0; j < themeCodes.length; j++) {
      if (theme.equalsIgnoreCase(themeCodes[j])) {
        return j;
      }
    }
    return 0;
  }

  private void connectIRC() {
    synchronized (IRCLOCK) {
      System.out.println("connectIRC function");
      if (ircB != null && ircB.startThread.isAlive()) {
        System.out.println("Previous connection still running");
        return;
      }
      saveSettings();
      savefiles("connectIRC");
      disconnectButton.setEnabled(true);
      pauseButton.setEnabled(true);
      config.setProperty("channel", channelField.getText());

      // connect
      if (ircB == null) {
        ircB = new IRC(channelField.getText(), serverField.getText(), portField
            .getText(), data, this);
      } else {// connect wikimediachannels
        ircB.setWikimediaChannels(channelField.getText());
      }
      // connect vandalismchannels

      // TODO Beren brat to z konfigurace nebo primo z checkboxu?
      // ircB.addVChannelToResolve("#wikipedia-nl-vandalism", vandalism_nl_box
      // .isSelected());
      // ircB.addVChannelToResolve("#wikipedia-it-vandalism", vandalism_it_box
      // .isSelected());
      // ircB.addVChannelToResolve("#vandalism-en-wp-2", vandalism_en_box
      // .isSelected());
      // ircB
      // .addVChannelToResolve("#vandalism-fr-wp", vandalism_fr_box.isSelected());
      // ircB.addVChannelToResolve("#wikipedia-cs-vandalfighter", vandalism_cs_box
      // .isSelected());

      ircB.startThread = new Thread(ircB);
      ircB.startThread.start();

      // connected = true;
      config.pausevar = false;
      // pauseButton.setText("Pause (Alt-A)");
      serverField.setEditable(false);
      portField.setEditable(false);
    }
  }

  private void disconnectIRC() {
    synchronized (IRCLOCK) {
      System.out.println("disconnectIRC function");
      saveSettings();
      savefiles("connectIRC");
      // connectButton.setEnabled(true);
      // pauseButton.setEnabled(false);

      //ircB.updateConfig();
      // String[] channels = config.getProperty("channel").split(",");

      if (ircB != null) {
        ircB.quit("quit :" + messages.getString("SoftwareName") + config.version
            + messages.getString("IRCquitMessage"));

      }

      config.pausevar = false;
      channelField.setText(config.getProperty("channel"));
      serverField.setEditable(true);
      portField.setEditable(true);
      ircB.printStatus();
    }
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  // <editor-fold defaultstate="collapsed" desc=" Generated Code
  // ">//GEN-BEGIN:initComponents
  private void initComponents() {
    java.awt.GridBagConstraints gridBagConstraints;

    blackrevertedGroup = new javax.swing.ButtonGroup();
    BColumnGroup = new javax.swing.ButtonGroup();
    blackdeletedGroup = new javax.swing.ButtonGroup();
    watchdeletedGroup = new javax.swing.ButtonGroup();
    watchwarnedGroup = new javax.swing.ButtonGroup();
    jPanel1 = new javax.swing.JPanel();
    statusLabel = new javax.swing.JLabel();
    tab = new javax.swing.JTabbedPane();
    userlistPanel2 = new javax.swing.JPanel();
    userListPanel = new javax.swing.JPanel();
    watchlistPanel = new javax.swing.JPanel();
    watchlists = new javax.swing.JPanel();
    watchlistButtons = new javax.swing.JPanel();
    lists = new javax.swing.JPanel();
    jScrollPane6 = new javax.swing.JScrollPane();
    whitelistList = new javax.swing.JList();
    jScrollPane7 = new javax.swing.JScrollPane();
    blacklistList = new javax.swing.JList();
    greylistList = new javax.swing.JList();
    buttons = new javax.swing.JPanel();
    jLabel26 = new javax.swing.JLabel();
    userlistField = new javax.swing.JTextField();
    whitelistAdd = new javax.swing.JButton();
    blacklistAdd = new javax.swing.JButton();
    greylistAdd = new javax.swing.JButton();
    whitelistRemove = new javax.swing.JButton();
    blacklistRemove = new javax.swing.JButton();
    greylistRemove = new javax.swing.JButton();
    sortwhitelistButton = new javax.swing.JButton();
    sortblacklistButton = new javax.swing.JButton();
    sortgreylistButton = new javax.swing.JButton();
    jLabel27 = new javax.swing.JLabel();
    importbotsButton = new javax.swing.JButton();
    whitelistimportField = new javax.swing.JTextField();
    importadminsButton = new javax.swing.JButton();
    jPanel26 = new javax.swing.JPanel();
    jPanel32 = new javax.swing.JPanel();
    jPanel31 = new javax.swing.JPanel();
    jLabel28 = new javax.swing.JLabel();
    articleField = new javax.swing.JTextField();
    watchlistAdd = new javax.swing.JButton();
    watchlistRemove = new javax.swing.JButton();
    sortarticlesButton = new javax.swing.JButton();
    watchlistimportButton = new javax.swing.JButton();
    jScrollPane8 = new javax.swing.JScrollPane();
    watchlistList = new javax.swing.JList();
    temporaryPanel = new javax.swing.JPanel();
    jPanel24 = new javax.swing.JPanel();
    jScrollPane3 = new javax.swing.JScrollPane();
    tempwhitelistList = new javax.swing.JList();
    jScrollPane4 = new javax.swing.JScrollPane();
    tempblacklistList = new javax.swing.JList();
    jScrollPane5 = new javax.swing.JScrollPane();
    jScrollPane11 = new javax.swing.JScrollPane();
    tempwatchlistList = new javax.swing.JList();
    jPanel25 = new javax.swing.JPanel();
    jLabel24 = new javax.swing.JLabel();
    templistField = new javax.swing.JTextField();
    jLabel25 = new javax.swing.JLabel();
    tempwhitelistAdd = new javax.swing.JButton();
    tempblacklistAdd = new javax.swing.JButton();
    tempwatchlistAdd = new javax.swing.JButton();
    tempwhitelistRemove = new javax.swing.JButton();
    tempblacklistRemove = new javax.swing.JButton();
    tempwatchlistRemove = new javax.swing.JButton();
    sorttempwhitelistButton = new javax.swing.JButton();
    sorttempblacklistButton = new javax.swing.JButton();
    sorttempwatchlistButton = new javax.swing.JButton();
    regexPanel = new javax.swing.JPanel();
    jPanel21 = new javax.swing.JPanel();
    jPanel22 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    regexpwhiteList = new javax.swing.JList();
    jScrollPane2 = new javax.swing.JScrollPane();
    regexpblackList = new javax.swing.JList();
    jPanel23 = new javax.swing.JPanel();
    jLabel23 = new javax.swing.JLabel();
    regexpField = new javax.swing.JTextField();
    regexpwhiteAdd = new javax.swing.JButton();
    regexpblackAdd = new javax.swing.JButton();
    regexpwhiteRemove = new javax.swing.JButton();
    regexpblackRemove = new javax.swing.JButton();
    sortregexpwhitelistButton = new javax.swing.JButton();
    sortregexpblacklistButton = new javax.swing.JButton();
    configPanel1 = new javax.swing.JPanel();
    miscConfigPanel = new javax.swing.JPanel();
    miscConfigLabel = new JLabel();
    localizeConfigPanel = new javax.swing.JPanel();
    localizeConfigLabel = new JLabel();
    jLabel2 = new javax.swing.JLabel();
    langList = new JComboBox(langStrings);
    timeZoneList = new JComboBox(timeZoneStrings);
    jLabel29 = new javax.swing.JLabel();
    jLabel30 = new javax.swing.JLabel();
    feelList = new JComboBox(feelStrings);
    themeLabel = new javax.swing.JLabel();
    themeList = new JComboBox(themeStrings);

    langList.setSelectedIndex(getLangInt());
    timeZoneList.setSelectedIndex(getTimeZoneInt());
    feelList.setSelectedIndex(getFeelInt());
    themeList.setEnabled(feelList.getSelectedIndex() == 0);
    themeLabel.setEnabled(feelList.getSelectedIndex() == 0);
    themeList.setSelectedIndex(getThemeInt());
    olddeleted = new javax.swing.JCheckBox();
    registerdeleted = new javax.swing.JCheckBox();
    autoscroll = new javax.swing.JCheckBox();
    singleclick = new javax.swing.JCheckBox();
    queueedits = new javax.swing.JCheckBox();
    removereviewed = new javax.swing.JCheckBox();
    stripformatting = new javax.swing.JCheckBox();
    newversion = new javax.swing.JCheckBox();
    connectatstart = new javax.swing.JCheckBox();
    reversetable = new javax.swing.JCheckBox();
    jPanel3 = new javax.swing.JPanel();
    DefaultProject = new javax.swing.JLabel();
    defaultProjectField = new javax.swing.JTextField();
    jPanel12 = new javax.swing.JPanel();
    browserpick = new javax.swing.JCheckBox();
    browser2 = new javax.swing.JPanel();
    browserchooserButton = new javax.swing.JButton();
    browserField = new javax.swing.JTextField();
    jPanel11 = new javax.swing.JPanel();
    BColumnTakesYouTo = new javax.swing.JLabel();
    blockRadioPanel = new javax.swing.JPanel();
    blockButton = new javax.swing.JRadioButton();
    vipButton = new javax.swing.JRadioButton();
    vipeditButton = new javax.swing.JRadioButton();
    jSeparator1 = new javax.swing.JSeparator();
    showips = new javax.swing.JCheckBox();
    newpages = new javax.swing.JCheckBox();
    showwatch = new javax.swing.JCheckBox();
    jSeparator2 = new javax.swing.JSeparator();
    listprecedence = new javax.swing.JCheckBox();
    watchuserpages = new javax.swing.JCheckBox();
    watchmasschanges = new javax.swing.JCheckBox();
    watchpagemoves = new javax.swing.JCheckBox();
    watchnewpages = new javax.swing.JCheckBox();
    beeptempblack = new javax.swing.JCheckBox();
    beeppermblack = new javax.swing.JCheckBox();
    beeptempwatch = new javax.swing.JCheckBox();
    beeppermwatch = new javax.swing.JCheckBox();
    beepregexpblack = new javax.swing.JCheckBox();
    beepcollaborationwarning = new javax.swing.JCheckBox();
    jSeparator3 = new javax.swing.JSeparator();
    rwhiteuser = new javax.swing.JCheckBox();
    rwhitepage = new javax.swing.JCheckBox();
    rwhitesummary = new javax.swing.JCheckBox();
    rblackuser = new javax.swing.JCheckBox();
    rblackpage = new javax.swing.JCheckBox();
    rblacksummary = new javax.swing.JCheckBox();
    jSeparator4 = new javax.swing.JSeparator();
    blackreverted = new javax.swing.JCheckBox();
    revertedUsers = new javax.swing.JPanel();
    revertpermButton = new javax.swing.JRadioButton();
    reverttempButton = new javax.swing.JRadioButton();
    blackdeleted = new javax.swing.JCheckBox();
    speediedUsers = new javax.swing.JPanel();
    delpagepermButton = new javax.swing.JRadioButton();
    delpagetempButton = new javax.swing.JRadioButton();
    watchdeleted = new javax.swing.JCheckBox();
    watchspeediedArticles = new javax.swing.JPanel();
    watchdelpermButton = new javax.swing.JRadioButton();
    watchdeltempButton = new javax.swing.JRadioButton();
    watchwarned = new javax.swing.JCheckBox();
    watchwarnedPanel = new javax.swing.JPanel();
    watchwarnedpermButton = new javax.swing.JRadioButton();
    watchwarnedtempButton = new javax.swing.JRadioButton();
    jSeparator5 = new javax.swing.JSeparator();
    jPanel15 = new javax.swing.JPanel();
    exportchooserButton1 = new javax.swing.JButton();
    exportField1 = new javax.swing.JTextField();
    exportButton1 = new javax.swing.JButton();
    jPanel14 = new javax.swing.JPanel();
    importchooserButton = new javax.swing.JButton();
    importField = new javax.swing.JTextField();
    importButton = new javax.swing.JButton();
    jSeparator6 = new javax.swing.JSeparator();
    risk = new javax.swing.JCheckBox();
    jPanel2 = new javax.swing.JPanel();
    colorblist = new javax.swing.JCheckBox();
    colorblistButton = new javax.swing.JButton();
    jPanel4 = new javax.swing.JPanel();
    colorwatch = new javax.swing.JCheckBox();
    colorwatchButton = new javax.swing.JButton();
    jPanel5 = new javax.swing.JPanel();
    colorchanged = new javax.swing.JCheckBox();
    changedField = new javax.swing.JTextField();
    changedField.setText(config.getProperty("changedField"));
    colorchangedButton = new javax.swing.JButton();
    jPanel6 = new javax.swing.JPanel();
    colornew = new javax.swing.JCheckBox();
    colornewButton = new javax.swing.JButton();
    jPanel7 = new javax.swing.JPanel();
    jPanel78 = new javax.swing.JPanel();
    colorips = new javax.swing.JCheckBox();
    coloripsButton = new javax.swing.JButton();
    colornewusers = new javax.swing.JCheckBox();
    colornewusersButton = new javax.swing.JButton();
    jPanel8 = new javax.swing.JPanel();
    coloruserpage = new javax.swing.JCheckBox();
    coloruserpageButton = new javax.swing.JButton();
    jPanel9 = new javax.swing.JPanel();
    colormoves = new javax.swing.JCheckBox();
    colormovesButton = new javax.swing.JButton();
    configCtrlsPanel = new javax.swing.JPanel();
    configDefaults = new javax.swing.JButton();
    configCancel = new javax.swing.JButton();
    configSave = new javax.swing.JButton();
    IRCPannel = new javax.swing.JPanel();
    connectionsPanel = new ConnectionsPanel(this);
    irctoptopPanel1 = new javax.swing.JPanel();
    jPanel19 = new javax.swing.JPanel();
    jPanel10 = new javax.swing.JPanel();
    vandalism_nl_box = new javax.swing.JCheckBox();
    vandalism_fr_box = new javax.swing.JCheckBox();
    vandalism_en_box = new javax.swing.JCheckBox();
    vandalism_it_box = new javax.swing.JCheckBox();
    vandalism_cs_box = new javax.swing.JCheckBox();
    jPanel20 = new javax.swing.JPanel();
    VandalismStreamButton = new javax.swing.JButton();
    jLabel21 = new javax.swing.JLabel();
    jSeparator7 = new javax.swing.JSeparator();
    jLabel22 = new javax.swing.JLabel();
    jPanel13 = new javax.swing.JPanel();
    mediawikistreamCheckbox = new javax.swing.JCheckBox();
    jPanel16 = new javax.swing.JPanel();
    jLabel3 = new javax.swing.JLabel();
    channelField = new javax.swing.JTextField();
    jLabel4 = new javax.swing.JLabel();
    serverField = new javax.swing.JTextField();
    jLabel5 = new javax.swing.JLabel();
    portField = new javax.swing.JTextField();
    jPanel17 = new javax.swing.JPanel();
    jPanel27 = new javax.swing.JPanel();
    jPanel40 = new javax.swing.JPanel();
    jPanel41 = new javax.swing.JPanel();
    ircconButton = new javax.swing.JButton();
    ircdisconnectButton = new javax.swing.JButton();
    jLabel6 = new javax.swing.JLabel();
    jLabel7 = new javax.swing.JLabel();
    jLabel8 = new javax.swing.JLabel();
    jLabel9 = new javax.swing.JLabel();
    jLabel10 = new javax.swing.JLabel();
    jLabel11 = new javax.swing.JLabel();
    jLabel12 = new javax.swing.JLabel();
    jLabel13 = new javax.swing.JLabel();
    jLabel14 = new javax.swing.JLabel();
    jLabel15 = new javax.swing.JLabel();
    editorPane1 = new JEditorPane();
    aboutPanel = new javax.swing.JPanel();
    jScrollPane10 = new javax.swing.JScrollPane();
    jTextPane1 = new javax.swing.JTextPane();
    colorConfigPanel = new javax.swing.JPanel();
    colorConfigLabel = new JLabel();
    regexpConfigPanel = new javax.swing.JPanel();
    regexpConfigLabel = new JLabel();
    listsConfigPanel = new javax.swing.JPanel();
    listsConfigLabel = new JLabel();
    automaticConfigPanel = new javax.swing.JPanel();
    automaticConfigLabel = new JLabel();
    showConfigPanel = new javax.swing.JPanel();
    showConfigLabel = new JLabel();
    beepConfigPanel = new javax.swing.JPanel();
    beepConfigLabel = new JLabel();
    startConfigPanel = new javax.swing.JPanel();
    startConfigLabel = new JLabel();
    browserConfigPanel = new javax.swing.JPanel();
    browserConfigLabel = new JLabel();

    userPopupMenu = new JPopupMenu();

    userContributionsPopupItem = new JMenuItem(messages
        .getString("UserContributionsMenu"));
    userTalkPagePopupItem = new JMenuItem(messages
        .getString("UserTalkPageMenu"));
    userBlockPopupItem = new JMenuItem(messages.getString("UserBlockMenu"));
    user2WhitelistPopupItem = new JMenuItem(messages
        .getString("User2WhitelistMenu"));
    user2GreylistPopupItem = new JMenuItem(messages
        .getString("User2GreylistMenu"));
    user2BlacklistPopupItem = new JMenuItem(messages
        .getString("User2BlacklistMenu"));
//    user2WatchlistPopupItem = new JMenuItem(messages
//        .getString("User2WatchlistMenu"));
    user2TempWhitelistPopupItem = new JMenuItem(messages
        .getString("User2TempWhitelistMenu"));
    user2TempBlacklistPopupItem = new JMenuItem(messages
        .getString("User2TempBlacklistMenu"));
//    user2TempWatchlistPopupItem = new JMenuItem(messages
//        .getString("User2TempWatchlistMenu"));
    userNamePopupLabel = new JLabel();
    userNamePopupLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,
        14, 0, 0));

    userPopupMenu.add(userContributionsPopupItem);
    userPopupMenu.add(userTalkPagePopupItem);
    userPopupMenu.add(userBlockPopupItem);
    userPopupMenu.addSeparator();
    userPopupMenu.add(user2WhitelistPopupItem);
    userPopupMenu.add(user2GreylistPopupItem);
    userPopupMenu.add(user2BlacklistPopupItem);
//    userPopupMenu.add(user2WatchlistPopupItem);
    userPopupMenu.addSeparator();
    userPopupMenu.add(user2TempWhitelistPopupItem);
    userPopupMenu.add(user2TempBlacklistPopupItem);
//    userPopupMenu.add(user2TempWatchlistPopupItem);
    userPopupMenu.addSeparator();
    {
      JPanel mjp = new JPanel();
      mjp.setLayout(new FlowLayout(FlowLayout.LEFT));
      userPopupMenu.add(mjp);
      mjp.add(userNamePopupLabel);
    }

    userContributionsPopupItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showUserContributions((String) popupRow[config.editorcol],
            (String) popupRow[config.projectcol]);
      }
    });
    userTalkPagePopupItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showUserTalkPage((String) popupRow[config.editorcol],
            (String) popupRow[config.projectcol]);
      }
    });
    userBlockPopupItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showBlockPage((String) popupRow[config.editorcol],
            (String) popupRow[config.projectcol]);
      }
    });
    user2WhitelistPopupItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        addRemoveFromWhitelist((String) popupRow[config.editorcol],
            (String) popupRow[config.projectcol]);
      }
    });
    user2GreylistPopupItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        addRemoveFromGreylist((String) popupRow[config.editorcol],
            (String) popupRow[config.projectcol]);
      }
    });
    user2BlacklistPopupItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        addRemoveFromBlacklist((String) popupRow[config.editorcol],
            (String) popupRow[config.projectcol]);
      }
    });
//    user2WatchlistPopupItem.addActionListener(new ActionListener() {
//      public void actionPerformed(ActionEvent e) {
//        addRemoveFromWatchlist((String) popupRow[config.editorcol],
//            (String) popupRow[config.projectcol]);
//      }
//    });
    user2TempWhitelistPopupItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        addRemoveFromTempWhitelist((String) popupRow[config.editorcol],
            (String) popupRow[config.projectcol]);
      }
    });
    user2TempBlacklistPopupItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        addRemoveFromTempBlacklist((String) popupRow[config.editorcol],
            (String) popupRow[config.projectcol]);
      }
    });
//    user2TempWatchlistPopupItem.addActionListener(new ActionListener() {
//      public void actionPerformed(ActionEvent e) {
//        addRemoveFromTempWatchlist((String) popupRow[config.editorcol],
//            (String) popupRow[config.projectcol]);
//      }
//    });

    projectPopupMenu = new JPopupMenu();

    projectRCPopupItem = new JMenuItem(messages
        .getString("ProjectRecentChangesMenu"));
    projectMainPagePopupItem = new JMenuItem(messages
        .getString("ProjectMainPageMenu"));
    projectNamePopupLabel = new JLabel();
    projectNamePopupLabel.setBorder(javax.swing.BorderFactory
        .createEmptyBorder(0, 14, 0, 0));

    projectPopupMenu.add(projectRCPopupItem);
    projectPopupMenu.add(projectMainPagePopupItem);
    projectPopupMenu.addSeparator();
    {
      JPanel mjp = new JPanel();
      mjp.setLayout(new FlowLayout(FlowLayout.LEFT));
      projectPopupMenu.add(mjp);
      mjp.add(projectNamePopupLabel);
    }

    projectRCPopupItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showProjectRecentChanges((String) popupRow[config.projectcol]);
      }
    });
    projectMainPagePopupItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showProjectMainPage((String) popupRow[config.projectcol]);
      }
    });

    xPopupMenu = new JPopupMenu();

    xAcceptPopupItem = new JMenuItem(messages.getString("XAcceptMenu"));
    xSimplyDeletePopupItem = new JMenuItem(messages
        .getString("XSimplyDeleteMenu"));
    xWarningPopupItem = new JMenuItem(messages.getString("XWarningMenu"));
    xWarningAndDeletePopupItem = new JMenuItem(messages
        .getString("XWarningAndDeleteMenu"));
    xNamePopupLabel = new JLabel();
    xNamePopupLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,
        14, 0, 0));
    xPopupMenu.add(xAcceptPopupItem);
    xPopupMenu.add(xSimplyDeletePopupItem);
    xPopupMenu.add(xWarningPopupItem);
    xPopupMenu.add(xWarningAndDeletePopupItem);
    xPopupMenu.addSeparator();
    {
      JPanel mjp = new JPanel();
      mjp.setLayout(new FlowLayout(FlowLayout.LEFT));
      xPopupMenu.add(mjp);
      mjp.add(xNamePopupLabel);
    }
    xAcceptPopupItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        acceptEdit(popupRowNumber, popupRow);
      }
    });
    xSimplyDeletePopupItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        simplyDeleteEdit(popupRowNumber, popupRow);
      }
    });
    xWarningPopupItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        warnEdit(popupRow);
      }
    });
    xWarningAndDeletePopupItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        warnEditAndRemove(popupRowNumber, popupRow);
      }
    });

    pagePopupMenu = new JPopupMenu();

    pageDiffPopupItem = new JMenuItem(messages.getString("PageDiffMenu"));
    pageHistoryPopupItem = new JMenuItem(messages.getString("PageHistoryMenu"));
    pageActualPopupItem = new JMenuItem(messages.getString("PageActualMenu"));
    pageLockPopupItem = new JMenuItem(messages.getString("PageLockMenu"));
    page2WatchlistPopupItem = new JMenuItem(messages
        .getString("Page2WatchlistMenu"));
    page2TempWatchlistPopupItem = new JMenuItem(messages
        .getString("Page2TempWatchlistMenu"));
    pageNamePopupLabel = new JLabel();
    pageNamePopupLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,
        14, 0, 0));
    pagePopupMenu.add(pageDiffPopupItem);
    pagePopupMenu.add(pageHistoryPopupItem);
    pagePopupMenu.add(pageActualPopupItem);
    pagePopupMenu.add(pageLockPopupItem);
    pagePopupMenu.add(page2WatchlistPopupItem);
    pagePopupMenu.add(page2TempWatchlistPopupItem);
    pagePopupMenu.addSeparator();
    {
      JPanel mjp = new JPanel();
      mjp.setLayout(new FlowLayout(FlowLayout.LEFT));
      pagePopupMenu.add(mjp);
      mjp.add(pageNamePopupLabel);
    }
    pageDiffPopupItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showPageDiff(popupRowNumber, popupRow);
      }
    });
    pageHistoryPopupItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showPageHistory((String) popupRow[config.articlecol],
            (String) popupRow[config.projectcol]);
      }
    });
    pageActualPopupItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showActualPage((String) popupRow[config.articlecol],
            (String) popupRow[config.projectcol]);
      }
    });
    pageLockPopupItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showLockPage((String) popupRow[config.articlecol],
            (String) popupRow[config.projectcol]);
      }
    });
    page2WatchlistPopupItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        addRemoveFromWatchlist((String) popupRow[config.articlecol],
            (String) popupRow[config.projectcol]);
      }
    });
    page2TempWatchlistPopupItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        addRemoveFromTempWatchlist((String) popupRow[config.articlecol],
            (String) popupRow[config.projectcol]);
      }
    });

    specialPagePopupMenu = new JPopupMenu();

    specialPageActionPopupItem = new JMenuItem("dummy");
    specialPageLogPopupItem = new JMenuItem(messages.getString("SpecialPageLogMenu"));
    specialPageNamePopupLabel = new JLabel();
    specialPageNamePopupLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,
        14, 0, 0));
    specialPagePopupMenu.add(specialPageActionPopupItem);
    specialPagePopupMenu.add(specialPageLogPopupItem);
    specialPagePopupMenu.addSeparator();
    {
      JPanel mjp = new JPanel();
      mjp.setLayout(new FlowLayout(FlowLayout.LEFT));
      specialPagePopupMenu.add(mjp);
      mjp.add(specialPageNamePopupLabel);
    }
    specialPageActionPopupItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        openBrowser((String) data.editstableModel.getValueAt(popupRowNumber,
            config.urlcol));
      }
    });
    specialPageLogPopupItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showSpecialPageLog(popupRow);
      }
    });

    specialPage2PopupMenu = new JPopupMenu();

    specialPage2LogPopupItem = new JMenuItem(messages.getString("SpecialPageLogMenu"));
    specialPage2NamePopupLabel = new JLabel();
    specialPage2NamePopupLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,
        14, 0, 0));
    specialPage2PopupMenu.add(specialPage2LogPopupItem);
    specialPage2PopupMenu.addSeparator();
    {
      JPanel mjp = new JPanel();
      mjp.setLayout(new FlowLayout(FlowLayout.LEFT));
      specialPage2PopupMenu.add(mjp);
      mjp.add(specialPage2NamePopupLabel);
    }
    specialPage2LogPopupItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        openBrowser((String) data.editstableModel.getValueAt(popupRowNumber,
            config.urlcol));
      }
    });

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setTitle(messages.getString("SoftwareName")+config.version);
    setIconImage(getImage());
    jPanel1.setLayout(new java.awt.BorderLayout());

    statusLabel.setText(messages.getString("Ready"));
    jPanel1.add(statusLabel, java.awt.BorderLayout.CENTER);

    getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

    tab.setName("configuration");

    {
      userListPanel.setLayout(new TreeColumnGridBagLayout(jLabel13, jLabel14, jLabel15));

      GridBagConstraints c = new GridBagConstraints();
      int row = 0;

      c.gridx = 0;
      c.gridy = row++;
      c.weightx = 1;
      Font f = jLabel13.getFont();
      Font headerFont = new Font(f.getName(), Font.BOLD, f.getSize() + 2);
      jLabel13.setFont(headerFont);
      jLabel13.setHorizontalAlignment(SwingConstants.CENTER);
      Insets defaultInsets = c.insets;
      c.insets = new java.awt.Insets(5, 5, 5, 5);
      //c.insets = new Insets(2,2,2,2);
      c.fill = GridBagConstraints.BOTH;
      jLabel13.setText(messages.getString("Whitelist"));
      userListPanel.add(jLabel13, c);

      c.gridx = 1;
      jLabel14.setFont(headerFont);
      jLabel14.setHorizontalAlignment(SwingConstants.CENTER);
      jLabel14.setText(messages.getString("Greylist"));
      userListPanel.add(jLabel14, c);

      c.gridx = 2;
      jLabel15.setFont(headerFont);
      jLabel15.setHorizontalAlignment(SwingConstants.CENTER);
      jLabel15.setText(messages.getString("Blacklist"));
      userListPanel.add(jLabel15, c);

      c.gridx = 0;
      c.gridy = row++;
      c.weighty = 1;
      c.insets = defaultInsets;
      whitelistList.setModel(config.whitelistModel);
      whitelistList
      .addListSelectionListener(new javax.swing.event.ListSelectionListener() {
        public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
          whitelistListValueChanged(evt);
        }
      });
      jScrollPane6.setViewportView(whitelistList);
      userListPanel.add(jScrollPane6, c);

      c.gridx = 1;
      greylistList.setModel(config.greylistModel);
      greylistList
      .addListSelectionListener(new javax.swing.event.ListSelectionListener() {
        public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
          greylistListValueChanged(evt);
        }
      });
      jScrollPane8.setViewportView(greylistList);
      userListPanel.add(jScrollPane8, c);

      c.gridx = 2;
      blacklistList.setModel(config.blacklistModel);
      blacklistList
      .addListSelectionListener(new javax.swing.event.ListSelectionListener() {
        public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
          blacklistListValueChanged(evt);
        }
      });
      jScrollPane7.setViewportView(blacklistList);
      userListPanel.add(jScrollPane7, c);

      c.gridx = 0;
      c.gridy = row++;
      c.weighty = 0;
      jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
      jLabel26.setLabelFor(userlistField);
      jLabel26.setText(messages.getString("UsernameOrIP") + ": ");
      userListPanel.add(jLabel26, c);

      c.gridx = 1;
      userListPanel.add(userlistField, c);

      c.gridx = 0;
      c.gridy = row++;
      whitelistAdd.setText(messages.getString("AddToWhitelist"));
      whitelistAdd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          whitelistAddActionPerformed(evt);
        }
      });
      userListPanel.add(whitelistAdd, c);

      c.gridx = 1;
      greylistAdd.setText(messages.getString("AddToGreylist"));
      greylistAdd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          greylistAddActionPerformed(evt);
        }
      });
      userListPanel.add(greylistAdd, c);

      c.gridx = 2;
      blacklistAdd.setText(messages.getString("AddToBlacklist"));
      blacklistAdd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          blacklistAddActionPerformed(evt);
        }
      });
      userListPanel.add(blacklistAdd, c);

      c.gridx = 0;
      c.gridy = row++;
      whitelistRemove.setText(messages.getString("RemoveFromWhitelist"));
      whitelistRemove.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          whitelistRemoveActionPerformed(evt);
        }
      });
      userListPanel.add(whitelistRemove, c);

      c.gridx = 1;
      greylistRemove.setText(messages.getString("RemoveFromGreylist"));
      greylistRemove.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          greylistRemoveActionPerformed(evt);
        }
      });
      userListPanel.add(greylistRemove, c);

      c.gridx = 2;
      blacklistRemove.setText(messages.getString("RemoveFromBlacklist"));
      blacklistRemove.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          blacklistRemoveActionPerformed(evt);
        }
      });
      userListPanel.add(blacklistRemove, c);

      c.gridx = 0;
      c.gridy = row++;
      sortwhitelistButton.setText(messages.getString("SortWhitelist"));
      sortwhitelistButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          sortwhitelistButtonActionPerformed(evt);
        }
      });
      userListPanel.add(sortwhitelistButton, c);

      c.gridx = 1;
      sortgreylistButton.setText(messages.getString("SortGreylist"));
      sortgreylistButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          sortgreylistButtonActionPerformed(evt);
        }
      });
      userListPanel.add(sortgreylistButton, c);

      c.gridx = 2;
      sortblacklistButton.setText(messages.getString("SortBlacklist"));
      sortblacklistButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          sortblacklistButtonActionPerformed(evt);
        }
      });
      userListPanel.add(sortblacklistButton, c);

      c.gridx = 0;
      c.gridy = row++;
      importadminsButton.setText(messages.getString("ImportAdmins"));
      importadminsButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          importadminsButtonActionPerformed(evt);
        }
      });
      userListPanel.add(importadminsButton, c);

      c.gridx = 1;
      importbotsButton.setText(messages.getString("ImportBots"));
      importbotsButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          importbotsButtonActionPerformed(evt);
        }
      });
      userListPanel.add(importbotsButton, c);

      c.gridx = 0;
      c.gridy = row++;
      jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
      jLabel27.setText(messages.getString("ImportAdminsToWhitelistFrom"));
      userListPanel.add(jLabel27, c);

      c.gridx = 1;
      whitelistimportField.setText(messages
          .getString("whitelistimportFieldlabel"));
      userListPanel.add(whitelistimportField, c);

      tab.addTab(messages.getString("UserLists"), userListPanel);
    }

    {
      temporaryPanel.setLayout(new TreeColumnGridBagLayout(jLabel9, jLabel10, jPanel22));

      GridBagConstraints c = new GridBagConstraints();
      int row = 0;

      c.gridx = 0;
      c.gridy = row++;
      c.weightx = 1;
      Font f = jLabel9.getFont();
      Font headerFont = new Font(f.getName(), Font.BOLD, f.getSize() + 2);
      jLabel9.setFont(headerFont);
      jLabel9.setHorizontalAlignment(SwingConstants.CENTER);
      Insets defaultInsets = c.insets;
      c.insets = new java.awt.Insets(5, 5, 5, 5);
      //c.insets = new Insets(2,2,2,2);
      c.fill = GridBagConstraints.BOTH;
      jLabel9.setText(messages.getString("TempWhitelist"));
      temporaryPanel.add(jLabel9, c);

      c.gridx = 1;
      jLabel10.setFont(headerFont);
      jLabel10.setHorizontalAlignment(SwingConstants.CENTER);
      jLabel10.setText(messages.getString("TempBlacklist"));
      temporaryPanel.add(jLabel10, c);

      c.gridx = 0;
      c.gridy = row++;
      c.weighty = 1;
      c.insets = defaultInsets;
      tempwhitelistList.setModel(config.tempwhitelistModel);
      tempwhitelistList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
        public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
          tempwhitelistListValueChanged(evt);
        }
      });
      jScrollPane3.setViewportView(tempwhitelistList);
      temporaryPanel.add(jScrollPane3, c);

      c.gridx = 1;
      tempblacklistList.setModel(config.tempblacklistModel);
      tempblacklistList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
        public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
          tempblacklistListValueChanged(evt);
        }
      });
      jScrollPane4.setViewportView(tempblacklistList);
      temporaryPanel.add(jScrollPane4, c);

      c.gridx = 0;
      c.gridy = row++;
      c.weighty = 0;
      jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
      jLabel24.setText(messages.getString("UsernameIPOrPage") + ": ");
      temporaryPanel.add(jLabel24, c);

      c.gridx = 1;
      temporaryPanel.add(templistField, c);

      c.gridx = 0;
      c.gridy = row++;
      tempwhitelistAdd.setText(messages.getString("AddToTempWhitelist"));
      tempwhitelistAdd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          tempwhitelistAddActionPerformed(evt);
        }
      });
      temporaryPanel.add(tempwhitelistAdd, c);

      c.gridx = 1;
      tempblacklistAdd.setText(messages.getString("AddToTempBlacklist"));
      tempblacklistAdd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          tempblacklistAddActionPerformed(evt);
        }
      });
      temporaryPanel.add(tempblacklistAdd, c);

      c.gridx = 0;
      c.gridy = row++;
      tempwhitelistRemove.setText(messages.getString("RemoveFromTempWhitelist"));
      tempwhitelistRemove.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          tempwhitelistRemoveActionPerformed(evt);
        }
      });
      temporaryPanel.add(tempwhitelistRemove, c);

      c.gridx = 1;
      tempblacklistRemove.setText(messages.getString("RemoveFromTempBlacklist"));
      tempblacklistRemove.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          tempblacklistRemoveActionPerformed(evt);
        }
      });
      temporaryPanel.add(tempblacklistRemove, c);

      c.gridx = 0;
      c.gridy = row++;
      sorttempwhitelistButton.setText(messages.getString("SortWhitelist"));
      sorttempwhitelistButton
      .addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          sorttempwhitelistButtonActionPerformed(evt);
        }
      });
      temporaryPanel.add(sorttempwhitelistButton, c);

      c.gridx = 1;
      sorttempblacklistButton.setText(messages.getString("SortBlacklist"));
      sorttempblacklistButton
      .addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          sorttempblacklistButtonActionPerformed(evt);
        }
      });
      temporaryPanel.add(sorttempblacklistButton, c);

      c.gridx = 2;
      c.gridy = 0;
      temporaryPanel.add(jPanel22, c);

      tab.addTab(messages.getString("TempLists"), temporaryPanel);
    }

    {
      watchlistPanel.setLayout(new TreeColumnGridBagLayout(jLabel11, jLabel12, jPanel23));

      GridBagConstraints c = new GridBagConstraints();
      int row = 0;

      c.gridx = 0;
      c.gridy = row++;
      c.weightx = 1;
      Font f = jLabel11.getFont();
      Font headerFont = new Font(f.getName(), Font.BOLD, f.getSize() + 2);
      jLabel11.setFont(headerFont);
      jLabel11.setHorizontalAlignment(SwingConstants.CENTER);
      Insets defaultInsets = c.insets;
      c.insets = new java.awt.Insets(5, 5, 5, 5);
      //c.insets = new Insets(2,2,2,2);
      c.fill = GridBagConstraints.BOTH;
      jLabel11.setText(messages.getString("Watchlist"));
      watchlistPanel.add(jLabel11, c);

      c.gridx = 1;
      jLabel12.setFont(headerFont);
      jLabel12.setHorizontalAlignment(SwingConstants.CENTER);
      jLabel12.setText(messages.getString("TempWatchlist"));
      watchlistPanel.add(jLabel12, c);

      c.gridx = 0;
      c.gridy = row++;
      c.weighty = 1;
      c.insets = defaultInsets;
      watchlistList.setModel(config.watchlistModel);
      watchlistList
      .addListSelectionListener(new javax.swing.event.ListSelectionListener() {
        public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
          watchlistListValueChanged(evt);
        }
      });
      jScrollPane11.setViewportView(watchlistList);
      watchlistPanel.add(jScrollPane11, c);

      c.gridx = 1;
      tempwatchlistList.setModel(config.tempwatchlistModel);
      tempwatchlistList
          .addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
              tempwatchlistListValueChanged(evt);
            }
          });
      jScrollPane5.setViewportView(tempwatchlistList);
      watchlistPanel.add(jScrollPane5, c);

      c.gridx = 0;
      c.gridy = row++;
      c.weighty = 0;
      jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
      jLabel30.setLabelFor(articleField);
      jLabel30.setText(messages.getString("ArticleName") + ": ");
      watchlistPanel.add(jLabel30, c);

      c.gridx = 1;
      watchlistPanel.add(articleField, c);

      c.gridx = 0;
      c.gridy = row++;
      watchlistAdd.setText(messages.getString("Add"));
      watchlistAdd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          watchlistAddActionPerformed(evt);
        }
      });
      watchlistPanel.add(watchlistAdd, c);

      c.gridx = 1;
      tempwatchlistAdd.setText(messages.getString("AddToTempWatchlist"));
      tempwatchlistAdd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          tempwatchlistAddActionPerformed(evt);
        }
      });
      watchlistPanel.add(tempwatchlistAdd, c);

      c.gridx = 0;
      c.gridy = row++;
      watchlistRemove.setText(messages.getString("Remove"));
      watchlistRemove.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          watchlistRemoveActionPerformed(evt);
        }
      });
      watchlistPanel.add(watchlistRemove, c);

      c.gridx = 1;
      tempwatchlistRemove
          .setText(messages.getString("RemoveFromTempWatchlist"));
      tempwatchlistRemove
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
              tempwatchlistRemoveActionPerformed(evt);
            }
          });
      watchlistPanel.add(tempwatchlistRemove, c);

      c.gridx = 0;
      c.gridy = row++;
      sortarticlesButton.setText(messages.getString("SortWatchlist"));
      sortarticlesButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          sortarticlesButtonActionPerformed(evt);
        }
      });
      watchlistPanel.add(sortarticlesButton, c);

      c.gridx = 1;
      sorttempwatchlistButton.setText(messages.getString("SortWatchlist"));
      sorttempwatchlistButton
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
              sorttempwatchlistButtonActionPerformed(evt);
            }
          });
      watchlistPanel.add(sorttempwatchlistButton, c);

      c.gridx = 0;
      c.gridy = row++;
      watchlistimportButton.setText(messages.getString("ImportWatchlist"));
      watchlistimportButton
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
              watchlistimportButtonActionPerformed(evt);
            }
          });
      watchlistPanel.add(watchlistimportButton, c);

      c.gridx = 2;
      c.gridy = 0;
      watchlistPanel.add(jPanel23, c);

      tab.addTab(messages.getString("Watchlists"), watchlistPanel);
    }

    {
      regexPanel.setLayout(new TreeColumnGridBagLayout(jLabel7, jLabel8, jPanel21));

      GridBagConstraints c = new GridBagConstraints();
      int row = 0;

      c.gridx = 0;
      c.gridy = row++;
      c.weightx = 1;
      Font f = jLabel7.getFont();
      Font headerFont = new Font(f.getName(), Font.BOLD, f.getSize() + 2);
      jLabel7.setFont(headerFont);
      jLabel7.setHorizontalAlignment(SwingConstants.CENTER);
      Insets defaultInsets = c.insets;
      c.insets = new java.awt.Insets(5, 5, 5, 5);
      //c.insets = new Insets(2,2,2,2);
      c.fill = GridBagConstraints.BOTH;
      jLabel7.setText(messages.getString("RegexpWhitelist"));
      regexPanel.add(jLabel7, c);

      c.gridx = 1;
      jLabel8.setFont(headerFont);
      jLabel8.setHorizontalAlignment(SwingConstants.CENTER);
      jLabel8.setText(messages.getString("RegexpBlacklist"));
      regexPanel.add(jLabel8, c);

      c.gridx = 0;
      c.gridy = row++;
      c.weighty = 1;
      c.insets = defaultInsets;
      regexpwhiteList.setModel(config.regexpwhiteModel);
      regexpwhiteList
      .addListSelectionListener(new javax.swing.event.ListSelectionListener() {
        public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
          regexpwhiteListValueChanged(evt);
        }
      });
      jScrollPane1.setViewportView(regexpwhiteList);
      regexPanel.add(jScrollPane1, c);

      c.gridx = 1;
      regexpblackList.setModel(config.regexpblackModel);
      regexpblackList
      .addListSelectionListener(new javax.swing.event.ListSelectionListener() {
        public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
          regexpblackListValueChanged(evt);
        }
      });
      jScrollPane2.setViewportView(regexpblackList);
      regexPanel.add(jScrollPane2, c);

      c.gridx = 0;
      c.gridy = row++;
      c.weighty = 0;
      jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
      jLabel23.setText(messages.getString("regexp") + ": ");
      regexPanel.add(jLabel23, c);

      c.gridx = 1;
      regexpField.setText("");
      regexPanel.add(regexpField, c);

      c.gridx = 0;
      c.gridy = row++;
      regexpwhiteAdd.setText(messages.getString("AddToWhitelist"));
      regexpwhiteAdd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          regexpwhiteAddActionPerformed(evt);
        }
      });
      regexPanel.add(regexpwhiteAdd, c);

      c.gridx = 1;
      regexpblackAdd.setText(messages.getString("AddToBlacklist"));
      regexpblackAdd.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          regexpblackAddActionPerformed(evt);
        }
      });
      regexPanel.add(regexpblackAdd, c);

      c.gridx = 0;
      c.gridy = row++;
      regexpwhiteRemove.setText(messages.getString("RemoveFromWhitelist"));
      regexpwhiteRemove.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          regexpwhiteRemoveActionPerformed(evt);
        }
      });
      regexPanel.add(regexpwhiteRemove, c);

      c.gridx = 1;
      regexpblackRemove.setText(messages.getString("RemoveFromBlacklist"));
      regexpblackRemove.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          regexpblackRemoveActionPerformed(evt);
        }
      });
      regexPanel.add(regexpblackRemove, c);

      c.gridx = 0;
      c.gridy = row++;
      sortregexpwhitelistButton.setText(messages.getString("SortWhitelist"));
      sortregexpwhitelistButton
      .addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          sortregexpwhitelistButtonActionPerformed(evt);
        }
      });
      regexPanel.add(sortregexpwhitelistButton, c);

      c.gridx = 1;
      sortregexpblacklistButton.setText(messages.getString("SortBlacklist"));
      sortregexpblacklistButton
      .addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          sortregexpblacklistButtonActionPerformed(evt);
        }
      });
      regexPanel.add(sortregexpblacklistButton, c);

      c.gridx = 2;
      c.gridy = 0;
      regexPanel.add(jPanel21, c);

      tab.addTab(messages.getString("regexpLists"), regexPanel);
    }

    configPanel1.setLayout(new java.awt.BorderLayout());

    // UIManager.put("ColorChooser.cancelText","Zrusit");
    // UIManager.getDefaults().setDefaultLocale(config.currentLocale);
    // Locale.setDefault(config.currentLocale);
    // JComponent.setDefaultLocale(config.currentLocale);

    hackColorChooser();

    // Configuration starts here
    {
      final class ListData {
        public static final String CONFIG = "config";

        public static final String GENERAL = "general";

        public static final String RECENT_CHANGES = "recent changes";

        public static final String LISTS = "lists";

        public static final String START = "start";

        public static final String LOCALIZATION = "localize";

        public static final String BROWSER = "browser";

        public static final String MISC = "misc";

        public static final String SHOW = "show";

        public static final String COLOR = "color";

        public static final String BEEP = "beep";

        public static final String IMPORT_EXPORT = "import export";

        public static final String AUTOMATIC = "automatic";

        public static final String REGEXP = "regexp";

        public static final String NONE = "none";

        public String ident;

        public String toDisplay;

        public ListData(String ident, String toDisplay) {
          this.ident = ident;
          this.toDisplay = toDisplay;
        }

        public String toString() {
          return toDisplay;
        }
      }

      configCardPanel = new JPanel();
      configCardPanel.setLayout(new java.awt.CardLayout());
      configCardPanel.add(new JScrollPane(startConfigPanel), ListData.START);
      configCardPanel.add(new JScrollPane(localizeConfigPanel),
          ListData.LOCALIZATION);
      configCardPanel
          .add(new JScrollPane(browserConfigPanel), ListData.BROWSER);
      configCardPanel.add(new JScrollPane(showConfigPanel), ListData.SHOW);
      configCardPanel.add(new JScrollPane(colorConfigPanel), ListData.COLOR);
      configCardPanel.add(new JScrollPane(beepConfigPanel), ListData.BEEP);
      configCardPanel.add(new JScrollPane(listsConfigPanel),
          ListData.IMPORT_EXPORT);
      configCardPanel.add(new JScrollPane(regexpConfigPanel), ListData.REGEXP);
      configCardPanel.add(new JScrollPane(automaticConfigPanel),
          ListData.AUTOMATIC);
      configCardPanel.add(new JScrollPane(miscConfigPanel), ListData.MISC);
      configCardPanel.add(new JPanel(), ListData.NONE);

      DefaultMutableTreeNode top = new DefaultMutableTreeNode(new ListData(
          ListData.CONFIG, messages.getString("CTConfiguration")));
      DefaultMutableTreeNode general = new DefaultMutableTreeNode(new ListData(
          ListData.GENERAL, messages.getString("CTGeneral")));
      top.add(general);
      DefaultMutableTreeNode recentChanges = new DefaultMutableTreeNode(
          new ListData(ListData.RECENT_CHANGES, messages
              .getString("CTRecentChanges")));
      top.add(recentChanges);
      DefaultMutableTreeNode lists = new DefaultMutableTreeNode(new ListData(
          ListData.LISTS, messages.getString("CTLists")));
      top.add(lists);

      DefaultMutableTreeNode start = new DefaultMutableTreeNode(new ListData(
          ListData.START, messages.getString("CTStart")));
      general.add(start);
      DefaultMutableTreeNode localization = new DefaultMutableTreeNode(
          new ListData(ListData.LOCALIZATION, messages
              .getString("CTLocalization")));
      general.add(localization);
      DefaultMutableTreeNode browser = new DefaultMutableTreeNode(new ListData(
          ListData.BROWSER, messages.getString("CTBrowser")));
      general.add(browser);
      DefaultMutableTreeNode misc = new DefaultMutableTreeNode(new ListData(
          ListData.MISC, messages.getString("CTMisc")));
      general.add(misc);

      DefaultMutableTreeNode show = new DefaultMutableTreeNode(new ListData(
          ListData.SHOW, messages.getString("CTShow")));
      recentChanges.add(show);
      DefaultMutableTreeNode color = new DefaultMutableTreeNode(new ListData(
          ListData.COLOR, messages.getString("CTColor")));
      recentChanges.add(color);
      DefaultMutableTreeNode beep = new DefaultMutableTreeNode(new ListData(
          ListData.BEEP, messages.getString("CTBeep")));
      recentChanges.add(beep);

      DefaultMutableTreeNode importExport = new DefaultMutableTreeNode(
          new ListData(ListData.IMPORT_EXPORT, messages
              .getString("CTImportExport")));
      lists.add(importExport);
      DefaultMutableTreeNode automatic = new DefaultMutableTreeNode(
          new ListData(ListData.AUTOMATIC, messages.getString("CTAutomatic")));
      lists.add(automatic);
      DefaultMutableTreeNode regexp = new DefaultMutableTreeNode(new ListData(
          ListData.REGEXP, messages.getString("CTRegexp")));
      lists.add(regexp);

      configTree = new JTree(top);
      configTree.getSelectionModel()
         .setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
      configTree.addTreeSelectionListener(new TreeSelectionListener() {
        public void valueChanged(TreeSelectionEvent e) {
          DefaultMutableTreeNode node = (DefaultMutableTreeNode) configTree
              .getLastSelectedPathComponent();

          if (node == null)
            return;

          Object nodeInfo = node.getUserObject();
          CardLayout cl = (CardLayout) (configCardPanel.getLayout());
          if (node.isLeaf()) {
            ListData ld = (ListData) nodeInfo;
            cl.show(configCardPanel, ld.ident);
          } else {
            cl.show(configCardPanel, ListData.NONE);
          }
        }
      });
      configTree.setSelectionPath(new TreePath(start.getPath()));
      configTree.expandPath(new TreePath(recentChanges.getPath()));
      configTree.expandPath(new TreePath(lists.getPath()));

      configListScrollPane = new JScrollPane(configTree);
    }

    configSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
        configListScrollPane, configCardPanel);
    configSplitPane.setDividerLocation(200);

    {
      localizeConfigPanel.setLayout(new java.awt.GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      int row = 0;
      c.fill = GridBagConstraints.HORIZONTAL;
      c.gridwidth = 3;
      c.gridx = 0;
      c.gridy = row++;
      c.anchor = GridBagConstraints.LINE_START;

      localizeConfigLabel.setText(messages.getString("CPLocalization"));
      Font f = localizeConfigLabel.getFont();
      localizeConfigLabel.setFont(new Font(f.getName(), Font.BOLD,
          f.getSize() + 4));
      Insets defaultInsets = new java.awt.Insets(5, 5, 5, 5);
      c.insets = new java.awt.Insets(5, 5, 10, 0);
      localizeConfigPanel.add(localizeConfigLabel, c);
      c.insets = defaultInsets;

      jLabel2.setText(messages.getString("chooseLang"));
      c.gridwidth = 1;
      c.gridy = row++;
      localizeConfigPanel.add(jLabel2, c);

      langList.setModel(new javax.swing.DefaultComboBoxModel(langStrings));
      langList.setSelectedIndex(getLangInt());
      langList.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          langListActionPerformed(evt);
        }
      });
      c.gridx = 1;
      localizeConfigPanel.add(langList, c);

      jLabel28.setText(messages.getString("chooseTimeZone"));
      c.gridx = 0;
      c.gridy = row++;
      localizeConfigPanel.add(jLabel28, c);

      timeZoneList.setModel(new javax.swing.DefaultComboBoxModel(
          timeZoneStrings));
      timeZoneList.setSelectedIndex(getTimeZoneInt());
      timeZoneList.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          timeZoneListActionPerformed(evt);
        }
      });
      c.gridx = 1;
      localizeConfigPanel.add(timeZoneList, c);

      DefaultProject.setText(messages.getString("DefaultProject"));
      c.gridx = 0;
      c.gridy = row++;
      localizeConfigPanel.add(DefaultProject, c);

      defaultProjectField.setText(messages.getString("defaultprojectfield"));
      defaultProjectField.setMinimumSize(new java.awt.Dimension(30, 19));
      defaultProjectField.setPreferredSize(new java.awt.Dimension(100, 19));
      c.gridx = 1;
      localizeConfigPanel.add(defaultProjectField, c);

      c.gridx = 0;
      c.gridy = row++;
      c.gridwidth = 3;
      localizeConfigPanel.add(jSeparator1, c);

      c.gridy = row++;
      c.gridwidth = 1;
      jLabel29.setText(messages.getString("chooseFeel"));
      localizeConfigPanel.add(jLabel29, c);

      feelList.setModel(new javax.swing.DefaultComboBoxModel(feelStrings));
      feelList.setSelectedIndex(getFeelInt());
      themeList.setEnabled(feelList.getSelectedIndex() == 0);
      themeLabel.setEnabled(feelList.getSelectedIndex() == 0);
      feelList.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          feelListActionPerformed(evt);
        }
      });
      c.gridx = 1;
      localizeConfigPanel.add(feelList, c);

      c.gridx = 0;
      c.gridy = row++;
      c.insets = new java.awt.Insets(defaultInsets.top, 15,
          defaultInsets.bottom, defaultInsets.right);
      themeLabel.setText(messages.getString("chooseTheme"));
      localizeConfigPanel.add(themeLabel, c);
      c.insets = defaultInsets;

      themeList.setModel(new javax.swing.DefaultComboBoxModel(themeStrings));
      themeList.setSelectedIndex(getThemeInt());
      themeList.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          themeListActionPerformed(evt);
        }
      });
      c.gridx = 1;
      localizeConfigPanel.add(themeList, c);

      c.gridy = row++;
      c.gridwidth = 3;
      c.weighty = 1;
      localizeConfigPanel.add(new JLabel(), c);

      c.gridx = 2;
      c.gridy = 0;
      c.gridwidth = 1;
      c.gridheight = row;
      c.weightx = 1;
      localizeConfigPanel.add(new JLabel(), c);
    }

    {
      miscConfigPanel.setLayout(new java.awt.GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      int row = 0;
      c.weightx = 1;
      c.gridx = 0;
      c.gridy = row++;
      c.anchor = GridBagConstraints.LINE_START;

      miscConfigLabel.setText(messages.getString("CPMisc"));
      Font f = miscConfigLabel.getFont();
      miscConfigLabel
          .setFont(new Font(f.getName(), Font.BOLD, f.getSize() + 4));
      Insets defaultInsets = c.insets;
      c.insets = new java.awt.Insets(5, 5, 10, 0);
      miscConfigPanel.add(miscConfigLabel, c);
      c.insets = defaultInsets;

      // configMiscPanel.add(jPanel18);
      autoscroll.setText(messages.getString("autoscrolllabel"));
      // autoscroll.setBorder(javax.swing.BorderFactory
      // .createEmptyBorder(0, 0, 0, 0));
      // autoscroll.setMargin(new java.awt.Insets(0, 0, 0, 0));
      c.gridy = row++;
      miscConfigPanel.add(autoscroll, c);

      queueedits.setText(messages.getString("queueeditslabel"));
      // queueedits.setBorder(javax.swing.BorderFactory
      // .createEmptyBorder(0, 0, 0, 0));
      // queueedits.setMargin(new java.awt.Insets(0, 0, 0, 0));
      queueedits.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          queueeditsActionPerformed(evt);
        }
      });
      c.gridy = row++;
      miscConfigPanel.add(queueedits, c);

      stripformatting.setText(messages.getString("stripformattinglabel"));
      // stripformatting.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,
      // 0,
      // 0, 0));
      // stripformatting.setMargin(new java.awt.Insets(0, 0, 0, 0));
      c.gridy = row++;
      miscConfigPanel.add(stripformatting, c);

      reversetable.setText(messages.getString("reversetablelabel"));
      // reversetable.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,
      // 0, 0,
      // 0));
      // reversetable.setMargin(new java.awt.Insets(0, 0, 0, 0));
      c.gridy = row++;
      miscConfigPanel.add(reversetable, c);

      c.gridy = row++;
      c.weighty = 1;
      miscConfigPanel.add(new JLabel(), c);
    }

    {
      startConfigPanel.setLayout(new java.awt.GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      int row = 0;
      c.weightx = 1;
      c.gridx = 0;
      c.gridy = row++;
      c.anchor = GridBagConstraints.LINE_START;
      startConfigLabel.setText(messages.getString("CPStart"));
      Font f = startConfigLabel.getFont();
      startConfigLabel
          .setFont(new Font(f.getName(), Font.BOLD, f.getSize() + 4));
      Insets defaultInsets = c.insets;
      c.insets = new java.awt.Insets(5, 5, 10, 0);
      startConfigPanel.add(startConfigLabel, c);
      c.insets = defaultInsets;

      newversion.setText(messages.getString("newversionlabel"));
      // newversion.setBorder(javax.swing.BorderFactory
      // .createEmptyBorder(0, 0, 0, 0));
      // newversion.setMargin(new java.awt.Insets(0, 0, 0, 0));
      newversion.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          newversionActionPerformed(evt);
        }
      });
      c.gridy = row++;
      startConfigPanel.add(newversion, c);

      connectatstart.setText(messages.getString("connectatstartlabel"));
      connectatstart.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          connectatstartActionPerformed(evt);
        }
      });
      c.gridy = row++;
      startConfigPanel.add(connectatstart, c);

      c.gridy = row++;
      c.weighty = 1;
      startConfigPanel.add(new JLabel(), c);
    }

    {
      browserConfigPanel.setLayout(new java.awt.GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      int row = 0;
      c.weightx = 1;
      c.gridx = 0;
      c.gridy = row++;
      c.anchor = GridBagConstraints.LINE_START;
      browserConfigLabel.setText(messages.getString("CPBrowser"));
      Font f = browserConfigLabel.getFont();
      browserConfigLabel.setFont(new Font(f.getName(), Font.BOLD,
          f.getSize() + 4));
      Insets defaultInsets = c.insets;
      c.insets = new java.awt.Insets(5, 5, 10, 0);
      browserConfigPanel.add(browserConfigLabel, c);
      c.insets = defaultInsets;

      singleclick.setText(messages.getString("singleclicklabel"));
      c.gridy = row++;
      browserConfigPanel.add(singleclick, c);

      browserpick.setText(messages.getString("browserpiclable"));
      browserpick.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          browserpickActionPerformed(evt);
        }
      });

      c.gridy = row++;
      browserConfigPanel.add(browserpick, c);

      browser2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

      browserchooserButton.setText(messages.getString("browse"));
      browserchooserButton
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
              browserchooserButtonActionPerformed(evt);
            }
          });

      browser2.add(browserchooserButton);

      browserField.setText(config.getProperty("browser"));
      browserField.setMinimumSize(new java.awt.Dimension(150, 19));
      browserField.setPreferredSize(new java.awt.Dimension(350, 19));
      browser2.add(browserField);

      c.gridy = row++;
      c.insets = new java.awt.Insets(defaultInsets.top, 15,
          defaultInsets.bottom, defaultInsets.right);
      browserConfigPanel.add(browser2, c);
      c.insets = defaultInsets;

      BColumnTakesYouTo.setText(messages.getString("BColumnTakesYouTo"));
      c.gridy = row++;
      c.insets = new java.awt.Insets(defaultInsets.top, defaultInsets.left + 5,
          defaultInsets.bottom, defaultInsets.right);
      browserConfigPanel.add(BColumnTakesYouTo, c);
      c.insets = defaultInsets;

      blockRadioPanel.setLayout(new GridLayout(3, 1));

      BColumnGroup.add(blockButton);
      blockButton.setText(messages.getString("blockButtonlabel"));
      blockButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          blockButtonActionPerformed(evt);
        }
      });

      blockRadioPanel.add(blockButton);

      BColumnGroup.add(vipButton);
      vipButton.setText(messages.getString("vipButtonlabel"));
      vipButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          vipButtonActionPerformed(evt);
        }
      });

      blockRadioPanel.add(vipButton);

      BColumnGroup.add(vipeditButton);
      vipeditButton.setText(messages.getString("vipeditbuttonlabel"));
      vipeditButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          vipeditButtonActionPerformed(evt);
        }
      });

      blockRadioPanel.add(vipeditButton);

      c.gridy = row++;
      c.insets = new java.awt.Insets(5, 15, 5, 5);
      browserConfigPanel.add(blockRadioPanel, c);
      c.insets = defaultInsets;

      c.gridy = row++;
      c.weighty = 1;
      browserConfigPanel.add(new JLabel(), c);
    }

    {
      showConfigPanel.setLayout(new java.awt.GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      int row = 0;
      c.weightx = 1;
      c.fill = GridBagConstraints.HORIZONTAL;
      c.gridx = 0;
      c.gridy = row++;
      c.anchor = GridBagConstraints.LINE_START;
      showConfigLabel.setText(messages.getString("CPShow"));
      Font f = showConfigLabel.getFont();
      showConfigLabel
          .setFont(new Font(f.getName(), Font.BOLD, f.getSize() + 4));
      Insets defaultInsets = c.insets;
      c.insets = new java.awt.Insets(5, 5, 10, 0);
      showConfigPanel.add(showConfigLabel, c);
      c.insets = defaultInsets;

      showips.setText(messages.getString("IPedits"));
      showips.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          showipsActionPerformed(evt);
        }
      });
      c.gridy = row++;
      showConfigPanel.add(showips, c);

      newpages.setText(messages.getString("newpages"));
      newpages.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          newpagesActionPerformed(evt);
        }
      });
      c.gridy = row++;
      showConfigPanel.add(newpages, c);

      showwatch.setText(messages.getString("showwatchlabel"));
      showwatch.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          showwatchActionPerformed(evt);
        }
      });
      c.gridy = row++;
      showConfigPanel.add(showwatch, c);

      c.gridy = row++;
      showConfigPanel.add(jSeparator2, c);

      listprecedence.setText(messages.getString("listprecedencelabel"));
      listprecedence.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          listprecedenceActionPerformed(evt);
        }
      });
      c.gridy = row++;
      showConfigPanel.add(listprecedence, c);

      watchuserpages.setText(messages.getString("watchuserpageslabel"));
      c.gridy = row++;
      showConfigPanel.add(watchuserpages, c);

      watchmasschanges.setText(messages.getString("watcmasschangeslabel"));
      c.gridy = row++;
      showConfigPanel.add(watchmasschanges, c);

      watchpagemoves.setText(messages.getString("watchpagemoveslabel"));
      c.gridy = row++;
      showConfigPanel.add(watchpagemoves, c);

      watchnewpages.setText(messages.getString("watchnewpageslabel"));
      c.gridy = row++;
      showConfigPanel.add(watchnewpages, c);

      c.gridy = row++;
      showConfigPanel.add(jSeparator6, c);

      olddeleted.setText(messages.getString("olddeletedLabel"));
      c.gridy = row++;
      showConfigPanel.add(olddeleted, c);

      registerdeleted.setText(messages.getString("registerdeletedLabel"));
      c.gridy = row++;
      showConfigPanel.add(registerdeleted, c);

      removereviewed.setText(messages.getString("removereviewed"));
      c.gridy = row++;
      showConfigPanel.add(removereviewed, c);

      c.gridy = row++;
      c.weighty = 1;
      showConfigPanel.add(new JLabel(), c);
    }

    {
      beepConfigPanel.setLayout(new java.awt.GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      int row = 0;
      c.weightx = 1;
      c.fill = GridBagConstraints.HORIZONTAL;
      c.gridx = 0;
      c.gridy = row++;
      c.anchor = GridBagConstraints.LINE_START;
      beepConfigLabel.setText(messages.getString("CPBeep"));
      Font f = beepConfigLabel.getFont();
      beepConfigLabel
          .setFont(new Font(f.getName(), Font.BOLD, f.getSize() + 4));
      Insets defaultInsets = c.insets;
      c.insets = new java.awt.Insets(5, 5, 10, 0);
      beepConfigPanel.add(beepConfigLabel, c);
      c.insets = defaultInsets;

      beeptempblack.setText(messages.getString("beeptempblacklabel"));
      c.gridy = row++;
      beepConfigPanel.add(beeptempblack, c);

      beeppermblack.setText(messages.getString("beeppermblacklabel"));
      c.gridy = row++;
      beepConfigPanel.add(beeppermblack, c);

      beeptempwatch.setText(messages.getString("beeptempwatchlabel"));
      c.gridy = row++;
      beepConfigPanel.add(beeptempwatch, c);

      beeppermwatch.setText(messages.getString("beeppermwatchlabel"));
      c.gridy = row++;
      beepConfigPanel.add(beeppermwatch, c);

      beepregexpblack.setText(messages.getString("beepregexpblacklabel"));
      c.gridy = row++;
      beepConfigPanel.add(beepregexpblack, c);

      beepcollaborationwarning.setText(messages.getString("beepcollaborationwarninglabel"));
      c.gridy = row++;
      beepConfigPanel.add(beepcollaborationwarning, c);

      c.gridy = row++;
      c.weighty = 1;
      beepConfigPanel.add(new JLabel(), c);
    }

    {
      regexpConfigPanel.setLayout(new java.awt.GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      int row = 0;
      c.weightx = 1;
      c.gridx = 0;
      c.gridy = row++;
      c.anchor = GridBagConstraints.LINE_START;
      regexpConfigLabel.setText(messages.getString("CPRegexp"));
      Font f = regexpConfigLabel.getFont();
      regexpConfigLabel.setFont(new Font(f.getName(), Font.BOLD,
          f.getSize() + 4));
      Insets defaultInsets = c.insets;
      c.insets = new java.awt.Insets(5, 5, 10, 0);
      regexpConfigPanel.add(regexpConfigLabel, c);
      c.insets = defaultInsets;

      rwhiteuser.setText(messages.getString("rwhiteuserlabel"));
      c.gridy = row++;
      regexpConfigPanel.add(rwhiteuser, c);

      rwhitepage.setText(messages.getString("rwhitepagelabel"));
      c.gridy = row++;
      regexpConfigPanel.add(rwhitepage, c);

      rwhitesummary.setText(messages.getString("rwhitesummarylabel"));
      c.gridy = row++;
      regexpConfigPanel.add(rwhitesummary, c);

      rblackuser.setText(messages.getString("rblackuserlabel"));
      c.gridy = row++;
      regexpConfigPanel.add(rblackuser, c);

      rblackpage.setText(messages.getString("rblackpagelabel"));
      c.gridy = row++;
      regexpConfigPanel.add(rblackpage, c);

      rblacksummary.setText(messages.getString("rblacksummarylabel"));
      c.gridy = row++;
      regexpConfigPanel.add(rblacksummary, c);

      c.gridy = row++;
      c.weighty = 1;
      regexpConfigPanel.add(new JLabel(), c);
    }

    {
      automaticConfigPanel.setLayout(new java.awt.GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      int row = 0;
      c.weightx = 1;
      c.gridx = 0;
      c.gridy = row++;
      c.anchor = GridBagConstraints.LINE_START;
      automaticConfigLabel.setText(messages.getString("CPAutomatic"));
      Font f = automaticConfigLabel.getFont();
      automaticConfigLabel.setFont(new Font(f.getName(), Font.BOLD,
          f.getSize() + 4));
      Insets defaultInsets = c.insets;
      c.insets = new java.awt.Insets(5, 5, 10, 0);
      automaticConfigPanel.add(automaticConfigLabel, c);
      c.insets = defaultInsets;

      blackreverted.setText(messages.getString("blackrevertedlabel"));
      blackreverted.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          blackrevertedActionPerformed(evt);
        }
      });
      c.gridy = row++;
      automaticConfigPanel.add(blackreverted, c);

      revertedUsers
          .setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
      blackrevertedGroup.add(revertpermButton);
      revertpermButton.setText(messages.getString("revertpermButtonlabel"));
      revertedUsers.add(revertpermButton);

      blackrevertedGroup.add(reverttempButton);
      reverttempButton.setText(messages.getString("reverttempButtonlabel"));
      revertedUsers.add(reverttempButton);

      c.gridy = row++;
      automaticConfigPanel.add(revertedUsers, c);

      blackdeleted.setText(messages.getString("blackdeletedlabel"));
      blackdeleted.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          blackdeletedActionPerformed(evt);
        }
      });
      c.gridy = row++;
      automaticConfigPanel.add(blackdeleted, c);

      speediedUsers
          .setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

      blackdeletedGroup.add(delpagepermButton);
      delpagepermButton.setText(messages.getString("delpagepermButtonlabel"));
      speediedUsers.add(delpagepermButton);

      blackdeletedGroup.add(delpagetempButton);
      delpagetempButton.setText(messages.getString("delpagetempButtonlabel"));
      speediedUsers.add(delpagetempButton);

      c.gridy = row++;
      automaticConfigPanel.add(speediedUsers, c);

      watchdeleted.setText(messages.getString("watchdeletedlabel"));
      watchdeleted.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          watchdeletedActionPerformed(evt);
        }
      });
      c.gridy = row++;
      automaticConfigPanel.add(watchdeleted, c);

      watchspeediedArticles.setLayout(new java.awt.FlowLayout(
          java.awt.FlowLayout.LEFT));

      watchdeletedGroup.add(watchdelpermButton);
      watchdelpermButton.setText(messages.getString("watchdelpermButtonlabel"));
//      watchdelpermButton.addActionListener(new java.awt.event.ActionListener() {
//        public void actionPerformed(java.awt.event.ActionEvent evt) {
//          watchdelpermButtonActionPerformed(evt);
//        }
//      });

      watchspeediedArticles.add(watchdelpermButton);

      watchdeletedGroup.add(watchdeltempButton);
      watchdeltempButton.setText(messages.getString("watchdeltempButtonlabel"));
      watchspeediedArticles.add(watchdeltempButton);

      c.gridy = row++;
      automaticConfigPanel.add(watchspeediedArticles, c);


      watchwarned.setText(messages.getString("watchwarnedlabel"));
      watchwarned.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          watchwarnedActionPerformed(evt);
        }
      });
      c.gridy = row++;
      automaticConfigPanel.add(watchwarned, c);

      watchwarnedPanel.setLayout(new java.awt.FlowLayout(
          java.awt.FlowLayout.LEFT));

      watchwarnedGroup.add(watchwarnedpermButton);
      watchwarnedpermButton.setText(messages.getString("watchwarnedpermButtonlabel"));

      watchwarnedPanel.add(watchwarnedpermButton);

      watchwarnedGroup.add(watchwarnedtempButton);
      watchwarnedtempButton.setText(messages.getString("watchwarnedtempButtonlabel"));
      watchwarnedPanel.add(watchwarnedtempButton);

      c.gridy = row++;
      automaticConfigPanel.add(watchwarnedPanel, c);


      c.gridy = row++;
      c.weighty = 1;
      automaticConfigPanel.add(new JLabel(), c);
    }

    {
      listsConfigPanel.setLayout(new java.awt.GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      int row = 0;
      c.weightx = 1;
      c.gridx = 0;
      c.gridy = row++;
      c.anchor = GridBagConstraints.LINE_START;
      listsConfigLabel.setText(messages.getString("CPImportExport"));
      Font f = listsConfigLabel.getFont();
      listsConfigLabel
          .setFont(new Font(f.getName(), Font.BOLD, f.getSize() + 4));
      Insets defaultInsets = c.insets;
      c.insets = new java.awt.Insets(5, 5, 10, 0);
      listsConfigPanel.add(listsConfigLabel, c);
      c.insets = defaultInsets;

      jPanel15.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

      exportchooserButton1.setText(messages.getString("browse"));
      exportchooserButton1
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
              exportchooserButton1ActionPerformed(evt);
            }
          });

      jPanel15.add(exportchooserButton1);

      exportField1.setPreferredSize(new java.awt.Dimension(250, 19));
      jPanel15.add(exportField1);

      exportButton1.setText(messages.getString("exportLists"));
      exportButton1.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          exportButton1ActionPerformed(evt);
        }
      });

      jPanel15.add(exportButton1);

      c.gridy = row++;
      listsConfigPanel.add(jPanel15, c);

      jPanel14.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

      importchooserButton.setText(messages.getString("browse"));
      importchooserButton
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
              importchooserButtonActionPerformed(evt);
            }
          });

      jPanel14.add(importchooserButton);

      importField.setPreferredSize(new java.awt.Dimension(250, 19));
      jPanel14.add(importField);

      importButton.setText(messages.getString("importLists"));
      importButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          importButtonActionPerformed(evt);
        }
      });

      jPanel14.add(importButton);

      c.gridy = row++;
      listsConfigPanel.add(jPanel14, c);

      c.gridy = row++;
      c.weighty = 1;
      listsConfigPanel.add(new JLabel(), c);
    }

    {
      colorConfigPanel.setLayout(new java.awt.GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      int row = 0;
      c.weightx = 1;
      c.gridx = 0;
      c.gridy = row++;
      c.anchor = GridBagConstraints.LINE_START;
      colorConfigLabel.setText(messages.getString("CPColor"));
      Font f = colorConfigLabel.getFont();
      colorConfigLabel
          .setFont(new Font(f.getName(), Font.BOLD, f.getSize() + 4));
      Insets defaultInsets = c.insets;
      c.insets = new java.awt.Insets(5, 5, 10, 0);
      colorConfigPanel.add(colorConfigLabel, c);
      c.insets = defaultInsets;

      jPanel11.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

      risk.setText(messages.getString("risklabel"));
      risk.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          riskActionPerformed(evt);
        }
      });
      jPanel11.add(risk);

      c.gridy = row++;
      colorConfigPanel.add(jPanel11, c);

      jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

      colorblist.setText(messages.getString("colorblistlabel"));
      colorblistButton.setBackground(config.getColorProp("blacklistcolor"));
      jPanel2.add(colorblist);

      colorblistButton.setBackground(config.getColorProp("blacklistcolor"));
      colorblistButton.setText("     ");
      colorblistButton.setBackground(config.getColorProp("blacklistcolor"));
      colorblistButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          colorblistButtonActionPerformed(evt);
        }
      });

      jPanel2.add(colorblistButton);

      c.gridy = row++;
      jPanel2
          .setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
      // jPanel2.setMargin(new java.awt.Insets(0, 0, 0, 0));
      colorConfigPanel.add(jPanel2, c);

      jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

      colorwatch.setText(messages.getString("colorwatchlabel"));
      jPanel4.add(colorwatch);

      colorwatchButton.setText("     ");
      colorwatchButton.setBackground(config.getColorProp("watchlistcolor"));
      colorwatchButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          colorwatchButtonActionPerformed(evt);
        }
      });

      jPanel4.add(colorwatchButton);

      c.gridy = row++;
      colorConfigPanel.add(jPanel4, c);

      jPanel5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

      colorchanged.setText(messages.getString("colorchangedlabel"));
      jPanel5.add(colorchanged);

      changedField.setMinimumSize(new java.awt.Dimension(50, 19));
      changedField.setPreferredSize(new java.awt.Dimension(50, 19));
      jPanel5.add(changedField);

      colorchangedButton.setText("     ");
      colorchangedButton.setBackground(config.getColorProp("changedcolor"));
      colorchangedButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          colorchangedButtonActionPerformed(evt);
        }
      });

      jPanel5.add(colorchangedButton);

      c.gridy = row++;
      c.insets = new java.awt.Insets(0, 0, 0, 0);
      colorConfigPanel.add(jPanel5, c);
      c.insets = defaultInsets;

      jPanel6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

      colornew.setText(messages.getString("colornewlabel"));
      jPanel6.add(colornew);

      colornewButton.setText("     ");
      colornewButton.setBackground(config.getColorProp("newcolor"));
      colornewButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          colornewButtonActionPerformed(evt);
        }
      });

      jPanel6.add(colornewButton);

      c.gridy = row++;
      colorConfigPanel.add(jPanel6, c);

      jPanel7.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

      colorips.setText(messages.getString("coloripslabel"));
      jPanel7.add(colorips);

      coloripsButton.setText("     ");
      coloripsButton.setBackground(config.getColorProp("ipcolor"));
      coloripsButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          coloripsButtonActionPerformed(evt);
        }
      });

      jPanel7.add(coloripsButton);

      c.gridy = row++;
      colorConfigPanel.add(jPanel7, c);

      jPanel78.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

      colornewusers.setText(messages.getString("colornewuserslabel"));
      jPanel78.add(colornewusers);

      colornewusersButton.setText("     ");
      colornewusersButton.setBackground(config.getColorProp("newusercolor"));
      colornewusersButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          colornewusersButtonActionPerformed(evt);
        }
      });

      jPanel78.add(colornewusersButton);

      c.gridy = row++;
      colorConfigPanel.add(jPanel78, c);

      jPanel8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

      coloruserpage.setText(messages.getString("coloruserpagelabel"));
      jPanel8.add(coloruserpage);

      coloruserpageButton.setText("     ");
      coloruserpageButton.setBackground(config.getColorProp("userpagecolor"));
      coloruserpageButton
          .addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
              coloruserpageButtonActionPerformed(evt);
            }
          });

      jPanel8.add(coloruserpageButton);

      c.gridy = row++;
      colorConfigPanel.add(jPanel8, c);

      jPanel9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

      colormoves.setText(messages.getString("colormoveslabel"));
      jPanel9.add(colormoves);

      colormovesButton.setText("     ");
      colormovesButton.setBackground(config.getColorProp("movecolor"));
      colormovesButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          colormovesButtonActionPerformed(evt);
        }
      });

      jPanel9.add(colormovesButton);

      c.gridy = row++;
      colorConfigPanel.add(jPanel9, c);

      c.gridy = row++;
      c.weighty = 1;
      colorConfigPanel.add(new JLabel(), c);
    }

    configPanel1.add(configSplitPane, java.awt.BorderLayout.CENTER);

    configCtrlsPanel.setLayout(new java.awt.FlowLayout(
        java.awt.FlowLayout.RIGHT));

    configDefaults.setText(messages.getString("ConfigRestoreDefaults"));
    configDefaults.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        configDefaultsActionPerformed(evt);
      }
    });

    configCtrlsPanel.add(configDefaults);

    configCancel.setText(messages.getString("ConfigCancel"));
    configCancel.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        configCancelActionPerformed(evt);
      }
    });

    configCtrlsPanel.add(configCancel);

    configSave.setText(messages.getString("ConfigSave"));
    configSave.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        configSaveActionPerformed(evt);
      }
    });

    configCtrlsPanel.add(configSave);

    configPanel1.add(configCtrlsPanel, java.awt.BorderLayout.SOUTH);

    tab.addTab(messages.getString("configuration"), configPanel1);

    irctoptopPanel1.setLayout(new java.awt.GridBagLayout());

    jPanel19.setLayout(new java.awt.BorderLayout());

    jPanel10.setLayout(new java.awt.GridLayout(4, 1));

    vandalism_nl_box.setText(messages.getString("vandalism_nl_box"));
    vandalism_nl_box.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,
        0, 0, 0));
    vandalism_nl_box.setMargin(new java.awt.Insets(0, 0, 0, 0));
    vandalism_nl_box.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        vandalism_nl_boxActionPerformed(evt);
      }
    });

    jPanel10.add(vandalism_nl_box);

    vandalism_fr_box.setText(messages.getString("vandalism_fr_wp"));
    vandalism_fr_box.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,
        0, 0, 0));
    vandalism_fr_box.setMargin(new java.awt.Insets(0, 0, 0, 0));
    jPanel10.add(vandalism_fr_box);

    vandalism_en_box.setText(messages.getString("vandalism_en_box"));
    vandalism_en_box.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,
        0, 0, 0));
    vandalism_en_box.setMargin(new java.awt.Insets(0, 0, 0, 0));
    vandalism_en_box.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        vandalism_en_boxActionPerformed(evt);
      }
    });
    jPanel10.add(vandalism_en_box);

    vandalism_cs_box.setText(messages.getString("vandalism_cs_box"));
    vandalism_cs_box.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,
        0, 0, 0));
    vandalism_cs_box.setMargin(new java.awt.Insets(0, 0, 0, 0));
    vandalism_cs_box.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        vandalism_cs_boxActionPerformed(evt);
      }
    });
    jPanel10.add(vandalism_cs_box);

    vandalism_it_box.setText(messages.getString("vandalism_it_box"));
    vandalism_it_box.setBorder(javax.swing.BorderFactory.createEmptyBorder(0,
        0, 0, 0));
    vandalism_it_box.setMargin(new java.awt.Insets(0, 0, 0, 0));
    jPanel10.add(vandalism_it_box);

    jPanel19.add(jPanel10, java.awt.BorderLayout.CENTER);

    VandalismStreamButton.setText(messages.getString("Connect"));
    VandalismStreamButton
        .addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            VandalismStreamButtonActionPerformed(evt);
          }
        });

    jPanel20.add(VandalismStreamButton);

    jPanel19.add(jPanel20, java.awt.BorderLayout.SOUTH);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    irctoptopPanel1.add(jPanel19, gridBagConstraints);

    jLabel21.setText("                                        ");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    irctoptopPanel1.add(jLabel21, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    irctoptopPanel1.add(jSeparator7, gridBagConstraints);

    jLabel22.setText("                                        ");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    irctoptopPanel1.add(jLabel22, gridBagConstraints);

    jPanel13.setLayout(new javax.swing.BoxLayout(jPanel13,
        javax.swing.BoxLayout.Y_AXIS));

    mediawikistreamCheckbox.setText(messages
        .getString("MediawikiIRCConnection"));
    mediawikistreamCheckbox.setBorder(javax.swing.BorderFactory
        .createEmptyBorder(0, 0, 0, 0));
    mediawikistreamCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));
    mediawikistreamCheckbox
        .addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            mediawikistreamCheckboxActionPerformed(evt);
          }
        });

    jPanel13.add(mediawikistreamCheckbox);

    jPanel16.setLayout(new java.awt.GridLayout(3, 2));

    jLabel3.setText(messages.getString("Cchannel"));
    jPanel16.add(jLabel3);

    channelField.setText(config.getProperty("channel"));
    jPanel16.add(channelField);

    jLabel4.setText(messages.getString("Sserver"));
    jPanel16.add(jLabel4);

    serverField.setText("irc.wikimedia.org");
    jPanel16.add(serverField);

    jLabel5.setText(messages.getString("Port"));
    jPanel16.add(jLabel5);

    portField.setText("6667");
    jPanel16.add(portField);

    jPanel13.add(jPanel16);

    jPanel17.setLayout(new javax.swing.BoxLayout(jPanel17,
        javax.swing.BoxLayout.Y_AXIS));

    ircconButton.setText(messages.getString("Connect"));
    ircconButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        ircconButtonActionPerformed(evt);
      }
    });

    jPanel27.add(ircconButton);

    ircdisconnectButton.setText(java.util.ResourceBundle.getBundle(
        "MessagesBundle").getString("disconnect"));
    ircdisconnectButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        ircdisconnectButtonActionPerformed(evt);
      }
    });

    jPanel27.add(ircdisconnectButton);

    jPanel17.add(jPanel27);

    jLabel6.setText(messages.getString("multChannels"));
    jPanel17.add(jLabel6);

    jPanel13.add(jPanel17);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 6;
    irctoptopPanel1.add(jPanel13, gridBagConstraints);

    IRCPannel.add(irctoptopPanel1);

    tab.addTab(messages.getString("Connections"), IRCPannel);

    {
      //tab.addTab(messages.getString("Connections"), connectionsPanel);
    }

    {
      aboutPanel.setLayout(new javax.swing.BoxLayout(aboutPanel,
          javax.swing.BoxLayout.Y_AXIS));

      editorPane1.putClientProperty("charset", "UTF-8");
      String lang = config.currentLocale.getLanguage();
      URL helpURL = this.getClass().getResource("/About_"+lang+".html");
      if (helpURL == null)
        helpURL = this.getClass().getResource("/About.html");
      if (helpURL != null) {
        try {
          editorPane1.setPage(helpURL);
        } catch (IOException e) {
          System.err.println("Attempted to read a bad URL: " + helpURL);
        }
      } else {
        System.err.println("Couldn't find file: About.html");
      }

      editorPane1.addHyperlinkListener(new HyperlinkListener() {
        public void hyperlinkUpdate(HyperlinkEvent event) {
          if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            openBrowser(event.getURL().toString());
          }
        }
      });
      editorPane1.addPropertyChangeListener("page", new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
          try {
            int indexOf = editorPane1.getDocument()
            .getText(0, editorPane1.getDocument().getLength()).indexOf("{{Version}}");
            editorPane1.select(indexOf, indexOf+"{{Version}}".length());
            editorPane1.replaceSelection(messages.getString("SoftwareName")+config.version);
            editorPane1.setEditable(false);
            aboutLoaded = true;
          } catch (BadLocationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
          }
        }
      });

      // synchronization HACK
      while (!aboutLoaded) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e1) {
        }
      }

      jScrollPane10.setViewportView(editorPane1);

      aboutPanel.add(jScrollPane10);

      tab.addTab(messages.getString("about"), aboutPanel);
    }

    getContentPane().add(tab, java.awt.BorderLayout.CENTER);
  }// </editor-fold>//GEN-END:initComponents

  private void ircdisconnectButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_ircdisconnectButtonActionPerformed
    disconnectIRC();
  }// GEN-LAST:event_ircdisconnectButtonActionPerformed

//  private void watchdelpermButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_watchdelpermButtonActionPerformed
//    // TODO add your handling code here:
//  }// GEN-LAST:event_watchdelpermButtonActionPerformed

  private void browserpickActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_browserpickActionPerformed
    if (browserpick.isSelected()) {
      browserchooserButton.setEnabled(false);
      browserField.setEnabled(false);
    } else {
      browserchooserButton.setEnabled(true);
      browserField.setEnabled(true);

    }
  }// GEN-LAST:event_browserpickActionPerformed

  private void sortarticlesButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_sortarticlesButtonActionPerformed
    Object list[] = bubblesort(config.watchlistModel.toArray());

    config.watchlistModel.clear();
    for (i = 0; i < list.length; i++)
      config.watchlistModel.addElement((String) list[i]);

  }// GEN-LAST:event_sortarticlesButtonActionPerformed

  private void watchlistimportButtonActionPerformed(
      java.awt.event.ActionEvent evt) {// GEN-FIRST:event_watchlistimportButtonActionPerformed
    JFileChooser importChooser = new JFileChooser();
    int returnval = importChooser.showOpenDialog(this);
    if (returnval == JFileChooser.APPROVE_OPTION) {
      try {
        File watchlistFile = new File("" + importChooser.getSelectedFile());
        FileInputStream watchimport = new FileInputStream(watchlistFile);
        BufferedInputStream bis = new BufferedInputStream(watchimport);
        String watchlist = new String("");
        String proj = (String) JOptionPane.showInputDialog(this,
            "Enter project, e.g. en.wikipedia\nor hit enter to use "
                + defaultProjectField.getText(), "Project name",
            JOptionPane.PLAIN_MESSAGE);
        if (proj.length() < 5)
          proj = defaultProjectField.getText();
        if (proj.substring(proj.length() - 4).equals(".org"))
          proj = proj.substring(0, proj.length() - 4);

        i = bis.available();
        while (i > 0) {
          byte b[] = new byte[i];
          bis.read(b);
          watchlist = new String(watchlist + new String(b));
          i = bis.available();
        }

        //Pattern p = Pattern.compile("titles\\u005B\\u005D['\"] value=\"([^\"]*)\"");
        Pattern p = Pattern.compile("<input\\s+name=['\"]titles\\u005B\\u005D['\"] ([^>]*)>");
        Pattern p2 = Pattern.compile("value=\"([^\"]*)\"");
        Matcher m = p.matcher(watchlist);

        while (m.find()) {
          Matcher m2 = p2.matcher(m.group(1));
          if (m2.find()) {
            if (!config.watchlistModel.contains(m2.group(1) + "#" + proj))
              config.watchlistModel.addElement(m2.group(1) + "#" + proj);
          }
        }
        watchlistList.clearSelection();
        watchimport.close();
        bis.close();

      } catch (Exception ex) {
      }
    }

  }// GEN-LAST:event_watchlistimportButtonActionPerformed

  private void watchlistRemoveActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_watchlistRemoveActionPerformed
    int indices[] = watchlistList.getSelectedIndices();
    String strings[][] = new String[indices.length][2];
    for (int i = 0; i < strings.length; i++) {
      strings[i] = ((String)config.watchlistModel.getElementAt(indices[i])).split("#");
    }
    for (i = indices.length - 1; i >= 0; i--)
      config.watchlistModel.remove(indices[i]);
    watchlistList.clearSelection();

    synchronized (config.editstableTable.getTreeLock()) {
      for (i = data.editstableModel.getRowCount() - 1; i >= 0; i--) {
        String article = (String)data.editstableModel.getValueAt(i, config.articlecol);
        String project = (String)data.editstableModel.getValueAt(i, config.projectcol);
        for (int j = 0; j < strings.length; j++) {
          if (strings[j][0].equals(article) && strings[j][1].equals(project)) {
            ListIndicator li = (ListIndicator)data.editstableModel.getValueAt(i, config.listcol);
            li = li.removeList(ListIndicator.WATCH);
            data.editstableModel.setValueAt(li, i, config.listcol);
          }
        }
      }
    }
  }// GEN-LAST:event_watchlistRemoveActionPerformed

  private void watchlistAddActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_watchlistAddActionPerformed
    String text = articleField.getText().trim();
    if (text.length() > 0) {
      if (text.matches("[^\\u0023]*")) {
        text = text + "#" + defaultProjectField.getText();
        articleField.setText(text);
      }
      if (!config.watchlistModel.contains(text))
        config.watchlistModel.addElement(text);
      articleField.setText("");
      watchlistList.clearSelection();

      synchronized (config.editstableTable.getTreeLock()) {
        String[] parts = text.split("#");
        for (i = data.editstableModel.getRowCount() - 1; i >= 0; i--) {
          if (parts[0].equals(data.editstableModel.getValueAt(i, config.articlecol))
              && parts[1].equals(data.editstableModel.getValueAt(i, config.projectcol))) {
            ListIndicator li = (ListIndicator)data.editstableModel.getValueAt(i, config.listcol);
            li = li.addList(ListIndicator.WATCH);
            data.editstableModel.setValueAt(li, i, config.listcol);
          }
        }
      }
    }
  }// GEN-LAST:event_watchlistAddActionPerformed

  private void importbotsButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_importbotsButtonActionPerformed
    importbotsButton.setEnabled(false);
    whitelistimportField.setEditable(false);
    botimport = new Importer(whitelistimportField.getText(),
        "/index.php?title=Special:Listusers&group=bot&limit=5000&offset=0",
        this, Importer.GREYLIST, importbotsButton, whitelistimportField);
  }// GEN-LAST:event_importbotsButtonActionPerformed

  private void importadminsButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_importadminsButtonActionPerformed
    importadminsButton.setEnabled(false);
    whitelistimportField.setEditable(false);
    adminimport = new Importer(whitelistimportField.getText(),
        "/index.php?title=Special:Listusers&group=sysop&limit=5000&offset=0",
        this, Importer.WHITELIST, importadminsButton, whitelistimportField);
  }// GEN-LAST:event_importadminsButtonActionPerformed

  private void sortblacklistButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_sortblacklistButtonActionPerformed
    Object list[] = bubblesort(config.blacklistModel.toArray());

    config.blacklistModel.clear();
    for (i = 0; i < list.length; i++)
      config.blacklistModel.addElement((String) list[i]);

  }// GEN-LAST:event_sortblacklistButtonActionPerformed

  private void sortwhitelistButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_sortwhitelistButtonActionPerformed
    Object list[] = bubblesort(config.whitelistModel.toArray());

    config.whitelistModel.clear();
    for (i = 0; i < list.length; i++)
      config.whitelistModel.addElement((String) list[i]);
  }// GEN-LAST:event_sortwhitelistButtonActionPerformed

  private void sortgreylistButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_sortgreylistButtonActionPerformed
    Object list[] = bubblesort(config.greylistModel.toArray());

    config.greylistModel.clear();
    for (i = 0; i < list.length; i++)
      config.greylistModel.addElement((String) list[i]);

  }// GEN-LAST:event_sortgreylistButtonActionPerformed

  private void blacklistRemoveActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_blacklistRemoveActionPerformed
    int indices[] = blacklistList.getSelectedIndices();
    String strings[][] = new String[indices.length][2];
    for (int i = 0; i < strings.length; i++) {
      strings[i] = ((String)config.blacklistModel.getElementAt(indices[i])).split("#");
    }
    for (i = indices.length - 1; i >= 0; i--)
      config.blacklistModel.remove(indices[i]);
    blacklistList.clearSelection();

    synchronized (config.editstableTable.getTreeLock()) {
      for (i = data.editstableModel.getRowCount() - 1; i >= 0; i--) {
        String editor = (String)data.editstableModel.getValueAt(i, config.editorcol);
        String project = (String)data.editstableModel.getValueAt(i, config.projectcol);
        for (int j = 0; j < strings.length; j++) {
          if (strings[j][0].equals(editor) && strings[j][1].equals(project)) {
            ListIndicator li = (ListIndicator)data.editstableModel.getValueAt(i, config.listcol);
            li = li.removeList(ListIndicator.BLACK);
            data.editstableModel.setValueAt(li, i, config.listcol);
          }
        }
      }
    }
  }// GEN-LAST:event_blacklistRemoveActionPerformed

  private void whitelistRemoveActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_whitelistRemoveActionPerformed
    int indices[] = whitelistList.getSelectedIndices();
    for (i = indices.length - 1; i >= 0; i--)
      config.whitelistModel.remove(indices[i]);
    whitelistList.clearSelection();
  }// GEN-LAST:event_whitelistRemoveActionPerformed

  private void greylistRemoveActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_whitelistRemoveActionPerformed
    int indices[] = greylistList.getSelectedIndices();
    for (i = indices.length - 1; i >= 0; i--)
      config.greylistModel.remove(indices[i]);
    greylistList.clearSelection();
  }// GEN-LAST:event_whitelistRemoveActionPerformed

  private void blacklistAddActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_blacklistAddActionPerformed
    String text = userlistField.getText().trim();
    if (text.length() > 0) {
      if (text.matches("[^\\u0023]*")) {
        text = text + "#" + defaultProjectField.getText();
        userlistField.setText(text);
      }
      if (!config.blacklistModel.contains(userlistField.getText())) {
        if (config.whitelistModel.contains(userlistField.getText()))
          config.whitelistModel.removeElement(userlistField.getText());
        if (config.greylistModel.contains(userlistField.getText()))
          config.greylistModel.removeElement(userlistField.getText());
        config.blacklistModel.addElement(userlistField.getText());
      }
      userlistField.setText("");
      blacklistList.clearSelection();

      synchronized (config.editstableTable.getTreeLock()) {
        String[] parts = text.split("#");
        for (i = data.editstableModel.getRowCount() - 1; i >= 0; i--) {
          if (parts[0].equals(data.editstableModel.getValueAt(i, config.editorcol))
              && parts[1].equals(data.editstableModel.getValueAt(i, config.projectcol))) {
            ListIndicator li = (ListIndicator)data.editstableModel.getValueAt(i, config.listcol);
            li = li.addList(ListIndicator.BLACK);
            data.editstableModel.setValueAt(li, i, config.listcol);
          }
        }
      }
    }
  }// GEN-LAST:event_blacklistAddActionPerformed

  private void whitelistAddActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_whitelistAddActionPerformed
    if (userlistField.getText().length() > 0) {
      if (userlistField.getText().matches("[^\\u0023]*"))
        userlistField.setText(userlistField.getText() + "#"
            + defaultProjectField.getText());
      if (!config.whitelistModel.contains(userlistField.getText())) {
        if (config.blacklistModel.contains(userlistField.getText()))
          config.blacklistModel.removeElement(userlistField.getText());
        if (config.greylistModel.contains(userlistField.getText()))
          config.greylistModel.removeElement(userlistField.getText());
        config.whitelistModel.addElement(userlistField.getText());

        synchronized (config.editstableTable.getTreeLock()) {
          int tmp = data.editstableModel.getRowCount();
          for (i = tmp - 1; i >= 0; i--)
            if (((String) data.editstableModel.getValueAt(i, config.editorcol))
                .equals(userlistField.getText()))
              data.editstableModel.removeRow(i);
        }
      }
      userlistField.setText("");
      whitelistList.clearSelection();
    }
  }// GEN-LAST:event_whitelistAddActionPerformed

  private void greylistAddActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_greylistAddActionPerformed
    if (userlistField.getText().length() > 0) {
      if (userlistField.getText().matches("[^\\u0023]*"))
        userlistField.setText(userlistField.getText() + "#"
            + defaultProjectField.getText());
      if (!config.greylistModel.contains(userlistField.getText())) {
        if (config.whitelistModel.contains(userlistField.getText()))
          config.whitelistModel.removeElement(userlistField.getText());
        if (config.blacklistModel.contains(userlistField.getText()))
          config.blacklistModel.removeElement(userlistField.getText());
        config.greylistModel.addElement(userlistField.getText());

        synchronized (config.editstableTable.getTreeLock()) {
          int tmp = data.editstableModel.getRowCount();
          for (i = tmp - 1; i >= 0; i--)
            if (((String) data.editstableModel.getValueAt(i, config.editorcol))
                .equals(userlistField.getText()))
              data.editstableModel.removeRow(i);
        }
      }
      userlistField.setText("");
      greylistList.clearSelection();
    }
  }// GEN-LAST:event_greylistAddActionPerformed

  private void watchlistListValueChanged(
      javax.swing.event.ListSelectionEvent evt) {// GEN-FIRST:event_watchlistListValueChanged
    this.valueChanged(evt);// TODO add your handling code here:
  }// GEN-LAST:event_watchlistListValueChanged

  private void blacklistListValueChanged(
      javax.swing.event.ListSelectionEvent evt) {// GEN-FIRST:event_blacklistListValueChanged
    this.valueChanged(evt);// TODO add your handling code here:
  }// GEN-LAST:event_blacklistListValueChanged

  private void whitelistListValueChanged(
      javax.swing.event.ListSelectionEvent evt) {// GEN-FIRST:event_whitelistListValueChanged
    this.valueChanged(evt);// TODO add your handling code here:
  }// GEN-LAST:event_whitelistListValueChanged

  private void greylistListValueChanged(javax.swing.event.ListSelectionEvent evt) {// GEN-FIRST:event_greylistListValueChanged
    this.valueChanged(evt);// TODO add your handling code here:
  }// GEN-LAST:event_greylistListValueChanged

  private void tempwatchlistListValueChanged(
      javax.swing.event.ListSelectionEvent evt) {// GEN-FIRST:event_tempwatchlistListValueChanged
    this.valueChanged(evt);// TODO add your handling code here:
  }// GEN-LAST:event_tempwatchlistListValueChanged

  private void tempblacklistListValueChanged(
      javax.swing.event.ListSelectionEvent evt) {// GEN-FIRST:event_tempblacklistListValueChanged
    this.valueChanged(evt);// TODO add your handling code here:
  }// GEN-LAST:event_tempblacklistListValueChanged

  private void tempwhitelistListValueChanged(
      javax.swing.event.ListSelectionEvent evt) {// GEN-FIRST:event_tempwhitelistListValueChanged
    this.valueChanged(evt);// TODO add your handling code here:
  }// GEN-LAST:event_tempwhitelistListValueChanged

  private void sorttempwatchlistButtonActionPerformed(
      java.awt.event.ActionEvent evt) {// GEN-FIRST:event_sorttempwatchlistButtonActionPerformed

    Object list[] = bubblesort(config.tempwatchlistModel.toArray());

    config.tempwatchlistModel.clear();
    for (i = 0; i < list.length; i++)
      config.tempwatchlistModel.addElement((String) list[i]);
  }// GEN-LAST:event_sorttempwatchlistButtonActionPerformed

  private void sorttempblacklistButtonActionPerformed(
      java.awt.event.ActionEvent evt) {// GEN-FIRST:event_sorttempblacklistButtonActionPerformed
    Object list[] = bubblesort(config.tempblacklistModel.toArray());

    config.tempblacklistModel.clear();
    for (i = 0; i < list.length; i++)
      config.tempblacklistModel.addElement((String) list[i]);
  }// GEN-LAST:event_sorttempblacklistButtonActionPerformed

  private void sorttempwhitelistButtonActionPerformed(
      java.awt.event.ActionEvent evt) {// GEN-FIRST:event_sorttempwhitelistButtonActionPerformed
    Object list[] = bubblesort(config.tempwhitelistModel.toArray());

    config.tempwhitelistModel.clear();
    for (i = 0; i < list.length; i++)
      config.tempwhitelistModel.addElement((String) list[i]);
  }// GEN-LAST:event_sorttempwhitelistButtonActionPerformed

  private void tempwatchlistRemoveActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_tempwatchlistRemoveActionPerformed
    int indices[] = tempwatchlistList.getSelectedIndices();
    String strings[][] = new String[indices.length][2];
    for (int i = 0; i < strings.length; i++) {
      strings[i] = ((String)config.tempwatchlistModel.getElementAt(indices[i])).split("#");
    }
    for (i = indices.length - 1; i >= 0; i--)
      config.tempwatchlistModel.remove(indices[i]);
    tempwatchlistList.clearSelection();

    synchronized (config.editstableTable.getTreeLock()) {
      for (i = data.editstableModel.getRowCount() - 1; i >= 0; i--) {
        String article = (String)data.editstableModel.getValueAt(i, config.articlecol);
        String project = (String)data.editstableModel.getValueAt(i, config.projectcol);
        for (int j = 0; j < strings.length; j++) {
          if (strings[j][0].equals(article) && strings[j][1].equals(project)) {
            ListIndicator li = (ListIndicator)data.editstableModel.getValueAt(i, config.listcol);
            li = li.removeList(ListIndicator.TWATCH);
            data.editstableModel.setValueAt(li, i, config.listcol);
          }
        }
      }
    }
  }// GEN-LAST:event_tempwatchlistRemoveActionPerformed

  private void tempblacklistRemoveActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_tempblacklistRemoveActionPerformed
    int indices[] = tempblacklistList.getSelectedIndices();
    String strings[][] = new String[indices.length][2];
    for (int i = 0; i < strings.length; i++) {
      strings[i] = ((String)config.tempblacklistModel.getElementAt(indices[i])).split("#");
    }
    for (i = indices.length - 1; i >= 0; i--)
      config.tempblacklistModel.remove(indices[i]);
    tempblacklistList.clearSelection();

    synchronized (config.editstableTable.getTreeLock()) {
      for (i = data.editstableModel.getRowCount() - 1; i >= 0; i--) {
        String editor = (String)data.editstableModel.getValueAt(i, config.editorcol);
        String project = (String)data.editstableModel.getValueAt(i, config.projectcol);
        for (int j = 0; j < strings.length; j++) {
          if (strings[j][0].equals(editor) && strings[j][1].equals(project)) {
            ListIndicator li = (ListIndicator)data.editstableModel.getValueAt(i, config.listcol);
            li = li.removeList(ListIndicator.TBLACK);
            data.editstableModel.setValueAt(li, i, config.listcol);
          }
        }
      }
    }
  }// GEN-LAST:event_tempblacklistRemoveActionPerformed

  private void tempwhitelistRemoveActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_tempwhitelistRemoveActionPerformed
    int indices[] = tempwhitelistList.getSelectedIndices();
    for (i = indices.length - 1; i >= 0; i--)
      config.tempwhitelistModel.remove(indices[i]);
    tempwhitelistList.clearSelection();
  }// GEN-LAST:event_tempwhitelistRemoveActionPerformed

  private void tempwatchlistAddActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_tempwatchlistAddActionPerformed
    String text = articleField.getText().trim();
    if (text.length() > 0) {
      if (text.matches("[^\\u0023]*")) {
        text = text + "#" + defaultProjectField.getText();
        articleField.setText(text);
      }
      if (!config.tempwatchlistModel.contains(text))
        config.tempwatchlistModel.addElement(text);
      articleField.setText("");
      tempwatchlistList.clearSelection();

      synchronized (config.editstableTable.getTreeLock()) {
        String[] parts = text.split("#");
        for (i = data.editstableModel.getRowCount() - 1; i >= 0; i--) {
          if (parts[0].equals(data.editstableModel.getValueAt(i, config.articlecol))
              && parts[1].equals(data.editstableModel.getValueAt(i, config.projectcol))) {
            ListIndicator li = (ListIndicator)data.editstableModel.getValueAt(i, config.listcol);
            li = li.addList(ListIndicator.TWATCH);
            data.editstableModel.setValueAt(li, i, config.listcol);
          }
        }
      }
    }
  }// GEN-LAST:event_tempwatchlistAddActionPerformed

  private void tempblacklistAddActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_tempblacklistAddActionPerformed
    String text = templistField.getText().trim();
    if (text.length() > 0) {
      if (text.matches("[^\\u0023]*")) {
        text = text + "#" + defaultProjectField.getText();
        templistField.setText(text);
      }
      if (!config.tempblacklistModel.contains(templistField.getText())) {
        if (config.tempwhitelistModel.contains(templistField.getText()))
          config.tempwhitelistModel.removeElement(templistField.getText());
        config.tempblacklistModel.addElement(templistField.getText());
      }
      templistField.setText("");
      tempblacklistList.clearSelection();

      synchronized (config.editstableTable.getTreeLock()) {
        String[] parts = text.split("#");
        for (i = data.editstableModel.getRowCount() - 1; i >= 0; i--) {
          if (parts[0].equals(data.editstableModel.getValueAt(i, config.editorcol))
              && parts[1].equals(data.editstableModel.getValueAt(i, config.projectcol))) {
            ListIndicator li = (ListIndicator)data.editstableModel.getValueAt(i, config.listcol);
            li = li.addList(ListIndicator.TBLACK);
            data.editstableModel.setValueAt(li, i, config.listcol);
          }
        }
      }
    }
  }// GEN-LAST:event_tempblacklistAddActionPerformed

  private void tempwhitelistAddActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_tempwhitelistAddActionPerformed
    if (templistField.getText().length() > 0) {
      if (templistField.getText().matches("[^\\u0023]*"))
        templistField.setText(templistField.getText() + "#"
            + defaultProjectField.getText());
      if (!config.tempwhitelistModel.contains(templistField.getText())) {
        if (config.tempblacklistModel.contains(templistField.getText()))
          config.tempblacklistModel.removeElement(templistField.getText());
        config.tempwhitelistModel.addElement(templistField.getText());

        synchronized (config.editstableTable.getTreeLock()) {
          int tmp = data.editstableModel.getRowCount();
          for (i = tmp - 1; i >= 0; i--)
            if (((String) data.editstableModel.getValueAt(i, config.editorcol))
                .equals(templistField.getText()))
              data.editstableModel.removeRow(i);
        }
      }
      templistField.setText("");
      tempwhitelistList.clearSelection();
    }

  }// GEN-LAST:event_tempwhitelistAddActionPerformed

  private void sortregexpblacklistButtonActionPerformed(
      java.awt.event.ActionEvent evt) {// GEN-FIRST:event_sortregexpblacklistButtonActionPerformed
    Object list[] = bubblesort(config.regexpblackModel.toArray());

    config.regexpblackModel.clear();
    for (i = 0; i < list.length; i++)
      config.regexpblackModel.addElement((String) list[i]);
  }// GEN-LAST:event_sortregexpblacklistButtonActionPerformed

  private void sortregexpwhitelistButtonActionPerformed(
      java.awt.event.ActionEvent evt) {// GEN-FIRST:event_sortregexpwhitelistButtonActionPerformed
    Object list[] = bubblesort(config.regexpwhiteModel.toArray());

    config.regexpwhiteModel.clear();
    for (i = 0; i < list.length; i++)
      config.regexpwhiteModel.addElement((String) list[i]);
  }// GEN-LAST:event_sortregexpwhitelistButtonActionPerformed

  private void regexpblackRemoveActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_regexpblackRemoveActionPerformed
    int indices[] = regexpblackList.getSelectedIndices();
    String strings[] = new String[indices.length];
    for (int i = 0; i < strings.length; i++) {
      strings[i] = (String)config.regexpblackModel.getElementAt(indices[i]);
    }
    for (i = indices.length - 1; i >= 0; i--)
      config.regexpblackModel.remove(indices[i]);
    regexpblackList.clearSelection();

    boolean rblackpage = config.getBooleanProp("rblackpage");
    boolean rblacksummary = config.getBooleanProp("rblacksummary");
    boolean rblackuser = config.getBooleanProp("rblackuser");
    synchronized (config.editstableTable.getTreeLock()) {
      for (i = data.editstableModel.getRowCount() - 1; i >= 0; i--) {
        String article = (String)data.editstableModel.getValueAt(i, config.articlecol);
        String summary = (String)data.editstableModel.getValueAt(i, config.summarycol);
        String editor = (String)data.editstableModel.getValueAt(i, config.editorcol);
        boolean matches = false;
        for (int j = strings.length - 1; j >= 0; j--) {
          if ((rblackpage && article.matches(strings[j]))
              || (rblacksummary && summary.matches(strings[j]))
              || (rblackuser && editor.matches(strings[j]))) {
            matches = true;
            break;
          }
        }
        if (matches) {
          boolean matchesOthers = false;
          int a = config.regexpblackModel.getSize();
          for (int j = 0; j < a; j++) {
            if ((rblackpage && article.matches((String) config.regexpblackModel.getElementAt(j)))
                || (rblacksummary && summary.matches((String) config.regexpblackModel.getElementAt(j)))) {
              matchesOthers = true;
              break;
            }
          }
          if (!matchesOthers) {
            ListIndicator li = (ListIndicator)data.editstableModel.getValueAt(i, config.listcol);
            li = li.removeList(ListIndicator.RBLACK);
            data.editstableModel.setValueAt(li, i, config.listcol);
          }
        }
      }
    }
  }// GEN-LAST:event_regexpblackRemoveActionPerformed

  private void regexpwhiteRemoveActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_regexpwhiteRemoveActionPerformed
    int indices[] = regexpwhiteList.getSelectedIndices();
    for (i = indices.length - 1; i >= 0; i--)
      config.regexpwhiteModel.remove(indices[i]);
    regexpwhiteList.clearSelection();
  }// GEN-LAST:event_regexpwhiteRemoveActionPerformed

  private void regexpblackAddActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_regexpblackAddActionPerformed
    if (regexpField.getText().length() > 0) {
      String text = regexpField.getText();

      if (!config.regexpblackModel.contains(text)) {
        if (config.regexpwhiteModel.contains(text))
          config.regexpwhiteModel.removeElement(text);
        config.regexpblackModel.addElement(text);
      }
      regexpField.setText("");
      regexpblackList.clearSelection();

      boolean rblackpage = config.getBooleanProp("rblackpage");
      boolean rblacksummary = config.getBooleanProp("rblacksummary");
      boolean rblackuser = config.getBooleanProp("rblackuser");
      synchronized (config.editstableTable.getTreeLock()) {
        for (i = data.editstableModel.getRowCount() - 1; i >= 0; i--) {
          String article = (String)data.editstableModel.getValueAt(i, config.articlecol);
          String summary = (String)data.editstableModel.getValueAt(i, config.summarycol);
          String editor = (String)data.editstableModel.getValueAt(i, config.editorcol);

          if ((rblackpage && article.matches(text))
              || (rblacksummary && summary.matches(text))
              || (rblackuser && editor.matches(text))) {
            ListIndicator li = (ListIndicator)data.editstableModel.getValueAt(i, config.listcol);
            li = li.addList(ListIndicator.RBLACK);
            data.editstableModel.setValueAt(li, i, config.listcol);
          }
        }
      }
    }
  }// GEN-LAST:event_regexpblackAddActionPerformed

  private void regexpwhiteAddActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_regexpwhiteAddActionPerformed
    String text = regexpField.getText();

    if (text.length() > 0) {
      if (!config.regexpwhiteModel.contains(text)) {
        if (config.regexpblackModel.contains(text))
          config.regexpblackModel.removeElement(text);
        config.regexpwhiteModel.addElement(text);

        synchronized (config.editstableTable.getTreeLock()) {
          ListModel lm = data.editstableModel;
          boolean rblackpage = config.getBooleanProp("rblackpage");
          boolean rblacksummary = config.getBooleanProp("rblacksummary");
          boolean rblackuser = config.getBooleanProp("rblackuser");
          for (i = data.editstableModel.getRowCount() - 1; i >= 0; i--) {

            if ((rblackpage && ((String)lm.getValueAt(i, config.articlecol)).matches(text))
                || (rblacksummary && ((String)lm.getValueAt(i, config.summarycol)).matches(text))
                || (rblackuser && ((String)lm.getValueAt(i, config.editorcol)).matches(text)))
              data.editstableModel.removeRow(i);
          }
        }
      }
      regexpField.setText("");
      regexpwhiteList.clearSelection();
    }
  }// GEN-LAST:event_regexpwhiteAddActionPerformed

  private void regexpblackListValueChanged(
      javax.swing.event.ListSelectionEvent evt) {// GEN-FIRST:event_regexpblackListValueChanged
    this.valueChanged(evt);
  }// GEN-LAST:event_regexpblackListValueChanged

  private void regexpwhiteListValueChanged(
      javax.swing.event.ListSelectionEvent evt) {// GEN-FIRST:event_regexpwhiteListValueChanged
    this.valueChanged(evt);
  }// GEN-LAST:event_regexpwhiteListValueChanged

  private void VandalismStreamButtonActionPerformed(
      java.awt.event.ActionEvent evt) {// GEN-FIRST:event_VandalismStreamButtonActionPerformed
    connectIRC();
  }// GEN-LAST:event_VandalismStreamButtonActionPerformed

  private void mediawikistreamCheckboxActionPerformed(
      java.awt.event.ActionEvent evt) {// GEN-FIRST:event_mediawikistreamCheckboxActionPerformed
    if (mediawikistreamCheckbox.isSelected()) {
      // activate IRC fields
      channelField.setEditable(true);
      serverField.setEditable(true);
      serverField.setEditable(true);
    } else {
      // grey out IRC fields
      channelField.setEditable(false);
      serverField.setEditable(false);
      serverField.setEditable(false);
    }
  }// GEN-LAST:event_mediawikistreamCheckboxActionPerformed

  private void ircconButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_ircconButtonActionPerformed
    System.out.println("connecting");
    connectIRC();
  }// GEN-LAST:event_ircconButtonActionPerformed

  private void watchdeletedActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_watchdeletedActionPerformed
    if (watchdeleted.isSelected()) {
      watchdelpermButton.setEnabled(true);
      watchdeltempButton.setEnabled(true);
    } else {
      watchdelpermButton.setEnabled(false);
      watchdeltempButton.setEnabled(false);
    }
  }// GEN-LAST:event_watchdeletedActionPerformed

  private void watchwarnedActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_watchwarningActionPerformed
    if (watchwarned.isSelected()) {
      watchwarnedpermButton.setEnabled(true);
      watchwarnedtempButton.setEnabled(true);
    } else {
      watchwarnedpermButton.setEnabled(false);
      watchwarnedtempButton.setEnabled(false);
    }
  }// GEN-LAST:event_watchwarningActionPerformed

  private void blackdeletedActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_blackdeletedActionPerformed
    if (blackdeleted.isSelected()) {
      delpagepermButton.setEnabled(true);
      delpagetempButton.setEnabled(true);
    } else {
      delpagepermButton.setEnabled(false);
      delpagetempButton.setEnabled(false);
    }

  }// GEN-LAST:event_blackdeletedActionPerformed

  private void blackrevertedActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_blackrevertedActionPerformed
    if (blackreverted.isSelected()) {
      reverttempButton.setEnabled(true);
      revertpermButton.setEnabled(true);
    } else {
      reverttempButton.setEnabled(false);
      revertpermButton.setEnabled(false);
    }
  }// GEN-LAST:event_blackrevertedActionPerformed

  private void riskActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_riskActionPerformed
  }// GEN-LAST:event_riskActionPerformed

  private void listprecedenceActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_listprecedenceActionPerformed
    if (listprecedence.isSelected()) {
      watchuserpages.setEnabled(true);
      watchmasschanges.setEnabled(true);
      watchpagemoves.setEnabled(true);
      watchnewpages.setEnabled(true);
    } else {
      watchuserpages.setEnabled(false);
      watchmasschanges.setEnabled(false);
      watchpagemoves.setEnabled(false);
      watchnewpages.setEnabled(false);
    }

    this.actionPerformed(evt);
  }// GEN-LAST:event_listprecedenceActionPerformed

  private void colormovesButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_colormovesButtonActionPerformed
    config.setColorProp("movecolor", JColorChooser.showDialog(this, messages
        .getString("ColorChooser.title"), config.getColorProp("movecolor")));

    colormovesButton.setBackground(config.getColorProp("movecolor"));
    colormovesButton.setContentAreaFilled(false);
    colormovesButton.setOpaque(true);
  }// GEN-LAST:event_colormovesButtonActionPerformed

  private void coloruserpageButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_coloruserpageButtonActionPerformed
    config.setColorProp("userpagecolor", JColorChooser.showDialog(this,
        messages.getString("ColorChooser.title"), config
            .getColorProp("userpagecolor")));

    coloruserpageButton.setBackground(config.getColorProp("userpagecolor"));
    coloruserpageButton.setContentAreaFilled(false);
    coloruserpageButton.setOpaque(true);
  }// GEN-LAST:event_coloruserpageButtonActionPerformed

  private void coloripsButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_coloripsButtonActionPerformed
    System.out.println("IP color: " + config.getProperty("ipcolorR") + " "
        + config.getProperty("ipcolorG") + " " + config.getProperty("ipcolorB")
        + " ");
    config.setColorProp("ipcolor", JColorChooser.showDialog(this, messages
        .getString("ColorChooser.title"), config.getColorProp("ipcolor")));
    // System.out.println("IP color: "+config.getProperty("ipcolorR")+"
    // "+config.getProperty("ipcolorG")+" "+config.getProperty("ipcolorB")+" ");

    coloripsButton.setBackground(config.getColorProp("ipcolor"));
    coloripsButton.setContentAreaFilled(false);
    coloripsButton.setOpaque(true);
    System.out.println("IP color: " + config.getProperty("ipcolorR") + " "
        + config.getProperty("ipcolorG") + " " + config.getProperty("ipcolorB")
        + " ");

  }// GEN-LAST:event_coloripsButtonActionPerformed

  private void colornewusersButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_colornewusersButtonActionPerformed
    System.out.println("New user color: " + config.getProperty("newuserolorR") + " "
        + config.getProperty("newusercolorG") + " " + config.getProperty("newusercolorB")
        + " ");
    config.setColorProp("newusercolor", JColorChooser.showDialog(this, messages
        .getString("ColorChooser.title"), config.getColorProp("newusercolor")));
    // System.out.println("New user color: "+config.getProperty("newusercolorR")+"
    // "+config.getProperty("newusercolorG")+" "+config.getProperty("newusercolorB")+" ");

    colornewusersButton.setBackground(config.getColorProp("newusercolor"));
    colornewusersButton.setContentAreaFilled(false);
    colornewusersButton.setOpaque(true);
    System.out.println("New user color: " + config.getProperty("newusercolorR") + " "
        + config.getProperty("newusercolorG") + " " + config.getProperty("newusercolorB")
        + " ");

  }// GEN-LAST:event_colornewusersButtonActionPerformed

  private void colornewButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_colornewButtonActionPerformed
    UIManager.put("ColorChooser.cancelText", "Zrušit");
    config.setColorProp("newcolor", JColorChooser.showDialog(this, messages
        .getString("ColorChooser.title"), config.getColorProp("newcolor")));

    colornewButton.setBackground(config.getColorProp("newcolor"));
    colornewButton.setContentAreaFilled(false);
    colornewButton.setOpaque(true);
  }// GEN-LAST:event_colornewButtonActionPerformed

  private void colorchangedButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_colorchangedButtonActionPerformed
    config.setColorProp("changedcolor", JColorChooser.showDialog(this, messages
        .getString("ColorChooser.title"), config.getColorProp("changedcolor")));

    colorchangedButton.setBackground(config.getColorProp("changedcolor"));
    colorchangedButton.setContentAreaFilled(false);
    colorchangedButton.setOpaque(true);
  }// GEN-LAST:event_colorchangedButtonActionPerformed

  private void colorwatchButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_colorwatchButtonActionPerformed
    config.setColorProp("watchlistcolor", JColorChooser.showDialog(this,
        messages.getString("ColorChooser.title"), config
            .getColorProp("watchlistcolor")));

    colorwatchButton.setBackground(config.getColorProp("watchlistcolor"));
    colorwatchButton.setContentAreaFilled(false);
    colorwatchButton.setOpaque(true);
  }// GEN-LAST:event_colorwatchButtonActionPerformed

  private void colorblistButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_colorblistButtonActionPerformed
    Color newcolor = JColorChooser
        .showDialog(this, messages.getString("ColorChooser.title"), config
            .getColorProp("blacklistcolor"));
    config.setColorProp("blacklistcolor", newcolor);
    colorblistButton.setBackground(newcolor);

    colorblistButton.setContentAreaFilled(false);
    colorblistButton.setOpaque(true);
  }// GEN-LAST:event_colorblistButtonActionPerformed

  private void importButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_importButtonActionPerformed
    config.importLists(importField.getText());
  }// GEN-LAST:event_importButtonActionPerformed

  private void exportButton1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_exportButton1ActionPerformed
    config.exportLists(exserverField.getText());
  }// GEN-LAST:event_exportButton1ActionPerformed

  private void importchooserButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_importchooserButtonActionPerformed
    // launch file chooser
    JFileChooser importChooser = new JFileChooser();
    int returnval = importChooser.showOpenDialog(this);
    if (returnval == JFileChooser.APPROVE_OPTION)
      importField.setText("" + importChooser.getSelectedFile());
  }// GEN-LAST:event_importchooserButtonActionPerformed

  private void exportchooserButton1ActionPerformed(
      java.awt.event.ActionEvent evt) {// GEN-FIRST:event_exportchooserButton1ActionPerformed
    // launch file chooser
    JFileChooser exportChooser = new JFileChooser();
    int returnval = exportChooser.showOpenDialog(this);
    if (returnval == JFileChooser.APPROVE_OPTION)
      exserverField.setText("" + exportChooser.getSelectedFile());
  }// GEN-LAST:event_exportchooserButton1ActionPerformed

  private void newversionActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_newversionActionPerformed
    this.actionPerformed(evt);
  }// GEN-LAST:event_newversionActionPerformed

  private void connectatstartActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_connectatstartActionPerformed
    this.actionPerformed(evt);
  }// GEN-LAST:event_connectatstartActionPerformed

  private void queueeditsActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_queueeditsActionPerformed
    this.actionPerformed(evt);
  }// GEN-LAST:event_queueeditsActionPerformed

  private void showwatchActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_showwatchActionPerformed
    if (showwatch.isSelected()) {
      listprecedence.setEnabled(true);
    } else {
      listprecedence.setEnabled(false);
    }
    if (listprecedence.isSelected()) {
      watchuserpages.setEnabled(true);
      watchmasschanges.setEnabled(true);
      watchpagemoves.setEnabled(true);
      watchnewpages.setEnabled(true);
    } else {
      watchuserpages.setEnabled(false);
      watchmasschanges.setEnabled(false);
      watchpagemoves.setEnabled(false);
      watchnewpages.setEnabled(false);
    }
  }// GEN-LAST:event_showwatchActionPerformed

  private void newpagesActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_newpagesActionPerformed
    if (newpages.isSelected()) {
      listprecedence.setEnabled(true);
      if (listprecedence.isSelected()) {
        watchuserpages.setEnabled(true);
        watchmasschanges.setEnabled(true);
        watchpagemoves.setEnabled(true);
        watchnewpages.setEnabled(true);
      } else {
        watchuserpages.setEnabled(false);
        watchmasschanges.setEnabled(false);
        watchpagemoves.setEnabled(false);
        watchnewpages.setEnabled(false);
      }
    } else {
      listprecedence.setEnabled(false);
    }

  }// GEN-LAST:event_newpagesActionPerformed

  private void showipsActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_showipsActionPerformed

    if (showips.isSelected()) {
      listprecedence.setEnabled(true);

      if (listprecedence.isSelected()) {
        watchuserpages.setEnabled(true);
        watchmasschanges.setEnabled(true);
        watchpagemoves.setEnabled(true);
        watchnewpages.setEnabled(true);
      } else {
        watchuserpages.setEnabled(false);
        watchmasschanges.setEnabled(false);
        watchpagemoves.setEnabled(false);
        watchnewpages.setEnabled(false);
      }
    } else {
      listprecedence.setEnabled(false);
    }
  }// GEN-LAST:event_showipsActionPerformed

  private void vipeditButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_vipeditButtonActionPerformed
    this.actionPerformed(evt);
  }// GEN-LAST:event_vipeditButtonActionPerformed

  private void blockButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_blockButtonActionPerformed
    this.actionPerformed(evt);
  }// GEN-LAST:event_blockButtonActionPerformed

  private void vipButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_vipButtonActionPerformed
    this.actionPerformed(evt);
  }// GEN-LAST:event_vipButtonActionPerformed

  private void langListActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_langListActionPerformed
    this.actionPerformed(evt);
    System.out.println(langList.getSelectedItem());
    System.out.println(langList.getSelectedIndex());

  }// GEN-LAST:event_langListActionPerformed

  private void timeZoneListActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_langListActionPerformed
    this.actionPerformed(evt);
    data.setTimeZone(TimeZone.getTimeZone(timeZoneCodes[timeZoneList.getSelectedIndex()]));
    //TODO Beren dodelat
  }// GEN-LAST:event_langListActionPerformed

  private void feelListActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_feelListActionPerformed
    this.actionPerformed(evt);
    try {
      feel = feelCodes[feelList.getSelectedIndex()];
      UIManager.setLookAndFeel(feel);
      themeList.setEnabled(feelList.getSelectedIndex() == 0);
      themeLabel.setEnabled(feelList.getSelectedIndex() == 0);
      SwingUtilities.updateComponentTreeUI(this);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }// GEN-LAST:event_feelListActionPerformed

  private void themeListActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_themeListActionPerformed
    this.actionPerformed(evt);
    try {
      theme = themeCodes[themeList.getSelectedIndex()];
      MetalLookAndFeel.setCurrentTheme(themes[themeList.getSelectedIndex()]);
      UIManager.setLookAndFeel(feel);
      SwingUtilities.updateComponentTreeUI(this);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }// GEN-LAST:event_themeListActionPerformed

  private void vandalism_nl_boxActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_vandalism_nl_boxActionPerformed
    this.actionPerformed(evt);
  }// GEN-LAST:event_vandalism_nl_boxActionPerformed

  private void vandalism_cs_boxActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_vandalism_cs_boxActionPerformed
    this.actionPerformed(evt);
  }// GEN-LAST:event_vandalism_cs_boxActionPerformed

  private void configSaveActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_configSaveActionPerformed
    this.actionPerformed(evt);
    saveSettings();
    savefiles("Saved by user");
  }// GEN-LAST:event_configSaveActionPerformed

  private void configCancelActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_configCancelActionPerformed
    this.actionPerformed(evt);
  }// GEN-LAST:event_configCancelActionPerformed

  private void configDefaultsActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_configDefaultsActionPerformed
    this.actionPerformed(evt);
    initDefaultSettings();

  }// GEN-LAST:event_configDefaultsActionPerformed

  private void vandalism_en_boxActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_vandalism_en_boxActionPerformed
    this.actionPerformed(evt);
  }// GEN-LAST:event_vandalism_en_boxActionPerformed

  private void browserchooserButtonActionPerformed(
      java.awt.event.ActionEvent evt) {// GEN-FIRST:event_browserchooserButtonActionPerformed
    // launch file chooser
    JFileChooser browserChooser = new JFileChooser();
    int returnval = browserChooser.showOpenDialog(this);
    if (returnval == JFileChooser.APPROVE_OPTION) {
      browserField.setText("" + browserChooser.getSelectedFile());
    }

  }// GEN-LAST:event_browserchooserButtonActionPerformed

  private void showUserTalkPage(String user, String project) {
    String myUser = urlEncode(user);

    openBrowser("http://" + project + ".org/wiki/User_talk:" + myUser);
  }

  private void showUserContributions(int row) {
    String user = (String) data.editstableModel.getValueAt(row,
        config.editorcol);
    String project = (String) data.editstableModel.getValueAt(row,
        config.projectcol);
    showUserContributions(user, project);
  }

  private void showUserContributions(String userAndProject) {
    try {
      StringTokenizer stt = new StringTokenizer(userAndProject, "#");
      String user = stt.nextToken(), project = stt.nextToken();
      showUserContributions(user, project);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void showUserContributions(String user, String project) {
    try {
      String myUser = urlEncode(user);

      openBrowser("http://" + project
          + ".org/w/index.php?title=Special:Contributions&target=" + myUser);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void showBlockPage(String user, String project) {
    try {
      if (config.getBooleanProp("blockButton")) {
        String str = urlEncode(user);

        openBrowser("http://" + project
            + ".org/w/index.php?title=Special:Blockip&ip=" + str);
      }
      else {
    	  if (config.getBooleanProp("vipButton")) {
        openBrowser(messages.getString("vipButtonURL"));
    	  }
    	  if (config.getBooleanProp("vipeditButton")) {
    	        openBrowser(messages.getString("vipeditbuttonURL"));
    	  }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void showLockPage(String page, String project) {
    try {
      String str = urlEncode(page);

      openBrowser("http://" + project + ".org/w/index.php?title=" + str
          + "&action=protect");

    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void showProjectRecentChanges(int row) {
    showProjectRecentChanges((String) data.editstableModel.getValueAt(row,
        config.projectcol));
  }

  private void showProjectRecentChanges(String project) {
    openBrowser("http://" + project + ".org/wiki/Special:RecentChanges");
  }

  private void showProjectMainPage(String project) {
    openBrowser("http://" + project + ".org/wiki/");
  }

  private void acceptEdit(int row, Object[] rowObject) {
    synchronized (config.editstableTable.getTreeLock()) {
      if (row < data.editstableModel.getRowCount()
          && data.editstableModel.getValueAt(row, config.sectimecol).equals(
              rowObject[config.sectimecol])) {
        acceptEdit(row);
      } else {
        int tmp = data.editstableModel.getRowCount();
        Object sectime = rowObject[config.sectimecol];
        for (i = tmp - 1; i >= 0; i--)
          if (sectime.equals(data.editstableModel.getValueAt(i,
              config.sectimecol))) {
            acceptEdit(tmp);
            return;
          }
      }
    }
  }

  private void acceptEdit(int row) {
    String project = (String) data.editstableModel.getValueAt(row,
        config.projectcol);
    ircB.sendMsg(project, "Checked OK "+project+" [["
        + data.editstableModel.getValueAt(row, config.articlecol) + "]] "
        + data.editstableModel.getValueAt(row, config.urlcol));
    data.editstableModel.removeRow(row);
  }

  private void simplyDeleteEdit(int row, Object[] rowObject) {
    synchronized (config.editstableTable.getTreeLock()) {
      if (row < data.editstableModel.getRowCount()
          && data.editstableModel.getValueAt(row, config.sectimecol).equals(
              rowObject[config.sectimecol])) {
        data.editstableModel.removeRow(row);
      } else {
        int tmp = data.editstableModel.getRowCount();
        Object sectime = rowObject[config.sectimecol];
        for (i = tmp - 1; i >= 0; i--)
          if (sectime.equals(data.editstableModel.getValueAt(i,
              config.sectimecol))) {
            data.editstableModel.removeRow(tmp);
            return;
          }
      }
    }
  }

  private void warnEdit(Object[] rowObject) {
    sendWarn(rowObject);
    synchronized (config.editstableTable.getTreeLock()) {
      String elem = rowObject[config.articlecol] + "#" + rowObject[config.projectcol];

      boolean watchwarned = config.getBooleanProp("watchwarned");
      if (watchwarned) {
        if (config.getBooleanProp("watchwarnedpermButton")) {
          if (!config.watchlistModel.contains(elem))
            config.watchlistModel.addElement(elem);
        } else {
          if (!config.tempwatchlistModel.contains(elem))
            config.tempwatchlistModel.addElement(elem);
        }
      }

      int tmp = data.editstableModel.getRowCount();
      Object sectime = rowObject[config.sectimecol];

      for (i = tmp - 1; i >= 0; i--) {
        if (sectime.equals(data.editstableModel
            .getValueAt(i, config.sectimecol)))
          data.editstableModel.setValueAt(new WarningIndicator("!", null),i, config.xcol);

        if (watchwarned) {
          if (rowObject[config.projectcol].equals(data.editstableModel.getValueAt(i, config.projectcol))
              && rowObject[config.articlecol].equals(data.editstableModel.getValueAt(i, config.articlecol))) {
            ListIndicator li = (ListIndicator)data.editstableModel.getValueAt(i, config.listcol);
            if (config.getBooleanProp("watchwarnedpermButton"))
              data.editstableModel.setValueAt(li.addList(ListIndicator.WATCH),i, config.listcol);
            else
              data.editstableModel.setValueAt(li.addList(ListIndicator.TWATCH),i, config.listcol);
          }
        }
      }
    }
    SwingUtilities.invokeLater(repainter);
  }

  private void sendWarn(Object[] rowObject) {
    ircB.sendMsg((String) rowObject[config.projectcol], "Checked Warning "
        + rowObject[config.projectcol] + " [[" + rowObject[config.articlecol]
        + "]] " + rowObject[config.urlcol]);
  }

  private void warnEditAndRemove(int row, Object[] rowObject) {
    sendWarn(rowObject);
    synchronized (config.editstableTable.getTreeLock()) {
      if (row < data.editstableModel.getRowCount()
          && data.editstableModel.getValueAt(row, config.sectimecol).equals(
              rowObject[config.sectimecol])) {
        data.editstableModel.removeRow(row);
      } else {
        int tmp = data.editstableModel.getRowCount();
        Object sectime = rowObject[config.sectimecol];
        for (i = tmp - 1; i >= 0; i--)
          if (sectime.equals(data.editstableModel.getValueAt(i,
              config.sectimecol))) {
            data.editstableModel.removeRow(tmp);
            return;
          }
      }
    }
    SwingUtilities.invokeLater(repainter);
  }

  private void addRemoveFromGreylist(String userName, String projectName) {
    String user = userName + "#" + projectName;
    if (config.greylistModel.contains(user)) {
      config.greylistModel.removeElement(user);
    } else {
      if (config.whitelistModel.contains(user))
        config.whitelistModel.removeElement(user);
      if (config.blacklistModel.contains(user))
        config.blacklistModel.removeElement(user);
      config.greylistModel.addElement(user);

      synchronized (config.editstableTable.getTreeLock()) {
        int tmp = data.editstableModel.getRowCount();
        for (i = tmp - 1; i >= 0; i--)
          if (userName.equals(data.editstableModel.getValueAt(i,
              config.editorcol))
              && projectName.equals(data.editstableModel.getValueAt(i,
                  config.projectcol)))
            data.editstableModel.removeRow(i);
      }
    }
  }

  private void addRemoveFromBlacklist(String userName, String projectName) {
    String user = userName + "#" + projectName;
    boolean isInBlacklist;
    if (config.blacklistModel.contains(user)) {
      config.blacklistModel.removeElement(user);
      isInBlacklist = false;
    } else {
      if (config.whitelistModel.contains(user))
        config.whitelistModel.removeElement(user);
      if (config.greylistModel.contains(user))
        config.greylistModel.removeElement(user);
      config.blacklistModel.addElement(user);
      isInBlacklist = true;
    }
    synchronized (config.editstableTable.getTreeLock()) {
      int tmp = data.editstableModel.getRowCount();
      for (i = tmp - 1; i >= 0; i--)
        if (userName.equals(data.editstableModel.getValueAt(i,
            config.editorcol))
            && projectName.equals(data.editstableModel.getValueAt(i,
                config.projectcol))) {
          ListIndicator li = (ListIndicator)data.editstableModel.getValueAt(i, config.listcol);
          li = isInBlacklist?li.addList(ListIndicator.BLACK):li.removeList(ListIndicator.BLACK);
          data.editstableModel.setValueAt(li, i, config.listcol);
        }
    }
    SwingUtilities.invokeLater(repainter);
  }

  private void addRemoveFromWhitelist(String userName, String projectName) {
    String user = userName + "#" + projectName;
    if (config.whitelistModel.contains(user)) {
      config.whitelistModel.removeElement(user);
    } else {
      if (config.blacklistModel.contains(user))
        config.blacklistModel.removeElement(user);
      if (config.greylistModel.contains(user))
        config.greylistModel.removeElement(user);
      config.whitelistModel.addElement(user);

      synchronized (config.editstableTable.getTreeLock()) {
        int tmp = data.editstableModel.getRowCount();
        for (i = tmp - 1; i >= 0; i--)
          if (userName.equals(data.editstableModel.getValueAt(i,
              config.editorcol))
              && projectName.equals(data.editstableModel.getValueAt(i,
                  config.projectcol)))
            data.editstableModel.removeRow(i);
      }
    }
  }

  private void addRemoveFromTempWhitelist(String userName, String projectName) {
    String user = userName + "#" + projectName;
    if (config.tempwhitelistModel.contains(user)) {
      config.tempwhitelistModel.removeElement(user);
    } else {
      if (config.tempblacklistModel.contains(user))
        config.tempblacklistModel.removeElement(user);
      config.tempwhitelistModel.addElement(user);

      synchronized (config.editstableTable.getTreeLock()) {
        int tmp = data.editstableModel.getRowCount();
        for (i = tmp - 1; i >= 0; i--)
          if (userName.equals(data.editstableModel.getValueAt(i,
              config.editorcol))
              && projectName.equals(data.editstableModel.getValueAt(i,
                  config.projectcol)))
            data.editstableModel.removeRow(i);
      }
    }
  }

  private void addRemoveFromTempBlacklist(String userName, String projectName) {
    String user = userName + "#" + projectName;
    boolean isInBlacklist;
    if (config.tempblacklistModel.contains(user)) {
      config.tempblacklistModel.removeElement(user);
      isInBlacklist = false;
    } else {
      if (config.tempwhitelistModel.contains(user))
        config.tempwhitelistModel.removeElement(user);
      config.tempblacklistModel.addElement(user);
      isInBlacklist = true;
    }

    synchronized (config.editstableTable.getTreeLock()) {
      int tmp = data.editstableModel.getRowCount();
      for (i = tmp - 1; i >= 0; i--)
        if (userName.equals(data.editstableModel.getValueAt(i,
            config.editorcol))
            && projectName.equals(data.editstableModel.getValueAt(i,
                config.projectcol))) {
          ListIndicator li = (ListIndicator)data.editstableModel.getValueAt(i, config.listcol);
          li = isInBlacklist?li.addList(ListIndicator.TBLACK):li.removeList(ListIndicator.TBLACK);
          data.editstableModel.setValueAt(li, i, config.listcol);
        }
    }

    SwingUtilities.invokeLater(repainter);
  }

  private void addRemoveFromWatchlist(String pageName, String projectName) {
    String page = pageName + "#" + projectName;
    boolean isInWatchlist;
    if (config.watchlistModel.contains(page)) {
      config.watchlistModel.removeElement(page);
      isInWatchlist = false;
    } else {
      config.watchlistModel.addElement(page);
      isInWatchlist = true;
    }

    synchronized (config.editstableTable.getTreeLock()) {
      int tmp = data.editstableModel.getRowCount();
      for (i = tmp - 1; i >= 0; i--)
        if (pageName.equals(data.editstableModel.getValueAt(i,
            config.articlecol))
            && projectName.equals(data.editstableModel.getValueAt(i,
                config.projectcol))) {
          ListIndicator li = (ListIndicator)data.editstableModel.getValueAt(i, config.listcol);
          li = isInWatchlist?li.addList(ListIndicator.WATCH):li.removeList(ListIndicator.WATCH);
          data.editstableModel.setValueAt(li, i, config.listcol);
        }
    }
    SwingUtilities.invokeLater(repainter);
  }

  private void addRemoveFromTempWatchlist(String pageName, String projectName) {
    String page = pageName + "#" + projectName;
    boolean isInWatchlist;
    if (config.tempwatchlistModel.contains(page)) {
      config.tempwatchlistModel.removeElement(page);
      isInWatchlist = false;
    } else {
      config.tempwatchlistModel.addElement(page);
      isInWatchlist = true;
    }

    synchronized (config.editstableTable.getTreeLock()) {
      int tmp = data.editstableModel.getRowCount();
      for (i = tmp - 1; i >= 0; i--)
        if (pageName.equals(data.editstableModel.getValueAt(i,
            config.articlecol))
            && projectName.equals(data.editstableModel.getValueAt(i,
                config.projectcol))) {
          ListIndicator li = (ListIndicator)data.editstableModel.getValueAt(i, config.listcol);
          li = isInWatchlist?li.addList(ListIndicator.TWATCH):li.removeList(ListIndicator.TWATCH);
          data.editstableModel.setValueAt(li, i, config.listcol);
        }
    }
    SwingUtilities.invokeLater(repainter);
  }

  private void showPageDiff(int row, Object[] rowObject) {
    openBrowser((String) rowObject[config.urlcol]);
    if (config.getBooleanProp("removereviewed"))
      synchronized (config.editstableTable.getTreeLock()) {
        if (row < data.editstableModel.getRowCount()
            && data.editstableModel.getValueAt(row, config.sectimecol).equals(
                rowObject[config.sectimecol])) {
          data.editstableModel.removeRow(row);
        } else {
          int tmp = data.editstableModel.getRowCount();
          Object sectime = rowObject[config.sectimecol];
          for (i = tmp - 1; i >= 0; i--)
            if (sectime.equals(data.editstableModel.getValueAt(i,
                config.sectimecol))) {
              data.editstableModel.removeRow(tmp);
              return;
            }
        }
      }
  }

  private void showPageHistory(String page, String project) {
    try {
      String myPage = urlEncode(page);

      openBrowser("http://" + project + ".org/w/index.php?title=" + myPage
          + "&action=history");
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void showActualPage(String page, String project) {
    try {
      String myPage = urlEncode(page);

      openBrowser("http://" + project + ".org/wiki/" + myPage);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void showSpecialPageLog(Object[] row) {
    String project = (String)row[config.projectcol];
    String subject = (String)row[config.subjectcol];
    String user = (String)row[config.editorcol];
    Short special = (Short)row[config.specialcol];

    String url = "http://" + project + ".org/w/index.php?title=Special%3ALog&type=";
    String type = "";
    if (special == Edit.SPECIAL_UPLOAD) {
      type = "upload";
    } else if (special == Edit.SPECIAL_MOVE) {
      type = "move";
    } else if (special == Edit.SPECIAL_UNDELETE || special == Edit.SPECIAL_DELETE) {
      type = "delete";
    } else if (special == Edit.SPECIAL_BLOCK || special == Edit.SPECIAL_UNBLOCK ) {
      type = "block";
    } else if (special == Edit.SPECIAL_PROTECT || special == Edit.SPECIAL_UNPROTECT || special == Edit.SPECIAL_MODIFY_PROTECT ) {
      type = "protect";
    }
    url = url + type + "&user=" + urlEncode(user) + "&page=" + urlEncode(subject);
    try {
      openBrowser(url);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void updateUserPopupMenu() {
    String userName = (String) popupRow[config.editorcol];
    String projectName = (String) popupRow[config.projectcol];
    String user = userName + "#" + projectName;
    userNamePopupLabel.setText(user);
    // userNamePopupItem.hide();
    user2WhitelistPopupItem
        .setText(config.whitelistModel.contains(user) ? messages
            .getString("UserDelWhitelistMenu") : messages
            .getString("User2WhitelistMenu"));
    user2GreylistPopupItem
        .setText(config.greylistModel.contains(user) ? messages
            .getString("UserDelGreylistMenu") : messages
            .getString("User2GreylistMenu"));
    user2BlacklistPopupItem
        .setText(config.blacklistModel.contains(user) ? messages
            .getString("UserDelBlacklistMenu") : messages
            .getString("User2BlacklistMenu"));
//    user2WatchlistPopupItem
//        .setText(config.watchListModel.contains(user) ? messages
//            .getString("UserDelWatchlistMenu") : messages
//            .getString("User2WatchlistMenu"));
    user2TempWhitelistPopupItem.setText(config.tempwhitelistModel
        .contains(user) ? messages.getString("UserDelTempWhitelistMenu")
        : messages.getString("User2TempWhitelistMenu"));
    user2TempBlacklistPopupItem.setText(config.tempblacklistModel
        .contains(user) ? messages.getString("UserDelTempBlacklistMenu")
        : messages.getString("User2TempBlacklistMenu"));

//    user2TempWatchlistPopupItem.setText(config.tempwatchlistModel
//        .contains(user) ? messages.getString("UserDelTempWatchlistMenu")
//        : messages.getString("User2TempWatchlistMenu"));
  }

  private void updateProjectPopupMenu() {
    String projectName = (String) popupRow[config.projectcol];
    projectNamePopupLabel.setText(projectName);
  }

  private void updateXPopupMenu() {
    String dateStr = (String) popupRow[config.timecol];
    String articleStr = (String) popupRow[config.articlecol];
    xNamePopupLabel.setText(dateStr + ": " + articleStr);
  }

  private void updatePagePopupMenu() {
    String article = popupRow[config.articlecol] + "#"
        + popupRow[config.projectcol];
    pageNamePopupLabel.setText(article);
    page2WatchlistPopupItem
        .setText(config.watchlistModel.contains(article) ? messages
            .getString("PageDelWatchlistMenu") : messages
            .getString("Page2WatchlistMenu"));
    page2TempWatchlistPopupItem.setText(config.tempwatchlistModel
        .contains(article) ? messages.getString("PageDelTempWatchlistMenu")
        : messages.getString("Page2TempWatchlistMenu"));
  }

  private void updateSpecialPagePopupMenu() {
    Short special = (Short)popupRow[config.specialcol];
    String article = popupRow[config.articlecol] + "#"
        + popupRow[config.projectcol];
    specialPageNamePopupLabel.setText(article);

    if (special == Edit.SPECIAL_BLOCK || special == Edit.SPECIAL_UNBLOCK
        || special == Edit.SPECIAL_NEWUSER) {
      specialPageActionPopupItem
        .setText(messages.getString("SpecialPageContributionsPopupMenu"));
    } else if (special == Edit.SPECIAL_UPLOAD || special == Edit.SPECIAL_MOVE) {
      specialPageActionPopupItem
        .setText(messages.getString("SpecialPagePagePopupMenu"));
    } else if (special == Edit.SPECIAL_PROTECT || special == Edit.SPECIAL_UNPROTECT || special == Edit.SPECIAL_MODIFY_PROTECT) {
      specialPageActionPopupItem
        .setText(messages.getString("SpecialPageHistoryPopupMenu"));
    } else if (special == Edit.SPECIAL_DELETE) {
      specialPageActionPopupItem
        .setText(messages.getString("SpecialPageLogMenu"));
    }
  }

  private void updateSpecialPage2PopupMenu() {
    Short special = (Short)popupRow[config.specialcol];
    String article = popupRow[config.articlecol] + "#"
        + popupRow[config.projectcol];
    specialPage2NamePopupLabel.setText(article);
  }

  private void hackColorChooser() {
    hackColorChooserValue("ColorChooser.cancelText");
    hackColorChooserValue("ColorChooser.hsbBlueText");
    hackColorChooserValue("ColorChooser.hsbBrightnessText");
    hackColorChooserValue("ColorChooser.hsbGreenText");
    hackColorChooserValue("ColorChooser.hsbHueText");
    hackColorChooserValue("ColorChooser.hsbNameText");
    hackColorChooserValue("ColorChooser.hsbRedText");
    hackColorChooserValue("ColorChooser.hsbSaturationText");
    hackColorChooserValue("ColorChooser.okText");
    hackColorChooserValue("ColorChooser.previewText");
    hackColorChooserValue("ColorChooser.resetText");
    hackColorChooserValue("ColorChooser.rgbBlueText");
    hackColorChooserValue("ColorChooser.rgbGreenText");
    hackColorChooserValue("ColorChooser.rgbRedText");
    hackColorChooserValue("ColorChooser.swatchesNameText");
    hackColorChooserValue("ColorChooser.swatchesRecentText");
  }

  private void hackColorChooserValue(String s) {
    UIManager.put(s, messages.getString(s));
  }

  protected boolean isAvailableLookAndFeel(String laf) {
    try {
      Class lnfClass = Class.forName(laf);
      LookAndFeel newLAF = (LookAndFeel) (lnfClass.newInstance());
      return newLAF.isSupportedLookAndFeel();
    } catch (Exception e) { // If ANYTHING weird happens, return false
      return false;
    }
  }

  private static String urlEncode(String value) {
    value = value.replace(' ', '_');
    try {
      value = URLEncoder.encode(value, "UTF-8");
    } catch (UnsupportedEncodingException ex) {
    }
    return value;
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.ButtonGroup BColumnGroup;

  private javax.swing.JLabel BColumnTakesYouTo;

  private javax.swing.JLabel DefaultProject;

  private javax.swing.JPanel IRCPannel;

  private ConnectionsPanel connectionsPanel;

  private javax.swing.JButton VandalismStreamButton;

  private javax.swing.JPanel aboutPanel;

  private javax.swing.JCheckBox autoscroll;

  private javax.swing.JCheckBox blackdeleted;

  private javax.swing.ButtonGroup blackdeletedGroup;

  private javax.swing.JButton blacklistAdd;

  private javax.swing.JButton greylistAdd;

  private javax.swing.JList blacklistList;

  private javax.swing.JButton blacklistRemove;

  private javax.swing.JButton greylistRemove;

  private javax.swing.JCheckBox blackreverted;

  private javax.swing.ButtonGroup blackrevertedGroup;

  private javax.swing.JRadioButton blockButton;

  private javax.swing.JPanel blockRadioPanel;

  private javax.swing.JPanel browser2;

  private javax.swing.JTextField browserField;

  private javax.swing.JButton browserchooserButton;

  private javax.swing.JCheckBox browserpick;

  private javax.swing.JPanel buttons;

  private javax.swing.JTextField changedField;

  private javax.swing.JTextField channelField;

  private javax.swing.JCheckBox colorblist;

  private javax.swing.JButton colorblistButton;

  private javax.swing.JCheckBox colorchanged;

  private javax.swing.JButton colorchangedButton;

  private javax.swing.JCheckBox colorips;

  private javax.swing.JCheckBox colornewusers;

  private javax.swing.JButton coloripsButton;

  private javax.swing.JButton colornewusersButton;

  private javax.swing.JCheckBox colormoves;

  private javax.swing.JButton colormovesButton;

  private javax.swing.JCheckBox colornew;

  private javax.swing.JButton colornewButton;

  private javax.swing.JCheckBox coloruserpage;

  private javax.swing.JButton coloruserpageButton;

  private javax.swing.JCheckBox colorwatch;

  private javax.swing.JButton colorwatchButton;

  private javax.swing.JButton configCancel;

  private javax.swing.JPanel configCtrlsPanel;

  private javax.swing.JButton configDefaults;

  private javax.swing.JSplitPane configSplitPane;

  private javax.swing.JPanel miscConfigPanel;

  private JLabel miscConfigLabel;

  private javax.swing.JPanel configPanel1;

  private javax.swing.JButton configSave;

  private javax.swing.JTextField defaultProjectField;

  private javax.swing.JRadioButton delpagepermButton;

  private javax.swing.JRadioButton delpagetempButton;

  private javax.swing.JButton exportButton1;

  private javax.swing.JTextField exportField1;

  private javax.swing.JButton exportchooserButton1;

  private javax.swing.JButton importButton;

  private javax.swing.JTextField importField;

  private javax.swing.JButton importadminsButton;

  private javax.swing.JButton importbotsButton;

  private javax.swing.JButton importchooserButton;

  private javax.swing.JButton ircconButton;

  private javax.swing.JButton ircdisconnectButton;

  private javax.swing.JPanel irctoptopPanel1;

  private javax.swing.JLabel jLabel2;

  private javax.swing.JLabel jLabel21;

  private javax.swing.JLabel jLabel22;

  private javax.swing.JLabel jLabel23;

  private javax.swing.JLabel jLabel24;

  private javax.swing.JLabel jLabel25;

  private javax.swing.JLabel jLabel26;

  private javax.swing.JLabel jLabel27;

  private javax.swing.JLabel jLabel28;

  private javax.swing.JLabel jLabel29;

  private javax.swing.JLabel jLabel30;

  private javax.swing.JLabel themeLabel;

  private javax.swing.JLabel jLabel3;

  private javax.swing.JLabel jLabel4;

  private javax.swing.JLabel jLabel5;

  private javax.swing.JLabel jLabel6;

  private javax.swing.JLabel jLabel7;

  private javax.swing.JLabel jLabel8;

  private javax.swing.JLabel jLabel9;

  private javax.swing.JLabel jLabel10;

  private javax.swing.JLabel jLabel11;

  private javax.swing.JLabel jLabel12;

  private javax.swing.JLabel jLabel13;

  private javax.swing.JLabel jLabel14;

  private javax.swing.JLabel jLabel15;

  private JEditorPane editorPane1;

  private javax.swing.JPanel jPanel1;

  private javax.swing.JPanel jPanel10;

  private javax.swing.JPanel jPanel11;

  private javax.swing.JPanel jPanel12;

  private javax.swing.JPanel jPanel13;

  private javax.swing.JPanel jPanel14;

  private javax.swing.JPanel jPanel15;

  private javax.swing.JPanel jPanel16;

  private javax.swing.JPanel jPanel17;

  private javax.swing.JPanel localizeConfigPanel;

  private JLabel localizeConfigLabel;

  private javax.swing.JPanel jPanel19;

  private javax.swing.JPanel jPanel2;

  private javax.swing.JPanel jPanel20;

  private javax.swing.JPanel jPanel21;

  private javax.swing.JPanel jPanel22;

  private javax.swing.JPanel jPanel23;

  private javax.swing.JPanel jPanel24;

  private javax.swing.JPanel jPanel25;

  private javax.swing.JPanel jPanel26;

  private javax.swing.JPanel jPanel27;

  private javax.swing.JPanel jPanel3;

  private javax.swing.JPanel jPanel31;

  private javax.swing.JPanel jPanel32;

  private javax.swing.JPanel jPanel4;

  private javax.swing.JPanel jPanel5;

  private javax.swing.JPanel jPanel6;

  private javax.swing.JPanel jPanel7;

  private javax.swing.JPanel jPanel78;

  private javax.swing.JPanel jPanel8;

  private javax.swing.JPanel jPanel9;

  private javax.swing.JPanel jPanel40;

  private javax.swing.JPanel jPanel41;

  private javax.swing.JScrollPane jScrollPane1;

  private javax.swing.JScrollPane jScrollPane10;

  private javax.swing.JScrollPane jScrollPane2;

  private javax.swing.JScrollPane jScrollPane3;

  private javax.swing.JScrollPane jScrollPane4;

  private javax.swing.JScrollPane jScrollPane5;

  private javax.swing.JScrollPane jScrollPane6;

  private javax.swing.JScrollPane jScrollPane7;

  private javax.swing.JScrollPane jScrollPane8;

  private javax.swing.JScrollPane jScrollPane11;

  private javax.swing.JSeparator jSeparator1;

  private javax.swing.JSeparator jSeparator2;

  private javax.swing.JSeparator jSeparator3;

  private javax.swing.JSeparator jSeparator4;

  private javax.swing.JSeparator jSeparator5;

  private javax.swing.JSeparator jSeparator6;

  private javax.swing.JSeparator jSeparator7;

  private javax.swing.JTextPane jTextPane1;

  private javax.swing.JComboBox langList;

  private javax.swing.JComboBox timeZoneList;

  private javax.swing.JComboBox feelList;

  private javax.swing.JComboBox themeList;

  private javax.swing.JCheckBox listprecedence;

  private javax.swing.JPanel lists;

  private javax.swing.JCheckBox mediawikistreamCheckbox;

  private javax.swing.JCheckBox newpages;

  private javax.swing.JCheckBox newversion;

  private javax.swing.JCheckBox connectatstart;

  private javax.swing.JCheckBox olddeleted;

  private javax.swing.JCheckBox registerdeleted;

  private javax.swing.JTextField portField;

  private javax.swing.JCheckBox queueedits;

  private javax.swing.JCheckBox rblackpage;

  private javax.swing.JCheckBox rblacksummary;

  private javax.swing.JCheckBox rblackuser;

  private javax.swing.JPanel regexPanel;

  private javax.swing.JTextField regexpField;

  private javax.swing.JButton regexpblackAdd;

  private javax.swing.JList regexpblackList;

  private javax.swing.JButton regexpblackRemove;

  private javax.swing.JButton regexpwhiteAdd;

  private javax.swing.JList regexpwhiteList;

  private javax.swing.JButton regexpwhiteRemove;

  private javax.swing.JCheckBox removereviewed;

  private javax.swing.JCheckBox reversetable;

  private javax.swing.JPanel revertedUsers;

  private javax.swing.JRadioButton revertpermButton;

  private javax.swing.JRadioButton reverttempButton;

  private javax.swing.JCheckBox risk;

  private javax.swing.JCheckBox rwhitepage;

  private javax.swing.JCheckBox rwhitesummary;

  private javax.swing.JCheckBox rwhiteuser;

  private javax.swing.JTextField serverField;

  private javax.swing.JCheckBox showips;

  private javax.swing.JCheckBox showwatch;

  private javax.swing.JCheckBox singleclick;

  private javax.swing.JCheckBox beeptempblack;

  private javax.swing.JCheckBox beeppermblack;

  private javax.swing.JCheckBox beeptempwatch;

  private javax.swing.JCheckBox beeppermwatch;

  private javax.swing.JCheckBox beepregexpblack;

  private javax.swing.JCheckBox beepcollaborationwarning;

  private javax.swing.JButton sortarticlesButton;

  private javax.swing.JButton sortblacklistButton;

  private javax.swing.JButton sortgreylistButton;

  private javax.swing.JButton sortregexpblacklistButton;

  private javax.swing.JButton sortregexpwhitelistButton;

  private javax.swing.JButton sorttempblacklistButton;

  private javax.swing.JButton sorttempwatchlistButton;

  private javax.swing.JButton sorttempwhitelistButton;

  private javax.swing.JButton sortwhitelistButton;

  private javax.swing.JPanel speediedUsers;

  private javax.swing.JLabel statusLabel;

  private javax.swing.JCheckBox stripformatting;

  private javax.swing.JTabbedPane tab;

  private javax.swing.JButton tempblacklistAdd;

  private javax.swing.JList tempblacklistList;

  private javax.swing.JButton tempblacklistRemove;

  private javax.swing.JTextField templistField;

  private javax.swing.JPanel temporaryPanel;

  private javax.swing.JButton tempwatchlistAdd;

  private javax.swing.JList tempwatchlistList;

  private javax.swing.JButton tempwatchlistRemove;

  private javax.swing.JButton tempwhitelistAdd;

  private javax.swing.JList tempwhitelistList;

  private javax.swing.JButton tempwhitelistRemove;

  private javax.swing.JPanel userListPanel;

  private javax.swing.JPanel watchlistPanel;

  private javax.swing.JPanel watchlists;

  private javax.swing.JPanel watchlistButtons;

  private javax.swing.JTextField userlistField;

  private javax.swing.JPanel userlistPanel2;

  private javax.swing.JCheckBox vandalism_en_box;

  private javax.swing.JCheckBox vandalism_fr_box;

  private javax.swing.JCheckBox vandalism_it_box;

  private javax.swing.JCheckBox vandalism_nl_box;

  private javax.swing.JCheckBox vandalism_cs_box;

  private javax.swing.JRadioButton vipButton;

  private javax.swing.JRadioButton vipeditButton;

  private javax.swing.JCheckBox watchdeleted;

  private javax.swing.JCheckBox watchwarned;

  private javax.swing.ButtonGroup watchdeletedGroup;

  private javax.swing.ButtonGroup watchwarnedGroup;

  private javax.swing.JRadioButton watchdelpermButton;

  private javax.swing.JRadioButton watchwarnedpermButton;

  private javax.swing.JRadioButton watchdeltempButton;

  private javax.swing.JRadioButton watchwarnedtempButton;

  private javax.swing.JButton watchlistAdd;

  private javax.swing.JList watchlistList;

  private javax.swing.JTextField articleField;

  private javax.swing.JButton watchlistRemove;

  private javax.swing.JButton watchlistimportButton;

  private javax.swing.JCheckBox watchmasschanges;

  private javax.swing.JCheckBox watchnewpages;

  private javax.swing.JCheckBox watchpagemoves;

  private javax.swing.JPanel watchspeediedArticles;

  private javax.swing.JPanel watchwarnedPanel;

  private javax.swing.JCheckBox watchuserpages;

  private javax.swing.JButton whitelistAdd;

  private javax.swing.JList whitelistList;

  private javax.swing.JButton whitelistRemove;

  private javax.swing.JTextField whitelistimportField;

  private javax.swing.JList greylistList;

  private javax.swing.JList configList;

  private javax.swing.JTree configTree;

  private javax.swing.JScrollPane configListScrollPane;

  private javax.swing.JPanel configCardPanel;

  private javax.swing.JPanel colorConfigPanel;

  private JLabel colorConfigLabel;

  private javax.swing.JPanel regexpConfigPanel;

  private JLabel regexpConfigLabel;

  private javax.swing.JPanel listsConfigPanel;

  private JLabel listsConfigLabel;

  private javax.swing.JPanel automaticConfigPanel;

  private JLabel automaticConfigLabel;

  private javax.swing.JPanel showConfigPanel;

  private JLabel showConfigLabel;

  private javax.swing.JPanel beepConfigPanel;

  private JLabel beepConfigLabel;

  private JPanel startConfigPanel;

  private JLabel startConfigLabel;

  private JPanel browserConfigPanel;

  private JLabel browserConfigLabel;

  // End of variables declaration//GEN-END:variables

}
