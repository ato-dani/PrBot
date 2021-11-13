import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.oauth.StatefulAuthHelper;

import java.awt.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.security.SecureRandom;
import java.util.HashMap;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.util.Key;
import com.google.common.reflect.TypeToken;

/**
 * Handles the complicated OAuth2 login processes using a dummy webserver.
 */
public class OAuthHandler {

    private static Credentials credentials = Credentials.webapp(System.getenv("REDDIT_CLIENT_ID"),
            System.getenv("REDDIT_CLIENT_SECRET") , "http://localhost:1337/redditauth");
    private static NetworkAdapter networkAdapter = new OkHttpNetworkAdapter(
            new UserAgent("prBotCs321", "com.example.Prbot", "v0.1", "prbot"));
    private static StatefulAuthHelper helper = OAuthHelper.interactive(networkAdapter, credentials);
    private static Desktop desktop = Desktop.getDesktop();
    private static RedirectServer server = null;

    /**
     * Allows to change any dependencies used by the authorizeReddit method. Created
     * to implement the dependency inversion principle.
     */
    public static void setAuthorizeRedditDependencies(RedirectServer newServer, NetworkAdapter newNetworkAdapter,
                                                      StatefulAuthHelper newHelper, Desktop newDesktop) {
        server = newServer;
        networkAdapter = newNetworkAdapter;
        helper = newHelper;
        desktop = newDesktop;
    }
    /**
     * Login to twitter via OAuth2, and get the login access token and secret
     * based on this: https://twitter4j.org/en/code-examples.html
     *
     * @param apiKey        Twitter developer API key.
     * @param apiSecretKey  Twitter developer API secret.
     * @return              true if successful.
     * @throws Exception    RedirectServer threw an exception.
     */
    public static AccessTokenInfo authorizeTwitter(String apiKey, String apiSecretKey) throws Exception {
        AccessTokenInfo accessTokenInfo = new AccessTokenInfo(null, null, null);
        RedirectServer server = new RedirectServer();
        server.startUp();
        Twitter twitter = TwitterFactory.getSingleton();
        try{
            twitter.setOAuthConsumer(apiKey, apiSecretKey);
        }
        catch(IllegalStateException e){
            twitter = (new TwitterFactory()).getInstance();
            twitter.setOAuthConsumer(apiKey, apiSecretKey);
        }
        RequestToken requestToken = twitter.getOAuthRequestToken();
        try{
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(new URI(requestToken.getAuthorizationURL()));
        }
        catch(Exception e){
            //todo: print url so user can copy+paste
        }
        while(server.getTwitterResponse() == null){
            //do nothing and wait while the user finishes up in their browser
            Thread.sleep(1);
        }
        //this is hideous, but works
        //this essentially saves the value of the second key-value pair in the URL
        String oAuthVerifier = server.getTwitterResponse().split("\\?")[1].split("\\&")[1].split("=")[1];
        server.shutDown();

        try {
            AccessToken accessToken = twitter.getOAuthAccessToken(oAuthVerifier);
            accessTokenInfo = new AccessTokenInfo(accessToken.getToken(), accessToken.getTokenSecret(), null);
            System.out.println("Access token: " + accessToken.getToken());
            System.out.println("Access token secret: " + accessToken.getTokenSecret());
        } catch (Exception e) {
            System.out.println("user has declined");
        }
        //todo: actually save these values
        return accessTokenInfo;
    }

    /**
     * Login to Reddit via OAuth2, and get the login access token
     *
     * @param clientId      Reddit developer API client ID.
     * @param clientSecret  Reddit developer API client secret.
     * @return              true if successful.
     * @throws Exception    RedirectServer threw an exception.
     */
    public static AccessTokenInfo authorizeReddit(String clientId, String clientSecret) throws Exception {
        AccessTokenInfo accessTokenInfo = new AccessTokenInfo(null, null);
        if (server == null) {
            System.out.println("Server is null: " + server);
            server = new RedirectServer();
        }
        server.startUp();
        // System.out.println("Generate Request Url2 Called");
        String authUrl = helper.getAuthorizationUrl(true, false, "submit", "identity");
        try {
            desktop.browse(new URI(authUrl));
        } catch (Exception e) {
            System.out.println("Error: " + e);
            e.printStackTrace();
        }
        while (server.getRedditResponse() == null) {
            // do nothing and wait while the user finishes up in their browser
            Thread.sleep(1);
        }
        // System.out.println("Reddit response is " + server.getRedditResponse());
        URI url = new URI(server.getRedditFinalUrl());
        server.shutDown();

        if (server.redditAuthorizationHasError(url)) {
            server = null;
            // user declined
            // Send empty token or something
            return new AccessTokenInfo(null, null);
        }
        server = null;
        try {
            RedditClient reddit = helper.onUserChallenge(url.toString());
            System.out.println("out of main status: " + helper.getAuthStatus());
            // TODO: Save the access token in some way possible a cookie
            accessTokenInfo = new AccessTokenInfo(reddit.getAuthManager().getAccessToken(),
                    reddit.getAuthManager().getCurrent().getExpiration().toString());
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        return accessTokenInfo;
    }

    //Discord json response
    public static class DiscordTokenResponse {
        public static class Webhook {
            @Key("id")
            public String id;
            @Key("token")
            public String token;

            public String getToken() {
                return token;
            }

            public String getId() {
                return id;
            }

            public String toString() {
                return ("id: " + id + " token: " + token);
            }
        }

        @Key("webhook")
        public Webhook webhook;

        public Webhook getWebhook() {
            return webhook;
        }
    }

    /**
     * Login to Discord via OAuth2, and get the login access token
     *
     * @param clientId     Discord developer API client ID.
     * @param clientSecret Discord developer API client secret.
     * @return AccessTokenInfo with token if successful.
     * @throws Exception RedirectServer threw an exception.
     */
    public static AccessTokenInfo authorizeDiscord(String clientId, String clientSecret) throws Exception {
        String baseUrl = "http://localhost:1337";
        String discordPath = "/discordauth";
        String tokenUrl = "https://discord.com/api/oauth2/token";
        AccessTokenInfo accessTokenInfo = new AccessTokenInfo(null, null);
        if (server == null) {
            System.out.println("Server is null: " + server);
            server = new RedirectServer();
        }
        server.startUp();
        // System.out.println("Generate Request Url2 Called");
        String authUrl = "https://discord.com/oauth2/authorize?client_id=903414150628274227&redirect_uri=http://localhost:1337/discordauth&response_type=code&scope=webhook.incoming";
        try {
            desktop.browse(new URI(authUrl));
        } catch (Exception e) {
            System.out.println("Error: " + e);
            e.printStackTrace();
        }
        while (server.getDiscordResponse() == null) {
            // do nothing and wait while the user finishes up in their browser
            Thread.sleep(1);
        }
        URI url = new URI(baseUrl + server.getDiscordResponse());

        server.shutDown(); //Instance variable so shutdown so other servies can use

        //Reused since its the same for discord and reddit
        if (server.redditAuthorizationHasError(url)) {
            server = null;
            // user declined
            return new AccessTokenInfo(null, null, null); //User declined authorization
        }

        server = null;
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("client_id", clientId);
        parameters.put("client_secret", clientSecret);
        parameters.put("grant_type", "authorization_code");
        parameters.put("code", Requester.getQueryValue(url.getQuery(), "code"));
        parameters.put("redirect_uri", baseUrl + discordPath);
        HttpHeaders headers = new HttpHeaders();
        headers = headers.set("Content-Type", "application/x-www-form-urlencoded"); //Doesn't accept JSON type for auth
        Requester requester = new Requester();
        Type discordTokenResponseType = new TypeToken<DiscordTokenResponse>() {
        }.getType();
        DiscordTokenResponse response = (DiscordTokenResponse) requester.makePOSTRequest(tokenUrl, parameters, null,
                headers, discordTokenResponseType, false);
        System.out.println("Webhook: " + response.getWebhook()); //Test to see what webhook is
        accessTokenInfo = new AccessTokenInfo(response.getWebhook().getToken(), response.getWebhook().getId(), null);
        return accessTokenInfo;
    }

    public static void main(String args[]) {
    }
}
