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

import java.util.List;

/**
 * Provides a list of 'words' from one of the mdsc files. See {@code Wordlists} 
 * for details of available files. The constructors allow the specification of 
 * lower case and that words contain a minimum number of characters. The latter 
 * is supports some applications where short tokens can lead to noisy results. 
 * 
 */
public class Wordlist {

    private List<String> list;
    private String tag;
    
    /**
     * Creates a list of 'words' from the specified source file.
     * @param wordListName a constant from the {@code Wordlists} enumeration  
     */
    public Wordlist(final Wordlists wordListName) {
	this(wordListName, false, 1);
    }
    
    /**
     * Creates a list of 'words' from the specified source file and allows
     * the words to be normalised to lower case.
     * @param wordListName a constant from the {@code Wordlists} enumeration 
     * @param normalised a boolean that indicates whether words in the
     * recovered list should be normalised to lower case.
     */
    public Wordlist(final Wordlists wordListName, final boolean normalised) {
	this(wordListName, normalised, 1);
    }
    
    /**
     * Creates a list of 'words' from the specified source file, that can be 
     * normalised to lower case, and consists of at least a minimum number of 
     * characters.
     * @param wordListName a constant from the {@code Wordlists} enumeration 
     * @param normalised a boolean that indicates whether words in the
     * recovered list should be normalised to lower case.
     * @param minimumLength the length of the shortest token to include in the 
     * wordlist. 1 loads every token, and is the default for the other constructors.
     */
    public Wordlist(final Wordlists wordListName, final boolean normalised, final int minimumLength) {
	this.tag = wordListName.tag();
        WordlistReader reader = new WordlistReader(wordListName);
	if (! normalised ) {
	    if ( minimumLength == 1) {
		this.list = reader.asList();
	    }
	    else {
		this.list = reader.asList(minimumLength);
	    }
	}
	else if ( normalised ) {
	    if (minimumLength == 1) {
		this.list = reader.asLowerCaseList();
	    }
	    else {
		this.list = reader.asLowerCaseList( minimumLength );
	    }
	}
    }
     
    /**
     * Returns the list of words. 
     * @return a {@code List} of 'words'
     */
    public List<String> list() {
	
	return this.list;
    }
    
    /**
     * A identifying string for the word list.
     */
    public String tag() {
        return this.tag;
    }
}
