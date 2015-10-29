package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class Server implements Runnable {

	private ServerSocket serverSocket;
	private ArrayList<Attendant> attendants;
	private boolean started;
	private boolean running;
	private Thread thread;
	public static final Integer PORT = 2525;

	public Server(int port) throws Exception {
		attendants = new ArrayList<Attendant>();
		started = false;
		running = false;
		open(port);
	}

/*		Abre um server socket, especificada numa dada porta*/
	private void open(int port) throws Exception {
		serverSocket = new ServerSocket(port);
		started = true;
	}

	private void close() {
		for (Attendant attendant : attendants) {
			try {
				attendant.stop();
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		try {
			serverSocket.close();
		} catch (Exception e) {
			System.out.println(e);
		}

		serverSocket = null;
		started = false;
		running = false;
		thread = null;
	}
	
	//Envia mensagens para todos os clientes exceto o remetente da mensagem
	public void broadcast(Attendant from, String message) throws Exception {
		Iterator<Attendant> i = attendants.iterator();
		while (i.hasNext()) {
			Attendant a = i.next();
			if (!a.equals(from)) {
				try {
					a.send(message);
				} catch (Exception e) {
					a.stop();
					i.remove();
				}

			}
		}
	}

	public void start() {
		if (!started || running) {
			return;
		}
		running = true;
		thread = new Thread(this);
		thread.start();
	}

	public void stop() throws Exception {
		running = false;
		if (thread != null) {
			thread.join();
		}

	}

	/*Loop principal do servidor. Ele espera conexões que, quando estabelecidas,
	abre uma nova thread específica para tratá-la*/
	@Override
	public void run() {
		System.out.println("Aguardando Conexão");
		while (running) {
			try {
				serverSocket.setSoTimeout(2500);
				Socket socket = serverSocket.accept();

				System.out.println("Conexão Estabelecida");

				Attendant atendente = new Attendant(this, socket);
				atendente.start();

				attendants.add(atendente);

			} catch (SocketTimeoutException e) {
				// ignorar
			} catch (Exception e) {
				System.out.println(e);
				break;
			}
		}
		close();
	}

	public static void main(String args[]) throws Exception {
		System.out.println("Iniciando Servidor...");

		Server servidor = new Server(PORT);
		servidor.start();

		System.out.println("Pressione ENTER para encerrar o Servidor");
		new Scanner(System.in).nextLine();
		System.out.println("Encerrando o servidor...");
		servidor.stop();
		System.out.println("Servidor fechado.");

	}
}
