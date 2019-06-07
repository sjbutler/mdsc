/*
    mdsc - a multiple dictionary spell checker
    Copyright (C) 2017-2019 Simon Butler 
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
    GERMAN_DE_50K( "de_DE", "/de/de_DE-50k-2018" ),
    SCOWL_EN_CA( "en_CA", "/scowl/en_CA" ), 
    SCOWL_EN_GB( "en_GB", "/scowl/en_GB" ),
    SCOWL_EN_US( "en_US", "/scowl/en_US" ),
    SCOWL_HACKER( "hacker", "/scowl/hacker" ),
    SCOWL_PROPER_NOUNS( "proper", "/scowl/proper-nouns" ),
    SCOWL_RUDE( "rude", "/scowl/rude" ),
    TECHNICAL( "technical", "/technical" );

    private final String tag;
    private final String path;

    private Wordlists( String tag, String path ) {
        this.tag = tag;
        this.path = path;
    }
    
    /**
     * Recovers a tag or 'name' for the list.
     * @return a {@code String} identifying the list
     */
    public String tag() {
        return this.tag;
    }
    
    String path() {
	return this.path;
    }
}
