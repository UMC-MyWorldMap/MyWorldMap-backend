package umc.TripPiece.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Getter
@Setter
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

    @Column(name = "color")
    private String color; // 변경: String 타입으로 수정하여 요청된 hex 값을 저장 가능

    @ElementCollection
    @CollectionTable(name = "map_colors", joinColumns = @JoinColumn(name = "map_id"))
    @Column(name = "colors")
    private List<String> colors = new ArrayList<>(); // 다중 색상 처리

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;
}
