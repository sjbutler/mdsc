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

/**
 * The result of a spell check using a single dictionary.
 */
public class Result {

    private final String dictionaryName;
    private final String word;
    private final boolean isCorrect;
    private final ArrayList<SuggestedSpelling> suggestions;
    
    /**
     * Specifies a word that is spelt correctly. 
     * 
     * @param word
     * @param dictionaryName 
     */
    Result( String word, String dictionaryName ) {
        this.word = word;
        this.dictionaryName = dictionaryName;
        this.isCorrect = true;
        this.suggestions = null;
    }
    
    /**
     * Specifies a word that is misspelt and offers suggested corrections. The 
     * number of corrections offered are constrained by cost and a numeric 
     * limit set when the library is instantiated. 
     * 
     * @param word
     * @param dictionaryName
     * @param suggestions 
     */
    Result( 
            String word, 
            String dictionaryName, 
            ArrayList<SuggestedSpelling> suggestions ) {
        this.word = word;
        this.dictionaryName = dictionaryName;
        this.isCorrect = false;
        this.suggestions = suggestions;
    }
            
    /**
     * Specifies a result that is correct and offers 
     * possible alternative spellings. This may not make it into production.
     * 
     * @param word the string tested
     * @param dictionaryName the name of the dictionary reporting the result
     * @param alternatives a list of alternatives if the word was not found in 
     * the dictionary
     * @param isCorrect true if the word is found in the dictionary
     */
    Result( 
            String word, 
            String dictionaryName, 
            ArrayList<SuggestedSpelling> alternatives, 
            boolean isCorrect ) {
        this.word = word;
        this.dictionaryName = dictionaryName;
        this.isCorrect = isCorrect;
        this.suggestions = alternatives;
    }
            
    /**
     * Indicates whether the 'word' is correctly spelt in the queried dictionaries.
     * @return {@code true} if the word was found in the dictionary.
     */
    public boolean isCorrect() {
        return this.isCorrect;
    }
    
    /**
     * The name of the dictionary used.
     * @return a {@code String} identifying the dictionary. 
     */
    public String dictionaryName() {
        return this.dictionaryName;
    }
    
    /**
     * The word that was spell-checked.
     * @return the string tested
     */
    public String word() {
        return this.word;
    }
    
    /**
     * Retrieves a list of suggested alternative spellings.
     * @return a list of suggested spellings
     */
    public ArrayList<SuggestedSpelling> suggestions() {
        return this.suggestions;
    }
    
}
