package eu.hansolo.fx.charts.event;


import eu.hansolo.fx.charts.tree.TreeNode;


public class TreeNodeEvent {
    public enum EventType { PARENT_CHANGED, CHILDREN_CHANGED, NODE_SELECTED }

    private final TreeNode  SRC;
    private final EventType TYPE;


    // ******************** Constructors **************************************
    public TreeNodeEvent(final TreeNode SRC, final EventType TYPE) {
        this.SRC  = SRC;
        this.TYPE = TYPE;
    }


    // ******************** Methods *******************************************
    public TreeNode getSource() { return SRC; }

    public EventType getType() { return TYPE; }
}