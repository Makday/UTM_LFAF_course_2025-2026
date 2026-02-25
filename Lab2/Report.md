# 2_FiniteAutomata

### Course: Formal Languages & Finite Automata
### Author: Cristian Bruma

----

## Objectives:

1. Understand what an automaton is and what it can be used for.

2. Continuing the work in the same repository and the same project, the following need to be added:
   a. Provide a function in your grammar type/class that could classify the grammar based on Chomsky hierarchy.

   b. For this you can use the variant from the previous lab.

3. According to your variant number (by universal convention it is register ID), get the finite automaton definition and do the following tasks:

   a. Implement conversion of a finite automaton to a regular grammar.

   b. Determine whether your FA is deterministic or non-deterministic.

   c. Implement some functionality that would convert an NDFA to a DFA.

   d. Represent the finite automaton graphically (Optional, and can be considered as a __*bonus point*__):

    - You can use external libraries, tools or APIs to generate the figures/diagrams.

    - Your program needs to gather and send the data about the automaton and the lib/tool/API return the visual representation.

Please consider that all elements of the task 3 can be done manually, writing a detailed report about how you've done the conversion and what changes have you introduced. In case if you'll be able to write a complete program that will take some finite automata and then convert it to the regular grammar - this will be **a good bonus point**.


## Implementation description

Compare to the previous laboratory work, both the automata and the grammar classes have been reworked.
`Grammar` now contains a `.getGrammarType()` method to check its grammar type. The method checks every type according to Chomsky's hierarchy from most restrictive to the least.
```java
    public GrammarType getGrammarType() {
        if (isRegularGrammar())
            return GrammarType.TYPE_3;

        if (isContextFreeGrammar())
            return GrammarType.TYPE_2;

        if (isContextSensitiveGrammar())
            return GrammarType.TYPE_1;

        return GrammarType.TYPE_0;
    }
```
Upon execution, it returns an enum type for convenience.

As for the finite automata implementation, the approach from the previous laboratory work with just one NFA class was changed.
Currently, there is an abstract class `FiniteAutomaton`, which generalizes the parameters of a finite automaton, since both NFA and DFA have the same parameters, except transitions.
The `FiniteAutomaton` also adds abstract general methods.  
```java
public abstract class FiniteAutomaton {
    protected Set<State> states;
    protected Set<Character> alphabet;
    protected State startState;
    protected Set<State> acceptStates;

    public FiniteAutomaton(Set<State> states, Set<Character> alphabet, State startState, Set<State> acceptStates) {
        this.states = states;
        this.alphabet = alphabet;
        this.startState = startState;
        this.acceptStates = acceptStates;
    }

    public Set<State> getStates() {return states;}
    public Set<Character> getAlphabet() {return alphabet;}
    public State getStartState() {return startState;}
    public Set<State> getAcceptStates() {return acceptStates;}

    public abstract boolean accepts(String input);
    public abstract Grammar toRegularGrammar();
    public abstract boolean isDeterministic();
    public abstract DFA toDFA();
    public abstract Map<State, Map<Character, Set<State>>> getTransitions();
}
```

The two implementation of `FiniteAutomaton` are `DFA` and `NFA`, their main difference is in the implementation of parameter `transitions`.
The `DFA` uses for `transitions` nested maps, which map States + Inputs to a single State. For clarity it can be represented as `transitions[State][Input] = State`.
```java
public class DFA extends FiniteAutomaton {
    private final Map<State, Map<Character, State>> transitions;

    public DFA(Set<State> states, Set<Character> alphabet, State startState,
               Set<State> acceptStates, Map<State, Map<Character, State>> transitions) {
        super(states, alphabet, startState, acceptStates);
        this.transitions = transitions;
    }
}
```
The `NFA`, however, uses the same nested maps, but the final value they point to is a **SET** of states. For clarity it can be shown as `transitions[State][Input] = {State0, State1, ...}`.
```java
public class NFA extends FiniteAutomaton {
    private final Map<State, Map<Character, Set<State>>> transitions;

    public NFA(Set<State> states, Set<Character> alphabet, State startState,
               Set<State> acceptStates, Map<State, Map<Character, Set<State>>> transitions) {
        super(states, alphabet, startState, acceptStates);
        this.transitions = transitions;
    }
}

```

Speaking of implemented methods:
### NFA — Simulation and Determinism Check

The `accepts()` method simulates the NFA by tracking a *set* of all currently active states, advancing every one of them in parallel for each input character. If the set becomes empty at any point, the string is immediately rejected; otherwise, it is accepted if any active state at the end is an accept state.

```java
@Override
public boolean accepts(final String inputString) {
    Set currentStates = new HashSet<>();
    currentStates.add(startState);

    for (int i = 0; i < inputString.length(); i++) {
        char c = inputString.charAt(i);
        Set nextStates = new HashSet<>();
        for (State state : currentStates) {
            if (transitions.containsKey(state) && transitions.get(state).containsKey(c))
                nextStates.addAll(transitions.get(state).get(c));
        }
        currentStates = nextStates;
        if (currentStates.isEmpty()) return false;
    }

    for (State state : currentStates) {
        if (acceptStates.contains(state)) return true;
    }
    return false;
}
```

`isDeterministic()` inspects the transition map and returns `false` if any symbol maps to more than one target state, or if an epsilon transition (`'\0'`) is present — both of which are defining characteristics of non-determinism.

```java
@Override
public boolean isDeterministic() {
    for (Map.Entry<State, Map<Character, Set<State>>> entry : transitions.entrySet()) {
        for (Map.Entry<Character, Set<State>> t : entry.getValue().entrySet()) {
            if (t.getKey() == '\0') return false;
            if (t.getValue().size() > 1) return false;
        }
    }
    return true;
}
```

### NFA → DFA Conversion (Subset Construction)

`toDFA()` implements the classic *subset construction* algorithm. Each resulting DFA state represents a subset of NFA states reachable together; a BFS queue explores all reachable subsets and builds the deterministic transition table incrementally. A DFA state is marked as an accept state whenever any NFA state in its corresponding subset is an accept state.

```java
public DFA toDFA() {
    Map> dfaTransitions = new HashMap<>();
    Set dfaAcceptStates = new HashSet<>();
    Set<Set> visited = new HashSet<>();
    Queue<Set> queue = new LinkedList<>();

    Set initial = Set.of(startState);
    queue.add(initial);
    visited.add(initial);

    while (!queue.isEmpty()) {
        Set current = queue.poll();
        State dfaFrom = State.fromSet(current);
        dfaTransitions.put(dfaFrom, new HashMap<>());

        for (Character c : alphabet) {
            Set next = new HashSet<>();
            for (State s : current) {
                next.addAll(transitions
                        .getOrDefault(s, Map.of())
                        .getOrDefault(c, Set.of()));
            }
            if (next.isEmpty()) continue;

            State dfaTo = State.fromSet(next);
            dfaTransitions.get(dfaFrom).put(c, dfaTo);

            if (!visited.contains(next)) {
                visited.add(next);
                queue.add(next);
            }
        }

        for (State s : current) {
            if (acceptStates.contains(s)) {
                dfaAcceptStates.add(dfaFrom);
                break;
            }
        }
    }

    return new DFA(new HashSet<>(dfaTransitions.keySet()), alphabet,
            State.fromSet(initial), dfaAcceptStates, dfaTransitions);
}
```

### Converting Automata to Regular Grammar

Both `NFA` and `DFA` implement `toRegularGrammar()` using the same right-linear logic: for every transition `from --terminal--> to`, a production `from → terminal to` is added. If `to` is an accept state, the non-terminal is dropped, yielding `from → terminal`, which encodes the end of a valid derivation. The DFA version additionally maps each `State` to a letter (`A`, `B`, `C`, …) first, to keep grammar symbols clean and readable.

```java
// NFA version
for (State to : t.getValue()) {
    if (acceptStates.contains(to)) {
        P.add(new Production(from.getName(), String.valueOf(terminal)));
    } else {
        P.add(new Production(from.getName(), terminal + to.getName()));
    }
}

// DFA version — same logic, but with letter-mapped state names
if (acceptStates.contains(t.getValue())) {
    P.add(new Production(from, String.valueOf(terminal)));
} else {
    P.add(new Production(from, terminal + to));
}
```

### DFA — Unified Transition Interface

Since the abstract base class exposes `getTransitions()` returning `Map<State, Map<Character, Set<State>>>`, the DFA overrides it by wrapping each single target state in a `Set`. This allows both automaton types to be handled polymorphically without changing their internal storage.

```java
@Override
public Map<State, Map<Character, Set<State>>> getTransitions() {
    Map<State, Map<Character, Set<State>>> result = new HashMap<>();
    for (Map.Entry<State, Map<Character, State>>  entry : transitions.entrySet()) {
        Map<Character, Set<State>> inner = new HashMap<>();
        for (Map.Entry<Character, State> t : entry.getValue().entrySet()) {
            inner.put(t.getKey(), new HashSet<>(Set.of(t.getValue())));
        }
        result.put(entry.getKey(), inner);
    }
    return result;
}
```

### FiniteAutomatonVisualizer

`FiniteAutomatonVisualizer` renders any `FiniteAutomaton` (NFA or DFA) as an interactive graph window using the **JGraphX** library. It accepts a `FiniteAutomaton` and a display name via its constructor, then on `display()` builds a `mxGraph` by iterating over the automaton's states and transitions through the unified `getTransitions()` interface — meaning it works identically for both NFA and DFA without any extra logic.

States are drawn as ellipses, with accept states distinguished by a double ellipse and a yellow fill. A dashed edge with no label is drawn from an invisible zero-size start node to the start state, following the standard automaton diagram convention. Transition edges are labelled with their input character, and self-loops or multiple edges between the same pair of states are handled naturally by JGraphX.

```java
for (State state : fa.getStates()) {
    String style = fa.getAcceptStates().contains(state)
            ? "shape=doubleEllipse;perimeter=ellipsePerimeter;fillColor=#fff2cc;strokeColor=#d6b656;" + fontSize
            : "shape=ellipse;perimeter=ellipsePerimeter;" + fontSize;

    Object vertex = graph.insertVertex(parent, null, state.getName(), 0, 0, WIDTH, HEIGHT, style);
    vertexMap.put(state, vertex);
}
graph.insertEdge(parent, null, "", startNode, vertexMap.get(fa.getStartState()), "dashed=1;" + fontSize);

for (Map.Entry<State, Map<Character, Set<State>>> entry : fa.getTransitions().entrySet()) {
    Object fromVertex = vertexMap.get(entry.getKey());
    for (Map.Entry<Character, Set<State>> t : entry.getValue().entrySet()) {
        for (State to : t.getValue()) {
            Object toVertex = vertexMap.get(to);
            graph.insertEdge(parent, null, String.valueOf(t.getKey()), fromVertex, toVertex, fontSize);
        }
    }
}
```

After the graph is built, `mxFastOrganicLayout` is applied to automatically position the nodes in a readable, force-directed layout before the result is displayed in a `JFrame`.

```java
new mxFastOrganicLayout(graph).execute(parent);

mxGraphComponent component = new mxGraphComponent(graph);
JFrame frame = new JFrame(name);
frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
frame.getContentPane().add(component);
frame.setSize(800, 600);
frame.setVisible(true);
```

As an example, for the finite automaton:
```
Q = {q0,q1,q2,q3,q4},
∑ = {a,b},
F = {q4},
δ(q0,a) = q1,
δ(q1,b) = q1,
δ(q1,b) = q2,
δ(q2,b) = q3,
δ(q3,a) = q1,
δ(q2,a) = q4.
```
It produces the following NFA and DFA visualizations:
![NFAgraph](Lab2/images/DFAgraph.png)
![DFAgraph](Lab2/images/DFAgraph.png)

## Conclusions / Screenshots / Results

&ensp;&ensp;&ensp;&ensp;In this laboratory work, the finite automaton implementation was significantly extended compared to the previous one. The single NFA class was replaced with a proper hierarchy — an abstract `FiniteAutomaton` base and two concrete implementations, `NFA` and `DFA`, each with their own transition structure. On top of that, key theoretical operations were implemented in code: classifying grammars by Chomsky hierarchy, checking determinism, converting an NFA to a DFA via subset construction, and converting any automaton to a regular grammar. Finally, the optional visualization task was completed using the JGraphX library, which made it straightforward to visually verify the correctness of both the original NFA and the converted DFA.

## References

1. Cojuhari I. "Regular language. Finite automata" - Seminar Slides. https://drive.google.com/file/d/1rBGyzDN5eWMXTNeUxLxmKsf7tyhHt9Jk/view

2. Cojuhari I. "FINITE AUTOMATA" - Seminar Slides. https://else.fcim.utm.md/pluginfile.php/64791/mod_resource/content/0/Chapter_2.pdf
