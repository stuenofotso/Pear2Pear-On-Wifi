package com.polytech.peer2peer.util;

public class RowItemCatalogue {
	private int imageId;
	private String title;
	private String desc;
	private String chemin;
	public RowItemCatalogue(int imageId, String title, String desc,String chemin) {
		this.imageId = imageId;
		this.title = title;
		this.desc = desc;
		this.chemin=chemin;
	}
	public int getImageId() {
		return imageId;
	}
	public void setImageId(int imageId) {
		this.imageId = imageId;
	}
	
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getChemin() {
		return chemin;
	}
	public void setChemin(String chemin) {
		this.chemin = chemin;
	}
	@Override
	public String toString() {
		return title + "\n" + desc;
	}	
	

}
