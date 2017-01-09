package se.hkr.activeageing.qrauth;

import android.os.AsyncTask;
import android.util.Log;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Karolis on 2017-01-08.
 */
public class AsyncArduino extends AsyncTask<String, Void, Boolean> {

    public final static String TAG = "AsyncArduino";

    protected Boolean doInBackground(String... params) {
        Log.i(TAG, "Async Arduino started");
        boolean isSignal = false;
        try {
            ServerSocket serverSocket = new ServerSocket(5454);
            Socket socket = serverSocket.accept();
            if (socket.isConnected()) {
                isSignal= true;
                serverSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            isSignal = false;
        }
        Log.i(TAG, "Async Arduino finished");
        return isSignal;
    }
}
