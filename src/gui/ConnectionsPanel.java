/*
 ConnectionsPanel - UI class for CryptoDerk's Vandal Fighter
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
 * Original code was written on 5-dec-2006
 * by Beren
 * http://cs.wikipedia.org/wiki/User:Beren
 */

package gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import data.Configuration;
import data.Data;


public class ConnectionsPanel extends JPanel {

  private static final long serialVersionUID = -2884906663896766230L;

  private static abstract class TreeData extends DefaultMutableTreeNode {

    public TreeData() {
      super();
    }
    
    public TreeData(Object userObject) {
      super(userObject);
    }
    
    public abstract String getId();
    public abstract String getLabel();
    public String toString() {
      return getLabel();
    }
    
    private JPanel panel;
    public JPanel getPanel() { return panel; }
    public void setPanel(JPanel panel) { this.panel = panel; } 
    
    protected boolean active = false;
    public boolean isActive() {
      return active;
    }
    public void setActive(boolean active) {
      this.active = active;
    }
  }
  
  private static class Top extends TreeData {
    public static final Top TOP = new Top();
    public String getId() {
      return "servers";
    }
    
    String label;
    public void setLabel(String label) {
      this.label = label;
    }
    public String getLabel() {
      return label;
    }    
    
    public boolean isActive() {
      return true;
    }
  }
  
  public static class IrcServer extends TreeData {
    String serverNick;
    String serverName;
    String serverPort;
    String userNamePolicy;
    String user;
    String password;
    boolean connected = false;
    String channelId;

    public IrcServer(String serverNick, String serverName, String serverPort,
        String userNamePolicy, String user, String password) {
      super(serverNick);
      this.serverNick = serverNick;
      this.serverName = serverName;
      this.serverPort = serverPort;
      this.userNamePolicy = userNamePolicy;
      this.user = user;
      this.password = password;
      channelId = serverNick;
    }
    
    public String getId() {
      return channelId;
    }
    
    public String getLabel() {
      return getUserObject().toString();
    }
  }
  
  public static class IrcChannel extends TreeData {
    IrcServer server;
    String channelNick;
    String channelName;
    String channelProject;
    String channelParser;
    String channelSender;
    String channelId;
    
    public IrcChannel(IrcServer server, String channelNick, String channelName, String channelProject, String channelParser, String channelSender) {
      super(channelName);
      this.server = server;
      this.channelNick = channelNick;
      this.channelName = channelName;
      this.channelProject = channelProject;
      this.channelParser = channelParser;
      this.channelSender = channelSender;
      this.channelId = server.serverNick+"."+channelNick;
    }
    
    public String getId() {
      return channelId;
    }
    
    public String getLabel() {
      return getUserObject().toString();
    }
  }
  
  private static class MyTreeCellRenderer extends DefaultTreeCellRenderer {
    Icon serverIcon;
    Icon serverGrayIcon;
    Icon channelIcon;
    Icon channelGrayIcon;
    //Color defaultTextSelectionColor = null;
    Color defaultTextNonSelectionColor = null;
    
    public MyTreeCellRenderer(Icon serverIcon, Icon serverGrayIcon, Icon channelIcon, Icon channelGrayIcon)  {
      this.serverIcon = serverIcon;
      this.serverGrayIcon = serverGrayIcon;
      this.channelIcon = channelIcon;
      this.channelGrayIcon = channelGrayIcon;
    }
    
    public Component getTreeCellRendererComponent(JTree tree, Object value,
        boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

      //if (defaultTextSelectionColor == null)
      //  defaultTextSelectionColor = getTextSelectionColor();
      if (defaultTextNonSelectionColor == null)
        defaultTextNonSelectionColor = getTextNonSelectionColor();

      //setTextSelectionColor(defaultTextSelectionColor);
      setTextNonSelectionColor(defaultTextNonSelectionColor);
      if (value instanceof IrcServer) {
        IrcServer server = (IrcServer)value;
        if (!server.isActive())
          setTextNonSelectionColor(Color.gray);
      } else if (value instanceof IrcChannel) {
        IrcChannel channel = (IrcChannel)value;
        boolean active = channel.server.isActive() && channel.isActive();
        if (!active)
          setTextNonSelectionColor(Color.gray);
      }
      
      super.getTreeCellRendererComponent(tree, value, sel, expanded,
          leaf, row, hasFocus);
     
      if (value instanceof IrcServer) {
        IrcServer server = (IrcServer)value;
        setIcon(server.isActive()?serverIcon:serverGrayIcon);
      } else if (value instanceof IrcChannel) {
        IrcChannel channel = (IrcChannel)value;
        boolean active = channel.server.isActive() && channel.isActive();
        setIcon(active?channelIcon:channelGrayIcon);
      }

      return this;
    }
  }
  
  private static class MyTreeCellEditor extends DefaultTreeCellEditor {
    Icon serverIcon;
    Icon channelIcon;
    
    public MyTreeCellEditor(JTree tree, DefaultTreeCellRenderer renderer, Icon serverIcon, Icon channelIcon)  {
      super(tree, renderer);
      this.serverIcon = serverIcon;
      this.channelIcon = channelIcon;
    }
    
    public Component getTreeCellEditorComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row) {
      
      Component component = super.getTreeCellEditorComponent(tree, value, sel, expanded,
          leaf, row);
     
      if (value instanceof IrcServer) {
        editingIcon = serverIcon;
      } else if (value instanceof IrcChannel) {
        editingIcon = channelIcon;
      }

      return component;
    }
  }
  
  Configuration config;
  Data data;
  ResourceBundle messages;
  Vf vf;
  
  private DefaultTreeModel treeModel;
  private DefaultMutableTreeNode top;
  private JPanel cardPanel;
  private JPanel serverPanel;
  private JScrollPane treeScrollPane;
  private JSplitPane splitPane;
  private JTree tree;
  private TreeData popupTreeData;
  private JPopupMenu topPopupMenu, ircServerPopupMenu, ircChannelPopupMenu;
  private JMenuItem addServerPopupItem, addChannelPopupItem;
  private JMenuItem removeServerPopupItem, removeChannelPopupItem;
  
  public ConnectionsPanel(Vf vf) {
    super();
    
    this.vf = vf;
    config = Configuration.getConfigurationObject();
    data = Data.getDataObject();
    messages = ResourceBundle.getBundle("MessagesBundle", config.currentLocale);
    
    initComponents();
    buildComponentTree();
  }
  
  private void initComponents() {
    cardPanel = new JPanel();
    serverPanel = new JPanel();
    treeScrollPane = new JScrollPane();
    splitPane = new JSplitPane();
    top = Top.TOP;
    treeModel = new DefaultTreeModel(top, false);
    tree = new JTree();
    topPopupMenu = new JPopupMenu();
    ircServerPopupMenu = new JPopupMenu();
    ircChannelPopupMenu = new JPopupMenu();
    addServerPopupItem = new JMenuItem();
    addChannelPopupItem = new JMenuItem();
    removeServerPopupItem = new JMenuItem();
    removeChannelPopupItem = new JMenuItem();
  }
  
  private void buildComponentTree() {
    setLayout(new BorderLayout());
    add(splitPane, BorderLayout.CENTER);
    
    splitPane.setLeftComponent(treeScrollPane);
    splitPane.setRightComponent(cardPanel);
    splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
    splitPane.setDividerLocation(200);
    
    treeScrollPane.setViewportView(tree);
    
    tree.setModel(treeModel);
    tree.getSelectionModel()
        .setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.setEditable(true);
    tree.addTreeSelectionListener(new TreeSelectionListener() {
      public void valueChanged(TreeSelectionEvent e) {
        TreeData node = (TreeData) tree.getLastSelectedPathComponent();

        if (node == null)
          return;

        CardLayout cl = (CardLayout) (cardPanel.getLayout());
        
        if (node instanceof IrcServer) {
          cl.show(cardPanel, node.getId());
        } else if (node instanceof IrcChannel) {
            cl.show(cardPanel, node.getId());
        } else {
          // TODO
          cl.show(cardPanel, Top.TOP.getId());
        }
      }
    });
    Icon serverIcon = new ImageIcon("images/IRCServer.png");
    Icon serverGrayIcon = new ImageIcon("images/IRCServerGray.png");
    Icon channelIcon = new ImageIcon("images/IRCChannel.png");
    Icon channelGrayIcon = new ImageIcon("images/IRCChannelGray.png");
    MyTreeCellRenderer renderer = new MyTreeCellRenderer(serverIcon, serverGrayIcon,
        channelIcon, channelGrayIcon);
    MyTreeCellEditor editor = new MyTreeCellEditor(tree, renderer, serverIcon, channelIcon);
    tree.setCellRenderer(renderer);
    tree.setCellEditor(editor);
    //tree.setRowHeight(15);
    
    tree.addMouseListener(new MouseAdapter()
    {
      public void mouseReleased(MouseEvent e)
      {
        if (e.isPopupTrigger())
        {
          int x = e.getX();
          int y = e.getY();
          TreePath path = tree.getPathForLocation(x, y);
          
          if (path != null)
          {
            popupTreeData = (TreeData)path.getLastPathComponent();
            if (popupTreeData != null) {
              if (popupTreeData instanceof Top)
                topPopupMenu.show(tree, x, y);
              else if (popupTreeData instanceof IrcServer)
                ircServerPopupMenu.show(tree, x, y);
              else if (popupTreeData instanceof IrcChannel)
                ircChannelPopupMenu.show(tree, x, y);
            }
          }
        }
      }
    });

    treeModel.addTreeModelListener(new TreeModelListener() {
      public void treeNodesChanged(TreeModelEvent e) {
          TreeData node = (TreeData)e.getTreePath().getLastPathComponent();

          /*
           * If the event lists children, then the changed
           * node is the child of the node we've already
           * gotten.  Otherwise, the changed node and the
           * specified node are the same.
           */
          try {
              int index = e.getChildIndices()[0];
              node = (TreeData)node.getChildAt(index);
          } catch (NullPointerException exc) {}

          //TODO otestovat, zda se to nezmenilo na neco, co uz existuje
          //TODO nahradit podivne 
          System.out.println("The user has finished editing the node.");
          System.out.println("New value: " + node.getUserObject());
          System.out.println("Id: " + node.getId());
      }
      public void treeNodesInserted(TreeModelEvent e) {
      }
      public void treeNodesRemoved(TreeModelEvent e) {
      }
      public void treeStructureChanged(TreeModelEvent e) {
      }
    });
    
    //tree.setSelectionPath(new TreePath(new Object[] { top, general,
    //    start }));
    
    cardPanel.setLayout(new CardLayout());
    cardPanel.add(new JScrollPane(serverPanel), Top.TOP.getId());
    
    topPopupMenu.add(addServerPopupItem);
    addServerPopupItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        IrcServer server = createDefaultServer();
        addServer(server);
        CardLayout cl = (CardLayout) (cardPanel.getLayout());
        cl.show(cardPanel, server.getId());
        tree.setSelectionPath(new TreePath(new Object[] { top, server}));
      }
    });
    
    ircServerPopupMenu.add(addChannelPopupItem);
    addChannelPopupItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        IrcServer server = (IrcServer)popupTreeData;
        IrcChannel channel = createDefaultChannel(server);
        addChannel(channel);
        CardLayout cl = (CardLayout) (cardPanel.getLayout());
        cl.show(cardPanel, channel.getId());
        tree.setSelectionPath(new TreePath(new Object[] { top, server, channel}));
      }
    });
    
    ircServerPopupMenu.add(removeServerPopupItem);
    removeServerPopupItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        IrcServer server = (IrcServer)popupTreeData;
        removeServer(server);
        CardLayout cl = (CardLayout) (cardPanel.getLayout());
        cl.show(cardPanel, Top.TOP.getId());
      }
    });
    
    ircChannelPopupMenu.add(removeChannelPopupItem);
    removeChannelPopupItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        IrcChannel channel = (IrcChannel)popupTreeData;
        
        removeChannel(channel);
        CardLayout cl = (CardLayout) (cardPanel.getLayout());
        cl.show(cardPanel, channel.server.getId());
      }
    });
    
    updateByConfiguration();
    localizeComponents();
  }
  
  private IrcServer createDefaultServer() {
    HashSet mySet = new HashSet();
    for (Enumeration en = top.children(); en.hasMoreElements();) {
      DefaultMutableTreeNode n1 = (DefaultMutableTreeNode)en.nextElement();
      IrcServer server = (IrcServer)n1;
      mySet.add(server.getId());
    }
    String defaultNick = "Server";
    String nick = defaultNick + "-1";
    int index = 2;
    while (mySet.contains(nick)) {
      nick = defaultNick + "-" + (index++);
    }
    IrcServer result = new IrcServer(nick, "", "", "", "", "");
    return result;
  }
  
  private IrcChannel createDefaultChannel(IrcServer server) {
    HashSet mySet = new HashSet();
    for (Enumeration en = server.children(); en.hasMoreElements();) {
      DefaultMutableTreeNode n1 = (DefaultMutableTreeNode)en.nextElement();
      IrcChannel channel = (IrcChannel)n1;
      mySet.add(channel.getLabel());
    }
    String defaultNick = "Channel";
    String nick = defaultNick + "-1";
    int index = 2;
    while (mySet.contains(nick)) {
      nick = defaultNick + "-" + (index++);
    }
    IrcChannel result = new IrcChannel(server, nick, nick, "", "", "");
    return result;
  }
  
  private void updateByConfiguration() {
    String[] servers = config.getArrayProp("servers");
    for (int i = 0; i < servers.length; i++) {
      String serverNick = servers[i];
      String serverType = config
      .getProperty("server." + serverNick + ".type");
      String serverName = config
      .getProperty("server." + serverNick + ".name");
      String serverPort = config
      .getProperty("server." + serverNick + ".port");
      String serverUserNamePolicy = config.getProperty("server." + serverNick
          + ".usernamepolicy");
      String serverUser = config
      .getProperty("server." + serverNick + ".user");
      String serverPassword = config.getProperty("server." + serverNick
          + ".password");
      boolean serverActive = config.getBooleanProp("server." + serverNick
          + ".active");
      
      String[] channels = config.getArrayProp(
          "server." + serverNick + ".channels");
      
      if ("irc".equals(serverType)) {
        IrcServer server = new IrcServer(serverNick, serverName, serverPort,
            serverUserNamePolicy, serverUser, serverPassword);
        server.active = serverActive;
        addServer(server);
        
        for (int j = 0; j < channels.length; j++) {
          String channelNick = channels[j];
          String channelName = config.getProperty("server." + serverNick
              + ".channel." + channelNick + ".name");
          if (channelName == null) 
            channelName = channelNick;
          String channelProject = config.getProperty("server." + serverNick
              + ".channel." + channelNick + ".project");
          // TODO - more parsers, more senders?
          String channelParser = config.getProperty("server." + serverNick
              + ".channel." + channelNick + ".parser");
          String channelSender = config.getProperty("server." + serverNick
              + ".channel." + channelNick + ".sender");
          
          addChannel(new IrcChannel(server, channelNick, channelName, channelProject,
              channelParser, channelSender));
        }
      }
    }
  }
  
  public void saveSettings() {
    synchronized (config.props) {
      // remove all previous data
      config.props.remove("servers");
      List keys = new ArrayList();
      for (Enumeration en = config.props.keys(); en.hasMoreElements();) {
        String key = (String)en.nextElement();
        if (key.startsWith("server.")) 
          keys.add(key);
      }
      for (Iterator it = keys.listIterator(); it.hasNext();) {
        String key = (String)it.next();
        config.props.remove(key);
      }
      
      // add current data
      StringBuffer serversBuffer = new StringBuffer();
      for (Enumeration en = top.children(); en.hasMoreElements();) {
        IrcServer server = (IrcServer)en.nextElement();
        String serverNick = server.serverNick;
        if (serversBuffer.length() != 0) 
          serversBuffer.append(" ");
        serversBuffer.append(serverNick);
        
        config.props.put("server." + serverNick + ".type", "irc");
        config.props.put("server." + serverNick + ".name", server.serverName);
        config.props.put("server." + serverNick + ".port", server.serverPort);
        config.props.put("server." + serverNick + ".usernamepolicy", server.userNamePolicy);
        config.props.put("server." + serverNick + ".user", server.user);
        config.props.put("server." + serverNick + ".password", server.password);
        config.props.put("server." + serverNick + ".active", server.active+"");
        
        StringBuffer channelsBuffer = new StringBuffer();
        for (Enumeration en2 = server.children(); en2.hasMoreElements();) {
          IrcChannel channel = (IrcChannel)en2.nextElement();
          String channelNick = channel.channelNick;
          if (channelsBuffer.length() != 0) 
            channelsBuffer.append(" ");
          channelsBuffer.append(channelNick);
          
          if (channel.channelName != null)
            config.props.put("server." + serverNick
                + ".channel." + channelNick + ".name", channel.getLabel());
          if (channel.channelProject != null)
            config.props.put("server." + serverNick
                + ".channel." + channelNick + ".project", channel.channelProject);
          if (channel.channelParser != null)
            config.props.put("server." + serverNick
                + ".channel." + channelNick + ".parser", channel.channelParser);
          if (channel.channelSender != null)
            config.props.put("server." + serverNick
                + ".channel." + channelNick + ".sender", channel.channelSender);
        }
        config.props.put("server." + serverNick + ".channels", channelsBuffer.toString());   
      }
      config.props.put("servers", serversBuffer.toString());
    }
  }
  
  private void addServer(IrcServer server) {
    if (getServer(server.serverNick) == null) {
      IRCServerPanel serverPanel = new IRCServerPanel(this, server);
      server.setPanel(serverPanel);
      cardPanel.add(new JScrollPane(serverPanel), server.getId());
      treeModel.insertNodeInto(server, top,
          top.getChildCount());
      tree.scrollPathToVisible(new TreePath(server.getPath()));
      config.setProperty("servers", config.getProperty("servers")+" "+server.serverNick);
      config.setProperty("server." + server.serverNick + ".type", "IRC");
      
    }
  }
  
  private void removeServer(IrcServer server) {
    JPanel serverPanel = server.getPanel();
    for (Enumeration en = server.children(); en.hasMoreElements();) {
      DefaultMutableTreeNode n1 = (DefaultMutableTreeNode)en.nextElement();
      if (n1 instanceof IrcChannel) {
        IrcChannel channel = (IrcChannel)n1;
        JPanel channelPanel = channel.getPanel();
        cardPanel.remove(channelPanel.getParent());
        treeModel.removeNodeFromParent(channel);
      }
    }
    cardPanel.remove(serverPanel.getParent());
    treeModel.removeNodeFromParent(server);
  }
  
  private void removeChannel(IrcChannel channel) {
    JPanel channelPanel = channel.getPanel();
    cardPanel.remove(channelPanel.getParent());
    treeModel.removeNodeFromParent(channel);
  }
  
  protected void addChannel(IrcChannel channel) {
    if (getChannel(channel.server.serverNick, channel.channelName) == null) {
      IRCChannelPanel channelPanel = new IRCChannelPanel(this, channel);
      channel.setPanel(channelPanel);
      cardPanel.add(new JScrollPane(channelPanel), channel.getId());
      treeModel.insertNodeInto(channel, channel.server,
          channel.server.getChildCount());
      tree.scrollPathToVisible(new TreePath(channel.server.getPath()));
    }
  }
  
  private IrcServer getServer(String nick) {
    IrcServer result = null;
    if (nick != null) {
      for (Enumeration en = top.children(); en.hasMoreElements();) {
        IrcServer server = (IrcServer)en.nextElement();
        if (nick.equals(server.serverNick)) { 
          result = server;
          break;
        }
      }
    }
    return result;
  }
  
  private IrcChannel getChannel(String serverNick, String channelName) {
    IrcChannel result = null;
    if (serverNick != null) {
  cycle:
      for (Enumeration en = top.children(); en.hasMoreElements();) {
        DefaultMutableTreeNode n1 = (DefaultMutableTreeNode)en.nextElement();
        if (n1 instanceof IrcServer) {
          IrcServer server = (IrcServer)n1;
          if (serverNick.equals(server.serverNick)) { 
            for (Enumeration en2 = n1.children(); en2.hasMoreElements();) {
              IrcChannel channel = (IrcChannel)en2.nextElement();
              if (channelName.equals(channel.channelName)) { 
                result = channel;
                break cycle;
              }
            }
          }
        }
      }
    }
    return result;
  }
  
  public void localizeComponents() {
    Top.TOP.setLabel(messages.getString("CnTServers"));
    addServerPopupItem.setText(messages.getString("CnTPopupAddServer"));
    addChannelPopupItem.setText(messages.getString("CnTPopupAddChannel"));
    removeServerPopupItem.setText(messages.getString("CnTPopupRemoveServer"));
    removeChannelPopupItem.setText(messages.getString("CnTPopupRemoveChannel"));
  }
  
  public void repaintTree() {
    tree.repaint();
  }
}
