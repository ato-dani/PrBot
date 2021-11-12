import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.lang.reflect.Type;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.Key;
import com.google.common.reflect.TypeToken;

public class RedditMessagePoster implements MessagePoster {
  public static class RedditResponseType {
    public static class Data {
      @Key
      public String sr;
      @Key("comment_karma")
      public Integer commentKarma;
      @Key("link_karma")
      public Integer linkKarma;

      public String getSr() {
        return sr;
      }

      public Integer getCommentKarma() {
        return commentKarma;
      }

      public Integer getLinkKarma() {
        return linkKarma;
      }
    }

    @Key
    public String kind;
    @Key
    public List<Data> data;

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("kind: " + kind);
      for (Data entry : data) {
        builder.append("Subreddit: " + entry.sr);
      }
      return builder.toString();
    }
  }

  public static class RedditSubmitPostResponse {
    @Key
    public Boolean success;

    public Boolean getSuccess() {
      return success;
    }
  }

  public static class RedditIdentityResponse {
    @Key
    public String name;

    public String getName() {
      return name;
    }
  }

  private final String API = "https://oauth.reddit.com";
  private final String SUBMIT_POST_API = API + "/api/submit.json";
  private final String IDENTITY_API = API + "/api/v1/me";
  private final String KARMA_API = API + "/api/v1/me/karma";
  private HttpRequestFactory requestFactory;

  public HttpRequestFactory getRequestFactory(String token) {
    if (requestFactory == null) {
      System.out.println("Setting request factory");
      requestFactory = (new NetHttpTransport()).createRequestFactory(new HttpRequestInitializer() {
        @Override
        public void initialize(HttpRequest request) {
          request.getHeaders().setAuthorization("bearer " + token);
        }
      });
    }
    return requestFactory;
  }

  // A sample on how to make the http request using makeGetRequestV2 and parsing
  // response
  public void sampleRequest(AccessTokenInfo accessTokenInfo) {
    Requester requester = new Requester();
    // shows what fields there are on the returned json object, so that it parses
    // the fields
    Type responseType = new TypeToken<RedditResponseType>() {
    }.getType();
    Map<String, String> parameters = new HashMap<>();
    parameters.put("user", "somename");
    requester.setReqFactory(getRequestFactory(accessTokenInfo.getAccessToken()));
    requester.makeGETRequest(KARMA_API, parameters, null, null);
    // The response is parsed for use since we told it what fields to expect
    RedditResponseType response = (RedditResponseType) requester.makeGETRequest(KARMA_API, null, null, responseType);
  }

  public String getUserName(AccessTokenInfo accessTokenInfo) {
    String username = "";
    Requester requester = new Requester();
    Type identityResponseType = new TypeToken<RedditIdentityResponse>() {
    }.getType();
    requester.setReqFactory(getRequestFactory(accessTokenInfo.getAccessToken()));
    RedditIdentityResponse response = (RedditIdentityResponse) requester.makeGETRequest(IDENTITY_API, null, null,
        identityResponseType);
    username = response == null ? username : response.getName();
    return username;
  }

  public ResponseFormatter postMessage(AccessTokenInfo accessTokenInfo, String title, String message, String channel) {
    Requester requester = new Requester();
    Type submitPostResponseType = new TypeToken<RedditSubmitPostResponse>() {
    }.getType();
    requester.setReqFactory(getRequestFactory(accessTokenInfo.getAccessToken()));
    Map<String, String> parameters = new HashMap<>();
    parameters.put("sr", "u_" + getUserName(accessTokenInfo));
    parameters.put("title", title);
    parameters.put("text", message);
    parameters.put("kind", "self");
    Map<String, String> data = new HashMap<>();
    data.put("resubmit", "true");
    data.put("content", message);
    data.put("send_replies", "true");
    data.put("api_type", "json");
    RedditSubmitPostResponse response = (RedditSubmitPostResponse) requester.makePOSTRequest(SUBMIT_POST_API, data,
        parameters, null, submitPostResponseType, true);
    if (response != null && response.getSuccess()) {
      // no errors all succeeded
      return new ResponseFormatter(true, "Post submitted successfully!");
    } else {
      return new ResponseFormatter(false, "Post was not successful. Please, reintegrate reddit.");
    }

  }

  public static void main(String args[]) {
    //  Used to test it. Didn't remove this because it is used an example and Toke
    //  has expired so doesn't work anymore(no security issue) 
    AccessTokenInfo testAccessToken = new AccessTokenInfo("425698524640-pHO4dhYiXU-1rIPMMmt-YL8BeJuhFQ", null);
    RedditMessagePoster redditPoster = new RedditMessagePoster();
    String title = "Making API post";
    String message = "I will tell you how to make api post using reddit API";
    String channel = null;
    redditPoster.postMessage(testAccessToken, title, message, channel);
    // redditPoster.sampleRequest(testAcessToken);
  }
}
