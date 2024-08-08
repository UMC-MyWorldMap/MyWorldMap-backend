package umc.TripPiece.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import umc.TripPiece.domain.enums.Color;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Map {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visit_id")
    private Long visitId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "country_code", nullable = false)
    private String countryCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "color", nullable = false)
    private Color color;
}
