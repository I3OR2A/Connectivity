package com.example.libconnection.bluetooth;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Created by I3OR2A on 2016/6/22.
 */
public class NodeManager {

    private static final String TAG = NodeManager.class.getName();

    private ConnectionSender connectionSender;

    private ConnectionHandler connectionHandler;

    private EventHandler mEvemtHandler;

    public NodeManager() {

    }

    public synchronized void start() {
        if (connectionSender == null) {
            connectionSender = new ConnectionSender();
            connectionSender.start();
        }
    }

    public synchronized void stop() {
        if (connectionSender != null) {
            connectionSender.interrupt();
            connectionSender = null;
        }
    }

    class ConnectionSender extends Thread {
        private final String TAG = ConnectionSender.class.getName();

        public ConnectionSender() {

        }

        public void run() {

        }

        public void interrupt() {
            super.interrupt();
        }
    }

    class ConnectionHandler extends Thread {
        private final String TAG = ConnectionHandler.class.getName();

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

        private NodeManager nodeManager;

        public EventHandler(NodeManager nodeManager, Looper looper) {
            super(looper);
            this.nodeManager = nodeManager;
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                default:
                    Log.e(TAG, "Unknown message type " + msg.what);
            }
        }
    }
}
