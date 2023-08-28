package com.neuron.plantuml.sequenceDiagram;

import static java.util.stream.Collectors.groupingBy;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
class PatternMetaData {

  List<String> pattern;
  Integer startIndex;
  Integer endIndex;
  Integer repeatCount;

  @Override
  public String toString() {
    return pattern + " -> " + repeatCount;
  }
}

public class ChatGPTVersion {

  static List<PatternMetaData> patternMetaDataList = new LinkedList<>();

  public static void main(String[] args) {
//        List<String> input = Arrays.asList("1", "2", "3", "4", "5", "3", "4", "5", "4", "5", "4", "5", "4", "5", "6", "7", "4", "5", "4", "5", "4");
    List<String> input = Arrays.asList("1", "1", "1", "1");
    extracted(input);
  }

  public static LinkedList<PatternMetaData> extracted(List<String> input) {
    if (input.stream().distinct().count() == 1) {
      LinkedList<PatternMetaData> patternMetaData = new LinkedList<>();
      patternMetaData.add(
          PatternMetaData.builder().pattern(List.of(input.get(0))).repeatCount(input.size())
              .startIndex(0).endIndex(input.size()).build());
      return patternMetaData;
    }
    patternMetaDataList.clear();
    findRepeatingSublists(input);
    LinkedList<PatternMetaData> patternMetaData = filterPatterns(patternMetaDataList);
    return fillGaps(patternMetaData, input);
  }

  private static Map<List<String>, Integer> findRepeatingSublists(List<String> list) {
    Map<List<String>, Integer> countMap = new HashMap<>();
    for (int i = 0; i < list.size(); i++) {
      for (int j = i + 1; j <= list.size(); j++) {
        List<String> sublist = list.subList(i, j);
        if (Collections.frequency(list, sublist.get(0)) > 1) {
          int count = 0;
          for (int k = 0; k <= list.size() - sublist.size(); k++) {
            if (list.subList(k, k + sublist.size()).equals(sublist)) {
              count++;
            }
          }
          if (count > 1) {
            countMap.put(sublist, count);
            patternMetaDataList.add(
                PatternMetaData.builder().pattern(sublist).startIndex(i).endIndex((j - 1)).build());
          }
        }
      }
    }
    return countMap;
  }

  private static LinkedList<PatternMetaData> filterPatterns(List<PatternMetaData> patternList) {
    LinkedList<PatternMetaData> filteredPatternData = new LinkedList<>();
    patternList.stream().forEach(patternMetaData -> {
      List<PatternMetaData> collect1 = patternList.stream().filter(x -> {
        return patternMetaData.getEndIndex().equals(x.getStartIndex() - 1)
            && patternMetaData.getPattern().equals(x.getPattern());
      }).collect(Collectors.toList());

      if (!collect1.isEmpty()) {
        filteredPatternData.add(patternMetaData);
        filteredPatternData.add(collect1.get(0));
        patchIndexes1(filteredPatternData);
      }
    });
    return filteredPatternData;
  }

  private static void patchIndexes1(LinkedList<PatternMetaData> filteredPatternData) {
    removeNonRepeatedPatterns(filteredPatternData);
    removeRepeatedPatterns(filteredPatternData);

    LinkedList<PatternMetaData> patternMetaDataList = new LinkedList<>();
    patternMetaDataList.addAll(filteredPatternData);
    filteredPatternData.sort(Comparator.comparing(PatternMetaData::getStartIndex));

    for (int i = 0; i < filteredPatternData.size() - 1; i++) {
      PatternMetaData current = filteredPatternData.get(i);

      Integer endIndex = current.getEndIndex();

      List<PatternMetaData> collect = patternMetaDataList.stream().filter(x -> {
        boolean b1 = endIndex >= x.getStartIndex();
        boolean b2 = endIndex <= x.getEndIndex();
        return (!current.equals(x)) && b1 && b2;
      }).collect(Collectors.toList());

      if (!collect.isEmpty()) {
        filteredPatternData.removeAll(collect);
        patchIndexes1(filteredPatternData);
        return;
      }
    }
  }

  private static void removeNonRepeatedPatterns(LinkedList<PatternMetaData> filteredPatternData) {
    Map<List<String>, Integer> patternCount = new HashMap<>();
    for (PatternMetaData metaData : filteredPatternData) {
      patternCount.put(metaData.pattern, patternCount.getOrDefault(metaData.pattern, 0) + 1);
    }
    filteredPatternData.removeIf(metaData -> patternCount.get(metaData.pattern) < 2);
  }

  private static void removeRepeatedPatterns(LinkedList<PatternMetaData> filteredPatternData) {
    Map<String, List<PatternMetaData>> collect = filteredPatternData.stream().collect(groupingBy(
        x -> x.getPattern().toString() + x.getStartIndex().toString() + x.getEndIndex()
            .toString()));
    filteredPatternData.clear();
    collect.forEach((x, y) -> {
      filteredPatternData.add(y.get(0));
    });
  }


  private static LinkedList<PatternMetaData> fillGaps(LinkedList<PatternMetaData> patternData,
      List<String> input) {
    LinkedList<PatternMetaData> patternDataList = new LinkedList<>();
    if (patternData.isEmpty()) {
      PatternMetaData build = PatternMetaData.builder().pattern(input).startIndex(0)
          .endIndex(input.size() - 1).repeatCount(1).build();
      patternDataList.add(build);
      return patternDataList;
    }
    patternData.sort(Comparator.comparing(PatternMetaData::getStartIndex));
    int lastIndex = 0;

    for (PatternMetaData metaData : patternData) {
      List<String> strings = input.subList(lastIndex, metaData.startIndex);
      PatternMetaData build = PatternMetaData.builder().pattern(strings).startIndex(lastIndex)
          .endIndex(metaData.startIndex - 1).build();
      patternDataList.add(metaData);
      if (!strings.isEmpty()) {
        patternDataList.add(build);
      }
      lastIndex = metaData.endIndex + 1;
    }
    List<String> strings = input.subList(patternData.getLast().getEndIndex() + 1, input.size());
    if (strings != null) {
      PatternMetaData build = PatternMetaData.builder().pattern(strings)
          .startIndex(patternData.getLast().getEndIndex() + 1).endIndex(input.size()).build();
      if (!strings.isEmpty()) {
        patternDataList.add(build);
      }
    }

    patternDataList.sort(Comparator.comparing(PatternMetaData::getStartIndex));
    return makeRepetitions(patternDataList);
  }

  private static LinkedList<PatternMetaData> makeRepetitions(
      LinkedList<PatternMetaData> patternData) {
    LinkedList<PatternMetaData> combinedList = new LinkedList<>();
    PatternMetaData prevMetaData = null;

    for (PatternMetaData currentMetaData : patternData) {
      if (prevMetaData != null && prevMetaData.getEndIndex() + 1 == currentMetaData.getStartIndex()
          && prevMetaData.getPattern().equals(currentMetaData.getPattern())) {
        prevMetaData.setEndIndex(currentMetaData.getEndIndex());
        prevMetaData.setRepeatCount(prevMetaData.getRepeatCount() + 1);
      } else {
        if (prevMetaData != null) {
          combinedList.add(prevMetaData);
        }
        prevMetaData = PatternMetaData.builder().pattern(currentMetaData.getPattern())
            .startIndex(currentMetaData.getStartIndex()).endIndex(currentMetaData.getEndIndex())
            .repeatCount(1).build();
      }
    }
    if (prevMetaData != null) {
      combinedList.add(prevMetaData);
    }

    return combinedList;
  }
}
