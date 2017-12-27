/*
 * Copyright 2014 Sony Corporation
 */

package freed.cam.apis.sonyremote.sonystuff;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import freed.cam.apis.sonyremote.sonystuff.ServerDevice.ApiService;
import freed.utils.Log;

/**
 * Simple Camera Remote API cameraUiWrapper class. (JSON based API <--> Java API)
 */
public class SimpleRemoteApi {

    private static final String TAG = SimpleRemoteApi.class.getSimpleName();

    // If you'd like to suppress detailed log output, change this value into
    // false.
    private static final boolean FULL_LOG = false;

    // API server device you want to send requests.
    private final ServerDevice mTargetServer;

    // Request ID of API calling. This will be counted up by each API calling.
    private int mRequestId;

    public static final String SYSTEM= "system";
    private static final String AVCONTENT = "avContent";
    private static final String CAMERA = "camera";
    private static final String GUIDE = "guide";
    public static final String ACCESSCONTROL = "accessControl";

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
     * @throws IOException
     */
    private String findActionListUrl(String service) throws IOException {
        List<ApiService> services = mTargetServer.getApiServices();
        for (ApiService apiService : services) {
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

    private JSONObject executeGetMethod(String service, String method)throws IOException
    {
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", method)
                            .put("params", new JSONArray())
                            .put("id", id())
                            .put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson);
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }


    private JSONObject executeSetMethod(String service, String method, String mode) throws IOException
    {
        return executeSetMethod(service,method, new JSONArray().put(mode));
    }

    private JSONObject executeSetMethod(String service, String method, JSONArray array) throws IOException
    {
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", method) //
                            .put("params", array) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson);
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
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
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject getAvailableApiList() throws IOException {
        String service = CAMERA;
        return executeGetMethod(service,"getAvailableApiList");
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
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject getApplicationInfo() throws IOException {
        String service = CAMERA;
        return executeGetMethod(service,"getApplicationInfo");
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
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject getShootMode() throws IOException {
        String service = CAMERA;
        return executeGetMethod(service, "getShootMode");
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
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject setShootMode(String shootMode) throws IOException {
        String service = CAMERA;
        return executeSetMethod(service, "setShootMode", shootMode);
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
        String service = CAMERA;
        return executeGetMethod(service, "getAvailableShootMode");
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
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject getSupportedShootMode() throws IOException {
        String service = CAMERA;
        return executeGetMethod(service, "getSupportedShootMode");
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
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject startLiveview() throws IOException {
        String service = CAMERA;
        return executeGetMethod(service, "startLiveview");
    }

    public JSONObject startLiveviewWithSize(String size) throws IOException {
        String service = CAMERA;
        return executeSetMethod(service, "startLiveviewWithSize",size);
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
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public void stopLiveview() throws IOException {
        String service = CAMERA;
        executeGetMethod(service, "stopLiveview");
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
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject startRecMode() throws IOException {
        String service = CAMERA;
        return executeGetMethod(service, "startRecMode");
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
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public void stopRecMode() throws IOException {
        String service = CAMERA;
        executeGetMethod(service, "stopRecMode");
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
     * @throws IOException
     */
    public JSONObject actTakePicture() throws IOException {
        String service = CAMERA;
        return executeGetMethod(service, "actTakePicture");
    }

    public JSONObject awaitTakePicture() throws IOException {
        String service = CAMERA;
        return executeGetMethod(service, "awaitTakePicture");
    }

    public JSONObject startContShoot() throws IOException {
        String service = CAMERA;
        return executeGetMethod(service, "startContShooting");
    }

    public JSONObject stopContShoot() throws IOException {
        String service = CAMERA;
        return executeGetMethod(service,"stopContShooting");
    }

    public JSONObject startBulbCapture() throws IOException {
        String service = CAMERA;
        return executeGetMethod(service, "startBulbShooting");
    }

    public JSONObject stopBulbCapture() throws IOException {
        String service = CAMERA;
        return executeGetMethod(service,"stopBulbShooting");
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
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject startMovieRec() throws IOException {
        String service = CAMERA;
        return executeGetMethod(service, "startMovieRec");
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
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject stopMovieRec() throws IOException {
        String service = CAMERA;
        return executeGetMethod(service, "stopMovieRec");
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
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject actZoom(String direction, String movement) throws IOException {
        String service = CAMERA;
        return executeSetMethod(service, "actZoom", new JSONArray().put(direction).put(movement));
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
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject getEvent(boolean longPollingFlag, String eventVersion) throws IOException {
        String service = CAMERA;
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getEvent") //
                            .put("params", new JSONArray().put(longPollingFlag)) //
                            .put("id", id()).put("version", eventVersion);
            String url = findActionListUrl(service) + "/" + service;
            int longPollingTimeout = longPollingFlag ? 20000 : 8000; // msec

            log("Request:  " + requestJson);
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
     * @return JSON data of response
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject setCameraFunction() throws IOException {
        String service = CAMERA;
        return executeSetMethod(service, "setCameraFunction", "Remote Shooting");
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
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject getCameraMethodTypes() throws IOException {
        String service = CAMERA;
        return getMethodTypes(service,"1.4");
    }

    private JSONObject getMethodTypes(String service, String eventApi) throws IOException
    {
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "getMethodTypes")
                            .put("params", new JSONArray().put(eventApi))
                            .put("id", id())
                            .put("version", eventApi);
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson);
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
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */
    public JSONObject getAvcontentMethodTypes() throws IOException {
        String service = AVCONTENT;
        return getMethodTypes(service,"1.4");
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
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */

    public JSONObject getSchemeList() throws IOException {
        String service = AVCONTENT;
        return executeGetMethod(service, "getSchemeList");
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
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */

    public JSONObject getSourceList(String scheme) throws IOException {
        String service = AVCONTENT;
        try {
        JSONObject params = new JSONObject().put("scheme", scheme);
            return executeSetMethod(service, "getSourceList", new JSONArray().put(0, params));
        } catch (JSONException e) {
            Log.WriteEx(e);
        }
        return null;
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
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */

    public JSONObject getContentList(JSONArray params) throws IOException {
        String service = AVCONTENT;
        try {

            JSONObject requestJson =
                    new JSONObject().put("method", "getContentList").put("params", params) //
                            .put("version", "1.3").put("id", id());

            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson);
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
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */

    public JSONObject setStreamingContent(String uri) throws IOException {
        String service = AVCONTENT;
        try {

            JSONObject params = new JSONObject().put("remotePlayType", "simpleStreaming").put(
                    "uri", uri);
            JSONObject requestJson =
                    new JSONObject().put("method", "setStreamingContent") //
                            .put("params", new JSONArray().put(0, params)) //
                            .put("version", "1.0").put("id", id());

            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson);
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
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */

    public JSONObject startStreaming() throws IOException {
        String service = AVCONTENT;
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "startStreaming").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0").put("id", id());
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson);
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
     * @throws IOException all errors and exception are wrapped by this
     *             Exception.
     */

    public JSONObject stopStreaming() throws IOException {
        String service = AVCONTENT;
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", "stopStreaming").put("params", new JSONArray()) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson);
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
        return replyJson != null && replyJson.has("error");
    }


    public JSONObject setParameterToCamera(String parameter, JSONArray valueToSet) throws IOException {
        String service = CAMERA;
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", parameter) //
                            .put("params", valueToSet) //
                            .put("id", id()).put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson);
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    public JSONObject getParameterFromCamera(String valueToGet) throws IOException {
        String service = CAMERA;
        return getParameterFromService(valueToGet,service);
    }

    public JSONObject getParameterFromGuide(String valueToGet) throws IOException {
        String service = GUIDE;
        return getParameterFromService(valueToGet,service);
    }

    private JSONObject getParameterFromService(String valueToGet, String service) throws IOException {
        try {
            JSONObject requestJson =
                    new JSONObject().put("method", valueToGet) //
                            .put("params", new JSONArray()).put("id", id()) //
                            .put("version", "1.0");
            String url = findActionListUrl(service) + "/" + service;

            log("Request:  " + requestJson);
            String responseJson = SimpleHttpClient.httpPost(url, requestJson.toString());
            log("Response: " + responseJson);
            return new JSONObject(responseJson);
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    public JSONObject getVersions() throws IOException
    {
        return getParameterFromCamera("getVersions");
    }

    public JSONObject setTouchToFocus(double x, double y) throws IOException
    {
        String service = CAMERA;
        try {
            return executeSetMethod(service,"setTouchAFPosition" , new JSONArray().put(x).put(y));
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    public JSONObject actObjectTracking(double x, double y) throws IOException
    {
        String service = CAMERA;
        try {
            return executeSetMethod(service, "actTrackingFocus",  new JSONArray().put(new JSONObject().put("xPosition", x).put("yPosition",y)));
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }

    public void setLiveviewFrameInfo(boolean enable) throws IOException
    {
        String service = "camera";
        try {
            executeSetMethod(service, "setLiveviewFrameInfo",  new JSONArray().put(new JSONObject().put("frameInfo", enable)));
        } catch (JSONException e) {
            throw new IOException(e);
        }
    }
}
