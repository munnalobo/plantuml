package com.neuron.plantuml.sequenceDiagram;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class ChatGPTVersionTest {

  //"1","2","3","4","5","3","6","7","1","2","3","4","5","3","6","7"
  ChatGPTVersion2 chatGPTVersion;

  @BeforeEach
  void setUp() {
    chatGPTVersion = new ChatGPTVersion2();

  }

  @ParameterizedTest
  @MethodSource("provideTestCases")
  public void testExtracted(List<String> input, LinkedList<PatternMetaData> expectedOutput) {
    LinkedList<PatternMetaData> actualOutput = chatGPTVersion.extracted(input);
    assertEquals(expectedOutput.toString(), actualOutput.toString());
  }

  private static Stream<Object[]> provideTestCases() {
    return Stream.of(
        new Object[]{Arrays.asList("1", "2", "3", "4", "5"), new LinkedList<>(Arrays.asList(
            "[1, 2, 3, 4, 5] -> 1"
        ))},
        new Object[]{
            Arrays.asList("1", "2", "3", "4", "5", "3", "4", "5", "4", "5", "4", "5", "4", "5", "6",
                "7", "4", "5", "4", "5", "4"), new LinkedList<>(Arrays.asList(
            "[1, 2] -> 1, [3, 4, 5] -> 2, [4, 5] -> 3, [6, 7] -> 1, [4, 5] -> 2, [4] -> 1"
        ))},
        new Object[]{Arrays.asList("1", "1", "2", "2"), new LinkedList<>(Arrays.asList(
            "[1] -> 2, [2] -> 2"
        ))},
        new Object[]{Arrays.asList("1", "2", "1", "2", "3", "3"), new LinkedList<>(Arrays.asList(
            "[1, 2] -> 2, [3] -> 2"
        ))},
        new Object[]{Arrays.asList("1", "1", "2", "2", "3", "3", "4", "4"),
            new LinkedList<>(Arrays.asList(
                "[1] -> 2, [2] -> 2, [3] -> 2, [4] -> 2"
            ))},
        new Object[]{Arrays.asList("1", "2", "3", "1", "2", "3"), new LinkedList<>(Arrays.asList(
            "[1, 2, 3] -> 2"
        ))},
        new Object[]{Arrays.asList("1", "2", "3", "4", "1", "2", "3", "4"),
            new LinkedList<>(Arrays.asList(
                "[1, 2, 3, 4] -> 2"
            ))},
        new Object[]{Arrays.asList("1", "1", "1", "1"), new LinkedList<>(Arrays.asList(
            "[1] -> 4"
        ))},
        new Object[]{Arrays.asList("1", "2", "2", "1", "1", "2", "2", "1"),
            new LinkedList<>(Arrays.asList(
                "[1] -> 1, [2] -> 2, [1] -> 2, [2] -> 2, [1] -> 1"
            ))},
        new Object[]{Arrays.asList("1", "2", "1", "2", "1", "2"), new LinkedList<>(Arrays.asList(
            "[1, 2] -> 3"
        ))},
        new Object[]{Arrays.asList("1", "1", "2", "2", "3", "3", "3", "4", "4"),
            new LinkedList<>(Arrays.asList(
                "[1] -> 2, [2] -> 2, [3] -> 3, [4] -> 2"
            ))},
        new Object[]{Arrays.asList("1", "2", "3", "1", "2", "3", "1", "2", "3"),
            new LinkedList<>(Arrays.asList(
                "[1, 2, 3] -> 3"
            ))},
        new Object[]{Arrays.asList("1", "2", "3", "4", "1", "2", "3", "4", "1", "2", "3", "4"),
            new LinkedList<>(Arrays.asList(
                "[1, 2, 3, 4] -> 3"
            ))},
        new Object[]{Arrays.asList("1", "1", "1", "1", "1", "1"), new LinkedList<>(Arrays.asList(
            "[1] -> 6"
        ))},
        new Object[]{Arrays.asList("1", "2", "2", "1", "1", "2", "2", "1", "1", "2", "2", "1"),
            new LinkedList<>(Arrays.asList(
                "[1] -> 1, [2] -> 2, [1] -> 2, [2] -> 2, [1] -> 2, [2] -> 2, [1] -> 1"
            ))},
        new Object[]{
            Arrays.asList("1", "2", "3", "4", "5", "3", "6", "7", "1", "2", "3", "4", "5", "3", "6",
                "7"),
            new LinkedList<>(Arrays.asList(
                "[1] -> 1, [2] -> 2, [1] -> 2, [2] -> 2, [1] -> 2, [2] -> 2, [1] -> 1"
            ))}
    );
  }
}
