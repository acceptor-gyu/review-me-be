package reviewme.be.config.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import reviewme.be.user.dto.response.UserProfileResponse;
import reviewme.be.user.entity.User;
import reviewme.be.user.exception.InvalidJWTException;
import reviewme.be.user.exception.NoValidBearerFormatException;
import reviewme.be.user.service.JWTService;
import reviewme.be.user.service.UserService;
import reviewme.be.util.exception.NotLoggedInUserException;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultInterceptor implements HandlerInterceptor {

    private final JWTService jwtService;
    private final UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String header = request.getHeader("Authorization");

        if (header == null) {
            throw new NotLoggedInUserException("Authorization header가 존재하지 않습니다.");
        }

        if (request.getHeader("Authorization").split(" ").length != 2) {
            throw new NoValidBearerFormatException("Bearer 토큰이 존재하지 않습니다.");
        }

        String jwt = request.getHeader("Authorization").split(" ")[1];

        if (jwtService.validateJwtIsManipulated(jwt)) {
            throw new InvalidJWTException("조작된 토큰입니다.");
        }

        if (jwtService.validateJwtIsExpired(jwt)) {
            throw new InvalidJWTException("유효 기간이 만료된 토큰입니다.");
        }

        UserProfileResponse loggedInUser = jwtService.extractUserFromJwt(jwt, UserProfileResponse.class);
        User user = userService.getUserById(loggedInUser.getId());
        request.setAttribute("user", user);

        return true;
    }

}
