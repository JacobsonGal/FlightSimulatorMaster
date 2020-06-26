package model.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;

import model.server.network.CacheManager;
import model.server.network.ClientHandler;
import model.server.network.ClientHandlerPath;
import model.server.network.FileCacheManager;
import model.server.network.MyClientHandler;
import model.server.network.Server;

public class MySerialServer implements Server {
	
	private int port;
	private static ClientHandler c;
	private volatile boolean stop;
	
	
	public MySerialServer() {
		this.stop = false;
	}

	@Override
	public void open(int port,ClientHandler c) {
		this.port=port;
		this.c=c;
		new Thread(()->{
			try {
				runServer();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	@Override
	public void stop() {
		stop=true;
	}


	public  void runServer()throws Exception {
		ServerSocket server=new ServerSocket(port);
		System.out.println("Solver Server is alive and waiting for a problem!");
		server.setSoTimeout(300000000);
		while(!stop){
			try{
				Socket aClient=server.accept(); // blocking call
				System.out.println("You are connected to the solver server!");
				try {
					c.handleClient(aClient.getInputStream(), aClient.getOutputStream());
					//aClient.getInputStream().close();
					//aClient.getOutputStream().close();
					aClient.close();
		} catch (IOException e) {
			System.out.println("invalid input2-output");
			e.printStackTrace();
		}
		}catch(SocketTimeoutException e) {
			System.out.println("Time Out");
			e.printStackTrace();
		}
		}
		server.close();
	}
	
	public static void main(String[] args) {

	     Server s=new MySerialServer(); // initialize
	     CacheManager cm=new FileCacheManager();
	     MyClientHandler ch=new MyClientHandler(cm);
	     s.open(5000,new ClientHandlerPath(ch));
	        
		/*Server s = null;
		
		try {
			s = new MySerialServer();
			s.open(5500,c);
			Thread.sleep(100);
			System.out.println("Press any key to close the server");
			Scanner scanner = new Scanner(System.in);
			scanner.nextLine();
			scanner.close();
		} catch (Exception e) {}
		*/
	}
	
}
