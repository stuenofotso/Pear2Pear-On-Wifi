package com.polytech.peer2peer.util;

public class RowItem {
	private int imageId;
	private int download;
	private String title;
	private String desc;
	private long nbreTelechargements;
	
	public RowItem(int imageId, String title, String desc, int download, long nbreTelechargements) {
		this.imageId = imageId;
		this.title = title;
		this.desc = desc;
		this.download=download;
		this.nbreTelechargements = nbreTelechargements;
	}
	
	
	public long getNbreTelechargements() {
		return nbreTelechargements;
	}


	public void setNbreTelechargements(long nbreTelechargements) {
		this.nbreTelechargements = nbreTelechargements;
	}


	public int getImageId() {
		return imageId;
	}
	public void setImageId(int imageId) {
		this.imageId = imageId;
	}
	
	public int getDownload() {
		return download;
	}
	public void setDownload(int download) {
		this.download = download;
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
	@Override
	public String toString() {
		return title + "\n" + desc;
	}	
	
}
