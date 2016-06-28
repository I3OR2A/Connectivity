package com.example.libconnection.broadcast;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.net.DatagramSocket;

public class BroadcastService {

    private static final String TAG = BroadcastService.class.getName();

    private ConnectionHandler connectionHandler;

    private EventHandler mEventHandler;

    private OnErrorListener onErrorListener;

    private OnPreparedListener onPreparedListener;

    private OnReceivedListener onReceivedListener;

    interface OnPreparedListener {
        void onPrepared();
    }

    interface OnErrorListener {
        void onError();
    }

    interface OnReceivedListener {
        void onReceived();
    }

    public void setOnPreparedListener(OnPreparedListener onPreparedListener) {
        this.onPreparedListener = onPreparedListener;
    }

    public void setOnErrorListener(OnErrorListener onErrorListener) {
        this.onErrorListener = onErrorListener;
    }

    public void setOnReceivedListener(OnReceivedListener onReceivedListener) {
        this.onReceivedListener = onReceivedListener;
    }

    public BroadcastService() {
        Looper looper;
        if ((looper = Looper.myLooper()) != null) {
            mEventHandler = new EventHandler(this, looper);
        } else if ((looper = Looper.getMainLooper()) != null) {
            mEventHandler = new EventHandler(this, looper);
        } else {
            mEventHandler = null;
        }
    }

    public void start() {
        if (connectionHandler == null) {
            connectionHandler = new ConnectionHandler();
            connectionHandler.start();
        }
    }

    public void stop() {
        if (connectionHandler != null) {
            connectionHandler.interrupt();
            connectionHandler = null;
        }
    }

    class ConnectionHandler extends Thread {
        private final String TAG = ConnectionHandler.class.getName();

        private DatagramSocket datagramSocket;

        public ConnectionHandler() {

        }

        public void run() {

        }

        public void interrupt() {
            super.interrupt();
        }
    }

    class EventHandler extends Handler {
        private final String TAG = EventHandler.class.getName();

        private BroadcastService broadcastService;

        public EventHandler(BroadcastService broadcastService, Looper looper) {
            super(looper);
            this.broadcastService = broadcastService;
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                default:
                    Log.e(TAG, "Unknown message type " + msg.what);
            }
        }
    }
}
