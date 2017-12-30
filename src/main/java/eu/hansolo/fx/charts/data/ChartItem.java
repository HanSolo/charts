/*
 * Copyright (c) 2017 by Gerrit Grunwald
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

import eu.hansolo.fx.charts.event.ChartItemEvent;
import eu.hansolo.fx.charts.event.ChartItemEvent.EventType;
import eu.hansolo.fx.charts.event.ChartItemEventListener;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class ChartItem implements Comparable<ChartItem> {
    private final ChartItemEvent               UPDATE_EVENT   = new ChartItemEvent(EventType.UPDATE, ChartItem.this);
    private final ChartItemEvent               FINISHED_EVENT = new ChartItemEvent(EventType.FINISHED, ChartItem.this);
    private       List<ChartItemEventListener> listenerList   = new CopyOnWriteArrayList<>();
    private       String                       name;
    private       double                       value;
    private       double                       oldValue;
    private       Color                        fillColor;
    private       Color                        strokeColor;
    private       Color                        textColor;
    private       Instant                      timestamp;
    private       boolean                      animated;
    private       long                         animationDuration;
    private       DoubleProperty               currentValue;
    private       Timeline                     timeline;


    // ******************** Constructors **************************************
    public ChartItem() {
        this("", 0, Color.rgb(233, 30, 99), Color.TRANSPARENT, Color.BLACK, Instant.now(), true, 800);
    }
    public ChartItem(final String NAME) {
        this(NAME, 0, Color.rgb(233, 30, 99), Color.TRANSPARENT, Color.BLACK, Instant.now(), true, 800);
    }
    public ChartItem(double VALUE) {
        this("", VALUE, Color.rgb(233, 30, 99), Color.TRANSPARENT, Color.BLACK, Instant.now(), true, 800);
    }
    public ChartItem(final double VALUE, final Instant TIMESTAMP) {
        this("", VALUE, Color.rgb(233, 30, 99), Color.TRANSPARENT, Color.BLACK, TIMESTAMP, true, 800);
    }
    public ChartItem(final double VALUE, final Color FILL_COLOR) {
        this("", VALUE, FILL_COLOR, Color.TRANSPARENT, Color.BLACK, Instant.now(), true, 800);
    }
    public ChartItem(final String NAME, final Color FILL_COLOR) {
        this(NAME, 0, FILL_COLOR, Color.TRANSPARENT, Color.BLACK, Instant.now(), true, 800);
    }
    public ChartItem(final String NAME, final double VALUE) {
        this(NAME, VALUE, Color.rgb(233, 30, 99), Color.TRANSPARENT, Color.BLACK, Instant.now(), true, 800);
    }
    public ChartItem(final String NAME, final double VALUE, final Instant TIMESTAMP) {
        this(NAME, VALUE, Color.rgb(233, 30, 99), Color.TRANSPARENT, Color.BLACK, TIMESTAMP, true, 800);
    }
    public ChartItem(final String NAME, final double VALUE, final Color FILL_COLOR) {
        this(NAME, VALUE, FILL_COLOR, Color.TRANSPARENT, Color.BLACK, Instant.now(), true, 800);
    }
    public ChartItem(final String NAME, final double VALUE, final Color FILL_COLOR, final Color TEXT_COLOR) {
        this(NAME, VALUE, FILL_COLOR, Color.TRANSPARENT, TEXT_COLOR, Instant.now(), true, 800);
    }
    public ChartItem(final String NAME, final double VALUE, final Color FILL_COLOR, final Instant TIMESTAMP) {
        this(NAME, VALUE, FILL_COLOR, Color.TRANSPARENT, Color.BLACK, TIMESTAMP, true, 800);
    }
    public ChartItem(final String NAME, final double VALUE, final Color FILL_COLOR, final Color TEXT_COLOR, final Instant TIMESTAMP) {
        this(NAME, VALUE, FILL_COLOR, Color.TRANSPARENT, TEXT_COLOR, TIMESTAMP, true, 800);
    }
    public ChartItem(final String NAME, final double VALUE, final Color FILL_COLOR, final Instant TIMESTAMP, final boolean ANIMATED, final long ANIMATION_DURATION) {
        this(NAME, VALUE, FILL_COLOR, Color.TRANSPARENT, Color.BLACK, TIMESTAMP, ANIMATED, ANIMATION_DURATION);
    }
    public ChartItem(final String NAME, final double VALUE, final Color FILL_COLOR, final Color TEXT_COLOR, final Instant TIMESTAMP, final boolean ANIMATED, final long ANIMATION_DURATION) {
        this(NAME, VALUE, FILL_COLOR, Color.TRANSPARENT, TEXT_COLOR, TIMESTAMP, ANIMATED, ANIMATION_DURATION);
    }
    public ChartItem(final String NAME, final double VALUE, final Color FILL_COLOR, final Color STROKE_COLOR, final Color TEXT_COLOR, final Instant TIMESTAMP, final boolean ANIMATED, final long ANIMATION_DURATION) {
        name              = NAME;
        value             = VALUE;
        oldValue          = 0;
        fillColor         = FILL_COLOR;
        strokeColor       = STROKE_COLOR;
        textColor         = TEXT_COLOR;
        timestamp         = TIMESTAMP;
        currentValue      = new DoublePropertyBase(value) {
            @Override protected void invalidated() {
                oldValue = value;
                value = get();
                fireChartItemEvent(UPDATE_EVENT);
            }
            @Override public Object getBean() { return ChartItem.this; }
            @Override public String getName() { return "currentValue"; }
        };
        timeline          = new Timeline();
        animated          = ANIMATED;
        animationDuration = ANIMATION_DURATION;

        timeline.setOnFinished(e -> fireChartItemEvent(FINISHED_EVENT));
    }


    // ******************** Methods *******************************************
    public String getName() { return name; }
    public void setName(final String NAME) {
        name = NAME;
        fireChartItemEvent(UPDATE_EVENT);
    }

    public double getValue() { return value; }
    public void setValue(final double VALUE) {
        if (animated) {
            timeline.stop();
            KeyValue kv1 = new KeyValue(currentValue, value, Interpolator.EASE_BOTH);
            KeyValue kv2 = new KeyValue(currentValue, VALUE, Interpolator.EASE_BOTH);
            KeyFrame kf1 = new KeyFrame(Duration.ZERO, kv1);
            KeyFrame kf2 = new KeyFrame(Duration.millis(animationDuration), kv2);
            timeline.getKeyFrames().setAll(kf1, kf2);
            timeline.play();
        } else {
            oldValue = value;
            value = VALUE;
            fireChartItemEvent(FINISHED_EVENT);
        }
    }

    public double getOldValue() { return oldValue; }

    public Color getFillColor() { return fillColor; }
    public void setFillColor(final Color COLOR) {
        fillColor = COLOR;
        fireChartItemEvent(UPDATE_EVENT);
    }

    public Color getStrokeColor() { return strokeColor; }
    public void setStrokeColor(final Color COLOR) {
        strokeColor = COLOR;
        fireChartItemEvent(UPDATE_EVENT);
    }

    public Color getTextColor() { return textColor; }
    public void setTextColor(final Color COLOR) {
        textColor = COLOR;
        fireChartItemEvent(UPDATE_EVENT);
    }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(final Instant TIMESTAMP) {
        timestamp = TIMESTAMP;
        fireChartItemEvent(UPDATE_EVENT);
    }

    public ZonedDateTime getTimestampAdDateTime() { return getTimestampAsDateTime(ZoneId.systemDefault()); }
    public ZonedDateTime getTimestampAsDateTime(final ZoneId ZONE_ID) { return ZonedDateTime.ofInstant(timestamp, ZONE_ID); }

    public LocalDate getTimestampAsLocalDate() { return getTimestampAsLocalDate(ZoneId.systemDefault()); }
    public LocalDate getTimestampAsLocalDate(final ZoneId ZONE_ID) { return getTimestampAsDateTime(ZONE_ID).toLocalDate(); }

    public boolean isAnimated() { return animated; }
    public void setAnimated(final boolean ANIMATED) { animated = ANIMATED; }

    public long getAnimationDuration() { return animationDuration; }
    public void setAnimationDuration(final long DURATION) { animationDuration = clamp(10, 10000, DURATION); }

    @Override public String toString() {
        return new StringBuilder().append("{\n")
                                  .append("  \"name\":").append(name).append(",\n")
                                  .append("  \"value\":").append(value).append(",\n")
                                  .append("  \"fillColor\":").append(fillColor.toString().replace("0x", "#")).append(",\n")
                                  .append("  \"strokeColor\":").append(strokeColor.toString().replace("0x", "#")).append(",\n")
                                  .append("  \"timestamp\":").append(timestamp.toEpochMilli()).append(",\n")
                                  .append("}")
                                  .toString();
    }

    @Override public int compareTo(final ChartItem DATA) { return Double.compare(getValue(), DATA.getValue()); }

    private long clamp(final long MIN, final long MAX, final long VALUE) {
        if (VALUE < MIN) return MIN;
        if (VALUE > MAX) return MAX;
        return VALUE;
    }


    // ******************** Event Handling ************************************
    public void setOnChartItemEvent(final ChartItemEventListener LISTENER) { addChartItemEventListener(LISTENER); }
    public void addChartItemEventListener(final ChartItemEventListener LISTENER) { if (!listenerList.contains(LISTENER)) listenerList.add(LISTENER); }
    public void removeChartItemEventListener(final ChartItemEventListener LISTENER) { if (listenerList.contains(LISTENER)) listenerList.remove(LISTENER); }

    public void fireChartItemEvent(final ChartItemEvent EVENT) {
        for (ChartItemEventListener listener : listenerList) { listener.onChartItemEvent(EVENT); }
    }
}
