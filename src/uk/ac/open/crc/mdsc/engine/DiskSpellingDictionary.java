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
/* Created by bgalbs on Jan 30, 2003 at 11:38:39 PM */
package uk.ac.open.crc.mdsc.engine;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of <code>SpellDictionary</code> that doesn't cache any
 * words in memory. Avoids the huge footprint of
 * {@code HashedSpellingDictionary} at the cost of relatively minor latency.
 * A future version of this class that implements some caching strategies might
 * be a good idea in the future, if there's any demand for it.
 * <p>
 * This class makes use of the "classic" Java IO library (java.io). However, it
 * could probably benefit from the new IO APIs (java.nio), or those introduced
 * in Java 8.
 *
 * @author Ben Galbraith (ben@galbraiths.org)
 * @version 0.1
 * @since 0.5
 */
public class DiskSpellingDictionary extends ASpellSpellingDictionary {

    private static final Logger LOGGER = 
            LoggerFactory.getLogger( DiskSpellingDictionary.class );
    
    private final static String DIRECTORY_WORDS = "words";
    private final static String DIRECTORY_DB = "db";
    private final static String FILE_CONTENTS = "contents";
    private final static String FILE_DB = "words.db";
    private final static String FILE_INDEX = "words.idx";

    /* maximum number of words an index entry can represent */
    private final static int MAXIMUM_INDEX_SIZE = 200;

    private final File baseDirectory;
    private final File wordListFile;
    private final File databaseDirectory;
    private Map<String,Integer[]> index;
    /**
     * A flag indicating if the initial preparation or loading of the on disk
     * dictionary is complete.
     * 
     * 
     */
    protected boolean ready;

    /**
     * Used at time of creation of index to speed up determining the number of
     * words per index entry.
     */
    private List<String> indexCodeCache = null;

    /**
     * Construct a spell dictionary on disk. The spell dictionary is created
     * from words list(s) contained in file(s). A words list file is a file with
     * one word per line. Words list files are located in a
     * <code>base/words</code> dictionary where <code>base</code> is the path to
     * <code>words</code> dictionary. The on disk spell dictionary is created in
     * <code>base/db</code> dictionary and contains files:
     * <ul>
     * <li><code>contents</code> list the words files used for spelling.</li>
     * <li><code>words.db</code> the content of words files organized as a
     * <em>database</em> of words.</li>
     * <li><code>words.idx</code> an index file to the <code>words.db</code>
     * file content.</li>
     * </ul>
     * The <code>contents</code> file has a list of <code>filename, size</code>
     * indicating the name and length of each files in the
     * <code>base/words</code> dictionary. If one of theses files was changed,
     * added or deleted before the call to the constructor, the process of
     * producing new or updated <code>words.db</code> and <code>words.idx</code>
     * files is started again.
     * <p>
     * The spellchecking process is then worked upon the <code>words.db</code>
     * and <code>words.idx</code> files.
     * </p>
     * <p>
     * NB: the constructor may take a long time to run while it constructs or loads
     * the dictionaries. The caller should run this constructor in a separate
     * thread to avoid the calling thread being blocked.
     * </p>
     * 
     * NOTE: Do *not* create two instances of this class pointing to the same
     * <code>base</code> unless you are sure that a new dictionary does not have
     * to be created. In the future, some sort of external locking mechanism may
     * be created that handles this scenario gracefully.
     *
     * @param baseDirectory the base directory in which <code>SpellDictionaryDisk</code>
     * can expect to find its necessary files.
     * @param phoneticFile the phonetic file used by the spellchecker.
     * @throws java.io.FileNotFoundException indicates problems locating the
     * files on the system
     * @throws java.io.IOException indicates problems reading the files
     */
    public DiskSpellingDictionary( File baseDirectory, File phoneticFile ) 
            throws FileNotFoundException, IOException {
        super( phoneticFile );
        this.ready = false;

        this.baseDirectory = baseDirectory;
        this.wordListFile = new File( baseDirectory, DIRECTORY_WORDS );
        this.databaseDirectory = new File( baseDirectory, DIRECTORY_DB );

        if ( !this.baseDirectory.exists() ) {
            throw new FileNotFoundException( 
                    "Couldn't find required path '" 
                            + this.baseDirectory + "'" );
        }
        if ( !this.wordListFile.exists() ) {
            throw new FileNotFoundException( 
                    "Couldn't find required path '" 
                            + this.wordListFile + "'" );
        }
        if ( !this.databaseDirectory.exists() ) {
            databaseDirectory.mkdirs();
        }

        if ( newDictionaryFiles() ) {
            buildNewDictionaryDatabase();
            loadIndex();
            ready = true;
        }
        else {
            loadIndex();
            ready = true;
        }
    }

    /**
     * Builds the file words database file and the contents file for the on disk
     * dictionary.
     *
     * @throws java.io.FileNotFoundException if the files cannot be located
     */
    protected final void buildNewDictionaryDatabase() 
            throws FileNotFoundException, IOException {
        /* combine all dictionary files into one sorted file */
        File sortedFile = buildSortedFile();

        /* create the db for the sorted file */
        buildCodeDatabase( sortedFile );
        sortedFile.delete();

        /* build contents file */
        buildContentsFile();
    }

    /**
     * Adds another word to the dictionary. <em>This method is not yet
     * implemented for this class</em>.
     *
     * @param word The word to add.
     */
    @Override
    public void addWord( String word ) {
        throw new UnsupportedOperationException( "addWord not implemented" );
    }

    /**
     * Returns a list of words that have the same phonetic code.
     *
     * @param code The phonetic code common to the list of words
     * @return A list of words having the same phonetic code
     */
    @Override
    public List<String> getWords( String code ) {
        ArrayList<String> words = new ArrayList<>();

        Location location = getLocationFor( code );
        if ( location != null ) {
            try (InputStream input = 
                    new FileInputStream( new File( databaseDirectory, FILE_DB ) )) {
                input.skip( location.startIndex() );
                byte[] bytes = new byte[location.length()];
                input.read( bytes, 0, location.length() );

                String data = new String( bytes );
                String[] lines = data.split( "\\R" );  // SB: any linebreak sequence
                for ( String line : lines ) { 
                    String[] s = line.split( "," );
                    if ( s[0].equals( code ) ) {
                        words.add( s[1] );
                    }
                }
            }
            catch ( IOException e ) {
                LOGGER.error( 
                        "Unable to access file at : {}" + FILE_DB + "{}{}", 
                        this.databaseDirectory, 
                        System.lineSeparator(), 
                        e.getMessage());
            }
        }

        return words;
    }

    /**
     * Indicates if the initial preparation or loading of the on disk dictionary
     * is complete.
     *
     * @return the indication that the dictionary initial setup is done.
     */
    public boolean isReady() {
        return ready;
    }

    private boolean newDictionaryFiles() throws FileNotFoundException, IOException {
        /* load in contents file, which indicates the files and sizes of the last db build */
        List<FileSize> contents = new ArrayList<>();
        File contentsFile = new File( databaseDirectory, FILE_CONTENTS );
        if ( contentsFile.exists() ) {
            try ( BufferedReader reader = 
                    new BufferedReader( new FileReader( contentsFile ) ) ) {
                
                String line;
                while ( (line = reader.readLine()) != null ) {
                    // format of file should be [filename],[size]
                    String[] s = line.split( "," );
                    contents.add( new FileSize( s[0], Integer.parseInt( s[1] ) ) );
                }
            }
            catch ( FileNotFoundException e ) {
                LOGGER.error( e.getMessage() );
                throw e;
            }
            catch ( IOException e ) {
                LOGGER.error( e.getMessage());
                throw e;
            }
        }

        /* compare this to the actual directory */
        boolean changed = false;
        File[] wordFiles = wordListFile.listFiles();
        if ( contents.size() != wordFiles.length ) {
            // if the size of the contents list and the number 
            // of word files are different, it
            // means we've definitely got to reindex
            changed = true;
        }
        else {
            // check and make sure that all the word files haven't changed on us
            for ( File wordFile : wordFiles ) {
                FileSize fileSize = 
                        new FileSize( wordFile.getName(), wordFile.length() );
                if ( !contents.contains( fileSize ) ) {
                    changed = true;
                    break;
                }
            }
        }

        return changed;
    }

    // SB: notes made by original developers date from 2005 at the latest.
    // SB: Most users in 2014 have a large amount of RAM and TB+ drives, so the 
    // SB: possibility of running out of RAM or disk space are less likely.
    // SB: FIXME : tidy up exception handling & add logging
    private File buildSortedFile() throws FileNotFoundException, IOException {
        List<String> wordList = new ArrayList<>();

        /*
         * read every single word into the list. eeek. if this causes problems,
         * we may wish to explore disk-based sorting or more efficient memory-based storage
         */
        File[] wordFiles = wordListFile.listFiles();
        for ( File wordFile : wordFiles ) { 
            try (BufferedReader br = 
                    new BufferedReader( new FileReader( wordFile ) ) ) {
                String word;
                while ( (word = br.readLine()) != null ) {
                    if ( !word.equals( "" ) ) {
                        wordList.add( word.trim() );
                    }
                }
            }
        }

        Collections.sort( wordList );

        // FIXME - error handling for running out of disk space would be nice.
        File file;
        try {
            file = File.createTempFile( "mdsc", "sorted" );
        } 
        catch ( IOException e ) {
            // log
            LOGGER.error( e.getMessage() );
            // rethrow
            throw ( e );
        }
        
        try ( BufferedWriter writer = 
                new BufferedWriter( new FileWriter( file ) ) ) {
            String previousWord = null;
            for ( String word : wordList ) { 
                if ( previousWord == null || !previousWord.equals( word ) ) {
                    writer.write( word );
                    writer.newLine();
                }
                previousWord = word;
            }
        }

        return file;
    }

    private void buildCodeDatabase( File sortedWords ) 
            throws FileNotFoundException, IOException {
        List<CodeWord> codeWordList = new ArrayList<>();

        try ( BufferedReader reader = 
                new BufferedReader( new FileReader( sortedWords ) ) ) {
            String word;
            while ( (word = reader.readLine()) != null ) {
                codeWordList.add( new CodeWord( this.getPhoneticCode( word ), word ) );
            }
        }

        Collections.sort( codeWordList );

        List<IndexedCode> codeIndex = new ArrayList<>();

        try ( BufferedOutputStream out = 
                new BufferedOutputStream( new FileOutputStream( new File( databaseDirectory, FILE_DB ) ) ) ) {
            String currentCode = null;
            int currentPosition = 0;
            int currentLength = 0;
            for ( CodeWord codeWord : codeWordList ) {
                String thisCode = codeWord.getCode();
    //            if (thisCode.length() > 3) thisCode = thisCode.substring(0, 3);
                thisCode = getIndexCode( thisCode, codeWordList );
                String toWrite = codeWord.getCode() + "," + codeWord.getWord() + "\n";
                byte[] bytes = toWrite.getBytes();

                if ( currentCode == null ) {
                    currentCode = thisCode;
                }
                if ( !currentCode.equals( thisCode ) ) {
                    codeIndex.add( new IndexedCode( currentCode, currentPosition, currentLength) );
                    currentPosition += currentLength;
                    currentLength = bytes.length;
                    currentCode = thisCode;
                }
                else {
                    currentLength += bytes.length;
                }
                out.write( bytes );
            }
            
            // Output the last iteration
            if ( currentCode != null && currentPosition != 0 && currentLength != 0 ) {
                codeIndex.add( new IndexedCode( currentCode, currentPosition, currentLength ) );
            }
        }


        try ( BufferedWriter writer = 
                new BufferedWriter( new FileWriter( new File( this.databaseDirectory, FILE_INDEX ) ) ) ) {
            for ( IndexedCode indexedCode : codeIndex ) {
                writer.write( indexedCode.code() );
                writer.write( "," );
                writer.write( indexedCode.position() );
                writer.write( "," );
                writer.write( indexedCode.length() );
                writer.newLine();
            }
        }
    }

    /**
     *
     * @throws IOException if a problem is encountered writing to disk
     */
    private void buildContentsFile() throws IOException {
        File[] wordFiles = this.wordListFile.listFiles();
        if ( wordFiles.length > 0 ) {
            try ( BufferedWriter writer = 
                    new BufferedWriter( new FileWriter( new File( this.databaseDirectory, FILE_CONTENTS ) ) ) ) {
                for ( File wordFile : wordFiles ) {
                    writer.write( wordFile.getName() );
                    writer.write( "," );
                    writer.write( String.valueOf( wordFile.length() ) );
                    writer.newLine();
                }
            }
        }
        else {
            new File( databaseDirectory, FILE_CONTENTS ).delete();
        }
    }

    /**
     * Loads the index file from disk. The index file accelerates word lookup
     * in the dictionary database file.
     *
     * @throws java.io.IOException if a problem is encountered reading the index file.
     */
    protected final void loadIndex() throws IOException {
        this.index = new HashMap<>();
        File indexFile = new File( databaseDirectory, FILE_INDEX );
        try ( BufferedReader reader = 
                new BufferedReader( new FileReader( indexFile ) ) ) {
            String line;
            while ( (line = reader.readLine()) != null ) {
                String[] fields = line.split( "," );
                this.index.put( 
                        fields[0], 
                        new Integer[]{Integer.parseInt( fields[1] ), 
                            Integer.parseInt( fields[2] )} );
            }
        }
        catch ( IOException e ) {
            // log it
            LOGGER.error( e.getMessage() );
            // rethrow
            throw ( e );
        }
    }

    private Location getLocationFor( String code ) {
        while ( code.length() > 0 ) {
            Integer[] positionAndLength = this.index.get( code );
            if ( positionAndLength == null ) {
                code = code.substring( 0, code.length() - 1 );
            }
            else {
                return new Location( positionAndLength[0], positionAndLength[1] );
            }
        }
        return null;
    }

    
    // SB: Specifying the generic type CodeWord for the list is incompatible 
    // SB: with the call to Collections.binarySearch() according to NetBeans.
    // SB: don't see it myself, but ...
    // SB: see how this works in production (if this class is used in production).
    private String getIndexCode( String code, List codeWords ) {
        if ( this.indexCodeCache == null ) {
            this.indexCodeCache = new ArrayList<>();
        }

        if ( code.length() <= 1 ) {
            return code;
        }
        
        for ( String c : this.indexCodeCache ) {
            if ( code.startsWith( c ) ) {
                return c;
            }
        }

        int foundSize = -1;
        boolean cacheable = false;
        for ( int z = 1; z < code.length(); z++ ) {
            String thisCode = code.substring( 0, z );
            int count = 0;
            for ( int i = 0; i < codeWords.size(); ) {
                if ( i == 0 ) {
                    i = Collections.binarySearch( codeWords, new CodeWord( thisCode, "" ) );
                    if ( i < 0 ) {
                        i = 0;
                    }
                }

                CodeWord cw = (CodeWord)codeWords.get( i );
                if ( cw.getCode().startsWith( thisCode ) ) {
                    count++;
                    if ( count > MAXIMUM_INDEX_SIZE ) {
                        break;
                    }
                }
                else if ( cw.getCode().compareTo( thisCode ) > 0 ) {
                    break;
                }
                i++;
            }
            if ( count <= MAXIMUM_INDEX_SIZE ) {
                cacheable = true;
                foundSize = z;
                break;
            }
        }

        String newCode = (foundSize == -1) ? code : code.substring( 0, foundSize );
        if ( cacheable ) {
            this.indexCodeCache.add( newCode );
        }
        return newCode;
    }

    private class CodeWord implements Comparable {

        private final String code;
        private final String word;

        public CodeWord( String code, String word ) {
            this.code = code;
            this.word = word;
        }

        public String getCode() {
            return this.code;
        }

        public String getWord() {
            return this.word;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( !(o instanceof CodeWord) ) {
                return false;
            }

            final CodeWord codeWord = (CodeWord) o;

            return this.word.equals( codeWord.word );
        }

        @Override
        public int hashCode() {
            return this.word.hashCode();
        }

        @Override
        public int compareTo( Object o ) {
            return this.code.compareTo( ((CodeWord) o).getCode() );
        }
    }

    private class FileSize {

        private final String filename;
        private final long size;

        public FileSize( String filename, long size ) {
            this.filename = filename;
            this.size = size;
        }

        public String getFilename() {
            return this.filename;
        }

        public long getSize() {
            return this.size;
        }

        /**
         * 
         * @param o an {@code Object} to compare this instance with.
         * @return {@code true} if the references are to the same object 
         * or if and only if the objects have identical states.
         */
        @Override
        public boolean equals( Object o ) {
            if ( this == o ) {
                return true;
            }
            if ( !(o instanceof FileSize) ) {
                return false;
            }

            final FileSize fileSize = (FileSize) o;

            return ( ( this.size == fileSize.size ) 
                    && ( this.filename.equals( fileSize.filename ) ) );
        }

        @Override
        public int hashCode() {
            int result;
            result = this.filename.hashCode();
            result = (int) (29 * result + this.size);
            return result;
        }
    }
    
    
    private final class Location {
        private final int startIndex;
        private final int length;
        
        private Location( int startIndex, int length ) {
            this.startIndex = startIndex;
            this.length = length;
        }
        
        int startIndex() {
            return this.startIndex;
        }
        
        int length() {
            return this.length;
        }
    }
    
    
    private final class IndexedCode {
        private final String code;
        private final int position;
        private final int length;
        
        private IndexedCode( String code, int position, int length ) {
            this.code = code;
            this.position = position;
            this.length = length;
        }
        
        String code() {
            return this.code;
        }
        
        int position() {
            return this.position;
        }
        
        int length() {
            return this.length;
        }
    }
    
}
