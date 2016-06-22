package com.example.libconnection.tcp;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.example.libconnection.OnErrorListener;
import com.example.libconnection.OnPreparedListener;
import com.example.libconnection.OnReceivedListener;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by I3OR2A on 2016/6/17.
 */
public class NodeManager {
    private static String TAG = NodeManager.class.getName();

    private ConnectionSender connectionSender;

    private ConnectionHandler connectionHandler;

    private OnErrorListener onErrorListener;

    private OnPreparedListener onPreparedListener;

    private OnReceivedListener onReceivedListener;

    private EventHandler eventHandler;

    public void setOnErrorListener(OnErrorListener onErrorListener) {
        this.onErrorListener = onErrorListener;
    }

    public void setOnPreparedListener(OnPreparedListener onPreparedListener) {
        this.onPreparedListener = onPreparedListener;
    }

    public void setOnReceivedListener(OnReceivedListener onReceivedListener) {
        this.onReceivedListener = onReceivedListener;
    }

    public NodeManager() {
        Looper looper;
        if ((looper = Looper.myLooper()) != null) {
            eventHandler = new EventHandler(this, looper);
        } else if ((looper = Looper.getMainLooper()) != null) {
            eventHandler = new EventHandler(this, looper);
        } else {
            eventHandler = null;
        }
    }

    class ConnectionSender extends Thread {
        private String TAG = ConnectionSender.class.getName();

        private Socket socket;

        private String ip;

        private int port;

        public ConnectionSender(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        public void run() {
            super.run();
            try {
                socket = new Socket(InetAddress.getByName(ip), port);
                connectionHandler = new ConnectionHandler(socket);
                connectionHandler.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void interrupt() {
            super.interrupt();
        }
    }

    class ConnectionHandler extends Thread {
        private String TAG = ConnectionHandler.class.getName();

        private Socket socket;

        private ObjectInputStream objectInputStream;

        private ObjectOutputStream objectOutputStream;

        public ConnectionHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            super.run();
            try {
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectOutputStream.writeObject("request connection");


                objectInputStream = new ObjectInputStream(socket.getInputStream());
                String string = (String) objectInputStream.readObject();
                if (string.startsWith("establish connection")) {
                    while (!Thread.currentThread().isInterrupted()) {
                        objectInputStream.readObject();
                    }
                } else {
                    Log.e(TAG, "Unknown message " + string);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        public void interrupt() {
            super.interrupt();
        }
    }

    class EventHandler extends Handler {
        private String TAG = EventHandler.class.getName();

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
