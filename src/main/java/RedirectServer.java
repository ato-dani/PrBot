import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

//many thanks to this tutorial: https://dzone.com/articles/simple-http-server-in-java
public class RedirectServer {
    //copy the response from the Twitter API to twitterResponse, and display the landing page
    private class TwitterAuthorizationHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange ex) throws IOException{
            twitterResponse = ex.getRequestURI().toString();
            System.out.println("DEBUG:twitterResponse = " + twitterResponse);
            OutputStream out = ex.getResponseBody();
            String responsePage = FileIO.readFile(TWITTER_REDIRECT_PAGE);
            ex.sendResponseHeaders(200, responsePage.length());
            out.write(responsePage.getBytes(StandardCharsets.UTF_8));
            out.flush();
            out.close();
        }
    }

    final int SERVER_PORT = 1337;
    final String TWITTER_REDIRECT_PAGE = "src/main/resources/twitter_redirect.html";

    private HttpServer server;
    private TwitterAuthorizationHandler twitterAuthorizationHandler;
    private String twitterResponse = null;

    public RedirectServer() throws IOException{
        server = HttpServer.create(new InetSocketAddress(SERVER_PORT), 0);
        twitterAuthorizationHandler = new TwitterAuthorizationHandler();
        server.createContext("/twitterauth", twitterAuthorizationHandler);
        server.setExecutor(null);
    }

    public void startUp(){
        try{
            server.start();
        } catch(Exception e){
            System.out.println("Failed to start OAuth redirect server!");
            e.printStackTrace();
        }
    }

    //server MUST be shut down when done, or else it runs in the background, hogging the port
    public void shutDown(){
        try{
            server.stop(1);
        } catch(Exception e){
            System.out.println("Failed to stop OAuth redirect server!");
            e.printStackTrace();
        }
    }

    public String getTwitterResponse(){
        return twitterResponse;
    }
}
