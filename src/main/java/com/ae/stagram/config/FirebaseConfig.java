package com.ae.stagram.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;

/**
 * Primary애노테이션을 사용하여 가장 먼저 초기화가 되도록 설정
 */
@Configuration
public class FirebaseConfig {

    @Value("${firebase.sdk.path}")
    private String firebaseAdminSdkPath;

//    private FirebaseApp firebaseApp;

    @Primary
    @Bean
    public FirebaseApp initailize() throws IOException {

        FirebaseOptions options = new FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials
                .fromStream(new ClassPathResource(firebaseAdminSdkPath).getInputStream()))
            .build();

        if (FirebaseApp.getApps().isEmpty()){
//            firebaseApp = FirebaseApp.initializeApp(options);
             FirebaseApp.initializeApp(options);
        }
        return FirebaseApp.getInstance();
    }

    @Bean
    public FirebaseAuth getAuth() throws IOException {
        return FirebaseAuth.getInstance(initailize());
    }
}
