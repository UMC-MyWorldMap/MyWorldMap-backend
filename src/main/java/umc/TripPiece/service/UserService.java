package umc.TripPiece.service;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;
import umc.TripPiece.domain.User;
import umc.TripPiece.web.dto.request.UserRequestDto;
import umc.TripPiece.web.dto.response.UserResponseDto;

public interface UserService {

    /* 회원가입 */
    User signUp(UserRequestDto.SignUpDto request, MultipartFile profileImg);

    /* 카카오 회원가입 */
    User signUpKakao(UserRequestDto.SignUpKakaoDto request, MultipartFile profileImg);

    /* 로그인 */
    User login(UserRequestDto.LoginDto request);

    /* 카카오 로그인 */
    User loginKakao(UserRequestDto.LoginKakaoDto request);

    /* 토큰 재발급 */
    User reissue(UserRequestDto.ReissueDto request);

    /* 로그아웃 */
    void logout(Long userId);

    /* 회원탈퇴 */
    void withdrawal(Long userId);

    /* 유저 저장 */
    User save(User user);

    /* 유저 수정 */
    User update(UserRequestDto.@Valid UpdateDto request, String token, MultipartFile profileImg);

    /* 프로필 조회 */
    UserResponseDto.ProfileDto getProfile(String token);
}
