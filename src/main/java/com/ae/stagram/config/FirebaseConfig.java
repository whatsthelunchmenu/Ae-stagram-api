package com.ae.stagram.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import java.io.FileInputStream;
import java.io.IOException;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * PostConstruct애노테이션은 객체가 생성된 후 별도의 초기화가 필요할 사용하는 애노테이션 Primary애노테이션을 사용하여 가장 먼저 초기화가 되도록 설정
 */
@Configuration
public class FirebaseConfig {

    @Value("${firebase.sdk.path}")
    private String firebaseAdminSdkPath;

    private FirebaseApp firebaseApp;

    @Primary
    @PostConstruct
    public void initailize() throws IOException {
//        String path = new ClassPathResource(firebaseAccessKey).getURL().toString();
        FileInputStream serviceAccount = new FileInputStream(firebaseAdminSdkPath);

        FirebaseOptions options = new FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials
                .fromStream(serviceAccount))
            .build();

        firebaseApp = FirebaseApp.initializeApp(options);
    }

    @Bean
    public FirebaseAuth getAuth() {
        return FirebaseAuth.getInstance(firebaseApp);
    }
}
