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

import java.util.ArrayList;
import java.util.List;


/**
 * authors: Michael L\u00E4uchli, MLaeuchli (github)
 *          Stefan Mettler, orizion (github)
 */
public class ParetoModel {

    private String               title;
    private ArrayList<ParetoBar> data;


    // ******************** Constructors **************************************
    /**
     *
     * @param title The Title displayed for the ParetoChart
     * @param data The data to be displayed
     */
    public ParetoModel(String title, List<ParetoBar> data) {
        this.title = title;

        this.data = new ArrayList<>(data);
    }
    /**
     *
     * @param data The data to be displayed
     */
    public ParetoModel(List<ParetoBar> data){
        this("Pareto Chart", data);
    }
    /**
     *
     */
    public ParetoModel() {
        this("Pareto Chart",new ArrayList<>());
    }
    /**
     *
     * @param model Copy constructor
     */
    public ParetoModel(ParetoModel model) {
        this.title = model.getTitle();
        this.data = model.getData();
    }


    // ******************** Methods *******************************************
    /**
     *
     * @return The list of ParetoBars contained in this Model
     */
    public ArrayList<ParetoBar> getData() { return data; }
    /**
     *
     * @param DATA Sets the passed List as the new List oif ParetoBars in the Model
     */
    public void setData(final List<ParetoBar> DATA) { data = new ArrayList<>(DATA); }

    public String getTitle() { return title; }
    public void setTitle(final String TITLE) { this.title = TITLE; }

    /**
     *
     * @return Returns the smallest value among the ParetoBars
     */
    public double getMin() { return this.getData().stream().mapToDouble(ParetoBar::getValue).min().orElse(0); }

    /**
     * @return Returns the biggest value among the ParetoBars
     */
    public double getMax() { return this.getData().stream().mapToDouble(ParetoBar::getValue).max().orElse(0); }

    /**
     *
     * @return Returns the total value of all the ParetoBars
     */
    public double getTotal() { return this.getData().stream().mapToDouble(ParetoBar::getValue).sum(); }
}