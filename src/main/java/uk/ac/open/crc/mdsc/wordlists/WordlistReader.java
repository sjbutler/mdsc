/*
    mdsc - a multiple dictionary spell checker
    Copyright (C) 2017 Simon Butler 
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

package uk.ac.open.crc.mdsc.wordlists;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class for reading plain text files. The file can be plain text or 
 * contain comments (line or end of line) that begin with a #. Specifically
 * the class is intended to read lists of words with one word per line. 
 * Comments are permitted to allow annotation of files.
 * 
 */
class WordlistReader {
    private static final Logger LOGGER = 
            LoggerFactory.getLogger( WordlistReader.class );
    
    private final String fileName;
    
    private final List<Line> list;
    
    /** 
     * Reads the specified file. 
     * 
     * @param specifier
     */
    WordlistReader( final Wordlists specifier ) {
        this.fileName = specifier.path();
        this.list = new ArrayList<>();
        read();
    }
    
    /**
     * Recovers a list of the non-comment terms found in the word list file.
     * @return a {@code List} of 'words'
     */
    List<String> asList() {
        ArrayList<String> payloadList = new ArrayList<>( this.list.size() );
        
        this.list.stream().forEach( line -> { 
            if ( line.hasPayload() ) {
                payloadList.add( line.payload() );
            }
        } );
        
        return payloadList;
    }
    
    /**
     * Recovers a list of the non-comment terms containing at least the 
     * specified number of characters 
     * found in the word list file normalised to lower case.
     * @param minimumWordLength a positive integer specifying the minimum 
     * number of characters required
     * @return a {@code List} of lower case words of at least the specified 
     * number of characters
     */
    List<String> asList( int minimumWordLength ) {
        ArrayList<String> payloadList = new ArrayList<>( this.list.size() );
        
        this.list.stream().forEach( line -> { 
            if ( line.hasPayload() && line.payload().length() >= minimumWordLength ) {
                payloadList.add( line.payload() );
            }
        } );
        
        return payloadList;
    }
    
    
    /**
     * Recovers a list of the non-comment terms found in the word list file 
     * normalised to lower case.
     * @return a {@code List} of lower case 'words'
     */
    List<String> asLowerCaseList() {
        ArrayList<String> payloadList = new ArrayList<>( this.list.size() );
        
        this.list.stream().forEach( line -> { 
            if ( line.hasPayload() ) {
                payloadList.add( line.payload().toLowerCase() );
            }
        } );
        
        return payloadList;
    }
    
    
    /**
     * Recovers a list of the non-comment terms with two or more characters 
     * found in the word list file normalised to lower case.
     * @return a {@code List} of lower case words of two or more characters
     */
    List<String> asLowerCaseListNoSingleLetters() {
        return asLowerCaseList( 2 );
    }
    
    
    /**
     * Recovers a list of the non-comment terms containing at least the 
     * specified number of characters 
     * found in the word list file normalised to lower case.
     * @param minimumWordLength a positive integer specifying the minimum 
     * number of characters required
     * @return a {@code List} of lower case words of at least the specified 
     * number of characters
     */
    List<String> asLowerCaseList( int minimumWordLength ) {
        ArrayList<String> payloadList = new ArrayList<>( this.list.size() );
        
        this.list.stream().forEach( line -> { 
            if ( line.hasPayload() && line.payload().length() >= minimumWordLength ) {
                payloadList.add( line.payload().toLowerCase() );
            }
        } );
        
        return payloadList;
    }
    
    
    private void read() {
        InputStream inStream = this.getClass().getResourceAsStream( 
		"/wordlists/" + this.fileName );
        try ( BufferedReader in = new BufferedReader(new InputStreamReader(inStream))) {
            String line;
            
            while ( ( line = in.readLine() ) != null ) {
                this.list.add( new Line( line.trim() ) );
            }
        }
        catch ( IOException e ) {
            LOGGER.error( "Encountered problem reading from \"{}\"\n" 
                    + e.getMessage(), 
                    this.fileName );
        }
    }
    
    /**
     * Represents a line in a file.
     */
    private class Line {
        private final String content;
        private final String comment;
        private final String payload;
        
        private final boolean isComment;
        private final boolean containsComment;
        private final boolean isBlank;
        private final boolean hasPayload;
        
        Line( String rawLine ) {
            this.content = rawLine;
            if ( rawLine.startsWith( "#" ) ) {
                this.comment = rawLine.trim();
                this.payload = "";
            }
            else if ( rawLine.contains( "#" ) ) {
                int index = rawLine.indexOf( "#" );
                this.payload = rawLine.substring( 0, index ).trim();
                this.comment = rawLine.substring( index ).trim();
            }
            else {
                this.comment = "";
                this.payload = rawLine.trim();
            }
            
            this.isComment = this.comment.equals( this.content );
            this.containsComment = ! this.comment.isEmpty();
            this.isBlank = this.content.isEmpty();
            this.hasPayload = ! this.payload.isEmpty();
        }
        
        String content() {
            return this.content;
        }
        
        String comment() {
            return this.comment;
        }
        
        String payload() {
            return this.payload;
        }
        
        boolean hasPayload() {
            return this.hasPayload;
        }
        
        boolean isBlank() {
            return this.isBlank;
        }
        
        boolean isComment() {
            return this.isComment;
        }
        
        boolean containsComment() {
            return this.containsComment;
        }
        
        String originalText() {
            return this.content;
        }
    }
}
