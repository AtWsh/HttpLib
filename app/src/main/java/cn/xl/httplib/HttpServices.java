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
    Observable<retrofit2.Response<String>> post(@Path("path") String path);

    @POST("{path}")
    Observable<retrofit2.Response<String>> postWithParamsMap(@Path("path") String path, @QueryMap Map<String, String> params);

    @POST("{path}")
    Observable<retrofit2.Response<String>> post(@Path("path") String path, @Body Object requestBody);

    @POST("{path}")
    Observable<retrofit2.Response<String>> post(@Path("path") String path, @Header("Authorization") String header);

    @POST("{path}")
    Observable<retrofit2.Response<String>> postWithHeaderMap(@Path("path") String path, @HeaderMap Map<String, String> headers);

    @POST("{path}")
    Observable<retrofit2.Response<String>> post(@Path("path") String path, @QueryMap Map<String, String> params, @Body Object requestBody);

    @POST("{path}")
    Observable<retrofit2.Response<String>> post(@Path("path") String path, @QueryMap Map<String, String> params, @Header("Authorization") String header);

    @POST("{path}")
    Observable<retrofit2.Response<String>> post(@Path("path") String path, @QueryMap Map<String, String> params, @HeaderMap Map<String, String> headers);

    @POST("{path}")
    Observable<retrofit2.Response<String>> post(@Path("path") String path, @Body Object requestBody, @Header("Authorization") String header);

    @POST("{path}")
    Observable<retrofit2.Response<String>> post(@Path("path") String path, @Body Object requestBody, @HeaderMap Map<String, String> headers);

    @POST("{path}")
    Observable<retrofit2.Response<String>> post(@Path("path") String path, @QueryMap Map<String, String> params, @Body Object requestBody, @Header("Authorization") String header);

    @POST("{path}")
    Observable<retrofit2.Response<String>> post(@Path("path") String path, @QueryMap Map<String, String> params, @Body Object requestBody, @HeaderMap Map<String, String> headers);


    //get
    @GET("orders/{path}")
    Observable<retrofit2.Response<String>> get(@Path("path") String path);

    @GET("{path}")
    Observable<retrofit2.Response<String>> getWithParamsMap(@Path("path") String path, @QueryMap Map<String, String> params);

    @GET("{path}")
    Observable<retrofit2.Response<String>> get(@Path("path") String path, @Header("Authorization") String header);

    @GET("{path}")
    Observable<retrofit2.Response<String>> getWithHeaderMap(@Path("path") String path, @HeaderMap Map<String, String> header);

    @GET("{path}")
    Observable<retrofit2.Response<String>> get(@Path("path") String path, @QueryMap Map<String, String> params, @Header("Authorization") String header);

    @GET("{path}")
    Observable<retrofit2.Response<String>> get(@Path("path") String path, @QueryMap Map<String, String> params, @HeaderMap Map<String, String> header);


    //上传
    @POST("{path}")
    Observable<retrofit2.Response<String>> uploadFile(@Path("path") String path,  @Body MultipartBody multipartBody);

    @GET("{path}/appphotos")
    Observable<retrofit2.Response<String>> getPhotos(@Path("path") String path, @Header("Authorization") String header, @QueryMap Map<String, String> params);

    @POST("{path}/faces")
    Observable<retrofit2.Response<String>> uploadFacesFile(@Path("path") String path,  @Header("Authorization") String header, @Body MultipartBody multipartBody);

    @POST("{path}")
    Observable<retrofit2.Response<String>> uploadFile(@Path("path") String path,  @Header("Authorization") String header, @Body MultipartBody multipartBody);

    /* DEFINITE_INTERFACE_MSJK_UPLOAD_PHOTO_SET */
    @POST("{path}/appphotos")
    Observable<retrofit2.Response<String>> uploadPhotoSet(@Path("path") String path,  @Header("Authorization") String header, @Body RequestBody requestBody);

    @POST("contactbook/add")
    Observable<retrofit2.Response<String>> uploadContact(@Header("Authorization") String header, @Body RequestBody requestBody);
}
