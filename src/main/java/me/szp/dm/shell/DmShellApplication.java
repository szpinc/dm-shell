package me.szp.dm.shell;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.util.StringUtils;

@SpringBootApplication
public class DmShellApplication {
    public static void main(String[] args) {

        String user = null;
        String password = null;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ("-u".equals(arg)) {
                user = args[i + 1];
            }
            if ("-p".equals(arg)) {
                password = args[i + 1];
            }
        }

        if (!StringUtils.hasText(user)) {
            System.err.println("require username");
            return;
        }
        if (!StringUtils.hasText(password)) {
            System.err.println("require password");
            return;
        }
        new SpringApplicationBuilder(DmShellApplication.class)
                .properties(String.format("spring.datasource.url=jdbc:dm://%s:%s", args[0], args[1]))
                .properties("spring.datasource.username=" + user)
                .properties("spring.datasource.password=" + password)
                .run();
    }

}
