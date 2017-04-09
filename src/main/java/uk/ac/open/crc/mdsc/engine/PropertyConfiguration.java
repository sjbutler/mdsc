/*
 mdsc -- multiple dictionary spell checker
 Copyright (C) 2014-2015 The Open University
 Based on code from Jazzy - a Java library for Spell Checking
 Copyright (C) 2001-2005 Mindaugas Idzelis
 
 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation with the 'classpath' exception, 
 either version 3 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.

 The full text of the licence can be found in the file LICENCE.txt
*/
package uk.ac.open.crc.mdsc.engine;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation class to read the properties controlling the spell engine. The
 * properties are read form the <code>configuration.properties</code> file.
 *
 * @author aim4min
 */
public class PropertyConfiguration extends Configuration {

    private static final Logger LOGGER = 
            LoggerFactory.getLogger( PropertyConfiguration.class );
    
    /**
     * The persistent set of properties supported by the spell engine
     */
    private final Properties properties;
    /**
     * The name of the file containing spell engine properties
     */
    private final URL filename;

    /**
     * Constructs and loads spell engine properties configuration.
     */
    public PropertyConfiguration() {
        this.properties = new Properties();
        this.filename = getClass().getClassLoader().getResource( 
                "uk/ac/open/crc/mdsc/engine/configuration.properties" );
        try ( InputStream in = this.filename.openStream() ) {
            this.properties.load( in );
        }
        catch ( IOException e ) {
            LOGGER.error( "Could not load Properties file : {}", e);
        }
    }

    /**
     * Recovers the boolean value associated with the given key.
     * @param key a property name
     * @return {@inheritDoc}
     */
    @Override
    public boolean getBoolean( String key ) {
        return Boolean.valueOf( this.properties.getProperty( key ) );
    }

    /**
     * Retrieves the integer value associated with the given key.
     * @param key a property name
     * @return {@inheritDoc}
     */
    @Override
    public int getInteger( String key ) {
        return Integer.parseInt( this.properties.getProperty( key ) );
    }

    /**
     * @see uk.ac.open.crc.mdsc.engine.Configuration#setBoolean(String, boolean)
     */
    @Override
    public void setBoolean( String key, boolean value ) {
        this.properties.setProperty( key, Boolean.toString( value ) );
        //save();
    }

    /**
     * @see uk.ac.open.crc.mdsc.engine.Configuration#setInteger(String, int)
     */
    @Override
    public void setInteger( String key, int value ) {
        this.properties.setProperty( key, Integer.toString( value ) );
        // save();
    }

    /**
     * Writes the property list (key and element pairs) in the
     * PropertyConfiguration file.
     */
    @Deprecated
    public void save() {
        LOGGER.error( "Attempt made to save configuration. Operation not supported." );
        throw new UnsupportedOperationException( "Configuration cannot be saved" );
    }

}
