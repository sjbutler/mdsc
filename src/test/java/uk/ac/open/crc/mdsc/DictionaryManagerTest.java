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

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;


/**
 *
 * 
 */
public class DictionaryManagerTest {

    
    @Test
    public void instantiationTest() {
        DictionaryManager dm = new DictionaryManager();
        assertThat("null DictionaryManager", dm,notNullValue());
	
	DictionarySet ds = dm.dictionarySet();
	
	assertThat("DictionaryManager: Dictionary set is not empty", 
		ds.getDictionaryList(), 
		empty()); // the list is initialised with no members
    }
    
    
}
