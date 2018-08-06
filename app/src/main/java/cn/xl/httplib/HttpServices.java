package cn.xl.httplib;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * author: wenshenghui
 * created on: 2018/8/2 11:58
 * description:
 */
public interface HttpServices {
    /* common interface */

    @POST("{path}")
    Observable<retrofit2.Response<String>> post(@Path("path") String path, @QueryMap Map<String, String> params);

    @POST("{path}")
    Observable<retrofit2.Response<String>> post(@Path("path") String path, @Body RequestBody requestBody);

    @POST("{path}")
    Observable<retrofit2.Response<String>> post(@Path("path") String path, @QueryMap Map<String, String> params, @Body RequestBody requestBody);

    @POST("{path}")
    Observable<retrofit2.Response<String>> post(@Path("path") String path, @Header("Authorization") String header, @QueryMap Map<String, String> params);

    @POST("{path}")
    Observable<retrofit2.Response<String>> post(@Path("path") String path, @Header("Authorization") String header, @Body RequestBody requestBody);

//    @POST("{path}")
//    Observable<String> post(@Path("path") String path, @QueryMap Map<String, String> params1, @Body Map<String, String> params2);

    @POST("{path}")
    Observable<retrofit2.Response<String>> uploadFile(@Path("path") String path,  @Body MultipartBody multipartBody);


    /* difinite interface */

    @GET("{path}/appphotos")
    Observable<retrofit2.Response<String>> getPhotos(@Path("path") String path, @Header("Authorization") String header, @QueryMap Map<String, String> params);

    @GET("orders/{path}")
    Observable<retrofit2.Response<String>> get(@Path("path") String path);

    @GET("{path}")
    Observable<retrofit2.Response<String>> get(@Path("path") String path, @Header("Authorization") String header);

    @GET("{path}")
    Observable<retrofit2.Response<String>> get(@Path("path") String path, @Header("Authorization") String header, @QueryMap Map<String, String> params);

    @GET("{path}")
    Observable<retrofit2.Response<String>> get(@Path("path") String path, @Header("Authorization") Map<String, String> header, @QueryMap Map<String, String> params);


    @POST("{path}/faces")
    Observable<retrofit2.Response<String>> uploadFacesFile(@Path("path") String path,  @Header("Authorization") String header, @Body MultipartBody multipartBody);

    @POST("{path}")
    Observable<retrofit2.Response<String>> uploadFile(@Path("path") String path,  @Header("Authorization") String header, @Body MultipartBody multipartBody);

    /* DEFINITE_INTERFACE_MSJK_UPLOAD_PHOTO_SET */
    @POST("{path}/appphotos")
    Observable<retrofit2.Response<String>> uploadPhotoSet(@Path("path") String path,  @Header("Authorization") String header, @Body RequestBody requestBody);

    @POST("contactbook/add")
    Observable<retrofit2.Response<String>> uploadContact(@Header("Authorization") String header, @Body RequestBody requestBody);

    @POST
    Observable<retrofit2.Response<String>> rxJavaPost(
            @Url String url,
            @HeaderMap Map<String, String> headers,
            @Body Object jsonParams
    );
}
