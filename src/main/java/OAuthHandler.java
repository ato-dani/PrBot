import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.awt.*;
import java.net.URI;
import java.security.SecureRandom;

public class OAuthHandler {

    //based on this: https://twitter4j.org/en/code-examples.html
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

    public static boolean authorizeReddit(String apiKey, String apiSecretKey) throws Exception{
        RedirectServer server = new RedirectServer();
        server.startUp();
        String nonce = generateNonce(16);
        String authURL = "https://www.reddit.com/api/v1/authorize?client_id=" + apiKey + "&response_type=TYPE&state=" +
                nonce + "&redirect_uri=http://127.0.0.1:1337/redditauth&duration=permanent&scope=edit,read,save,submit";
        try{
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(new URI(authURL));
        }
        catch(Exception e){
            //todo: print url so user can copy+paste
        }
        while(server.getRedditResponse() == null){
            //do nothing and wait while the user finishes up in their browser
            Thread.sleep(1);
        }
        System.out.println("DEBUG:" + server.getRedditResponse());
        server.shutDown();
        return false;
    }

    //https://mkyong.com/java/java-how-to-generate-a-random-12-bytes/
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
