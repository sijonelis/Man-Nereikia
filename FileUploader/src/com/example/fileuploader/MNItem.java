package com.example.fileuploader;

import android.graphics.Bitmap;

public class MNItem{
	String id;
	String name;
	String category;
	String description;
	String address;
	String image;
	Bitmap imageBitmap;
	
	public MNItem(){
		super();
	}
	public MNItem(String id, String name, String category, String description, String address, String image, Bitmap imageBitmap){
		this.id = id;
		this.name = name;
		this.category = category;
		this.description = description;
		this.address = address;
		this.image = image;
		this.imageBitmap = imageBitmap;
	}
}