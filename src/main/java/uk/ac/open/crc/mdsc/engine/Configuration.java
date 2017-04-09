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

import java.security.AccessControlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The various settings used to control how a spell checker works are read from
 * here. Includes the COST_* constants that decide how to figure the cost of
 * converting one word to another in the EditDistance class.
 * <p>
 * Also includes SPELL_* constants that control how misspellings are detected,
 * for example, how to handle mixed-case words, etc.
 * </p>
 * @author aim4min
 * @see EditDistance
 */
public abstract class Configuration {

    private static final Logger LOGGER = 
            LoggerFactory.getLogger( Configuration.class );
    
    /**
     * Used by EditDistance: the cost of having to remove a character.
     * (integer greater than 0).
     */
    public static final String COST_REMOVE_CHAR = "EDIT_DEL1";

    /**
     * Used by EditDistance: the cost of having to insert a character.
     * (integer greater than 0).
     */
    public static final String COST_INSERT_CHAR = "EDIT_DEL2";

    /**
     * Used by EditDistance: the cost of having to swap two adjoining
     * characters. For the swap value to ever be used, it should be smaller than
     * the COST_REMOVE_CHAR or COST_INSERT_CHAR values
     * (integer greater than 0)
     */
    public static final String COST_SWAP_CHARS = "EDIT_SWAP";

    /**
     * Used by EditDistance: the cost of having to change case, for example,
     * from i to I.
     * (integer greater than 0)
     */
    public static final String COST_CHANGE_CASE = "EDIT_CASE";

    /**
     * Used by EditDistance: the cost of having to substitute one character for
     * another. For the sub value to ever be used, it should be smaller than the
     * COST_REMOVE_CHAR or COST_INSERT_CHAR values
     * (integer greater than 0)
     */
    public static final String COST_SUBST_CHARS = "EDIT_SUB";

//    public static final String EDIT_SIMILAR = "EDIT_SIMILAR"; //DMV: these does not seem to be used at all
//    public static final String EDIT_MIN = "EDIT_MIN";
//    public static final String EDIT_MAX = "EDIT_MAX";
    /**
     * The maximum cost of suggested spelling. Any suggestions that cost more
     * are thrown away.
     * (integer greater than 1)
     */
    public static final String SPELL_THRESHOLD = "SPELL_THRESHOLD";

    /**
     * words that are all upper case are not spell checked, example: "CIA".
     * (boolean)
     */
    public static final String SPELL_IGNORE_UPPER_CASE = "SPELL_IGNORE_UPPER_CASE";
    /**
     * words that have mixed case are not spell checked, example:
     * "SpellChecker". (boolean)
     */
    public static final String SPELL_IGNORE_MIXED_CASE = "SPELL_IGNORE_MIXED_CASE";
    /**
     * words that look like an Internet address are not spell checked, example:
     * "http://www.google.com". (boolean)
     */
    public static final String SPELL_IGNORE_INTERNET_ADDRESSES = 
            "SPELL_IGNORE_INTERNET_ADDRESS";
    /**
     * words that have digits in them are not spell checked, example: "mach5".
     * (boolean)
     */
    public static final String SPELL_IGNORE_DIGIT_WORDS = "SPELL_IGNORE_DIGIT_WORDS";
    
    // SB: if the original developers don't know what this is for ...
    /**
     * I don't know what this does. It doesn't seem to be used (boolean)
     */
    public static final String SPELL_IGNORE_MULTIPLE_WORDS = 
            "SPELL_IGNORE_MULTIPLE_WORDS";
    /**
     * The first word of a sentence is expected to start with an upper case
     * letter. (boolean)
     */
    public static final String SPELL_IGNORE_SENTENCE_CAPITALIZATION = 
            "SPELL_IGNORE_SENTENCE_CAPTILIZATION";

    /**
     * Gets one of the integer constants
     *
     * @param key one of the integer constants defined in this class
     * @return int value of the setting
     */
    public abstract int getInteger( String key );

    /**
     * Gets one of the boolean constants
     *
     * @param key one of the boolean constants defined in this class
     * @return boolean value of the setting
     */
    public abstract boolean getBoolean( String key );

    /**
     * Sets one of the integer constants
     *
     * @param key one of the integer constants defined in this class
     * @param value new integer value of the constant
     */
    public abstract void setInteger( String key, int value );

    /**
     * Sets one of the boolean constants
     *
     * @param key one of the boolean constants defined in this class
     * @param value new boolean value of this setting
     */
    public abstract void setBoolean( String key, boolean value );

    /**
     * gets a new default Configuration
     *
     * @return Configuration
     */
    public static final Configuration getConfiguration() {
        try {
            String configurationClassName = System.getProperty( "mdsc.config" );
            if ( configurationClassName != null 
                    && ! configurationClassName.isEmpty() ) {
                return getConfiguration( configurationClassName );
            }
        }
        catch ( AccessControlException e ) {
            LOGGER.error( "Unable to access properties: {}", e.getMessage());
        }
        return getConfiguration( null );
    }

    /**
     * Returns a new instance of a configurationClassName class.
     *
     * @param configurationClassName a name for the configuration class
     * @return Configuration
     */
    public static final Configuration getConfiguration( String configurationClassName ) {

        Configuration result;

        if ( configurationClassName != null && ! configurationClassName.isEmpty() ) {
            try {
                result = (Configuration) Class.forName( configurationClassName ).newInstance();
            }
            catch ( InstantiationException 
                    | IllegalAccessException 
                    | ClassNotFoundException e ) {
                result = new PropertyConfiguration();
            }
        }
        else {
            result = new PropertyConfiguration();
        }
        return result;
    }
}
