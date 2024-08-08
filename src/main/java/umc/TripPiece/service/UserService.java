package umc.TripPiece.service;

import umc.TripPiece.domain.User;
import umc.TripPiece.web.dto.request.UserRequestDto;

public interface UserService {
    /* 회원가입 */
    User signUp(UserRequestDto.SignUpDto request);

    /* 로그인 */
    User login(UserRequestDto.LoginDto request);

    /* 토큰 재발급 */
    User reissue(UserRequestDto.ReissueDto request);

    /* 로그아웃 */
    void logout(Long user_id);

    User save(User user);
}
