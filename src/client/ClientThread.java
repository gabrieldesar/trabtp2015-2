package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ClientThread implements Runnable {

	private String name;
	private ClientView clientView;
	private Socket socket;
	private BufferedReader in;
	private PrintStream out;
	private boolean started;
	private boolean running;
	private Thread thread;

	public ClientThread(String address, int port, ClientView clientView, String name) throws Exception {
		this.setName(name);
		started = false;
		running = false;
		this.clientView = clientView;
		open(address, port);
	}

	//Abre conexão com o servidor
	public void open(String address, int port) throws Exception {
		try {
			socket = new Socket(address, port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintStream(socket.getOutputStream());
			started = true;
		} catch (Exception e) {
			System.out.println(e);
			close();
			throw e;
		}
	}

	public void close() {
		if (in != null) {
			try {
				in.close();
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		if (out != null) {
			try {
				out.close();
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		if (socket != null) {
			try {
				socket.close();
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		in = null;
		out = null;
		socket = null;
		started = false;
		running = false;
		thread = null;
	}

	public void start() {
		if (!started || running) {
			return;
		}
		running = true;
		thread = new Thread(this);
		thread.start();
		//Enviando nome do cliente
		send(name);
	}

	public void stop() throws Exception {
		running = false;
		if (thread != null) {
			thread.join();
		}
	}

	public boolean isRunning() {
		return running;
	}

	public void send(String message) {
		out.println(message);
	}

	//Loop de interação do cliente com o servidor
	@Override
	public void run() {
		while (running) {
			try {
				socket.setSoTimeout(2500);

				String message = in.readLine();

				if (message == null) {
					break;
				}

				System.out.println("Mensagem enviada pelo servidor: " + message);
				clientView.printMessage(message + "\n");
			} catch (SocketTimeoutException e) {
				//nothing
			} catch (Exception e) {
				System.out.println(e);
				break;
			}
		}
		close();

	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
