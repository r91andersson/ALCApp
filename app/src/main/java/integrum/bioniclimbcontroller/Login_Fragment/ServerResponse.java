package integrum.bioniclimbcontroller.Login_Fragment;

import integrum.bioniclimbcontroller.User.User;

/**
 * Created by Robin on 2016-11-03.
 */
public class ServerResponse {

    private String result;
    private String message;
    private User user;

    public String getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }
}
