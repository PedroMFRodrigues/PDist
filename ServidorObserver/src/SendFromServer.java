import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SendFromServer extends Observable implements Runnable {

    public static final int MAX_SIZE = 4000;
    public static final int TIMEOUT = 5 * 1000; //segundos
    protected Socket serverSocket;

    MessageToServer msg;
    Messages messageToSend;

    public SendFromServer(MessageToServer message, String ip, int port ) throws UnknownHostException, IOException {
        serverSocket = new Socket(InetAddress.getByName(ip),port);
        messageToSend = new Messages("SendToServer");
        msg = message;
    }

    public void SetMessage(String subject, String message) {
        this.messageToSend.message = message;
        this.messageToSend.subject = subject;
        setChanged();
        notifyObservers(messageToSend);
    }

    public void processRequests(){
        ObjectOutputStream out;

        if (serverSocket == null) {
            this.SetMessage("Erro", "Erro na Criação do Socket");
            return;
        }

        this.SetMessage("Inicio", "Servidor de carregamento de ficheiros iniciado...");

        try {                 
            serverSocket.setSoTimeout(TIMEOUT);
            
            out = new ObjectOutputStream(serverSocket.getOutputStream());

            out.writeObject(msg);
            out.flush();
            this.SetMessage("Fim", "Enviada Mensagem ao Servidor");
        } catch (UnknownHostException ex) {
            this.SetMessage("Erro", "Erro de UnknownHostException no envio da mensagem");
        } catch (IOException ex) {
            this.SetMessage("Erro", "Erro de IOException no envio da mensagem");
        }finally{
            this.SetMessage("Fim", "Mensagem Enviada!");
            try {
                serverSocket.close();
            } catch (IOException e) {
            }
        }
    }

    public void start() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        this.SetMessage("Inicio", "A iniciar Comunicação");

        try {
            this.processRequests();

        } catch (NumberFormatException e) {
            this.SetMessage("Erro", "O porto de escuta deve ser um inteiro positivo.");
        } catch (Exception e) {
            this.SetMessage("Erro", "Ocorreu uma excepcao ao nivel do socket TCP:\n\t" + e);
        }
    }
}
