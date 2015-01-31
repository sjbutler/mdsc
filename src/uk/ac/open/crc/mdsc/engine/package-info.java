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
 * Spelling dictionary classes adapted from Jazzy. Only the 
 * {@linkplain HashedSpellingDictionary} class is used, along with 
 * the {@linkplain ASpellSpellingDictionary} class and the transformator
 * hierarchy. The other dictionary classes have not been tested and
 * may behave unpredictably, particularly those that access  
 * persistent storage. 
 */
package uk.ac.open.crc.mdsc.engine;
