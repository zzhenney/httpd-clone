
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import server_conf.HttpdConf;
import server_conf.MimeTypes;

public class WebServer {
    public HttpdConf configuration;
    public MimeTypes mimeTypes;
    private static ServerSocket socketServer;
    private static ExecutorService threadPool;
    private Map<String,String> accessFiles;
    private final int PORT = 8800;
    
    public WebServer() throws FileNotFoundException, IOException{
        configuration = new HttpdConf("conf/httpd.conf");
        configuration.load();
        mimeTypes = new MimeTypes("conf/MIME.types");
        mimeTypes.load();
        System.out.println("Root:" + configuration.getRoot());
    }
   
    public void start() throws IOException{
      
        try{

            threadPool = Executors.newFixedThreadPool(5);
            socketServer = new ServerSocket(PORT);
            
            while(true){               
                Socket socket = socketServer.accept();
                System.out.println("New Request: Thread Created");
                threadPool.execute(new Worker(socket, configuration, mimeTypes));      
            } 
            
        }catch(IOException ioe){
            System.out.println("IOException: " + ioe.getMessage());
            System.out.println("Server shutting down...");
            threadPool.shutdown();
        } 
    }

    public static void main(String[] args) throws IOException{
        
        WebServer webserver = new WebServer();
        webserver.start();

    }
}
