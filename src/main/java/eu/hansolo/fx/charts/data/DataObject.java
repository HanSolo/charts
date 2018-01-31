/*
 * Copyright (c) 2018 by Gerrit Grunwald
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

package eu.hansolo.fx.charts.data;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.Map;


public interface DataObject {
    String getName();

    Paint getFill();
    void  setFill(final Paint FILL);

    Color getStroke();
    void  setStroke(final Color STROKE);

    Map<String, ChartItem> getProperties();
}
