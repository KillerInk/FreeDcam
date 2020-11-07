package com.troop.freedcam.camera.events;

import com.troop.freedcam.camera.basecamera.CameraControllerInterface;
import com.troop.freedcam.eventbus.EventBusHelper;
import com.troop.freedcam.utils.Size;

public class CameraStateEvents {

    public static class CameraOpenEvent
    {}

    public static class CameraOpenFinishEvent
    {
        public final CameraControllerInterface cameraControllerInterface;

        public CameraOpenFinishEvent(CameraControllerInterface cameraControllerInterface)
        {
            this.cameraControllerInterface = cameraControllerInterface;
        }
    }

    public static class CameraCloseEvent
    {}

    public static class PreviewOpenEvent
    {}

    public static class PreviewCloseEvent
    {}

    public static class CameraErrorEvent
    {
        public final String msg;

        public CameraErrorEvent(String msg)
        {
            this.msg = msg;
        }
    }

    public static class CameraChangedAspectRatioEvent
    {
        public final Size size;

        public CameraChangedAspectRatioEvent(Size size)
        {
            this.size = size;
        }
    }

    public static void fireCameraOpenEvent()
    {
        EventBusHelper.post(new CameraOpenEvent());
    }

    public static void fireCameraOpenFinishEvent(CameraControllerInterface cameraControllerInterface)
    {
        EventBusHelper.post(new CameraOpenFinishEvent(cameraControllerInterface));
    }

    public static void fireCameraCloseEvent()
    {
        EventBusHelper.post(new CameraCloseEvent());
    }

    public static void firePreviewOpenEvent()
    {
        EventBusHelper.post(new PreviewOpenEvent());
    }

    public static void firePreviewCloseEvent()
    {
        EventBusHelper.post(new PreviewCloseEvent());
    }

    public static void fireCameraErrorEvent(String msg)
    {
        EventBusHelper.post(new CameraErrorEvent(msg));
    }

    public static void fireCameraAspectRatioChangedEvent(Size msg)
    {
        EventBusHelper.post(new CameraChangedAspectRatioEvent(msg));
    }
}
