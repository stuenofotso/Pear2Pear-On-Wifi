package com.polytech.pear2pear.dbadapters;

import java.io.IOException;
import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;
import com.polytech.pear2pear.models.CatalogueDeFichiers;
import com.polytech.pear2pear.models.CatalogueLocal;
import com.polytech.pear2pear.models.CataloguePartageDeFichiers;
import com.polytech.pear2pear.models.CatalogueSousReseaux;
import com.polytech.pear2pear.models.CatalogueTableRoutage;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {
	
	public  static  final  Class<?>[]  classes  =  new  Class[]  {
		CatalogueDeFichiers.class,
		CatalogueLocal.class,
		CataloguePartageDeFichiers.class,
		CatalogueSousReseaux.class,
		CatalogueTableRoutage.class,
	
		};

	public static void main(String[] args) throws SQLException, IOException {
		writeConfigFile("ormlite_config.txt", classes);
		
	}
}
