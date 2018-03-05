package com.zhjirui.okhttp.callback;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by 18513 on 2017/12/13.
 */

public interface HttpLisenter {
    void onFailure(Call call, IOException e);
    void onSuccess(Call call, Response response, String json);
    void onException(Call call, int code);
}
