package com.polytech.pear2pear.p2p_network_management;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedDelete;
import com.j256.ormlite.table.TableUtils;
import com.polytech.pear2pear.dbadapters.DatabaseHelper;
import com.polytech.pear2pear.models.CatalogueDeFichiers;
import com.polytech.pear2pear.models.CatalogueLocal;
import com.polytech.pear2pear.models.CataloguePartageDeFichiers;
import com.polytech.pear2pear.models.CatalogueSousReseaux;
import com.polytech.pear2pear.models.CatalogueTableRoutage;
import com.polytech.pear2pear.prototype.WifiApManager;
import com.polytech.peer2peer.filesearch.CatalogService;

public class SetupHotspot  {
	private Context context;
	private String currentNetWorkUuid;
	public static final String NETWORK_PREFIX = "p2p_";
	public static final String 	ADRESSE_PERE_SOUS_RESEAU = "192.168.43.1";
	public static final String NETWORK_KEY = "wearefriends";
	public static final String NETWORK_TYPE = "WPA";
	public static final int SOCKET_TIME_OUT = 5000;

	private DatabaseHelper helper;
	public static final int FILE_CATALOG_TRANSFERT_SERVER_PORT = 4445;
	public static final int FILE_CATALOG_TRANSFERT_CLIENT_PORT = 4444;
	protected static final int PORT_PUBLICATION_FICHIERS_PAIRS = 4998;
	protected static final int PORT_PUBLICATION_SOUS_RESEAUX_DETECTES_PAIRS = 4999 ;
	protected static final String MESSAGE_REINITIALISATION_SOUS_RESEAUX_DETECTES_PAIRS = "reinitialisation";

	protected static final int BUFFER_SIZE = 64000;

	private String previousSSID;
	private String previousPassword;

	private DatagramSocket listenerFichiers, listenerSousReseaux;




	private boolean flag = false; //booléen permettant de contrôler l'arrêt des sockets








	public SetupHotspot(Context context){ //a la fin de l'utilisation de l'object, penser à appeler close() pour liberer le helper
		this.context = context;

		//generation de l'uuid du reseau.
		currentNetWorkUuid = (((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getMacAddress()).replace(":", "").toLowerCase();

		//initialisation des catalogues
		initializeTables();

		//le père publie ses fichiers dans les catalogues
		publishMyFiles();

		//creation du hotspot
		createHotspot(NETWORK_PREFIX+this.currentNetWorkUuid, NETWORK_KEY);

		//creation du listener pour la publication des catalogues locaux de fichiers vers les catalogues de sous reseaux par les nouveaux pairs
		createNewPeerListener();

		//creation du listener pour la publication par chaque pair des sous réseaux  p2p qu'il détecte
		createNearSubNetworkListener();

        //demarrage du service de recherche des fichiers
        try {
			lunchSearchService();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}


	public void createNewPeerListener(){ //un peer se connectera au réseau et se connectera à cette socket pour déposer ses fichiers partagés



		Thread t  = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				byte[] buf;
				DatagramPacket packet;

				while(!flag){
					try{
						listenerFichiers = new DatagramSocket(PORT_PUBLICATION_FICHIERS_PAIRS);
						listenerFichiers.setSoTimeout(SOCKET_TIME_OUT);
						buf = new byte[BUFFER_SIZE];

						while (listenerFichiers != null && !listenerFichiers.isClosed())
						{
							try
							{
								packet = new DatagramPacket(buf, buf.length); //en attente d'une requête

								listenerFichiers.receive(packet);

								String	requete = new String(packet.getData(), 0, packet.getLength());


								//we save file informations in subnetwork databases

								InetAddress peerAdress = packet.getAddress();

								ObjectMapper mapper = new ObjectMapper();
								JsonNode file = mapper.readTree(requete);
								Log.e("recu ", file.findValue("nomFichier").asText());

								try {
									RuntimeExceptionDao<CatalogueDeFichiers, Integer> catalogueDeFichiersDao = (RuntimeExceptionDao<CatalogueDeFichiers, Integer>) getHelper().getSimpleDataDao(CatalogueDeFichiers.class);
									RuntimeExceptionDao<CataloguePartageDeFichiers, Integer> cataloguePartageDeFichiersDao = (RuntimeExceptionDao<CataloguePartageDeFichiers, Integer>) getHelper().getSimpleDataDao(CataloguePartageDeFichiers.class);
									CatalogueDeFichiers catalogueDeFichiers; CataloguePartageDeFichiers cataloguePartageDeFichiers;
									Log.e("recu ", file.findValue("nomFichier").asText());
									catalogueDeFichiers = new CatalogueDeFichiers(file.findValue("nomFichier").asText(), file.findValue("descriptionFichier").asText(), file.findValue("hashFichier").asText(), file.findValue("nbreTelechargements").asLong(), peerAdress.getHostAddress(), file.findValue("macAdress").asText(), file.findValue("cheminFichier").asText(), new Date());
									catalogueDeFichiersDao.create(catalogueDeFichiers);

									cataloguePartageDeFichiers = new CataloguePartageDeFichiers(catalogueDeFichiers.getIdFichier(), file.findValue("nomFichier").asText(), file.findValue("descriptionFichier").asText(), currentNetWorkUuid, file.findValue("nbreTelechargements").asLong(), file.findValue("hashFichier").asText());
									cataloguePartageDeFichiersDao.create(cataloguePartageDeFichiers);


								} catch (Exception e) {
									Log.e(DatabaseHelper.class.getName(), "echec de publication des fichiers", e);
									throw new RuntimeException(e);
								}


								buf = "OK".getBytes();

								packet = new DatagramPacket(buf, buf.length, peerAdress, PORT_PUBLICATION_FICHIERS_PAIRS);
								listenerFichiers.send(packet);


							}
							catch(SocketTimeoutException e){

							}
							catch (Exception e)
							{
								e.printStackTrace();
								Log.e("problem with root new peer listener ", "error : "+e.getMessage());


								if(listenerFichiers!=null) {
									listenerFichiers.close();
									listenerFichiers = null;

								}


							}


						}
					}

					catch(Exception e){
						e.printStackTrace();
						Log.e("problem with root new peer listener ", "error : "+e.getMessage());


						if(listenerFichiers!=null) {
							listenerFichiers.close();
							listenerFichiers = null;

						}

					}

				}



			}
		});


		t.start();
	}

	public List<String> rootListPeersConnected(){
        List<String> liste = new ArrayList<String>();
        for(CatalogueDeFichiers cdf: ((RuntimeExceptionDao<CatalogueDeFichiers, Integer>) getHelper().getSimpleDataDao(CatalogueDeFichiers.class)).queryForAll()){
            if(!liste.contains(cdf.getAdressePeer())){
                liste.add(cdf.getAdressePeer());
            }
        }
		return liste;
	}
	
	public class ReceiveTask extends AsyncTask<Void,Void,DatagramPacket> {
		int port;
		String response = "";
		CatalogService catService;
		Context context;
		SendDataTask send;
		ReceiveTask(int port,Context context) throws SQLException {
			this.port = port;
			this.context = context;
		}

		@Override
		protected DatagramPacket doInBackground(Void... voids) {
			DatagramSocket socket = null;
			byte[] buf;
			boolean flag = false;
			try {
				socket = new DatagramSocket(port);
			} catch (SocketException e) {
				e.printStackTrace();
			}
			byte[] buffer = new byte[1024];
			try {
				DatagramPacket paquet = new DatagramPacket(buffer, buffer.length); //en attente d'une requête
				while(!flag) {
					Log.d("Info ", "The Server is waiting for a messafe");
					Log.d("Info","En attente d'un message du client");
					socket.receive(paquet);
					Log.d("Info : ","Reception d'un message du client : "+paquet.toString());
					handleRequest(paquet,context);
				}
			} catch (SocketException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		}

		public void handleRequest(DatagramPacket paquet,Context context) throws SQLException, IOException {
			if(paquet !=null){
				String request = new String(paquet.getData(),0,paquet.getLength());
				Log.d("Info","la requete recu est "+request);
				if(request.equals("request_catalog")) {
					Log.e("reception : ", "Reception d'une requete");
					InetAddress clientAddress = paquet.getAddress();
					int clientPort = paquet.getPort();
					CatalogService cas = new CatalogService(context);
					//send = new SendDataTask(clientAddress, clientPort);
					DatagramSocket socket=null;
					try {
						socket = new DatagramSocket(clientPort);
					} catch (SocketException e) {
						e.printStackTrace();
					}
					List<CatalogueDeFichiers> datas = cas.sortByFreq();
					
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					ObjectOutputStream outputStream = new ObjectOutputStream(out);
					outputStream.writeObject(datas);
					outputStream.flush();
					outputStream.close();
					byte[] listData = out.toByteArray();
					DatagramPacket paq = new DatagramPacket(listData, listData.length,clientAddress,clientPort); //en attente d'une requête
					socket.setSoTimeout(60000);
					Log.d("Info","Sending catalog "+cas.sortByFreq(10).toArray().length);
					socket.send(paq);
					Log.e("reception : ", "catalogue envoye"+listData.length);
				}else {
					Log.e("Recherche : ", "Recherche d'un fichier");
					InetAddress clientAddress = paquet.getAddress();
					int clientPort = paquet.getPort();
					CatalogService cas = new CatalogService(context);
					DatagramSocket socket=null;
					try {
						socket = new DatagramSocket(clientPort);
					} catch (SocketException e) {
						e.printStackTrace();
					}
					List<CatalogueDeFichiers> datas = cas.search(request);
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					ObjectOutputStream outputStream = new ObjectOutputStream(out);
					outputStream.writeObject(datas);
					outputStream.flush();
					outputStream.close();
					byte[] listData = out.toByteArray();
					DatagramPacket paq = new DatagramPacket(listData, listData.length,clientAddress,clientPort); //en attente d'une requête
					socket.setSoTimeout(60000);
					Log.d("Info","Sending catalog");
					socket.send(paq);
					Log.e("reception : ", "catalogue envoye");
				}

			}
		}

	}

	public class SendDataTask extends AsyncTask<Serializable,Void,Void> {
		InetAddress destAddr;
		int destPort;
		String response = "";
		SendDataTask(InetAddress addr, int port) {
			destAddr = addr;
			destPort = port;
		}

		@Override
		protected Void doInBackground(Serializable... datas) {
			Log.d("Info","Envoi du resusltat au client");
			DatagramSocket socket = null;
			byte[] buf;
			boolean flag = false;
			try {
				socket = new DatagramSocket(destPort);
			} catch (SocketException e) {
				e.printStackTrace();
			}
			buf = new byte[64000];
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				ObjectOutputStream outputStream = new ObjectOutputStream(out);
				outputStream.writeObject(datas);
				outputStream.close();
				byte[] listData = out.toByteArray();
				DatagramPacket paquet = new DatagramPacket(listData, listData.length, destAddr,destPort); //en attente d'une requête
				socket.setSoTimeout(60000);
				Log.d("Info","Sending catalog");
				socket.send(paquet);


			} catch (SocketException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}




	public void lunchSearchService() throws Exception{ //un peer se connectera au réseau et se connectera à cette socket pour déposer ses fichiers partagés
		ReceiveTask rcv = new ReceiveTask(SetupHotspot.FILE_CATALOG_TRANSFERT_SERVER_PORT,this.context);
		rcv.execute();
	}


	public void createNearSubNetworkListener(){ //un peer se connectera au réseau et se connectera à cette socket pour déposer périodiquement la liste des sous réseaux p2p qu'il détecte


		Thread t  = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				byte[] buf;
				DatagramPacket packet;

				while(!flag){
					try{
						listenerSousReseaux = new DatagramSocket(PORT_PUBLICATION_SOUS_RESEAUX_DETECTES_PAIRS);
						listenerSousReseaux.setSoTimeout(SOCKET_TIME_OUT);

						buf = new byte[BUFFER_SIZE];

						while (listenerSousReseaux != null && !listenerSousReseaux.isClosed())
						{
							try
							{
								packet = new DatagramPacket(buf, buf.length); //en attente d'une requête
								listenerSousReseaux.receive(packet);

								String	requete = new String(packet.getData(), 0, packet.getLength());

								InetAddress peerAdress = packet.getAddress();

								//we save file informations in subnetwork databases

								if(requete.contains(MESSAGE_REINITIALISATION_SOUS_RESEAUX_DETECTES_PAIRS)){
									try {
										RuntimeExceptionDao<CatalogueSousReseaux, Integer> catalogueSousReseauxDao = (RuntimeExceptionDao<CatalogueSousReseaux, Integer>) getHelper().getSimpleDataDao(CatalogueSousReseaux.class);


										DeleteBuilder<CatalogueSousReseaux, Integer> delBuild = catalogueSousReseauxDao.deleteBuilder();
										delBuild.where().eq("adressePearPasserelle", peerAdress.getHostAddress());

										PreparedDelete<CatalogueSousReseaux> predDel = delBuild.prepare();

										catalogueSousReseauxDao.delete(predDel);

									} catch (Exception e) {
										Log.e(DatabaseHelper.class.getName(), "echec de suppression des sous reseaux detectés pour le pair "+peerAdress, e);
										throw new RuntimeException(e);
									}
								}
								else if(!requete.equalsIgnoreCase(currentNetWorkUuid.toString())){ //la chaine reçue est l'identifiant unique du sous réseau détecté; on l'ajoute au catalogue
									try {
										RuntimeExceptionDao<CatalogueSousReseaux, Integer> catalogueSousReseauxDao = (RuntimeExceptionDao<CatalogueSousReseaux, Integer>) getHelper().getSimpleDataDao(CatalogueSousReseaux.class);

										catalogueSousReseauxDao.create(new CatalogueSousReseaux(requete, peerAdress.getHostAddress()));


									} catch (Exception e) {
										Log.e(DatabaseHelper.class.getName(), "echec d'ajout du sous reseau detecté pour le pair "+peerAdress, e);
										throw new RuntimeException(e);
									}
								}




								buf = "OK".getBytes();

								packet = new DatagramPacket(buf, buf.length, peerAdress, PORT_PUBLICATION_SOUS_RESEAUX_DETECTES_PAIRS);
								listenerSousReseaux.send(packet);


							}
							catch(SocketTimeoutException e){

							}
							catch (Exception e)
							{
								e.printStackTrace();
								Log.e("echec d'ajout du sous reseau detecté pour le pair ", "error : "+e.getMessage());


								if(listenerSousReseaux!=null) {
									listenerSousReseaux.close();
									listenerSousReseaux = null;

								}

							}


						}
					}
					catch(Exception e){
						e.printStackTrace();
						Log.e("echec d'ajout du sous reseau detecté pour le pair ", "error : "+e.getMessage());


						if(listenerSousReseaux!=null) {
							listenerSousReseaux.close();
							listenerSousReseaux = null;

						}

					}
					finally{
						if(listenerSousReseaux!=null) {
							listenerSousReseaux.close();
							listenerSousReseaux = null;

						}
					}

				}



			}
		});


		t.start();
	}





	public WifiApManager createHotspot(String ssid, String password){
		try{
			WifiManager wifiManager = (WifiManager) 
					context.getSystemService(Context.WIFI_SERVICE);


			Method getConfigMethod = 
					wifiManager.getClass().getMethod("getWifiApConfiguration");
			WifiConfiguration wifiConfig = (WifiConfiguration) 
					getConfigMethod.invoke(wifiManager);

			previousSSID = wifiConfig.SSID;
			previousPassword = wifiConfig.preSharedKey;

			wifiConfig.SSID = ssid;
			wifiConfig.preSharedKey = password;



			WifiApManager wam = new WifiApManager(context);

			wam.setWifiApState(null, false);
			wam.setWifiApState(wifiConfig, true);


			Log.e("création du hotspot ", "wifi créé avec le status "+wam.getWifiApState());

			return wam;

		}catch (Exception e) {
			Log.e(DatabaseHelper.class.getName(), "Erreur lors de la création du hotspot", e);
			throw new RuntimeException(e);
		}
	}

	public void publishMyFiles(){
		//le père publie ses fichiers dans les catalogues
		try {
			RuntimeExceptionDao<CatalogueLocal, Integer> catalogueLocalDao = (RuntimeExceptionDao<CatalogueLocal, Integer>) getHelper().getSimpleDataDao(CatalogueLocal.class);
			RuntimeExceptionDao<CatalogueDeFichiers, Integer> catalogueDeFichiersDao = (RuntimeExceptionDao<CatalogueDeFichiers, Integer>) getHelper().getSimpleDataDao(CatalogueDeFichiers.class);
			RuntimeExceptionDao<CataloguePartageDeFichiers, Integer> cataloguePartageDeFichiersDao = (RuntimeExceptionDao<CataloguePartageDeFichiers, Integer>) getHelper().getSimpleDataDao(CataloguePartageDeFichiers.class);
			CatalogueDeFichiers catalogueDeFichiers; CataloguePartageDeFichiers cataloguePartageDeFichiers;

			String rootMacAdress = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getMacAddress();

			for(CatalogueLocal fichier : catalogueLocalDao.queryForAll()){
				catalogueDeFichiers = new CatalogueDeFichiers(fichier.getNomFichier(), fichier.getDescriptionFichier(), fichier.getHashFichier(), fichier.getNbreTelechargements(), ADRESSE_PERE_SOUS_RESEAU, rootMacAdress,fichier.getCheminFichier(), new Date());
				catalogueDeFichiersDao.create(catalogueDeFichiers);

				cataloguePartageDeFichiers = new CataloguePartageDeFichiers(catalogueDeFichiers.getIdFichier(), fichier.getNomFichier(), fichier.getDescriptionFichier(), currentNetWorkUuid, fichier.getNbreTelechargements(), fichier.getHashFichier());
				cataloguePartageDeFichiersDao.create(cataloguePartageDeFichiers);
			}

		} catch (Exception e) {
			Log.e(DatabaseHelper.class.getName(), "echec de publication des fichiers", e);
			throw new RuntimeException(e);
		}

	}


	public void initializeTables(){
		//initialisation des catalogues
		try {
			Log.i(DatabaseHelper.class.getName(), "reinitialisation des catalogues");

			TableUtils.dropTable(getHelper().getConnectionSource(), CatalogueDeFichiers.class, true);
			TableUtils.createTable(getHelper().getConnectionSource(), CatalogueDeFichiers.class);
			TableUtils.dropTable(getHelper().getConnectionSource(), CataloguePartageDeFichiers.class, true);
			TableUtils.createTable(getHelper().getConnectionSource(), CataloguePartageDeFichiers.class);
			TableUtils.dropTable(getHelper().getConnectionSource(), CatalogueSousReseaux.class, true);
			TableUtils.createTable(getHelper().getConnectionSource(), CatalogueSousReseaux.class);
			TableUtils.dropTable(getHelper().getConnectionSource(), CatalogueTableRoutage.class, true);
			TableUtils.createTable(getHelper().getConnectionSource(), CatalogueTableRoutage.class);


		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "echec d'initialisation des catalogues", e);
			throw new RuntimeException(e);
		}
	}


	public void close(){		
		if  (helper  !=  null)  {
			OpenHelperManager.releaseHelper();
			helper  =  null;
		}

		if(previousSSID!=null){
			createHotspot(previousSSID, previousPassword);
		}

		if(listenerSousReseaux!=null) {
			listenerSousReseaux.close();
			listenerSousReseaux = null;

		}

		if(listenerFichiers!=null) {
			listenerFichiers.close();
			listenerFichiers = null;

		}
		flag = true;
	}

	public  DatabaseHelper  getHelper()  {
		if  (helper  ==  null)  {
			helper  = OpenHelperManager.getHelper(context,  DatabaseHelper.class);
		}
		return  helper;
	}
}
