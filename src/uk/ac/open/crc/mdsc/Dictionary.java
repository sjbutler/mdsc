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
import java.util.Collections;
import java.util.List;
import uk.ac.open.crc.mdsc.engine.SpellingDictionary;
import uk.ac.open.crc.mdsc.engine.Word;

/**
 * Represents a single spelling dictionary. To understand the creation and 
 * management of these objects refer to the {@linkplain DictionaryManager} class.
 *<p>
 * NB: this class wraps an instance of {@linkplain uk.ac.open.crc.mdsc.engine.HashedSpellingDictionary} and is
 * constrained by the contracts of that class. The main issue to be aware of 
 * is the way that words are compared with those stored in the dictionary. The 
 * word being tested is compared with the words in the dictionary capitalised
 * as it is provided to the function, and then compared again after being 
 * normalised to lower case if there is no match. The words in the dictionary 
 * are not normalised. In other words, the word being tested is tested first 
 * case sensitive, then case insensitive, but the capitalisation of the 
 * reference word remains constant. Consequently, proper nouns are only marked as 
 * correct when their capitalisation matches that used in the dictionary. This 
 * is the expected behaviour of a spell checker, but might not always be 
 * what is wanted by the user of this class. The class 
 * {@linkplain NormalisedDictionaryManager} implements a dictionary that matches 
 * strings regardless of case, either in the dictionary or the string 
 * being tested.
 * 
 * @author Simon Butler (simon@facetus.org.uk)
 */
public class Dictionary {

    private final String name;
    private final String description;
    private final SpellingDictionary spellingDictionary;
    private final int maximumSuggestions;
    private final int maximumCost;
    
    /**
     * Creates a dictionary.
     * @param name a unique name for the dictionary
     * @param description a brief human readable description
     * @param spellingDictionary an instance of {@code SpellingDictionary} 
     * containing the dictionary itself
     * @param maximumSuggestions the maximum number of suggestions returned 
     * for a misspelt word
     * @param maximumCost the maximum cost of the transformation to a 
     * suggested alternative spelling
     */
    Dictionary( String name, 
            String description, 
            SpellingDictionary spellingDictionary, 
            int maximumSuggestions, 
            int maximumCost ) {
        this.name = name;
        this.description = description;
        this.spellingDictionary = spellingDictionary;
        this.maximumSuggestions = maximumSuggestions;
        this.maximumCost = maximumCost;
    } 
    
    

    /**
     * Retrieves the dictionary's name. The name is used as a tag to identify 
     * the dictionary internally. 
     * @return a {@code String} containing the dictionary's name.
     */
    public final String name() {
        return this.name;
    }
    
    /**
     * Retrieves the description. The description is intended to be human readable
     * and should be a succinct description of the dictionary.
     * @return a {@code String} containing the description of the dictionary
     */
    public final String description() {
        return this.description;
    }
    
    
    // keep this package private for the moment -- there's no need for 
    // this to be available in the public API
    final int costThreshold() {
        return this.maximumCost;
    }
    
    /**
     * Checks the spelling of a word in this dictionary. Allows the caller
     * to specify a cost threshold that is different to the value set in the
     * constructor. Costs are calculated using three digits where 100 is the 
     * equivalent of 1 in conventional understanding of the Levenshtein distance.
     * The authors of Jazzy specify a number of values for individual 
     * transformations that mostly range from 90-100, but also include include
     * a cost of 10 for changing case. See the uk.ac.open.crc.mdsc.engine
     * package.
     * 
     * @param word a word to be tested
     * @param costThreshold the maximum cost of any suggested alternative spelling
     * @return an instance of {@code Result} containing the results of the test 
     * including a list of any alternative spellings.
     */
    public Result checkSpelling( String word, int costThreshold ) {
        
        if ( this.spellingDictionary.isCorrect( word ) ) {
            return new Result( word, this.name );
        }
        else {
            List<Word> alternativeSpellings = 
                    this.spellingDictionary.getSuggestions( word, costThreshold );
            ArrayList<SuggestedSpelling> suggestions = new ArrayList<>();
            alternativeSpellings.stream().forEach( (Word alternative) -> {
                suggestions.add( new SuggestedSpelling( alternative, this.name ) );
            } );
            
            // sort and trim suggestions list before returning
            Collections.sort( suggestions );
            if ( suggestions.size() > this.maximumSuggestions ) {
                suggestions.subList( this.maximumSuggestions, suggestions.size() - 1 ).clear();
            }
            
            return new Result( word, this.name, suggestions );
        }
    }
    
    /**
     * Checks the spelling of a word in this dictionary. Uses the maximum cost 
     * value supplied to the constructor.
     * @param word a word to be tested
     * @return an instance of {@code Result} containing the results of the test 
     * including a list of any alternative spellings.
     */
    public Result checkSpelling( String word ) {
        return this.checkSpelling( word, this.maximumCost );
    }
    
    
}
