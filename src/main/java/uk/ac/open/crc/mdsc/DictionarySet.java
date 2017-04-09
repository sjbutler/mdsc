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

import java.util.ArrayList;
import java.util.List;

/**
 * {@code DictionarySet} provides the spell checking functionality in 
 * mdsc. The dictionaries used are created from lists of words using the 
 * {@linkplain DictionaryManager} class. A DictionarySet is then obtained from 
 * the manager and used to spell check. 
 */
public class DictionarySet {

    private final List<Dictionary> dictionaries;
    
    /**
     * Creates an empty set of dictionaries. {@code DictionarySet} is 
     * only ever instantiated by the manager class, so there is no
     * need for this constructor to be public.
     */
    DictionarySet() {
        this.dictionaries = new ArrayList<>();
    }
    
    private DictionarySet( List<Dictionary> dictionaries ) {
        this.dictionaries = dictionaries;
    }
    
    
    /**
     * Copy constructor. The copy is a (largely) shallow copy that 
     * preserves references to the set of dictionaries held by the 
     * {@code DictionaryManager} at the time
     * @param existingDictionarySet an instance of {@code DictionarySet} 
     * to be copied
     */
    DictionarySet( DictionarySet existingDictionarySet ) {
        this( existingDictionarySet.getDictionaryList() );
    }

    /**
     * Register a dictionary.
     * @param dictionary a dictionary 
     */
    void register( Dictionary dictionary ) {
        this.dictionaries.add( dictionary );  // may need to be more circumspect
    }
    
    /** 
     * Remove a dictionary from the set.
     * @param dictionaryName  a dictionary name
     */
    void remove( String dictionaryName ) {
        throw new UnsupportedOperationException( "remove dictionary not implemented" );
    }
    
    /**
     * Checks the spelling of a single word in multiple dictionaries and reports
     * whether the word has been identified, and in which dictionary, and 
     * offers suggested alternative spellings for incorrectly spelt words.
     * 
     * @param word a word to be spell checked by the dictionaries
     * @return a list of {@code Result}s giving the result of the check 
     * for each dictionary
     */
    public List<Result> spellCheck( String word ) {
        if ( word == null || word.isEmpty() || containsNonWordCharacters( word ) ) {
            throw new IllegalArgumentException( 
                    "Only single words accepted for spell checking." );
        }

        ArrayList<Result> results = new ArrayList<>();
        
        this.dictionaries.stream().forEach( (dictionary) -> {
            results.add( dictionary.checkSpelling( word ) );
        } );
        
        return results;
    }
    
    /**
     * Recovers a list of the dictionaries in this set.
     * @return a list of dictionaries.
     */
    protected List<Dictionary> getDictionaryList() {
        return this.dictionaries;
    }
    
    private boolean containsNonWordCharacters( String testString ) {
        return !testString.matches( "\\w+" );
    }
    
}
