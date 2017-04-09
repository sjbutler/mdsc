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

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Another implementation of <code>SpellDictionary</code> that doesn't cache any
 * words in memory. Avoids the huge footprint of
 * <code>SpellDictionaryHashMap</code> at the cost of relatively minor latency.
 * A future version of this class that implements some caching strategies might
 * be a good idea in the future, if there's any demand for it.
 *
 * This implementation requires a special dictionary file, with "code*word"
 * lines sorted by code. It's using a dichotomy algorithm to search for words in
 * the dictionary
 *
 * @author Damien Guillaume
 * @version 0.1
 */
public class DichotomyDiskSpellingDictionary extends ASpellSpellingDictionary {

    private static final Logger LOGGER = 
            LoggerFactory.getLogger( DichotomyDiskSpellingDictionary.class );
    /**
     * Holds the dictionary file for reading
     */
    private RandomAccessFile dictionaryFile = null;

    /**
     * dictionary and phonetic file encoding
     */
    private String encoding = null;

    /**
     * Dictionary convenience Constructor.
     *
     * @param wordListFile The file containing the words list for the dictionary
     * @throws java.io.FileNotFoundException indicates problems locating the
     * words list file on the system
     * @throws java.io.IOException indicates problems reading the words list
     * file
     */
    public DichotomyDiskSpellingDictionary( File wordListFile )
            throws FileNotFoundException, IOException {
        super( (File) null );
        this.dictionaryFile = new RandomAccessFile( wordListFile, "r" );
    }

    /**
     * Dictionary convenience Constructor.
     *
     * @param wordListFile The file containing the words list for the dictionary
     * @param encoding Uses the character set encoding specified
     * @throws java.io.FileNotFoundException indicates problems locating the
     * words list file on the system
     * @throws java.io.IOException indicates problems reading the words list
     * file
     */
    public DichotomyDiskSpellingDictionary( 
            File wordListFile, 
            String encoding )
            throws FileNotFoundException, IOException {
        super( (File) null );
        this.encoding = encoding;
        this.dictionaryFile = new RandomAccessFile( wordListFile, "r" );
    }

    /**
     * Dictionary constructor that uses an aspell phonetic file to build the
     * transformation table.
     *
     * @param wordListFile The file containing the words list for the dictionary
     * @param phoneticFile The file to use for phonetic transformation of the
     * wordlist.
     * @throws java.io.FileNotFoundException indicates problems locating the
     * file on the system
     * @throws java.io.IOException indicates problems reading the words list
     * file
     */
    public DichotomyDiskSpellingDictionary( 
            File wordListFile, 
            File phoneticFile )
            throws FileNotFoundException, IOException {
        super( phoneticFile );
        this.dictionaryFile = new RandomAccessFile( wordListFile, "r" );
    }

    /**
     * Dictionary constructor that uses an aspell phonetic file to build the
     * transformation table.
     *
     * @param wordListFile The file containing the words list for the dictionary
     * @param phoneticFile The file to use for phonetic transformation of the
     * wordlist.
     * @param encoding Uses the character set encoding specified
     * @throws java.io.FileNotFoundException indicates problems locating the
     * file on the system
     * @throws java.io.IOException indicates problems reading the words list
     * file
     */
    public DichotomyDiskSpellingDictionary( 
            File wordListFile, 
            File phoneticFile, 
            String encoding )
            throws FileNotFoundException, IOException {
        super( phoneticFile, encoding );
        this.encoding = encoding;
        this.dictionaryFile = new RandomAccessFile( wordListFile, "r" );
    }

    /**
     * Add a word permanently to the dictionary (and the dictionary file).
     * <i>not implemented !</i>
     *
     * @param word The word to add.
     */
    @Override
    public void addWord( String word ) {
        throw new UnsupportedOperationException( 
                "error: addWord is not implemented "
                        + "for DichotomyDiskSpellingDictionary" );
    }

    // SB: review the naming -- I've done it once, but it still needs refining.
    /**
     * Search the dictionary file for the words corresponding to the code within
     * positions p1 - p2
     */
    private List<String> dichotomyFind( 
            String code, 
            long position1, 
            long position2 ) 
            throws IOException {
        //System.out.println("dichoFind("+code+","+p1+","+p2+")");
        long positionMarker = (position1 + position2) / 2;
        this.dictionaryFile.seek( positionMarker );
        String line;
        // SB: why is a line read and discarded?
        if ( encoding == null ) {
            line = this.dictionaryFile.readLine();
        }
        else {
            line = dictionaryReadLine();
        }
        positionMarker = this.dictionaryFile.getFilePointer();
        if ( this.encoding == null ) {
            line = this.dictionaryFile.readLine();
        }
        else {
            line = dictionaryReadLine();
        }
        long positionMarker2 = this.dictionaryFile.getFilePointer();
        if ( positionMarker2 >= position2 ) {
            return (sequentialFind( code, position1, position2 ));
        }
        int starDelimiterIndex = line.indexOf( '*' );
        if ( starDelimiterIndex == -1 ) {
            throw new IOException( "bad format: no * !" );
        }
        String testcode = line.substring( 0, starDelimiterIndex );
        int comparisonResult = code.compareTo( testcode );
        if ( comparisonResult < 0 ) {
            return (dichotomyFind( code, position1, positionMarker - 1 ));
        }
        else if ( comparisonResult > 0 ) {
            return (dichotomyFind( code, positionMarker2, position2 ));
        }
        else {
            List<String> wordList = 
                    dichotomyFind( code, position1, positionMarker - 1 );
            List<String> l2 = 
                    dichotomyFind( code, positionMarker2, position2 ); // SB: rename l2
            String word = line.substring( starDelimiterIndex + 1 );
            wordList.add( word );
            wordList.addAll( l2 );
            return (wordList);
        }
    }

    private ArrayList<String> sequentialFind( 
            String code, 
            long position1, 
            long position2 ) 
            throws IOException {
        //System.out.println("seqFind("+code+","+p1+","+p2+")");
        ArrayList<String> wordList = new ArrayList<>();
        this.dictionaryFile.seek( position1 );
        while ( this.dictionaryFile.getFilePointer() < position2 ) {
            String line;
            if ( encoding == null ) {
                line = this.dictionaryFile.readLine();
            }
            else {
                line = dictionaryReadLine();
            }
            int starDelimiterIndex = line.indexOf( '*' );
            if ( starDelimiterIndex == -1 ) {
                throw new IOException( "bad format: no * !" );
            }
            if ( code.equals( line.substring( 0, starDelimiterIndex ) ) ) {
                wordList.add( line.substring( starDelimiterIndex + 1 ) );
            }
        }
        return wordList;
    }

    // SB: surely there are better ways of specifying that a line
    // should be read using a specific encoding?
    /**
     * Read a line of dictFile with a specific encoding
     */
    private String dictionaryReadLine() throws IOException {
        final int maximumLineLength = 255;
        byte b = 0;
        byte[] buffer = new byte[maximumLineLength];
        int bytesRead = 0;
        try {
            for ( ; b != '\n' && b != '\r' && bytesRead < maximumLineLength - 1; bytesRead++ ) {
                b = this.dictionaryFile.readByte();
                buffer[bytesRead] = b;
            }
        }
        catch ( EOFException ex ) {
            // SB: This happens if EOF is encountered before the buffer is filled.
            // SB: and the exception simply does not need to be reported.
            // SB: A neater solution would be better.
        }
        
        if ( bytesRead == 0 ) {
            return ("");
        }
        else {
            return new String( buffer, 0, bytesRead - 1, encoding );
        }
    }

    /**
     * Returns a list of strings (words) for the code.
     *
     * @param code The phonetic code common to the list of words
     * @return A list of words having the same phonetic code
     */
    @Override
    public List<String> getWords( String code ) {
        List<String> list;
        try {
            list = dichotomyFind( code, 0, dictionaryFile.length() - 1 );
        }
        catch ( IOException e ) {
            LOGGER.error( "Unable to read from file: {}", e.getMessage());
            list = new ArrayList<>();
        }
        return list;
    }

}
