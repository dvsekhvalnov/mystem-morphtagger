# mystem-morphtagger
Implements GATE (https://gate.ac.uk/) plugin to annotate words with russian morphology, based on Mystem parser output.
Homonyms support included.

## Status
The project was developed in early 2006, during my PhD days to support other research in NLP area.
It using quite ancient java setup. Haven't been maintained since 2012.
Originally published at https://code.google.com/archive/p/mystem-morphtagger/  Ported to github in 2020.

It it pretty stable and works just fine on 10-50Gb Corpora (have never tested beyond).

Latest release supports Mystem v2 and GATE v7 on Windows, Linux and OSX.

## Prerequisites
You will need to download awesome Yandex's MyStem parser on your own. Look for version 2 here: https://yandex.ru/dev/mystem/
And don't forget about legal disclaimer that may differ from current project license.

## How it works
It runs Mystem on text input and then run FSM over GATE tokenizer to match mystem output. Produces morphological annotations for each word in a text.
See some screenshots:

<img src="https://github.com/dvsekhvalnov/mystem-morphtagger/blob/main/images/settings.png?raw=true" width="600" alt="Structured json views" />
<img src="https://github.com/dvsekhvalnov/mystem-morphtagger/blob/main/images/annotations.png?raw=true" width="600" alt="Structured json views" />
<img src="https://github.com/dvsekhvalnov/mystem-morphtagger/blob/main/images/homonyms.png?raw=true" width="600" alt="Structured json views" />

## How to install into standalone GATE system
1. Download `mystem` executable for your platform
2. Place executable under:
    * **Windows** : native/win32/mystem.exe
    * **Linux** : native/linux/mystem
    * **OSX** : native/osx/mystem
    * **FreeBSD** : native/freebsd/mystem
3. Unzip distribution, it contains 2 files `ru-morph-tagger.jar` and `creole.xml`
4. For GATE Developer IDE:
    * Manage CREOLE Plugins -> add custom repository
    * select a directory distribution have been unpacked to (where `creole.xml` is located)
    * select checkbox to load morph-tagger plugin
    * Right-click on Processing Resources -> New -> Russian MorphTagger
    * There are 2 configuration options for plugin:
        * `encoding` (default `utf-8`) input documents encoding, will be passed to mystem
        * `nativeFolder` (default current working directory) full path to native folder with **end slash**. Example: `c:\soft\GATE\` (if you have `c:\soft\GATE\native\win32\mystem.exe`)
5. Add `ANNIE Tokeniser` to the pipeline before `MorphTagger`. `MorphTagger` annotates text based on `Token {kind=word}` annotations.
6. Add any processing resources after `MorphTagger` that relies on morphological information.

## How to build
You will need Ant build tool, that you can download here: https://ant.apache.org/

1. `cd ./plugin`
2. `ant clean make.build`

### Build against different GATE version
Replace `./plugin/lib/gate-commons.jar` and `./plugin/lib/gate.jar` with desired version. (Shipped with version 7).

### Running unit tests & embedding
1. Create a project from plugin source using your favorite IDE
2. Add jar dependencies from `./plugin/lib/`
3. Add jar (all) dependencies from `$GATE_HOME/lib`
4. Adjust file pathes inside `gatehome/gate-user.xml`

## Morphological annotation details
**MorphTagger** produces `Morph` annotation for every word with different features. See below for details.

1. `baseForm` - base form of the word or lemma.
2. `pos` - part of speech
    * adjective
    * adverb
    * interjection
    * numeral
    * substantive
    * verb
    * preposition
    * particle
    * conjunction
    * s-pronoun
    * adv-pronoun
    * a-pronoun
    * a-numeral
    composite - when word is part of composite (see MyStem documentation)
3. `case` - noun case
    * nominative
    * genitive
    * dative
    * accusative
    * instrumental
    * ablative
    * partitive
    * locative
    * vocative
4. `multiplicity`
    * singular
    *plural
5. `gender`
    * feminine
    * masculine
    * neuter
6. `animation`
    * inanimate
    * animated
7. `degree`
    * superlative
    * comparative
8. form - adjective form
    * brief
    * full
    * possessive
9. `predicate-noun`
10. `parenthetical`
11. `aspect` - verb aspect
    * imperfect
    * perfect
12. `person`
    * person1
    * person2
    * person3
13. `voice` - verb voice
    * voice
    * active
14. `tense` - verb tense
    * present
    * nopast
    * past
15. `mood` - verb mood
    * indicative
    * imperative
16. `representation` - verb representation
    * participle
    * gerund
    * infinitive
17. `transitivity` - verb transitivity
    * transitive
    * nontransitive
18. `surname` - word is a surname
19. `first-name` - word is a name
20. `last-name` - word is a last name
21. `geo` - word is the name of geographic location
22. `impede`
23. `distorted` - word is in distorted form
24. `common-gender` - word has common masculine and feminine gender
25. `colloquial`
26. `rare`
27. `abbreviation` - word is an abbreviation
28. `archaic`
29. `obscene`




