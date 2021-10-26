import com.google.gson.JsonObject;
/**
 * Holds the access token information(the access token and the time it expires
 * at)
 */
public class AccessTokenInfo {
  private String accessToken;
  private String expiresIn;
  private String accessTokenSecret;

  public AccessTokenInfo(String accessToken, String expiresIn) {
    this.accessToken = accessToken;
    this.expiresIn = expiresIn;
    this.accessTokenSecret = null;
  }

  public AccessTokenInfo(String accessToken, String accessTokenSecret, String expiresIn) {
    this.accessToken = accessToken;
    this.expiresIn = expiresIn;
    this.accessTokenSecret = accessTokenSecret;
  }
  public String getAccessToken() {
    return this.accessToken;
  }
  public String getExpiresIn() {
    return this.expiresIn;
  }
  public String getAccessTokenSecret() {
    return this.accessTokenSecret;
  }
  public JsonObject getAsJsonObject() {
    JsonObject object = new JsonObject();
    object.addProperty("access_token", this.accessToken);
    object.addProperty("access_token_secret", this.accessTokenSecret);
    object.addProperty("expires_in", this.expiresIn);
    return object;
  }
}
