package com.polytech.pear2pear.models;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class CataloguePartageDeFichiers implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2267241911923750964L;
	
	@DatabaseField(generatedId = true, allowGeneratedIdInsert=true)
	private long idCatalogue;
	@DatabaseField
	private long idFichier ;
	@DatabaseField
	private String nomFichier;
	@DatabaseField
	private String descriptionFichier;
	@DatabaseField
	private String identifiantUniqueSousReseau ;
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
	public long getIdFichier() {
		return idFichier;
	}
	public void setIdFichier(long idFichier) {
		this.idFichier = idFichier;
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
	public String getIdentifiantUniqueSousReseau() {
		return identifiantUniqueSousReseau;
	}
	public void setIdentifiantUniqueSousReseau(String identifiantUniqueSousReseau) {
		this.identifiantUniqueSousReseau = identifiantUniqueSousReseau;
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
	
	
	public CataloguePartageDeFichiers(long idFichier, String nomFichier,
			String descriptionFichier, String identifiantUniqueSousReseau,
			long nbreTelechargements, String hashFichier) {
		super();
		this.idFichier = idFichier;
		this.nomFichier = nomFichier;
		this.descriptionFichier = descriptionFichier;
		this.identifiantUniqueSousReseau = identifiantUniqueSousReseau;
		this.nbreTelechargements = nbreTelechargements;
		this.hashFichier = hashFichier;
		this.idCatalogue = 0;
	}
	public CataloguePartageDeFichiers(long idCatalogue, long idFichier,
			String nomFichier, String descriptionFichier,
			String identifiantUniqueSousReseau, long nbreTelechargements,
			String hashFichier) {
		super();
		this.idCatalogue = idCatalogue;
		this.idFichier = idFichier;
		this.nomFichier = nomFichier;
		this.descriptionFichier = descriptionFichier;
		this.identifiantUniqueSousReseau = identifiantUniqueSousReseau;
		this.nbreTelechargements = nbreTelechargements;
		this.hashFichier = hashFichier;
	}
	public CataloguePartageDeFichiers() {
		super();
	}
	
	
	
	
	
}
