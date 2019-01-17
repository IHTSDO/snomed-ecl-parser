# How to generate ANTLR4 grammar

See https://github.com/IHTSDO/snomed-scg-parser/blob/master/parser-generation/README.md to generate ANTLR4 from the ABNF grammar file.

In order to allow non-english language characters, in addition the following lexer rule needs to be manually added (borrowed from the ANTLR4 lexer, https://github.com/antlr/grammars-v4/blob/master/antlr4/LexBasic.g4):

```
LETTER 
   : 'A' .. 'Z'
   | 'a' .. 'z'
   | '\u00C0' .. '\u00D6'
   | '\u00D8' .. '\u00F6'
   | '\u00F8' .. '\u02FF'
   | '\u0370' .. '\u037D'
   | '\u037F' .. '\u1FFF'
   | '\u200C' .. '\u200D'
   | '\u2070' .. '\u218F'
   | '\u2C00' .. '\u2FEF'
   | '\u3001' .. '\uD7FF'
   | '\uF900' .. '\uFDCF'
   | '\uFDF0' .. '\uFFFD'
   ;
```

Further, LETTER should replace utf8_tail in the generated file.
