package casesKt

data class TestUser(
    val name: String,
    val age: Int,
    val city: String,
    val phone: List<String> = emptyList()
) {
    companion object {


        /**
         * для тестов, генерация данных
         */
        @JvmStatic
        fun createUsers(): List<TestUser> {
            val users = listOf(
                TestUser("Alice", 25, "Moscow"),
                TestUser("Bob", 17, "SPb"),
                TestUser("Charlie", 30, "Moscow"),
                TestUser("Diana", 19, "Kazan")
            )
            return users
        }
    }
}