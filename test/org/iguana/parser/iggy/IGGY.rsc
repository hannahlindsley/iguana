module iggy::IGGY

keyword Keywords = "regex" | "var" | "left" | "right" | "non-assoc" | "align" | "offside" | "ignore" | "while";

token CommentChar = [/] | [*]* ![/ *];
                  
token Comment = "/*" CommentChar* [*]+  "/" | "//" ![\r \n]* [\r\n];

token WhiteSpaces = [\ \t \f \r \n]+;

token Letter = [A-Za-z$_];
token LetterOrDigit = [A-Za-z$_0-9];
token LetterOrDigits = Letter (Letter | LetterOrDigit)*;

token Number = [0] | [1-9] [0-9]*;

token Character = "\\" [\' \" \\ t f r n] | ![\' \" \\];
token RangeChar = "\\" [\\ \[ \] \- t f r n \ ] | ![\\ \[ \] \- \ ];

token Char = "\'" Character* "\'";
token String = "\"" Character* "\"";

lexical WhiteSpaceOrComment = WhiteSpaces | Comment;              
lexical Layout = WhiteSpaceOrComment* !>> [\ \t \f \r \n] !>> "/*" !>> "//";

lexical Identifier = ident: [$A-Z_a-z] !<< LetterOrDigits \ Keywords;
lexical NontName = Identifier;
lexical VarName = Identifier;
lexical Label = Identifier;

lexical Tag = "@NoLayout" | "@Layout";

token Associativity = "left" | "right" | "non-assoc";

syntax LAttribute = lattr: "\<" Label "\>";
syntax AAttribute = aattr: Associativity;

syntax Attribute = LAttribute | AAttribute;

syntax Definition = Global* Rule+;

syntax Global = var: "var" VarName@0 "=" Expression;

syntax Rule = @scope="VarName" Syntax: Tag? NontName@0 Parameters? "::=" Body
            | regex: "regex" RegexRule
            | regex: "regex" "{" RegexRule+ "}"
            ;

syntax RegexRule = regex: NontName@0 "::=" RegexBody;
            
syntax Body = {Alternates "\>"}*;

syntax Alternates = seq: {Alternate "|"}+;
            
syntax Alternate = seq: Sequence 
                 | seq: "(" Sequence ("|" Sequence)+ ")" AAttribute
                 ;            
            
syntax RegexBody = {Regexs "|"}*;
                
syntax Sequence = seq: Symbol Symbol+ ReturnExpression? Attribute*
                | seq: Symbol ReturnExpression? LAttribute?
                ;
                
syntax Parameters = "(" {VarName@0 ","}* ")"; 

syntax Symbol =                  call        : Symbol Arguments
              >                  \offside    : "offside" Symbol
              >                  star        : Symbol "*"
              |                  plus        : Symbol "+"
              |                  option      : Symbol "?"
              | @scope="VarName" sequence    : "(" Symbol Symbol+ ")"
              | @scope="VarName" alternation : "(" Symbols ("|" Symbols)+ ")"
              >                  \align      : "align"   Symbol
              |                  \ignore     : "ignore"  Symbol
              |                  conditional : Expression "?" Symbol ":" Symbol
              |                  \while      : "while" "(" Expression ")" Symbol 
              
              |                  variable    : VarName@0 "=" Symbol
              |                  labeled     : VarName@0 ":" Symbol
              
              |                  constraints : "[" {Expression ","}+ "]"
              |                  bindings    : "{" {Binding ","}+ "}"
              
              |                  precede     : Regex "\<\<" Symbol
              |                  notPrecede  : Regex "!\<\<" Symbol
              >                  follow      : Symbol "\>\>" Regex
              |                  notFollow   : Symbol "!\>\>" Regex
              |                  exclude     : Symbol "\\" Regex
              |                  except      : Symbol "!" !>> "\>" Label
              
              |                  nont        : NontName@1
              |                  string      : String
              |                  character   : Char
              
              |                  \bracket    : "(" Symbol!nont ")"
              ;

syntax Symbols = seq: Symbol*;

syntax Arguments = "(" {Expression ","}* ")";
              
syntax Regex = star        : Regex "*"
             | plus        : Regex "+"
             | option      : Regex "?"
             | \bracket    : "(" Regex ")"
             | sequence    : "(" Regex Regex+ ")"  
             | alternation : "(" Regexs ("|" Regexs)+ ")"
               
             | nont        : NontName@1
             | charClass   : CharClass
             | string      : String
             | character   : Char
             ;
             
syntax Regexs = seq: Regex+;
             
syntax CharClass = chars: "[" Range* "]" | notChars: "[^" Range* "]";

syntax Range = range: RangeChar "-" RangeChar | character: RangeChar;

syntax Expression =            call           : Expression Arguments
                  > left      (multiplication : Expression "*" Expression
                  |            division       : Expression "/" Expression)
                  > left      (plus           : Expression "+" Expression
                  |            minus          : Expression "-" Expression)
                  > non-assoc (greaterEq      : Expression "\>=" Expression  
		          |            lessEq         : Expression "\<=" Expression
		          |            greater        : Expression "\>" Expression
		          |            less           : Expression "\<" Expression)
	              > non-assoc (equal          : Expression "==" Expression
	              |            notEqual       : Expression "!=" Expression)
	              > left      (and            : Expression "&&" Expression 
	              |            or             : Expression "||" Expression)
	              > right      ifThenElse     : Expression "?" Expression ":" Expression
	              |            lExtent        : VarName@1 ".l"
	              |            rExtent        : VarName@1 ".r"
	              |            yield          : VarName@1 ".yield"
                  |            name           : VarName@1
                  |            number         : Number
                  |            \bracket       : "(" Expression ")"
                  ;

syntax ReturnExpression = exp: "{" Expression "}";

syntax Binding = assign  : VarName@1 "=" Expression
               | declare : "var" VarName@0 "=" Expression
               ;

str input = "";

public void main() {
	save(#Definition,"/Users/anastasiaizmaylova/git/iguana/test/org/iguana/parser/iggy/IGGYBeta");
}