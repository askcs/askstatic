package com.askcs.slush;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Store {
	
	
	public Item create( String path, String parent );
	
	public Item read( String path );
	
	public Item update( Item item );
	
	public void save( Item item );
	
	public void delete( Item item );
	
	public String createUploadUrl( String retpath );
	
	public void handleUpload(String path, HttpServletRequest req);
	
	public void handleDownload( Item item, HttpServletResponse res ) throws IOException;

	public void handleDirectory( Item item, HttpServletResponse res ) throws IOException;
	
}
