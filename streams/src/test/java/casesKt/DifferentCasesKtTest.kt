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
}