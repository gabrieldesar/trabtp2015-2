package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class Attendant implements Runnable {

	private Server server;
	private Socket socket;
	private BufferedReader in;
	private PrintStream out;
	private boolean started;
	private boolean running;
	private Thread thread;
	private String clientName;

	public Attendant(Server server, Socket socket) throws Exception {
		this.server = server;
		this.socket = socket;
		this.started = false;
		this.running = false;
		open();
	}

	private void open() throws Exception {
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintStream(socket.getOutputStream());
			started = true;
		} catch (Exception e) {
			close();
			throw e;
		}

	}

	private void close() {
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
		try {
			socket.close();
		} catch (Exception e) {
			System.out.println(e);
		}

		in = null;
		out = null;
		socket = null;

		started = false;
		running = false;

		thread = null;
	}

	public void send(String message) {
		out.println(message);
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

	/* Loop de interação com cada cliente. A primeira mensagem recebida é o nome do cliente. 
	 * 
	 * */
	@Override
	public void run() {
		boolean isNameSent = false;
		while (running) {
			try {
				socket.setSoTimeout(2500);
				String message = in.readLine();
				if (message == null) {
					break;
				}
				if (isNameSent==false){
					isNameSent = true;
					clientName = message;
					server.broadcast(this, clientName+ " entrou no chat");
				}else{
					server.broadcast(this, clientName+": "+message);
				}
				

				System.out.println("Mensagem recebida do cliente [" + socket.getInetAddress().getHostName() + ": "
						+ socket.getPort() + "] " + message);
			

			} catch (SocketException e) {
				System.out.println("O Cliente fechou de forma inesperada, encerrar conexão...");
				break;
			} catch (SocketTimeoutException e) {
				// ignorar
			} catch (Exception e) {
				System.out.println(e);
			}

		}
		System.out.println("Encerrando Conexão");
		close();

	}
	
	

}
