package me.szp.dm.shell;

import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, DruidDataSourceAutoConfigure.class})
public class DmShellApplication {
    public static void main(String[] args) {

//        if (args.length != 2) {
//            throw new IllegalArgumentException("参数不正确");
//        }

        new SpringApplicationBuilder(DmShellApplication.class)
//                .properties("spring.datasource.host=" + args[0])
//                .properties("spring.datasource.port=" + args[1])
                .run();
    }

}
