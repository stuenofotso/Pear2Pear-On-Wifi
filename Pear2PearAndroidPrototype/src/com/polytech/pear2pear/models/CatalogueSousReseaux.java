package com.polytech.pear2pear.models;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class CatalogueSousReseaux implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2267241911923750964L;
	
	@DatabaseField(generatedId = true, allowGeneratedIdInsert=true)
	private long idCatalogue;
	@DatabaseField
	private String identifiantUniqueSousReseau ;
	@DatabaseField
	private String adressePearPasserelle;
	public long getIdCatalogue() {
		return idCatalogue;
	}
	public void setIdCatalogue(long idCatalogue) {
		this.idCatalogue = idCatalogue;
	}
	public String getIdentifiantUniqueSousReseau() {
		return identifiantUniqueSousReseau;
	}
	public void setIdentifiantUniqueSousReseau(String identifiantUniqueSousReseau) {
		this.identifiantUniqueSousReseau = identifiantUniqueSousReseau;
	}
	public String getAdressePearPasserelle() {
		return adressePearPasserelle;
	}
	public void setAdressePearPasserelle(String adressePearPasserelle) {
		this.adressePearPasserelle = adressePearPasserelle;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public CatalogueSousReseaux(long idCatalogue,
			String identifiantUniqueSousReseau, String adressePearPasserelle) {
		super();
		this.idCatalogue = idCatalogue;
		this.identifiantUniqueSousReseau = identifiantUniqueSousReseau;
		this.adressePearPasserelle = adressePearPasserelle;
	}
	public CatalogueSousReseaux(String identifiantUniqueSousReseau,
			String adressePearPasserelle) {
		super();
		this.identifiantUniqueSousReseau = identifiantUniqueSousReseau;
		this.adressePearPasserelle = adressePearPasserelle;
		this.idCatalogue = 0;
	}
	public CatalogueSousReseaux() {
		super();
	}
	
	
	
	
	
}
