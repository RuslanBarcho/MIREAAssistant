
package radonsoft.mireaassistant.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestWrapper {

    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("response")
    @Expose
    private Response response;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

}
