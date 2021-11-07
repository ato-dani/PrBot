import java.lang.reflect.Type;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.*;

@RunWith(MockitoJUnitRunner.class)
public class RedditMessagePosterTest {
  @Mock
  Requester requester;
  AccessTokenInfo accessTokenInfo;
  RedditMessagePoster redditMessagePoster;
  RedditMessagePoster.RedditSubmitPostResponse redditSubmitPostResponse;

  /**
   * Called before every test and reinitialize the values needed for testing
   * reddit message posting feature.
   */
  @Before
  public void setupTest() {
    accessTokenInfo = new AccessTokenInfo("425698524640-pHO4dhYiXU-1rIPMMmt-YL8BeJuhFQ", null);
    redditMessagePoster = new RedditMessagePoster();
    redditSubmitPostResponse = new RedditMessagePoster.RedditSubmitPostResponse();
  }

  @Test
  public void testSuccessfulPost() {
    // mock the value we get when posting successful message to reddit
    redditSubmitPostResponse.success = true;
    Mockito.when(requester.makePOSTRequest(any(), any(), any(), any(), any(), anyBoolean()))
        .thenReturn(redditSubmitPostResponse);
    redditMessagePoster.requester = requester;
    ResponseFormatter responseFormatter = redditMessagePoster.postMessage(accessTokenInfo,
        "Testing PrBot Reddit integeration", "This is a message", null);
    String expectedMessage = "Post submitted successfully!";
    String outputMessage = responseFormatter.getMessage();
    assertEquals(true, responseFormatter.getSuccessStatus());
    assertTrue("Failed testSuccessfulPost: Expected " + expectedMessage + " but got " + outputMessage,
        expectedMessage.equals(outputMessage));
  }

  @Test
  public void testUnsuccessfulPost() {
    // mock the value we get when posting unsuccessful message to reddit
    redditSubmitPostResponse.success = false;
    Mockito.when(requester.makePOSTRequest(any(), any(), any(), any(), any(), anyBoolean()))
        .thenReturn(redditSubmitPostResponse);
    redditMessagePoster.requester = requester;
    ResponseFormatter responseFormatter = redditMessagePoster.postMessage(accessTokenInfo,
        "Testing PrBot Reddit integeration", "This is a message", null);
    String expectedMessage = "Post was not successful. Please, reintegrate reddit.";
    String outputMessage = responseFormatter.getMessage();
    assertEquals(false, responseFormatter.getSuccessStatus());
    assertTrue("Failed testSuccessfulPost: Expected " + expectedMessage + " but got " + outputMessage,
        expectedMessage.equals(outputMessage));
  }

  public static void main(String args[]) {
    org.junit.runner.JUnitCore.main("RedditMessagePosterTest");
  }
}
