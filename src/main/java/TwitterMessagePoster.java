import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterMessagePoster implements MessagePoster{
    AccessTokenInfo devToken = null;
    AccessTokenInfo loginToken = null;
    TwitterFactory twitterFactory = null;

    public TwitterMessagePoster(AccessTokenInfo loginToken){
        try{
            this.loginToken = loginToken;
            devToken = new AccessTokenInfo(System.getenv("TWITTER_CLIENT_ID"), System.getenv("TWITTER_CLIENT_SECRET"), null);
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setOAuthConsumerKey(devToken.getAccessToken())
                    .setOAuthConsumerSecret(devToken.getAccessTokenSecret())
                    .setOAuthAccessToken(loginToken.getAccessToken())
                    .setOAuthAccessTokenSecret(loginToken.getAccessTokenSecret());
            twitterFactory = new TwitterFactory(cb.build());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public Status tweet(String message) throws TwitterException{
        if(twitterFactory == null){
            throw new TwitterException("TwitterMessagePoster.twitterFactory is null.");
        }
        else {
            Twitter twitter = twitterFactory.getInstance();
            return twitter.updateStatus(message);
        }
    }

    public ResponseFormatter postMessage(AccessTokenInfo accessTokenInfo, String title, String message, String channel){
        if(loginToken == null){
            return new ResponseFormatter(false, "Twitter never configured!");
        }
        else {
            try {
                Status status = tweet(message);
                return new ResponseFormatter(true, "Successfully tweeted: " + status.getText());
            } catch (TwitterException e) {
                return new ResponseFormatter(false, "Failed to post twitter status!");
            }
        }
    }
}
