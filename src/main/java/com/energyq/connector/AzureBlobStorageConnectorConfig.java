package com.energyq.connector;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;

import java.util.Map;

public class AzureBlobStorageConnectorConfig extends AbstractConfig {

    public AzureBlobStorageConnectorConfig(ConfigDef config, Map<String, String> parsedConfig) {
        super(config, parsedConfig);
    }

    public AzureBlobStorageConnectorConfig(Map<String, String> parsedConfig) {
        this(conf(), parsedConfig);
    }

    public static final String KAFKA_TOPIC_CONF = "kafka.topic";
    private static final String KAFKA_TOPIC_DOC = "Topic to write to";

    private static final String ACCOUNT_NAME_CONF = "azbs.account.name";
    private static final String ACCOUNT_NAME_DOC = "Azure Blob Storage account name";

    private static final String ACCOUNT_KEY_CONF = "azbs.account.key";
    private static final String ACCOUNT_KEY_DOC = "Azure Blob Storage account key";

    private static final String CONTAINER_NAME_CONF = "azbs.container.name";
    private static final String CONTAINER_NAME_DOC = "Azure Blob Storage container name";

    private static final String BLOB_NAME_PATTERN_CONF = "azbs.blob.name.pattern";
    private static final String BLOB_NAME_PATTERN_DOC = "Pattern to match blob names";

    public static final String INTERVAL_CONF = "azbs.poll.interval.ms";
    private static final String INTERVAL_DOC = "Interval between messages (ms)";

    public static ConfigDef conf() {
        return new ConfigDef()
                .define(KAFKA_TOPIC_CONF, Type.STRING, Importance.HIGH, KAFKA_TOPIC_DOC)
                .define(INTERVAL_CONF, Type.LONG, 1_000L, Importance.HIGH, INTERVAL_DOC)
                .define(ACCOUNT_NAME_CONF, Type.STRING, Importance.HIGH, ACCOUNT_NAME_DOC)
                .define(ACCOUNT_KEY_CONF, Type.PASSWORD, Importance.HIGH, ACCOUNT_KEY_DOC)
                .define(CONTAINER_NAME_CONF, Type.STRING, Importance.HIGH, CONTAINER_NAME_DOC)
                .define(BLOB_NAME_PATTERN_CONF, Type.STRING, Importance.HIGH, BLOB_NAME_PATTERN_DOC);

    }

    public String getKafkaTopic() {
        return this.getString(KAFKA_TOPIC_CONF);
    }

    public Long getInterval() {
        return this.getLong(INTERVAL_CONF);
    }

    public String getAccountName() {
        return this.getString(ACCOUNT_NAME_CONF);
    }

    public String getAccountKey() {
        return this.getPassword(ACCOUNT_KEY_CONF).value();
    }

    public String getContainerName() {
        return this.getString(CONTAINER_NAME_CONF);
    }

    public String getBlobNamePattern() {
        return this.getString(BLOB_NAME_PATTERN_CONF);
    }

}
