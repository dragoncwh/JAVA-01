package cwh.java.geektime.l15w8.sharding;

import java.util.Collection;
import org.apache.shardingsphere.api.sharding.hint.HintShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.hint.HintShardingValue;

public class DefaultDatabaseShardingAlgorithm implements HintShardingAlgorithm<String> {

  @Override
  public Collection<String> doSharding(Collection<String> collection, HintShardingValue<String> hintShardingValue) {
    return null;
  }
}
