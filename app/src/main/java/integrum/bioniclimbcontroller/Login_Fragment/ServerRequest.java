package integrum.bioniclimbcontroller.Login_Fragment;

import integrum.bioniclimbcontroller.User.User;

/**
 * Created by Robin on 2016-11-03.
 */
public class ServerRequest {

    private String operation;
    private User user;

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
