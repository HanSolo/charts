/*
 * Copyright (c) 2016 by Gerrit Grunwald
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

package eu.hansolo.fx.charts.world;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by hansolo on 01.12.16.
 */
public class CountryRegion implements CRegion {
    private String        name;
    private List<Country> countries;


    // ******************** Constructors **************************************
    public CountryRegion(final String NAME, final Country... COUNTRIES) {
        name      = NAME;
        countries = new ArrayList<>(COUNTRIES.length);
        for (Country country : COUNTRIES) { countries.add(country); }
    }


    // ******************** Methods *******************************************
    @Override public String name() { return name; }

    @Override public List<Country> getCountries() { return countries; }

    @Override public void setColor(final Color COLOR) {
        for (Country country : getCountries()) { country.setColor(COLOR); }
    }
}
