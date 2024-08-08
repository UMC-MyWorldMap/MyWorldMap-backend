package umc.TripPiece.domain.enums;

public enum UserMethod {
    GENERAL, KAKAO;

    public enum Color {
        COLOR_1("6744FF"),
        COLOR_2("FFB40F"),
        COLOR_3("25CEC1"),
        COLOR_4("FD2D69");

        private final String colorCode;

        Color(String colorCode) {
            this.colorCode = colorCode;
        }

        public String getColorCode() {
            return colorCode;
        }

        @Override
        public String toString() {
            return colorCode;
        }
    }
}
