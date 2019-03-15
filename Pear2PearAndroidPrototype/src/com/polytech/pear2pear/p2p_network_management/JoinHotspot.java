package com.polytech.pear2pear.p2p_network_management;

import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.polytech.pear2pear.dbadapters.DatabaseHelper;
import com.polytech.pear2pear.models.CatalogueLocal;

public class JoinHotspot  {

	public static final int TIME_TO_SLEEP_BEFORE_SCAN = 5000;
	public static final int PORT_TELECHARGEMENT_FICHIERS = 4997 ;
    public  static final int MAX_FILE_SIZE = SetupHotspot.BUFFER_SIZE;
	protected static final int PORT_PUBLICATION_SOUS_RESEAUX_DETECTES_PAIRS = 4999 ;
	private Context context;
	private boolean alreadyConnected = false;
	private boolean root = false;
	private boolean continuProcess = true;
	private  List<ScanResult> scanResults;
	private DatabaseHelper helper;
	private SetupHotspot setupHotspot;
	private Handler handler;
	private DatagramSocket clientFichiers, clientFichier, clientSousReseaux;
	private static WifiManager wifi;
	
	

	public JoinHotspot(final Context context, Handler handler){

		this.context = context;
		this.handler = handler;



	}
	
	public boolean isAlreadyConnected() {
		return alreadyConnected;
	}

	public boolean isRoot() {
		return root;
	}

	public List<ScanResult> getScanResults() {
		return scanResults;
	}

	public SetupHotspot getSetupHotspot() {
		return setupHotspot;
	}
	
	public void compute(){
		//activation du wifi
				wifi = (WifiManager) 
						context.getSystemService(Context.WIFI_SERVICE);

				activateWifi(wifi, true);


				//scan available subnetwork
				wifi.startScan();
			


				//when scan results are available, try to connect to a subnetwork or if you are already connected, send it to subnetwork root
				context.registerReceiver(new BroadcastReceiver()
				{
					@Override
					public void onReceive(Context c, Intent intent) 
					{
						scanResults = wifi.getScanResults();

						if(!alreadyConnected){
							alreadyConnected = tryToConnect(wifi);
							if(!alreadyConnected){ //alors il n'a pas �t� possible de se connecter � un sous r�seau existant... Je passe en mode root
								setupHotspot = new SetupHotspot(context);
								root = true;
								alreadyConnected = true;
							}
							else{ //il s'est connect�... publication du catalogue local de fichiers
								publieCatalogueFichiers(wifi);
								

							}
                            createNewDownloadListener(); //quite le root ou un pair quelconque, tout le monde peut recevoir la requete de telechargement d'un fichier et y repondre
                            handler.sendEmptyMessage(1);
						}
						else{ //alors je suis deja connect� � un sous r�seau, j'envoi le scanResults au root du sous reseau
							publieResultatsScan(wifi);
						}
					}
				}, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)); 
	}




    public void createNewDownloadListener(){ //un peer se connectera au reseau et se connectera a cette socket pour telecharger un fichier que je possede



        Thread t  = new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                byte[] buf;
                DatagramPacket packet;
                Log.d("prebegining download request ", "server waiting");
                while(continuProcess){
                    try{
                        clientFichier = new DatagramSocket(PORT_TELECHARGEMENT_FICHIERS);
						clientFichier.setSoTimeout(SetupHotspot.SOCKET_TIME_OUT);
                        buf = new byte[SetupHotspot.BUFFER_SIZE];
                        Log.d("begining download request listener", "server waiting");
                        while (clientFichier != null && !clientFichier.isClosed())
                        {
                            try
                            {
                                packet = new DatagramPacket(buf, buf.length); //en attente d'une requete
                                
                                Log.d("waiting download request ", "server waiting");
                                

								clientFichier.receive(packet);
								
								

                                String	requete = new String(packet.getData(), 0, packet.getLength());
                                Log.d(" download request received", "server "+requete);

                                //we detect file requested and send it

                                InetAddress peerAdress = packet.getAddress();

                                ObjectMapper mapper = new ObjectMapper();
                                JsonNode file = mapper.readTree(requete);


                                try {

									String cheminFichier = file.findValue("cheminFichier").asText();
                                    String hashFichier = file.findValue("hashFichier").asText();
                                    
                                    Log.d(" download request received", "cheminFichier "+cheminFichier+" hashFichier "+hashFichier);

									File fichier = new File(cheminFichier);

                                    FileInputStream fis = new FileInputStream(fichier);

									if(!fichier.exists()){
										Log.e("chemin invalide", "le fichier correspondant au chemin "+cheminFichier+" n'existe.");
									}
									else{
										long taille = fichier.length();

                                        long nbreFichiers = taille/MAX_FILE_SIZE;
                                        if(taille%MAX_FILE_SIZE!=0){
                                            nbreFichiers++;
                                        }

                                        buf = (nbreFichiers + "").getBytes();
                                        packet.setData(buf);
                                        packet.setLength(buf.length);

                                //        packet = new DatagramPacket(buf, buf.length, peerAdress, PORT_TELECHARGEMENT_FICHIERS);
                                       
                                        Log.d(" sending nbreFichiers", "nbreFichiers "+nbreFichiers);
                                        clientFichier.send(packet);



                                        for(int i=0; i<nbreFichiers; i++){

                                            buf = new byte[MAX_FILE_SIZE];

                                            taille = fis.read(buf);
                                            
                                            packet.setData(buf);
                                            packet.setLength(buf.length);
                                            Log.e("envoi de ", "envoi de "+(i+1));
                                    //        packet = new DatagramPacket(buf, (int)taille, peerAdress, PORT_TELECHARGEMENT_FICHIERS);
                                            clientFichier.send(packet);
                                            
                                            Thread.sleep(200);
                                        }


                                        //augmentation du nombre de telechargements
                                        RuntimeExceptionDao<CatalogueLocal, Integer> catalogueLocalDao = (RuntimeExceptionDao<CatalogueLocal, Integer>) getHelper().getSimpleDataDao(CatalogueLocal.class);


                                        QueryBuilder<CatalogueLocal, Integer> queBuild = catalogueLocalDao.queryBuilder();
                                        queBuild.where().eq("hashFichier", hashFichier);

                                        PreparedQuery<CatalogueLocal> predQue = queBuild.prepare();

                                        CatalogueLocal catalogueLocal = catalogueLocalDao.query(predQue).get(0);
                                        catalogueLocal.setNbreTelechargements(catalogueLocal.getNbreTelechargements()+1);
                                        catalogueLocalDao.update(catalogueLocal);

									}


                                } catch (Exception e) {
                                    Log.e(DatabaseHelper.class.getName(), "echec de publication du fichier", e);
                                    throw new RuntimeException(e);
                                }

//
//                                buf = "OK".getBytes();
//
//                                packet = new DatagramPacket(buf, buf.length, peerAdress, PORT_TELECHARGEMENT_FICHIERS);
//                                listenerFichiers.send(packet);
//

                            }
                            catch(SocketTimeoutException e){

                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                                Log.e("sending file ", "problem with sending file. error : "+e.getMessage());


                                if(clientFichier!=null) {
                                    clientFichier.close();
                                    clientFichier = null;

                                }


                            }


                        }
                    }

                    catch(Exception e){
                        e.printStackTrace();
                        Log.e("new peer listener ", "problem with root new peer listener. error : "+e.getMessage());


                        if(clientFichier!=null) {
                            clientFichier.close();
                            clientFichier = null;

                        }

                    }

                }



            }
        });


        t.start();
    }





	public void publieCatalogueFichiers(final WifiManager wifi){
		Thread t  = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				byte[] buf;
				try {
					clientFichiers = new DatagramSocket();
				} catch (SocketException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				DatagramPacket packet;

				ObjectMapper mapper = new ObjectMapper();
				Map<String, String> map = new HashMap<String, String>();



				boolean flag;
				String requete;

				RuntimeExceptionDao<CatalogueLocal, Integer> catalogueLocalDao = (RuntimeExceptionDao<CatalogueLocal, Integer>) getHelper().getSimpleDataDao(CatalogueLocal.class);

				try{
					for(CatalogueLocal fichier : catalogueLocalDao.queryForAll()){


						Log.e("publication de fichier", "publication de "+fichier.getNomFichier());
						map.put("nomFichier", fichier.getNomFichier());
						map.put("descriptionFichier", fichier.getDescriptionFichier());
						map.put("cheminFichier", fichier.getCheminFichier());
						map.put("nbreTelechargements", ""+fichier.getNbreTelechargements());
						map.put("hashFichier", fichier.getHashFichier()); 
						map.put("macAdress", wifi.getConnectionInfo().getMacAddress()); 

						do{

							buf = mapper.writeValueAsString(map).getBytes();

							packet = new DatagramPacket(buf, buf.length, new InetSocketAddress(SetupHotspot.ADRESSE_PERE_SOUS_RESEAU, SetupHotspot.PORT_PUBLICATION_FICHIERS_PAIRS));


							clientFichiers.send(packet);


							buf =new byte[64000];

							packet.setData(buf);
							packet.setLength(buf.length);
							clientFichiers.receive(packet);

							requete = new String(packet.getData(), 0, packet.getLength());
							flag = requete.contains("OK");

						}while(!flag);





					}
					clientFichiers.close();
				}catch(Exception e){
					e.printStackTrace();
					Log.e("catalogueLocal client ", "problem with sending catalogueLocal client. error : "+e.getMessage());

				}
				finally{
					if(clientFichiers!=null) {
						clientFichiers.close();
						clientFichiers = null;

					}
				}

			}
		});


		t.start();

		scheduleWifiScan(wifi);
	}

	public void publieResultatsScan(WifiManager wifi){
		Thread t  = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				byte[] buf;
				try {
					clientSousReseaux = new DatagramSocket();
				} catch (SocketException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				DatagramPacket packet;
				boolean flag;
				String requete;

				RuntimeExceptionDao<CatalogueLocal, Integer> catalogueLocalDao = (RuntimeExceptionDao<CatalogueLocal, Integer>) getHelper().getSimpleDataDao(CatalogueLocal.class);

				try{

					do{

						buf = SetupHotspot.MESSAGE_REINITIALISATION_SOUS_RESEAUX_DETECTES_PAIRS.getBytes();

						packet = new DatagramPacket(buf, buf.length, new InetSocketAddress(SetupHotspot.ADRESSE_PERE_SOUS_RESEAU, SetupHotspot.PORT_PUBLICATION_SOUS_RESEAUX_DETECTES_PAIRS));


						clientSousReseaux.send(packet);


						buf =new byte[64000];

						packet.setData(buf);
						packet.setLength(buf.length);
						clientSousReseaux.receive(packet);

						requete = new String(packet.getData(), 0, packet.getLength());
						flag = requete.contains("OK")?true:false;
					}while(!flag);

					for(ScanResult  sr : scanResults){






						do{

							buf = sr.SSID.replace(SetupHotspot.NETWORK_PREFIX, "").getBytes();

							packet = new DatagramPacket(buf, buf.length, new InetSocketAddress(SetupHotspot.ADRESSE_PERE_SOUS_RESEAU, SetupHotspot.PORT_PUBLICATION_SOUS_RESEAUX_DETECTES_PAIRS));


							clientSousReseaux.send(packet);


							buf =new byte[64000];

							packet.setData(buf);
							packet.setLength(buf.length);
							clientSousReseaux.receive(packet);

							requete = new String(packet.getData(), 0, packet.getLength());
							flag = requete.contains("OK")?true:false;

						}while(!flag);





					}
					clientSousReseaux.close();

				}catch(Exception e){
					e.printStackTrace();
					Log.e("scanResults client ", "problem with sending scanResults client. error : "+e.getMessage());

				}
				finally{
					if(clientSousReseaux!=null) {
						clientSousReseaux.close();
						clientSousReseaux = null;

					}
				}

			}
		});


		t.start();

		scheduleWifiScan(wifi);

	}

	public void scheduleWifiScan(final WifiManager wifi){
		Thread t  = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(TIME_TO_SLEEP_BEFORE_SCAN);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				wifi.startScan();
			}
		});
		t.start();
	}





	public  void activateWifi(WifiManager wifi, boolean enable){
		try {

			boolean b=wifi.isWifiEnabled();
			if(b != enable){
				wifi.setWifiEnabled(enable);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public boolean tryToConnect(WifiManager wifi){
		if(scanResults!=null){
			for(ScanResult sr: scanResults){
				if(sr.SSID.contains(SetupHotspot.NETWORK_PREFIX)){
					if(connectSsid(wifi, sr.SSID, SetupHotspot.NETWORK_KEY, SetupHotspot.NETWORK_TYPE)){
						return true;
					}
				}
			}
		}
		return false;
	}


	public boolean connectSsid(WifiManager wifi, String ssid, String password, String type){
		boolean flag = false;



		int networkId = -1;
		for(WifiConfiguration wc: wifi.getConfiguredNetworks()){
			if( wc.SSID.toString().equals("\""+ssid+"\"")){
				networkId = wc.networkId;
				Log.e("network", " "+wc.SSID+" "+wc.networkId);

				break;
			}
		}


		WifiConfiguration wc = new WifiConfiguration();
		wc.SSID = "\""+ssid+"\"";


		wc.preSharedKey  = "\""+password+"\"";


		wc.status = WifiConfiguration.Status.ENABLED; 
		wc.priority = 40;

		if(networkId==-1)		 networkId = wifi.addNetwork(wc) ;
		else{
			wc.networkId = networkId;
			networkId = wifi.updateNetwork(wc);
		}
		if (networkId != -1) {

			flag = wifi.disconnect();
			flag = flag&&wifi.enableNetwork(networkId, true);
			flag = flag&&wifi.reconnect();  




			ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

			while (flag && !connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected());

			Log.e("connect� ", "connect� � "+wc.SSID+" "+wc.preSharedKey+" "+networkId);
		} 
		return flag;
	}

	public void close(){
		continuProcess = false;
		if  (helper  !=  null)  {
			OpenHelperManager.releaseHelper();
			helper  =  null;
		}

		if  (setupHotspot  !=  null)  {
			setupHotspot.close();
			setupHotspot  =  null;
		}
		
		if(clientSousReseaux!=null) {
			clientSousReseaux.close();
			clientSousReseaux = null;

		}
		
		if(clientFichiers!=null) {
			clientFichiers.close();
			clientFichiers = null;

		}

        if(clientFichier!=null) {
            clientFichier.close();
            clientFichier = null;

        }
		
		

	}

	private  DatabaseHelper  getHelper()  {
		if  (helper  ==  null)  {
			helper  = OpenHelperManager.getHelper(context,  DatabaseHelper.class);
		}
		return  helper;
	}
	
	
	
	
	public void publieCatalogueFichiers(final CatalogueLocal fichier){
		Thread t  = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				byte[] buf;
				try {
					clientFichiers = new DatagramSocket();
				} catch (SocketException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				DatagramPacket packet;

				ObjectMapper mapper = new ObjectMapper();
				Map<String, String> map = new HashMap<String, String>();



				boolean flag;
				String requete;


				try{


						Log.e("publication de fichier", "publication de "+fichier.getNomFichier());
						map.put("nomFichier", fichier.getNomFichier());
						map.put("descriptionFichier", fichier.getDescriptionFichier());
						map.put("cheminFichier", fichier.getCheminFichier());
						map.put("nbreTelechargements", ""+fichier.getNbreTelechargements());
						map.put("hashFichier", fichier.getHashFichier()); 
						map.put("macAdress", wifi.getConnectionInfo().getMacAddress()); 

						do{

							buf = mapper.writeValueAsString(map).getBytes();

							packet = new DatagramPacket(buf, buf.length, new InetSocketAddress(SetupHotspot.ADRESSE_PERE_SOUS_RESEAU, SetupHotspot.PORT_PUBLICATION_FICHIERS_PAIRS));


							clientFichiers.send(packet);


							buf =new byte[64000];

							packet.setData(buf);
							packet.setLength(buf.length);
							clientFichiers.receive(packet);

							requete = new String(packet.getData(), 0, packet.getLength());
							flag = requete.contains("OK");

						}while(!flag);

						clientFichiers.close();



					

				}catch(Exception e){
					e.printStackTrace();
					Log.e("catalogueLocal client ", "problem with sending catalogueLocal client. error : "+e.getMessage());

				}
				finally{
					if(clientFichiers!=null) {
						clientFichiers.close();
						clientFichiers = null;

					}
				}

			}
		});


		t.start();

	}
	
	
	


}
