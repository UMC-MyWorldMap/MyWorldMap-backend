package umc.TripPiece.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import umc.TripPiece.converter.UserConverter;
import umc.TripPiece.domain.User;
import umc.TripPiece.repository.UserRepository;
import umc.TripPiece.web.dto.request.UserRequestDto;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /* 회원가입 */
    @Override
    @Transactional
    public User signUp(@Valid UserRequestDto.SignUpDto request) {


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
        return userRepository.save(newUser);
    }

    /* 로그인 */
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

        return user;
    }

    @Transactional
    public User update(UserRequestDto.UpdateDto request) {
        String email = request.getEmail();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 계정입니다.");
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

        return user;

    }

    private boolean isPasswordMatch(String rawPassword, String encodedPassword){
        // password 일치 여부 확인
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}