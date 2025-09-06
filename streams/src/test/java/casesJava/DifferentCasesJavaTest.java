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
 *
 *  я попробовал первые примеры прогнать и что интересно, для kotlin они отработали, а на stream получил ошибку :
 *  ```
 *  java.lang.IllegalStateException: stream has already been operated upon or closed
 *  ```
 *
 *  Почему возникает ошибка?
 *  Java Stream — это одноразовый (single-use) конвейер. После вызова терминальной операции стрим закрывается и не может быть использован повторно.
 *
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
        Stream<String> sorted = stream
                .filter(Objects::nonNull)
                .filter(s -> s.length() > 2)
                .map(String::toUpperCase)
                .sorted();

        //Помним - Стрим - одноразовый!!!
        //Терминальные операции (eager)
        // Сохраняем результат обработки в result, чтобы потом продолжить обработку
        List<String> result = sorted.collect(Collectors.toList());
        System.out.println(result);
        // или
        long count = result.stream().count();
        System.out.println(count);
        // или
        Optional<String> first = result.stream().findFirst();
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
