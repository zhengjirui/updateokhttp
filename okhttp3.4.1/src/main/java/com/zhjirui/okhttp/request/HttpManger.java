package com.zhjirui.okhttp.request;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.zhjirui.okhttp.Utils.Utiles;
import com.zhjirui.okhttp.callback.HttpLisenter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by 18513 on 2017/12/13.
 */

public class HttpManger {
    private final String TAG = "HttpManger";

    //http://blog.csdn.net/qq_31694651/article/details/52254188

    ////构建字符串请求体
    public static final MediaType TEXT = MediaType.parse("text/plain; charset=utf-8");

    // //构建字节请求体和构建文件请求体
    public static final MediaType STREAM = MediaType.parse("application/octet-stream");

    //post上传json
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    private static HttpManger httpManger = null;
    private OkHttpClient mHttpClient = new OkHttpClient();
    private HttpLisenter httpLisenter = null;
    private MyCall myCall = null;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (httpLisenter != null) {
                MyCall call = (MyCall) msg.obj;

                switch (msg.what) {
                    case 0:
                        try {
                            JSONObject jsonObject = new JSONObject(call.getJson());
                            if (call.getResponse().isSuccessful()) {
//                                JSONObject reJson = jsonObject.getJSONObject();
                                String code = jsonObject.getString("code");
                                if (code.equalsIgnoreCase("1000")) {
                                    JSONObject data = jsonObject.getJSONObject("data");
                                    httpLisenter.onSuccess(call.getCall(), call.getResponse(), data.toString());
                                }

                            } else {
                                httpLisenter.onException(call.getCall(), call.getResponse().code());
                            }
                        } catch (JSONException e) {
                            httpLisenter.onException(call.getCall(), -999);
                            Utiles.showToast("服务器异常");
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                        httpLisenter.onFailure(call.getCall(), call.getE());
                        break;
                }
            } else {
                throw new NullPointerException();
            }
        }
    };

    private HttpManger() {
    }

    public static HttpManger getHttpInstance() {
        if (httpManger == null) {
            httpManger = new HttpManger();
        }
        return httpManger;
    }

    public void cancelAllRequest() {
        if (mHttpClient != null) {
            mHttpClient.dispatcher().cancelAll();
        }
    }

    private RequestBody createRequestBody(MediaType JSON, String json) {
        RequestBody body = RequestBody.create(JSON, json);
        return body;
    }

    private Request createRequest(String url, MediaType type, String json) {
        Request request = new Request.Builder()
                .url(url)
//                .method("POST" ,this.createRequestBody(type,json))
                .post(this.createRequestBody(type, json))
                .build();
        return request;
    }

    private void enqueue(final Request request) {
        cancelAllRequest();
        mHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                myCall = new MyCall(call, e);
                Message ms = new Message();
                ms.what = 1;
                ms.obj = myCall;
                handler.sendMessage(ms);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                myCall = new MyCall(call, response, json);
                Message ms = new Message();
                ms.what = 0;
                ms.obj = myCall;
                handler.sendMessage(ms);
            }
        });
    }

    public void sendPostJsonRequest(String url, String json, HttpLisenter httpLisenter) {
        if (url != null && !url.equalsIgnoreCase("")
                && json != null && !json.equalsIgnoreCase("")) {
            this.httpLisenter = httpLisenter;
            Request request = createRequest(url, JSON, json);
            enqueue(request);
        } else {
            Log.e(TAG, "请检查url和json");
        }
    }

    class MyCall {
        private Call call = null;

        private IOException e = null;

        private Response response = null;

        private String json = "";

        public MyCall(Call call, IOException e) {
            this.call = call;
            this.e = e;
        }

        public MyCall(Call call, Response response, String json) {
            this.call = call;
            this.response = response;
            this.json = json;
        }

        public Call getCall() {
            return call;
        }

        public void setCall(Call call) {
            this.call = call;
        }

        public IOException getE() {
            return e;
        }

        public void setE(IOException e) {
            this.e = e;
        }

        public Response getResponse() {
            return response;
        }

        public void setResponse(Response response) {
            this.response = response;
        }

        public String getJson() {
            return json;
        }

        public void setJson(String json) {
            this.json = json;
        }
    }

}
