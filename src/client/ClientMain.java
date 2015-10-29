package client;

import java.net.ConnectException;

import javax.swing.JOptionPane;

public class ClientMain {
	private ClientThread clientThread;
	private ClientView clientView;
	private static String ip;
	private final static int PORT = 2525;

	public ClientMain() {
		System.out.println("Iniciando cliente...");
		System.out.println("Informe o Usuário...");
		String name = (String) JOptionPane.showInputDialog(null, "Usuário", "Chat v1.0", JOptionPane.QUESTION_MESSAGE,
				null, null, null);
		System.out.println("Usuário: " + name);

		System.out.println("Informe o ip...");
		ip = (String) JOptionPane.showInputDialog(null, "Digite o ip", "Chat v1.0", JOptionPane.QUESTION_MESSAGE, null,
				null, null);
		System.out.println("Ip: " + ip);
		clientView = new ClientView();
		startClient(ip, PORT, name);
	}

	public void startClient(String ip, int port, String name) {
		try {
			clientThread = new ClientThread(ip, port, clientView, name);

		} catch (ConnectException e) {
			System.out.println("Host " + ip + ":" + port + " inexistente.");
			System.exit(0);
		} catch (Exception e) {
			System.out.println("Erro ao conectar");
			System.exit(1);
		}
		System.out.println("Conexão Estabelecida");
		clientView.addThread(clientThread);
		clientThread.start();
	}

	public static void main(String args[]) {
		new ClientMain();
	}
}
