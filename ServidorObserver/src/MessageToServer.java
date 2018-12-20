import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 *
 * @author PedroRodrigues
 */
public class MessageToServer implements Serializable{
    static final long serialVersionUID = 1L;
    
    String from;
    String subject;
    String message;
    int portToSend;
    ArrayList<String> files;
    
    public MessageToServer ( String subject, String message,int port,ArrayList<String> files) throws UnknownHostException{
        this.from=InetAddress.getLocalHost().getHostAddress();
        this.subject=subject;
        this.message=message;
        
        this.portToSend=port;
        
        this.files=files;
    }
}
