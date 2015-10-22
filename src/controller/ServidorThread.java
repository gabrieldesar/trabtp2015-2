package controller;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class ServidorThread implements Runnable {

	private ServerSocket server;
	private ArrayList<AtendenteThread> atendentes;
	private boolean inicializado;
	private boolean executando;
	private Thread thread;

	public ServidorThread(int porta) throws Exception {
		atendentes = new ArrayList<AtendenteThread>();
		inicializado = false;
		executando = false;
		open(porta);
	}

	private void open(int porta) throws Exception {
		server = new ServerSocket(porta);
		inicializado = true;
	}

	private void close() {
		for (AtendenteThread atendente : atendentes) {
			try {
				atendente.stop();
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		try {
			server.close();
		} catch (Exception e) {
			System.out.println(e);
		}

		server = null;
		inicializado = false;
		executando = false;
		thread = null;
	}

	public void broadcast(AtendenteThread origem, String mensagem) throws Exception {
		Iterator<AtendenteThread> i = atendentes.iterator();
		while (i.hasNext()) {
			AtendenteThread atendente = i.next();
			if (atendente != origem) {
				try {
					atendente.send(mensagem);
				} catch (Exception e) {
					atendente.stop();
					i.remove();
				}

			}
		}
	}

	public void start() {
		if (!inicializado || executando) {
			return;
		}
		executando = true;
		thread = new Thread(this);
		thread.start();
	}

	public void stop() throws Exception {
		executando = false;
		if (thread != null) {
			thread.join();
		}

	}

	@Override
	public void run() {
		System.out.println("Aguardando Conexão");
		while (executando) {
			try {
				server.setSoTimeout(2500);
				Socket socket = server.accept();

				System.out.println("Conexão Estabelecida");

				AtendenteThread atendente = new AtendenteThread(this, socket);
				atendente.start();

				atendentes.add(atendente);

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

		ServidorThread servidor = new ServidorThread(2525);
		servidor.start();

		System.out.println("Pressione ENTER para encerrar o Servidor");
		new Scanner(System.in).nextLine();
		System.out.println("Encerrando o servidor...");
		servidor.stop();
		System.out.println("Servidor fechado.");

	}
}
