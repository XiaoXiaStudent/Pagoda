package org.javaboy.pagoda.framework.config;

import com.alibaba.druid.spring.boot.autoconfigure.properties.DruidStatProperties;
import com.alibaba.druid.util.Utils;
import org.javaboy.pagoda.common.utils.spring.SpringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.Map;

/**
 * druid 配置多数据源
 *
 * @author pagoda
 */
@Configuration
public class DruidConfig {

   /*      // 定义主数据源
        @Bean
        @ConfigurationProperties("spring.datasource.druid.master")
        public DataSource masterDataSource(DruidProperties druidProperties) {
                // 创建DruidDataSource实例
                DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
                // 使用DruidProperties配置数据源
                return druidProperties.dataSource(dataSource);
        }

        // 定义从数据源，只有在特定条件下才会创建
        @Bean
        @ConfigurationProperties("spring.datasource.druid.slave")
        @ConditionalOnProperty(prefix = "spring.datasource.druid.slave", name = "enabled", havingValue = "true")
        public DataSource slaveDataSource(DruidProperties druidProperties) {
                // 创建DruidDataSource实例
                DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
                // 使用DruidProperties配置数据源
                return druidProperties.dataSource(dataSource);
        }

        // 定义动态数据源，并设置为主数据源
        @Bean(name = "dynamicDataSource")
        @Primary
        public DynamicDataSource dataSource(DataSource masterDataSource) {
   *//*               Map<Object, Object> targetDataSources = new HashMap<>();
               //  将主数据源添加到目标数据源映射中
                targetDataSources.put(DataSourceType.MASTER.name(), masterDataSource);
              //   尝试添加从数据源
                setDataSource(targetDataSources, DataSourceType.SLAVE.name(), "slaveDataSource"); *//*
                // 创建并返回DynamicDataSource实例
                 return new DynamicDataSource(masterDataSource, targetDataSources);
        }
 */
        /**
         * 设置数据源
         *
         * @param targetDataSources 备选数据源集合
         * @param sourceName        数据源名称
         * @param beanName          bean名称
         */
        public void setDataSource(Map<Object, Object> targetDataSources, String sourceName, String beanName) {
                try {
                        // 从Spring上下文中获取指定名称的数据源Bean
                        DataSource dataSource = SpringUtils.getBean(beanName);
                        // 将数据源添加到映射中
                        targetDataSources.put(sourceName, dataSource);
                } catch (Exception e) {
                        // 异常捕获，但没有进一步处理（可能需要日志记录或其他处理）
                }
        }


        /**
         * 去除监控页面底部的广告
         */
        @SuppressWarnings({"rawtypes", "unchecked"})
        @Bean
        @ConditionalOnProperty(name = "spring.datasource.druid.statViewServlet.enabled", havingValue = "true")
        public FilterRegistrationBean removeDruidFilterRegistrationBean(DruidStatProperties properties) {
                // 获取web监控页面的参数
                DruidStatProperties.StatViewServlet config = properties.getStatViewServlet();
                // 提取common.js的配置路径
                String pattern = config.getUrlPattern() != null ? config.getUrlPattern() : "/druid/*";
                String commonJsPattern = pattern.replaceAll("\\*", "js/common.js");
                final String filePath = "support/http/resources/js/common.js";
                // 创建filter进行过滤
                Filter filter = new Filter() {
                        @Override
                        public void init(javax.servlet.FilterConfig filterConfig) throws ServletException {
                        }

                        @Override
                        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                                throws IOException, ServletException {
                                chain.doFilter(request, response);
                                // 重置缓冲区，响应头不会被重置
                                response.resetBuffer();
                                // 获取common.js
                                String text = Utils.readFromResource(filePath);
                                // 正则替换banner, 除去底部的广告信息
                                text = text.replaceAll("<a.*?banner\"></a><br/>", "");
                                text = text.replaceAll("powered.*?shrek.wang</a>", "");
                                response.getWriter().write(text);
                        }

                        @Override
                        public void destroy() {
                        }
                };
                FilterRegistrationBean registrationBean = new FilterRegistrationBean();
                registrationBean.setFilter(filter);
                registrationBean.addUrlPatterns(commonJsPattern);
                return registrationBean;
        }
}
