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

import eu.hansolo.fx.charts.Symbol;
import eu.hansolo.fx.charts.event.EventType;
import eu.hansolo.fx.charts.event.ItemEvent;
import eu.hansolo.fx.charts.event.ItemEventListener;
import eu.hansolo.fx.charts.tools.Helper;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class ChartItem implements Item, Comparable<ChartItem> {
    private final ItemEvent               UPDATE_EVENT   = new ItemEvent(EventType.UPDATE, ChartItem.this);
    private final ItemEvent               FINISHED_EVENT = new ItemEvent(EventType.FINISHED, ChartItem.this);
    private       List<ItemEventListener> listenerList   = new CopyOnWriteArrayList<>();
    private       String                  _name;
    private       StringProperty          name;
    private       double                  _value;
    private       DoubleProperty          value;
    private       double                  oldValue;
    private       Color                   _fillColor;
    private       ObjectProperty<Color>   fillColor;
    private       Color                   _strokeColor;
    private       ObjectProperty<Color>   strokeColor;
    private       Color                   _textColor;
    private       ObjectProperty<Color>   textColor;
    private       Instant                 _timestamp;
    private       ObjectProperty<Instant> timestamp;
    private       Symbol                  _symbol;
    private       ObjectProperty<Symbol>  symbol;
    private       boolean                 _animated;
    private       BooleanProperty         animated;
    private       long                    animationDuration;
    private       DoubleProperty          currentValue;
    private       Timeline                timeline;


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
        _name             = NAME;
        _value            = VALUE;
        oldValue          = 0;
        _fillColor        = FILL_COLOR;
        _strokeColor      = STROKE_COLOR;
        _textColor        = TEXT_COLOR;
        _timestamp        = TIMESTAMP;
        _symbol           = Symbol.NONE;
        _animated         = ANIMATED;
        currentValue      = new DoublePropertyBase(_value) {
            @Override protected void invalidated() {
                oldValue = getValue();
                setValue(get());
                fireItemEvent(UPDATE_EVENT);
            }
            @Override public Object getBean() { return ChartItem.this; }
            @Override public String getName() { return "currentValue"; }
        };
        timeline          = new Timeline();
        animationDuration = ANIMATION_DURATION;

        timeline.setOnFinished(e -> fireItemEvent(FINISHED_EVENT));
    }


    // ******************** Methods *******************************************
    @Override public String getName() { return null == name ? _name : name.get(); }
    public void setName(final String NAME) {
        if (null == name) {
            _name = NAME;
            fireItemEvent(UPDATE_EVENT);
        } else {
            name.set(NAME);
        }
    }
    public StringProperty nameProperty() {
        if (null == name) {
            name = new StringPropertyBase(_name) {
                @Override protected void invalidated() { fireItemEvent(UPDATE_EVENT); }
                @Override public Object getBean() { return ChartItem.this; }
                @Override public String getName() { return "name"; }
            };
            _name = null;
        }
        return name;
    }

    public double getValue() { return null == value ? _value : value.get(); }
    public void setValue(final double VALUE) {
        if (null == value) {
            if (isAnimated()) {
                timeline.stop();
                KeyValue kv1 = new KeyValue(currentValue, _value, Interpolator.EASE_BOTH);
                KeyValue kv2 = new KeyValue(currentValue, VALUE, Interpolator.EASE_BOTH);
                KeyFrame kf1 = new KeyFrame(Duration.ZERO, kv1);
                KeyFrame kf2 = new KeyFrame(Duration.millis(animationDuration), kv2);
                timeline.getKeyFrames().setAll(kf1, kf2);
                timeline.play();
            } else {
                oldValue = _value;
                _value = VALUE;
                fireItemEvent(FINISHED_EVENT);
            }
        } else {
            value.set(VALUE);
        }
    }
    public DoubleProperty valueProperty() {
        if (null == value) {
            value = new DoublePropertyBase(_value) {
                @Override public void set(final double VALUE) {
                    oldValue = get();
                    super.set(VALUE);
                }
                @Override protected void invalidated() {
                    if (isAnimated()) {
                        timeline.stop();
                        KeyValue kv1 = new KeyValue(currentValue, getOldValue(), Interpolator.EASE_BOTH);
                        KeyValue kv2 = new KeyValue(currentValue, get(), Interpolator.EASE_BOTH);
                        KeyFrame kf1 = new KeyFrame(Duration.ZERO, kv1);
                        KeyFrame kf2 = new KeyFrame(Duration.millis(animationDuration), kv2);
                        timeline.getKeyFrames().setAll(kf1, kf2);
                        timeline.play();
                    } else {
                        fireItemEvent(FINISHED_EVENT);
                    }
                }
                @Override public Object getBean() { return ChartItem.this; }
                @Override public String getName() { return "value"; }
            };
        }
        return value;
    }

    public double getOldValue() { return oldValue; }

    @Override public Color getFillColor() { return null == fillColor ? _fillColor : fillColor.get(); }
    public void setFillColor(final Color COLOR) {
        if (null == fillColor) {
            _fillColor = COLOR;
            fireItemEvent(UPDATE_EVENT);
        } else {
            fillColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> fillColorProperty() {
        if (null == fillColor) {
            fillColor = new ObjectPropertyBase<Color>(_fillColor) {
                @Override protected void invalidated() { fireItemEvent(UPDATE_EVENT); }
                @Override public Object getBean() { return ChartItem.this; }
                @Override public String getName() { return "fillColor"; }
            };
            _fillColor = null;
        }
        return fillColor;
    }

    public Color getStrokeColor() { return null == strokeColor ? _strokeColor : strokeColor.get(); }
    public void setStrokeColor(final Color COLOR) {
        if (null == strokeColor) {
            _strokeColor = COLOR;
            fireItemEvent(UPDATE_EVENT);
        } else {
            strokeColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> strokeColorProperty() {
        if (null == strokeColor) {
            strokeColor = new ObjectPropertyBase<Color>(_strokeColor) {
                @Override protected void invalidated() { fireItemEvent(UPDATE_EVENT); }
                @Override public Object getBean() { return ChartItem.this; }
                @Override public String getName() { return "strokeColor"; }
            };
            _strokeColor = null;
        }
        return strokeColor;
    }

    public Color getTextColor() { return null == textColor ? _textColor : textColor.get(); }
    public void setTextColor(final Color COLOR) {
        if (null == textColor) {
            _textColor = COLOR;
            fireItemEvent(UPDATE_EVENT);
        } else {
            textColor.set(COLOR);
        }
    }
    public ObjectProperty<Color> textColorProperty() {
        if (null == textColor) {
            textColor = new ObjectPropertyBase<Color>(_textColor) {
                @Override protected void invalidated() { fireItemEvent(UPDATE_EVENT); }
                @Override public Object getBean() { return ChartItem.this; }
                @Override public String getName() { return "textColor"; }
            };
            _textColor = null;
        }
        return textColor;
    }

    @Override public Symbol getSymbol() { return null == symbol ? _symbol : symbol.get(); }
    @Override public void setSymbol(final Symbol SYMBOL) {
        if (null == symbol) {
            _symbol = SYMBOL;
            fireItemEvent(UPDATE_EVENT);
        } else {
            symbol.set(SYMBOL);
        }
    }
    public ObjectProperty<Symbol> symbolProperty() {
        if (null == symbol) {
            symbol = new ObjectPropertyBase<Symbol>(_symbol) {
                @Override protected void invalidated() { fireItemEvent(UPDATE_EVENT); }
                @Override public Object getBean() {  return ChartItem.this;  }
                @Override public String getName() {  return "symbol";  }
            };
            _symbol = null;
        }
        return symbol;
    }

    public Instant getTimestamp() { return null == timestamp ? _timestamp : timestamp.get(); }
    public void setTimestamp(final Instant TIMESTAMP) {
        if (null == timestamp) {
            _timestamp = TIMESTAMP;
            fireItemEvent(UPDATE_EVENT);
        } else {
            timestamp.set(TIMESTAMP);
        }
    }
    public ObjectProperty<Instant> timestampProperty() {
        if (null == timestamp) {
            timestamp = new ObjectPropertyBase<Instant>(_timestamp) {
                @Override protected void invalidated() { fireItemEvent(UPDATE_EVENT); }
                @Override public Object getBean() { return ChartItem.this; }
                @Override public String getName() { return "timestamp"; }
            };
            _timestamp = null;
        }
        return timestamp;
    }

    public ZonedDateTime getTimestampAdDateTime() { return getTimestampAsDateTime(ZoneId.systemDefault()); }
    public ZonedDateTime getTimestampAsDateTime(final ZoneId ZONE_ID) { return ZonedDateTime.ofInstant(getTimestamp(), ZONE_ID); }

    public LocalDate getTimestampAsLocalDate() { return getTimestampAsLocalDate(ZoneId.systemDefault()); }
    public LocalDate getTimestampAsLocalDate(final ZoneId ZONE_ID) { return getTimestampAsDateTime(ZONE_ID).toLocalDate(); }

    public boolean isAnimated() { return null == animated ? _animated : animated.get(); }
    public void setAnimated(final boolean ANIMATED) {
        if (null == animated) {
            _animated = ANIMATED;
        }  else {
            animated.set(ANIMATED);
        }
    }
    public BooleanProperty animatedProperty() {
        if (null == animated) {
            animated = new BooleanPropertyBase(_animated) {
                @Override public Object getBean() { return ChartItem.this; }
                @Override public String getName() { return "animated"; }
            };
        }
        return animated;
    }

    public long getAnimationDuration() { return animationDuration; }
    public void setAnimationDuration(final long DURATION) { animationDuration = Helper.clamp(10, 10000, DURATION); }

    @Override public String toString() {
        return new StringBuilder().append("{\n")
                                  .append("  \"name\":").append(name).append(",\n")
                                  .append("  \"value\":").append(value).append(",\n")
                                  .append("  \"fillColor\":").append(fillColor.toString().replace("0x", "#")).append(",\n")
                                  .append("  \"strokeColor\":").append(strokeColor.toString().replace("0x", "#")).append(",\n")
                                  .append("  \"timestamp\":").append(getTimestamp().toEpochMilli()).append(",\n")
                                  .append("}")
                                  .toString();
    }

    @Override public int compareTo(final ChartItem ITEM) { return Double.compare(getValue(), ITEM.getValue()); }


    // ******************** Event Handling ************************************
    public void setOnItemEvent(final ItemEventListener LISTENER) { addItemEventListener(LISTENER); }
    public void addItemEventListener(final ItemEventListener LISTENER) { if (!listenerList.contains(LISTENER)) listenerList.add(LISTENER); }
    public void removeItemEventListener(final ItemEventListener LISTENER) { if (listenerList.contains(LISTENER)) listenerList.remove(LISTENER); }

    public void fireItemEvent(final ItemEvent EVENT) {
        for (ItemEventListener listener : listenerList) { listener.onItemEvent(EVENT); }
    }
}
