
package radonsoft.mireaassistant.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Group {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("institute")
    @Expose
    private int institute;
    @SerializedName("group")
    @Expose
    private String group;
    @SerializedName("secret")
    @Expose
    private String secret;
    @SerializedName("last_update")
    @Expose
    private int lastUpdate;
    @SerializedName("__v")
    @Expose
    private int v;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getInstitute() {
        return institute;
    }

    public void setInstitute(int institute) {
        this.institute = institute;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public int getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(int lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }

}
