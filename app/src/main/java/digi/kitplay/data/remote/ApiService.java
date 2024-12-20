package digi.kitplay.data.remote;

import java.util.List;

import digi.kitplay.data.model.api.ResponseListObj;
import digi.kitplay.data.model.api.ResponseWrapper;
import digi.kitplay.data.model.api.response.CheckUpdateResponse;

import digi.kitplay.data.model.api.response.CommentTest;
import digi.kitplay.data.model.api.response.PostTest;
import digi.kitplay.data.model.api.response.SocketResponse;
import digi.kitplay.data.model.api.response.VerifyTokenResponse;
import digi.kitplay.data.socket.dto.Device;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ApiService {
    @GET()
    Observable<CheckUpdateResponse> checkUpdate(@Url String url);

    @POST("/v1/device/verify-token")
    @Headers({"IgnoreAuth: 1"})
    Observable<ResponseWrapper<VerifyTokenResponse>> verifyToken(@Header("Authorization") String authHeader);

    @POST("/v1/device/check-device-id")
    @Headers({"IgnoreAuth:1"})
    Observable<ResponseWrapper<SocketResponse>> checkDeviceId(@Body Device device);

    @GET("/posts")
    @Headers({"IgnoreAuth:1"})
    Observable<List<PostTest>> getPosts();

    @GET("/posts/1")
    @Headers({"IgnoreAuth:1"})
    Observable<PostTest> getPost();

    @GET("/posts/1/comments")
    @Headers({"IgnoreAuth:1"})
    Observable<List<CommentTest>> getComments();
}
