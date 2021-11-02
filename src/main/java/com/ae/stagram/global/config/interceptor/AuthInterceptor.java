package com.ae.stagram.global.config.interceptor;

import com.ae.stagram.global.exception.AestagramException;
import com.ae.stagram.global.exception.ExceptionCode;
import com.ae.stagram.web.dto.user.UserDto;
import com.ae.stagram.global.exception.TokenNotFoundException;
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

    private final String tokenKey = "Authorization";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        String token = request.getHeader(tokenKey);
        Optional.ofNullable(token)
                .orElseThrow(() -> new AestagramException(ExceptionCode.TOKEN_NOT_FOUND));

        FirebaseToken firebaseToken = checkToken(token);

        request.setAttribute("firebaseUser",
                UserDto.builder()
                        .uuid(firebaseToken.getUid())
                        .displayName(firebaseToken.getName())
                        .email(firebaseToken.getEmail())
                        .build());

        return true;
    }

    public FirebaseToken checkToken(String token) {
        try {
            return firebaseAuth.verifyIdToken(token);
        } catch (Exception ex) {
            throw new AestagramException(ExceptionCode.TOKEN_NOT_FOUND);
        }
    }
}
