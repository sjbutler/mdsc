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

/**
 * <p>API for spell-checking and convenience classes. Dictionaries can be 
 * built using {@code DictionaryManager} and your own word lists or those
 * that are available in the jar &mdash; see the
 * wordlists package and the documentation for more information. 
 * Alternatively there are 
 * convenience classes that provide dictionaries constructed with 
 * specific word lists. </p>
 * 
 * <p>The classes are organised so that a {@linkplain DictionaryManager} 
 * instantiates and collates collections of {@linkplain Dictionary} objects
 * that represent an individual word list. To spell check, obtain a 
 * {@linkplain DictionarySet} from the {@code DictionaryManager} and provide 
 * a single word to the {@linkplain DictionarySet#spellCheck(java.lang.String)}
 * method. A list of {@linkplain Result}s are returned one for each dictionary 
 * in the {@code DictionarySet} with an indication of correctness and suggested 
 * alternatives if incorrect.</p>
 */
package uk.ac.open.crc.mdsc;
