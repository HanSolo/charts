package eu.hansolo.fx.charts.forcedirectedgraph;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;

import java.util.*;


/**
 * authors: Michael L\u00E4uchli, MLaeuchli (github)
 *          Stefan Mettler, orizion (github)
 */
public class NodeEdgeModel {

    static final Color[]                  KELLY_COLORS = {
            Color.web("0xFFB300"),    // Vivid Yellow
            Color.web("0x803E75"),    // Strong Purple
            Color.web("0xFF6800"),    // Vivid Orange
            Color.web("0xA6BDD7"),    // Very Light Blue
            Color.web("0xC10020"),    // Vivid Red
            Color.web("0xCEA262"),    // Grayish Yellow
            Color.web("0x817066"),    // Medium Gray

            Color.web("0x007D34"),    // Vivid Green
            Color.web("0xF6768E"),    // Strong Purplish Pink
            Color.web("0x00538A"),    // Strong Blue
            Color.web("0xFF7A5C"),    // Strong Yellowish Pink
            Color.web("0x53377A"),    // Strong Violet
            Color.web("0xFF8E00"),    // Vivid Orange Yellow
            Color.web("0xB32851"),    // Strong Purplish Red
            Color.web("0xF4C800"),    // Vivid Greenish Yellow
            Color.web("0x7F180D"),    // Strong Reddish Brown
            Color.web("0x93AA00"),    // Vivid Yellowish Green
            Color.web("0x593315"),    // Deep Yellowish Brown
            Color.web("0xF13A13"),    // Vivid Reddish Orange
            Color.web("0x232C16"),    // Dark Olive Green
    };

    private final String kellyColorKey = "Kelly Color";
    private String currentColorThemeKey = "Kelly Color";

    private ArrayList<GraphNode> nodes;
    private ArrayList<GraphEdge> edges;
    private HashMap<String, ArrayList<String>> distinctValuesPerGroupKey;

    private HashMap<String, HashMap<String, Color>> colorSchemes;
    private HashMap<String ,ArrayList<Color>> colorThemes;
    private ArrayList<Color> kellyColors;
    
    // ----- Currently used Keys in GraphNode/GraphEdge -----
    private String currentSizeKey;
    private String currentGroupKey;
    private String currentForceKey;
    private String currentEdgeWithKey;

    // ----- Factors that scale all Nodes and Edges ----



    private SimpleObjectProperty<Color>     nodeBorderColor;
    private Color                           _nodeBorderColor;
    private BooleanProperty                 alwaysNormalize;
    private boolean                         _alwaysNormalize;
    private BooleanProperty                 neverNormalize;
    private boolean                         _neverNormalize;
    private BooleanProperty                 normalizeIfNotBetweenZeroAndOne;
    private boolean                         _normalizeIfNotBetweenZeroAndOne;
    private BooleanProperty                 isModified;
    private boolean                         _isModified;
    public static String                   DEFAULT = "Default";


    public NodeEdgeModel(List<GraphNode> nodes, List<GraphEdge> edges){
        this.nodes = new ArrayList<>(nodes);
        this.edges = new ArrayList<>(edges);

        // Default will set Value of Nodes to 1
        currentSizeKey = DEFAULT;
        currentGroupKey = DEFAULT;
        currentForceKey = DEFAULT;
        currentEdgeWithKey = DEFAULT;



        distinctValuesPerGroupKey = new HashMap<>();
        saveDistinctValuesForGroupKey(DEFAULT);
        colorSchemes = new HashMap<>();
        getOrCreateGroupColorScheme(DEFAULT);
        _nodeBorderColor = Color.WHITE;
        _alwaysNormalize = true;
        _neverNormalize = false;
        _isModified = false;
        _normalizeIfNotBetweenZeroAndOne = false;
        colorThemes = new HashMap();
        kellyColors = new ArrayList<>();
        Collections.addAll(kellyColors, KELLY_COLORS);

        colorThemes.put(kellyColorKey, kellyColors);
        setupConnectedNotes();
    }

    public NodeEdgeModel(){
        this(new ArrayList<>(),new ArrayList<>());

    }

    private boolean checkIfNodeKeyValuesBetweenZeroAndOne(String key){
        for(GraphNode node: nodes){
            if(node.getNumericAttribute(key) >1 || node.getNumericAttribute(key)<0){
                return false;
            }
        }
        return true;
    }

    private boolean checkIfEdgeValuesBetweenZeroAndOne(String key){
        for(GraphEdge edge: edges){
            if(edge.getNummericAttribute(key) >1 || edge.getNummericAttribute(key)<0){
                return false;
            }
        }
        return true;
    }

    /**
        Takes the NumericAttribute of every node with the key {@code key},
        normalizes it and sets it again.
     */
    private void setNodeSizeFromAttributeNormalized(String key) {
        double min = nodes.get(0).getNumericAttribute(key);
        double max = nodes.get(0).getNumericAttribute(key);
        for(GraphNode node: nodes){
            if(node.getNumericAttribute(key) < min){
                min = node.getNumericAttribute(key);
            } else if(node.getNumericAttribute(key) > max){
                max = node.getNumericAttribute(key);
            }
        }
        for(GraphNode node: nodes){
            //instead of creating a new attribute, we normalized the set value;
            //directly calculate the radius from the value and set that
            if(min!=max) {
                node.setValue(Math.sqrt(
                        (node.getNumericAttribute(key) - min)
                                / (Math.PI * (max - min))
                        )
                );
            } else{
                node.setValue(Math.sqrt(0.5/Math.PI));
            }
        }
    }

    private void setupNodeBorderColorListener(){
        nodeBorderColor.addListener(new ChangeListener<Color>() {
            @Override
            public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
                for(GraphNode node: nodes){
                    node.setStroke(newValue);
                }
                isModified.setValue(true);
            }
        });
    }

    /**
     * Creates the color scheme and adds it to the list of schemes
     * @param groupKey
     */
    private void initGroupColorScheme(String groupKey) {
        saveDistinctValuesForGroupKey(groupKey);
        HashMap<String, Color> colorScheme = new HashMap<>();
        int index = 0;
        for(String value : distinctValuesPerGroupKey.get(groupKey)) {
            colorScheme.put(value, KELLY_COLORS[index++]);
            if(index >= KELLY_COLORS.length) {
                index = 0;
            }
        }
        this.colorSchemes.put(groupKey,colorScheme);
    }

    /**
     * Finds the distinct values for the given Group Key and puts them
     * in the {@code distinctValuesPerGroupKey} HashMap
     * @param groupKey the group for which we want all distinct values
     */
    private void saveDistinctValuesForGroupKey(String groupKey) {
        if(distinctValuesPerGroupKey.containsKey(groupKey)) return;
        //collects all values for the grouping key and returns distinct values
        String[] distinct = getNodes().stream()
                .map(n -> n.getStringAttribute(groupKey))
                .distinct().toArray(String[]::new);
        distinctValuesPerGroupKey.put(groupKey, new ArrayList<String>(Arrays.asList(distinct)));
    }

    // ******************** Grouping ******************************************
    /**
     * Sets the group Key and colors on all nodes, in the colors of the group's associated color.
     * The currentColorscheme will be created if it doesnt exist already
     * @param group
     */
    public void setGroupColors(String group){
        if(null == colorSchemes.get(group) || colorSchemes.get(group).isEmpty()) {
            getOrCreateGroupColorScheme(group);
        }
        currentGroupKey = group;
        isModifiedProperty().set(true);
    }

    /**
     * Sets the group Key and colors on all nodes to the current group key, in the colors of the group's associated color.
     * The currentColorscheme will be created if it doesnt exist already
     */
    public void setGroupColors() {
        setGroupColors(getCurrentGroupKey());
    }

    /**
     * Set the key that is currently used for dividing nodes into groups
     * and color the groups accordingly
     * @param group
     */
    /*
    public void setCurrentGroupKey(String group) {
        currentGroupKey = group;
        for (GraphNode node : nodes) {
            node.setFill(getOrCreateGroupColorScheme().get(node.getStringAttribute(currentGroupKey)));
        }
    }
*/

    /**
     * The key "default" is used for nodes not matching any other entries in the scheme
     * @param colorscheme
     */
    public void setGroupColorScheme(String groupKey, HashMap<String, Color> colorscheme) {
        colorSchemes.put(groupKey,colorscheme);
    }

    /**
     * Creates or returns (if it already exists) the color scheme for the given grouping key
     * @param group
     * @return
     */
    public HashMap<String, Color> getOrCreateGroupColorScheme(String group) {
        if(null == colorSchemes.get(group) || colorSchemes.get(group).isEmpty()) { initGroupColorScheme(group); }
        return this.colorSchemes.get(group);
    }

    /**
     * Get or create the colorscheme for the current group
     * @return
     */
    public HashMap<String, Color> getOrCreateGroupColorScheme() {
        return getOrCreateGroupColorScheme(getCurrentGroupKey());
    }

    public HashMap<String, ArrayList<String>> getDistinctValuesPerGroupKey() {
        return distinctValuesPerGroupKey;
    }

    public ArrayList<String> getDistinctValuesPerGroupKey(String groupKey) {
        return distinctValuesPerGroupKey.get(groupKey);
    }

    public Color getGroupValueColor(String value, String key){
        return colorSchemes.get(key).get(value);
    }

    public void setGroupValueColor(String value, String key, Color color){
        colorSchemes.get(key).replace(value,color);
        for(GraphNode node: nodes){
            if(node.getStringAttribute(key).equals(value)){
                node.setFill(color);
            }
        }
        isModifiedProperty().set(true);
    }

    public void addColorTheme(List<Color> theme, String key){
        colorThemes.put(key, new ArrayList<>(theme));
    }

    public void instantiateColorScheme(String colorThemeKey) {
        currentColorThemeKey = colorThemeKey;
        instantiateColorScheme();
    }

    public void instantiateColorScheme() {
        saveDistinctValuesForGroupKey(currentGroupKey);
        ArrayList<Color> currentColorTheme = colorThemes.get(currentColorThemeKey);
        HashMap<String, Color> colorScheme = new HashMap<>();
        int index = 0;

        if(null == currentColorThemeKey || null == currentColorTheme) {
            currentColorThemeKey = kellyColorKey;
            currentColorTheme = colorThemes.get(currentColorThemeKey);
        }

        if (distinctValuesPerGroupKey.get(currentGroupKey).size() <= currentColorTheme.size()) {
            for (String value : distinctValuesPerGroupKey.get(currentGroupKey)) {
                colorScheme.put(value, currentColorTheme.get(index++));
            }
        } else {
            System.err.println("Too many distinct values to match them to the predefined set of colors");
        }
        this.colorSchemes.put(currentGroupKey,colorScheme);
        isModifiedProperty().set(true);
    }

    public void setCurrentColorThemeKey(String currentColorThemeKey){ this.currentColorThemeKey = currentColorThemeKey;}

    public String getCurrentColorThemeKey(){return currentColorThemeKey;}

    public Set<String> getColorThemeKeys(){
        return colorThemes.keySet();
    }


// ******************** Normalize Getter und Setter ******************************************
    public boolean isAlwaysNormalize(){ return null != alwaysNormalize ? alwaysNormalize.get() : _alwaysNormalize;   }

    public BooleanProperty alwaysNormalizeProperty(){
        if(null == alwaysNormalize){
            alwaysNormalize = new SimpleBooleanProperty(_alwaysNormalize);
        }
        return alwaysNormalize;
    }

    public void setAlwaysNormalize(boolean alwaysNormalize){
        if(null != this.alwaysNormalize){
            this.alwaysNormalize.set(alwaysNormalize);
        } else{
            _alwaysNormalize = alwaysNormalize;
        }
    }

    public boolean isNeverNormalize(){ return null != neverNormalize ? neverNormalize.get() : _neverNormalize;}

    public BooleanProperty neverNormalizeProperty(){
        if(null == neverNormalize){
            neverNormalize = new SimpleBooleanProperty(_neverNormalize);
        }
        return neverNormalize;
    }

    public void setNeverNormalize(boolean neverNormalize){
        if(null != this.neverNormalize){
            this.neverNormalize.set(neverNormalize);
        } else{
            _neverNormalize = neverNormalize;
        }
    }

    public boolean isNormalizeIfNotBetweenZeroAndOne(){ return null != normalizeIfNotBetweenZeroAndOne ? normalizeIfNotBetweenZeroAndOne.get() : _normalizeIfNotBetweenZeroAndOne;    }

    public BooleanProperty normalizeIfNotBetweenZeroAndOneProperty(){
        if(null == normalizeIfNotBetweenZeroAndOne){
            normalizeIfNotBetweenZeroAndOne = new SimpleBooleanProperty(_normalizeIfNotBetweenZeroAndOne);
        }
        return normalizeIfNotBetweenZeroAndOne;
    }

    public void setNormalizeIfNotBetweenZeroAndOne(boolean normalizeIfNotBetweenZeroAndOne){
        if(null != this.normalizeIfNotBetweenZeroAndOne){
            this.normalizeIfNotBetweenZeroAndOne.set(normalizeIfNotBetweenZeroAndOne);
        } else {
            _normalizeIfNotBetweenZeroAndOne = normalizeIfNotBetweenZeroAndOne;
        }
    }

    /**
     * Sets the attribute associated with the key {@code key}
     * as the value of all nodes.
     * This means effectively that now the Value of the attribute is used for the
     * radius calculation of the node
     * @param key
     */
    public void setNodeSizeKey(String key){
        this.currentSizeKey = key;
        if(isAlwaysNormalize()){
            setNodeSizeFromAttributeNormalized(key);
        } else if(isNormalizeIfNotBetweenZeroAndOne()){
            if(checkIfNodeKeyValuesBetweenZeroAndOne(key)){
                setNodeSizeFromAttributeNotNormalized(key);
            } else{
                setNodeSizeFromAttributeNormalized(key);
            }
        } else if( isNeverNormalize()){
            setNodeSizeFromAttributeNotNormalized(key);
        } else{
            System.err.println("Normalization behavior not defined, data was normalized as default behavior");
        }
        isModifiedProperty().set(true);
    }

    public void setNodeSizeFromAttributeNotNormalized(String key){
        for(GraphNode node : nodes){
            node.setValue(node.getNumericAttribute(key));
        }
    }

    public void setEdgeForceFromAttributeNormalized(String key){
        double min = edges.get(0).getNummericAttribute(key);
        double max = edges.get(0).getNummericAttribute(key);
        for(GraphEdge edge: edges){
            if (edge.getNummericAttribute(key) < min){
                min = edge.getNummericAttribute(key);
            } else if(edge.getNummericAttribute(key) > max){
                max = edge.getNummericAttribute((key));
            }
        }
        for(GraphEdge edge: edges){
            if(min!=max){
                edge.setForce((edge.getNummericAttribute(key)-min)/(max-min));
            }else{
                edge.setForce(0.5);
            }
        }
        isModifiedProperty().set(true);
    }

    public void setEdgeWidthFromAttributeNormalized(String key){
        double min = edges.get(0).getNummericAttribute(key);
        double max = min;
        for(GraphEdge edge: edges){
            if (edge.getNummericAttribute(key) < min){
                min = edge.getNummericAttribute(key);
            } else if(edge.getNummericAttribute(key) > max){
                max = edge.getNummericAttribute((key));
            }
        }
        for(GraphEdge edge: edges){
            if(min!=max){
                edge.setWidth((edge.getNummericAttribute(key)-min)/(max-min));
            }else{
                edge.setWidth(0.5);
            }
        }
        isModifiedProperty().set(true);
    }

    public void setEdgeForceFromAttributeNotNormalized(String key){

        for(GraphEdge edge : edges){
            edge.setForce(edge.getNummericAttribute(key));
        }
    }

    public void setEdgeWithFromAttributeNotNormalized(String key){
        for(GraphEdge edge : edges){
            edge.setWidth(edge.getNummericAttribute(key));
        }
    }

    public void printPositionsOnConsole(){
        for(GraphNode node: nodes){
            System.out.println(node.getPosition().getX() + " " + node.getPosition().getY());
        }
    }

    public ArrayList<String> getNumericAttributeKeysOfNodes(){
        return nodes.get(0).getNumericAttributeKeys();
    }

    /**
     * Returns a list of Strings representing all the attributes of a node
     * @return
     */
    public ArrayList<String> getStringAttributeKeysOfNodes(){
        return nodes.get(0).getStringAttributeKeys();
    }
    public ArrayList<String> getNumericAttributeKeysOfEdges(){
        return edges.get(0).getNummericAttributeKeys();
    }

    public void setupConnectedNotes(){
        for(GraphEdge edge: edges){
            edge.getU().addConnection(edge.getV());
            edge.getV().addConnection(edge.getU());
        }
    }

    /**
     * Iterats trough nodes and returns the first Node Found in that area
     *
     * @param X XPosition of click translated to Real Position (see xPositionDrawnToReal in Graphpanel)
     * @param Y YPosition of click translated to Real Position (see yPositionDrawnToReal in Graphpanel)
     * @param scaleFactor Factor that scales the drawn size of the Node
     * @param minRadius Minimal Radius tha drawn Node has
     * @return First node found or null
     */
    public GraphNode getNodeAt(final double X, final double Y, double scaleFactor, double minRadius, double nodeSizeFactor){
        for(GraphNode node : nodes){
            if(node.containedIn(X,Y,nodeSizeFactor,scaleFactor,minRadius)) return node;
        }
        return null;
    }

    public ArrayList<GraphNode> getNodes() {
        return nodes;
    }

    public ArrayList<GraphEdge> getEdges() {
        return edges;
    }

    public String getCurrentSizeKey() {
        return currentSizeKey;
    }

    public String getCurrentGroupKey() {
        return currentGroupKey;
    }

    public String getCurrentForceKey() {
        return currentForceKey;
    }

    public String getCurrentEdgeWithKey() {
        return currentEdgeWithKey;
    }

    public Color getNodeBorderColor() { return null == nodeBorderColor ? _nodeBorderColor : nodeBorderColor.get(); }
    public void setNodeBorderColor(final Color COLOR) {
        if (null == nodeBorderColor) {
            _nodeBorderColor = COLOR;
        } else {
            nodeBorderColor.set(COLOR);
        }
        isModifiedProperty().setValue(true);
    }
    public ObjectProperty<Color> nodeBorderColorProperty(){
        if(null == nodeBorderColor){
            nodeBorderColor = new SimpleObjectProperty<>(_nodeBorderColor);
            setupNodeBorderColorListener();
        }
        return nodeBorderColor;
    }

    public boolean isModified() { return null == isModified ? _isModified : isModified.get(); }
    public void setModified(final boolean modified) {
        if (null == isModified) {
            _isModified = modified;
        } else {
            isModified.set(modified);
        }
    }
    public BooleanProperty isModifiedProperty() {
        if (null == isModified){
            isModified = new SimpleBooleanProperty(_isModified);
        }
        return isModified;
    }

    public void setEdgeWithKey(String key){
        this.currentEdgeWithKey = key;
        if(isAlwaysNormalize()){
            setEdgeWidthFromAttributeNormalized(key);
        } else if(isNormalizeIfNotBetweenZeroAndOne()){
            if(checkIfEdgeValuesBetweenZeroAndOne(key)){
                setEdgeWithFromAttributeNotNormalized(key);
            } else{
                setEdgeWidthFromAttributeNormalized(key);
            }
        } else if( isNeverNormalize()){
            setEdgeWithFromAttributeNotNormalized(key);
        } else{
            System.err.println("Normalization behavior not defined, data was normalized as default behavior");
        }
    }

    public void setEdgeForceKey(String key){
        this.currentForceKey = key;
        if(isAlwaysNormalize()){
            setEdgeForceFromAttributeNormalized(key);
        } else if(isNormalizeIfNotBetweenZeroAndOne()){
            if(checkIfEdgeValuesBetweenZeroAndOne(key)){
                setEdgeForceFromAttributeNotNormalized(key);
            } else{
                setEdgeForceFromAttributeNormalized(key);
            }
        } else if( isNeverNormalize()){
            setEdgeForceFromAttributeNotNormalized(key);
        } else{
            System.out.println("Normalization behavior not defined, data was normalized as default behavior");
        }
    }
}
