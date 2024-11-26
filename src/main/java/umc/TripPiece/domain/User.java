package umc.TripPiece.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import umc.TripPiece.domain.common.BaseEntity;
import umc.TripPiece.domain.enums.Gender;
import umc.TripPiece.domain.enums.UserMethod;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id", unique = true, nullable = false)
    private Long id;

    @Column(length = 20)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column
    private String password;

    @Setter
    @Column(nullable = false, length = 20)
    private String nickname;

    @Setter
    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Setter
    @Column(nullable = false, length = 20)
    private String birth;
    
    @Column
    @Setter
    private String profileImg;

    @Setter
    @Column(nullable = false, length = 30)
    private String country;

    @Column (nullable = false)
    private Boolean gpsConsent;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private UserMethod method;

    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean isPublic;

    @OneToOne
    @JoinColumn(name = "uuid_id")
    private Uuid uuid;

    @Setter
    @Column(name = "refresh_token")
    private String refreshToken;

    // 일반 가입자와 소셜 로그인 회원 구분을 위한 providerId
    @Column(name = "provider_id", unique = true)
    private Long providerId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<TripPiece> tripPieces = new ArrayList<>();

    public Uuid getUuid() {
        return this.uuid;
    }
}