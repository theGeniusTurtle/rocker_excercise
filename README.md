## Rocker exercise
Implementation of a Rocker exercise as a part of the interview process.

### Running
From the command line, just simply run `sbt "run <full path to a dir filled with text files>"` in the project dir. Command would  like: `sbt "run /Users/mmazurkiewicz/workspace/rocker_exercise/text_files"`.

### Assumptions
1. We are only indexing *.txt files.
2. Indexing works on ENG alphabet
3. There is no limitation about word length, "a, an, the" are also counted.
4. Two words are matching regardless of casing.
