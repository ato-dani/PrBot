/**
 * Holds the response information(the success status and message for any
 * failures or errors)
 */
public class ResponseFormatter {
  private boolean successStatus;
  private String message;

  public ResponseFormatter(boolean successStatus, String message) {
    this.successStatus = successStatus;
    this.message = message;
  }

  public boolean getSuccessStatus() {
    return successStatus;
  }

  public String getMessage() {
    return message;
  }
}
