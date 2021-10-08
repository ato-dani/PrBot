/**
 * Holds the access token information(the access token and the time it expires
 * at)
 */
public class AccessTokenInfo {
  private String accessToken;
  private String expiresIn;

  public AccessTokenInfo(String accessToken, String expiresIn) {
    this.accessToken = accessToken;
    this.expiresIn = expiresIn;
  }

  public String getAccessToken() {
    return this.accessToken;
  }

  public String getExpiresIn() {
    return this.expiresIn;
  }
}
