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

    User save(User user);

    /* 수정하기 */
    User update(UserRequestDto.@Valid UpdateDto request, String token, MultipartFile profileImg);

    /* 프로필 조회 */
    UserResponseDto.ProfileDto getProfile(String token);

}
