public interface MessagePoster {
  /**
   * Posts the given message by making a post request to the platform. It returns
   * containin
   * 
   * @param accessToken user or bot access token.
   * @param title       title of the message: required by some platforms like
   *                    reddit.
   * @param message     text message to be posted.
   * @param channel     if there is some specfic channel to post to like discord
   *                    server.
   * @return a response formatter which contains the success status and message if
   *         there was error.
   */
  public ResponseFormatter postMessage(String accessToken, String title, String message, String channel);
}
