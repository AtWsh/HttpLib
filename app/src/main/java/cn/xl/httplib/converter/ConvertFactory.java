package cn.xl.httplib.converter;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import cn.xl.httplib.HttpConstants;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * author: wenshenghui
 * created on: 2018/8/3 17:49
 * description:
 */
public class ConvertFactory extends Converter.Factory {

    private Gson gson;

    public ConvertFactory(Gson g) {
        gson = g;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new ConvertFactory.ToStringConverter();
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations,
                                                          Annotation[] methodAnnotations, Retrofit retrofit) {
        return new ConvertFactory.RequestConverter();
    }

    private final class RequestConverter implements Converter<Object, RequestBody> {
        @Override
        public RequestBody convert(Object o) throws IOException {
            if (o instanceof String) {
                return RequestBody.create(HttpConstants.sJsonType, (String) o);
            } else if (o instanceof RequestBody) {
                return (RequestBody) o;
            } else {
                return RequestBody.create(HttpConstants.sJsonType, gson.toJson(o));
            }
        }
    }

    private static final class ToStringConverter implements Converter<ResponseBody, String> {
        @Override
        public String convert(@NonNull ResponseBody value) {
            try {
                String data = value.string();
                value.close();
                return data;
            } catch (IOException e) {
                return "";
            }
        }
    }
}
