/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author PedroRodrigues
 */
public class ServidorObserver {

    public static final int TIMEOUT = 10 * 1000; //segundos
    private static final int REGPORT = 5555;
    
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String port;
        String ip;
        ServerSocket servSocket=null;

        System.out.println("Insert the regist Service Location >>>");
        System.out.print("IP: ");
        ip = scanner.nextLine();
        
        ServidorObserver servidor=new ServidorObserver();
        
        servSocket= new ServerSocket(0);
        
        if (!servidor.registerServer(ip, REGPORT,servSocket)){
            System.err.println("Sem Contacto com o Register Server");
            return;
        }
        
        MyObserverServer newObserver= new MyObserverServer(servSocket);
        newObserver.listeningClient();
        while(servSocket!=null){}//dont let the main thread close;
    }

    
    public boolean registerServer(String ip, int port,ServerSocket servSocket) {

        final String tosend = "RequestADDServer "+servSocket.getLocalPort();
        Socket socket = null;
        PrintWriter out;
        BufferedReader in;
        String request;

        try {
            socket = new Socket(InetAddress.getByName(ip), port);
            socket.setSoTimeout(TIMEOUT);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println(tosend);
            out.flush();

            request = in.readLine();
            System.out.println(request);

            if (request.contains("Registered")) {
                return true;
            }

            return false;
        } catch (IOException ex) {
            System.err.println("Erro a enviar Pedido a Register Service: " + ex);
        } catch (Exception ex) {
            System.err.println("Erro A Register Service: " + ex);
        }

        return false;
    }
}
class Messages {

    String message;
    String from;
    String subject;

    public Messages(String message, String from, String subject) {
        this.message = message;
        this.from = from;
        this.subject = subject;
    }

    Messages(String from) {
        this.message = "";
        this.from = from;
        this.subject = "";
    }

    @Override
    public String toString() {
        String aux;
        aux = "Subject: " + this.subject + " From: " + this.from + " Message: " + this.message;

        return aux;
    }

}
