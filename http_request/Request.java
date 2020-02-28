package http_request;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class Request {
    
    private String uri;
    private String body  = "";
    private String verb;
    private String httpVersion;
    private Map<String, String> headers;
    private int statusCode;
    private boolean error = true;
    private InetAddress inetAddress;
    
    public Request(String request){
        headers = new LinkedHashMap<>();
        parse(request);
    }//end constructor
    
    public Request(Stream client){
        headers = new LinkedHashMap<>();
        DataInputStream di_stream = new DataInputStream((InputStream)client);
        
        try {
            String data = di_stream.readUTF();
            parse(data);
            
        }catch(IOException ex) {
            Logger.getLogger(Request.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//end constructor
    
    public String getHeader(String key){
        if(headers.containsKey(key))
            return headers.get(key);
        else
            return "";
    }

    private void parse(String request){
        
        StringTokenizer tokenizer = new StringTokenizer(request, " \r\n");
        StringTokenizer tokenizer2;
        verb = tokenizer.nextToken();
        uri = tokenizer.nextToken();
        httpVersion = tokenizer.nextToken("\r\n");
 
        
        while(tokenizer.hasMoreTokens()){
            try{
                String tokens = tokenizer.nextToken();
                tokenizer2 = new StringTokenizer(tokens, ":\r\n");
                    
                    
                    while(tokenizer2.hasMoreTokens() && tokens.contains(":")){
                       String key = tokenizer2.nextToken(); 
                       String value;
                       //System.out.println("NEXT TOKEN: " + to);
                       if(tokenizer2.hasMoreElements()){
                           value = tokenizer2.nextToken();
                       }
                       else{
                           value = "";
                       }

                       
                       System.out.println("kv: " + key + " -- " + value);
                       headers.put(key, value);    
                    }
                    
                    if(!tokens.contains(":"))
                        body += tokens + "\r\n";
                    
                    
            }catch(Exception e){
                System.out.println("Request 81 Exception: " + e.getMessage());
            }
            
        }
            
        checkForErrors();
    }
    
    public String getURI(){
        return uri;
    }
    
    public void setURI(String url){
        this.uri = url;
    }
    
    public String getVerb(){
        return verb;
    }
    
    public void setVerb(String verb){
        this.verb = verb;
    }
    
    public String getHttpVersion(){
        return httpVersion;
    }
    
    public void setHttpVersion(String httpVersion){
        this.httpVersion = httpVersion;
    }
    
    public int getStatusCode(){
        return this.statusCode;
    }
    
    public boolean hasError(){
        return error;
    }
    
    public String getBody(){
        return body;
    }
    
    public void checkForErrors(){
        if(verb.equals("GET") || 
           verb.equals("PUT") || 
           verb.equals("POST") || 
           verb.equals("HEAD") ||
           verb.equals("DELETE")){
            
            error = false;
            statusCode = 200;
        }else{
            // error is set to true by default
            statusCode = 400;
        }
    }
    
    public void setInetAddress(InetAddress inetAddress){
        this.inetAddress = inetAddress;
    }
    
    public InetAddress getInetAddress(){
        return this.inetAddress;
    } 
    
    public boolean isConditional(){
        return headers.containsKey("If-Modified-Since");
    }
    
    public String getExtension(){
        String[] tokens = uri.split("\\.");
        String extension = tokens[tokens.length-1];
        return extension;
    }

}
