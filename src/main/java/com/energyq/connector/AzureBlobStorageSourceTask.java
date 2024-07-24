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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

public class AzureBlobStorageSourceTask extends SourceTask {

    public static final String TASK_ID = "task.id";
    public static final String CURRENT_ITERATION = "current.iteration";

    private String accountName;
    private String accountKey;
    private String containerName;
    private String blobNamePattern;
    private String topic;
    private BlobContainerClient containerClient;
    private AzureBlobStorageConnectorConfig config;

    @Override
    public String version() {
        return AzureBlobStorageSourceTask.class.getPackage().getImplementationVersion();
    }

    @Override
    public void start(Map<String, String> props) {

        config = new AzureBlobStorageConnectorConfig(props);

        accountName = config.getAccountName();
        accountKey = config.getAccountKey();
        containerName = config.getContainerName();
        blobNamePattern = config.getBlobNamePattern();
        topic = config.getKafkaTopic();

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

                try {
                    // Convert BlobContent to a JSON object using Jackson ObjectMapper
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(blobContent);

                    // For each JSON node, create a SourceRecord
                    for (JsonNode node : jsonNode) {

                        Map<String, String> sourcePartition = new HashMap<>();
                        sourcePartition.put("blobName", blobItem.getName());

                        Map<String, String> sourceOffset = new HashMap<>();
                        sourceOffset.put("position",
                                String.valueOf(blobItem.getProperties().getLastModified().toInstant().toEpochMilli()));

                        SourceRecord sourceRecord = new SourceRecord(sourcePartition, sourceOffset, topic, null,
                                node.toString());

                        records.add(sourceRecord);
                    }
                } catch (JsonProcessingException e) {
                    // move the blob to a dead-letter container for further analysis
                    System.err.println("Error processing blob: " + blobItem.getName());
                }

                // Remove the blob to avoid processing it again
                // Will need something a little more sophisticated in a production scenario
                blobClient.delete();

            }
        }

        return records;
    }

    @Override
    public void stop() {
        // Cleanup resources
    }
}