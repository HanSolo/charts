/*
 * Copyright (c) 2019 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hansolo.fx.charts.pareto;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;


/**
 * authors: Michael L\u00E4uchli, MLaeuchli (github)
 *          Stefan Mettler, orizion (github)
 */
public class ParetoBar {
    private Double                value;
    private String                name;
    private double                x;
    private double                y;
    private double                width;
    private double                height;
    private Color                 _fillColor;
    private ObjectProperty<Color> fillColor;
    private ArrayList<ParetoBar>  bars;


    // ******************** Constructors **************************************
    /**
     *
     * @param name The name to display for the bar
     * @param bars List of type ParetoBar, if value is null, these bars will be used to determine this bars value.
     */
    public ParetoBar(String name, List<ParetoBar> bars) { this(name, null, bars);    }
    /**
     *
     * @param name The name to display for the bar
     * @param value The value of this bar. If value is null, the ParetoBar will try to calculate the value from the list
     *              of ParetoBars
     */
    public ParetoBar(String name, double value) {
        this(name, value, null);
    }
    /**
     *
     * @param name The name to display for the bar
     * @param value The value of this bar. If value is null, the ParetoBar will try to calculate the value from the list
     *              of ParetoBars
     * @param bars List of type ParetoBar, if value is null, these bars will be used to determine this bars value.
     */
    public ParetoBar(String name, Double value, List<ParetoBar> bars) {
        this(name, value, Color.BLUE, bars);

    }
    /**
     *
     * @param name The name to display for the bar
     * @param value The value of this bar. If value is null, the ParetoBar will try to calculate the value from the list
     *              of ParetoBars
     * @param color The color used to fill this Bar
     * @param bars List of type ParetoBar, if value is null, these bars will be used to determine this bars value.
     */
    public ParetoBar(String name, Double value, Color color, List<ParetoBar> bars){
        this.bars = (bars == null) ? new ArrayList<>() : new ArrayList<>(bars);
        this.name = name;
        this._fillColor = color;


        if(value == null && bars != null && bars.size() > 0 ){
            double temp = 0;
            for(ParetoBar bar: bars){
                temp += bar.getValue();
            }
            this.value = temp;
        } else{
            this.value = value;
        }

        if (bars != null) { sortAscending(); }
    }


    // ******************** Methods *******************************************
    private void updateValue() { value = bars.stream().mapToDouble(ParetoBar::getValue).sum(); }

    public double getX() { return x; }
    public void setX(final double X) { x = X; }

    public double getY() { return y; }
    public void setY(final double Y) { y = Y; }

    public double getWidth() { return width; }
    public void setWidth(final double WIDTH) { width = WIDTH; }

    public double getHeight() { return height; }
    public void setHeight(final double HEIGHT) { height = HEIGHT; }

    public Color getFillColor() { return (null != fillColor) ? fillColor.getValue() : _fillColor; }
    public void setFillColor(final Color COLOR) {
        if (null == fillColor) {
            _fillColor = COLOR;
        } else {
            fillColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> fillColorProperty() {
        if( null == fillColor){
            fillColor = new ObjectPropertyBase<Color>(_fillColor) {
                @Override public Object getBean() { return ParetoBar.this; }
                @Override public String getName() { return "fillColor"; }
            };
            _fillColor = null;
        }
        return fillColor;
    }

    public Double getValue() {
        if (null == value) { updateValue(); }
        return value;
    }

    private void sortAscending() {
        bars.sort((c1,c2)  -> {
            if(c1.getValue() == c2.getValue()) { return 0; }
            return c1.getValue() > c2.getValue() ? -1 : 1;
        });
    }

    public String getName() { return name; }

    public ArrayList<ParetoBar> getBars() { return bars; }

    /**
     * Adds a new bar to the list of bars and updates the value field
     * @param bar The ParetoBar to add to this bar
     */
    public void addBar(ParetoBar bar) {
        if(null == bars) { bars = new ArrayList<>(); }
        bars.add(bar);
        sortAscending();
        updateValue();
    }
    public void addBar(String name, double value) { addBar(new ParetoBar(name, value)); }
}
