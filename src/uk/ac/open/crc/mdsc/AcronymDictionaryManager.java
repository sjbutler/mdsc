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
 * Provides a dictionary for looking up acronyms. The dictionary is not 
 * intended to provide alternative spellings, merely to be used as a service to 
 * determine if a misspelt word (according to the {@code DefaultDictionaryManager} 
 * is a known acronym. The dictionary can therefore be used to make decisions
 * about retaining the acronym rather than using abbreviation expansion or a 
 * suggested alternative spelling.
 *
 * <p>
 * The acronyms are normalised to lower case. A future version will include 
 * non-normalised acronyms so that the standard typography can be used to  
 * support the correction of typography in identifier names, if required.
 * </p>
 */
public class AcronymDictionaryManager extends DictionaryManager {
    private static final boolean IS_NORMALISED = true;
    private static final Logger LOGGER = 
            LoggerFactory.getLogger( AcronymDictionaryManager.class );
    /**
     * Creates a default set of normalised acronym dictionaries. These are a set of 
     * acronyms with trailing digits found in identifier names during our research, and 
     * acronyms extracted from the en_GB and en_US abbreviations to size 70 
     * found in the SCOWL word lists. 
     */
    public AcronymDictionaryManager() {
        super();
        // now create the dictionaries
        try {
            create( "mdsc acronyms", 
                    "A selection of acronyms found in identifier names", 
                    new InputStreamReader( this.getClass().getResourceAsStream( 
                            "/uk/ac/open/crc/mdsc/wordlists/acronyms" ) ) , 
                    IS_NORMALISED );
            create( "mdsc trailing digit acronyms", 
                    "A selection of trailing digit acronyms found in identifier names", 
                    new InputStreamReader( this.getClass().getResourceAsStream( 
                            "/uk/ac/open/crc/mdsc/wordlists/trailing-digit-acronyms" ) ) , 
                    IS_NORMALISED );
            // long term ambition is to call this in from AMAP
            // rather than use this list
            create( "AMAP acronyms", 
                    "Acronyms from AMAP", 
                    new InputStreamReader( this.getClass().getResourceAsStream( 
                            "/uk/ac/open/crc/mdsc/wordlists/amap/acronyms" ) ) , 
                    IS_NORMALISED );
            create( "en_GB-acronyms", 
                    "en_GB acronyms from the SCOWL package", 
                    new InputStreamReader( this.getClass().getResourceAsStream( 
                            "/uk/ac/open/crc/mdsc/wordlists/derived/en_GB-acronyms" ) ) , 
                    IS_NORMALISED );
            create( "en_US-acronyms", 
                    "en_US ascronyms from the SCOWL package", 
                    new InputStreamReader( this.getClass().getResourceAsStream( 
                            "/uk/ac/open/crc/mdsc/wordlists/derived/en_US-acronyms" ) ) , 
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
