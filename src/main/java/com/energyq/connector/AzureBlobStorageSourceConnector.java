package com.energyq.connector;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import java.util.List;
import java.util.Map;

public class AzureBlobStorageSourceConnector extends SourceConnector {

    private Map<String, String> configProps;

    @Override
    public String version() {
        return "1.0";
    }

    @Override
    public void start(Map<String, String> props) {
        configProps = props;
    }

    @Override
    public Class<? extends Task> taskClass() {
        return AzureBlobStorageSourceTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        return List.of(configProps);
    }

    @Override
    public void stop() {
        // Cleanup resources
    }

    @Override
    public ConfigDef config() {
        // Define configuration options here
        return new ConfigDef()
                .define("azbs.account.name", ConfigDef.Type.STRING, ConfigDef.Importance.HIGH,
                        "Azure Blob Storage account name")
                .define("azbs.account.key", ConfigDef.Type.PASSWORD, ConfigDef.Importance.HIGH,
                        "Azure Blob Storage account key")
                .define("azbs.container.name", ConfigDef.Type.STRING, ConfigDef.Importance.HIGH,
                        "Azure Blob Storage container name")
                .define("azbs.blob.name.pattern", ConfigDef.Type.STRING, ConfigDef.Importance.HIGH,
                        "Pattern to match blob names")
                .define("azbs.poll.interval.ms", ConfigDef.Type.INT, ConfigDef.Importance.MEDIUM,
                        "Poll interval in milliseconds")
                .define("topic", ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "Kafka topic to publish data to");
    }
}
