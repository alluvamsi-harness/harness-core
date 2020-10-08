package io.harness.ng.core.mapper;

import static io.harness.data.structure.EmptyPredicate.isEmpty;
import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.EMPTY_MAP;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import io.harness.ng.core.common.beans.Tag;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Map;

@UtilityClass
public class TagMapper {
  public static List<Tag> convertToList(Map<String, String> tags) {
    if (isEmpty(tags)) {
      return EMPTY_LIST;
    }

    return tags.entrySet()
        .stream()
        .map(e -> Tag.builder().key(e.getKey()).value(e.getValue()).build())
        .collect(toList());
  }

  public static Map<String, String> convertToMap(List<Tag> tags) {
    if (isEmpty(tags)) {
      return EMPTY_MAP;
    }

    return tags.stream().collect(toMap(Tag::getKey, Tag::getValue));
  }
}
