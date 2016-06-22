package com.example.libconnection.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by I3OR2A on 2016/6/20.
 */
public class RootManager {

    private static String TAG = RootManager.class.getName();

    private static final String NAME_SECURE = "BluetoothSecure";
    private static final String NAME_INSECURE = "BluetoothInSecure";

    private static final UUID MY_UUID_SECURE = UUID.fromString("");
    private static final UUID MY_UUID_INSECURE = UUID.fromString("");

    private BluetoothAdapter bluetoothAdapter;

    private ConnectionReceiver secureConnectionReceiver;
    private ConnectionReceiver inSecureConnectionReceiver;

    private ConcurrentHashMap<BluetoothSocket, ConnectionHandler> connectionHandlers = new ConcurrentHashMap<>();

    private EventHandler mEventHandler;

    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    private Context context;

    public RootManager(Context context) {
        this.context = context;

        Looper looper;
        if ((looper = Looper.myLooper()) != null) {
            mEventHandler = new EventHandler(this, looper);
        } else if ((looper = Looper.getMainLooper()) != null) {
            mEventHandler = new EventHandler(this, looper);
        } else {
            mEventHandler = null;
        }
    }

    public synchronized void start() {
        if (secureConnectionReceiver == null) {
            secureConnectionReceiver = new ConnectionReceiver(true);
            secureConnectionReceiver.start();
        }
        if (inSecureConnectionReceiver == null) {
            inSecureConnectionReceiver = new ConnectionReceiver(false);
            inSecureConnectionReceiver.start();
        }
    }

    public synchronized void stop() {
        if (secureConnectionReceiver != null) {
            secureConnectionReceiver.interrupt();
            secureConnectionReceiver = null;
        }

        if (inSecureConnectionReceiver != null) {
            inSecureConnectionReceiver.interrupt();
            inSecureConnectionReceiver = null;
        }
    }

    class ConnectionReceiver extends Thread {
        private final String TAG = ConnectionReceiver.class.getName();

        private BluetoothServerSocket bluetoothServerSocket;
        private String mSocketType;

        public ConnectionReceiver(boolean secure) {
            mSocketType = secure ? "Secure" : "InSecure";

            try {
                if (secure) {
                    bluetoothServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE, MY_UUID_SECURE);
                } else {
                    bluetoothServerSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME_INSECURE, MY_UUID_INSECURE);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            BluetoothSocket bluetoothSocket = null;

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    bluetoothSocket = bluetoothServerSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (bluetoothSocket != null) {
                    ConnectionHandler connectionHandler = new ConnectionHandler(bluetoothSocket);
                    connectionHandlers.put(bluetoothSocket, connectionHandler);
                    connectionHandler.start();
                }
            }
        }

        public void interrupt() {
            super.interrupt();
            if (bluetoothServerSocket != null)
                try {
                    bluetoothServerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    class ConnectionHandler extends Thread {
        private final String TAG = ConnectionHandler.class.getName();

        private BluetoothSocket bluetoothSocket;

        private ObjectInputStream objectInputStream;

        private ObjectOutputStream objectOutputStream;

        public ConnectionHandler(BluetoothSocket bluetoothSocket) {
            this.bluetoothSocket = bluetoothSocket;
        }

        public void run() {

            try {
                objectInputStream = new ObjectInputStream(bluetoothSocket.getInputStream());
                String string = (String) objectInputStream.readObject();
                if (string.startsWith("request connection")) {
                    objectOutputStream = new ObjectOutputStream(bluetoothSocket.getOutputStream());
                    objectOutputStream.writeObject("establish connection");
                    while (!Thread.currentThread().isInterrupted()) {
                        int objectNumbers = objectInputStream.read();
                        Log.i(TAG, "Number of object: " + objectNumbers);

                        for (int objectNumber = 0; objectNumber < objectNumbers; ++objectNumber) {
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
            if (bluetoothSocket != null)
                try {
                    bluetoothSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    class EventHandler extends Handler {

        private final String TAG = EventHandler.class.getName();

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
