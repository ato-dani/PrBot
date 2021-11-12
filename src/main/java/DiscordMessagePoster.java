import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Type;

import com.google.api.client.util.Key;
import com.google.common.reflect.TypeToken;

public class DiscordMessagePoster implements MessagePoster {


    public static class DiscordTokenResponse {
        @Key
        public Boolean success;

        public Boolean getSuccess() {
            return success;
        }
    }

    private final String API_ENDPOINT = "https://discord.com/api/v8";
    private final String SUBMIT_POST_API = API_ENDPOINT + "/webhooks";


    public ResponseFormatter postMessage(AccessTokenInfo accessTokenInfo, String title, String message, String channel) {

        if(message == null){
            return new ResponseFormatter(false, "A message needs to be filled in for Discord!");
        }

        Requester requester = new Requester();
        Type discordTokenResponseType = new TypeToken<DiscordTokenResponse>() {
        }.getType();
        String discordToken = accessTokenInfo.getAccessToken();
        String discordIdSecret = accessTokenInfo.getAccessTokenSecret();
        String finalPostApi = SUBMIT_POST_API + "/" + discordIdSecret + "/" + discordToken;

        //Discord doesn't require title or channel using webhooks.
        Map<String, String> data = new HashMap<>();
        data.put("content", message);
        DiscordTokenResponse response = (DiscordTokenResponse) requester.makePOSTRequest(finalPostApi, data,
                null, null, discordTokenResponseType, true);

        if( response!= null && response.getSuccess()){
            return new ResponseFormatter(true, "Discord post submitted successfully!");
        } else {
            return new ResponseFormatter(false, "Discord was not successful. Please, reintegrate.");
        }
    }

    //This was used to test posting. Keep in for now until done final delivery.
/*
    public static void main(String[] args){
            AccessTokenInfo newTokenTest = new AccessTokenInfo("V4CrbgeMruou8EE4qx7zZ2VUys0R-3uD4A6Jw4gCGhQ6xCKEwa-BdcOUAh9JvFrkEFzY", "908832177704296538", null);
            DiscordMessagePoster discordPoster = new DiscordMessagePoster();
            String content = "This is a discord test";
            discordPoster.postMessage(newTokenTest, " ", content, null);


    }*/
}
