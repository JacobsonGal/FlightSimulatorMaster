package model;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;


public class SimulatorModel {
    public static volatile boolean stop=false;
    private static PrintWriter out;
    private static Socket socket;
    public void Connect(String ip,int port){
        try {
            socket = new Socket(ip, port);
            out=new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("You are connected to FlightGear Simulator !");

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }
}
