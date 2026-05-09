package lfaf.university.labs2026;

import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import lfaf.university.labs2026.automata.FiniteAutomaton;
import lfaf.university.labs2026.helpers.State;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FiniteAutomatonVisualizer {
    private final String name;
    private final FiniteAutomaton fa;
    private static final int WIDTH = 150;
    private static final int HEIGHT = 150;
    private static final String fontSize = "fontSize=16;";

    public FiniteAutomatonVisualizer(FiniteAutomaton fa,  String name) {
        this.fa = fa;
        this.name = name;
    }

    public void display() {
        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();

        graph.getModel().beginUpdate();
        Object startNode = graph.insertVertex(parent, null, "", 0, 0, 0, 0, "");
        try {
            Map<State, Object> vertexMap = new HashMap<>();

            for(State state : fa.getStates()) {
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

        } finally {
            graph.getModel().endUpdate();
        }
        new mxFastOrganicLayout(graph).execute(parent);

        mxGraphComponent component = new mxGraphComponent(graph);
        JFrame frame = new JFrame(name);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(component);
        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}
