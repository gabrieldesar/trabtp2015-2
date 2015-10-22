package view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import controller.ClienteThread;

public class ClientFrame extends JFrame implements ActionListener {

	private static final long serialVersionUID = -3919707070322327843L;
	private JPanel panelMain;
	private JPanel panelNorth;
	private JPanel panelSouth;
	private JTextField texto;
	private JTextArea text;
	private ClienteThread cliente;
	final int WIDTH = 500;
	final int HEIGHT = 425;

	public ClientFrame() {
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

		text = new JTextArea(20, 40);
		JScrollPane scr = new JScrollPane(text);
		text.setEditable(false);
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		text.setEnabled(false);
		panelNorth.add(scr);

		return panelNorth;
	}

	public JPanel panelSouth() {
		panelSouth.removeAll();

		texto = new JTextField(33);
		JButton button = new JButton("Enviar");
		button.addActionListener(this);

		panelSouth.add(texto);
		panelSouth.add(button);
		return panelSouth;
	}

	public JPanel panelMain() {

		panelMain.removeAll();
		return panelMain;
	}

	public void addThread(ClienteThread thread) {
		this.cliente = thread;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (!texto.getText().equals("")) {
			text.append(cliente.getName() + ": " + texto.getText() + "\n");
			if (!cliente.isExecutando()) {
				return;
			}
			cliente.send(texto.getText());
		}
		texto.setText("");
	}

	public void printMsg(String mensagem) {
		text.append(mensagem);
	}
}
