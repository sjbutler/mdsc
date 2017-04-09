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
 * Provides a default set of dictionaries with entries normalised  to lower case 
 * using SCOWL word lists to 
 * 80, separated by language, the SCOWL 'hacker' dictionary of technical terms
 * and a dictionary of technical terms derived from identifier names. These
 * dictionaries contain words only; no initialisms or abbreviations.
 * <p>
 * This class is a normalised version of {@linkplain DefaultDictionaryManager}.
 * </p>
 */
public class NormalisedDictionaryManager extends DictionaryManager {
    private static final boolean IS_NORMALISED = true;
    private static final Logger LOGGER = 
            LoggerFactory.getLogger( NormalisedDictionaryManager.class );
    public NormalisedDictionaryManager() {
        super();
        // now create the dictionaries
        try {
            create( "en_GB", 
                    "en_GB dictionary from the SCOWL package", 
                    new InputStreamReader( this.getClass().getResourceAsStream( 
                            "/wordlists/scowl/en_GB" ) ) , 
                    IS_NORMALISED );
            create( "en_US", 
                    "en_US dictionary from the SCOWL package", 
                    new InputStreamReader( this.getClass().getResourceAsStream( 
                            "/wordlists/scowl/en_US" ) ) , 
                    IS_NORMALISED );
            create( "en_CA", 
                    "en_CA dictionary from the SCOWL package", 
                    new InputStreamReader( this.getClass().getResourceAsStream( 
                            "/wordlists/scowl/en_CA" ) ) , 
                    IS_NORMALISED );
            create( "proper-nouns", 
                    "proper nouns dictionary from the SCOWL package", 
                    new InputStreamReader( this.getClass().getResourceAsStream( 
                            "/wordlists/scowl/proper-nouns" ) ) , 
                    IS_NORMALISED );
            create( "hacker", 
                    "hacker dictionary from the SCOWL package", 
                    new InputStreamReader( this.getClass().getResourceAsStream( 
                            "/wordlists/scowl/hacker" ) ) , 
                    IS_NORMALISED );
            create( "technical", 
                    "mdsc technical dictionary", 
                    new InputStreamReader( this.getClass().getResourceAsStream( 
                            "/wordlists/technical" ) ) , 
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
