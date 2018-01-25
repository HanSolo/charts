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
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class ChartItem implements Item, Comparable<ChartItem> {
    private final ItemEvent               UPDATE_EVENT   = new ItemEvent(ChartItem.this, EventType.UPDATE);
    private final ItemEvent               FINISHED_EVENT = new ItemEvent(ChartItem.this, EventType.FINISHED);
    private       List<ItemEventListener> listenerList   = new CopyOnWriteArrayList<>();
    private       String                  _name;
    private       StringProperty          name;
    private       double                  _value;
    private       DoubleProperty          value;
    private       double                  oldValue;
    private       Color                   _fill;
    private       ObjectProperty<Color>   fill;
    private       Color                   _stroke;
    private       ObjectProperty<Color>   stroke;
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
        this("", 0, Color.rgb(233, 30, 99), Color.TRANSPARENT, Color.BLACK, Instant.now(), false, 800);
    }
    public ChartItem(final String NAME) {
        this(NAME, 0, Color.rgb(233, 30, 99), Color.TRANSPARENT, Color.BLACK, Instant.now(), false, 800);
    }
    public ChartItem(double VALUE) {
        this("", VALUE, Color.rgb(233, 30, 99), Color.TRANSPARENT, Color.BLACK, Instant.now(), false, 800);
    }
    public ChartItem(final double VALUE, final Instant TIMESTAMP) {
        this("", VALUE, Color.rgb(233, 30, 99), Color.TRANSPARENT, Color.BLACK, TIMESTAMP, false, 800);
    }
    public ChartItem(final double VALUE, final Color FILL_COLOR) {
        this("", VALUE, FILL_COLOR, Color.TRANSPARENT, Color.BLACK, Instant.now(), false, 800);
    }
    public ChartItem(final String NAME, final Color FILL_COLOR) {
        this(NAME, 0, FILL_COLOR, Color.TRANSPARENT, Color.BLACK, Instant.now(), false, 800);
    }
    public ChartItem(final String NAME, final double VALUE) {
        this(NAME, VALUE, Color.rgb(233, 30, 99), Color.TRANSPARENT, Color.BLACK, Instant.now(), false, 800);
    }
    public ChartItem(final String NAME, final double VALUE, final Instant TIMESTAMP) {
        this(NAME, VALUE, Color.rgb(233, 30, 99), Color.TRANSPARENT, Color.BLACK, TIMESTAMP, true, 800);
    }
    public ChartItem(final String NAME, final double VALUE, final Color FILL) {
        this(NAME, VALUE, FILL, Color.TRANSPARENT, Color.BLACK, Instant.now(), false, 800);
    }
    public ChartItem(final String NAME, final double VALUE, final Color FILL, final Color TEXT_COLOR) {
        this(NAME, VALUE, FILL, Color.TRANSPARENT, TEXT_COLOR, Instant.now(), false, 800);
    }
    public ChartItem(final String NAME, final double VALUE, final Color FILL, final Instant TIMESTAMP) {
        this(NAME, VALUE, FILL, Color.TRANSPARENT, Color.BLACK, TIMESTAMP, false, 800);
    }
    public ChartItem(final String NAME, final double VALUE, final Color FILL, final Color TEXT_COLOR, final Instant TIMESTAMP) {
        this(NAME, VALUE, FILL, Color.TRANSPARENT, TEXT_COLOR, TIMESTAMP, false, 800);
    }
    public ChartItem(final String NAME, final double VALUE, final Color FILL, final Instant TIMESTAMP, final boolean ANIMATED, final long ANIMATION_DURATION) {
        this(NAME, VALUE, FILL, Color.TRANSPARENT, Color.BLACK, TIMESTAMP, ANIMATED, ANIMATION_DURATION);
    }
    public ChartItem(final String NAME, final double VALUE, final Color FILL, final Color TEXT_COLOR, final Instant TIMESTAMP, final boolean ANIMATED, final long ANIMATION_DURATION) {
        this(NAME, VALUE, FILL, Color.TRANSPARENT, TEXT_COLOR, TIMESTAMP, ANIMATED, ANIMATION_DURATION);
    }
    public ChartItem(final String NAME, final double VALUE, final Color FILL, final Color STROKE, final Color TEXT_COLOR, final Instant TIMESTAMP, final boolean ANIMATED, final long ANIMATION_DURATION) {
        _name             = NAME;
        _value            = VALUE;
        oldValue          = 0;
        _fill             = FILL;
        _stroke           = STROKE;
        _textColor        = TEXT_COLOR;
        _timestamp        = TIMESTAMP;
        _symbol           = Symbol.NONE;
        _animated         = ANIMATED;
        currentValue      = new DoublePropertyBase(_value) {
            @Override protected void invalidated() {
                oldValue = ChartItem.this.getValue();
                ChartItem.this.setValue(get());
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
                if (timeline.getCurrentRate() > 0) {
                    // Only update values if timeline is already running
                    oldValue = _value;
                    _value   = VALUE;
                } else {
                    // Start timeline only if it is NOT already running
                    oldValue = _value;
                    _value = VALUE;
                    timeline.stop();
                    KeyValue kv1 = new KeyValue(currentValue, oldValue, Interpolator.EASE_BOTH);
                    KeyValue kv2 = new KeyValue(currentValue, VALUE, Interpolator.EASE_BOTH);
                    KeyFrame kf1 = new KeyFrame(Duration.ZERO, kv1);
                    KeyFrame kf2 = new KeyFrame(Duration.millis(animationDuration), kv2);
                    timeline.getKeyFrames().setAll(kf1, kf2);
                    timeline.play();
                }
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
                        if (Double.compare(timeline.getCurrentRate(), 0.0) == 0) {
                            // Only start timeline if it is NOT already running
                            timeline.stop();
                            KeyValue kv1 = new KeyValue(currentValue, oldValue, Interpolator.EASE_BOTH);
                            KeyValue kv2 = new KeyValue(currentValue, get(), Interpolator.EASE_BOTH);
                            KeyFrame kf1 = new KeyFrame(Duration.ZERO, kv1);
                            KeyFrame kf2 = new KeyFrame(Duration.millis(animationDuration), kv2);
                            timeline.getKeyFrames().setAll(kf1, kf2);
                            timeline.play();
                        }
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

    @Override public Color getFill() { return null == fill ? _fill : fill.get(); }
    public void setFill(final Color FILL) {
        if (null == fill) {
            _fill = FILL;
            fireItemEvent(UPDATE_EVENT);
        } else {
            fill.set(FILL);
        }
    }
    public ObjectProperty<Color> fillProperty() {
        if (null == fill) {
            fill = new ObjectPropertyBase<Color>(_fill) {
                @Override protected void invalidated() { fireItemEvent(UPDATE_EVENT); }
                @Override public Object getBean() { return ChartItem.this; }
                @Override public String getName() { return "fill"; }
            };
            _fill = null;
        }
        return fill;
    }

    public Color getStroke() { return null == stroke ? _stroke : stroke.get(); }
    public void setStroke(final Color STROKE) {
        if (null == stroke) {
            _stroke = STROKE;
            fireItemEvent(UPDATE_EVENT);
        } else {
            stroke.set(STROKE);
        }
    }
    public ObjectProperty<Color> strokeProperty() {
        if (null == stroke) {
            stroke = new ObjectPropertyBase<Color>(_stroke) {
                @Override protected void invalidated() { fireItemEvent(UPDATE_EVENT); }
                @Override public Object getBean() { return ChartItem.this; }
                @Override public String getName() { return "stroke"; }
            };
            _stroke = null;
        }
        return stroke;
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
    public void setTimestamp(final ZonedDateTime TIMESTAMP) { setTimestamp(TIMESTAMP.toInstant()); }
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
                                  .append("  \"name\":").append(getName()).append(",\n")
                                  .append("  \"value\":").append(getValue()).append(",\n")
                                  .append("  \"timestamp\":").append(getTimestamp().toEpochMilli()).append(",\n")
                                  .append("}")
                                  .toString();
    }

    @Override public int compareTo(final ChartItem ITEM) { return Double.compare(getValue(), ITEM.getValue()); }

    @Override public boolean equals(Object o) {
        if (o == this) { return true; }
        if (!(o instanceof ChartItem)) { return false; }

        ChartItem item = (ChartItem) o;

        return item.getName().equals(getName()) &&
               item.getTimestamp().equals(getTimestamp()) &&
               Double.compare(item.getValue(), getValue()) == 0;
    }


    // ******************** Event Handling ************************************
    public void setOnItemEvent(final ItemEventListener LISTENER) { addItemEventListener(LISTENER); }
    public void addItemEventListener(final ItemEventListener LISTENER) { if (!listenerList.contains(LISTENER)) listenerList.add(LISTENER); }
    public void removeItemEventListener(final ItemEventListener LISTENER) { if (listenerList.contains(LISTENER)) listenerList.remove(LISTENER); }

    public void fireItemEvent(final ItemEvent EVENT) {
        for (ItemEventListener listener : listenerList) { listener.onItemEvent(EVENT); }
    }
}
