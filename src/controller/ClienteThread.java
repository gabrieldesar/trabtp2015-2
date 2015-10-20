package controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

import view.ClientFrame;

public class ClienteThread implements Runnable {
	
    private String		name;
    private ClientFrame		viewCliente;
    private Socket 		socket;    
    private BufferedReader      in;
    private PrintStream         out;    
    private boolean 		inicializado;
    private boolean 		executando;    
    private Thread              thread;
    
    public ClienteThread(String endereco, int porta, ClientFrame viewCliente, String name) throws Exception{
    	this.setName(name);
        inicializado = false;
        executando = false;
        this.viewCliente = viewCliente;
        open (endereco, porta);
    }
    
    public void open(String endereco, int porta)throws Exception{
        try{
            socket = new Socket(endereco, porta);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintStream (socket.getOutputStream());        
            inicializado = true;
        }
        catch(Exception e){
            System.out.println(e);
            close();
            throw e;
        }       
    }
    public void close(){
        if (in!=null){
            try{
                in.close();
            }
            catch(Exception e){
                System.out.println(e);
            }
        }
        if (out!=null){
            try{
                out.close();
            }
            catch(Exception e){
                System.out.println(e);
            }
        }
        if (socket!=null){
            try{
                socket.close();
            }
            catch(Exception e){
                System.out.println(e);
            }
        }
        in = null;
        out = null;
        socket = null;        
        inicializado = false;
        executando = false;        
        thread = null;
    }
    
    
    
    public void start(){
        if (!inicializado || executando){
            return;
        }
        executando = true;
        thread = new Thread(this);
        thread.start();
        send(name);
    }
    public void stop() throws Exception{
        executando = false;
        if (thread != null){
            thread.join();
        }
    }
    public boolean isExecutando(){
        return executando;
    }
    public void send(String mensagem){
    	out.println(mensagem);        
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
                 
                 System.out.println("Mensagem enviada pelo servidor: " +
                         mensagem);
                 viewCliente.printMsg(mensagem+"\n");
             }
             catch(SocketTimeoutException e){
                 //ignorar
                 
             }
             catch(Exception e){
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
