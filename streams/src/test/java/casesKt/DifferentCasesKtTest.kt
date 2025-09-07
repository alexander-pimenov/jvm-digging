package casesKt

import org.junit.jupiter.api.Test
import java.util.*
import java.util.stream.Collectors


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
     * Промежуточные (filter, map, sorted) — lazy, возвращают новый stream
     * Терминальные (collect, forEach, count) — eager, запускают обработку
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
    fun `multiple sequence operations`() {
        val sequence: Sequence<String> = listOf("a", "a2", "aa3", "bbb4", "zzzzz5").asSequence()

        // Можно делать multiple terminal operations?
        val filtered: List<String> = sequence.filter { it.length > 1 }.toList() // OK
        println(filtered) //[a2, aa3, bbb4, zzzzz5]
        val mapped = sequence.map { it.uppercase(Locale.getDefault()) }.sorted()
            .toList()   // Тоже OK? - Да, но всё же лучше брать data за основу.
        println(mapped) //[A, A2, AA3, BBB4, ZZZZZ5]
    }

    @Test
    fun `sequence for each operations, get data again`() {
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

    @Test
    fun `sequence with filtering 1`() {
        val users = TestUser.createUsers()

        val testUsers: List<TestUser> = users.asSequence().filter {
            it.age > 18 && it.city == "Moscow"
        }.sortedBy {
            it.name
        }
            .toList()
        println("Moscow: $testUsers") //Moscow: [TestUser(name=Alice, age=25, city=Moscow, phone=[]), TestUser(name=Charlie, age=30, city=Moscow, phone=[])]
    }

    @Test
    fun `sequence with grouping 2`() {
        val users = TestUser.createUsers()

        //В Kotlin можно использовать метод groupBy даже для списков, не создавая последовательность
        val usersByCity: Map<String, List<TestUser>> = users
            .groupBy { it.city }
        println("Users by city: $usersByCity")
        //Users by city: {Moscow=[TestUser(name=Alice, age=25, city=Moscow, phone=[]), TestUser(name=Charlie, age=30, city=Moscow, phone=[])], SPb=[TestUser(name=Bob, age=17, city=SPb, phone=[])], Kazan=[TestUser(name=Diana, age=19, city=Kazan, phone=[])]}

        // Средний возраст по городам
        // Самый читаемый Kotlin стиль
        val avgAgeByCity: Map<String, Double> = users.groupBy { it.city }
            .mapValues { (city, usersInCity) ->
                usersInCity.map { it.age }.average() //здесь map для коллекций
            }
        println("Avg age by city: $avgAgeByCity")

        // Если users очень большая коллекция
        val avgAgeByCity2: Map<String, Double> = users.asSequence()
            .groupBy { it.city }
            .mapValues { (city, usersInCity) ->
                usersInCity.asSequence().map { it.age }.average()
            }

        println("Avg age by city: $avgAgeByCity2")
        // Вывод результатов
        avgAgeByCity.forEach { (city, avgAge) ->
            println("$city: ${"%.2f".format(avgAge)}")
        }
    }

    /**
     * Способ 1 - он самый понятный и читаемый.
     * Способ 2 и 3 полезны только если ты обрабатываешь очень большие данные и хочешь избежать создания промежуточных списков.
     */
    @Test
    fun `different ways to group by`() {
        val users = TestUser.createUsers()

        // Способ 1 - самый простой
        //Способ 1: Простой и читаемый (рекомендуемый)
        //groupBy создает Map<String, List<TestUser>>
        //mapValues преобразует значения мапы
        //map { it.age } извлекает возрасты
        //average() вычисляет среднее
        val avg1: Map<String, Double> = users.groupBy { it.city }
            .mapValues { (_, usersInCity) ->
                usersInCity.map { it.age }.average()
            }
        println("Способ 1: $avg1")

        // Способ 2 - с fold
        //Способ 2: С использованием fold для аккумуляции суммы и count
        //0.0 to 0 - начальное значение: (sum = 0.0, count = 0)
        //(sum + user.age) to (count + 1) - обновляем сумму и счетчик
        //Результат: Map<String, Pair<Double, Int>> где Pair - (общая_сумма_возрастов, количество_людей)
        val avg2: Map<String, Double> = users.groupingBy { it.city }
            .fold(0.0 to 0) { (sum, count), user ->
                (sum + user.age) to (count + 1)
            }
            .mapValues { (_, value) -> value.first / value.second }
        println("Способ 2: $avg2")

        // Способ 3 - с aggregate - Правильный aggregate с сохранением суммы и количества
        val avg3: Map<String, Double> = users.groupingBy { it.city }
            .aggregate { key, accumulator: Pair<Double, Int>?, user, first ->
                if (first) {
                    // Первый элемент в группе: инициализируем сумму и счетчик
                    user.age.toDouble() to 1
                } else {
                    // Последующие элементы: обновляем сумму и счетчик
                    (accumulator!!.first + user.age) to (accumulator.second + 1)
                }
            }
            .mapValues { it.value!!.let { (sum, count) -> sum / count } }
        println("Способ 3: $avg3")

    }
}