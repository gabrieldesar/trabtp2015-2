package controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;


public class AtendenteThread implements Runnable{
	
	private ServidorThread	servidor;
    private Socket          socket;
    private BufferedReader  in;
    private PrintStream     out;    
    private boolean         inicializado;
    private boolean         executando;    
    private Thread          thread;
    private int				x;
    private int				y;
    
    public AtendenteThread(ServidorThread servidor, Socket socket)throws Exception{
    	x = 0;
    	y = 0;
        this.servidor = servidor;
    	this.socket = socket;
        this.inicializado = false;
        this.executando = false;        
        open();
    }
    
    private void open()throws Exception{
        try{
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(socket.getOutputStream());
            inicializado = true;
        }
        catch(Exception e){
            close();
            throw e;
        }
        
        
    }
    
    private void close(){
        if (in != null){
            try{
                in.close();
            }
            catch(Exception e){
                System.out.println(e);
            }
        }
        if (out != null){
            try{
                out.close();
            }
            catch(Exception e){
                System.out.println(e);
            }
        }
        try{
                socket.close();
            }
            catch(Exception e){
                System.out.println(e);
            }
            
        in      		= null;
        out     		= null;
        socket  		= null;
        
        inicializado    = false;
        executando      = false;
        
        thread  		= null;
    }
    public void send (String mensagem){
    	out.println(mensagem);    	
    }
    
    
    public void start(){
        if (!inicializado || executando ){
            return;
        }
        executando  = true;
        thread      = new Thread(this);
        thread.start();
    }
    
    public void stop() throws Exception{
        executando  = false;
        if (thread != null){
            thread.join();
        }
        
    }
    
    @Override
    public void run() {
        while (executando){
            try{
                socket.setSoTimeout(2500);
                String mensagem = in.readLine();
                if (mensagem == null){
                	break;
                }
                if (mensagem.equals("up")|| mensagem.equals("down")){
                	if (mensagem.equals("up")){
                		setY(getY()+1);
                		System.out.println(getY());
                	}
                	if (mensagem.equals("down")){
                		setY(getY()-1);
                		System.out.println(getY());
                	}
                }
                else{
                	System.out.println("Mensagem recebida do cliente [" +
                			socket.getInetAddress().getHostName() + ": "+
                			socket.getPort() +"] "+ mensagem);
                	servidor.sendToAll(this, mensagem);
                }
                
            }
            catch(SocketException e){
            	System.out.println("O Cliente fechou de forma inesperada, encerrar conexão...");
            	break;
            }
            catch(SocketTimeoutException e){
                //ignorar
            }            
            catch(Exception e){
                System.out.println(e);
            }
                   
        }
        System.out.println("Encerrando Conexão");
        close();
        
        
    }

	public void setX(int x) {
		this.x = x;
	}

	public int getX() {
		return x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getY() {
		return y;
	}
    
}
