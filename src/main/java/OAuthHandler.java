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
import java.net.URI;
import java.security.SecureRandom;

/**
 * Handles the complicated OAuth2 login processes using a dummy webserver.
 */
public class OAuthHandler {
    private static RedirectServer server = new RedirectServer();
    private static Credentials credentials = Credentials.webapp(System.getenv("REDDIT_CLIENT_ID"),
            System.getenv("REDDIT_CLIENT_SECRET"), server.getRedditBaseUrl());
    private static NetworkAdapter networkAdapter = new OkHttpNetworkAdapter(
            new UserAgent("prBotCs321", "com.example.Prbot", "v0.1", "prbot"));
    private static StatefulAuthHelper helper = OAuthHelper.interactive(networkAdapter, credentials);
    private static Desktop desktop = Desktop.getDesktop();

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
            // user declined
            // Send empty token or something
            return new AccessTokenInfo(null, null);
        }
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
}
