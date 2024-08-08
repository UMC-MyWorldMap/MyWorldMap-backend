package umc.TripPiece.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.TripPiece.domain.common.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Emoji extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String emoji;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_piece_id")
    private TripPiece tripPiece;

}
