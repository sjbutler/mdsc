/*
mdsc - multiple dictionary spell checker
Copyright (C) 2014-2015 The Open University

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
package uk.ac.open.crc.mdsc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides ISO 3166 two and three letter country codes normalised to lower 
 * case. The dictionaries are named "iso3166-2" and "iso3166-3".
 */
public class NormalisedIso3166DictionaryManager extends DictionaryManager {
    private static final boolean IS_NORMALISED = true;
    private static final Logger LOGGER = 
            LoggerFactory.getLogger( NormalisedIso3166DictionaryManager.class );
    
    
    public NormalisedIso3166DictionaryManager() {
        super();
        try {
            super.create( "iso3166-2", 
                    "ISO3166-2 two letter ISO country codes.", 
                    new InputStreamReader( this.getClass().getResourceAsStream( 
                            "/wordlists/iso-3166-2" ) ) , 
                    IS_NORMALISED );
            super.create( "iso3166-3", 
                    "ISO3166-3 three letter ISO country codes.", 
                    new InputStreamReader( this.getClass().getResourceAsStream( 
                            "/wordlists/iso-3166-3" ) ) , 
                    IS_NORMALISED );
        }
        catch ( FileNotFoundException e) {
            LOGGER.error(
                    "Word list not found in jar: {}", 
                    e.getMessage());
        }
        catch (IOException e) {
            LOGGER.error( 
                    "IOException thrown reading from jar: {}", 
                    e.getMessage());
        }
    }
    
}
