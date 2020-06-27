package model;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class SimulatorModel {
    public static volatile boolean stop=false;
    public static volatile boolean on=false;
    private static PrintWriter out;
    private static Socket socket;
    public void Connect(String ip,int port){
        try {
            socket = new Socket(ip, port);
            out=new PrintWriter(socket.getOutputStream());
            System.out.println("You are connected to FlightGear Simulator !");
            on=true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    public void Send(String[] data){
        for (String s: data) {
            out.println(s);
            out.flush();
            System.out.println(s);
        }

    }
    public void stop()
    {
        if(out!=null ){
        out.close();
        try {
            socket.close();
            on=false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }
}
