package com.askcs.slush.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.askcs.slush.Store;
import com.askcs.slush.store.FileStore;
import com.askcs.slush.store.TwigStore;

@SuppressWarnings( "serial" ) // TODO use serialVersionUID in releases?
public abstract class BasicServlet
extends HttpServlet {
	
	
	// private static final long serialVersionUID = -1602548867085837307L;
	
	protected static final Logger logger = Logger.getLogger( "com.askcs.slush" ); 
	
	final static public SimpleDateFormat RFC2822 = new SimpleDateFormat( "EEE, dd MMM yyyy HH:mm:ss Z" );
	
	protected MadProps defaults;
	protected MadProps settings;
	
	public BasicServlet() {
		super();
	}
	
	
	
	protected MadProps getSettings() {
		return settings;
	}
	
	
	
	protected MadProps getDefaults( ServletContext ctx, ServletConfig cfg ) {
		if ( defaults == null ) {
			defaults = new MadProps();
			defaults.setProperty( "debug", "false" );
			defaults.setProperty( "gae", ctx.getServerInfo()
			.toLowerCase().indexOf( "google app engine" ) >= 0 ? "true" : "false" );
		}
		return defaults;
	}
	
	
	
	@Override
	public void init( ServletConfig cfg )
	throws ServletException {
		super.init( cfg );
		
		getDefaults( cfg.getServletContext(), cfg );
		settings = new MadProps( defaults );
		
		String file = cfg.getInitParameter( "settings" );
		if ( file == null || file.length() == 0 ) {
			file = "/WEB-INF/" + getServletName().toLowerCase() + ".properties";
		} else if ( file.charAt( 0 ) != '/' ) {
			file = "/WEB-INF/" + file;
		}
		try {
			settings.load( cfg.getServletContext().getResourceAsStream( file ) );
		} catch ( NullPointerException npe ) {
			throw new ServletException( "Failed to open file \"" + file + "\"", npe );
		} catch ( IOException ioe ) {
			throw new ServletException( "Failed to load settings from \"" + file + "\"", ioe );
		}
		Enumeration<?> names = cfg.getInitParameterNames();
		for ( ; names.hasMoreElements(); ) {
			String name = (String) names.nextElement();
			settings.setProperty( name, cfg.getInitParameter( name ) );
		}
		
		if ( settings.getBooleanProperty( "debug" ) ) {
			Enumeration<?> keys = settings.propertyNames();
			StringBuffer buffer = new StringBuffer( 256 );
			buffer.append( "Global settings:" );
			for ( ; keys.hasMoreElements() ; ) {
				String key = (String) keys.nextElement();
				buffer.append( "\n  " + key + " = " + settings.getProperty( key ) );
			}
			logger.info( buffer.toString() );
		}
	}
	
	
	
	@Override
	public void service( HttpServletRequest req, HttpServletResponse res )
	throws IOException, ServletException {
		MadProps settings = new MadProps( this.settings );
		Map<String, String> headers = new LinkedHashMap<String, String>( 16, 0.75F );
		@SuppressWarnings("unchecked")
		Enumeration<String> names = req.getHeaderNames();
		for ( ; names.hasMoreElements(); ) {
			String name = names.nextElement();
			headers.put( name.trim().toLowerCase(), req.getHeader( name ) );
		}
		req.setAttribute( "headers", headers );
		
		String prefix = req.getContextPath() + req.getServletPath();
		String path = req.getPathInfo();
		if ( path == null || path.length() == 0 ) {
			res.sendRedirect(  prefix + "/" );
			return;
		}
		String query = req.getQueryString();
		if ( query == null ) {
			query = "";
		} else {
			query = "?" + query;
		}
		req.setAttribute( "prefix", prefix );
		req.setAttribute( "path", path );
		req.setAttribute( "query", query );
		
		Store store;
		if ( settings.getBooleanProperty( "gae" ) ) {
			store = new TwigStore();
		} else {
			store = new FileStore();
		}
		req.setAttribute( "store", store );
		
		if ( settings.getBooleanProperty( "debug" ) ) {
			StringBuffer buffer = new StringBuffer( 256 );
			buffer.append( "\nRequest settings:" );
			@SuppressWarnings("unchecked")
			Enumeration<String> props = (Enumeration<String>) settings.propertyNames();
			for ( ; props.hasMoreElements() ; ) {
				String name = props.nextElement();
				buffer.append( "\n  " + name + " = " + settings.getProperty( name ) );
			}
			logger.info( buffer.toString() );
		}
		
		req.setAttribute( "settings", settings );
		super.service( req, res ); // this will delegate to doGet, doPost, etc.
	}
	
}
