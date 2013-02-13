package com.askcs.slush.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Properties;

@SuppressWarnings( "serial" ) // TODO use serialVersionUID in releases?
public class MadProps
extends Properties {
	
	// private static final long serialVersionUID = 1646917839757849716L;
	
	protected HashMap<String, Object> parsed = new HashMap<String, Object>( 32, 0.75F );
	
	public MadProps() {
		this( null );
	}
	
	public MadProps( MadProps defaults ) {
		super( defaults );
	}
	
	
	
	@Override
	public Object setProperty( String key, String value ) {
		parsed.remove( key );
		return super.setProperty( key, value );
	}
	
	
	public <T> T getProperty( String key, Class<T> type ) {
		Object value = parsed.get( key );
		if ( value != null ) {
			try {
				return type.cast( value );
			} catch ( ClassCastException cce ) {
				// fall through to ParseException below
			}
		} else {
			try {
				Method method = this.getClass().getMethod( "get" + type.getSimpleName(), String.class );
				value = method.invoke( this, key );
				if ( value == null ) {
					return null;
				}
				return type.cast( value );
			} catch (NoSuchMethodException e) {
				// fall through to ParseException below 
			} catch (IllegalArgumentException e) {
				// 
			} catch (IllegalAccessException e) {
				// 
			} catch (InvocationTargetException e) {
				// 
			}
		}
		throw new IllegalArgumentException( "\"" + key + "\": can't parse value \""
		+ getProperty( key ) + " as " + type.getSimpleName() );
	}
	
	
	
	public String getStringProperty( String key ) {
		return getProperty( key );
	}
	
	
	
	public Character getCharacterProperty( String key ) {
		String string = getProperty( key );
		if ( string == null || string.length() == 0 ) {
			return null;
		}
		if ( string.length() == 1 ) {
			return Character.valueOf( string.charAt( 0 ) );
		}
		string = string.trim();
		if ( string.length() > 1 && (string = string.toLowerCase()).charAt( 0 ) == '\\' ) {
			char c = string.charAt( 1 );
			if ( string.length() == 2 && c == '0' ) {
				return Character.valueOf( '\0' );
			} else if ( string.length() <= 6 && ( c == 'u' || c == 'U' ) ) {
				try {
					return Character.valueOf( (char) Integer.parseInt( string.substring( 2 ), 16 ) );
				} catch ( NumberFormatException nfe ) {
				}
			}
		} else if ( string.length() > 0 ) {
			return Character.valueOf( string.charAt( 0 ) );
		}
		throw new IllegalArgumentException( "\"" + key + "\": can't parse value \""
		+ getProperty( key ) + " as Character" );
	}
	
	
	
	public Boolean getBooleanProperty( String key ) {
		String string = getProperty( key );
		if ( string == null || (string = string.trim().toLowerCase()).length() == 0 ) {
			return null;
		}
		if ( string.equals( "true" ) || string.equals( "yes" )
		|| (string.length() == 1 && "ty1".indexOf( string.charAt( 0 ) ) >= 0 ) ) {
			return Boolean.TRUE;
		} else if ( string.equals( "false" ) || string.equals( "no" )
		|| (string.length() == 1 && "fn0".indexOf( string.charAt( 0 ) ) >= 0 ) ) {
			return Boolean.FALSE;
		}
		throw new IllegalArgumentException( "\"" + key + "\": can't parse value \""
		+ getProperty( key ) + " as Boolean" );
	}
	
	
	
	public Byte getByteProperty( String key ) {
		String string = getProperty( key );
		if ( string == null || (string = string.trim().toLowerCase()).length() == 0 ) {
			return null;
		}
		throw new IllegalArgumentException( "\"" + key + "\": can't parse value \""
		+ getProperty( key ) + " as Byte" );
	}
	
	
	
}
