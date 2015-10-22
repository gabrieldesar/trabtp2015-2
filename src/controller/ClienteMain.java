package controller;

import java.net.ConnectException;

import javax.swing.JOptionPane;

import view.ClientFrame;

public class ClienteMain {
	private ClienteThread cliente;
	private ClientFrame viewCliente;
	private static String ip;
	private final static int porta = 2525;

	public ClienteMain() {
		System.out.println("Iniciando cliente...");
		System.out.println("Informe o Usuário...");
		String name = (String) JOptionPane.showInputDialog(null, "Usuário", "Chat v1.0", JOptionPane.QUESTION_MESSAGE,
				null, null, null);
		System.out.println("Usuário: " + name);
		viewCliente = new ClientFrame();

		System.out.println("Informe o ip...");
		ip = (String) JOptionPane.showInputDialog(null, "Digite o ip", "Chat v1.0", JOptionPane.QUESTION_MESSAGE, null,
				null, null);
		System.out.println("Ip: " + ip);

		startClient(ip, porta, name);
	}

	public void startClient(String ip, int porta, String name) {
		try {
			cliente = new ClienteThread(ip, porta, viewCliente, name);

		} catch (ConnectException e) {
			System.out.println("Host " + ip + ":" + porta + " inexistente.");
			System.exit(0);
		} catch (Exception e) {
			System.out.println("Erro ao conectar");
			System.exit(1);
		}
		System.out.println("Conexão Estabelecida");
		viewCliente.addThread(cliente);
		cliente.start();
	}

	public static void main(String args[]) {
		new ClienteMain();
	}
}
