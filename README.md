# ConfluentConnectorTester

Test Repo which consumes a BLOB from Azure and send to Confluent Cloud

Example Confluent JSON format

    {
        "name": "azure-blob-storage-source-connector",
        "config": {
"connector.class": "com.energyq.connector.AzureBlobStorageSourceConnector",
"tasks.max": "1",
"azbs.account.name": "your_account_name",
"azbs.account.key": "your_account_key",
"azbs.container.name": "your_container_name",
"azbs.blob.name.pattern": "your_blob_name_pattern",
"azbs.poll.interval.ms": "60000",
"topic": "your_kafka_topic",
"key.converter": "org.apache.kafka.connect.storage.StringConverter",
"value.converter": "org.apache.kafka.connect.json.JsonConverter",
"value.converter.schemas.enable": "false"
}
