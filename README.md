# mdsc

mdsc (multi-dictionary spell checker) is a library for checking the spelling 
of individual words, using more than one dictionary. It is designed, 
initially, to be a component in an identifier name convention checking system 
that is a product of the doctoral research of the author, Simon Butler.
See http://www.floss.nu/research for more details.

mdsc allows the caller to spell check individual words with a set of 
dictionaries and to receive information about the dictionary in which 
the word is found or that provides alternative spellings.

mdsc is not designed to be used for spell checking documents. If you are 
looking for a library to spell check documents and blocks of text in a 
Java application (GUI or otherwise) then Jazzy 
(http://jazzy.sourceforge.net/) may meet your requirements.

v0.2.1 of mdsc introduced an API to access the wordlists for use in
other applications. 

Releases of mdsc are available from maven central. For gradle users
just add 'uk.org.facetus:mdsc:0.2.4' as a dependency to your build
file. Maven users should add the following to the pom.

```
<dependency>
    <groupId>uk.org.facetus</groupId>
    <artifactId>mdsc</artifactId>
    <version>0.2.4</version>
</dependency>
```

## Origins and History

The core spell checking functionality of mdsc is based on the Jazzy spell 
checker code base (http://jazzy.sourceforge.net/), in particular the 
com.swabunga.spell.engine package. A list of the developers of Jazzy can 
be found in the file docs/JazzyAuthors.txt

mdsc was initially developed during Simon Butler's doctoral research and 
consequently The Open University owns the copyright of work on mdsc prior to 
and including the commit https://github.com/sjbutler/mdsc/commit/2d4320145488219ebf2015311c00a641d5a12647

The copyright of revisions made in the commit https://github.com/sjbutler/mdsc/commit/2ff0d6c862af7ea977a9101841acf53f1d0f5864
and since belongs to Simon Butler.

## Licence

mdsc is released under the terms of the GNU Public Licence (GPL) v3 with 
the 'classpath' exception. See the files COPYING and LICENSE for details.

## Requirements
### Java 8
mdsc requires a Java 8 JRE to run. It has not been tested with Java 9 or greater. 

### SLF4J
SLF4J is used for logging. The slf4j-api-1.7.x.jar file should be on the 
classpath along with an appropriate SJF4J jar for the application's 
logging system.
 

## Dictionaries

mdsc supports two types of dictionaries, and is, as far as is known, 
independent of natural language, so long as the characters can be encoded 
in UTF-8. The dictionaries must be in the form of a word list (i.e. a 
plain text file with one word per line).

A default set of dictionaries derived from the SCOWL word lists
(http://wordlist.aspell.net/) and Simon Butler's research are provided. 
Temporarily, we include lists of abbreviations derived from Emily Hill's AMAP project
(currently available from http://users.drew.edu/ehill1/AMAP.tar.gz). 
mdsc is also designed so that new or alternative dictionaries can 
be added through the API and used in isolation or groups.

Since v0.2.4 a German word list has also been included. The JavaDocs explain 
how to use the dictionary.

## Usage

The DefaultDictionaryManager is a convenience class that provides the 
default set of dictionaries. The AbbreviationDictionaryManager 
provides a set of abbreviation dictionaries. The class should only 
be used to identify abbreviations, not to try to correct them.

## Logging

mdsc uses SLF4J (http://www.slf4j.org/) for logging. 
Logging is minimal and intended to report initialisation errors. 

