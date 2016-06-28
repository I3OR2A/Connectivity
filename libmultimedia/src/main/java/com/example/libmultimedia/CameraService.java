package com.example.libmultimedia;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by I3OR2A on 2016/6/28.
 */
public class CameraService {

    private static final String TAG = CameraService.class.getName();

    private Context mContext;

    private EventHandler mEventHandler;

    public CameraService(Context context) {
        this.mContext = context;
    }

    public boolean checkCameraHardware() {
        if (mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    public static Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return camera;
    }

    public static class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mSurfaceHolder;
        private Camera mCamera;

        public CameraPreview(Context context, Camera camera) {
            super(context);
            this.mCamera = camera;
            this.mSurfaceHolder = getHolder();
            this.mSurfaceHolder.addCallback(this);
            this.mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        public void surfaceCreated(SurfaceHolder holder) {
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (mSurfaceHolder.getSurface() == null) {
                return;
            }

            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // set preview size amd make any resize, rotate or reformatting changes here

            // start preview with new setting
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    }

    class EventHandler extends Handler {
        private final String TAG = EventHandler.class.getName();

        private CameraService mCameraService;

        public EventHandler(CameraService cameraService, Looper looper) {
            super(looper);
            this.mCameraService = cameraService;
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                default:
                    Log.e(TAG, "Unknown message type " + msg.what);
            }
        }
    }
}
