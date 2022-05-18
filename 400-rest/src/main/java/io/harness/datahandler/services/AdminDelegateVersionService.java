/*
 * Copyright 2022 Harness Inc. All rights reserved.
 * Use of this source code is governed by the PolyForm Free Trial 1.0.0 license
 * that can be found in the licenses directory at the root of this repository, also available at
 * https://polyformproject.org/wp-content/uploads/2020/05/PolyForm-Free-Trial-1.0.0.txt.
 */

package io.harness.datahandler.services;

import static io.harness.delegate.beans.VersionOverrideType.DELEGATE_IMAGE_TAG;
import static io.harness.delegate.beans.VersionOverrideType.DELEGATE_JAR;
import static io.harness.delegate.beans.VersionOverrideType.UPGRADER_IMAGE_TAG;
import static io.harness.delegate.beans.VersionOverrideType.WATCHER_JAR;

import io.harness.delegate.beans.VersionOverride;
import io.harness.delegate.beans.VersionOverride.VersionOverrideKeys;
import io.harness.delegate.beans.VersionOverrideType;
import io.harness.persistence.HPersistence;

import com.google.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class AdminDelegateVersionService {
  private final HPersistence persistence;

  public void setDelegateImageTag(final String delegateTag, final String accountId, final int validFor) {
    setVersionOverride(accountId, DELEGATE_IMAGE_TAG, delegateTag, validFor);
  }

  public void setUpgraderImageTag(final String upgraderTag, final String accountId, final int validFor) {
    setVersionOverride(accountId, UPGRADER_IMAGE_TAG, upgraderTag, validFor);
  }

  public void setDelegateVersion(final String delegateVersion, final String accountId, final int validFor) {
    setVersionOverride(accountId, DELEGATE_JAR, delegateVersion, validFor);
  }

  public void setWatcherVersion(final String watcherVersion, final String accountId, final int validFor) {
    setVersionOverride(accountId, WATCHER_JAR, watcherVersion, validFor);
  }

  private void setVersionOverride(
      final String accountId, final VersionOverrideType overrideType, final String overrideValue, final int validFor) {
    final DateTime validity = DateTime.now().plusDays(validFor);
    final Query<VersionOverride> filter = persistence.createQuery(VersionOverride.class)
                                              .filter(VersionOverrideKeys.accountId, accountId)
                                              .filter(VersionOverrideKeys.overrideType, overrideType);
    final UpdateOperations<VersionOverride> updateOperation =
        persistence.createUpdateOperations(VersionOverride.class)
            .set(VersionOverrideKeys.version, overrideValue)
            .set(VersionOverrideKeys.validUntil, validity.toDate());

    persistence.upsert(filter, updateOperation);
  }
}
