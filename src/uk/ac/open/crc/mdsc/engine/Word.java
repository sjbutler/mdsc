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

import java.util.Comparator;
import java.util.Objects;

/**
 * The Word object holds information for one suggested spelling. It contains
 * both the suggested word string and the distance cost, which represents how
 * different the suggested word is from the misspelling.
 */
public class Word implements Comparator<Word> {

    private String word;
    private final int score;

    /**
     * Constructs a new Word.
     *
     * @param word The text of a word.
     * @param score The word's distance cost
     */
    public Word( String word, int score ) {
        this.word = word;
        this.score = score;
    }

    /**
     * Constructs a new Word.
     */
    public Word() {
        this.word = "";
        this.score = 0;
    }

    /**
     * Compares two words, mostly for the purpose of sorting words.
     *
     * @param word1 the first word
     * @param word2 the second word
     * @return -1 if the first word is more similar to the misspelled word
     * <br>1 if the second word is more similar to the misspelled word
     * <br>0 if both words are equally similar
     *
     */
    @Override
    public int compare( Word word1, Word word2 ) {
        if ( word1.getCost() < word2.getCost() ) {
            return -1;
        }
        if ( word1.getCost() == word2.getCost() ) {
            return 0;
        }
        return 1;
    }

    /**
     * Indicates if this word is equal to another one.
     *
     * @param o The other word to compare
     * @return The indication of equality
     */
    @Override
    public boolean equals( Object o ) {
        if ( o instanceof Word ) { // added by bd
            return (((Word) o).getWord().equals( getWord() ));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode( this.word );
        return hash;
    }

    /**
     * gets suggested spelling
     *
     * @return the actual text of the suggest spelling
     */
    public String getWord() {
        return this.word;
    }

    /**
     * Sets suggested spelling.
     *
     * @param word The text to set for suggested spelling
     */
    public void setWord( String word ) {
        this.word = word;
    }

    /**
     * A cost measures how close a match this word was to the original word.
     *
     * @return 0 if an exact match. Higher numbers are worse matches.
     * @see EditDistance
     */
    public int getCost() {
        return this.score;
    }

}