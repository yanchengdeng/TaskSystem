package com.task.system.api;

//接口静态类

import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.yc.lib.api.ApiCallBack;
import com.yc.lib.api.ApiCallBackList;
import com.yc.lib.api.ApiConfig;
import com.yc.lib.api.ResultListInfo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 接口
 */
public class API {


    /**
     * 实体对象请求
     *
     * @param call
     * @param clz
     * @param apiCallBack
     * @param <T>
     */
    public static <T> void getObject(Call<TaskInfo> call, final Class clz, final ApiCallBack<T> apiCallBack) {
        if (call == null) {
            throw new IllegalArgumentException("call不能为空");
        }

        if (apiCallBack == null) {
            throw new IllegalArgumentException("缺少ApiCallBack()回调");
        }

        call.enqueue(new Callback<TaskInfo>() {
            @Override
            public void onResponse(Call<TaskInfo> call, Response<TaskInfo> response) {
                showRequestParams(response);
                if (apiCallBack != null) {
                    TaskInfo resultInfo = response.body();
                    if (resultInfo != null) {
                        if (resultInfo.getStatus_code() == ApiConfig.successCode) {
                            //查询成功
                            if (resultInfo.getData() != null) {
                                try {
                                    //防止服务器中的字符串不严谨 ，这做一次转化 解决：com.google.gson.stream.MalformedJsonException
                                    String json = new Gson().toJson(resultInfo.getData());
                                    apiCallBack.onSuccess(resultInfo.getStatus_code(), resultInfo.getMessage(), (T) new Gson().fromJson(json, clz));
                                } catch (Exception e) {
                                    LogUtils.w("dyc", "json 数据格式异常");
                                }
                            } else {
                                apiCallBack.onFaild(resultInfo.getStatus_code(), resultInfo.getMessage() + ":data 无数据");
                            }
                        } else {
                            apiCallBack.onFaild(resultInfo.getStatus_code(), resultInfo.getMessage());
                        }
                    } else {
                        if (response != null) {
                            showResponseErrorInfo(apiCallBack, response);
                        }
                        apiCallBack.onFaild(resultInfo.getStatus_code(), resultInfo.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<TaskInfo> call, Throwable t) {
                showErroResponse(call, t);
                if (NetworkUtils.isConnected()) {
                    apiCallBack.onFaild(-1, t.getMessage());
                } else {
                    apiCallBack.onFaild(-1, "请检查网络");
                }
            }
        });
    }


    /**
     * 集合数据请求
     *
     * @param call
     * @param clz
     * @param apiCallBack
     * @param <T>
     */
    public static <T> void getList(Call<TaskInfoList> call, final Class clz, final ApiCallBackList<T> apiCallBack) {
        if (apiCallBack == null) {
            throw new IllegalArgumentException("缺少ApiCallBack()回调");
        }

        call.enqueue(new Callback<TaskInfoList>() {
            @Override
            public void onResponse(Call<TaskInfoList> call, Response<TaskInfoList> response) {
                showRequestParams(response);
                if (apiCallBack != null) {
                    TaskInfoList resultInfo = response.body();
                    if (resultInfo != null) {
                        if (resultInfo.getStatus_code() == ApiConfig.successCode) {
                            //查询成功
                            if (resultInfo.getData() != null) {
                                String json = new Gson().toJson(resultInfo.getData());
                                try {
                                    apiCallBack.onSuccess(
                                            resultInfo.getStatus_code(),
                                            resultInfo.getMessage(),
                                            (List<T>) API.jsonStringConvertToList(json, clz));
                                } catch (Exception e) {
                                    LogUtils.w("dyc", "json 数据格式异常");
                                }
                            } else {
                                apiCallBack.onFaild(resultInfo.getStatus_code(), resultInfo.getMessage() + ":data 无数据");
                            }
                        } else {
                            apiCallBack.onFaild(resultInfo.getStatus_code(), resultInfo.getMessage());
                        }
                    } else {
                        if (response != null) {
                            showResponseErrorInfo(apiCallBack, response);
                        }
                        apiCallBack.onFaild(resultInfo.getStatus_code(), resultInfo.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<TaskInfoList> call, Throwable t) {
                showErroResponse(call, t);
                if (NetworkUtils.isConnected()) {
                    apiCallBack.onFaild(-1, t.getMessage());
                } else {
                    apiCallBack.onFaild(-1, "请检查网络");
                }
            }
        });


    }

    /**
     * 打印响应错误信息
     *
     * @param call
     * @param t
     */
    private static void showErroResponse(Call call, Throwable t) {
        if (ApiConfig.getDebug()) {
            Log.d(ApiConfig.LogFilter, call.toString() + "--error：" + t.getMessage());
        }
    }


    /**
     * 打印请求信息 ， 接口返回结果
     *
     * @param response
     */
    private static void showRequestParams(Response response) {
        if (response == null) {
            Log.e(ApiConfig.LogFilter, "response返回响应值为空");
            return;
        }

        if (ApiConfig.getDebug()) {
            Log.w(ApiConfig.LogFilter, String.format("接口请求:url---->  %s", response.raw().request().url().toString()));
            if (response.raw() != null) {
                if (response.raw().request().body() != null) {
                    RequestBody body = response.raw().request().body();
                    Buffer requestBuffer = new Buffer();
                    try {
                        body.writeTo(requestBuffer);
                        Log.w(ApiConfig.getLogFilter(), postParseParams(body, requestBuffer));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (response.body() != null) {
                if (response.body() instanceof ResultListInfo) {
                    Log.d(ApiConfig.LogFilter, String.format("接口返回信息：code-->%s message:-->%s", ((ResultListInfo) response.body()).getStatus_code(), ((ResultListInfo) response.body()).getMessage()));
                } else if (response.body() instanceof TaskInfo) {
                    Log.d(ApiConfig.LogFilter, String.format("接口返回信息：code-->%s message:-->%s", ((TaskInfo) response.body()).getStatus_code(), ((TaskInfo) response.body()).getMessage()));
                    String result = "暂无数据";
                    if (((TaskInfo) response.body()).getData() != null) {
                        result = ((TaskInfo) response.body()).getData().toString();
                    }
                    Log.w(ApiConfig.LogFilter, String.format("接口返回结果：%s", result));
                }
            } else {
                Log.e(ApiConfig.LogFilter, String.format("接口返回信息：code-->%s message:-->%s", response.code(), response.message()));
            }
        }
    }

    /**
     * 请求错误信息
     */
    private static void showResponseErrorInfo(ApiCallBackList apiCallBack, Response response) {
        if (response.body() != null) {
            if (ApiConfig.getDebug()) {
                Log.e(ApiConfig.LogFilter, String.format("接口返回信息：code-->%s message:-->%s", response.code(), response.message()));
            }
        }
        if (NetworkUtils.isConnected()) {
            apiCallBack.onFaild(response.code(), response.message());
        } else {
            apiCallBack.onFaild(response.code(), "请检查网络");
        }
    }

    /**
     * 响应错误信息
     */
    private static void showResponseErrorInfo(ApiCallBack apiCallBack, Response response) {
        if (response.body() != null) {
            if (ApiConfig.getDebug()) {
                Log.e(ApiConfig.LogFilter, String.format("接口返回信息：code-->%s message:-->%s", response.code(), response.message()));
            }
        }
        if (NetworkUtils.isConnected()) {
            apiCallBack.onFaild(response.code(), response.message());
        } else {
            apiCallBack.onFaild(response.code(), "请检查网络");
        }
    }


    /**
     * gson字符串转化成集合
     *
     * @param jsonString
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> List<T> jsonStringConvertToList(String jsonString, Class<T> cls) {
        List<T> list = new ArrayList<T>();
        try {
            Gson gson = new Gson();
            JsonArray arry = new JsonParser().parse(jsonString).getAsJsonArray();
            for (JsonElement jsonElement : arry) {
                list.add(gson.fromJson(jsonElement, cls));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (ApiConfig.getDebug()) {
            Log.w(ApiConfig.LogFilter, String.format("接口返回结果：%s", jsonString));
        }
        return list;
    }


    private static String postParseParams(RequestBody body, Buffer requestBuffer) throws UnsupportedEncodingException {
        if (body.contentType() != null && !body.contentType().toString().contains("multipart")) {
            return URLDecoder.decode(requestBuffer.readUtf8(), "UTF-8");
        }
        return "null";
    }
}
