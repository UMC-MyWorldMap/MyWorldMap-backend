package umc.TripPiece.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import umc.TripPiece.apiPayload.code.status.ErrorStatus;
import umc.TripPiece.apiPayload.exception.handler.BadRequestHandler;
import umc.TripPiece.apiPayload.exception.handler.NotFoundHandler;
import umc.TripPiece.apiPayload.exception.handler.UserHandler;
import umc.TripPiece.aws.s3.AmazonS3Manager;
import umc.TripPiece.converter.UserConverter;
import umc.TripPiece.domain.Travel;
import umc.TripPiece.domain.User;
import umc.TripPiece.domain.Uuid;
import umc.TripPiece.domain.enums.Category;
import umc.TripPiece.domain.enums.UserMethod;
import umc.TripPiece.domain.jwt.JWTUtil;
import umc.TripPiece.repository.TravelRepository;
import umc.TripPiece.repository.UserRepository;
import umc.TripPiece.repository.UuidRepository;
import umc.TripPiece.web.dto.request.UserRequestDto;
import umc.TripPiece.web.dto.response.UserResponseDto;

import java.util.List;
import java.util.Optional;
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
            throw new UserHandler(ErrorStatus.EMAIL_DUPLICATE);
        });

        // 닉네임 중복 확인
        userRepository.findByNickname(request.getNickname()).ifPresent(user -> {
            throw new UserHandler(ErrorStatus.NICKNAME_DUPLICATE);
        });

        // 비밀번호 암호화
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User newUser = UserConverter.toUser(request, hashedPassword);

        // 프로필 사진 설정
        String profileImgUrl;

        if(profileImg == null) {
            profileImgUrl = null;
        } else {
            profileImgUrl = s3Manager.uploadFile(s3Manager.generateTripPieceKeyName(savedUuid), profileImg, Category.PICTURE);
        }
        newUser.setProfileImg(profileImgUrl);

        return userRepository.save(newUser);
    }

    /* 소셜 회원가입 */
    @Override
    @Transactional
    public User signUpSocial(@Valid UserMethod method, UserRequestDto.SignUpSocialDto request, MultipartFile profileImg) {

        // 프로필 이미지 일련번호 생성
        String uuid = UUID.randomUUID().toString();
        Uuid savedUuid = uuidRepository.save(Uuid.builder()
                .uuid(uuid).build());

        String profileImgUrl = s3Manager.uploadFile(s3Manager.generateTripPieceKeyName(savedUuid), profileImg, Category.PICTURE);

        // providerId 중복 확인
        userRepository.findByProviderId(request.getProviderId()).ifPresent(user -> {
            throw new UserHandler(ErrorStatus.PROVIDER_ID_DUPLICATE);
        });
        
        // 이메일 중복 확인
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new UserHandler(ErrorStatus.EMAIL_DUPLICATE);
        });

        // 닉네임 중복 확인
        userRepository.findByNickname(request.getNickname()).ifPresent(user -> {
            throw new UserHandler(ErrorStatus.NICKNAME_DUPLICATE);
        });

        User newUser = UserConverter.toSocialUser(request, method);

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
            throw new NotFoundHandler(ErrorStatus.NOT_FOUND_USER);
        }

        // 비밀번호가 일치하지 않을 경우
        if (!isPasswordMatch(password, user.getPassword())) {
            throw new UserHandler(ErrorStatus.INVALID_PASSWORD);
        }

        // 로그인 성공시 토큰 생성
        String refreshToken = jwtUtil.createRefreshToken(email);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return user;
    }

    /* 소셜 로그인 */
    @Override
    @Transactional
    public User loginSocial(UserRequestDto.LoginSocialDto request, UserMethod method) {
        String email = request.getEmail();
        Long providerId = request.getProviderId();

        // 소셜 로그인 계정 조회
        Optional<User> optionalUser;
        if (method == UserMethod.KAKAO) {
            optionalUser = userRepository.findByEmailAndProviderId(email, providerId);
        } else if (method == UserMethod.APPLE) {
            optionalUser = userRepository.findByEmailAndProviderId(email, providerId);
        } else {
            throw new BadRequestHandler(ErrorStatus.PLATFORM_BAD_REQUEST);
        }

        // 계정이 존재하지 않을 경우
        String errorMessage = "존재하지 않는 " + method.name() + " 계정입니다.";
        User user = optionalUser.orElseThrow(() ->
                new NotFoundHandler(ErrorStatus.SOCIAL_NOT_FOUND, errorMessage));

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
            new UserHandler(ErrorStatus.NOT_FOUND_USER)
        );

        user.setRefreshToken(null);
        userRepository.save(user);
    }

    /* 회원탈퇴 */
    @Override
    @Transactional
    public void withdrawal(Long userId) {
        // 유저 정보 조회
        User user = userRepository.findById(userId).orElseThrow(() ->
            new UserHandler(ErrorStatus.NOT_FOUND_USER)
        );

        // 유저와 관련된 여행 정보 삭제
        List<Travel> travels = travelRepository.findByUserId(userId);
        travelRepository.deleteAll(travels);

        // 유저의 UUID(프로필 이미지 관련) 삭제
        Uuid uuid = user.getUuid();
        if (uuid != null) {
            uuidRepository.delete(uuid);
        }

        // 유저의 프로필 이미지 삭제 (S3에서 파일 삭제)
        if (user.getProfileImg() != null) {
            s3Manager.deleteFile(user.getProfileImg());
        }

        user.setRefreshToken(null);
        userRepository.save(user);

        // 유저 삭제
        userRepository.delete(user);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User update(UserRequestDto.UpdateDto request, String token, MultipartFile profileImg) {
        final long MAX_UPLOAD_SIZE = 5 * 1024 * 1024;

        // 프로필 이미지 일련번호 생성
        String uuid = UUID.randomUUID().toString();
        Uuid savedUuid = uuidRepository.save(Uuid.builder()
                .uuid(uuid).build());

        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserHandler(ErrorStatus.NOT_FOUND_USER)
        );

        String profileImgUrl;

        if (profileImg != null) {
            // 파일 크기 검사
            if (profileImg.getSize() > MAX_UPLOAD_SIZE) {
                throw new UserHandler(ErrorStatus.PAYLOAD_TOO_LARGE);
            }
            try {
                profileImgUrl = s3Manager.uploadFile(s3Manager.generateTripPieceKeyName(savedUuid), profileImg, Category.PICTURE);
            } catch (Exception e) {
                throw new UserHandler(ErrorStatus.INVALID_PROFILE_IMAGE);
            }
        } else {
            profileImgUrl = user.getProfileImg();
        }

        if(request.getNickname() != null){
            user.setNickname(request.getNickname());
        }
        if(request.getGender() != null){
            user.setGender(request.getGender());
        }
        if(request.getBirth() != null){
            user.setBirth(request.getBirth());
        }
        if(request.getCountry() != null){
            user.setCountry(request.getCountry());
        }

        user.setProfileImg(profileImgUrl);

        return user;
    }

    @Override
    public UserResponseDto.ProfileDto getProfile(String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userRepository.findById(userId).orElseThrow(() ->
            new UserHandler(ErrorStatus.NOT_FOUND_USER)
        );
        List<Travel> travels =  travelRepository.findByUserId(userId);
        Integer travelNum = travels.size();

        return UserConverter.toProfileDto(user, travelNum);
    }
}