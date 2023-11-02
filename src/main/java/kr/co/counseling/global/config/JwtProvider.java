package kr.co.counseling.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {
    @Value("${SECRET_KEY}")
    private static String SECRET_KEY;

    private static final long TOKEN_VALIDATION_SECOND = 1000L * 60 * 120;
    private static final long REFRESH_TOKEN_VALIDATION_TIME = 1000L * 60 * 60 * 48;

    /*
        // 로깅을 통한 환경변수 주입 테스트

        @Autowired
        public void init() {
            log.info("SECRET_KEY: {}", SECRET_KEY);
        }
    */

}
