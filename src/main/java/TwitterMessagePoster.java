import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class TwitterMessagePoster implements MessagePoster{
    AccessTokenInfo loginToken = null;

    public TwitterMessagePoster(AccessTokenInfo devToken){
        try{
            loginToken = OAuthHandler.authorizeTwitter(devToken.getAccessToken(), devToken.getAccessTokenSecret());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public Status tweet(String message) throws TwitterException{
        Twitter twitter = TwitterFactory.getSingleton();
        return twitter.updateStatus(message);
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
