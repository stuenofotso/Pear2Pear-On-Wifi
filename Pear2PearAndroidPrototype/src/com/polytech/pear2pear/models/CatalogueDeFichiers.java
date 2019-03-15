package com.polytech.pear2pear.models;

import java.io.Serializable;
import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class CatalogueDeFichiers implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2267241911923750964L;
	

	@DatabaseField(generatedId = true, allowGeneratedIdInsert=true)
	private long idFichier ;
	@DatabaseField
	private String nomFichier;
	@DatabaseField
	private String descriptionFichier;
	@DatabaseField
	private String hashFichier;
	@DatabaseField
	private long nbreTelechargements;
	@DatabaseField
	private String adressePeer ;
	@DatabaseField
	private String adresseMacPeer;
	@DatabaseField
	private String cheminFichier;	
	@DatabaseField
	private Date datePublication; /**	lorsque l'�cart entre la date de publication et la date actuelle atteint 
	un certain seuil, c'est signe que le peer n'est plus accessible, l'entr�e devient invalide et ne sera plus renvoy�
	 � l'issue d'une recherche; sauf si le peer est le root du sous r�seau
	 */

	
	
	
	public Date getDatePublication() {
		return datePublication;
	}
	public String getAdresseMacPeer() {
		return adresseMacPeer;
	}
	public void setAdresseMacPeer(String adresseMacPeer) {
		this.adresseMacPeer = adresseMacPeer;
	}
	public void setDatePublication(Date datePublication) {
		this.datePublication = datePublication;
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
	
	public String getHashFichier() {
		return hashFichier;
	}
	public void setHashFichier(String hashFichier) {
		this.hashFichier = hashFichier;
	}
	public String getAdressePeer() {
		return adressePeer;
	}
	public void setAdressePeer(String adressePeer) {
		this.adressePeer = adressePeer;
	}
	public String getCheminFichier() {
		return cheminFichier;
	}
	public void setCheminFichier(String cheminFichier) {
		this.cheminFichier = cheminFichier;
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
	

	
	
	public CatalogueDeFichiers() {
		super();
	}
	public CatalogueDeFichiers(long idFichier, String nomFichier,
			String descriptionFichier, String hashFichier,
			long nbreTelechargements, String adressePeer,
			String adresseMacPeer, String cheminFichier, Date datePublication) {
		super();
		this.idFichier = idFichier;
		this.nomFichier = nomFichier;
		this.descriptionFichier = descriptionFichier;
		this.hashFichier = hashFichier;
		this.nbreTelechargements = nbreTelechargements;
		this.adressePeer = adressePeer;
		this.adresseMacPeer = adresseMacPeer;
		this.cheminFichier = cheminFichier;
		this.datePublication = datePublication;
	}
	public CatalogueDeFichiers(String nomFichier, String descriptionFichier,
			String hashFichier, long nbreTelechargements, String adressePeer,
			String adresseMacPeer, String cheminFichier, Date datePublication) {
		super();
		this.idFichier = 0;
		this.nomFichier = nomFichier;
		this.descriptionFichier = descriptionFichier;
		this.hashFichier = hashFichier;
		this.nbreTelechargements = nbreTelechargements;
		this.adressePeer = adressePeer;
		this.adresseMacPeer = adresseMacPeer;
		this.cheminFichier = cheminFichier;
		this.datePublication = datePublication;
	}
	
	
	
	
	
}
