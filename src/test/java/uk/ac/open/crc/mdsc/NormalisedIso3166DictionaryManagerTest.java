/*
    mdsc - multiple dictionary spell checker
    Copyright (C) 2017 Simon Butler

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

package uk.ac.open.crc.mdsc;

import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;
import org.junit.BeforeClass;

/**
 *
 * 
 */
public class NormalisedIso3166DictionaryManagerTest {

    private static NormalisedIso3166DictionaryManager dm;
    
    @BeforeClass
    public static void setUp() {
	dm = new NormalisedIso3166DictionaryManager();
    }
    
    @Test
    public void instantiationTest() {
	assertThat("NormalisedIso3166DictionaryManager initialised "
			+ "with empty list of dictionaries.",
		dm.dictionarySet().getDictionaryList(),
		not(empty()));
    }
    
    @Test
    public void simpleTwoLetterLcTest() {
	List<Result> responses = dm.dictionarySet().spellCheck( "se" );
	List<Boolean> booleanResponses  = new ArrayList<>();
	responses.forEach( r -> booleanResponses.add(r.isCorrect()));
	assertThat( responses.size(), is(2) );
	assertThat( booleanResponses.size(), is(2) );
	
	assertThat("unable to find 'se' in normalised ISO3166 dictionary",
		booleanResponses,
		hasItem(true));
    }
    
    @Test
    public void simpleThreeLetterLcTest() {
	List<Result> responses = dm.dictionarySet().spellCheck( "swe" );
	List<Boolean> booleanResponses  = new ArrayList<>();
	responses.forEach( r -> booleanResponses.add(r.isCorrect()));
	assertThat( responses.size(), is(2) );
	assertThat( booleanResponses.size(), is(2) );
	
	assertThat("unable to find 'swe' in normalised ISO3166 dictionary",
		booleanResponses,
		hasItem(true));
    }
    
    @Test
    public void simpleTwoLetterUcTest() {
	List<Result> responses = dm.dictionarySet().spellCheck( "SE" );
	List<Boolean> booleanResponses  = new ArrayList<>();
	responses.forEach( r -> booleanResponses.add(r.isCorrect()));
	assertThat( responses.size(), is(2) );
	assertThat( booleanResponses.size(), is(2) );
	
	assertThat("unable to find 'SE' in normalised ISO3166 dictionary",
		booleanResponses,
		hasItem(true));
    }
    
    @Test
    public void simpleThreeLetterUcTest() {
	List<Result> responses = dm.dictionarySet().spellCheck( "SWE" );
	List<Boolean> booleanResponses  = new ArrayList<>();
	responses.forEach( r -> booleanResponses.add(r.isCorrect()));
	assertThat( responses.size(), is(2) );
	assertThat( booleanResponses.size(), is(2) );
	
	assertThat("unable to find 'SWE' in normalised ISO3166 dictionary",
		booleanResponses,
		hasItem(true));
    }
}
