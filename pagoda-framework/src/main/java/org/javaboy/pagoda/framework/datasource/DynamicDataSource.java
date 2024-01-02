package org.javaboy.pagoda.framework.datasource;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

import org.javaboy.pagoda.common.enums.DataSourceType;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

/**
 * 动态数据源
 *
 * @author pagoda
 */
@Component
public class DynamicDataSource extends AbstractRoutingDataSource {
        public DynamicDataSource(LoadDataSource loadDataSource) {
                Map<String, DataSource> allDs = loadDataSource.loadDataSource();
                super.setDefaultTargetDataSource(allDs.get(DataSourceType.MASTER));
                super.setTargetDataSources(new HashMap<>(allDs));
                super.afterPropertiesSet();
        }

        @Override
        protected Object determineCurrentLookupKey() {
                return DynamicDataSourceContextHolder.getDataSourceType();
        }
}
