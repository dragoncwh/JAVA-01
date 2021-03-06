package cwh.java.geektime.datasourcesplit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

public class DynamicDataSourceRegister implements EnvironmentAware, ImportBeanDefinitionRegistrar {

  private static final Logger log = LoggerFactory.getLogger(DynamicDataSourceRegister.class);

  private DataSource primaryDataSource;

  private Map<String, DataSource> secondaryDataSources = new HashMap<>();

  private static final String DEFAULT_DATASOURCE_TYPE = "com.zaxxer.hikari.HikariDataSource";

  @Override
  public void setEnvironment(Environment environment) {
    initPrimary(environment);
    initSecondaries(environment);
  }

  @Override
  public void registerBeanDefinitions(
      AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
    List<String> dataSourceIds = new ArrayList<>();
    Map<Object, Object> targetDataSources = new HashMap<>();
    targetDataSources.put(DynamicDataSourceType.PRIMARY.name(), primaryDataSource);
    dataSourceIds.add(DynamicDataSourceType.PRIMARY.name());
    targetDataSources.putAll(secondaryDataSources);
    for (String key : secondaryDataSources.keySet()) {
      dataSourceIds.add(key);
    }
    DynamicDataSourceContext.setDataSourceIdsIfEmpty(dataSourceIds);

    GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
    beanDefinition.setBeanClass(DynamicDataSource.class);
    beanDefinition.setSynthetic(true);
    MutablePropertyValues mpv = beanDefinition.getPropertyValues();
    mpv.addPropertyValue("defaultTargetDataSource", primaryDataSource);
    mpv.addPropertyValue("targetDataSources", targetDataSources);
    beanDefinitionRegistry.registerBeanDefinition("dataSource", beanDefinition);

    log.info("Dynamic DataSource Registered");
  }

  private void initPrimary(Environment env) {
    String name = env.getProperty("spring.datasource.primary.name");
    String prefix = "spring.datasource." + name + ".";
    Map<String, Object> map = new HashMap<>();
    map.put("driverClassName", env.getProperty(prefix + "driverClassName"));
    map.put("url", env.getProperty(prefix + "url"));
    map.put("username", env.getProperty(prefix + "username"));
    map.put("password", env.getProperty(prefix + "password"));
    primaryDataSource = buildDataSource(map);
  }

  private void initSecondaries(Environment env) {
    String names = env.getProperty("spring.datasource.secondary.names");
    for (String name : names.split(",")) {
      String prefix = "spring.datasource." + name + ".";
      Map<String, Object> map = new HashMap<>();
      map.put("driverClassName", env.getProperty(prefix + "driverClassName"));
      map.put("url", env.getProperty(prefix + "url"));
      map.put("username", env.getProperty(prefix + "username"));
      map.put("password", env.getProperty(prefix + "password"));
      secondaryDataSources.put(name, buildDataSource(map));
    }
  }

  public DataSource buildDataSource(Map<String, Object> dataSourceMap) {
    try {
      Object type = dataSourceMap.get("type");
      if (type == null) {
        type = DEFAULT_DATASOURCE_TYPE;
      }
      Class<? extends DataSource> dataSourceType;
      dataSourceType = (Class<? extends DataSource>) Class.forName((String) type);
      String driverClassName = dataSourceMap.get("driverClassName").toString();
      String url = dataSourceMap.get("url").toString();
      String username = dataSourceMap.get("username").toString();
      String password = dataSourceMap.get("password").toString();

      DataSourceBuilder factory = DataSourceBuilder.create()
          .driverClassName(driverClassName)
          .url(url)
          .username(username)
          .password(password)
          .type(dataSourceType);
      return factory.build();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }
}
