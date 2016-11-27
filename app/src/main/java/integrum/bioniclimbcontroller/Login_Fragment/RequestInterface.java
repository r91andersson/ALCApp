package integrum.bioniclimbcontroller.Login_Fragment;

/**
 * Created by Robin on 2016-11-03.
 */
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import integrum.bioniclimbcontroller.Login_Fragment.ServerRequest;
import integrum.bioniclimbcontroller.Login_Fragment.ServerResponse;

public interface RequestInterface {

    @POST("/")
    Call<ServerResponse> operation(@Body ServerRequest request);

}
