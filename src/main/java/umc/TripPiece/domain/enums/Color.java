package umc.TripPiece.domain.enums;

import java.util.HashMap;
import java.util.Map;

public enum Color {
    BLUE("6744FF"),
    YELLOW("FFB40F"),
    CYAN("25CEC1"),
    RED("FD2D69");

    private final String code;
    private static final Map<String, Color> BY_CODE = new HashMap<>();

    static {
        for (Color c : values()) {
            BY_CODE.put(c.code.toUpperCase(), c); // 대소문자 구분 없도록 처리
        }
    }

    Color(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Color fromHex(String hexCode) {
        if (hexCode == null || hexCode.isBlank()) {
            throw new IllegalArgumentException("Color code cannot be null or empty.");
        }

        Color color = BY_CODE.get(hexCode.toUpperCase());
        if (color == null) {
            throw new IllegalArgumentException("Unknown color code: " + hexCode);
        }

        return color;
    }
}
