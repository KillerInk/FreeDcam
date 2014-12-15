/*
 * Copyright 2014 Sony Corporation
 */

package com.troop.freedcam.sonyapi.sonystuff;

import android.util.Log;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Simple Camera Remote API wrapper class. (JSON based API <--> Java API)
 */
public class SimpleRemoteApi {

    private static final String TAG = SimpleRemoteApi.class.getSimpleName();

    // If you'd like to suppress detailed log output, change this value into
    // false.
    private static final boolean FULL_LOG = true;

    // API server device you want to send requests.
    private ServerDevice mTargetServer;

    // Request ID of API calling. This will be counted up by each API calling.
    private int mRequestId;

    /**
     * Constructor.
     * 
     * @param target server device of Remote API
     */
    public SimpleRemoteApi(ServerDevice target) {
        mTargetServer = target;
        mRequestId = 1;
    }

    /**
     * Retrieves Action List URL from Server information.
     * 
     * @param service
     * @return
     * @throws java.io.IOException
     */
    private String findActionListUrl(String service) throws IOException {
        List<ServerDevice.ApiService> services = mTargetServer.getApiServices();
        for (ServerDevice.ApiService apiService : services) {
            if (apiService.getName().equals(service)) {
                return apiService.getActionListUrl();
            }
        }
        throw new IOException("actionUrl not found. service : " + service);
    }

    /**
     * Request ID. Counted up after calling.
     *
     * @return
     */
    private int id() {
        return mRequestId++;
    }

    // Output a log line.
    private void log(String msg) {
        if (FULL_LOG) {
            Log.d(TAG, msg);
        }
    }

    // Camera Service APIs

    /**
     * Calls getAvailableApiList API to the target server. Request JSON data is
     * such like as below.
     *
     * <pre>
     * {
     *   "method": "getAvailableApiList",
     *   "params": [""],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws java.io.IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject getAvailableApiList() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getAvailableApiList")
                            .put("params", new JSONArray()).put("id", id())
                            .put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls getApplicationInfo API to the target server. Request JSON data is
     * such like as below.
     *
     * <pre>
     * {
     *   "method": "getApplicationInfo",
     *   "params": [""],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws java.io.IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject getApplicationInfo() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getApplicationInfo") //
                            .put("params", new JSONArray()).put("id", id()) //
                            .put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls getShootMode API to the target server. Request JSON data is such
     * like as below.
     *
     * <pre>
     * {
     *   "method": "getShootMode",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws java.io.IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject getShootMode() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getShootMode").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls setShootMode API to the target server. Request JSON data is such
     * like as below.
     *
     * <pre>
     * {
     *   "method": "setShootMode",
     *   "params": ["still"],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @param shootMode shoot mode (ex. "still")
     * @return JSON data of response
     * @throws java.io.IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject setShootMode(String shootMode) throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "setShootMode") //
                            .put("params", new JSONArray().put(shootMode)) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls getAvailableShootMode API to the target server. Request JSON data
     * is such like as below.
     *
     * <pre>
     * {
     *   "method": "getAvailableShootMode",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response

     */
    public JSONObject getAvailableShootMode() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getAvailableShootMode") //
                            .put("params", new JSONArray()).put("id", id()) //
                            .put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls getSupportedShootMode API to the target server. Request JSON data
     * is such like as below.
     *
     * <pre>
     * {
     *   "method": "getSupportedShootMode",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws java.io.IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject getSupportedShootMode() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getSupportedShootMode") //
                            .put("params", new JSONArray()).put("id", id()) //
                            .put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls startLiveview API to the target server. Request JSON data is such
     * like as below.
     *
     * <pre>
     * {
     *   "method": "startLiveview",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws java.io.IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject startLiveview() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "startLiveview").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls stopLiveview API to the target server. Request JSON data is such
     * like as below.
     *
     * <pre>
     * {
     *   "method": "stopLiveview",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws java.io.IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject stopLiveview() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "stopLiveview").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls startRecMode API to the target server. Request JSON data is such
     * like as below.
     *
     * <pre>
     * {
     *   "method": "startRecMode",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws java.io.IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject startRecMode() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "startRecMode").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls stopRecMode API to the target server. Request JSON data is such
     * like as below.
     *
     * <pre>
     * {
     *   "method": "stopRecMode",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws java.io.IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject stopRecMode() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "stopRecMode").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls actTakePicture API to the target server. Request JSON data is such
     * like as below.
     *
     * <pre>
     * {
     *   "method": "actTakePicture",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws java.io.IOException
     */
    public JSONObject actTakePicture() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "actTakePicture").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls startMovieRec API to the target server. Request JSON data is such
     * like as below.
     *
     * <pre>
     * {
     *   "method": "startMovieRec",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws java.io.IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject startMovieRec() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "startMovieRec").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls stopMovieRec API to the target server. Request JSON data is such
     * like as below.
     *
     * <pre>
     * {
     *   "method": "stopMovieRec",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws java.io.IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject stopMovieRec() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "stopMovieRec").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls actZoom API to the target server. Request JSON data is such like as
     * below.
     *
     * <pre>
     * {
     *   "method": "actZoom",
     *   "params": ["in","stop"],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @param direction direction of zoom ("in" or "out")
     * @param movement zoom movement ("start", "stop", or "1shot")
     * @return JSON data of response
     * @throws java.io.IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject actZoom(String direction, String movement) throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "actZoom") //
                            .put("params", new JSONArray().put(direction).put(movement)) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls getEvent API to the target server. Request JSON data is such like
     * as below.
     *
     * <pre>
     * {
     *   "method": "getEvent",
     *   "params": [true],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @param longPollingFlag true means long polling request.
     * @return JSON data of response
     * @throws java.io.IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject getEvent(boolean longPollingFlag) throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getEvent") //
                            .put("params", new JSONArray().put(longPollingFlag)) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;
            int longPollingTimeout = (longPollingFlag) ? 20000 : 8000; // msec

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString(),
                    longPollingTimeout);
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls setCameraFunction API to the target server. Request JSON data is
     * such like as below.
     *
     * <pre>
     * {
     *   "method": "setCameraFunction",
     *   "params": ["Remote Shooting"],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @param cameraFunction camera function to set
     * @return JSON data of response
     * @throws java.io.IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject setCameraFunction(String cameraFunction) throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "setCameraFunction") //
                            .put("params", new JSONArray().put(cameraFunction)) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls getMethodTypes API of Camera service to the target server. Request
     * JSON data is such like as below.
     *
     * <pre>
     * {
     *   "method": "getMethodTypes",
     *   "params": ["1.0"],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws java.io.IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject getCameraMethodTypes() throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getMethodTypes") //
                            .put("params", new JSONArray().put("")) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    // Avcontent APIs

    /**
     * Calls getMethodTypes API of AvContent service to the target server.
     * Request JSON data is such like as below.
     *
     * <pre>
     * {
     *   "method": "getMethodTypes",
     *   "params": ["1.0"],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws java.io.IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject getAvcontentMethodTypes() throws IOException {
        String service = "avContent";
        try {
            String url = findActionListUrl(service) + "/" + service;
            JSONObject requestJson =
                    new JSONObject().put("method", "getMethodTypes") //
                            .put("params", new JSONArray().put("")) //
                            .put("id", id()).put("version", "1.0"); //

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls getSchemeList API to the target server. Request JSON data is such
     * like as below.
     *
     * <pre>
     * {
     *   "method": "getSchemeList",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws java.io.IOException all errors and exception are wrapped by this
     *             Exception.
     */

    public JSONObject getSchemeList() throws IOException {
        String service = "avContent";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getSchemeList").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls getSourceList API to the target server. Request JSON data is such
     * like as below.
     *
     * <pre>
     * {
     *   "method": "getSourceList",
     *   "params": [{
     *      "scheme": "storage"
     *      }],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @param scheme target scheme to get source
     * @return JSON data of response
     * @throws java.io.IOException all errors and exception are wrapped by this
     *             Exception.
     */

    public JSONObject getSourceList(String scheme) throws IOException {
        String service = "avContent";
        try {

            JSONObject params = new JSONObject().put("scheme", scheme);
            JSONObject requestJson =
                    new JSONObject().put("method", "getSourceList") //
                            .put("params", new JSONArray().put(0, params)) //
                            .put("version", "1.0").put("id", id());

            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls getContentList API to the target server. Request JSON data is such
     * like as below.
     *
     * <pre>
     * {
     *   "method": "getContentList",
     *   "params": [{
     *      "sort" : "ascending"
     *      "view": "date"
     *      "uri": "storage:memoryCard1"
     *      }],
     *   "id": 2,
     *   "version": "1.3"
     * }
     * </pre>
     *
     * @param params request JSON parameter of "params" object.
     * @return JSON data of response
     * @throws java.io.IOException all errors and exception are wrapped by this
     *             Exception.
     */

    public JSONObject getContentList(JSONArray params) throws IOException {
        String service = "avContent";
        try {

            JSONObject requestJson =
                    new JSONObject().put("method", "getContentList").put("params", params) //
                            .put("version", "1.3").put("id", id());

            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls setStreamingContent API to the target server. Request JSON data is
     * such like as below.
     *
     * <pre>
     * {
     *   "method": "setStreamingContent",
     *   "params": [
     *      "remotePlayType" : "simpleStreaming"
     *      "uri": "image:content?contentId=01006"
     *      ],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @param uri streaming contents uri
     * @return JSON data of response
     * @throws java.io.IOException all errors and exception are wrapped by this
     *             Exception.
     */

    public JSONObject setStreamingContent(String uri) throws IOException {
        String service = "avContent";
        try {

            JSONObject params = new JSONObject().put("remotePlayType", "simpleStreaming").put(
                    "uri", uri);
            JSONObject requestJson =
                    new JSONObject().put("method", "setStreamingContent") //
                            .put("params", new JSONArray().put(0, params)) //
                            .put("version", "1.0").put("id", id());

            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls startStreaming API to the target server. Request JSON data is such
     * like as below.
     *
     * <pre>
     * {
     *   "method": "startStreaming",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws java.io.IOException all errors and exception are wrapped by this
     *             Exception.
     */

    public JSONObject startStreaming() throws IOException {
        String service = "avContent";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "startStreaming").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0").put("id", id());
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    /**
     * Calls stopStreaming API to the target server. Request JSON data is such
     * like as below.
     *
     * <pre>
     * {
     *   "method": "stopStreaming",
     *   "params": [],
     *   "id": 2,
     *   "version": "1.0"
     * }
     * </pre>
     *
     * @return JSON data of response
     * @throws java.io.IOException all errors and exception are wrapped by this
     *             Exception.
     */

    public JSONObject stopStreaming() throws IOException {
        String service = "avContent";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "stopStreaming").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    // static method

    /**
     * Parse JSON and return whether it has error or not.
     * 
     * @param replyJson JSON object to check
     * @return return true if JSON has error. otherwise return false.
     */
    public static boolean isErrorReply(JSONObject replyJson) {
        boolean hasError = (replyJson != null && replyJson.has("error"));
        return hasError;
    }

    public JSONObject setParameterToCamera(String parameter, JSONArray valueToSet) throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", parameter) //
                            .put("params", valueToSet) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    public JSONObject getParameterFromCamera(String valueToGet) throws IOException {
        String service = "camera";
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", valueToGet) //
                            .put("params", new JSONArray()).put("id", id()) //
                            .put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson.toString());
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }
}
