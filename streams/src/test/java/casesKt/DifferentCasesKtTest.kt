package casesKt

import org.junit.jupiter.api.Test
import java.util.*


/**
 * Когда использовать стримы/sequences:
 * Большие коллекции — экономия памяти
 *
 * Цепочки операций — лучшая читаемость
 *
 * Параллельная обработка (только Java)
 *
 * Когда НЕ использовать:
 * Простые операции — for-loop может быть быстрее
 *
 * Мелкие коллекции — overhead стримов не оправдан
 *
 * я попробовал первые примеры прогнать и что интересно, для kotlin они отработали, а на stream получил ошибку :
 * ```
 * java.lang.IllegalStateException: stream has already been operated upon or closed
 * ```
 *
 * Почему возникает ошибка?
 * Java Stream — это одноразовый (single-use) конвейер. После вызова терминальной операции стрим закрывается и не может быть использован повторно.
 *
 * Внимание! Kotlin Sequence тоже может вести себя неожиданно
 *
 *
 */
class DifferentCasesKtTest {

    @Test
    fun `test for loop`() {
        val list: List<String?> = listOf("aa", "bbb", "cccc", null)
        // Императивный стиль
        val result: MutableList<String> = ArrayList()
        for (item in list) {
            if (item != null && item.length > 3) {
                result.add(item.uppercase(Locale.getDefault()))
            }
        }
        result.sort()
        println(result)
    }

    @Test
    fun `test sequence different cases`() {

        //создание последовательности
        val list: List<String> = listOf("aa", "bbb", "cccc")
        val sequence: Sequence<String> = list.asSequence()

        //  Промежуточные операции (lazy)
        sequence
            .filter { it.length > 2 }
            .map { it.uppercase(Locale.getDefault()) }
            .sorted()

        //Терминальные операции (eager)
        val result: List<String> = sequence.toList()
        println(result)
        // или
        val count: Int = sequence.count()
        println(count)
        // или
        val first: String? = sequence.firstOrNull()
        println(first ?: "<null>")


    }

    /**
     * Lazy evaluation (ленивые вычисления)
     * Элементы обрабатываются только когда нужно — по требованию.
     */
    @Test
    fun `test sequence different cases 2`() {
        // Kotlin - создание стрима из 1 млн элементов целых чисел
        val sequence: Sequence<Int> = (1..1_000_000).asSequence()
            .filter { it % 2 == 0 }
            .map { it * 2 }
            .take(10) // Обработает только первые 20 элементов!

        println("First 10 numbers from the stream: ${sequence.toList()}") // [4, 8, 12, 16, 20, 24, 28, 32, 36, 40]
    }

    @Test
    fun `test sequence interesting behavior cases`() {
        val sequence = listOf(1, 2, 3, 4, 5).asSequence()

        val filtered = sequence.filter {
            println("Incoming data. Filtering: $it")
            it % 2 == 0
        }

        // Первый проход
        println("First pass:")
        filtered.forEach { println("Result(1): $it") }
        // Output: Filtering: 1, Filtering: 2, Result: 2, Filtering: 3, Filtering: 4, Result: 4, Filtering: 5

        // Второй проход - тоже выведется! (а стим уже бы закрылся!!!)
        println("Second pass:")
        filtered.forEach { println("Result(2): $it") }
    }

    @Test
    fun `sequence for each operations`() {
        val data = listOf("apple", "banana", "cherry")

        // Создаем sequence заново для каждой операции
        val result1: List<String> = data.asSequence().filter { it.startsWith("a") }.toList()
        println(result1) // [apple]
        val result2: List<String> = data.asSequence().map { it.uppercase(Locale.getDefault()) }.toList()
        println(result2) // [APPLE, BANANA, CHERRY]
        // Или сохраняем intermediate sequence
        val filteredSequence: Sequence<String> = data.asSequence().filter { it.length > 5 }
        val result3: List<String> = filteredSequence.toList()
        println(result3) //[banana, cherry]
        val result4: List<String> =
            filteredSequence.map { it.uppercase(Locale.getDefault()) }.toList() // Будет работать
        println(result4) //[BANANA, CHERRY]
    }
}