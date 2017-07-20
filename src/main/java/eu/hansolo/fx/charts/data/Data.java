package eu.hansolo.fx.charts.data;

import eu.hansolo.fx.charts.Symbol;
import javafx.scene.paint.Color;


public interface Data {

    String getName();

    Color getColor();

    Symbol getSymbol();
    void setSymbol(Symbol symbol);
}
