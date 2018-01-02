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

package eu.hansolo.fx.charts.tools;


import eu.hansolo.fx.charts.event.EventType;
import eu.hansolo.fx.charts.event.LocationEvent;
import eu.hansolo.fx.charts.event.LocationEventListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Created by hansolo on 20.11.16.
 */
public class Location {
    public enum CardinalDirection {
        N("North", 348.75, 11.25),
        NNE("North North-East", 11.25, 33.75),
        NE("North-East", 33.75, 56.25),
        ENE("East North-East", 56.25, 78.75),
        E("East", 78.75, 101.25),
        ESE("East South-East", 101.25, 123.75),
        SE("South-East", 123.75, 146.25),
        SSE("South South-East", 146.25, 168.75),
        S("South", 168.75, 191.25),
        SSW("South South-West", 191.25, 213.75),
        SW("South-West", 213.75, 236.25),
        WSW("West South-West", 236.25, 258.75),
        W("West", 258.75, 281.25),
        WNW("West North-West", 281.25, 303.75),
        NW("North-West", 303.75, 326.25),
        NNW("North North-West", 326.25, 348.75);

        public String direction;
        public double from;
        public double to;

        CardinalDirection(final String DIRECTION, final double FROM, final double TO) {
            direction = DIRECTION;
            from      = FROM;
            to        = TO;
        }
    }
    private final LocationEvent         UPDATE_EVENT = new LocationEvent(Location.this, EventType.UPDATE);
    private String                      _name;
    private StringProperty              name;
    private Instant                     _timestamp;
    private ObjectProperty<Instant>     timestamp;
    private double                      _latitude;
    private DoubleProperty              latitude;
    private double                      _longitude;
    private DoubleProperty              longitude;
    private double                      _altitude;
    private DoubleProperty              altitude;
    private String                      _info;
    private StringProperty              info;
    private Color                       _color;
    private ObjectProperty<Color>       color;
    private int                         zoomLevel;
    private List<LocationEventListener> listenerList;
    private EventHandler<MouseEvent>    mouseEnterHandler;
    private EventHandler<MouseEvent>    mousePressHandler;
    private EventHandler<MouseEvent>    mouseReleaseHandler;
    private EventHandler<MouseEvent>    mouseExitHandler;


    // ******************** Constructors **************************************
    public Location() {
        this(0, 0, 0, Instant.now(), "", "", Color.BLUE);
    }
    public Location(final double LATITUDE, final double LONGITUDE) {
        this(LATITUDE, LONGITUDE, 0, Instant.now(), "", "", Color.BLUE);
    }
    public Location(final double LATITUDE, final double LONGITUDE, final String NAME) {
        this(LATITUDE, LONGITUDE, 0, Instant.now() ,NAME, "", Color.BLUE);
    }
    public Location(final double LATITUDE, final double LONGITUDE, final String NAME, final Color COLOR) {
        this(LATITUDE, LONGITUDE, 0, Instant.now() ,NAME, "", COLOR);
    }
    public Location(final double LATITUDE, final double LONGITUDE, final String NAME, final String INFO) {
        this(LATITUDE, LONGITUDE, 0, Instant.now() ,NAME, INFO, Color.BLUE);
    }
    public Location(final double LATITUDE, final double LONGITUDE, final String NAME, final String INFO, final Color COLOR) {
        this(LATITUDE, LONGITUDE, 0, Instant.now() ,NAME, INFO, COLOR);
    }
    public Location(final double LATITUDE, final double LONGITUDE, final double ALTITUDE, final String NAME) {
        this(LATITUDE, LONGITUDE, ALTITUDE, Instant.now(), NAME, "", Color.BLUE);
    }
    public Location(final double LATITUDE, final double LONGITUDE, final double ALTITUDE, final Instant TIMESTAMP, final String NAME) {
        this(LATITUDE, LONGITUDE, ALTITUDE, TIMESTAMP, NAME, "", Color.BLUE);
    }
    public Location(final double LATITUDE, final double LONGITUDE, final double ALTITUDE, final Instant TIMESTAMP, final String NAME, final String INFO, final Color COLOR) {
        _name        = NAME;
        _latitude    = LATITUDE;
        _longitude   = LONGITUDE;
        _altitude    = ALTITUDE;
        _timestamp   = TIMESTAMP;
        _info        = INFO;
        _color       = COLOR;
        zoomLevel    = 15;
        listenerList = new CopyOnWriteArrayList<>();
    }


    // ******************** Methods *******************************************
    public String getName() { return null == name ? _name : name.get(); }
    public void setName(final String NAME) {
        if (null == name) {
            _name = NAME;
            fireLocationEvent(UPDATE_EVENT);
        } else {
            name.set(NAME);
        }
    }
    public StringProperty nameProperty() {
        if (null == name) {
            name = new StringPropertyBase(_name) {
                @Override protected void invalidated() { fireLocationEvent(UPDATE_EVENT); }
                @Override public Object getBean() { return Location.this; }
                @Override public String getName() { return "name"; }
            };
            _name = null;
        }
        return name;
    }

    public Instant getTimestamp() { return null == timestamp ? _timestamp : timestamp.get(); }
    public long getTimestampInSeconds() { return getTimestamp().getEpochSecond(); }
    public void setTimestamp(final Instant TIMESTAMP) {
        if (null == timestamp) {
            _timestamp = TIMESTAMP;
            fireLocationEvent(UPDATE_EVENT);
        } else {
            timestamp.set(TIMESTAMP);
        }
    }
    public ObjectProperty<Instant> timestampProperty() {
        if (null == timestamp) {
            timestamp = new ObjectPropertyBase<Instant>(_timestamp) {
                @Override protected void invalidated() { fireLocationEvent(UPDATE_EVENT); }
                @Override public Object getBean() { return Location.this; }
                @Override public String getName() { return "timestamp"; }
            };
            _timestamp = null;
        }
        return timestamp;
    }

    public double getLatitude() { return null == latitude ? _latitude : latitude.get(); }
    public void setLatitude(final double LATITUDE) {
        if (null == latitude) {
            _latitude = LATITUDE;
            fireLocationEvent(UPDATE_EVENT);
        } else {
            latitude.set(LATITUDE);
        }
    }
    public DoubleProperty latitudeProperty() {
        if (null == latitude) {
            latitude = new DoublePropertyBase(_latitude) {
                @Override protected void invalidated() { fireLocationEvent(UPDATE_EVENT); }
                @Override public Object getBean() { return Location.this; }
                @Override public String getName() { return "latitude"; }
            };
        }
        return latitude;
    }

    public double getLongitude() { return null == longitude ? _longitude : longitude.get(); }
    public void setLongitude(final double LONGITUDE) {
        if (null == longitude) {
            _longitude = LONGITUDE;
            fireLocationEvent(UPDATE_EVENT);
        } else {
            longitude.set(LONGITUDE);
        }
    }
    public DoubleProperty longitudeProperty() {
        if (null == longitude) {
            longitude = new DoublePropertyBase(_longitude) {
                @Override protected void invalidated() { fireLocationEvent(UPDATE_EVENT); }
                @Override public Object getBean() { return Location.this; }
                @Override public String getName() { return "longitude"; }
            };
        }
        return longitude;
    }

    public double getAltitude() { return null == altitude ? _altitude : altitude.get(); }
    public void setAltitude(final double ALTITUDE) {
        if (null == altitude) {
            _altitude = ALTITUDE;
            fireLocationEvent(UPDATE_EVENT);
        } else {
            altitude.set(ALTITUDE);
        }
    }
    public DoubleProperty altitudeProperty() {
        if (null == altitude) {
            altitude = new DoublePropertyBase(_altitude) {
                @Override protected void invalidated() { fireLocationEvent(UPDATE_EVENT); }
                @Override public Object getBean() { return Location.this; }
                @Override public String getName() { return "altitude"; }
            };
        }
        return altitude;
    }

    public String getInfo() { return null == info ? _info : info.get(); }
    public void setInfo(final String INFO) {
        if (null == info) {
            _info = INFO;
            fireLocationEvent(UPDATE_EVENT);
        } else {
            info.set(INFO);
        }
    }
    public StringProperty infoProperty() {
        if (null == info) {
            info = new StringPropertyBase(_info) {
                @Override protected void invalidated() { fireLocationEvent(UPDATE_EVENT); }
                @Override public Object getBean() { return Location.this; }
                @Override public String getName() { return "info"; }
            };
            _info = null;
        }
        return info;
    }

    public Color getColor() { return null == color ? _color : color.get(); }
    public void setColor(final Color COLOR) {
        if (null == color) {
            _color = COLOR;
            fireLocationEvent(UPDATE_EVENT);
        } else {
            color.set(COLOR);
        }
    }
    public ObjectProperty<Color> colorProperty() {
        if (null == color) {
            color = new ObjectPropertyBase<Color>(_color) {
                @Override protected void invalidated() { fireLocationEvent(UPDATE_EVENT); }
                @Override public Object getBean() { return Location.this; }
                @Override public String getName() { return "color"; }
            };
            _color = null;
        }
        return color;
    }

    public ZonedDateTime getZonedDateTime() { return getZonedDateTime(ZoneId.systemDefault()); }
    public ZonedDateTime getZonedDateTime(final ZoneId ZONE_ID) { return ZonedDateTime.ofInstant(getTimestamp(), ZONE_ID); }

    public int getZoomLevel() { return zoomLevel; }
    public void setZoomLevel(final int LEVEL) {
        zoomLevel = Helper.clamp(0, 17, LEVEL);
        fireLocationEvent(UPDATE_EVENT);
    }

    public void update(final double LATITUDE, final double LONGITUDE) { set(LATITUDE, LONGITUDE); }

    public void set(final double LATITUDE, final double LONGITUDE) {
        set(LATITUDE, LONGITUDE, getAltitude(), getTimestamp(), getInfo());
    }
    public void set(final double LATITUDE, final double LONGITUDE, final double ALTITUDE, final Instant TIMESTAMP) {
        set(LATITUDE, LONGITUDE, ALTITUDE, TIMESTAMP, getInfo());
    }
    public void set(final double LATITUDE, final double LONGITUDE, final double ALTITUDE, final Instant TIMESTAMP, final String INFO) {
        if (null == latitude)  { _latitude  = LATITUDE;  } else { latitude.set(LATITUDE);   }
        if (null == longitude) { _longitude = LONGITUDE; } else { longitude.set(LONGITUDE); }
        if (null == altitude)  { _altitude  = ALTITUDE;  } else { altitude.set(ALTITUDE);   }
        if (null == timestamp) { _timestamp = TIMESTAMP; } else { timestamp.set(TIMESTAMP); }
        if (null == info)      { _info      = INFO;      } else { info.set(INFO);           }
        fireLocationEvent(UPDATE_EVENT);
    }
    public void set(final Location LOCATION) {
        if (null == name)      { _name      = LOCATION.getName();      } else { name.set(LOCATION.getName());           }
        if (null == latitude)  { _latitude  = LOCATION.getLatitude();  } else { latitude.set(LOCATION.getLatitude());   }
        if (null == longitude) { _longitude = LOCATION.getLongitude(); } else { longitude.set(LOCATION.getLongitude()); }
        if (null == altitude)  { _altitude  = LOCATION.getAltitude();  } else { altitude.set(LOCATION.getAltitude());   }
        if (null == timestamp) { _timestamp = LOCATION.getTimestamp(); } else { timestamp.set(LOCATION.getTimestamp()); }
        if (null == info)      { _info      = LOCATION.getInfo();      } else { info.set(LOCATION.getInfo());           }
        if (null == color)     { _color     = LOCATION.getColor();     } else { color.set(LOCATION.getColor());         }
        zoomLevel = LOCATION.getZoomLevel();
        fireLocationEvent(UPDATE_EVENT);
    }

    public double getDistanceTo(final Location LOCATION) { return calcDistanceInMeter(this, LOCATION); }

    public boolean isWithinRangeOf(final Location LOCATION, final double METERS) { return getDistanceTo(LOCATION) < METERS; }

    public double calcDistanceInMeter(final Location P1, final Location P2) {
        return calcDistanceInMeter(P1.getLatitude(), P1.getLongitude(), P2.getLatitude(), P2.getLongitude());
    }
    public double calcDistanceInKilometer(final Location P1, final Location P2) {
        return calcDistanceInMeter(P1, P2) / 1000.0;
    }
    public double calcDistanceInMeter(final double LAT_1, final double LON_1, final double LAT_2, final double LON_2) {
        final double EARTH_RADIUS      = 6_371_000; // m
        final double LAT_1_RADIANS     = Math.toRadians(LAT_1);
        final double LAT_2_RADIANS     = Math.toRadians(LAT_2);
        final double DELTA_LAT_RADIANS = Math.toRadians(LAT_2-LAT_1);
        final double DELTA_LON_RADIANS = Math.toRadians(LON_2-LON_1);

        final double A = Math.sin(DELTA_LAT_RADIANS * 0.5) * Math.sin(DELTA_LAT_RADIANS * 0.5) + Math.cos(LAT_1_RADIANS) * Math.cos(LAT_2_RADIANS) * Math.sin(DELTA_LON_RADIANS * 0.5) * Math.sin(DELTA_LON_RADIANS * 0.5);
        final double C = 2 * Math.atan2(Math.sqrt(A), Math.sqrt(1-A));

        final double DISTANCE = EARTH_RADIUS * C;

        return DISTANCE;
    }

    public double getAltitudeDifferenceInMeter(final Location LOCATION) { return (getAltitude() - LOCATION.getAltitude()); }

    public double getBearingTo(final Location LOCATION) {
        return calcBearingInDegree(getLatitude(), getLongitude(), LOCATION.getLatitude(), LOCATION.getLongitude());
    }
    public double getBearingTo(final double LATITUDE, final double LONGITUDE) {
        return calcBearingInDegree(getLatitude(), getLongitude(), LATITUDE, LONGITUDE);
    }

    public boolean isZero() { return Double.compare(getLatitude(), 0) == 0 && Double.compare(getLongitude(), 0) == 0; }

    public double calcBearingInDegree(final double LAT_1, final double LON_1, final double LAT_2, final double LON_2) {
        double lat1     = Math.toRadians(LAT_1);
        double lon1     = Math.toRadians(LON_1);
        double lat2     = Math.toRadians(LAT_2);
        double lon2     = Math.toRadians(LON_2);
        double deltaLon = lon2 - lon1;
        double deltaPhi = Math.log(Math.tan(lat2 * 0.5 + Math.PI * 0.25) / Math.tan(lat1 * 0.5 + Math.PI * 0.25));
        if (Math.abs(deltaLon) > Math.PI) {
            if (deltaLon > 0) {
                deltaLon = -(2.0 * Math.PI - deltaLon);
            } else {
                deltaLon = (2.0 * Math.PI + deltaLon);
            }
        }
        double bearing = (Math.toDegrees(Math.atan2(deltaLon, deltaPhi)) + 360.0) % 360.0;
        return bearing;
    }

    public String getCardinalDirectionFromBearing(final double BEARING) {
        double bearing = BEARING % 360.0;
        for (CardinalDirection cardinalDirection : CardinalDirection.values()) {
            if (Double.compare(bearing, cardinalDirection.from) >= 0 && Double.compare(bearing, cardinalDirection.to) < 0) {
                return cardinalDirection.direction;
            }
        }
        return "";
    }


    // ******************** Event Handling ************************************
    public void setOnLocationEvent(final LocationEventListener LISTENER) { addLocationEventListener(LISTENER); }
    public void addLocationEventListener(final LocationEventListener LISTENER) { if (!listenerList.contains(LISTENER)) listenerList.add(LISTENER); }
    public void removeLocationEventListener(final LocationEventListener LISTENER) { if (listenerList.contains(LISTENER)) listenerList.remove(LISTENER); }

    public void fireLocationEvent(final LocationEvent EVENT) {
        for (LocationEventListener listener : listenerList) { listener.onLocationEvent(EVENT); }
    }


    public EventHandler<MouseEvent> getMouseEnterHandler() { return mouseEnterHandler; }
    public void setMouseEnterHandler(final EventHandler<MouseEvent> HANDLER) { mouseEnterHandler = HANDLER; }

    public EventHandler<MouseEvent> getMousePressHandler() { return mousePressHandler; }
    public void setMousePressHandler(final EventHandler<MouseEvent> HANDLER) { mousePressHandler = HANDLER; }

    public EventHandler<MouseEvent> getMouseReleaseHandler() { return mouseReleaseHandler; }
    public void setMouseReleaseHandler(final EventHandler<MouseEvent> HANDLER) { mouseReleaseHandler = HANDLER;  }

    public EventHandler<MouseEvent> getMouseExitHandler() { return mouseExitHandler; }
    public void setMouseExitHandler(final EventHandler<MouseEvent> HANDLER) { mouseExitHandler = HANDLER; }


    // ******************** Misc **********************************************
    @Override public boolean equals(final Object OBJECT) {
        if (OBJECT instanceof Location) {
            final Location LOCATION = (Location) OBJECT;
            return (Double.compare(getLatitude(), LOCATION.getLatitude()) == 0 &&
                    Double.compare(getLongitude(), LOCATION.getLongitude()) == 0 &&
                    Double.compare(getAltitude(), LOCATION.getAltitude()) == 0);
        } else {
            return false;
        }
    }

    @Override public String toString() {
        return new StringBuilder().append("{")
                                  .append("  \"name     \":\"").append(getName()).append("\",\n")
                                  .append("  \"timestamp\":\"").append(getTimestamp()).append("\",\n")
                                  .append("  \"latitude \":").append(getLatitude()).append(",\n")
                                  .append("  \"longitude\":").append(getLongitude()).append(",\n")
                                  .append("  \"altitude \":").append(getAltitude()).append(",\n")
                                  .append("  \"info     \":\"").append(getInfo()).append("\",\n")
                                  .append("  \"color    \":\"").append(Helper.colorToWeb(getColor())).append("\",\n")
                                  .append("  \"zoomLevel\":").append(getZoomLevel()).append("\n")
                                  .append("}")
                                  .toString();
    }

    @Override public int hashCode() {
        int result;
        long temp;
        result = name != null ? name.hashCode() : 0;
        temp = Double.doubleToLongBits(getLatitude());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getLongitude());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getAltitude());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
