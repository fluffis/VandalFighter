package se.fluff.fluffwikibrowser;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class FluffWikiBrowser extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1092959481818516827L;
	private JTextArea texta;
	
	public FluffWikiBrowser() {
		texta = new JTextArea();
		JPanel cp = new JPanel(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane(texta);
		cp.add(scrollPane);
		
		setSize(800, 600);
		setContentPane(cp);
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
	}
	
	public void openURL(String url) {
		// setTitle();
		System.out.println("Should openURL " + url);
		FluffWikiBrowserLoader loader = new FluffWikiBrowserLoader("sv", url, this);
		loader.start();
	}
	
	public void handleMessage(String lang, String page) {
		System.out.println("got a page");
		this.texta.setText(page);
	}
}
