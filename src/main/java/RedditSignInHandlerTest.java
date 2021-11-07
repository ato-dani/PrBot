import java.util.Date;
import java.awt.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.*;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.models.OAuthData;
import net.dean.jraw.oauth.AuthManager;
import net.dean.jraw.oauth.StatefulAuthHelper;

@RunWith(MockitoJUnitRunner.class)
public class RedditSignInHandlerTest {
  @Mock
  RedirectServer server;
  @Mock
  NetworkAdapter networkAdapter;
  @Mock
  StatefulAuthHelper helper;
  @Mock
  Desktop desktop;
  @Mock
  RedditClient redditClient;
  @Mock
  AuthManager authManager;
  @Mock
  OAuthData oAuthData;
  AccessTokenInfo accessTokenInfo;
  private String token = "425698524640-pHO4dhYiXU-1rIPMMmt-YL8BeJuhFQ";

  /**
   * Called before every test and reinitialize the values needed for testing
   * reddit message posting feature.
   */
  @Before
  public void setupTest() {
    OAuthHandler.setAuthorizeRedditDependencies(server, networkAdapter, helper, desktop);
    accessTokenInfo = new AccessTokenInfo(token, null);
  }

  @Test
  public void testSuccessfulSignIn() {
    // mock the value we get when posting successful message to reddit
    // Stub client id and secret
    String clientId = "xxxsdlfjdsfjkdsljfdsxxxxxxxxx";
    String clientSecret = "xdfsvsdfsdfvdsvsdfdsfdsfsdf";
    AccessTokenInfo accessToken = new AccessTokenInfo(null, null);
    Date date = new Date();
    String redditResponseUrl = "http://localhost:1337/redditauth?state=15mvbslqn85r88pnv3ij69nkgn&code=17Q1U9O4tmsdrAKfvCLI_-jpFdvz1w#";
    doNothing().when(server).startUp();
    // mock the calls we get from the dependencies so as to test the logic without
    // actually opening up browser or making api request.
    Mockito.when(helper.getAuthorizationUrl(true, false, "submit", "identity")).thenReturn(redditResponseUrl);
    Mockito.when(server.getRedditResponse()).thenReturn(redditResponseUrl);
    Mockito.when(server.getRedditFinalUrl()).thenReturn(redditResponseUrl);
    Mockito.when(helper.onUserChallenge(any())).thenReturn(redditClient);
    doNothing().when(server).shutDown();
    Mockito.when(redditClient.getAuthManager()).thenReturn(authManager);
    Mockito.when(authManager.getAccessToken()).thenReturn(accessTokenInfo.getAccessToken());
    Mockito.when(authManager.getCurrent()).thenReturn(oAuthData);
    Mockito.when(oAuthData.getExpiration()).thenReturn(date);
    try {
      accessToken = OAuthHandler.authorizeReddit(clientId, clientSecret);
    } catch (Exception e) {
      System.out.println("Exception while testing: " + e);
      e.printStackTrace();
      System.exit(1);
    }
    assertTrue(
        "Failed testSuccessfulSignIn: Expected Date: " + date.toString() + " but got " + accessToken.getExpiresIn(),
        date.toString().equals(accessToken.getExpiresIn()));
    assertTrue("Failed testSuccessfulSuccessfulSignIn: Expected access token: " + token + " but got "
        + accessToken.getAccessToken(), token.equals(accessToken.getAccessToken()));

  }

  @Test
  public void testUnsuccessfulSignIn() {
    // mock the value we get when posting successful message to reddit
    // Stub client id and secret
    String clientId = "xxxsdlfjdsfjkdsljfdsxxxxxxxxx";
    String clientSecret = "xdfsvsdfsdfvdsvsdfdsfdsfsdf";
    AccessTokenInfo accessToken = new AccessTokenInfo(null, null);
    String redditResponseUrl = "http://localhost:1337/redditauth?error=denied";
    doNothing().when(server).startUp();
    // mock the calls we get from the dependencies so as to test the logic without
    // actually opening up browser or making api request.
    Mockito.when(helper.getAuthorizationUrl(true, false, "submit", "identity")).thenReturn(redditResponseUrl);
    Mockito.when(server.getRedditResponse()).thenReturn(redditResponseUrl);
    Mockito.when(server.getRedditFinalUrl()).thenReturn(redditResponseUrl);
    Mockito.when(server.redditAuthorizationHasError(any())).thenReturn(true);
    doNothing().when(server).shutDown();
    try {
      accessToken = OAuthHandler.authorizeReddit(clientId, clientSecret);
    } catch (Exception e) {
      System.out.println("Exception while testing: " + e);
      e.printStackTrace();
      System.exit(1);
    }
    String expectedDate = null;
    String expectedToken = null;
    assertTrue("Failed testSuccessfulSignIn: Expected Date: " + expectedDate + " but got " + accessToken.getExpiresIn(),
        expectedDate == accessToken.getExpiresIn());
    assertTrue("Failed testSuccessfulSuccessfulSignIn: Expected access token: " + expectedToken + " but got "
        + accessToken.getAccessToken(), expectedToken == accessToken.getAccessToken());

  }

  public static void main(String args[]) {
    org.junit.runner.JUnitCore.main("RedditSignInHandlerTest");
  }
}
