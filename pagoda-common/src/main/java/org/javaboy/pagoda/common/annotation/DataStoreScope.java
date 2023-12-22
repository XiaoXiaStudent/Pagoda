package org.javaboy.pagoda.common.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataStoreScope {
        public String storeCodeAlias() default "store_code"; // 新增属性，指定筛选字段
}
