package umc.TripPiece.domain;

import jakarta.persistence.*;
import lombok.*;
import umc.TripPiece.domain.common.BaseEntity;
import umc.TripPiece.domain.enums.Gender;
import umc.TripPiece.domain.enums.UserMethod;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id", unique = true, nullable = false)
    private Long id;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 20)
    private String nickname;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false, length = 20)
    private String birth;
    
    @Column
    private String profileImg;

    @Column(nullable = false, length = 30)
    private String country;

    @Column (nullable = false)
    private Boolean gpsConsent;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private UserMethod method;

    @Column(nullable = false)
    private Boolean isPublic;

    @Setter
    @Column(name = "refresh_token")
    private String refreshToken;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<TripPiece> tripPieces = new ArrayList<>();
}