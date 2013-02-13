package com.askcs.slush;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.askcs.slush.util.BasicServlet;
import com.askcs.slush.util.MadProps;

@SuppressWarnings( "serial" ) // TODO use serialVersionUID in releases?
public class Servlet
extends BasicServlet {
	
	
	
	// private static final long serialVersionUID = 9094700032521436600L;
	
	
	
	public Servlet() {
		super();
	}
	
	
	
	@Override
	protected MadProps getDefaults( ServletContext ctx, ServletConfig cfg ) {
		if ( defaults == null ) {
			super.getDefaults( ctx, cfg );
			defaults.setProperty( "prefix", "?" );
		}
		return defaults;
	}
	
	
	
	@Override
	public void init( ServletConfig cfg )
	throws ServletException {
		super.init( cfg );
		
	}
	
	
	
	@Override
	public void service( HttpServletRequest req, HttpServletResponse res )
	throws IOException, ServletException {
		req.setAttribute( "form", req.getParameter( "!" ) != null );
		super.service( req, res ); // this will delegate to doGet, doPost etc.
	}
	
	
	
	@Override
	public void doOptions( HttpServletRequest req, HttpServletResponse res ) {
		logger.info( "options" );
		
	}
	
	
	
	@Override
	public void doHead( HttpServletRequest req, HttpServletResponse res ) {
		logger.info( "head" );
	}
	
	
	
	@Override
	public void doGet( HttpServletRequest req, HttpServletResponse res )
	throws IOException {
		logger.info( "get" );
		
		String prefix = (String) req.getAttribute( "prefix" );
		String path = (String) req.getAttribute( "path" );
		String query = (String) req.getAttribute( "query" );
		Store store = (Store) req.getAttribute( "store" );
		
		if ( (Boolean) req.getAttribute( "form" ) ) {
			
			res.setContentType( "text/html" );
			res.setCharacterEncoding( "UTF-8" );
			PrintWriter w = res.getWriter();
			
			// Eeeeew
			w.print(
"<!DOCTYPE html>"
+ "<html>"
+ "<head>"
	+ "<meta charset=\"UTF-8\" />"
	+ "<title>!"+ path + "</title>"
+ "</head>"
+ "<body>"
	+ "<form action=\"" + store.createUploadUrl( prefix + path + query )
	+ "\" method=\"post\" enctype=\"multipart/form-data\">"
		+ "<input type=\"file\" name=\"file\" />"
		+ "<hr />"
		+ "<input type=\"button\" value=\"cancel\""
		+ " onclick=\"window.location='" + prefix + path + stripQuery( query ) + "'\" />"
		+ "<input type=\"submit\" value=\"submit\" />"
	+ "</form>"
+ "</body>"
+ "</html>" 
			);
			
		} else {
			
			@SuppressWarnings("unchecked")
			Map<String, String> headers = (Map<String, String>) req.getAttribute( "headers" );
			Item item = store.read( path );
			if ( item == null ) {
				res.sendError( 404 );
			} else {
				if ( item.type == null ) {
					// TODO we could write this HTML to the blobstore too?
					// although it will be a hassle to keep it up to date...
					store.handleDirectory( item, res );
				} else {
					try {
						String head;
						if ( ( (head = headers.get( "if-none-match" )) != null && head.equals( item.etag ) )
						|| ( (head = headers.get( "if-modified-since" )) != null && RFC2822.parse( head ).after( item.modified ) ) ) {
							// System.out.println("cached version for " + item.path );
							res.setStatus( 304 );
						} else {
							res.setHeader( "Etag", item.etag );
							res.setHeader( "Last-Modified", RFC2822.format( item.modified ) );
							res.setHeader( "Cache-Control", "max-age=3600, public, must-revalidate" );
							res.setHeader( "Expires", RFC2822.format( new Date( System.currentTimeMillis() + 3600 * 1000 ) ) );
							res.setHeader( "Content-Type", item.type );
							store.handleDownload( item, res );
						}
					} catch ( ParseException x ) {
						// this happens when request header contains malformed date..
						res.sendError( 400 );
					}
				}
			}
			
		}
		
	}
	
	
	
	@Override
	public void doPost( HttpServletRequest req, HttpServletResponse res )
	throws IOException {
		logger.info( "post" );
		
		String prefix = (String) req.getAttribute( "prefix" );
		String path = (String) req.getAttribute( "path" );
		String query = (String) req.getAttribute( "query" );
		Store store = (Store) req.getAttribute( "store" );
		
		if ( (Boolean) req.getAttribute( "form" ) ) {
			query = stripQuery( query );
			store.handleUpload( path + query, req );
			res.sendRedirect( prefix + path );
		} else {
			res.sendError( 400 ); // TODO REST upload
		}
	}
	
	
	
	@Override
	public void doPut( HttpServletRequest req, HttpServletResponse res ) {
		logger.info( "put" );
	}
	
	
	
	@Override
	public void doDelete( HttpServletRequest req, HttpServletResponse res ) {
		logger.info( "delete" );
	}
	
	
	
	private String stripQuery( String query ) {
		int i = query.indexOf( "!" );
		int j = query.indexOf( "&", i + 1 );
		if ( j > 0 ) {
			query = query.substring( 0, i ) + query.substring( j + 1 );
		} else {
			query = query.substring( 0, i );
		}
		if ( query.endsWith( "&" ) ) {
			query = query.substring( 0, query.length() - 1 );
		}
		if ( query.endsWith( "?" ) ) {
			query = query.substring( 0, query.length() - 1 );
		}
		return query;
	}
	
}
