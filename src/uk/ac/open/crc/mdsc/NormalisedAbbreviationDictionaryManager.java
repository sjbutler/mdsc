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
 * Provides a dictionary of abbreviations where the entries have been 
 * normalised to lower case. 
 */
public class NormalisedAbbreviationDictionaryManager extends DictionaryManager {
    private static final boolean IS_NORMALISED = true;
    private static final Logger LOGGER = 
            LoggerFactory.getLogger( NormalisedAbbreviationDictionaryManager.class );
    
    /**
     * Creates a set of abbreviation dictionaries that are normalised to 
     * lower case.
     */
    public NormalisedAbbreviationDictionaryManager() {
        super();
        // now create the dictionaries
        try {
            create( "mdsc abbreviations", 
                    "A selection of abbreviations found in identifier names", 
                    new InputStreamReader( this.getClass().getResourceAsStream( 
                            "/uk/ac/open/crc/mdsc/wordlists/abbreviations" ) ) , 
                    IS_NORMALISED );
            create( "en_GB-abbreviations", 
                    "en_GB abbreviations from the SCOWL package", 
                    new InputStreamReader( this.getClass().getResourceAsStream( 
                            "/uk/ac/open/crc/mdsc/wordlists/derived/en_GB-abbreviations" ) ) , 
                    IS_NORMALISED );
            create( "en_US-abbreviations", 
                    "en_US abbreviations from the SCOWL package", 
                    new InputStreamReader( this.getClass().getResourceAsStream( 
                            "/uk/ac/open/crc/mdsc/wordlists/derived/en_US-abbreviations" ) ) , 
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
        
        // TODO: now pull in dictionaries from AMAP
    }
}
