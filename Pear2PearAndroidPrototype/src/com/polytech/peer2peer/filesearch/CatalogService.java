package com.polytech.peer2peer.filesearch;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.polytech.pear2pear.dbadapters.DatabaseHelper;
import com.polytech.pear2pear.models.CatalogueDeFichiers;

/**
 * Created by Florentin on 12/12/2015.
 */
public class CatalogService {
    private RuntimeExceptionDao<CatalogueDeFichiers, Integer> catDao;
    DatabaseHelper dbHelper;
    public CatalogService(Context context) throws SQLException {
        dbHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
   //     Dao<CatalogueDeFichiers, Long> catDao = dbHelper.getDao(CatalogueDeFichiers.class);
        catDao = (RuntimeExceptionDao<CatalogueDeFichiers, Integer>) dbHelper.getSimpleDataDao(CatalogueDeFichiers.class);
		
        if(catDao == null){
    		Log.d("Info","catDao est null on ne peut rien y faire");
    	}
    }

    public List<CatalogueDeFichiers> sortByFreq(int start, int limit) throws SQLException {
        return catDao.queryBuilder().orderBy("nbreTelechargements",false).offset(start).limit(limit).query();
    }

    public List<CatalogueDeFichiers> search(String name) throws SQLException {
        return catDao.queryBuilder().where().like("nomFichier","%"+name+"%").or().like("descriptionFichier","%"+name+"%").query();
    }

    public List<CatalogueDeFichiers> sortByFreq() throws SQLException {
        return catDao.queryBuilder().orderBy("nbreTelechargements",false).limit(10).query();
    }

    public List<CatalogueDeFichiers> sortByFreq(int limit) throws SQLException {
    	if(catDao == null){
    		Log.d("Info","catDao est null on ne peut rien y faire");
    	}
    	return catDao.queryBuilder().orderBy("nbreTelechargements",false).limit(limit).query();
    }

}
