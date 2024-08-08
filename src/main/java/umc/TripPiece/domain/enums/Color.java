package umc.TripPiece.domain.enums;

import java.util.HashMap;
import java.util.Map;

public enum Color {
    BLUE("6744FF"),
    YELLOW("FFB40F"),
    CYAN("25CEC1"),
    RED("FD2D69");

    private String code;
    private static final Map<String, Color> BY_CODE = new HashMap<>();

    static {
        for (Color c : values()) {
            BY_CODE.put(c.code, c);
        }
    }

    Color(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Color fromString(String code) {
        return BY_CODE.get(code);
    }
}