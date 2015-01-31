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

/**
 * This class is based on Levenshtein Distance algorithms, and it calculates how
 * similar two words are. If the words are identical, then the distance is 0.
 * The more that the words have in common, the lower the distance value. The
 * distance value is based on how many operations it takes to get from one word
 * to the other. Possible operations are swapping characters, adding a
 * character, deleting a character, and substituting a character. The resulting
 * distance is the sum of these operations weighted by their cost, which can be
 * set in the Configuration object. When there are multiple ways to convert one
 * word into the other, the lowest cost distance is returned.
 * <br/>
 * Another way to think about this: what are the cheapest operations that would
 * have to be done on the "original" word to end up with the "similar" word?
 * Each operation has a cost, and these are added up to get the distance.
 * <br/>
 *
 * @see uk.ac.open.crc.mdsc.engine.Configuration#COST_REMOVE_CHAR
 * @see uk.ac.open.crc.mdsc.engine.Configuration#COST_INSERT_CHAR
 * @see uk.ac.open.crc.mdsc.engine.Configuration#COST_SUBST_CHARS
 * @see uk.ac.open.crc.mdsc.engine.Configuration#COST_SWAP_CHARS
 *
 */
class EditDistance {

    /**
     * Fetches the spell engine CONFIGURATION properties.
     */
    public static final Configuration CONFIGURATION = 
            Configuration.getConfiguration();

    /**
     * get the weights for each possible operation
     */
    static final int COST_OF_DELETING_SOURCE_CHARACTER = 
            CONFIGURATION.getInteger( Configuration.COST_REMOVE_CHAR );
    static final int COST_OF_INSERTING_SOURCE_CHARACTER = 
            CONFIGURATION.getInteger( Configuration.COST_INSERT_CHAR );
    static final int COST_OF_SUBSTITUTING_LETTERS = 
            CONFIGURATION.getInteger( Configuration.COST_SUBST_CHARS );
    static final int COST_OF_SWAPPING_LETTERS = 
            CONFIGURATION.getInteger( Configuration.COST_SWAP_CHARS );
    static final int COST_OF_CHANGING_CASE = 
            CONFIGURATION.getInteger( Configuration.COST_CHANGE_CASE );

    /**
     * Evaluates the distance between two words.
     *
     * @param word One word to evaluates
     * @param similar The other word to evaluates
     * @return a number representing how easy or complex it is to transform on
     * word into a similar one.
     */
    public static final int getDistance( String word, String similar ) {
        return getDistance( word, similar, null );
    }

    // SB: this method is a candidate for refactoring given its length
    /**
     * Evaluates the distance between two words.
     *
     * @param word One word to evaluate
     * @param similar The other word to evaluates
     * @return a number representing how easy or complex it is to transform on
     * word into a similar one.
     */
    public static final int getDistance( String word, String similar, int[][] matrix ) {
        /* JMH Again, there is no need to have a global class matrix variable
         *  in this class. I have removed it and made the getDistance static final
         * DMV: I refactored this method to make it more efficient, more readable, and simpler.
         * I also fixed a bug with how the distance was being calculated. You could get wrong
         * distances if you compared ("abc" to "ab") depending on what you had setup your
         * COST_REMOVE_CHAR and EDIT_INSERTION_COST values to - that is now fixed.
         * WRS: I added a distance for case comparison, so a misspelling of "i" would be closer to "I" than
         * to "a".
         * SB: revised identifier names, moved some declarations to minimise scope
         */

        boolean isSwap;

        int aSize = word.length() + 1;
        int bSize = similar.length() + 1;

        //Only allocate new memory if we need a bigger matrix. 
        if ( matrix == null || matrix.length < aSize || matrix[0].length < bSize ) {
            matrix = new int[aSize][bSize];
        }

        matrix[0][0] = 0;

        for ( int i = 1; i != aSize; ++i ) {
            matrix[i][0] = matrix[i - 1][0] + COST_OF_INSERTING_SOURCE_CHARACTER; //initialize the first column
        }
        for ( int j = 1; j != bSize; ++j ) {
            matrix[0][j] = matrix[0][j - 1] + COST_OF_DELETING_SOURCE_CHARACTER; //initalize the first row
        }
        for ( int i = 1; i != aSize; ++i ) {
            char sourceChar = word.charAt( i - 1 );
            for ( int j = 1; j != bSize; ++j ) {

                char otherChar = similar.charAt( j - 1 );
                if ( sourceChar == otherChar ) {
                    matrix[i][j] = matrix[i - 1][j - 1]; //no change required, so just carry the current cost up
                    continue;
                }

                int costOfSubstitution = COST_OF_SUBSTITUTING_LETTERS + matrix[i - 1][j - 1];

                //if needed, add up the cost of doing a swap
                int costOfSwap = Integer.MAX_VALUE;

                isSwap = (i != 1) && (j != 1) && sourceChar == similar.charAt( j - 2 ) && word.charAt( i - 2 ) == otherChar;
                if ( isSwap ) {
                    costOfSwap = COST_OF_SWAPPING_LETTERS + matrix[i - 2][j - 2];
                }

                int costOfDeletion = COST_OF_DELETING_SOURCE_CHARACTER + matrix[i][j - 1];
                int costOfInsertion = COST_OF_INSERTING_SOURCE_CHARACTER + matrix[i - 1][j];

                int costOfCaseChange = Integer.MAX_VALUE;

                if ( equalIgnoreCase( sourceChar, otherChar ) ) {
                    costOfCaseChange = COST_OF_CHANGING_CASE + matrix[i - 1][j - 1];
                }

                matrix[i][j] = minimum( 
                        costOfSubstitution, 
                        costOfSwap, 
                        costOfDeletion, 
                        costOfInsertion, 
                        costOfCaseChange );
            }
        }

        return matrix[aSize - 1][bSize - 1];
    }

    
    /**
     * checks to see if the two characters are equal ignoring case.
     *
     * @param char1
     * @param char2
     * @return boolean
     */
    private static boolean equalIgnoreCase( char char1, char char2 ) {
        if ( char1 == char2 ) {
            return true;
        }
        else {
            return (Character.toLowerCase( char1 ) == Character.toLowerCase( char2 ));
        }
    }

    /**
     * For debugging, this creates a string that represents the matrix. To read
     * the matrix, look at any square. That is the cost to get from the partial
     * letters along the top to the partial letters along the side.
     *
     * @param source - the source string that the matrix columns are based on
     * @param destination - the dest string that the matrix rows are based on
     * @param matrix - a two dimensional array of costs (distances)
     * @return String
     */
    static private String dumpMatrix( String source, String destination, int matrix[][] ) {
        StringBuilder matrixAsText = new StringBuilder( "" );

        int columns = matrix.length - 1;
        int rows = matrix[0].length - 1;

        for ( int i = 0; i < columns + 1; i++ ) {
            for ( int j = 0; j < rows + 1; j++ ) {
                if ( i == 0 && j == 0 ) {
                    matrixAsText.append( "\n " );
                    continue;

                }
                if ( i == 0 ) {
                    matrixAsText.append( "|   " );
                    matrixAsText.append( destination.charAt( j - 1 ) );
                    continue;
                }
                if ( j == 0 ) {
                    matrixAsText.append( source.charAt( i - 1 ) );
                    continue;
                }
                String num = Integer.toString( matrix[i - 1][j - 1] );
                int padding = 4 - num.length();
                matrixAsText.append( "|" );
                for ( int k = 0; k < padding; k++ ) {
                    matrixAsText.append( ' ' );
                }
                matrixAsText.append( num );
            }
            matrixAsText.append( '\n' );
        }
        return matrixAsText.toString();

    }

    static private int minimum( int a, int b, int c, int d, int e ) {
        int minimum = a;
        if ( b < minimum ) {
            minimum = b;
        }
        if ( c < minimum ) {
            minimum = c;
        }
        if ( d < minimum ) {
            minimum = d;
        }
        if ( e < minimum ) {
            minimum = e;
        }

        return minimum;
    }

    
    // this will replace the above
    // need to spot errors and pitfalls such as the empty array passed in.
    private static int min( int... a ) {
        int minimum = Integer.MAX_VALUE;
        
        for ( int value : a ) {
            if ( value < minimum ) {
                minimum = value;
            }
        }
        
        return minimum;
    }

    // another alternative
    private static int min2( int... a ) {
        int minimum = a[0];
        
        for ( int i = 1; i < a.length; i++ ) {
            if ( a[i] < minimum ) {
                minimum = a[i];
            }
        }
        
        return minimum;
    }
}
