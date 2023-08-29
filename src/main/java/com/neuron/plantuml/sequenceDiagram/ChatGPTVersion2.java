package com.neuron.plantuml.sequenceDiagram;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatGPTVersion2 {

  static LinkedList<PatternMetaData> patternMetaDataList = new LinkedList<>();

  public static void main(String[] args) {
//    List<String> input = Arrays.asList(
//        "1", "2",
//        "3", "4", "5",
//        "3", "4", "5",
//        "3", "4", "5",
//        "3", "6", "5",
//        "3", "6", "5",
//        "4", "5",
//        "4", "5",
//        "6", "7",
//        "4", "5",
//        "4", "5",
//        "4");
    List<String> input = Arrays.asList("1", "2", "3", "4", "5", "3", "4", "5", "4", "5", "4", "5",
        "4", "5", "6",
        "7", "4", "5", "4", "5", "4");
    extracted(input);
  }

  public static LinkedList<PatternMetaData> extracted(List<String> input) {
    patternMetaDataList = new LinkedList<>();
    makeSequence(input);
    makeLastElement(input);
    return patternMetaDataList;
  }

  private static void makeLastElement(List<String> input) {
    AtomicInteger lastCount = new AtomicInteger();
    patternMetaDataList.forEach(x -> {
      int i = x.getPattern().size() * x.repeatCount;
      lastCount.set(lastCount.get() + i);
    });
    List<String> strings = input.subList(lastCount.get(), input.size());
    if (!strings.isEmpty()) {
      PatternMetaData build1 = PatternMetaData.builder()
          .pattern(strings)
          .startIndex(lastCount.get())
          .endIndex(input.size())
          .repeatCount(1)
          .build();
      patternMetaDataList.add(build1);
    }
  }

  private static void makeSequence(List<String> input) {
    int repeatedElementIndex = findIndexOfFirstRepeatedString(input);
    if (repeatedElementIndex != -1) {

      int firstOccurrenceIndex = input.indexOf(input.get(repeatedElementIndex));
      List<String> head = input.subList(firstOccurrenceIndex, repeatedElementIndex);
      List<String> head1 = input.subList(0, firstOccurrenceIndex);

      if (!head1.isEmpty()) {
        PatternMetaData build1 = PatternMetaData.builder()
            .pattern(head1)
            .startIndex(0)
            .endIndex(firstOccurrenceIndex)
            .repeatCount(1)
            .build();
        patternMetaDataList.add(build1);
      }
      if (!head.isEmpty()) {
        PatternMetaData build = PatternMetaData.builder().pattern(head)
            .startIndex(firstOccurrenceIndex).endIndex(repeatedElementIndex - 1)
            .repeatCount(1)
            .build();
        patternMetaDataList.add(build);
      }
      List<String> tail = input.subList(repeatedElementIndex,
          repeatedElementIndex + head.size());

      PatternMetaData patternMetaData = patternMetaDataList.get(patternMetaDataList.size() - 1);

      if (patternMetaData.getPattern().equals(tail)) {

        int pointer = repeatedElementIndex;

        while (patternMetaData.getPattern().equals(tail)) {
          Integer repeatCount = patternMetaData.getRepeatCount();
          patternMetaData.setEndIndex(patternMetaData.getEndIndex() + tail.size());
          patternMetaData.setRepeatCount(repeatCount + 1);
          pointer = pointer + patternMetaData.getPattern().size();

          if (input.size() >= (pointer + patternMetaData.getPattern().size())) {
            tail = input.subList(pointer,
                pointer + patternMetaData.getPattern().size());
          } else {
            break;
          }
        }

        List<String> updatedTail = input.subList(patternMetaData.getEndIndex() + 1,
            input.size());
        makeSequence(updatedTail);

        System.out.println(
            "The repeatedElementIndex of the first repeated string is: " + repeatedElementIndex);
      }
    } else {
      System.out.println("No repeated strings found.");
    }
  }

  private static void makePatternMetaData() {

  }

  public static int findIndexOfFirstRepeatedString(List<String> list) {
    Set<String> seen = new HashSet<>();
    for (int i = 0; i < list.size(); i++) {
      String element = list.get(i);
      if (seen.contains(element)) {
        return i;
      }
      seen.add(element);
    }
    return -1;
  }

}
