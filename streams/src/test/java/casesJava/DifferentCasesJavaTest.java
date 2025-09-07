package casesJava;

import casesKt.TestUser;
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
 * <p>
 * я попробовал первые примеры прогнать и что интересно, для kotlin они отработали, а на stream получил ошибку :
 * ```
 * java.lang.IllegalStateException: stream has already been operated upon or closed
 * ```
 * <p>
 * Почему возникает ошибка?
 * Java Stream — это одноразовый (single-use) конвейер. После вызова терминальной операции стрим закрывается и не может быть использован повторно.
 * <p>
 * 3. Когда использовать parallel stream?
 * - Большие объемы данных
 * - Независимые операции
 * - Нет общих изменяемых состояний
 * - Готовность к возможным overhead'ам
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
     * Промежуточные (filter, map, sorted) — lazy, возвращают новый stream
     * Терминальные (collect, forEach, count) — eager, запускают обработку
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

    /**
     * race condition в параллельных стримах
     */
    @Test
    void testParallelStream() {
        // Java - создание стрима
        List<String> list = Arrays.asList("aa", "bbb", "cccc", null);
        if (Runtime.getRuntime().availableProcessors() > 1) {

            Stream<String> stream = list.parallelStream();

            // Промежуточные операции (lazy)
            Stream<String> sorted = stream.filter(Objects::nonNull)
                    .filter(s -> s.length() > 2)
                    .map(String::toUpperCase)
                    .sorted();
            List<String> result = sorted.collect(Collectors.toList());
            System.out.println("1: " + result);
        }

        // Java - создание стрима
        List<String> list2 = Arrays.asList("aa", "bbb", "cccc", null);
        // ПЛОХО - race condition в параллельных стримах
        List<String> result = new ArrayList<>();

        if (Runtime.getRuntime().availableProcessors() > 1) {
            Stream<String> stream2 = list2.parallelStream();

            stream2.forEach(item -> result.add(item));
            System.out.println("2: " + result);

            // ХОРОШО
            Stream<String> stream3 = list2.parallelStream();
            List<String> result3 = stream3.collect(Collectors.toList());
            System.out.println("3: " + result3);
        }
    }

    @Test
    void testForFiltering(){
        List<TestUser> users = TestUser.Companion.createUsers();

        // Найти москвичей старше 18, отсортировать по имени
        List<TestUser> moscow = users.stream().filter(user -> user.getAge() > 18 && user.getCity().equals("Moscow"))
                .sorted(Comparator.comparing(TestUser::getName)).toList();
        System.out.println("Mockow: " + moscow); //Moscow: [TestUser(name=Alice, age=25, city=Moscow, phone=[]), TestUser(name=Charlie, age=30, city=Moscow, phone=[])]
    }

    @Test
    void testForGroping(){
        List<TestUser> users = TestUser.Companion.createUsers();
        // Java
        Map<String, List<TestUser>> usersByCity = users.stream()
                .collect(Collectors.groupingBy(TestUser::getCity));
        System.out.println("Users by city: " + usersByCity); //
        //Users by city: {SPb=[TestUser(name=Bob, age=17, city=SPb, phone=[])], Moscow=[TestUser(name=Alice, age=25, city=Moscow, phone=[]), TestUser(name=Charlie, age=30, city=Moscow, phone=[])], Kazan=[TestUser(name=Diana, age=19, city=Kazan, phone=[])]}

        // Средний возраст по городам
        Map<String, Double> avgAgeByCity = users.stream()
                .collect(Collectors.groupingBy(
                        TestUser::getCity,
                        Collectors.averagingInt(TestUser::getAge)
                ));
        System.out.println("Average age by city: " + avgAgeByCity); //Average age by city: {SPb=17.0, Moscow=27.5, Kazan=19.0}
    }
}
