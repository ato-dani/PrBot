import com.github.alexdlaird.ngrok.NgrokClient;
import com.github.alexdlaird.ngrok.protocol.CreateTunnel;
import com.github.alexdlaird.ngrok.protocol.Tunnel;
import com.slack.api.Slack;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

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
            OutputStream out = ex.getResponseBody();
            String responsePage = FileIO.readFile(TWITTER_REDIRECT_PAGE);
            ex.sendResponseHeaders(200, responsePage.length());
            out.write(responsePage.getBytes(StandardCharsets.UTF_8));
            out.flush();
            out.close();
        }
    }

    //todo: will need to be modified later. for now just a clone of the twitter handler
    private class SlackAuthorizationHandler implements HttpHandler{
        @Override
        public void handle(HttpExchange ex) throws IOException{
            slackResponse = ex.getRequestURI().toString();
            OutputStream out = ex.getResponseBody();
            String responsePage = FileIO.readFile(SLACK_REDIRECT_PAGE);
            ex.sendResponseHeaders(200, responsePage.length());
            out.write(responsePage.getBytes(StandardCharsets.UTF_8));
            out.flush();
            out.close();
        }
    }

    final int SERVER_PORT = 1337;
    final String TWITTER_REDIRECT_PAGE = "src/main/html/twitter_redirect.html";
    final String SLACK_REDIRECT_PAGE = "src/main/html/slack_redirect.html";

    private HttpServer server;
    private Tunnel tunnel;
    private TwitterAuthorizationHandler twitterAuthorizationHandler;
    private SlackAuthorizationHandler slackAuthorizationHandler;
    private String twitterResponse = null;
    private String slackResponse = null;

    public RedirectServer() throws IOException{
        server = HttpServer.create(new InetSocketAddress(SERVER_PORT), 0);
        twitterAuthorizationHandler = new TwitterAuthorizationHandler();
        server.createContext("/twitterauth", twitterAuthorizationHandler);
        slackAuthorizationHandler = new SlackAuthorizationHandler();
        server.createContext("/slackauth", slackAuthorizationHandler);
        server.setExecutor(null);
        tunnel = generateTunnel();
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

    public String getSlackResponse(){
        return slackResponse;
    }

    public String getSlackRedirectUrl(){
        return tunnel.getPublicUrl().replaceAll("http", "https") + "/slackauth";
    }

    private Tunnel generateTunnel(){
        NgrokClient rock = new NgrokClient.Builder().build();
        CreateTunnel createTunnel = new CreateTunnel.Builder().withAddr(SERVER_PORT).build();
        Tunnel tunnel = rock.connect(createTunnel);
        return tunnel;
    }
}
