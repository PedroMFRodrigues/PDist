/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package registerservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


/**
 *
 * @author PedroRodrigues
 */
public class RegisterService {

    static ArrayList<CustomPair> socketList = new ArrayList<>();

    public static final int TIMEOUT = 10 * 1000; //segundos
    public static final int REGPORT = 5555; //just 1 at the time

    public static void main(String[] args) {

        ServerSocket servSocket = null;
        Socket socketToSend = null;

        BufferedReader in;
        PrintWriter out;

        String request;

        try {
            servSocket = new ServerSocket(REGPORT);
            System.out.println("Starting Service on : "+ InetAddress.getLocalHost().getHostAddress() +"  port: "+ servSocket.getLocalPort());
            while (true) {
                try {                    
                    socketToSend = servSocket.accept();
                    socketToSend.setSoTimeout(TIMEOUT);

                    System.out.println("A Receber mensagem");

                    in = new BufferedReader(new InputStreamReader(socketToSend.getInputStream()));

                    request = in.readLine();

                    request = analiseRequest(request, socketToSend);

                    try {                        
                        out = new PrintWriter(socketToSend.getOutputStream(), true);
                        out.println(request);
                        out.flush();
                        System.err.println(request);
                    } catch (IOException ex) {
                        System.err.println("Erro a Enviar Resposta " + ex);
                    } catch (NullPointerException ex) {
                        System.err.println("NullPointerException Erro a Enviar Resposta " + ex);
                    }

                } catch (Exception ex) {
                    System.out.println("Exception Erro: " + ex);
                }
            }
        } catch (IOException ex) {
            System.out.println("IOException Erro a Receber Mensagem" + ex);
        } finally {
            System.out.println("A Encerrar Recepcao de Mensagens");
            try {
                servSocket.close();
            } catch (Exception e) {
            }
            try {
                socketToSend.close();
            } catch (Exception ex) {
            }
        }
    }

    private static String analiseRequest(String request, Socket socket) {
        String aux = null;
        CustomPair cp = null;

        if (request.contains("RequestServer")) {//pedido de um Cliente para receber um Servidor 
            cp = getRandomClient();
            if (cp != null) {
                aux = cp.getIP() + " " + cp.getPort();
            } else {
                aux = "NoServerOnline";
            }
        } else if (request.contains("RequestADDServer")) {
            String[] parts = request.split(" ");
            System.out.println("New Serv:"+socket.getInetAddress().getHostAddress()+ "  Port: "+ parts[1]);
            socketList.add(new CustomPair(socket.getInetAddress().getHostAddress(),Integer.parseInt( parts[1])));
            aux = "Registered";
        }

        return aux;
    }

    private static CustomPair getRandomClient() {
        try {
            Random r = new Random();
            int low = 0;
            int high = socketList.size();
  
             
            if (high == 0) {
                return null;
            } else {
                
                int result = r.nextInt(high - low) + low;
                System.out.println(result);
                return socketList.get(result);
            }
        } catch (Exception e) {
            System.err.println(e);
        }
        return null;
    }
}

class CustomPair {
    private String ip;
    private int port;
    
    public CustomPair(String ip, int port){
        this.ip=ip;
        this.port= port;
    }
    
    public int getPort(){return port;}
    public String getIP(){return ip;}
}
