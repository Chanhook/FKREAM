package com.flab.fkream.sharding;

import com.flab.fkream.sharding.ShardingProperty.ShardingRule;
import com.flab.fkream.sharding.UserHolder.Sharding;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;
import jodd.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class DataSourceRouter extends AbstractRoutingDataSource {

    private Map<Integer, MhaDataSource> shards;

    private static final String SHARD_DELIMITER = "SHARD_DELIMITER";
    private static final String MASTER = "master";
    private static final String SLAVE = "slave";


    @Override
    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        super.setTargetDataSources(targetDataSources);

        shards = new HashMap<>();

        for (Object item : targetDataSources.keySet()) {
            String dataSourceName = item.toString();
            String shardNoStr = dataSourceName.split(SHARD_DELIMITER)[0];

            MhaDataSource shard = getShard(shardNoStr);
            if (dataSourceName.contains(MASTER)) {
                shard.setMasterName(dataSourceName);
            } else if (dataSourceName.contains(SLAVE)) {
                shard.setSlaveName(dataSourceName);
            }
        }
    }

    @Override
    protected Object determineCurrentLookupKey() {
        Sharding sharding = UserHolder.getSharding();
        int shardNo = getShardNo(sharding);
        MhaDataSource dataSource = shards.get(shardNo);
        return TransactionSynchronizationManager.isCurrentTransactionReadOnly()
            ? dataSource.getSlaveName() : dataSource.getMasterName();
    }

    private int getShardNo(Sharding sharding) {
        if (sharding == null) {
            return 0;
        }

        if (sharding.getShardNo() != null) {
            return sharding.getShardNo();
        }

        int shardNo = 0;
        ShardingProperty shardingProperty = ShardingConfig.getShardingPropertyMap()
            .get(sharding.getTarget());
        if (shardingProperty.getStrategy() == ShardingStrategy.RANGE) {
            shardNo = getShardNoByRange(shardingProperty.getRules(), sharding.getShardKey());
        }
        return shardNo;
    }

    private int getShardNoByRange(List<ShardingRule> rules, long shardKey) {
        for (ShardingRule rule : rules) {
            if (rule.getRangeMin() <= shardKey && shardKey <= rule.getRangeMax()) {
                return rule.getShardNo();
            }
        }
        return 0;
    }

    private MhaDataSource getShard(String shardNoStr) {
        int shardNo = 0;
        if (isNumeric(shardNoStr)) {
            shardNo = Integer.valueOf(shardNoStr);
        }

        MhaDataSource shard = shards.get(shardNo);
        if (shard == null) {
            shard = new MhaDataSource();
            shards.put(shardNo, shard);
        }
        return shard;
    }

    private boolean isNumeric(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    @Getter
    @Setter
    private class MhaDataSource {

        private String masterName;
        private String slaveName;
    }
}
