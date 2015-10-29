package client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ClientView extends JFrame implements ActionListener {

	private static final long serialVersionUID = -3919707070322327843L;
	private JPanel panelMain;
	private JPanel panelNorth;
	private JPanel panelSouth;
	private JTextField textField;
	private JTextArea textArea;
	private ClientThread clientThread;
	final int WIDTH = 500;
	final int HEIGHT = 425;

	public ClientView() {
		this.setTitle("Cliente-Servidor");

		panelNorth = new JPanel();
		panelNorth.setOpaque(false);
		this.panelNorth();

		panelSouth = new JPanel();
		panelSouth.setOpaque(false);
		this.panelSouth();

		panelMain = new JPanel();
		panelMain.setOpaque(false);
		this.panelMain();

		panelMain.add(panelNorth, BorderLayout.NORTH);
		panelMain.add(panelSouth, BorderLayout.SOUTH);
		this.add(panelMain, BorderLayout.CENTER);
		this.setSize(WIDTH, HEIGHT);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	public void refresh() {

	}

	public JPanel panelNorth() {
		panelNorth.removeAll();

		textArea = new JTextArea(20, 40);
		JScrollPane scr = new JScrollPane(textArea);
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setEnabled(false);
		panelNorth.add(scr);

		return panelNorth;
	}

	public JPanel panelSouth() {
		panelSouth.removeAll();

		textField = new JTextField(33);
		JButton button = new JButton("Enviar");
		button.addActionListener(this);

		panelSouth.add(textField);
		panelSouth.add(button);
		return panelSouth;
	}

	public JPanel panelMain() {

		panelMain.removeAll();
		return panelMain;
	}

	public void addThread(ClientThread thread) {
		this.clientThread = thread;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (!textField.getText().equals("")) {
			textArea.append(textField.getText() + "\n");
			if (!clientThread.isRunning()) {
				return;
			}
			clientThread.send(textField.getText());
		}
		textField.setText("");
	}

	public void printMessage(String message) {
		textArea.append(message);
	}
}
