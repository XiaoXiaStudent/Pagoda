package org.javaboy.pagoda.framework.aspectj;

import java.util.Objects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.javaboy.pagoda.common.annotation.DataSource;
import org.javaboy.pagoda.common.utils.StringUtils;
import org.javaboy.pagoda.framework.datasource.DynamicDataSourceContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 多数据源处理
 *
 * @author pagoda
 */
@Aspect
@Order(1)
@Component
public class DataSourceAspect {
        protected Logger logger = LoggerFactory.getLogger(getClass());

        @Pointcut("@annotation(org.javaboy.pagoda.common.annotation.DataSource)"
                + "|| @within(org.javaboy.pagoda.common.annotation.DataSource)")
        public void dsPointCut() {

        }

        @Around("dsPointCut()")
        public Object around(ProceedingJoinPoint point) throws Throwable {

                logger.info("Attempting to switch data source...");

                DataSource dataSource = getDataSource(point);

                if (StringUtils.isNotNull(dataSource)) {
                        DynamicDataSourceContextHolder.setDataSourceType(dataSource.value().name());
                }

                try {
                        return point.proceed();
                } finally {
                        // 销毁数据源 在执行方法之后
                        DynamicDataSourceContextHolder.clearDataSourceType();
                }
        }

        /**
         * 获取需要切换的数据源
         */
        public DataSource getDataSource(ProceedingJoinPoint point) {
                MethodSignature signature = (MethodSignature) point.getSignature();
                DataSource dataSource = AnnotationUtils.findAnnotation(signature.getMethod(), DataSource.class);
                if (Objects.nonNull(dataSource)) {
                        return dataSource;
                }

                return AnnotationUtils.findAnnotation(signature.getDeclaringType(), DataSource.class);
        }
}
