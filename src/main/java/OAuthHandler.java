import com.slack.api.Slack;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.awt.*;
import java.net.URI;

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

    public static boolean authorizeSlack(String apiKey, String apiSecretKey) throws Exception{
        //todo: everything
        return false;
    }


}
