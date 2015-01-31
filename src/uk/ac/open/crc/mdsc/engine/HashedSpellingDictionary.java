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
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The HashedSpellingDictionary holds the dictionary.
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
 * <p>
 * Note that you must create the dictionary with a word list for the added words
 * to persist.
 * </p>
 */
public class HashedSpellingDictionary extends ASpellSpellingDictionary {
    private static final Logger logger = 
            LoggerFactory.getLogger( HashedSpellingDictionary.class );

    /**
     * A field indicating the initial hash map capacity in buckets for the main
     * dictionary hash map. 500 is in excess of the number of double metaphone
     * codes, so should provide sufficient buckets.
     */
    private final static int INITIAL_CAPACITY = 500;

    /**
     * The hashmap that contains the word dictionary. The map is hashed on the
     * double metaphone code. The map entry contains a LinkedList of words that
     * have the same double meta code.
     */
    private HashMap<String,LinkedList<String>> mainDictionary;
    

    /**
     * Dictionary Constructor.
     *
     * @throws java.io.IOException indicates a problem with the file system
     */
    public HashedSpellingDictionary() throws IOException {
        super( (File) null );
        this.mainDictionary = new HashMap<>( INITIAL_CAPACITY );
    }

    /**
     * Dictionary Constructor.
     *
     * @param wordList The file containing the words list for the dictionary
     * @throws java.io.IOException indicates problems reading the words list
     * file
     */
    public HashedSpellingDictionary( Reader wordList ) throws IOException {
        super( (File) null );
        this.mainDictionary = new HashMap<>( INITIAL_CAPACITY );
        createDictionary( new BufferedReader( wordList ) );
    }

    /**
     * Dictionary Constructor with the option to normalise the entries.
     *
     * @param wordList The file containing the words list for the dictionary
     * @param isNormalised indicates whether the word list should be normalised
     * to lower case
     * @throws java.io.IOException indicates problems reading the words list
     * file
     */
    public HashedSpellingDictionary( Reader wordList, boolean isNormalised ) 
            throws IOException {
        super( (File) null );
        this.mainDictionary = new HashMap<>( INITIAL_CAPACITY );
        if ( isNormalised ) {
            createNormalisedDictionary( new BufferedReader( wordList ) );
        }
        else {
            createDictionary( new BufferedReader( wordList ) );
        }
    }

    /**
     * Dictionary convenience Constructor.
     *
     * @param wordListFile The file containing the words list for the dictionary
     * @throws java.io.FileNotFoundException indicates problems locating the
     * words list file on the system
     * @throws java.io.IOException indicates problems reading the words list
     * file
     */
    public HashedSpellingDictionary( File wordListFile ) 
            throws FileNotFoundException, IOException {
        this( new FileReader( wordListFile ) );
        logger.info( "Dictionary loaded: {} words ", this.mainDictionary.size());
    }

    /**
     * Dictionary convenience Constructor with the option to normalise the 
     * entries.
     *
     * @param wordListFile The file containing the words list for the dictionary
     * @param isNormalised indicates whether the word list should be normalised 
     * to lower case
     * @throws java.io.FileNotFoundException indicates problems locating the
     * words list file on the system
     * @throws java.io.IOException indicates problems reading the words list
     * file
     */
    public HashedSpellingDictionary( File wordListFile, boolean isNormalised ) 
            throws FileNotFoundException, IOException {
        this( new FileReader( wordListFile ) );
        logger.info( "Dictionary loaded: {} words ", this.mainDictionary.size());
    }

    /**
     * Dictionary constructor that uses an aspell phonetic file to build the
     * transformation table.
     *
     * @param wordListFile The file containing the words list for the dictionary
     * @param phonetic The file to use for phonetic transformation of the
     * wordlist.
     * @throws java.io.FileNotFoundException indicates problems locating the
     * file on the system
     * @throws java.io.IOException indicates problems reading the words list
     * file
     */
    public HashedSpellingDictionary( File wordListFile, File phonetic ) 
            throws FileNotFoundException, IOException {
        super( phonetic );
        this.mainDictionary = new HashMap<>( INITIAL_CAPACITY );
        createDictionary( new BufferedReader( new FileReader( wordListFile ) ) );
    }

    /**
     * Dictionary constructor that uses an aspell phonetic file to build the
     * transformation table. Encoding is used for phonetic file only; default
     * encoding is used for wordList
     *
     * @param wordListFile The file containing the words list for the dictionary
     * @param phonetic The file to use for phonetic transformation of the
     * wordlist.
     * @param phoneticEncoding Uses the character set encoding specified
     * @throws java.io.FileNotFoundException indicates problems locating the
     * file on the system
     * @throws java.io.IOException indicates problems reading the words list or
     * phonetic information
     */
    public HashedSpellingDictionary( 
            File wordListFile, 
            File phonetic, 
            String phoneticEncoding ) 
            throws FileNotFoundException, IOException {
        super( phonetic, phoneticEncoding );
        this.mainDictionary = new HashMap<>( INITIAL_CAPACITY );
        createDictionary( new BufferedReader( new FileReader( wordListFile ) ) );
    }

    /**
     * Dictionary constructor that uses an aspell phonetic file to build the
     * transformation table.
     *
     * @param wordListFile The file containing the words list for the dictionary
     * @param phonetic The reader to use for phonetic transformation of the
     * wordlist.
     * @throws java.io.IOException indicates problems reading the words list or
     * phonetic information
     */
    public HashedSpellingDictionary( 
            Reader wordListFile, 
            Reader phonetic ) 
            throws IOException {
        super( phonetic );
        this.mainDictionary = new HashMap<>( INITIAL_CAPACITY );
        createDictionary( new BufferedReader( wordListFile ) );
    }

    /**
     * Add words from a file to existing dictionary hashmap. This function can
     * be called as many times as needed to build the internal word list.
     * Duplicates are not added.
     * <p>
     * Note that adding a dictionary does not affect the target dictionary file
     * for the addWord method. That is, addWord() continues to make additions to
     * the dictionary file specified in createDictionary()
     * </p>
     * @param wordListFile a File object that contains the words, one word per
     * line.
     * @throws FileNotFoundException if the file is not located
     * @throws IOException when a problem is encountered reading the file
     */
    public void addDictionary( File wordListFile ) 
            throws FileNotFoundException, IOException {
        addDictionaryHelper( new BufferedReader( new FileReader( wordListFile ) ) );
    }

    /**
     * Add words from a Reader to existing dictionary hashmap. This function can
     * be called as many times as needed to build the internal word list.
     * Duplicates are not added.
     * <p>
     * Note that adding a dictionary does not affect the target dictionary file
     * for the addWord method. That is, addWord() continues to make additions to
     * the dictionary file specified in createDictionary()
     * </p>
     * @param wordList a Reader object that contains the words, one word per
     * line.
     * @throws IOException when a problem is encountered adding words to the 
     * dictionary.
     */
    public void addDictionary( Reader wordList ) throws IOException {
        addDictionaryHelper( new BufferedReader( wordList ) );
    }

    /**
     * Add a word permanently to the dictionary (and the dictionary file).
     * <p>
     * This needs to be made thread safe</p>
     * <p>Not implemented!</p>
     * @param word A word to add to the dictionary.
     */
    @Override
    public void addWord( String word ) {
        throw new UnsupportedOperationException();
    }

    /**
     * Constructs the dictionary from a word list file.
     * <p>
     * Each word in the reader should be on a separate line.
     * </p>
     * @param in a reader for a word list file
     * @throws java.io.IOException when a problem is encountered reading the file.
     */
    protected final void createDictionary( BufferedReader in ) 
            throws IOException {
        String line;
        while ( (line = in.readLine()) != null ) {
            if ( !line.isEmpty() ) {
                putWord( line );
            }
        }
    }
    
    /**
     * Constructs the dictionary from a word list file where all words are 
     * normalised to lower case.
     * <p>
     * Each word in the reader should be on a separate line.
     * </p>
     * <p>
     * This is a very slow function. On my machine it takes quite a while to
     * load the data in. I suspect that we could speed this up quite a lot.
     * </p>
     * @param in a reader for a word list file
     * @throws java.io.IOException when a problem is encountered reading the file.
     */
    protected final void createNormalisedDictionary( BufferedReader in ) 
            throws IOException {
        String line;
        while ( (line = in.readLine()) != null ) {
            if ( !line.isEmpty() ) {
                putWord( line.toLowerCase() );
            }
        }
    }

    /**
     * Adds to the existing dictionary from a word list file. If the word
     * already exists in the dictionary, a new entry is not added.
     * <p>
     * Each word in the reader should be on a separate line.
     * </p>
     *
     * @param in a reader for a word list file
     * @throws java.io.IOException when a problem is encountered reading the file.
     */
    protected void addDictionaryHelper( BufferedReader in ) throws IOException {
        String line;
        while ( (line = in.readLine()) != null ) {
            if ( !line.isEmpty() ) {
                putWordUnique( line );
            }
        }
    }

    /**
     * Stores a word in the dictionary
     *
     * @param word The word to add
     */
    protected void putWord( String word ) {
        String code = getPhoneticCode( word );
        LinkedList<String> wordList = this.mainDictionary.get( code );
        if ( wordList != null ) {
            wordList.add( word );
        }
        else {
            wordList = new LinkedList<>();
            wordList.add( word );
            this.mainDictionary.put( code, wordList );
        }
    }

    /**
     * Stores a word, if it is not already present in the dictionary. A word
     * with a different case is considered the same.
     *
     * @param word The word to add
     */
    protected void putWordUnique( String word ) {
        String code = getPhoneticCode( word );
        LinkedList<String> wordList = this.mainDictionary.get( code );

        if ( wordList != null ) {
            boolean isInDictionary = false;

            for ( String storedWord : wordList ) {
                if ( word.equalsIgnoreCase( storedWord ) ) {
                    isInDictionary = true;
                    break;
                }
            }

            if ( !isInDictionary ) {
                wordList.add( word );
            }

        }
        else {
            wordList = new LinkedList<>();
            wordList.add( word );
            this.mainDictionary.put( code, wordList );

        }
    }

    /**
     * Returns a list of strings (words) for the phonetic code.
     *
     * @param code a phonetic code.
     * @return a list of words associated with the phonetic code.
     */
    @Override
    public List<String> getWords( String code ) {
        //Check the main dictionary.
        LinkedList<String> mainDictionaryResult = this.mainDictionary.get( code );
        if ( mainDictionaryResult == null ) {
            return new LinkedList<>();
        }
        return mainDictionaryResult;
    }

}
