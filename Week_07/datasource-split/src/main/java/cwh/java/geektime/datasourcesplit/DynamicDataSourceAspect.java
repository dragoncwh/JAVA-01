package cwh.java.geektime.datasourcesplit;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DynamicDataSourceAspect {
  private static Logger log = LoggerFactory.getLogger(DynamicDataSourceAspect.class);

  //改变数据源
  @Before("@annotation(targetDataSource)")
  public void changeDataSource(JoinPoint joinPoint, TargetDataSource targetDataSource) {
    DynamicDataSourceType type = targetDataSource.type();
    if (type.equals(DynamicDataSourceType.PRIMARY)) {
      DynamicDataSourceContext.usePrimaryDataSource();
      log.info("Switching DataSource to primary {} in Method {}",
          DynamicDataSourceContext.getDataSourceType(), joinPoint.getSignature());
    } else {
      DynamicDataSourceContext.useSlaveDataSource();
      log.info("Switching DataSource to slave {} in Method {}",
          DynamicDataSourceContext.getDataSourceType(), joinPoint.getSignature());
    }
  }

  @After("@annotation(targetDataSource)")
  public void clearDataSource(JoinPoint joinPoint, TargetDataSource targetDataSource) {
    log.info("Clearing DataSource " + DynamicDataSourceContext.getDataSourceType());
    DynamicDataSourceContext.clear();
  }
}
