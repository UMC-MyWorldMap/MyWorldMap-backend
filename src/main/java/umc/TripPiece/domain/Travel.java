package umc.TripPiece.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import umc.TripPiece.domain.common.BaseEntity;
import umc.TripPiece.domain.enums.TravelStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Travel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "travel_id")
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;

    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean travelOpen;
    private Long likeCount;

    @Setter
    private String thumbnail;

    @Setter
    @ColumnDefault("0")
    private Integer memoNum;

    @Setter
    @ColumnDefault("0")
    private Integer pictureNum;

    @Setter
    @ColumnDefault("0")
    private Integer videoNum;

    @Setter
    @Enumerated(EnumType.STRING)
    private TravelStatus status;

    @OneToMany(mappedBy = "travel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TripPiece> tripPieces = new ArrayList<>();
}
