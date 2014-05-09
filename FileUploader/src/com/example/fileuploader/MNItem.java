package com.example.fileuploader;

public class MNItem{
	String id;
	String name;
	String category;
	String description;
	String address;
	String image;
	
	public MNItem(){
		super();
	}
	public MNItem(String id, String name, String category, String description, String address, String image){
		this.id = id;
		this.name = name;
		this.category = category;
		this.description = description;
		this.address = address;
		this.image = image;
	}
}