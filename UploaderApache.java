package com.example.samuel.vpl_personal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPListParseEngine;
import org.apache.commons.net.ftp.FTPReply;
import org.jibble.simpleftp.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/**
 * Created by Samuel on 19.09.2017.
 */

class UploaderApache extends AsyncTask <String,String,Boolean> {

    private String pfad;
    Context context;
    FTPClient ftp = null;

    public UploaderApache( Context context, String pfad) {
        this.context = context;
        ftp = new FTPClient();
        this.pfad = pfad;

    }


    // Erstellt einen Ordner, mit der dem Datum zur Zeit der Ausführung
    // Lädt alle Bilder hoch die sich in der Übergebenene ArrayList befinden und speichert sie in dem Ordner

    public void uploadFiles(File bilderPfad) throws Exception {

        String bildName = bilderPfad.getName();
        InputStream inputStream = new FileInputStream(bilderPfad);
        Log.i("LOGi", "Start uploading first file");
        boolean done = ftp.storeFile(bildName, inputStream);


        if (done) {
            Log.i("LOGi", "The first file is uploaded successfully.");
            inputStream.close();
        }


    }


    public void disconnect() {
        if (this.ftp.isConnected()) {
            try {
                this.ftp.logout();
                this.ftp.disconnect();
            } catch (IOException f) {
                // do nothing as file is already saved to server
            }
        }
    }

    // Führt den gewünschten Prozess im Hintergrund aus, die übergebenen Parameter sind "Host" , "Username", "Password"
    @Override
    protected Boolean doInBackground(String... strings) {
        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        int reply;


        String host = "ftp.docuware-online.de/example_example_example";

        try {

                Log.i("LOGi", "Verbinde..." + host);
                ftp.connect(host);

                Log.i("LOGi", "Fertig verbunden");
            } catch (IOException e) {
                Log.i("LOGi", "Fehler beim Verbinden: " + e);
                e.printStackTrace();
            }


        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            try {
                ftp.disconnect();
                publishProgress("Fehler beim Hochladen");
            } catch (IOException e) {
                e.printStackTrace();
                cancel(true);
            }

        }
        
                try {
                    ftp.setFileType(FTP.BINARY_FILE_TYPE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
               
               
                ftp.enterLocalPassiveMode();
                
                try {
                    Log.i("LOGi", "Starte Hochladen der Bilder:" + pfad);

                    //Uploads image
                    uploadFiles(new File(pfad));

                    Log.i("LOGi", "Nach hochladen der Bilder... ");

                } catch (Exception e) {
                    e.printStackTrace();
                    publishProgress("Fehler beim Hochladen der Bilder");
                    cancel(true);
                    
                  } 
        

            disconnect();

            return true;

        }

        @Override
    protected void onPreExecute() {

        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(Boolean bool) {
        super.onPostExecute(bool);
        if (bool) {

            Toast.makeText(context, "Hochladen erfolgreich!", Toast.LENGTH_SHORT).show();
            Log.i("LOGi", "Kein Fehler");
        } else {
            Toast.makeText(context, "Hochladen fehlgeschlagen!", Toast.LENGTH_SHORT).show();
            Log.i("LOGi", " Fehler");

        }

        sendMessage();

    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Toast.makeText(context, values[0], Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onCancelled() {
        Toast.makeText(context, "Hochladen abgebrochen", Toast.LENGTH_LONG).show();

    }


    private void sendMessage() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("custom-event-name");
        // You can also include some extra data.
        intent.putExtra("message", "This is my message!");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }






}
