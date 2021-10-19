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

    /**
     * Login to twitter via OAuth2, and get the login access token and secret
     * based on this: https://twitter4j.org/en/code-examples.html
     *
     * @param apiKey        Twitter developer API key.
     * @param apiSecretKey  Twitter developer API secret.
     * @return              true if successful.
     * @throws Exception    RedirectServer threw an exception.
     */
    public static boolean authorizeTwitter(String apiKey, String apiSecretKey) throws Exception {
        RedirectServer server = new RedirectServer();
        server.startUp();
        Twitter twitter = TwitterFactory.getSingleton();
        twitter.setOAuthConsumer(apiKey, apiSecretKey);
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
        AccessToken accessToken = twitter.getOAuthAccessToken(oAuthVerifier);
        System.out.println("Access token: " + accessToken.getToken());
        System.out.println("Access token secret: " + accessToken.getTokenSecret());
        //todo: actually save these values
        return true;
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
        RedirectServer server = new RedirectServer();
        server.startUp();
        // System.out.println("Generate Request Url2 Called");
        Credentials credentials = Credentials.webapp(clientId, clientSecret, server.getRedditBaseUrl());
        UserAgent userAgent = new UserAgent("prBotCs321", "com.example.Prbot", "v0.1", "prbot");
        NetworkAdapter networkAdapter = new OkHttpNetworkAdapter(userAgent);
        final StatefulAuthHelper helper = OAuthHelper.interactive(networkAdapter, credentials);
        String authUrl = helper.getAuthorizationUrl(true, false, "submit", "identity");
        try {
            Desktop desktop = Desktop.getDesktop();
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

    /**
     * Generate a nonce (random string of bytes)
     * based on: https://mkyong.com/java/java-how-to-generate-a-random-12-bytes/
     * @param numBytes Length of nonce in bytes
     * @return Hex-encoded nonce string.
     */
    private static String generateNonce(int numBytes){
        byte[] nonce = new byte[numBytes];
        new SecureRandom().nextBytes(nonce);
        StringBuilder sb = new StringBuilder();
        for(byte b : nonce){
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }


}
