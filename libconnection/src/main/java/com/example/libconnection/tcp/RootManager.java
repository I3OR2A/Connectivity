package com.example.libconnection.tcp;

import android.media.MediaPlayer;
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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by I3OR2A on 2016/6/17.
 */
public class RootManager {

    private static final String TAG = RootManager.class.getName();

    private ConnectionReceiver connectionReceiver;

    private ConcurrentHashMap<Socket, ConnectionHandler> connectionReceivers = new ConcurrentHashMap<>();

    private OnErrorListener onErrorListener;

    private OnReceivedListener onReceivedListener;

    private OnPreparedListener onPreparedListener;

    private MediaPlayer mediaPlayer;

    private EventHandler eventHandler;

    public RootManager() {
        Looper looper;
        if ((looper = Looper.myLooper()) != null) {
            eventHandler = new EventHandler(this, looper);
        } else if ((looper = Looper.getMainLooper()) != null) {
            eventHandler = new EventHandler(this, looper);
        } else {
            eventHandler = null;
        }
    }

    public void setOnErrorListener(OnErrorListener onErrorListener) {
        this.onErrorListener = onErrorListener;
    }

    public void setReceivedListener(OnReceivedListener onReceivedListener) {
        this.onReceivedListener = onReceivedListener;
    }

    public void setOnPreparedListener(OnPreparedListener onPreparedListener) {
        this.onPreparedListener = onPreparedListener;
    }

    class ConnectionReceiver extends Thread {
        private String TAG = ConnectionReceiver.class.getName();

        private ServerSocket serverSocket;

        private int port;

        public ConnectionReceiver(int port) {
            this.port = port;
        }

        public void run() {
            super.run();
            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket socket = serverSocket.accept();
                    ConnectionHandler connectionHandler = new ConnectionHandler(socket);
                    connectionReceivers.put(socket, connectionHandler);
                    connectionHandler.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                String string = (String) objectInputStream.readObject();
                if (string.startsWith("request connection")) {
                    objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectOutputStream.writeObject("establish connection");
                    while (!Thread.currentThread().isInterrupted()) {
                        int objectNumbers = objectInputStream.read();
                        Log.i(TAG, "Number of object: " + objectNumbers);

                        for(int objectNumber = 0; objectNumber < objectNumbers; ++objectNumber){
                            objectInputStream.readObject();
                        }
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

        private RootManager rootManager;

        public EventHandler(RootManager rootManager, Looper looper) {
            super(looper);
            this.rootManager = rootManager;
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                default:
                    Log.e(TAG, "Unknown message type " + msg.what);
            }
        }
    }
}
