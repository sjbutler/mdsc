/*
    mdsc - a multiple dictionary spell checker
    Copyright (C) 2017 Simon Butler 
    Full text of license can be found in LICENSE.txt

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License as published by
    the Free Software Foundation with the 'classpath' exception, 
    either version 3 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package uk.ac.open.crc.mdsc.wordlists;

/**
 * An enumeration of available word lists.
 */
public enum Wordlists {
    SCOWL_EN_CA( "/scowl/en_CA" ), 
    SCOWL_EN_GB( "/scowl/en_GB" ),
    SCOWL_EN_US( "/scowl/en_US" ),
    SCOWL_HACKER( "/scowl/hacker" ),
    SCOWL_PROPER_NOUNS( "/scowl/proper-nouns" ),
    SCOWL_RUDE( "/scowl/rude" ),
    TECHNICAL( "/technical" );

    private final String path;

    private Wordlists( String path ) {
        this.path = path;
    }
    
    String path() {
	return this.path;
    }
}
