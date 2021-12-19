## Rocker exercise
Implementation of a Rocker exercise as a part of the interview process.

### Running
From the command line, just simply run `sbt "run <full path to a dir filled with text files>"` in the project dir. Command would look like: `sbt "run /Users/mmazurkiewicz/workspace/rocker_exercise/text_files"`.

### Assumptions
1. We are only indexing *.txt files.
2. Indexing works on ENG alphabet
3. There is no limitation about word length, "a, an, the" are also counted.
4. Two words are matching regardless of casing.

### Extensions of implementation
1. Hyphened words and words with apostrophes are covered such that they can give false positive results. By that I mean if you want to search for "father-in-law" and in text files there are words "father", "in", "law" it will be counted. Regex which I'm using would need some tweaking. But regex, as we all know, is a fine art of which I do not held neither master nor bachelor degree ;)
2. Support for alphabets like e.g. cyrillic or greek and country specific lettering.
