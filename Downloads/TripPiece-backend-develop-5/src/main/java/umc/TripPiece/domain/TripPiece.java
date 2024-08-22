package umc.TripPiece.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import umc.TripPiece.domain.common.BaseEntity;
import umc.TripPiece.domain.enums.Category;
//import umc.TripPiece.domain.enums.Category;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TripPiece extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'MEMO'")
    private Category category;

    @Column(nullable = false)
    private String description;

    @Column
    @ColumnDefault("true")
    private Boolean travel_contain;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_id")
    private Travel travel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "tripPiece", cascade = CascadeType.ALL)
    private List<Picture> pictures = new ArrayList<>();

    @OneToMany(mappedBy = "tripPiece", cascade = CascadeType.ALL)
    private List<Video> videos = new ArrayList<>();

    @OneToMany(mappedBy = "tripPiece", cascade = CascadeType.ALL)
    private List<Emoji> emojis = new ArrayList<>();

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setTravel(Travel travel) {
        this.travel = travel;
    }
}
