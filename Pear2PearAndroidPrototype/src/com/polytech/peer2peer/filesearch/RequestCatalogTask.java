package com.polytech.peer2peer.filesearch;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.polytech.pear2pear.models.CatalogueDeFichiers;
import com.polytech.pear2pear.p2p_network_management.SetupHotspot;

public class RequestCatalogTask extends AsyncTask< Void,Void,DatagramPacket> {
    String destAddr;
    int destPort;
    Handler handler;
    String response = "";
    String request;

    public RequestCatalogTask(String destAddr, int port,String request,Handler handler){
        this.destAddr = destAddr;
        destPort = port ;
        this.handler = handler;
        this.request = request;
    }
    public RequestCatalogTask(Handler handler){
    	this.handler = handler;
        this.destAddr = "192.168.43.1";
        this.destPort = SetupHotspot.FILE_CATALOG_TRANSFERT_SERVER_PORT;
        this.request = "request_catalog";
    }
    
    public RequestCatalogTask(String request,Handler handler){
    	this.handler = handler;
        this.destAddr = "192.168.43.1";
        this.destPort = SetupHotspot.FILE_CATALOG_TRANSFERT_SERVER_PORT;
        this.request = request;
    }
    
    @Override
    protected DatagramPacket doInBackground(Void... voids) {
        DatagramSocket socket = null;
        try {
            Log.e("Info ","preparation de la connexion");
            socket = new DatagramSocket();
            ByteArrayOutputStream out = new ByteArrayOutputStream(64000);
            byte[] buffer = new byte[64000];
            int bytesRead;
            buffer = request.getBytes();
            Log.d("Info","Envoi d'une requete au serveur");
            DatagramPacket paquet = new DatagramPacket(buffer, buffer.length, new InetSocketAddress(this.destAddr,this.destPort));
            socket.send(paquet);
            buffer = new byte[64000];
            DatagramPacket response = new DatagramPacket(buffer, buffer.length); //en attente d'une requête
            socket.setSoTimeout(60000);
            socket.receive(response);
            Log.d("Info", "Receiving server response");
            handleResponse(response);

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void handleResponse(DatagramPacket paquet) throws SQLException, IOException, ClassNotFoundException {
        if(paquet !=null) {
            ByteArrayInputStream in = new ByteArrayInputStream(paquet.getData());
            ObjectInputStream ois = new ObjectInputStream(in);
            List<CatalogueDeFichiers> files = (List<CatalogueDeFichiers>) ois.readObject();
            Message msg = handler.obtainMessage();
            Bundle data = new Bundle();
            data.putSerializable("files", files.toArray());
            msg.setData(data);
            this.handler.sendMessage(msg);
        }
    }

}
