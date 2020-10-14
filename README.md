# mystem-morphtagger
Implements GATE (https://gate.ac.uk/) plugin to annotate words with russian morphology, based on Mystem parser output.

## Status
The project is from my early PhD days and using quite old java setup. Haven't been maintained since 2012.
If something not working feel free to drop me a message.

Latest release supports Mystem v2 and GATE v7 on Windows, Linux and OSX.

## Prerequisites
You will need to download awesome Yandex's MyStem parser on your own. Look for version 2 here: https://yandex.ru/dev/mystem/
And don't forget about legal disclaimer that may differ from current project license.

## What it do
It runs mystem on text input and then run FSM on mystem's output to produce GATE morphological annotations for each word.
See some screenshots:
<img src="https://github.com/dvsekhvalnov/mystem-morphtagger/blob/main/images/settings.png?raw=true" width="600" alt="Structured json views" />
<img src="https://github.com/dvsekhvalnov/mystem-morphtagger/blob/main/images/annotations.png?raw=true" width="600" alt="Structured json views" />
<img src="https://github.com/dvsekhvalnov/mystem-morphtagger/blob/main/images/homonyms.png?raw=true" width="600" alt="Structured json views" />

## How to build
You will need Ant build tool, that you can download here: https://ant.apache.org/

1. `cd ./plugin`
2. `ant make.build`





