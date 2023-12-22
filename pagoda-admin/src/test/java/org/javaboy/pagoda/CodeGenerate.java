package org.javaboy.pagoda;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@SpringBootTest
public class CodeGenerate {

        String path = "D:\\Pagoda\\pagoda-media\\src\\main";

        @Test

        void contextLoads() {
                FastAutoGenerator.create("jdbc:mysql://101.42.19.113:3306/ruoyi?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false&serverTimezone=GMT%2B8", "ruoyi", "juhaoru1")
                        .globalConfig(builder -> {
                                builder.author("javaboy") // 设置作者
                                        .fileOverride() // 覆盖已生成文件
                                        .outputDir(path + "\\java"); // 指定输出目录
                        })
                        .packageConfig(builder -> {
                                builder.parent("org.javaboy.pagoda") // 设置父包名
                                        .moduleName("media") // 设置父包模块名
                                        .pathInfo(Collections.singletonMap(OutputFile.mapperXml, path + "\\resources\\mapper")); // 设置mapperXml生成路径
                        })
                        .strategyConfig(builder -> {
                                builder.addInclude("file_directory") // 设置order_items需要生成的表名
                                        //.addTablePrefix("pagoda")
                                        .entityBuilder().enableLombok();
                                // 设置过滤表前缀
                        })
                        .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                        .execute();
        }
}
