/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.debezium;

import io.harness.eventsframework.impl.redis.RedisProducer;
import io.harness.eventsframework.producer.Message;

import io.debezium.embedded.EmbeddedEngineChangeEvent;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.DebeziumEngine.RecordCommitter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.source.SourceRecord;
import org.redisson.api.RedissonClient;

@Slf4j
public class RedisStreamChangeConsumer implements DebeziumEngine.ChangeConsumer<ChangeEvent<String, String>> {
  private static final String OP_FIELD = "__op";
  int batchSize = 500;
  String nullKey = "default";
  String nullValue = "default";
  Map<String, RedisProducer> destinationToProducerMap = new HashMap<>();

  public RedisStreamChangeConsumer(String collectionList, String redisAddress, String mongoDbName) {
    RedissonClient redisson = RedissonClientCreator.getRedissonClient(redisAddress);
    String collections[] = collectionList.split(",");
    for (String collection : collections) {
      String destination = mongoDbName + "." + collection;
      destinationToProducerMap.put(destination, RedisProducer.of(destination, redisson, 10000, "debezium", ""));
    }
  }

  /**
   * Split collection to batches by batch size using a stream
   */
  private <T> Stream<List<T>> batches(List<T> source, int length) {
    if (source.isEmpty()) {
      return Stream.empty();
    }

    int size = source.size();
    int fullChunks = (size - 1) / length;

    return IntStream.range(0, fullChunks + 1)
        .mapToObj(n -> source.subList(n * length, n == fullChunks ? size : (n + 1) * length));
  }

  @Override
  public void handleBatch(List<ChangeEvent<String, String>> records,
      RecordCommitter<ChangeEvent<String, String>> committer) throws InterruptedException {
    log.trace("Handling a batch of {} records", records.size());
    batches(records, batchSize).forEach(batch -> {
      boolean completedSuccessfully = false;

      // As long as we failed to execute the current batch to the stream, we should retry if the reason was either a
      // connection error or OOM in Redis.
      while (!completedSuccessfully) {
        // Add the batch records to the stream(s)
        for (ChangeEvent<String, String> record : batch) {
          String destination = record.destination();
          String key = (record.key() != null) ? (record.key()) : nullKey;
          String value = (record.value() != null) ? (record.value()) : nullValue;
          Optional<OpType> opType =
              getOperationType(((EmbeddedEngineChangeEvent<String, String>) record).sourceRecord());

          io.harness.debezium.DebeziumChangeEvent debeziumChangeEvent =
              io.harness.debezium.DebeziumChangeEvent.newBuilder()
                  .setKey(key)
                  .setValue(value)
                  .setOptype(opType.get().toString())
                  .build();
          Message message = Message.newBuilder().setData(debeziumChangeEvent.toByteString()).build();
          destinationToProducerMap.get(destination).send(message);
          try {
            committer.markProcessed(record);
          } catch (InterruptedException e) {
            log.error("caught exception", e);
          }
        }
        completedSuccessfully = true;
      }
    });

    // Mark the whole batch as finished once the sub batches completed
    committer.markBatchFinished();
  }

  private Optional<OpType> getOperationType(SourceRecord sourceRecord) {
    return Optional.ofNullable(sourceRecord.headers().lastWithName(OP_FIELD))
        .flatMap(x -> OpType.fromString((String) x.value()));
  }
}
