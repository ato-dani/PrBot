import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.Headers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import com.google.gson.JsonObject;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class GatewayServer {
  /**
   * Integerates User Reddit account with user.
   */
  private class RedditSignInHandler implements HttpHandler {
    /**
     * Integerate user's Reddit account with PrBot
     * 
     * @param ex Incoming request
     * @throws IOException Incapable of reading landing page HTML file
     */
    @Override
    public void handle(HttpExchange ex) {
      AccessTokenInfo accessToken = new AccessTokenInfo(null, null);
      try {
        Headers header = ex.getResponseHeaders();
        header.add("Content-Type", String.format("application/json; charset=%s", StandardCharsets.UTF_8));
        initHeaders(header);
        if (!doAPIKeysExist(REDDIT_CLIENT_ID, REDDIT_CLIENT_SECRET, ex)) {
          System.out.println("Please first define reddit client id and secret");
          System.out.printf("Reddit client id: %s, Reddit client secret: %s\n", REDDIT_CLIENT_ID, REDDIT_CLIENT_SECRET);
          return;
        }
        accessToken = OAuthHandler.authorizeReddit(REDDIT_CLIENT_ID, REDDIT_CLIENT_SECRET);
        String response = accessToken.getAsJsonObject().toString();
        sendResponse(ex, response);
        ex.close();
      } catch (Exception e) {
        System.out.println("Exception: " + e);
        e.printStackTrace();
      }
    }
  }

  /**
   * Integerates User Reddit account with user.
   */
  private class TwitterSignInHandler implements HttpHandler {
    /**
     * Integerate user's Reddit account with PrBot
     * 
     * @param ex Incoming request
     * @throws IOException Incapable of reading landing page HTML file
     */
    @Override
    public void handle(HttpExchange ex) {
      AccessTokenInfo accessToken = new AccessTokenInfo(null, null);
      try {
        Headers header = ex.getResponseHeaders();
        header.add("Content-Type", String.format("application/json; charset=%s", StandardCharsets.UTF_8));
        initHeaders(header);
        if (!doAPIKeysExist(TWITTER_CLIENT_ID, TWITTER_CLIENT_SECRET, ex)) {
          System.out.println("Please first define Twitter client id and secret");
          System.out.printf("Twitter client id: %s, Twitter client secret: %s\n", TWITTER_CLIENT_ID,
              TWITTER_CLIENT_SECRET);
          return;
        }
        accessToken = OAuthHandler.authorizeTwitter(TWITTER_CLIENT_ID, TWITTER_CLIENT_SECRET);
        String response = accessToken.getAsJsonObject().toString();
        sendResponse(ex, response);
        ex.close();
      } catch (Exception e) {
        System.out.println("Exception: " + e);
        e.printStackTrace();
      }
    }
  }

  private class RedditSubmitPostHandler implements HttpHandler {
    /**
     * Submit a post to Reddit
     * 
     * @param ex Incoming request
     * @throws IOException Incapable of reading landing page HTML file
     */
    @Override
    public void handle(HttpExchange ex) {
      System.out.println("Submit post");
      String query = ex.getRequestURI().getQuery();
      AccessTokenInfo accessTokenInfo = new AccessTokenInfo(Requester.getQueryValue(query, "access_token"), null);
      String title = Requester.getQueryValue(query, "title");
      String message = Requester.getQueryValue(query, "message");
      String channel = null;

      try {
        Headers header = ex.getResponseHeaders();
        header.add("Content-Type", String.format("application/json; charset=%s", StandardCharsets.UTF_8));
        initHeaders(header);
        if (!doAPIKeysExist(REDDIT_CLIENT_ID, REDDIT_CLIENT_SECRET, ex)) {
          System.out.println("Please first define reddit client id and secret");
          System.out.printf("Reddit client id: %s, Reddit client secret: %s\n", REDDIT_CLIENT_ID, REDDIT_CLIENT_SECRET);
          return;
        }
        if (accessTokenInfo.getAccessToken() == null || title == null || message == null) {
          String response = buildErrorJsonObject(REDDIT_SUBMIT_QUERY_ERROR).toString();
          sendResponse(ex, response);
          ex.close();
          return;
        }
        RedditMessagePoster messagePoster = new RedditMessagePoster();
        ResponseFormatter postResponse = messagePoster.postMessage(accessTokenInfo, title, message, channel);
        System.out.println("Post response: " + postResponse);
        JsonObject postResponseJson = postResponse.getAsJsonObject();
        String response = postResponseJson.toString();
        System.out.println("The response to send is " + response);
        sendResponse(ex, response);
        ex.close();
      } catch (Exception e) {
        System.out.println("Exception: " + e);
        e.printStackTrace();
        // not send anything purposely
        // frontend(browsers) makes second request after a timeout(possibly working
        // then)
      }
    }
  }

  private class EmailSubmitPostHandler implements HttpHandler {
    /**
     * Submit an email to list of given destination email
     * 
     * @param ex Incoming request
     * @throws IOException Incapable of reading landing page HTML file
     */
    @Override
    public void handle(HttpExchange ex) {
      String smtpHost = "host";
      int smtpPort = 587;
      boolean useTls = false;
      System.out.println("Submit post");
      String query = ex.getRequestURI().getQuery();
      String emailFrom = Requester.getQueryValue(query, "emailFrom");
      String emailPassword = Requester.getQueryValue(query, "password");
      String subject = Requester.getQueryValue(query, "title");
      String text = Requester.getQueryValue(query, "message");
      Set<String> emailTo = new HashSet<>();
      emailTo.add("danimuneye@gmail.com");
      Email mail = new Email(smtpHost, smtpPort, useTls, emailFrom, emailTo, emailPassword);
      try {
        Headers header = ex.getResponseHeaders();
        header.add("Content-Type", String.format("application/json; charset=%s", StandardCharsets.UTF_8));
        initHeaders(header);
        ResponseFormatter postResponse = mail.sendEmail(subject, text);
        System.out.println("email response: " + postResponse);
        JsonObject postResponseJson = postResponse.getAsJsonObject();
        String response = postResponseJson.toString();
        System.out.println("The response to send is " + response);
        sendResponse(ex, response);
        ex.close();
      } catch (Exception e) {
        System.out.println("Exception: " + e);
        e.printStackTrace();
        // not send anything purposely
        // frontend(browsers) makes second request after a timeout(possibly working
        // then)
      }
    }
  }
  final int SERVER_PORT = 1338;

  private HttpServer server;
  private RedditSignInHandler redditSignInHandler;
  private TwitterSignInHandler twitterSignInHandler;
  private RedditSubmitPostHandler redditSubmitPostHandler;
  private EmailSubmitPostHandler emailSubmitPostHandler;
  private String REDDIT_CLIENT_ID = System.getenv("REDDIT_CLIENT_ID");
  private String REDDIT_CLIENT_SECRET = System.getenv("REDDIT_CLIENT_SECRET");
  private String TWITTER_CLIENT_ID = System.getenv("TWITTER_CLIENT_ID");
  private String TWITTER_CLIENT_SECRET = System.getenv("TWITTER_CLIENT_SECRET");
  private String REDDIT_SUBMIT_QUERY_ERROR = "Signing in, title for the post, and the posted message are required";
  private String ERROR_KEY = "error";

  /**
   * Generate a RedirectServer, and initialize the landing pages
   * 
   * @throws IOException Invalid server port given.
   */
  public GatewayServer() {
    try {
      server = HttpServer.create(new InetSocketAddress(SERVER_PORT), 0);
      redditSignInHandler = new RedditSignInHandler();
      twitterSignInHandler = new TwitterSignInHandler();
      redditSubmitPostHandler = new RedditSubmitPostHandler();
      emailSubmitPostHandler = new EmailSubmitPostHandler();
      server.createContext("/redditsignin", redditSignInHandler);
      server.createContext("/twittersignin", twitterSignInHandler);
      server.createContext("/redditsubmitpost", redditSubmitPostHandler);
      server.createContext("/emailsubmitpost", emailSubmitPostHandler);
      server.setExecutor(null);
      startUp();
    } catch (Exception e) {
      System.out.println("Exception: " + e);
      e.printStackTrace();
    }

  }

  public void sendResponse(HttpExchange ex, String response) {
    try {
      OutputStream out = ex.getResponseBody();
      ex.sendResponseHeaders(200, response.getBytes().length);
      out.write(response.getBytes(StandardCharsets.UTF_8));
    } catch (Exception e) {
      System.out.println("Exception: " + e);
    }
  }

  /**
   * Sends internal error message if the client id and secret has not been
   * initalized.
   * 
   * @param clientId     the client id of the respective platform
   * @param clientSecret the client secret of the respective platform
   * @param ex           the exchange object.
   * @return true if the id and secret were initialized
   */
  public boolean doAPIKeysExist(String clientId, String clientSecret, HttpExchange ex) {
    boolean apiKeysExist = true;
    if (clientId == null || clientSecret == null) {
      apiKeysExist = false;
      String response = buildErrorJsonObject("Internal Error: Please try again later").toString();
      sendResponse(ex, response);
      ex.close();

    }

    return apiKeysExist;
  }

  /**
   * Builds a given message with a propert 'error' set to the given message
   * 
   * @param message the error message
   * @return JSON object containing the error
   */
  public JsonObject buildErrorJsonObject(String message) {
    JsonObject object = new JsonObject();
    object.addProperty(ERROR_KEY, message);
    return object;
  }

  /**
   * All required headers will be initalized here.
   * 
   * @param header the header object from the request.
   */
  public void initHeaders(Headers header) {
    // allow frontend to make request to this server
    header.add("Access-Control-Allow-Origin", "http://localhost:3000");
  }

  /**
   * Start the gateway Server.
   */
  public void startUp() {
    try {
      server.start();
    } catch (Exception e) {
      System.out.println("Failed to start Gateway server!");
      e.printStackTrace();
    }
  }

  public static void main(String args[]) {
    System.out.println("Trying to start server");
    GatewayServer server = new GatewayServer();
  }
}
