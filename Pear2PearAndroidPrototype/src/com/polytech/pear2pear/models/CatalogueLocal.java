package com.polytech.pear2pear.models;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class CatalogueLocal implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2267241911923750964L;
	
	@DatabaseField(generatedId = true, allowGeneratedIdInsert=true)
	private long idCatalogue;
	@DatabaseField
	private String nomFichier;
	@DatabaseField
	private String descriptionFichier;
	@DatabaseField
	private String cheminFichier ;
	@DatabaseField
	private long nbreTelechargements;
	@DatabaseField
	private String hashFichier;
	
	
	public long getIdCatalogue() {
		return idCatalogue;
	}
	public void setIdCatalogue(long idCatalogue) {
		this.idCatalogue = idCatalogue;
	}
	public String getNomFichier() {
		return nomFichier;
	}
	public void setNomFichier(String nomFichier) {
		this.nomFichier = nomFichier;
	}
	public String getDescriptionFichier() {
		return descriptionFichier;
	}
	public void setDescriptionFichier(String descriptionFichier) {
		this.descriptionFichier = descriptionFichier;
	}
	public String getCheminFichier() {
		return cheminFichier;
	}
	public void setCheminFichier(String cheminFichier) {
		this.cheminFichier = cheminFichier;
	}
	public String getHashFichier() {
		return hashFichier;
	}
	public void setHashFichier(String hashFichier) {
		this.hashFichier = hashFichier;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
	public long getNbreTelechargements() {
		return nbreTelechargements;
	}
	public void setNbreTelechargements(long nbreTelechargements) {
		this.nbreTelechargements = nbreTelechargements;
	}

	public CatalogueLocal(String nomFichier, String descriptionFichier,
			String cheminFichier,long nbreTelechargements, String hashFichier) {
		super();
		this.nomFichier = nomFichier;
		this.descriptionFichier = descriptionFichier;
		this.cheminFichier = cheminFichier;
		this.hashFichier = hashFichier;
		this.nbreTelechargements = nbreTelechargements;
		this.idCatalogue = 0;
	}
	
	
	
	public CatalogueLocal(long idCatalogue, String nomFichier,
			String descriptionFichier, String cheminFichier,
			long nbreTelechargements, String hashFichier) {
		super();
		this.idCatalogue = idCatalogue;
		this.nomFichier = nomFichier;
		this.descriptionFichier = descriptionFichier;
		this.cheminFichier = cheminFichier;
		this.nbreTelechargements = nbreTelechargements;
		this.hashFichier = hashFichier;
	}
	public CatalogueLocal() {
		super();
	}
	
	
	
	
	
}
