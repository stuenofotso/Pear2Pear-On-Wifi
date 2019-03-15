package com.polytech.pear2pear.models;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/** a la reception de la liste des reseaux vues par un reseau voisin, on supprime toutes les entrées de ce catalogue
*qui ont pour sous reseau passerelle ce dernier, avec pour nombre de sauts 2 et on ajoute au catalogue
* tous les reseaux non directement accessibles, avec pour nombre de sauts 2 et pour reseau passerelle ce dernier.
*
*a la reception de la table de routage d'un reseau voisin, on supprime toutes les entrées de ce catalogue qui ont
*pour sous reseau passerelle ce dernier, avec un nombre de sauts >2 et on ajoute au catalogue tous les reseaux non
*directement accessibles, avec pour nombre de sauts le nombre de sauts lu dans la table de routage du voisin, incrémenté de 1
*/
@DatabaseTable
public class CatalogueTableRoutage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2267241911923750964L;
	
	@DatabaseField(generatedId = true, allowGeneratedIdInsert=true)
	private long idCatalogue;
	@DatabaseField
	private int nbreSauts ;	
	@DatabaseField
	private String identifiantUniqueSousReseau ;
	@DatabaseField
	private String identifiantUniqueSousReseauPasserelle ;
	public long getIdCatalogue() {
		return idCatalogue;
	}
	public void setIdCatalogue(long idCatalogue) {
		this.idCatalogue = idCatalogue;
	}
	public int getNbreSauts() {
		return nbreSauts;
	}
	public void setNbreSauts(int nbreSauts) {
		this.nbreSauts = nbreSauts;
	}
	public String getIdentifiantUniqueSousReseau() {
		return identifiantUniqueSousReseau;
	}
	public void setIdentifiantUniqueSousReseau(String identifiantUniqueSousReseau) {
		this.identifiantUniqueSousReseau = identifiantUniqueSousReseau;
	}
	public String getIdentifiantUniqueSousReseauPasserelle() {
		return identifiantUniqueSousReseauPasserelle;
	}
	public void setIdentifiantUniqueSousReseauPasserelle(
			String identifiantUniqueSousReseauPasserelle) {
		this.identifiantUniqueSousReseauPasserelle = identifiantUniqueSousReseauPasserelle;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public CatalogueTableRoutage(long idCatalogue, int nbreSauts,
			String identifiantUniqueSousReseau,
			String identifiantUniqueSousReseauPasserelle) {
		super();
		this.idCatalogue = idCatalogue;
		this.nbreSauts = nbreSauts;
		this.identifiantUniqueSousReseau = identifiantUniqueSousReseau;
		this.identifiantUniqueSousReseauPasserelle = identifiantUniqueSousReseauPasserelle;
	}
	public CatalogueTableRoutage(int nbreSauts,
			String identifiantUniqueSousReseau,
			String identifiantUniqueSousReseauPasserelle) {
		super();
		this.nbreSauts = nbreSauts;
		this.identifiantUniqueSousReseau = identifiantUniqueSousReseau;
		this.identifiantUniqueSousReseauPasserelle = identifiantUniqueSousReseauPasserelle;
		this.idCatalogue = 0;
	}
	public CatalogueTableRoutage() {
		super();
	}
	
	
	
	
	
}
