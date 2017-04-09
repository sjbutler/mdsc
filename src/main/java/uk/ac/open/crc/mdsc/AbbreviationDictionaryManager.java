/*
 mdsc - a multiple dictionary spell checker
 Copyright (C) 2014-2015 The Open University
 Full text of license can be found in LICENSE.txt

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License as published by
 the Free Software Foundation with the 'classpath' exception, 
 either version 3 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package uk.ac.open.crc.mdsc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a dictionary for looking up abbreviations. The dictionary is not 
 * intended to provide alternative spellings, merely to be used as a service to 
 * determine if a misspelt word (according to the {@code DefaultDictionaryManager} 
 * is a known abbreviation. The dictionary can therefore be used to make decisions
 * about using abbreviation expansion rather than a suggested alternative
 * spelling. Note that acronyms have been extracted from the SCOWL lists 
 * and can be accessed either using the {@linkplain AcronymDictionaryManager}
 * or using the SCOWL files where abbreviations and acronyms are combined in 
 * {@code uk.ac.open.crc.mdsc.wordlists.scowl}.
 *
 */
public final class AbbreviationDictionaryManager extends DictionaryManager {

    private static final boolean IS_NORMALISED = true;
    private static final Logger LOGGER = LoggerFactory.getLogger( AbbreviationDictionaryManager.class );
    /**
     * Creates a default set of abbreviation dictionaries. These are a set of 
     * abbreviations found in identifier names during our research, and 
     * en_GB and en_US abbreviations to size 70 found in the SCOWL word lists. 
     * 
     */
    public AbbreviationDictionaryManager() {
        super();
        // now create the dictionaries
        try {
            create( "mdsc abbreviations", 
                    "A selection of abbreviations found in identifier names", 
                    new InputStreamReader( this.getClass().getResourceAsStream( 
                            "/wordlists/abbreviations" ) ) , 
                    IS_NORMALISED );
            // long term ambition is to call this in from AMAP
            // rather than use this list
            create( "AMAP abbreviations", 
                    "Abbreviations from AMAP", 
                    new InputStreamReader( this.getClass().getResourceAsStream( 
                            "/wordlists/amap/abbreviations" ) ) , 
                    IS_NORMALISED );
            create( "en_GB-abbreviations", 
                    "en_GB abbreviations from the SCOWL package", 
                    new InputStreamReader( this.getClass().getResourceAsStream( 
                            "/wordlists/derived/en_GB-abbreviations" ) ) , 
                    IS_NORMALISED );
            create( "en_US-abbreviations", 
                    "en_US abbreviations from the SCOWL package", 
                    new InputStreamReader( this.getClass().getResourceAsStream( 
                            "/wordlists/derived/en_US-abbreviations" ) ) , 
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
