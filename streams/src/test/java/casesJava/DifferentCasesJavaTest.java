package casesJava;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Когда использовать стримы/sequences:
 * Большие коллекции — экономия памяти
 * <p>
 * Цепочки операций — лучшая читаемость
 * <p>
 * Параллельная обработка (только Java)
 * <p>
 * Когда НЕ использовать:
 * Простые операции — for-loop может быть быстрее
 * <p>
 * Мелкие коллекции — overhead стримов не оправдан
 */
public class DifferentCasesJavaTest {

    @Test
    void testForLoop() {
        List<String> list = Arrays.asList("aa", "bbb", "cccc", null);
        // Императивный стиль
        List<String> result = new ArrayList<>();
        for (String item : list) {
            if (item != null && item.length() > 3) {
                result.add(item.toUpperCase());
            }
        }
        Collections.sort(result);
        System.out.println(result);
    }

    @Test
    void testSequenceDifferentCases() {
        // Java - создание стрима
        List<String> list = Arrays.asList("aa", "bbb", "cccc", null);
        Stream<String> stream = list.stream();

        // Промежуточные операции (lazy)
        stream
                .filter(Objects::nonNull)
                .filter(s -> s.length() > 2)
                .map(String::toUpperCase)
                .sorted();

        //Терминальные операции (eager)
        List<String> result = stream.collect(Collectors.toList());
        System.out.println(result);
        // или
        long count = stream.count();
        System.out.println(count);
        // или
        Optional<String> first = stream.findFirst();
        System.out.println(first.orElse("<null>"));
    }

    /**
     * Lazy evaluation (ленивые вычисления)
     * Элементы обрабатываются только когда нужно — по требованию.
     */
    @Test
    void testSequenceDifferentCases2() {
        // Java - создание стрима из 1 млн элементов целых чисел
        Stream<Integer> stream = IntStream.rangeClosed(1, 1_000_001)
                .filter(number -> number % 2 == 0)
                .map(number -> number * 2)
                .limit(10) // Обработает только первые 20 элементов!
                .boxed(); // Convert IntStream to Stream<Integer>

        System.out.println("First 10 numbers from the stream: " + stream.collect(Collectors.toList())); // [4, 8, 12, 16, 20, 24, 28, 32, 36, 40]

    }
}
