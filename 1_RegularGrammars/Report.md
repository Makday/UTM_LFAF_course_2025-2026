# 1_RegularGrammars

### Course: Formal Languages & Finite Automata
### Author: Cristian Bruma

----

## Objectives:

1. Discover what a language is and what it needs to have in order to be considered a formal one;

2. Provide the initial setup for the evolving project that you will work on during this semester. You can deal with each laboratory work as a separate task or project to demonstrate your understanding of the given themes, but you also can deal with labs as stages of making your own big solution, your own project. Do the following:

   a. Create GitHub repository to deal with storing and updating your project;

   b. Choose a programming language. Pick one that will be easiest for dealing with your tasks, you need to learn how to solve the problem itself, not everything around the problem (like setting up the project, launching it correctly and etc.);

   c. Store reports separately in a way to make verification of your work simpler (duh)

3. According to your variant number, get the grammar definition and do the following:

   a. Implement a type/class for your grammar;

   b. Add one function that would generate 5 valid strings from the language expressed by your given grammar;

   c. Implement some functionality that would convert and object of type Grammar to one of type Finite Automaton;

   d. For the Finite Automaton, please add a method that checks if an input string can be obtained via the state transition from it;


## Implementation description

There are 2 main classes, **Grammar** and **NFA**.  
**Grammar** is the implementation of a grammar...  
&ensp;&ensp;&ensp;&ensp;As for functionalities, it has the method *generateString()* which returns a random word satisfying the grammar, made to complete objective 3-b. The method works in the following way: it starts with a string with initial non-terminal character, then enters a loop that parses the string until there are only terminal characters, else it replaces every encountered non-terminal character with a random production from it.  
&ensp;&ensp;&ensp;&ensp;As for the other method, *toFiniteAutomaton()*, it casts the grammar to a Non-Deterministic Finite Automata. The method works in the following way:
- VT - set of terminal characters, becomes E - input alphabet; 
- S - initial non-terminal character, becomes q0 - initial state;  
- VN - set of non-terminal characters, along with a new state *X* are added as Q - finite set of states;  
- F - set of final states, is added with the element *X* inside;  
- As for building delta - transition function, it is implemented as a nested hashmap. The method check every grammar transition, if the transition has 1 singular character, then *delta(state, character) = X*, else *delta(state, character) = new state*, where the new state is the second character in the transition (This assumes the grammar is right linear).  


**NFA** is the implementation of the Non-Deterministic Finite Automata.  
&ensp;&ensp;&ensp;&ensp;It has the method *stringBelongToLanguage()*, which checks if the input string is accepted by the finite automaton. Since the automaton is non-deterministic, it tries multiple states at the same time. 
- First it starts with the set *current state*, to which q0 - initial state, is added.
- Then the input string is parsed character by character, for every state in the *current state* set, if there is a transition from this state with the input character, then it is added to *next state* set. *current state* set becomes *next state* set.
- After the string was fully parsed, it is checked if *current state* set contains a final state, if it does, then the input string is accepted by the automaton, else it isn't.

## Conclusions / Screenshots / Results

&ensp;&ensp;&ensp;&ensp;As it can be noticed, the given input grammar is regular and of type right-linear, this lead to making the grammar-to-automaton transition function exploit this property.  
&ensp;&ensp;&ensp;&ensp;Another fact to be noticed is that the provided grammar results in a NFA, there were 2 approaches to handling this: write a plain NFA class, but have increased complexity every time it checks if the string is accepted by it; write a NFA-to-DFA converter and then check the string using the DFA, which would be less complex. In this laboratory work, the first approach was chosen. The reason being that the input grammar is relatively small in its parameters and thus checking with a NFA is not that critical for the performance. The second approach would have been much more efficient since each transition would lead to a single state, but this would come at the cost of spending resources on transforming an NFA into an DFA. Since this transformation can be done only once, it is still a more efficient approach.


## References

1. Cojuhari I. "WORDS AND LANGUAGES" - Seminar Slides. https://else.fcim.utm.md/pluginfile.php/63168/mod_resource/content/0/Chapter_1.pdf

2. Cojuhari I. "FINITE AUTOMATA" - Seminar Slides. https://else.fcim.utm.md/pluginfile.php/64791/mod_resource/content/0/Chapter_2.pdf