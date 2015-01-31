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

import uk.ac.open.crc.mdsc.engine.Word;

/**
 * A class containing a suggested spelling, the cost of changing the given 
 * word to the candidate spelling and the name of the dictionary used to 
 * identify the suggestion.
 *
 *
 * @author Simon Butler (simon@facetus.org.uk)
 */
public final class SuggestedSpelling implements Comparable<SuggestedSpelling> {

    private final String suggestedWord;
    private final int cost;
    private final String dictionaryName;
    
    /**
     * Creates a suggested spelling.
     * @param word a transfer object from an instance of {@code SpellingDictionary}
     * @param dictionaryName the name of the dictionary supplying the suggestion
     * @see Dictionary
     */
    SuggestedSpelling( Word word, String dictionaryName ) {
        this.suggestedWord = word.getWord();
        this.cost = word.getCost();
        this.dictionaryName = dictionaryName;
    }
    
    /**
     * The word tested.
     * @return a String containing the word tested by the caller.
     */
    public String word() {
        return this.suggestedWord;
    }
    
    /**
     * The cost of the transformation(s) required to change the 
     * spell checked word to the substitution. The individual costs are 
     * substitution 100, deletion 95, swap two letters 90, and change case 10.
     * @return an integer indicating the cost of transforming the input word
     */
    public int cost() {
        return this.cost;
    }
    
    /**
     * Retrieves the name of the dictionary that supplied the suggested spelling.
     * @return a dictionary name
     */
    public String dictionaryName() {
        return this.dictionaryName;
    }
    
    @Override
    public int compareTo( SuggestedSpelling compared ) {
        if ( this == compared ) {
            return 0;
        }
        
        if ( this.cost < compared.cost() ) {
            return -1;
        }
        if ( this.cost == compared.cost() ) {
            // sort on the words
            return this.word().compareToIgnoreCase( compared.word() );
        }
        else {
            return 1;
        }
    }
    
    
}
