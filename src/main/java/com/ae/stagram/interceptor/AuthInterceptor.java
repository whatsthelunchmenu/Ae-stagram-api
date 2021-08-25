package com.ae.stagram.interceptor;

import com.ae.stagram.dto.UserDto;
import com.ae.stagram.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final FirebaseAuth firebaseAuth;

    private final UserService userService;

    private final String tokenKey = "Authorization";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {

        String token = request.getHeader(tokenKey);
        Optional.ofNullable(token)
            .orElseThrow(() -> new RuntimeException("토큰이 없습니다."));

        FirebaseToken firebaseToken = firebaseAuth.verifyIdToken(token);

        request.setAttribute("UserDto",
            UserDto.builder()
                .uuid(firebaseToken.getUid())
                .displayName(firebaseToken.getName())
                .email(firebaseToken.getEmail())
                .build());

        return true;
    }
}
