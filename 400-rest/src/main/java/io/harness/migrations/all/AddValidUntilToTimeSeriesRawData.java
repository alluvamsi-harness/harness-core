package io.harness.migrations.all;

import static io.harness.beans.PageRequest.PageRequestBuilder.aPageRequest;
import static io.harness.beans.SortOrder.OrderType.ASC;
import static io.harness.data.structure.HasPredicate.hasNone;
import static io.harness.threading.Morpheus.sleep;

import static java.time.Duration.ofMillis;

import io.harness.annotations.dev.HarnessModule;
import io.harness.annotations.dev.TargetModule;
import io.harness.beans.PageRequest;
import io.harness.beans.PageResponse;
import io.harness.migrations.Migration;

import software.wings.service.impl.analysis.TimeSeriesRawData;
import software.wings.service.impl.analysis.TimeSeriesRawData.TimeSeriesRawDataKeys;
import software.wings.service.intfc.DataStoreService;

import com.google.inject.Inject;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

@Slf4j
@TargetModule(HarnessModule._390_DB_MIGRATION)
public class AddValidUntilToTimeSeriesRawData implements Migration {
  @Inject private DataStoreService dataStoreService;

  @Override
  public void migrate() {
    int offset = 0;
    int pageSize = 1000;
    while (true) {
      try {
        PageRequest<TimeSeriesRawData> pageRequest = aPageRequest()
                                                         .withOffset(String.valueOf(offset))
                                                         .withLimit(String.valueOf(pageSize))
                                                         .addOrder(TimeSeriesRawDataKeys.createdAt, ASC)
                                                         .build();
        PageResponse<TimeSeriesRawData> response = dataStoreService.list(TimeSeriesRawData.class, pageRequest);
        if (hasNone(response.getResponse())) {
          log.info("No more documents left to update!");
          break;
        }
        offset += pageSize;
        List<TimeSeriesRawData> rawDataList = response.getResponse();
        rawDataList.forEach(rawData
            -> rawData.setValidUntil(new Date(new DateTime(rawData.getLastUpdatedAt()).plusMonths(6).getMillis())));

        dataStoreService.save(TimeSeriesRawData.class, rawDataList, false);
        sleep(ofMillis(1500));
        log.info("Updated {} time series raw data records", offset);
      } catch (Exception e) {
        log.info("Exception while adding valid until to time sereis raw data", e);
        break;
      }
    }
  }
}
