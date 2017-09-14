
package radonsoft.mireaassistant.model.schedule;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Odd {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("type")
    @Expose
    private Object type;
    @SerializedName("teacher")
    @Expose
    private Object teacher;
    @SerializedName("room")
    @Expose
    private Object room;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getType() {
        return type;
    }

    public void setType(Object type) {
        this.type = type;
    }

    public Object getTeacher() {
        return teacher;
    }

    public void setTeacher(Object teacher) {
        this.teacher = teacher;
    }

    public Object getRoom() {
        return room;
    }

    public void setRoom(Object room) {
        this.room = room;
    }

}
