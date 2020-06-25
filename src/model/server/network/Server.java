package model.server.network;

public interface Server {
	public void open(int port,ClientHandler c);
	public void stop();
	}
