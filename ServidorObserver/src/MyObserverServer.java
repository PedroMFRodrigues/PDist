
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

class MyObserverServer implements Observer {

    ArrayList<Clients> clientList;

    ServerSocket servSocket;
    int servPort;

    Socket socket;

    List<String> messages;
    int showMessages = 0;

    public MyObserverServer(ServerSocket s) throws UnknownHostException {
        servSocket = s;
    }

    public void listeningClient() {
        servPort = servSocket.getLocalPort();
        ReceiveFromCliente recFromServ = new ReceiveFromCliente(servSocket);
        recFromServ.addObserver(this);
        recFromServ.start();
    }

    public void SendToClient(MessageToServer msg, String Ip, int port) {
        msg.portToSend = servPort;

        SendFromServer sendToServer;
        try {
            sendToServer = new SendFromServer(msg, Ip, port);
            sendToServer.addObserver(this);
            sendToServer.start();
        } catch (IOException ex) {
            System.err.println("IOException Erro a enviar Mensaguem");
        }
    }

    public void CloseConnections() {
        try {
            if (!servSocket.isClosed()) {
                servSocket.close();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof Messages) {
            Messages m = (Messages) arg;
            System.out.println(m.toString());

            //TODO tratar mensagem recebida do obervable
        } else if (arg instanceof MessageToServer) {
            MessageToServer msgServer = (MessageToServer) arg;

            MessageAnaliser(msgServer);

        }
    }

    private void MessageAnaliser(MessageToServer msgServer) {
        if (msgServer.subject.equals("INITCONNECTION")) {//adiciona Cliente 
            clientList.add(new Clients(msgServer.from, msgServer.portToSend, msgServer.files));
            //TODO ADICIONAR A BD
        }
        if (msgServer.subject.equals("GETLISTOFFILES")) {
            ArrayList<String> files = new ArrayList<String>();
            for (int i = 0; i < clientList.size(); i++) {
                for (int j = 0; j < clientList.get(i).files.size(); j++) {
                    files.add(clientList.get(i).files.get(j));

                }

            }
            String IP = msgServer.from;
            int port = msgServer.portToSend;
            MessageToServer msg;
            try {
                msg = new MessageToServer("LISTOFFILES", "LISTOFFILES UPDATED", servPort, files);
                this.SendToClient(msg, msgServer.from, msgServer.portToSend);
            } catch (UnknownHostException ex) {
            }
        }

    }
}

class Clients {

    private String name;
    String ip;
    int port;
    ArrayList<String> files;

    public Clients(String ip, int port, ArrayList<String> files) {
        //this.name= name;
        this.ip = ip;
        this.port = port;
        this.files = files;
    }

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }

    public String getIP() {
        return ip;
    }
}
