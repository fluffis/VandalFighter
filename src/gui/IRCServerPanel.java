package gui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ResourceBundle;

import gui.ConnectionsPanel.IrcServer;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import data.Configuration;

public class IRCServerPanel extends JPanel {

  /**
   * 
   */
  private static final long serialVersionUID = 3248387806769421587L;

  ConnectionsPanel connectionsPanel;
  
  IrcServer server;
  Configuration config;
  ResourceBundle messages;
  JLabel serverNickLabel, serverNameLabel, serverPortLabel, userLabel, passwordLabel; 
  JTextField serverNameField, serverPortField, userField, passwordField; 
  JButton cancelChangesButton, applyChangesButton, connectButton, activateButton;
  CaretListener caretListener;
  
  public IRCServerPanel(ConnectionsPanel connectionsPanel, IrcServer server) {
    this.connectionsPanel = connectionsPanel;
    this.server = server;
    config = Configuration.getConfigurationObject();
    messages = ResourceBundle.getBundle("MessagesBundle", config.currentLocale);
    
    initComponents();
    server.setPanel(this);
  }
  
  public void initComponents() {
    serverNickLabel = new JLabel();
    serverNameLabel = new JLabel();
    serverNameField = new JTextField();
    serverPortLabel = new JLabel();
    serverPortField = new JTextField();
    userLabel = new JLabel();
    userField = new JTextField();
    passwordLabel = new JLabel();
    passwordField = new JTextField();
    cancelChangesButton = new JButton();
    applyChangesButton = new JButton();
    connectButton = new JButton(); 
    activateButton = new JButton(); 
    
    caretListener = new CaretListener() {
      public void caretUpdate(CaretEvent e) {
        if (!serverNameField.getText().equals(server.serverName)
            || !serverPortField.getText().equals(server.serverPort)
            || !userField.getText().equals(server.user)
            || !passwordField.getText().equals(server.password)) {
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
   
    serverNickLabel.setText(server.serverNick);
    Font f = serverNickLabel.getFont();
    serverNickLabel.setFont(new Font(f.getName(), Font.BOLD, f.getSize() + 4));
    Insets defaultInsets = new java.awt.Insets(5, 5, 5, 5);
    Insets otherInsets = new java.awt.Insets(5, 5, 10, 0);
    c.insets = otherInsets;
    add(serverNickLabel, c);
    c.insets = defaultInsets;

    serverNameLabel.setText(messages.getString("connections.server.name"));
    c.gridwidth = 1;
    c.gridy = row;
    add(serverNameLabel, c);
    
    serverNameField.setText(server.serverName);
    serverNameField.addCaretListener(caretListener);
    //c.weightx = 1;
    c.gridx = 1;
    c.gridy = row++;
    add(serverNameField, c);
    
    serverPortLabel.setText(messages.getString("connections.server.port"));
    c.weightx = 0;
    c.gridx = 0;
    c.gridy = row;
    add(serverPortLabel, c);
    
    serverPortField.setText(server.serverPort);
    serverPortField.addCaretListener(caretListener);
    c.gridx = 1;
    c.gridy = row++;
    add(serverPortField, c);
    
    userLabel.setText(messages.getString("connections.server.user"));
    c.gridx = 0;
    c.gridy = row;
    add(userLabel, c);
    
    userField.setText(server.user);
    userField.addCaretListener(caretListener);
    c.gridx = 1;
    c.gridy = row++;
    add(userField, c);
    
    passwordLabel.setText(messages.getString("connections.server.password"));
    c.gridx = 0;
    c.gridy = row;
    add(passwordLabel, c);
    
    passwordField.setText(server.password);
    passwordField.addCaretListener(caretListener);
    c.gridx = 1;
    c.gridy = row++;
    add(passwordField, c);
    
    // TODO user name policy 
    
    JPanel buttons = new JPanel();
    buttons.setLayout(new java.awt.FlowLayout(
        java.awt.FlowLayout.RIGHT));
    c.gridx = 0;
    c.gridy = row++;
    c.gridwidth = 2;
    add(buttons, c);

    cancelChangesButton.setText(messages.getString("connections.server.cancelChanges"));
    cancelChangesButton.setEnabled(false);
    cancelChangesButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cancelChangesActionPerformed(evt);
      }
    });
    buttons.add(cancelChangesButton);
    
    applyChangesButton.setText(messages.getString("connections.server.applyChanges"));
    applyChangesButton.setEnabled(false);
    applyChangesButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        applyChangesActionPerformed(evt);
      }
    });
    buttons.add(applyChangesButton);
    
    connectButton.setText(messages.getString("connections.server.connect"));
    if (!server.active)
      connectButton.setEnabled(false);
    buttons.add(connectButton); 
    
    activateButton.setText(messages.getString(server.active?"connections.server.deactivate":"connections.server.activate"));
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
    server.serverName = serverNameField.getText();
    server.serverPort = serverPortField.getText();
    server.user = userField.getText();
    server.password = passwordField.getText();

    applyChangesButton.setEnabled(false);
    cancelChangesButton.setEnabled(false);
  }
  
  private void cancelChangesActionPerformed(java.awt.event.ActionEvent evt) {
    serverNameField.setText(server.serverName);
    serverPortField.setText(server.serverPort);
    userField.setText(server.user);
    passwordField.setText(server.password);

    applyChangesButton.setEnabled(false);
    cancelChangesButton.setEnabled(false);
  }
  
  private void activateActionPerformed(java.awt.event.ActionEvent evt) {
    server.setActive(!server.isActive());
    activateButton.setText(messages.getString(server.active
        ?"connections.channel.deactivate"
        :"connections.channel.activate"));
    connectionsPanel.repaintTree();
  }
}
