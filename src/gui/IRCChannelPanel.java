package gui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ResourceBundle;

import gui.ConnectionsPanel.IrcChannel;
import gui.ConnectionsPanel.IrcServer;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import data.Configuration;

public class IRCChannelPanel extends JPanel {

  ConnectionsPanel connectionsPanel;
  
  IrcChannel channel;
  Configuration config;
  ResourceBundle messages;
  JLabel channelNameLabel, channelProjectLabel; 
  JTextField channelProjectField; 
  JButton cancelChangesButton, applyChangesButton, connectButton, activateButton;
  CaretListener caretListener;
  
  public IRCChannelPanel(ConnectionsPanel connectionsPannel, IrcChannel channel) {
    this.connectionsPanel = connectionsPannel;
    this.channel = channel;
    config = Configuration.getConfigurationObject();
    messages = ResourceBundle.getBundle("MessagesBundle", config.currentLocale);
    
    initComponents();
    channel.setPanel(this);
  }
  
  public void initComponents() {   
    channelNameLabel = new JLabel();
    channelProjectLabel = new JLabel();
    channelProjectField = new JTextField();
    // TODO channel parser
    // TODO channel sender
    cancelChangesButton = new JButton();
    applyChangesButton = new JButton();
    connectButton = new JButton(); 
    activateButton = new JButton(); 
    
    caretListener = new CaretListener() {
      public void caretUpdate(CaretEvent e) {
        if (!channelProjectField.getText().equals(channel.channelProject)) {
          applyChangesButton.setEnabled(true);
          cancelChangesButton.setEnabled(true);
        } else {
          applyChangesButton.setEnabled(false);
          cancelChangesButton.setEnabled(false);
        }
      }
    };
    
    setLayout(new java.awt.GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();
    int row = 0;
    c.fill = GridBagConstraints.HORIZONTAL;
    c.gridwidth = 2;
    c.gridx = 0;
    c.gridy = row++;
    c.anchor = GridBagConstraints.LINE_START;
   
    channelNameLabel.setText(channel.channelName);
    Font f = channelNameLabel.getFont();
    channelNameLabel.setFont(new Font(f.getName(), Font.BOLD, f.getSize() + 4));
    Insets defaultInsets = new java.awt.Insets(5, 5, 5, 5);
    Insets otherInsets = new java.awt.Insets(5, 5, 10, 0);
    c.insets = otherInsets;
    add(channelNameLabel, c);
    c.insets = defaultInsets;

    channelProjectLabel.setText(messages.getString("connections.channel.project"));
    c.gridwidth = 1;
    c.gridy = row;
    add(channelProjectLabel, c);
    
    channelProjectField.setText(channel.channelProject);
    channelProjectField.addCaretListener(caretListener);
    //c.weightx = 1;
    c.gridx = 1;
    c.gridy = row++;
    add(channelProjectField, c);
    
    JPanel buttons = new JPanel();
    buttons.setLayout(new java.awt.FlowLayout(
        java.awt.FlowLayout.RIGHT));
    c.gridx = 0;
    c.gridy = row++;
    c.gridwidth = 2;
    add(buttons, c);

    cancelChangesButton.setText(messages.getString("connections.channel.cancelChanges"));
    cancelChangesButton.setEnabled(false);
    cancelChangesButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cancelChangesActionPerformed(evt);
      }
    });
    buttons.add(cancelChangesButton);
    
    applyChangesButton.setText(messages.getString("connections.channel.applyChanges"));
    applyChangesButton.setEnabled(false);
    applyChangesButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        applyChangesActionPerformed(evt);
      }
    });
    buttons.add(applyChangesButton);
    
    connectButton.setText(messages.getString("connections.channel.connect"));
    if (!channel.server.active)
      connectButton.setEnabled(false);
    buttons.add(connectButton); 
    
    activateButton.setText(messages.getString(channel.active
        ?"connections.channel.deactivate"
        :"connections.channel.activate"));
    activateButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        activateActionPerformed(evt);
      }
    });
    buttons.add(activateButton); 
    
    c.gridy = row++;
    c.gridwidth = 3;
    c.weighty = 1;
    add(new JLabel(), c);

    c.gridx = 2;
    c.gridy = 0;
    c.gridwidth = 1;
    c.gridheight = row;
    c.weightx = 1;
    add(new JLabel(), c);
  }
  
  private void applyChangesActionPerformed(java.awt.event.ActionEvent evt) {
    channel.channelProject = channelProjectField.getText();

    applyChangesButton.setEnabled(false);
    cancelChangesButton.setEnabled(false);
  }
  
  private void cancelChangesActionPerformed(java.awt.event.ActionEvent evt) {
    channelProjectField.setText(channel.channelProject);

    applyChangesButton.setEnabled(false);
    cancelChangesButton.setEnabled(false);
  }
  
  private void activateActionPerformed(java.awt.event.ActionEvent evt) {
    channel.setActive(!channel.isActive());
    activateButton.setText(messages.getString(channel.active
        ?"connections.channel.deactivate"
        :"connections.channel.activate"));
    connectionsPanel.repaintTree();
  }
  
}
