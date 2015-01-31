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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.open.crc.mdsc.engine.Configuration;
import uk.ac.open.crc.mdsc.engine.HashedSpellingDictionary;
import uk.ac.open.crc.mdsc.engine.SpellingDictionary;

/**
 * A class used to populate and configure a group of dictionaries.
 * Spell checking is accomplished by using an instance of the {@code DictionarySet}
 * class obtained from the {@linkplain #dictionarySet()} method.
 *
 * <p>The subclasses of {@code DictionaryManager} are all convenience classes
 * with particular word lists loaded.
 * </p>
 *
 * @author Simon Butler (simon@facetus.org.uk)
 */
public class DictionaryManager {

    private static final int DEFAULT_MAXIMUM_COST = 3;
    private static final int DEFAULT_MAXIMUM_SUGESTIONS = 5;
    
    private static final Logger LOGGER = LoggerFactory.getLogger( DictionaryManager.class );
    
    // --------------------------------
    
    private int maximumCost;
    private int maximumSuggestions;
    private final Configuration configuration;
    private DictionarySet dictionarySet;
    
    public DictionaryManager() {
        this.maximumCost = DEFAULT_MAXIMUM_COST;
        this.configuration = Configuration.getConfiguration();
        this.configuration.setInteger( Configuration.SPELL_THRESHOLD, this.maximumCost );
        this.maximumSuggestions = DEFAULT_MAXIMUM_SUGESTIONS;
        
        this.dictionarySet = new DictionarySet();
    }
    
    /**
     * Creates a {@code Dictionary} using the supplied word list. The dictionary
     * is registered with the current {@code DictionarySet}. To use an individual
     * dictionary use the reference returned by this method. To use a group of 
     * dictionaries create them using this method then recover the 
     * {@code DictionarySet} using {@linkplain #dictionarySet()}
     * 
     * @param name an unique name for the dictionary.
     * @param description a brief description of the dictionary
     * @param wordListFile a file containing a word list
     * @return an instance of {@code Dictionary}
     * @throws java.io.FileNotFoundException if the word list file cannot be found
     */
    public final Dictionary create( String name, String description, File wordListFile ) 
            throws FileNotFoundException, IOException {
        
        // create the spelling dictionary
        SpellingDictionary spellingDictionary = 
                new HashedSpellingDictionary( wordListFile );
        // create the wrapper
        Dictionary dictionary = new Dictionary( 
                name, 
                description, 
                spellingDictionary , 
                this.maximumSuggestions, 
                this.maximumCost );
        // register it
        this.dictionarySet.register( dictionary );
        
        return dictionary;
    } 
    
    /**
     * Creates a {@code Dictionary} using the supplied word list. The dictionary
     * is registered with the current {@code DictionarySet}. To use an individual
     * dictionary use the reference returned by this method. To use a group of 
     * dictionaries create them using this method then recover the 
     * {@code DictionarySet} using {@linkplain #dictionarySet()}
     * 
     * @param name an unique name for the dictionary.
     * @param description a brief description of the dictionary
     * @param wordListFile a file containing a word list
     * @param isNormalised indicates whether the word list should be normalised to lower case.
     * @return an instance of {@code Dictionary}
     * @throws java.io.FileNotFoundException if the word list file cannot be found
     */
    public final Dictionary create( 
            String name, 
            String description, 
            File wordListFile, 
            boolean isNormalised ) 
            throws FileNotFoundException, IOException {
        
        // create the spelling dictionary
        SpellingDictionary spellingDictionary = 
                new HashedSpellingDictionary( wordListFile, isNormalised );
        // create the wrapper
        Dictionary dictionary = new Dictionary( 
                name, 
                description, 
                spellingDictionary , 
                this.maximumSuggestions, 
                this.maximumCost );
        // register it
        this.dictionarySet.register( dictionary );
        
        return dictionary;
    } 
    
    
    
    /**
     * Creates a {@code Dictionary} using the supplied word list which can be 
     * in a jar file, on the file system or &hellip;. The dictionary
     * is registered with the current {@code DictionarySet}. To use an individual
     * dictionary use the reference returned by this method. To use a group of 
     * dictionaries create them using this method then recover the 
     * {@code DictionarySet} using {@linkplain #dictionarySet()}
     * 
     * @param name an unique name for the dictionary.
     * @param description a brief description of the dictionary
     * @param wordListReader a {@code Reader} pointing to a word list
     * @param isNormalised indicates whether the word list should be normalised to lower case.
     * @return an instance of {@code Dictionary}
     * @throws java.io.FileNotFoundException if the word list file cannot be found
     */
    public final Dictionary create( 
            String name, 
            String description, 
            Reader wordListReader, 
            boolean isNormalised ) 
            throws FileNotFoundException, IOException {
        
        // create the spelling dictionary
        SpellingDictionary spellingDictionary = 
                new HashedSpellingDictionary( wordListReader, isNormalised );
        // create the wrapper
        Dictionary dictionary = new Dictionary( 
                name, 
                description, 
                spellingDictionary , 
                this.maximumSuggestions, 
                this.maximumCost );
        // register it
        this.dictionarySet.register( dictionary );
        
        return dictionary;
    } 
    
    
    
    
    /**
     * Sets the maximum cost of any suggested spellings returned by 
     * a dictionary. 
     * 
     * @param maximumCost the maximum cost of a transformation to an 
     * alternative spelling.
     */
    public void setCostThreshold( int maximumCost ) {
        this.maximumCost = maximumCost;
    }
    
    /**
     * Sets the maximum number of suggested spellings returned by 
     * a dictionary.
     * <p>
     * The new threshold only applies to dictionaries created 
     * by the manager after it has been set.
     * </p>
     * @param maximumSuggestions the maximum number of suggested alternative 
     * spellings to be returned by a dictionary.
     */
    public void setMaximumSuggestions( int maximumSuggestions ) {
        this.maximumSuggestions = maximumSuggestions;
    }
    
    
    /**
     * Retrieves the current dictionary set.
     * <p>
     * A copy of the current set of dictionaries and the state of the 
     * configuration variables is returned. Further changes to the configuration
     * and number of dictionaries made through the manager object should 
     * not affect the copy. 
     * </p>
     * 
     * @return A dictionary set
     */
    public DictionarySet dictionarySet() {
        return new DictionarySet( this.dictionarySet );
    }
    
    /**
     * Resets the dictionary manager to its default settings and 
     * creates a new default (empty) dictionary set.
     */
    public void reset() {
        this.dictionarySet = new DictionarySet();
    }
}
