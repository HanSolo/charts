package eu.hansolo.fx.charts.forcedirectedgraph;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * authors: Michael L\u00E4uchli, MLaeuchli (github)
 *          Stefan Mettler, orizion (github)
 */
public class GraphEdge {
    private ObjectProperty<GraphNode> u;
    private ObjectProperty<GraphNode> v;
    private double                    force;
    private double                    width;
    private HashMap<String, Double>   nummericAttributes;



    public GraphEdge(GraphNode node1, GraphNode node2, double force, double width, Map<String, Double> nummericAttributes ){
        this.u = new SimpleObjectProperty<>(node1);
        this.v = new SimpleObjectProperty<>(node2);
        this.force = force;
        this.width = width;
        this.nummericAttributes = new HashMap<>(nummericAttributes);
        addDefault();
    }
    public GraphEdge(GraphNode node1, GraphNode node2,Map<String, Double> nummericAttributes ){
        this(node1, node2, 1, 1, nummericAttributes);
    }
    public GraphEdge(GraphNode node1, GraphNode node2){
        this(node1,node2, new HashMap<>());
    }


    private void addDefault(){
        nummericAttributes.put(NodeEdgeModel.DEFAULT, 1.0);
    }

    public double getForce() {
        return force;
    }
    public void setForce(double force) {
        this.force = force;
    }

    public double getWidth() {
        return width;
    }
    public void setWidth(double width) {
        this.width = width;
    }

    public GraphNode getU() { return u.get(); }
    public void setU(final GraphNode U) { u.set(U); }
    public ObjectProperty<GraphNode> uProperty() { return u; }

    public GraphNode getV() { return v.get(); }
    public void setV(final GraphNode V) { v.set(V); }
    public ObjectProperty<GraphNode> vProperty() { return v; }

    /**
     * Returns the chosen numeric attribute if it exists, else -1 is returned
     * @param key
     * @return
     */
    public double getNummericAttribute(String key){
        if(nummericAttributes.containsKey(key)) {
            return nummericAttributes.get(key);
        }
        return -1d;
    }

    public ArrayList<String> getNummericAttributeKeys(){
        ArrayList<String> keys = new ArrayList<>();
        for(Object o: nummericAttributes.keySet()){
            if(o instanceof String){
                keys.add((String) o);
            }
        }
        return keys;
    }
}
