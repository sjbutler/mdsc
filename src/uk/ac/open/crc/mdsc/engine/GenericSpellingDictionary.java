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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The SpellDictionary class holds the instance of the dictionary.
 * <p>
 * This class is thread safe. Derived classes should ensure that this preserved.
 * </p>
 * <p>
 * There are many open source dictionary files. For just a few see:
 * http://wordlist.sourceforge.net/
 * </p>
 * <p>
 * This dictionary class reads words one per line. Make sure that your word list
 * is formatted in this way (most are).
 * </p>
 */
public class GenericSpellingDictionary extends ASpellSpellingDictionary {

    private static final Logger LOGGER = 
            LoggerFactory.getLogger( GenericSpellingDictionary.class );
    
//tech_monkey: the alphabet / replace list stuff has been moved into 
//  the Transformator classes,
//since they are so closely tied to how the phonetic transformations are done.
//    /**
//     * This replace list is used if no phonetic file is supplied or it doesn't
//     * contain the alphabet.
//     */
//    protected static final char[] englishAlphabet =
    // SB: Review this. Isn't it a misunderstanding of the integer 
    // argument to the hash map constructor?
    /**
     * A field indicating the initial hash map capacity in buckets for the main
     * dictionary hash map. 
     */
    private final static int INITIAL_CAPACITY = 500;

    /**
     * The hashmap that contains the word dictionary. The map is hashed on the
     * doublemetaphone code. The map entry contains a LinkedList of words that have
     * the same double metaphone code.
     */
    protected HashMap<String, LinkedList<String>> mainDictionary
            = new HashMap<>( INITIAL_CAPACITY );

    /**
     * Holds the dictionary file for appending
     */
    private File dictionaryFile = null;

    /**
     * Dictionary constructor that uses the DoubleMetaphone class with the
     * English alphabet.
     *
     * @param wordList The file containing dictionary as a words list.
     * @throws java.io.FileNotFoundException when the words list file could not
     * be located on the system.
     * @throws java.io.IOException when problems occurs while reading the words
     * list file
     */
    public GenericSpellingDictionary( File wordList ) throws 
            FileNotFoundException, IOException {
        this( wordList, (File) null );
    }

    /**
     * Dictionary constructor that uses an aspell phonetic file to build the
     * transformation table. If phonetic is null, then DoubleMeta is used with
     * the English alphabet
     *
     * @param wordList The file containing dictionary as a words list.
     * @param phonetic The file containing the phonetic transformation
     * information.
     * @throws java.io.FileNotFoundException when the words list or phonetic
     * file could not be located on the system
     * @throws java.io.IOException when problems occurs while reading the words
     * list or phonetic file
     */
    public GenericSpellingDictionary( File wordList, File phonetic ) 
            throws FileNotFoundException, IOException {

        super( phonetic );
        dictionaryFile = wordList;
        createDictionary( new BufferedReader( new FileReader( wordList ) ) );
    }

    /**
     * Add a word permanently to the dictionary (and the dictionary file).
     * <p>
     * This needs to be made thread safe (synchronized)</p>
     *
     * @param word The word to add to the dictionary
     */
    @Override
    public void addWord( String word ) {
        putWord( word );
        if ( this.dictionaryFile == null ) {
            return;
        }

        try ( FileWriter writer = 
                new FileWriter( this.dictionaryFile.toString(), true ) ) {
            writer.write( word );
            writer.write( System.lineSeparator() );
            writer.close();
        }
        catch ( IOException e ) {
            LOGGER.error( "Error writing to dictionary file: {0}", e.getMessage() );
        }
    }

    /**
     * Constructs the dictionary from a word list file.
     * <p>
     * Each word in the reader should be on a separate line.
     *
     * @param in a buffered reader pointing to the word list file
     * @throws java.io.IOException if a problem is encountered reading the file
     */
    protected final void createDictionary( BufferedReader in ) throws IOException {
        String line = "";
        while ( line != null ) {
            line = in.readLine();
            if ( line != null ) {
                putWord( line );
            }
        }
    }

    /**
     * Stores a word in the dictionary.
     *
     * @param word a word to be stored in the dictionary
     */
    protected void putWord( String word ) {
        String code = getPhoneticCode( word );
        LinkedList<String> list = this.mainDictionary.get( code );
        if ( list != null ) {
            list.add( word );
        }
        else {
            list = new LinkedList<>();
            list.add( word );
            this.mainDictionary.put( code, list );
        }
    }

    /**
     * Returns a list of strings (words) for the code.
     *
     * @param code The phonetic code we want to find words for
     * @return the list of words having the same phonetic code
     */
    @Override
    public List<String> getWords( String code ) {
        //Check the main dictionary.
        List<String> mainDictionaryResult = this.mainDictionary.get( code );
        if ( mainDictionaryResult == null ) {
            return new LinkedList<>();
        }
        return mainDictionaryResult;
    }

}
