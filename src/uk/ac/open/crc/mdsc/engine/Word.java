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
 both the suggested text string and the distance cost, which represents how
 different the suggested text is from the misspelling.
 */
public class Word implements Comparator<Word> {

    private String text;
    private final int score;

    /**
     * Constructs a new Word.
     *
     * @param text the text of a suggested spelling
     * @param score the cost of transformation between the tested word and text
     */
    public Word( String text, int score ) {
        this.text = text;
        this.score = score;
    }

    /**
     * Constructs a new Word.
     */
    public Word() {
        this.text = "";
        this.score = 0;
    }

    /**
     * Compares two words, mostly for the purpose of sorting words.
     *
     * @param word1 the first text
     * @param word2 the second text
     * @return -1 if the first text is more similar to the misspelled text
 <br>1 if the second text is more similar to the misspelled text
 <br>0 if both words are equally similar
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
     * Indicates if this text is equal to another one.
     *
     * @param o The other text to compare
     * @return The indication of equality
     */
    @Override
    public boolean equals( Object o ) {
        if ( o instanceof Word ) { // added by bd
            return (((Word) o).getText().equals( getText() ));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.text );
        return hash;
    }

    /**
     * gets suggested spelling
     *
     * @return the actual text of the suggest spelling
     */
    public String getText() {
        return this.text;
    }

    /**
     * Sets suggested spelling.
     *
     * @param text the text of the suggested spelling
     */
    public void setText( String text ) {
        this.text = text;
    }

    /**
     * A cost measures how close a match this text was to the original text.
     *
     * @return 0 if an exact match. Higher numbers are worse matches.
     * @see EditDistance
     */
    public int getCost() {
        return this.score;
    }

}