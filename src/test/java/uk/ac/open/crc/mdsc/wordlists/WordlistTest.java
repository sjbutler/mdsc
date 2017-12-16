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

import java.util.List;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
/**
 *
 * 
 */
public class WordlistTest {

    @Test
    public void simpleLoadTest() {
	Wordlist w = new Wordlist(Wordlists.SCOWL_EN_GB);
	List<String> list = w.list();
	
	assertThat("Null list recovered from simple instantiation", list, notNullValue());
	assertThat("Empty list recovered from simple instantiation", list, not(empty()));
	assertThat("Could not find 'and' in en_GB wordlist", list, hasItem("and"));
	assertThat("Could not find 'zephyr' in en_GB wordlist", list, hasItem("zephyr"));
	assertThat("Could not find 'modelling' in en_GB wordlist", list, hasItem("modelling"));
    }
    
    @Test
    public void specifyNormalisedLoadTest() {
	Wordlist w = new Wordlist(Wordlists.SCOWL_EN_US, true);
	List<String> list = w.list();
	
	assertThat("Null list recovered from normalise specified instantiation", list, notNullValue());
	assertThat("Empty list recovered from normalise specified instantiation", list, not(empty()));
	assertThat("Could not find 'elephant' in en_US normalised wordlist", list, hasItem("elephant"));
	assertThat("Could not find 'xylem' in en_US normalised wordlist", list, hasItem("xylem"));
	assertThat("Could not find 'modeling' in en_US normalised wordlist", list, hasItem("modeling"));
    }

    @Test
    public void specifyNotNormalisedLoadTest() {
	Wordlist w = new Wordlist(Wordlists.SCOWL_EN_CA, false);
	List<String> list = w.list();
	
	assertThat("Null list recovered from simple instantiation", list, notNullValue());
	assertThat("Empty list recovered from simple instantiation", list, not(empty()));
	assertThat("Could not find 'and' in en_GB wordlist", list, hasItem("idiocy"));
	assertThat("Could not find 'zephyr' in en_GB wordlist", list, hasItem("yoghurt"));
    }

    @Test
    public void minimumLengthTest() {
	Wordlist w = new Wordlist(Wordlists.SCOWL_PROPER_NOUNS, false, 5);
	List<String> list = w.list();
	
	assertThat("Null list recovered from simple instantiation", list, notNullValue());
	assertThat("Empty list recovered from simple instantiation", list, not(empty()));
	assertThat("Found 'Wii' in proper nouns with minimum length of 5.", list, not(hasItem("Wii")));
	assertThat("Could not find 'Milano' in proper nouns with minimum length of 5.", list, hasItem("Milano"));
    }
    
}
