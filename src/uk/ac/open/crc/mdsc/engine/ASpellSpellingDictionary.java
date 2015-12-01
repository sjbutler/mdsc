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
/* Created by bgalbs on Jan 30, 2003 at 11:45:25 PM */
package uk.ac.open.crc.mdsc.engine;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Container for various methods that any <code>SpellingDictionary</code> will use.
 * This class is based on the original Jazzy aspell port.
 * <p>
 * Derived classes will need words list files as spell checking reference. Words
 * list file is a dictionary with one word per line. There are many open source
 * dictionary files, see:
 * <a href="http://wordlist.sourceforge.net/">
 * http://wordlist.sourceforge.net/</a>
 * </p>
 * <p>
 * You can choose words lists from <a href="http://aspell.net/">aspell</a>'s
 * many different language dictionaries. To grab some, install
 * <code>aspell</code> and the dictionaries you require. Then run aspell
 * specifying the name of the dictionary and the words list file to dump it
 * into, for example:<br>
 * {@code aspell --master=fr-40 dump master > fr-40.txt}<br>
 *  Note: the number following the language is the size indicator. A
 * bigger number gives a more extensive language coverage. Size 40 is more than
 * adequate for many usages.
 * </p>
 * For some languages, Aspell can also supply you with the phonetic file. On
 * Windows, go into aspell <code>data</code> directory and copy the phonetic
 * file corresponding to your language, for example the
 * <code>fr_phonet.dat</code> for the <code>fr</code> language. The phonetic
 * file should be in directory <code>/usr/share/aspell</code> on Unix.
 *
 * @see GenericTransformator GenericTransformator for information on phonetic
 * files.
 */
public abstract class ASpellSpellingDictionary implements SpellingDictionary {

    private static final Logger LOGGER = 
            LoggerFactory.getLogger( ASpellSpellingDictionary.class );
    
    /**
     * The reference to a Transformator, used to transform a word into its
     * phonetic code.
     */
    protected Transformator transformator;

    /**
     * Constructs a new SpellDictionaryASpell
     *
     * @param phonetic The file to use for phonetic transformation of the words
     * list. If <code>phonetic</code> is null, the the transformation uses
     * {@link DoubleMetaphoneTransformator} transformation.
     * @throws java.io.IOException indicates problems reading the phonetic
     * information
     */
    public ASpellSpellingDictionary( File phonetic ) throws IOException {
        if ( phonetic == null ) {
            this.transformator = new DoubleMetaphoneTransformator();
        }
        else {
            this.transformator = new GenericTransformator( phonetic );
        }
    }

    /**
     * Constructs a new SpellDictionaryASpell
     *
     * @param phonetic The file to use for phonetic transformation of the words
     * list. If <code>phonetic</code> is null, the the transformation uses
     * {@link DoubleMetaphoneTransformator} transformation.
     * @param encoding Uses the character set encoding specified
     * @throws java.io.IOException indicates problems reading the phonetic
     * information
     */
    public ASpellSpellingDictionary( File phonetic, String encoding ) 
            throws IOException {
        if ( phonetic == null ) {
            this.transformator = new DoubleMetaphoneTransformator();
        }
        else {
            this.transformator = new GenericTransformator( phonetic, encoding );
        }
    }

    /**
     * Constructs a new ASpellSpellingDictionary
     *
     * @param phonetic The Reader to use for phonetic transformation of the
     * words list. If <code>phonetic</code> is null, the the transformation uses
     * {@link DoubleMetaphoneTransformator} transformation.
     * @throws java.io.IOException indicates problems reading the phonetic
     * information
     */
    public ASpellSpellingDictionary( Reader phonetic ) throws IOException {
        if ( phonetic == null ) {
            transformator = new DoubleMetaphoneTransformator();
        }
        else {
            transformator = new GenericTransformator( phonetic );
        }
    }

    /**
     * Returns a list of Word objects that are the suggestions to an incorrect
     * word.
     * <p>
     * This method is only needed to provide backward compatibility.
     * </p>
     * @see #getSuggestions(String, int, int[][])
     * @param word Suggestions for given misspelt word
     * @param threshold The lower boundary of similarity to misspelt word
     * @return a List of suggested alternative spellings
     */
    @Override
    public List<Word> getSuggestions( String word, int threshold ) {

        return getSuggestions( word, threshold, null );

    }

    // SB: NB threshold is not used -- issues in practice may dictate
    // SB: that it needs to be used to control the returned list. 
    // the abstraction level changes a lot (unhelpfully), so refactoring may be wise
    /**
     * Returns a list of Word objects that are the suggestions to an incorrect
     * word.
     * <p>
     * @param word suggestions for given misspelt word
     * @param threshold The lower boundary of similarity to misspelt word
     * @param matrix Two dimensional int array used to calculate edit distance.
     * Allocating this memory outside of the function will greatly improve
     * efficiency.
     * @return Vector a List of suggested alternative spellings
     */
    @Override
    public List<Word> getSuggestions( 
            String word, 
            int threshold, 
            int[][] matrix ) {

        int i;
        int j;

        if ( matrix == null ) {
            matrix = new int[0][0];
        }

        HashMap<String,String> nearMissCodes = new HashMap<>();
        String code = getPhoneticCode( word );

        // add all words that have the same phonetics
        nearMissCodes.put( code, code );
        List<Word> phoneticList = getWordsFromCode( word, nearMissCodes );

        // do some transformations to pick up more results
        //interchange
        nearMissCodes = new HashMap<>();
        char[] charArray = word.toCharArray();
        char a;
        char b;

        for ( i = 0; i < word.length() - 1; i++ ) {
            a = charArray[i];
            b = charArray[i + 1];
            charArray[i] = b;
            charArray[i + 1] = a;
            String s = getPhoneticCode( new String( charArray ) );
            nearMissCodes.put( s, s );
            charArray[i] = a;
            charArray[i + 1] = b;
        }

        char[] replacelist = transformator.getReplaceList();

        //change
        charArray = word.toCharArray();
        char original;
        for ( i = 0; i < word.length(); i++ ) {
            original = charArray[i];
            for ( j = 0; j < replacelist.length; j++ ) {
                charArray[i] = replacelist[j];
                String s = getPhoneticCode( new String( charArray ) );
                nearMissCodes.put( s, s );
            }
            charArray[i] = original;
        }

        //add
        charArray = (word += " ").toCharArray();
        int iy = charArray.length - 1;
        while ( true ) {
            for ( j = 0; j < replacelist.length; j++ ) {
                charArray[iy] = replacelist[j];
                String s = getPhoneticCode( new String( charArray ) );
                nearMissCodes.put( s, s );
            }
            if ( iy == 0 ) {
                break;
            }
            charArray[iy] = charArray[iy - 1];
            --iy;
        }

        //delete
        word = word.trim();
        charArray = word.toCharArray();
        char[] charArray2 = Arrays.copyOf( charArray, charArray.length - 1 ); 

        a = charArray[charArray.length - 1];
        int ii = charArray2.length;
        while ( true ) {
            String s = getPhoneticCode( new String( charArray ) );
            nearMissCodes.put( s, s );
            if ( ii == 0 ) {
                break;
            }
            b = a;
            a = charArray2[ii - 1];
            charArray2[ii - 1] = b;
            --ii;
        }

        nearMissCodes.remove( code ); //already accounted for in phoneticList

        List<Word> wordlist = getWordsFromCode( word, nearMissCodes );

        if ( wordlist.isEmpty() && phoneticList.isEmpty() ) {
            addBestGuess( word, phoneticList, matrix );
        }

        // Sort the lists before returning
        Collections.sort( phoneticList, new Word() ); //always sort phonetic matches along the top
        Collections.sort( wordlist, new Word() ); //the non-phonetic matches can be listed below

        phoneticList.addAll( wordlist );
        return phoneticList;
    }

   
    // SB: why does this method not create the list and return it?
    // Why insist on an empty list being passed in?
    // Review & revise? But look at the caller first (method above only).
    /**
     * When we don't come up with any suggestions (probably because the
     * threshold was too strict), then pick the best guesses from the those
     * words that have the same phonetic code.
     *
     * @param word - the word we are trying spell correct
     * @param matrix two dimensional array of int used to calculate edit distance.
     * Allocating this memory outside of the function will greatly improve
     * efficiency.
     * @param wordList - the linked list that will get the best guess
     */
    private void addBestGuess( 
            String word, 
            List<Word> wordList, 
            int[][] matrix ) {
        if ( matrix == null ) {
            matrix = new int[0][0];
        }

        int bestScore = Integer.MAX_VALUE;

        String code = getPhoneticCode( word );
        List<String> similarWordsList = getWords( code );

        LinkedList<Word> candidates = new LinkedList<>();

        for ( String similarWord : similarWordsList ) {
            int distance = EditDistance.getDistance( word, similarWord, matrix );
            if ( distance <= bestScore ) {
                bestScore = distance;
                candidates.add( new Word( similarWord, distance ) );
            }
        }

        //now, only pull out the guesses that had the best score
        for ( Word candidate : candidates ) {
            if ( candidate.getCost() == bestScore ) {
                wordList.add( candidate );
            }
        }

    }

    
    private List<Word> getWordsFromCode( 
            String word, 
            HashMap<String,String> codes ) {
        Configuration config = Configuration.getConfiguration();
        ArrayList<Word> result = new ArrayList<>();
        int[][] matrix = new int[0][0];
        final int configDistance = config.getInteger( Configuration.SPELL_THRESHOLD );

        codes.keySet().stream()
                .map( (code) -> getWords( code ) ).forEach( (similarWordList) -> {
            similarWordList.stream().forEach( (similarWord) -> {
                int distance = EditDistance.getDistance( word, similarWord, matrix );
                if ( distance < configDistance ) {
                    result.add( new Word( similarWord, distance ) );
                }
            } );
        } );

        return result;
    }

    /**
     * Returns the phonetic code representing the word.
     *
     * @param word The word we want the phonetic code.
     * @return The value of the phonetic code for the word.
     */
    public String getPhoneticCode( String word ) {
        return this.transformator.transform( word );
    }

    /**
     * Returns a list of words that have the same phonetic code.
     *
     * @param phoneticCode The phonetic code common to the list of words
     * @return A list of words having the same phonetic code
     */
    protected abstract List<String> getWords( String phoneticCode );

    // SB: NB this test behaves as a conventional spell checker with 
    // capitalisation, i.e. the test string 'sunday' would be seen as
    // incorrectly spelt if only 'Sunday' is in the dictionary.
    /**
     * Returns true if the word is correctly spelled against the current word
     * list.
     */
    @Override
    public boolean isCorrect( String word ) {
        List<String> possibles = getWords( getPhoneticCode( word ) );
        return ( possibles.contains( word ) 
                || possibles.contains( word.toLowerCase() ));
        // note from Jazzy author:
        //JMH should we always try the lowercase version. If I don't then capitalised
        //words are always returned as incorrect.
    }
}
