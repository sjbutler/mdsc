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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A Generic implementation of a transformator takes an
 * <a href="http://aspell.net/man-html/Phonetic-Code.html">
 * aspell phonetics file</a> and constructs some sort of transformation table
 * using the inner class TransformationRule.
 * <p>
 * Each transformation rule represent a line in the phonetic file. One line
 * contains two groups of characters separated by white space(s). The first
 * group is the <em>match expression</em>. The <em>match expression</em>
 * describes letters to associate with a syllable. The second group is the
 * <em>replacement expression</em> giving the phonetic equivalent of the
 * <em>match expression</em>.
 *
 * @see ASpellSpellingDictionary ASpellSpellingDictionary for information on
 * getting phonetic files for aspell.
 *
 * @author Robert Gustavsson (robert@lindesign.se)
 */
public class GenericTransformator implements Transformator {

    /**
     * This replace list is used if no phonetic file is supplied or it doesn't
     * contain the alphabet.
     */
    private static final char[] defaultEnglishAlphabet = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    /**
     * The alphabet start marker.
     *
     * @see GenericTransformator#KEYWORD_ALPHABET
     */
    public static final char ALPHABET_START = '[';
    /**
     * The alphabet end marker.
     *
     * @see GenericTransformator#KEYWORD_ALPHABET
     */
    public static final char ALPHABET_END = ']';
    /**
     * Phonetic file keyword indicating that a different alphabet is used for
     * this language. The keyword must be followed an
     * {@link GenericTransformator#ALPHABET_START ALPHABET_START} marker, a list
     * of characters defining the alphabet and a
     * {@link GenericTransformator#ALPHABET_END ALPHABET_END} marker.
     */
    public static final String KEYWORD_ALPHABET = "alphabet";
    /**
     * Phonetic file lines starting with the keywords are skipped. The key words
     * are: version, followup, collapse_result. Comments, starting with '#', are
     * also skipped to the end of line.
     */
    public static final String[] IGNORED_KEYWORDS = {"version", "followup", "collapse_result"};

    /**
     * Start a group of characters which can be appended to the match expression
     * of the phonetic file.
     */
    public static final char START_MULTI = '(';
    /**
     * End a group of characters which can be appended to the match expression
     * of the phonetic file.
     */
    public static final char END_MULTI = ')';
    /**
     * During phonetic transformation of a word each numeric character is
     * replaced by this DIGIT_CODE.
     */
    public static final String DIGIT_CODE = "0";
    /**
     * Phonetic file character code indicating that the replace expression is
     * empty.
     */
    public static final String EMPTY_REPLACEMENT_EXPRESSION = "_";

    private TransformationRule[] ruleArray = null;
    private char[] alphabetString = defaultEnglishAlphabet;

    /**
     * Construct a transformation table from the phonetic file
     *
     * @param phonetic the phonetic file as specified in aspell
     * @throws java.io.IOException indicates a problem while reading the
     * phonetic file
     */
    public GenericTransformator( File phonetic ) throws IOException {
        buildRules( new BufferedReader( new FileReader( phonetic ) ) );
        this.alphabetString = washAlphabetIntoReplaceList( getReplaceList() );

    }

    /**
     * Construct a transformation table from the phonetic file
     *
     * @param phonetic the phonetic file as specified in aspell
     * @param encoding the character set required
     * @throws java.io.IOException indicates a problem while reading the
     * phonetic file
     */
    public GenericTransformator( File phonetic, String encoding ) throws IOException {
        buildRules( new BufferedReader( new InputStreamReader( new FileInputStream( phonetic ), encoding ) ) );
        this.alphabetString = washAlphabetIntoReplaceList( getReplaceList() );
    }

    /**
     * Construct a transformation table from the phonetic file
     *
     * @param phonetic the phonetic file as specified in aspell. The file is
     * supplied as a reader.
     * @throws java.io.IOException indicates a problem while reading the
     * phonetic information
     */
    public GenericTransformator( Reader phonetic ) throws IOException {
        buildRules( new BufferedReader( phonetic ) );
        this.alphabetString = washAlphabetIntoReplaceList( getReplaceList() );
    }

    /**
     * Goes through an alphabet and makes sure that only one of those letters
     * that are coded equally will be in the replace list. In other words, it
     * removes any letters in the alphabet that are redundant phonetically.
     *
     * This is done to improve speed in the getSuggestion method.
     *
     * @param alphabet The complete alphabet to wash.
     * @return The washed alphabet to be used as replace list.
     */
    private char[] washAlphabetIntoReplaceList( char[] alphabet ) {

        HashMap<String, Character> letters = new HashMap<>( alphabet.length );

        for ( int i = 0; i < alphabet.length; i++ ) {
            String code = transform( String.valueOf( alphabet[i] ) );
            if ( !letters.containsKey( code ) ) {
                letters.put( code, alphabet[i] );
            }
        }

        Character[] temporaryCharacters = 
                letters.values().toArray( new Character[0] );
        char[] washedArray = new char[temporaryCharacters.length];
        for ( int i = 0; i < temporaryCharacters.length; i++ ) {
            washedArray[i] = temporaryCharacters[i];
        }

        return washedArray;
    }

    /**
     * Takes out all single character replacements and put them in a char array.
     * This array can later be used for adding or changing letters in
     * getSuggestion().
     *
     * @return char[] An array of chars with replacements characters
     */
    public char[] getCodeReplaceList() {

        ArrayList<String> replacementExpressions = new ArrayList<>();

        if ( this.ruleArray == null ) {
            return null;
        }
        for ( TransformationRule rule : this.ruleArray ) {
            if ( rule.getReplacementExpression().length() == 1 ) {
                replacementExpressions.add( rule.getReplacementExpression() );
            }
        }

        char[] replacements = new char[replacementExpressions.size()];
        for ( int i = 0; i < replacementExpressions.size(); i++ ) {
            replacements[i] = replacementExpressions.get( i ).charAt( 0 );
        }
        return replacements;
    }

    /**
     * Builds up an char array with the chars in the alphabet of the language as
     * it was read from the alphabet tag in the phonetic file.
     *
     * @return char[] An array of chars representing the alphabet or null if no
     * alphabet was available.
     */
    @Override
    public final char[] getReplaceList() {
        return this.alphabetString;
    }

    /**
     * Builds the phonetic code of the word.
     *
     * @param word the word to transform
     * @return the phonetic transformation of the word
     */
    @Override
    public String transform( String word ) {

        if ( this.ruleArray == null ) {
            return null;
        }

        StringBuilder outputString = new StringBuilder( word.toUpperCase() );
        int stringLength = outputString.length();
        int startPosition = 0;

        while ( startPosition < stringLength ) {

            int add = 1;
            if ( Character.isDigit( outputString.charAt( startPosition ) ) ) {
                replace( outputString, startPosition, startPosition + DIGIT_CODE.length(), DIGIT_CODE );
                startPosition += add;
                continue;
            }

            for ( TransformationRule rule : ruleArray ) {
                if ( rule.startsWithExpression() && startPosition > 0 ) {
                    continue;
                }
                if ( startPosition + rule.lengthOfMatch() > stringLength ) {
                    continue;
                }
                if ( rule.isMatching( outputString, startPosition ) ) {
                    String replacementExpression = rule.getReplacementExpression();

                    add = replacementExpression.length();
                    replace( outputString, startPosition, startPosition + rule.getTakeOut(), replacementExpression );
                    stringLength -= rule.getTakeOut();
                    stringLength += add;
                    break;
                }
            }
            startPosition += add;
        }

        return outputString.toString();
    }

    // Used to build up the transformation table.
    private void buildRules( BufferedReader in ) throws IOException {
        String line;
        ArrayList<TransformationRule> ruleList = new ArrayList<>();
        while ( (line = in.readLine()) != null ) {
            buildRule( stripComment( line ), ruleList );
        }

        this.ruleArray = ruleList.toArray( new TransformationRule[0] );
    }

    // SB: serious refactoring when time is available.
    // Here is where the real work of reading the phonetics file is done.
    private void buildRule( String inputString, ArrayList<TransformationRule> ruleList ) {
        if ( inputString.isEmpty() || startsWithIgnoredKeyWord( inputString ) ) {
            return;
        }

        // A different alphabet is used for this language, will be read into
        // the alphabetString variable.
        if ( inputString.startsWith( KEYWORD_ALPHABET ) ) {
            int start = inputString.indexOf( ALPHABET_START );
            int end = inputString.lastIndexOf( ALPHABET_END );
            if ( end != -1 && start != -1 ) {
                this.alphabetString = inputString.substring( ++start, end ).toCharArray();
            }
            return;
        }

        // inputString contains two groups of characters separated by white space(s).
        // The first group is the "match expression". The second group is the 
        // "replacement expression" giving the phonetic equivalent of the 
        // "match expression".
        // SB: a job for ANTLR
        TransformationRule rule;
        StringBuilder matchExpression = new StringBuilder();
        StringBuilder replacementExpression = new StringBuilder();
        boolean start = false;
        boolean end = false;
        int takeOutPart = 0;
        int matchLength = 0;
        boolean match = true;
        boolean inMulti = false;
        // SB: is there a better solution than repeated calls to .charAt(i)?
        for ( int i = 0; i < inputString.length(); i++ ) {
            if ( Character.isWhitespace( inputString.charAt( i ) ) ) {
                match = false;
            }
            else {
                if ( match ) {
                    if ( !isReservedChar( inputString.charAt( i ) ) ) {
                        matchExpression.append( inputString.charAt( i ) );
                        if ( !inMulti ) {
                            takeOutPart++;
                            matchLength++;
                        }
                        if ( inputString.charAt( i ) == START_MULTI || inputString.charAt( i ) == END_MULTI ) {
                            inMulti = !inMulti;
                        }
                    }
                    if ( inputString.charAt( i ) == '-' ) {
                        takeOutPart--;
                    }
                    if ( inputString.charAt( i ) == '^' ) {
                        start = true;
                    }
                    if ( inputString.charAt( i ) == '$' ) {
                        end = true;
                    }
                }
                else {
                    replacementExpression.append( inputString.charAt( i ) );
                }
            }
        }
        if ( replacementExpression.toString().equals( EMPTY_REPLACEMENT_EXPRESSION ) ) {
            replacementExpression = new StringBuilder();
            //System.out.println("Changing _ to \"\" for "+matchExp.toString());
        }
        rule = new TransformationRule( matchExpression.toString(), replacementExpression.toString(), takeOutPart, matchLength, start, end );
        //System.out.println(rule.toString());
        ruleList.add( rule );
    }

    private boolean startsWithIgnoredKeyWord( String string ) {
        for ( String keyword : IGNORED_KEYWORDS ) {
            if ( string.startsWith( keyword ) ) {
                return true;
            }
        }

        return false;
    }

    // Chars with special meaning to aspell. Not every one is implemented here.
    // SB: TODO determine the full set of reserved characters from aspell
    private boolean isReservedChar( char ch ) {
        return ( ch == '<'
                || ch == '>'
                || ch == '^'
                || ch == '$'
                || ch == '-'
                || Character.isDigit( ch ) );
    }

    /**
     * Removes end of line comments.
     *
     * @param line a {@code String}
     * @return a {@code String} with any comment beginning with '#' removed.
     */
    private String stripComment( String line ) {
        int position = line.indexOf( '#' );
        if ( position != -1 ) {
            line = line.substring( 0, position );
        }
        return line.trim();
    }

    // SB: moved here from a utility class
    // SB: review and refactor
    private StringBuilder replace( 
            StringBuilder buffer, 
            int start, 
            int end, 
            String text ) {
        int textLength = text.length();
        char[] charArray = new char[buffer.length() + textLength - (end - start)];
        buffer.getChars( 0, start, charArray, 0 );
        text.getChars( 0, textLength, charArray, start );
        buffer.getChars( end, buffer.length(), charArray, start + textLength );
        buffer.setLength( 0 );
        buffer.append( charArray );
        return buffer;
    }

    // Inner Classes
  /*
     * Holds the match string and the replace string and all the rule attributes.
     * Is responsible for indicating matches.
     */
    private class TransformationRule {

        private final String replacement;
        private final char[] match;
        // takeOut=number of chars to replace;
        // matchLength=length of matching string counting multies as one.
        private final int takeOut;
        private final int matchLength;
        private final boolean start;
        private final boolean end;

        public TransformationRule( 
                String match, 
                String replacement, 
                int takeOut, 
                int matchLength, 
                boolean start, 
                boolean end ) {
            this.match = match.toCharArray();
            this.replacement = replacement;
            this.takeOut = takeOut;
            this.matchLength = matchLength;
            this.start = start;
            this.end = end;
        }

        /**
         * Returns true if word from position and forward matches the match string.
         * Precondition: {@code wordPos+matchLength<word.length()}
         */
        public boolean isMatching( StringBuilder word, int wordPosition ) {
            boolean matching = true;
            boolean inMulti = false;
            boolean multiMatch = false;
            char matchCharacter;

            for ( int matchPosition = 0; 
                    matchPosition < this.match.length; 
                    matchPosition++ ) {
                matchCharacter = this.match[matchPosition];
                if ( matchCharacter == START_MULTI || matchCharacter == END_MULTI ) {
                    inMulti = !inMulti;
                    if ( !inMulti ) {
                        matching = matching & multiMatch;
                    }
                    else {
                        multiMatch = false;
                    }
                }
                else {
                    if ( matchCharacter != word.charAt( wordPosition ) ) {
                        if ( inMulti ) {
                            multiMatch = multiMatch | false;
                        }
                        else {
                            matching = false;
                        }
                    }
                    else {
                        if ( inMulti ) {
                            multiMatch = multiMatch | true;
                        }
                        else {
                            matching = true;
                        }
                    }
                    if ( !inMulti ) {
                        wordPosition++;
                    }
                    if ( !matching ) {
                        break;
                    }
                }
            }
            if ( this.end && wordPosition != word.length() ) {
                matching = false;
            }
            return matching;
        }

        public String getReplacementExpression() {
            return this.replacement;
        }

        public int getTakeOut() {
            return this.takeOut;
        }

        public boolean startsWithExpression() {
            return this.start;
        }

        public int lengthOfMatch() {
            return this.matchLength;
        }

        // Just for debugging purposes.
        @Override
        public String toString() {
            return "Match:" + String.valueOf( this.match ) 
                    + " Replace:" + this.replacement
                    + " TakeOut:" + this.takeOut + " MatchLength:" + this.matchLength
                    + " Start:" + this.start + " End:" + this.end;
        }

    }
}
