package com.polytech.pear2pear.dbadapters;

import java.sql.SQLException;
import java.util.HashMap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.polytech.peer2peer.R;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	// name of the database file for your application -- change to something appropriate for your app
	private static final String DATABASE_NAME = "pear2pear.db";
	// any time you make changes to your database objects, you may have to increase the database version
	private static final int DATABASE_VERSION = 1;

	// the DAO object we use to access the TagWord table
	private HashMap<Class<?>, Object> simpleDaos = new HashMap<Class<?>, Object>();
	private HashMap<Class<?>, Object> simpleRuntimeDaos = new HashMap<Class<?>, Object>();


	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
		
		for(Class<?> classe: DatabaseConfigUtil.classes){
			simpleDaos.put(classe, null);
			simpleRuntimeDaos.put(classe, null);
		}
	}

	/**
	 * This is called when the database is first created. Usually you should call createTable statements here to create
	 * the tables that will store your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			
			for(Class<?> classe: DatabaseConfigUtil.classes){
				TableUtils.createTable(connectionSource, classe);
			}
			
	
		
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}

	}

	/**
	 * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
	 * the various data to match the new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onUpgrade");
			
			for(Class<?> classe: DatabaseConfigUtil.classes){
				TableUtils.dropTable(connectionSource, classe, true);
			}
			
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the Database Access Object (DAO) for our TagWord class. It will create it or just give the cached
	 * value.
	 */
	public Object getDaos(Class<?> classe) throws SQLException {
		
//		private Dao<TagWord, Integer> simpleDaoz = null;
//		private RuntimeExceptionDao<TagWord, Integer> simpleRuntimeDaoz = null;
//		
		if (simpleDaos.get(classe) == null) {
			simpleDaos.put(classe, getDao(classe));
		}
		return simpleDaos.get(classe);
	}

	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our TagWord class. It will
	 * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public Object getSimpleDataDao(Class<?> classe) {
		if (simpleRuntimeDaos.get(classe) == null) {
			simpleRuntimeDaos.put(classe, getRuntimeExceptionDao(classe));
		}
		return simpleRuntimeDaos.get(classe);
	}

	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
		
		
		for(Class<?> classe: simpleDaos.keySet()){
			simpleDaos.put(classe, null);
		}
		
		for(Class<?> classe: simpleRuntimeDaos.keySet()){
			simpleRuntimeDaos.put(classe, null);
		}
	}
}
