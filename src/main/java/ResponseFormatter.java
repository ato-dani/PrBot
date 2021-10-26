import com.google.gson.JsonObject;

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
    return this.successStatus;
  }

  public String getMessage() {
    return this.message;
  }

  public JsonObject getAsJsonObject() {
    JsonObject object = new JsonObject();
    object.addProperty("success_status", this.successStatus);
    object.addProperty("message", this.message);
    return object;
  }
}
