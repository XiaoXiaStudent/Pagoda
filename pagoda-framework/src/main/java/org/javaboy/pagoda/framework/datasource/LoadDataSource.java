package org.javaboy.pagoda.framework.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import org.javaboy.pagoda.common.utils.StringUtils;
import org.javaboy.pagoda.framework.config.properties.DruidPropertiesExt;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@EnableConfigurationProperties(DruidPropertiesExt.class)
public class LoadDataSource {

        @Resource
        DruidPropertiesExt druidPropertiesExt;

        public Map<String, DataSource> loadDataSource() {
                HashMap<String, DataSource> map = new HashMap<>();
                Map<String, Map<String, String>> ds = druidPropertiesExt.getDruid();

                try {
                        Set<String> keySet = ds.keySet();

                        for (String key : keySet) {
                                if (!key.equals("master")) {
                                        String enabled = ds.get(key).get("enabled");

                                        if (StringUtils.isNotEmpty(enabled) && enabled.equals("true")) {
                                                map.put(key, druidPropertiesExt.dataSource((DruidDataSource) DruidDataSourceFactory.createDataSource(ds.get(key))));
                                        }

                                } else {

                                        map.put(key, druidPropertiesExt.dataSource((DruidDataSource) DruidDataSourceFactory.createDataSource(ds.get(key))));
                                }

                        }

                } catch (Exception e) {
                        e.printStackTrace();

                }

                return map;
        }
}
