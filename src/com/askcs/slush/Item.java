package com.askcs.slush;

import java.io.Serializable;
import java.util.Date;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.code.twig.annotation.Id;
import com.google.code.twig.annotation.Parent;

@SuppressWarnings( "serial" ) // TODO use serialVersionUID in releases?
public class Item
implements Serializable {
	
	// private static final long serialVersionUID = -2615612848148728107L;
	
	// TODO ugly, I didn't want any twig specifics in here... But subclassing
	// doesn't work?
		
	@Id public String path;
	public String parent;
	public BlobKey key;
	
	public Date created;
	public Date modified;
	
	public String etag;
	public String type; // type == null means directory i.s.o. regular file
	public long length;
	
	
	public Item() {
		this( null, null );
	}
	
	public Item( String path, String parent ) {
		this.path = path;
		this.parent = parent;
	}
	
	
}
