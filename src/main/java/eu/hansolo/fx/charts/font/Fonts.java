package eu.hansolo.fx.charts.font;

import javafx.scene.text.Font;


/**
 * Created by hansolo on 19.07.17.
 */
public class Fonts {
    private static final String LATO_LIGHT_NAME;
    private static final String LATO_REGULAR_NAME;
    private static final String LATO_BOLD_NAME;

    private static String latoLightName;
    private static String latoRegularName;
    private static String latoBoldName;


    static {
        try {
            latoLightName              = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/fx/charts/font/Lato-Lig.otf"), 10).getName();
            latoRegularName            = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/fx/charts/font/Lato-Reg.otf"), 10).getName();
            latoBoldName               = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/fx/charts/font/Lato-Bol.otf"), 10).getName();
        } catch (Exception exception) { }
        LATO_LIGHT_NAME               = latoLightName;
        LATO_REGULAR_NAME             = latoRegularName;
        LATO_BOLD_NAME                = latoBoldName;
    }


    // ******************** Methods *******************************************
    public static Font latoLight(final double SIZE) { return new Font(LATO_LIGHT_NAME, SIZE); }
    public static Font latoRegular(final double SIZE) { return new Font(LATO_REGULAR_NAME, SIZE); }
    public static Font latoBold(final double SIZE) { return new Font(LATO_BOLD_NAME, SIZE); }
}
