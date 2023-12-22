package org.javaboy.pagoda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 *
 * @author pagoda
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class PagodaApplication {
        public static void main(String[] args) {
                // System.setProperty("spring.devtools.restart.enabled", "false");
                SpringApplication.run(PagodaApplication.class, args);
                System.out.println("(♥◠‿◠)ﾉﾞ  pagoda百果园启动成功   ლ(´ڡ`ლ)ﾞ  \n");
        }
}
