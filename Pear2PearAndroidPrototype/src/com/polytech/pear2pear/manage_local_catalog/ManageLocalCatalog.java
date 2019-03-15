package com.polytech.pear2pear.manage_local_catalog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.polytech.pear2pear.dbadapters.DatabaseHelper;
import com.polytech.pear2pear.models.CatalogueLocal;

/**
 * Sample Android UI activity which displays a text window when it is run.
 */
public class ManageLocalCatalog {

	/**
	 * 
	 * @param is
	 * @return
	 * @throws Exception
	 * 
	 *             Cette fonction permet de convertir un flux de lecture de
	 *             fichier en chaine de caracteres. Il fait partir du processus
	 *             de conversion d'un fichier en chaine de caractere
	 */
	public static String convertStreamToString(final InputStream is)
			throws Exception {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				is));
		final StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line).append("\n");
		}
		reader.close();
		return sb.toString();
	}

	/**
	 * 
	 * @param filePath
	 * @return
	 * 
	 *         La fonction de hashage du fichier en question
	 */
	public static String getMD5EncryptedString(final String filePath) {
		// String str = FileUtils.readFileToString(file);
		String encTarget = null;
		try {
			encTarget = getStringFromFile(filePath);
		} catch (final Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		MessageDigest mdEnc = null;
		try {
			mdEnc = MessageDigest.getInstance("MD5");
		} catch (final NoSuchAlgorithmException e) {
			System.out.println("Exception while encrypting to md5");
			e.printStackTrace();
		} // Encryption algorithm
		
		mdEnc.update(encTarget.getBytes(), 0, encTarget.length());
		String md5 = new BigInteger(1, mdEnc.digest()).toString(16);
		while (md5.length() < 32) {
			md5 = "0" + md5;
		}
		return md5;
	}

	/**
	 * 
	 * @param filePath
	 * @return
	 * @throws Exception
	 * 
	 *             Conversion d'un fichier en chaine de caratere
	 */
	public static String getStringFromFile(String filePath) throws Exception {
//		filePath = Environment.getExternalStorageDirectory().toString() + "/"
	//			+ filePath;
		final File fl = new File(filePath);
		final FileInputStream fin = new FileInputStream(fl);
		final String ret = convertStreamToString(fin);
		// Make sure you close all streams.
		fin.close();
		return ret;
	}

	public static boolean validateId(final long idCatalogue) {
		if (idCatalogue >= 0) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean validateName(final String name) {
		if (!name.equals("")) {
			return true;
		} else {
			return false;
		}
	}

	private final Context context;

	private final boolean alreadyConnected = false;
	private final boolean root = false;

	private DatabaseHelper helper;
 

	private final String LOG_TAG = getClass().getSimpleName();

	public ManageLocalCatalog(final Context context) {
		// TODO Auto-generated constructor stub
		this.context = context; 
	}

	/***
	 * 
	 * @param nom_fichier
	 * @param description_fichier
	 * @param chemin_fichier
	 * @param nombre_telechargement
	 * @param tv
	 * 
	 *            Ajout d'un element dans le catalog local
	 */
	public CatalogueLocal addCatalog(final String nom_fichier,final String description_fichier, final String chemin_fichier) {
		// get our dao
		if (!validateName(chemin_fichier) || !validateName(nom_fichier)) {
			return null;
		}
		String hash_fichier = null;
		final Class<CatalogueLocal> n = CatalogueLocal.class;
		RuntimeExceptionDao<CatalogueLocal, Integer> simpleDao = null;
		simpleDao = (RuntimeExceptionDao<CatalogueLocal, Integer>) getHelper().getSimpleDataDao(n);
		// our string builder for building the content-view
		final StringBuilder sb = new StringBuilder();
		// create a new simple object
		final CatalogueLocal simple = new CatalogueLocal();
		simple.setNomFichier(nom_fichier);
		simple.setDescriptionFichier(description_fichier);
		simple.setCheminFichier(chemin_fichier);
		simple.setNbreTelechargements(0);
		hash_fichier = getMD5EncryptedString(chemin_fichier);
		// Log.i(LOG_TAG, "HASH :" + hash_fichier);
		simple.setHashFichier(hash_fichier);
		// store it in the database
		simpleDao.create(simple);

		try {
			Thread.sleep(5);
		} catch (final InterruptedException e) {
			// ignore
		}
		return simple;
	}

	
	public void close() {
		if (helper != null) {
			OpenHelperManager.releaseHelper();
			helper = null;
		}
	}

	private void deleteCatalog(final long idCatalogue) {
		// get our dao
		if (!validateId(idCatalogue)) {
			return;
		}

		if (!existInDB(idCatalogue)) {
			return;
		}
		RuntimeExceptionDao<CatalogueLocal, Integer> simpleDao = null;
		final Class<CatalogueLocal> n = CatalogueLocal.class;
		simpleDao = (RuntimeExceptionDao<CatalogueLocal, Integer>) getHelper()
				.getSimpleDataDao(n);

		final StringBuilder sb = new StringBuilder();

		sb.append("Deleted ids:");
		final CatalogueLocal simple = new CatalogueLocal();
		simple.setIdCatalogue(idCatalogue);
		simpleDao.delete(simple);
		sb.append(' ').append(simple.getIdCatalogue());
		Log.i(LOG_TAG, "deleting simple(" + simple.getIdCatalogue() + ")");
		sb.append('\n');
		sb.append("------------------------------------------\n");

		// tv.setText(sb.toString());
		Log.i(LOG_TAG, "Done with page at " + System.currentTimeMillis());
	}

	public boolean existInDB(final long id) {
		boolean flag = false;
		final Class<CatalogueLocal> n = CatalogueLocal.class;
		RuntimeExceptionDao<CatalogueLocal, Integer> simpleDao = null;
		simpleDao = (RuntimeExceptionDao<CatalogueLocal, Integer>) getHelper()
				.getSimpleDataDao(n);
		// our string builder for building the content-view
		final StringBuilder sb = new StringBuilder();
		// create a new simple object
		final CatalogueLocal simple = new CatalogueLocal();
		final List<CatalogueLocal> list = simpleDao.queryForAll();

		// if we already have items in the database
		final int simpleC = 1;
		for (final CatalogueLocal simple1 : list) {
			if (simple1.getIdCatalogue() == id) {
				flag = true;
				break;
			}
		}
		return flag;

	}

	private DatabaseHelper getHelper() {
		if (helper == null) {
			helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
		}
		return helper;
	}

	// ////////////
	/**
	 * 
	 * @param tv
	 * @return Renvoie les elements dans le catalogue local sous forme de list
	 */
	public List<CatalogueLocal> listCatalog() {
		final Class<CatalogueLocal> n = CatalogueLocal.class;
		RuntimeExceptionDao<CatalogueLocal, Integer> simpleDao = null;
		simpleDao = (RuntimeExceptionDao<CatalogueLocal, Integer>) getHelper().getSimpleDataDao(n);
		// our string builder for building the content-view
		final StringBuilder sb = new StringBuilder();
		// create a new simple object
		final CatalogueLocal simple = new CatalogueLocal();
		final List<CatalogueLocal> list = simpleDao.queryForAll();
		sb.append("Found ").append(list.size()).append(" entries in DB in ")
				.append("list").append("()\n");

		// if we already have items in the database
		int simpleC = 1;
		for (final CatalogueLocal simple1 : list) {
			sb.append('#').append(simpleC).append(": ")
					.append(simple1.getNomFichier()).append('\n');
			simpleC++;
		}
		sb.append("------------------------------------------\n");
		try {
			Thread.sleep(5);
		} catch (final InterruptedException e) {
			// ignore
		}
		return list;
	}
}