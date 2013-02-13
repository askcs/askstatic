package com.askcs.slush.store;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.askcs.slush.Item;
import com.askcs.slush.Store;

public class FileStore
implements Store {
	
	@Override
	public Item create( String path, String parent ) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Item read( String path ) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Item update( Item item ) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void save( Item item ) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void delete( Item item ) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String createUploadUrl( String retpath ) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public void handleUpload( String path, HttpServletRequest req ) {
		// TODO Auto-generated method stub
	}
	
	
	@Override
	public void handleDownload( Item item, HttpServletResponse res ) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void handleDirectory( Item item, HttpServletResponse res ) {
		// TODO Auto-generated method stub
	}
	
	
}
