package kr.co.quiz.global.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource("file:C:/tools/quiz-server.txt")
public class EnvProperties {
    @Autowired
    private Environment env;

    public void myMethod() {
        String SECRET_KEY = env.getProperty("SECRET_KEY");
    }
}
