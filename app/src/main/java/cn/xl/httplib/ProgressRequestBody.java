package cn.xl.httplib;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;

public class ProgressRequestBody extends RequestBody implements Handler.Callback {

    private static final String TAG = "xxx";
    private Handler mUiHandler;
    private RequestBody mBody;

    private HttpCallBack mProgressListener;
    private long mContentLength;
    private long mProgress;

    public ProgressRequestBody(RequestBody body) {
        mUiHandler = new Handler(Looper.getMainLooper(), this);
        this.mBody = body;
    }

    @Override
    public MediaType contentType() {
        return mBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return mBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink bufferedSink) throws IOException {
        mContentLength = mBody.contentLength();
        BufferedSink sink = Okio.buffer(new ForwardingSink(bufferedSink) {
            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (byteCount > 0) {
                    mProgress += byteCount;
                    mUiHandler.obtainMessage().sendToTarget();
                }
            }
        });
        mBody.writeTo(sink);
        sink.flush();
        sink.close();
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (mProgressListener != null) {
            mProgressListener.onProgress((int) (100 * mProgress / mContentLength));
        }
        return true;
    }

    public void setProgressListener(HttpCallBack progressListener) {
        this.mProgressListener = progressListener;
    }
}
