# mdsc

mdsc (multi-dictionary spell checker) is a library for checking the spelling 
of individual words, using more than one dictionary. It is designed, 
initially, to be a component in an identifier name convention checking system 
that is a product of the doctoral research of the author, Simon Butler.
See http://www.facetus.org.uk/research for more details.

mdsc allows the caller to spell check individual words with a set of 
dictionaries and to receive information about the dictionary in which 
the word is found or that provides alternative spellings.

mdsc is not designed to be used for spell checking documents. If you are 
looking for a library to spell check documents and blocks of text in a 
Java application (GUI or otherwise) then Jazzy 
(http://jazzy.sourceforge.net/) may meet your requirements.

v0.2.0 of mdsc introduces an API to access the wordlists for use in
other applications. 

Releases of mdsc are available from maven central. For gradle users
just add 'uk.org.facetus:mdsc:0.2.0' as a dependency to your build
file. Maven users should add the following to the pom.

```
<dependency>
    <groupId>uk.org.facetus</groupId>
    <artifactId>mdsc</artifactId>
    <version>0.2.0</version>
</dependency>
```

## Origins

The core spell checking functionality of mdsc is based on the Jazzy spell 
checker code base (http://jazzy.sourceforge.net/), in particular the 
com.swabunga.spell.engine package. A list of the developers of Jazzy can 
be found in the file docs/JazzyAuthors.txt

## Licence

mdsc is released under the terms of the GNU Public Licence (GPL) v3 with 
the 'classpath' exception. See the files COPYING and LICENSE for details.

## Requirements
### Java 8
mdsc requires a Java 8 JRE to run. Revising the code to run under Java 7 
is relatively straightforward should it be necessary. However, future 
versions may make greater use of Java 8 features, and be less readily 
modified.

### SLF4J
SLF4J is used for logging. The slf4j-api-1.7.x.jar file should be on the 
classpath along with an appropriate SJF4J jar for the application's 
logging system.
 

## Dictionaries

mdsc supports two types of dictionaries, and is, as far as known, 
independent of natural language, so long as the characters can be encoded 
in UTF-8. The dictionaries must be in the form of a word list (i.e. a 
plain text file with one word per line).

A default set of dictionaries derived from the SCOWL word lists
(http://wordlist.aspell.net/) and Simon Butler's research are provided. 
Temporarily, we include lists of abbreviations derived from Emily Hill's AMAP project
(currently available from http://users.drew.edu/ehill1/AMAP.tar.gz). 
mdsc is also designed so that new or alternative dictionaries can 
be added through the API and used in isolation or groups.

## Usage

The DefaultDictionaryManager is a convenience class that provides the 
default set of dictionaries. The AbbreviationDictionaryManager 
provides a set of abbreviation dictionaries. The class should only 
be used to identify abbreviations, not to try to correct them.

## Logging

mdsc uses SLF4J (http://www.slf4j.org/) for logging. Add the 
appropriate SLF4J jar file to the classpath of your application.

