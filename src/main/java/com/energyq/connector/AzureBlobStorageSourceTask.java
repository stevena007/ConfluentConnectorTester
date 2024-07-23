package com.energyq.connector;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AzureBlobStorageSourceTask extends SourceTask {

    private String accountName;
    private String accountKey;
    private String containerName;
    private String blobNamePattern;
    private String topic;
    private BlobContainerClient containerClient;

    @Override
    public String version() {
        return "1.0";
    }

    @Override
    public void start(Map<String, String> props) {

        accountName = props.get("azbs.account.name");
        accountKey = props.get("azbs.account.key");
        containerName = props.get("azbs.container.name");
        blobNamePattern = props.get("azbs.blob.name.pattern");
        topic = props.get("topic");

        String connectionString = String.format("DefaultEndpointsProtocol=https;AccountName=%s;AccountKey=%s;",
                accountName, accountKey);
        containerClient = new BlobContainerClientBuilder()
                .connectionString(connectionString)
                .containerName(containerName)
                .buildClient();
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {

        List<SourceRecord> records = new ArrayList<>();

        for (BlobItem blobItem : containerClient.listBlobs()) {

            if (blobItem.getName().matches(blobNamePattern)) {

                BlobClient blobClient = containerClient.getBlobClient(blobItem.getName());

                String blobContent = new String(blobClient.downloadContent().toBytes(), StandardCharsets.UTF_8);

                Map<String, String> sourcePartition = new HashMap<>();
                sourcePartition.put("blobName", blobItem.getName());

                Map<String, String> sourceOffset = new HashMap<>();
                sourceOffset.put("position",
                        String.valueOf(blobItem.getProperties().getLastModified().toInstant().toEpochMilli()));

                SourceRecord sourceRecord = new SourceRecord(sourcePartition, sourceOffset, topic, null, blobContent);

                records.add(sourceRecord);

                // Rename the blob to avoid processing it again
                String newBlobName = blobItem.getName() + ".processed";
                blobClient.renameTo(newBlobName);

            }
        }

        return records;
    }

    @Override
    public void stop() {
        // Cleanup resources
    }
}