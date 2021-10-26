import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * Dummy websever that acts as a redirect target for OAuth2 authentication
 * many thanks to this tutorial: https://dzone.com/articles/simple-http-server-in-java
 */
public class RedirectServer {
    /**
     * Copy the response from the Twitter API to twitterResponse, and display the landing page
     */
    private class TwitterAuthorizationHandler implements HttpHandler{
        /**
         * Handle requests to the Twitter redirect page
         * @param ex            Incoming request
         * @throws IOException  Incapable of reading landing page HTML file
         */
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

    /**
     * Copy the response from the Twitter API to twitterResponse, and display the landing page
     */
    private class RedditAuthorizationHandler implements HttpHandler {
        /**
         * Handle requests to the Twitter redirect page
         * @param ex            Incoming request
         * @throws IOException  Incapable of reading landing page HTML file
         */
        @Override
        public void handle(HttpExchange ex) throws IOException{
            redditResponse = ex.getRequestURI().toString();
            OutputStream out = ex.getResponseBody();
            String responsePage = null;

            if (redditAuthorizationHasError(ex.getRequestURI())) {
                // Error happened when authorizing, tell user to reauthorize
                responsePage = FileIO.readFile(ERROR_PAGE);
            } else {
                // while token wait and then send the token too
                responsePage = FileIO.readFile(REDDIT_REDIRECT_PAGE);
            }
            ex.sendResponseHeaders(200, responsePage.length());
            out.write(responsePage.getBytes(StandardCharsets.UTF_8));
            out.flush();
            out.close();
        }
    }

    final int SERVER_PORT = 1337;
    final String TWITTER_REDIRECT_PAGE = "src/main/html/twitter_redirect.html";
    final String REDDIT_REDIRECT_PAGE = "src/main/html/reddit_redirect.html";
    final String ERROR_PAGE = "src/main/html/error.html";

    private HttpServer server;
    private TwitterAuthorizationHandler twitterAuthorizationHandler;
    private RedditAuthorizationHandler redditAuthorizationHandler;
    private String twitterResponse = null;
    private String redditResponse = null;
    private String redditBaseUrl = "http://localhost:1337/redditauth";

    /**
     * Generate a RedirectServer, and initialize the landing pages
     * @throws IOException  Invalid server port given.
     */
    public RedirectServer() throws IOException{
        server = HttpServer.create(new InetSocketAddress(SERVER_PORT), 0);
        twitterAuthorizationHandler = new TwitterAuthorizationHandler();
        server.createContext("/twitterauth", twitterAuthorizationHandler);
        redditAuthorizationHandler = new RedditAuthorizationHandler();
        server.createContext("/redditauth", redditAuthorizationHandler);
        server.setExecutor(null);
    }

    /**
     * Spin up the RedirectServer.
     */
    public void startUp(){
        try{
            server.start();
        } catch(Exception e){
            System.out.println("Failed to start OAuth redirect server!");
            e.printStackTrace();
        }
    }

    /**
     * Shut down the RedirectServer.
     * server MUST be shut down when done, or else it runs in the background, hogging the port
     */
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

    public String getRedditBaseUrl() {
        return redditBaseUrl;
    }

    public String getRedditResponse() {
        return redditResponse;
    }

    /*
     * Uri will have a query parameter "error" if the authorization process resulted
     * in an error.
     * 
     * @param url the url the user is redirected to from reddit
     * @return true if the authorization process had any error
     */
    public Boolean redditAuthorizationHasError(URI url) {
        String errorQueryName = "error";
        return Requester.getQueryValue(url.getQuery(), errorQueryName) != null;
    }
     /**
     * Final url consists of base url with randomly generated state as query used for security
     * @return  Reddit final URL
     */
    public String getRedditFinalUrl() {
        // final url consists of base url with randomly generated state as query used
        // for security
        return redditBaseUrl + redditResponse;
    }
}
