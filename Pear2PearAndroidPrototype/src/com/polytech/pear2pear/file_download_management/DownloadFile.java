package com.polytech.pear2pear.file_download_management;

import java.io.File;
import java.io.FileOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.polytech.pear2pear.models.CatalogueDeFichiers;
import com.polytech.pear2pear.p2p_network_management.JoinHotspot;

/**
 * Created by LE MONDE DE DJJEFF on 11/01/2016.
 */
public class DownloadFile {
    private Context context;
    private Handler handler;


    public DownloadFile(Context context, Handler handler){
        this.context = context;
        this.handler = handler;
    }

    public void downloadFile(final CatalogueDeFichiers fichier){
        final Thread t  = new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                byte[] buf;
                DatagramSocket clientFichier = null;
                try {
                    clientFichier = new DatagramSocket();
                } catch (SocketException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                DatagramPacket packet;

                ObjectMapper mapper = new ObjectMapper();
                Map<String, String> map = new HashMap<String, String>();




                try{



                        map.put("cheminFichier", fichier.getCheminFichier());
                        map.put("hashFichier", fichier.getHashFichier());



                            buf = mapper.writeValueAsString(map).getBytes();

                            packet = new DatagramPacket(buf, buf.length, new InetSocketAddress(fichier.getAdressePeer(), JoinHotspot.PORT_TELECHARGEMENT_FICHIERS));


                            if(clientFichier != null){
                            	Log.d("sending download request ", "file "+fichier.getCheminFichier()+" "+fichier.getHashFichier()+" "+fichier.getAdressePeer());
                                clientFichier.send(packet);

                                buf =new byte[JoinHotspot.MAX_FILE_SIZE];

                                packet.setData(buf);
                                packet.setLength(buf.length);
                                
                                Log.d("waiting download response ", "waiting");
                                
                                clientFichier.receive(packet);
                               
                                try{
                                    int nbreFichiers = Integer.parseInt(new String(packet.getData(), 0, packet.getLength()));
                                    
                                    Log.d("download response received ", "nbreFichiers "+nbreFichiers);
                                    

                                    handler.sendEmptyMessage(nbreFichiers);

                                    File downloadDir = new File(Environment.getExternalStorageDirectory().getPath() + "/Pear2Pear/Download_Files/");
                                    if (!downloadDir.exists() && !downloadDir.mkdirs()) {
                                        Log.e("creation download dir", "impossible de créer le repertoire de stockage des fichiers téléchargés "+downloadDir.getPath()+" vérifier les permissions.");
                                    }
                                    File fichierTelecharge = new File(downloadDir, fichier.getNomFichier());

                                    try {

                                        FileOutputStream fout = new FileOutputStream(fichierTelecharge);
                                        
                                        for(int i=0; i<nbreFichiers; i++){
                                            buf =new byte[JoinHotspot.MAX_FILE_SIZE];

                                            packet.setData(buf);
                                            packet.setLength(buf.length);
                                            clientFichier.receive(packet);

                                            fout.write(packet.getData());
                                            handler.sendEmptyMessage(i+1);
                                        }
                                        
                                        fout.close();

                                        handler.sendEmptyMessage(0);

                                    } catch (Exception e) {
                                        // TODO Auto-generated catch block
                                        Log.e("echec telechargement", "error : " + e.getMessage());
                                        handler.sendEmptyMessage(-1);
                                    }

                                }catch (NumberFormatException e){
                                    e.printStackTrace();
                                    Log.e("element reçu errone", "element reçu sensé etre un nombre mais erreur "+new String(packet.getData()));
                                    handler.sendEmptyMessage(-1);
                                }
                                finally{
                                    if(clientFichier!=null) {
                                        clientFichier.close();
                                        clientFichier = null;

                                    }
                                }

                            }













                }catch(Exception e){
                    e.printStackTrace();
                    Log.e("catalogueLocal client ", "problem with sending catalogueLocal client. error : " + e.getMessage());
                    handler.sendEmptyMessage(-1);

                }
                finally{
                    if(clientFichier!=null) {
                        clientFichier.close();
                        clientFichier = null;

                    }
                }

            }
        });


        t.start();
    }




}
