package umc.TripPiece.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import umc.TripPiece.aws.s3.AmazonS3Manager;
import umc.TripPiece.converter.UserConverter;
import umc.TripPiece.domain.Travel;
import umc.TripPiece.domain.User;
import umc.TripPiece.domain.Uuid;
import umc.TripPiece.domain.jwt.JWTUtil;
import umc.TripPiece.repository.TravelRepository;
import umc.TripPiece.repository.UserRepository;
import umc.TripPiece.repository.UuidRepository;
import umc.TripPiece.web.dto.request.UserRequestDto;
import umc.TripPiece.web.dto.response.UserResponseDto;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final UuidRepository uuidRepository;
    private final TravelRepository travelRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;
    private final AmazonS3Manager s3Manager;

    /* 일반 회원가입 */
    @Override
    @Transactional
    public User signUp(@Valid UserRequestDto.SignUpDto request, MultipartFile profileImg) {

        // 프로필 이미지 일련번호 생성
        String uuid = UUID.randomUUID().toString();
        Uuid savedUuid = uuidRepository.save(Uuid.builder()
                .uuid(uuid).build());

        // 이메일 중복 확인
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        });

        // 닉네임 중복 확인
        userRepository.findByNickname(request.getNickname()).ifPresent(user -> {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        });

        // 비밀번호 암호화
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User newUser = UserConverter.toUser(request, hashedPassword);

        // 프로필 사진 설정
        String profileImgUrl;

        if(profileImg == null) {
            profileImgUrl = null;
        } else {
            profileImgUrl = s3Manager.uploadFile(s3Manager.generateTripPieceKeyName(savedUuid), profileImg);
        }
        newUser.setProfileImg(profileImgUrl);

        return userRepository.save(newUser);
    }

    /* 카카오 회원가입 */
    @Override
    @Transactional
    public User signUpKakao(@Valid UserRequestDto.SignUpKakaoDto request, MultipartFile profileImg) {

        // 프로필 이미지 일련번호 생성
        String uuid = UUID.randomUUID().toString();
        Uuid savedUuid = uuidRepository.save(Uuid.builder()
                .uuid(uuid).build());

        String profileImgUrl = s3Manager.uploadFile(s3Manager.generateTripPieceKeyName(savedUuid), profileImg);

        // providerId 중복 확인
        userRepository.findByProviderId(request.getProviderId()).ifPresent(user -> {
            throw new IllegalArgumentException("이미 회원가입된 providerId입니다.");
        });
        
        // 이메일 중복 확인
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        });

        // 닉네임 중복 확인
        userRepository.findByNickname(request.getNickname()).ifPresent(user -> {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        });

        User newUser = UserConverter.toUser(request);

        // 프로필 사진 설정
        newUser.setProfileImg(profileImgUrl);

        return userRepository.save(newUser);
    }

    /* 일반 로그인 */
    @Override
    @Transactional
    public User login(UserRequestDto.LoginDto request){
        String email = request.getEmail();
        String password = request.getPassword();

        // email 계정 조회
        User user = userRepository.findByEmail(email).orElse(null);

        // 이메일이 존재하지 않을 경우
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 계정입니다.");
        }

        // 비밀번호가 일치하지 않을 경우
        if (!isPasswordMatch(password, user.getPassword())) {
            throw new IllegalArgumentException("이메일 혹은 비밀번호를 확인해주세요.");
        }

        // 로그인 성공시 토큰 생성
        String refreshToken = jwtUtil.createRefreshToken(email);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return user;
    }

    /* 카카오 로그인 */
    @Override
    @Transactional
    public User loginKakao(UserRequestDto.LoginKakaoDto request) {
        String email = request.getEmail();
        Long providerId = request.getProviderId();

        // 카카오 계정 조회
        User user = userRepository.findByEmailAndProviderId(email, providerId).orElse(null);

        // 계정이 존재하지 않을 경우
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 카카오 계정입니다.");
        }

        // 로그인 성공시 토큰 생성
        String refreshToken = jwtUtil.createRefreshToken(email);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return user;
    }

    private boolean isPasswordMatch(String rawPassword, String encodedPassword){
        // password 일치 여부 확인
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /* 토큰 재발급 */
    @Override
    @Transactional
    public User reissue(UserRequestDto.ReissueDto request) {
        String refreshToken = request.getRefreshToken();

        User user = userRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 refreshToken입니다."));

        if(!refreshToken.equals(user.getRefreshToken())) {
            throw new IllegalArgumentException("유효하지 않은 refreshToken입니다.");
        };

        user.setRefreshToken(refreshToken);
        return user;
    }

    /* 로그아웃 */
    @Override
    @Transactional
    public void logout(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 계정입니다.")
        );

        user.setRefreshToken(null);
        userRepository.save(user);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User update(UserRequestDto.UpdateDto request, String token, MultipartFile profileImg) {

        // 프로필 이미지 일련번호 생성
        String uuid = UUID.randomUUID().toString();
        Uuid savedUuid = uuidRepository.save(Uuid.builder()
                .uuid(uuid).build());

        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userRepository.findById(userId).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 계정입니다.")
        );

        String profileImgUrl;

        if(profileImg == null) {
            profileImgUrl = user.getProfileImg();
        } else {
            profileImgUrl = s3Manager.uploadFile(s3Manager.generateTripPieceKeyName(savedUuid), profileImg);
        }

        if(request.getNickname() != null){
            user.updatenickname(request.getNickname());
        }
        if(request.getGender() != null){
            user.updategender(request.getGender());
        }
        if(request.getBirth() != null){
            user.updatebirth(request.getBirth());
        }
        if(request.getCountry() != null){
            user.updatecountry(request.getCountry());
        }

        user.setProfileImg(profileImgUrl);

        return user;

    }

    @Override
    public UserResponseDto.ProfileDto getProfile(String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userRepository.findById(userId).orElseThrow(() ->
                new IllegalArgumentException("존재하지 않는 계정입니다.")
        );
        List<Travel> travels =  travelRepository.findByUserId(userId);
        Integer travelNum = travels.size();

        return UserConverter.toProfileDto(user, travelNum);
    }
}