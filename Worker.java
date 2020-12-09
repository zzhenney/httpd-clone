import http_request.Request;
import http_response.Resource;
import http_response.Response;
import http_response.ResponseFactory;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;
import java.util.logging.Level;
import server_conf.HttpdConf;
import server_conf.MimeTypes;
import java.nio.charset.StandardCharsets;

public class Worker implements Runnable {
    private Socket client;
    private MimeTypes mimes;
    private HttpdConf config;
    private Request request;
    private Logger logger;
    private boolean running = true;
    
    public Worker(Socket socket, HttpdConf config, MimeTypes mimes){
        this.client = socket;
        this.mimes = mimes;
        this.config = config;
        
    }
    
    @Override
    public void run(){
        System.out.println("INCOMING REQUEST");
        try{

            InputStream istream = client.getInputStream();
            int length = istream.available();
            StringBuilder data = new StringBuilder();
            try(Reader readIn = new InputStreamReader(new BufferedInputStream(istream), StandardCharsets.UTF_8)){
                

                while(length != 0){
                    int c = readIn.read();
                    data.append((char)c);
                    length--;
                }

            }
            //Reader readIn = new InputStreamReader(new BufferedInputStream(istream), StandardCharsets.UTF_8);
            

            String str = data.toString();     
            System.out.println(str);
            request = new Request(str);
            request.setInetAddress(client.getInetAddress());
            String requestURI = request.getURI();
            String requestExtension = request.getExtension();
            String requestMimeType = mimes.lookup(requestExtension);

            Resource resource = new Resource(requestURI, config, requestMimeType);

            ResponseFactory responseFactory = new ResponseFactory();
            Response response = responseFactory.getResponse(request, resource);

            try(OutputStream ostream = new BufferedOutputStream(client.getOutputStream())){
                response.send(ostream);
            }
            //OutputStream ostream = new BufferedOutputStream(client.getOutputStream());
            //response.send(ostream);



        }catch (Exception ex) {
            System.out.println(ex);
            //Response badRequest = new BadRequestResponse();
            //badRequest.send(out);
        }

        finally{
            try {
                client.close();
            } catch (IOException ex) {
                System.out.println("Worker.java: " + ex);

            }
        } 
    }    
}
