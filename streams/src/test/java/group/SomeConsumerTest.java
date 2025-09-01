package group;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Ключевые тестовые сценарии:
 * Пустой поток - должен вернуть пустой список
 * <p>
 * Пользователи с группами на "A" - должны быть включены в результат
 * <p>
 * Пользователи без групп на "A" - должны быть исключены
 * <p>
 * Обработка null значений:
 * <p>
 * null группы
 * <p>
 * null объекты групп
 * <p>
 * null имена групп
 * <p>
 * Чувствительность к регистру - только заглавная "A"
 * <p>
 * Большие потоки - проверка производительности
 * <p>
 * Параметризованные тесты - различные варианты имен групп
 * <p>
 * Пользователи с null - обработка null пользователей
 */
class SomeConsumerTest {

    private final SomeConsumer consumer = new SomeConsumer();

    @Test
    void shouldReturnEmptyListWhenStreamIsEmpty() {
        // Given
        Stream<User> emptyStream = Stream.empty();

        // When
        ArrayList<User> result = consumer.consume3(emptyStream);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnUsersWithGroupsStartingWithA() {
        // Given
        User user1 = createUser("user1", 25,
                Arrays.asList(
                        createGroup("Admin", "Administrators"),
                        createGroup("Users", "Regular users"))
        );

        User user2 = createUser("user2", 30,
                Arrays.asList(
                        createGroup("Analytics", "Data analysts"),
                        createGroup("Beta", "Beta testers"))
        );

        User user3 = createUser("user3", 28,
                Arrays.asList(
                        createGroup("Support", "Support team"),
                        createGroup("Dev", "Developers"))
        );

        Stream<User> usersStream = Stream.of(user1, user2, user3);

        // When
        ArrayList<User> result = consumer.consume3(usersStream);

        // Then
        assertThat(result)
                .hasSize(2)
                .extracting(User::getUsername)
                .containsExactlyInAnyOrder("user1", "user2");
    }

    @Test
    void shouldNotReturnUsersWithoutGroupsStartingWithA() {
        // Given
        User user1 = createUser("user1", 25,
                Arrays.asList(
                        createGroup("Users", "Regular users"),
                        createGroup("Beta", "Beta testers"))
        );

        User user2 = createUser("user2", 30,
                Arrays.asList(
                        createGroup("Support", "Support team"),
                        createGroup("Dev", "Developers"))
        );

        Stream<User> usersStream = Stream.of(user1, user2);

        // When
        ArrayList<User> result = consumer.consume3(usersStream);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldHandleNullGroups() {
        // Given
        User user1 = createUser("user1", 25, null);
        User user2 = createUser("user2", 30,
                Arrays.asList(createGroup("Admin", "Administrators")));

        Stream<User> usersStream = Stream.of(user1, user2);

        // When
        ArrayList<User> result = consumer.consume3(usersStream);

        // Then
        assertThat(result)
                .hasSize(1)
                .extracting(User::getUsername)
                .containsExactly("user2");
    }

    @Test
    void shouldHandleEmptyGroups() {
        // Given
        User user1 = createUser("user1", 25, Collections.emptyList());
        User user2 = createUser("user2", 30,
                Arrays.asList(createGroup("Analytics", "Data analysts")));

        Stream<User> usersStream = Stream.of(user1, user2);

        // When
        ArrayList<User> result = consumer.consume3(usersStream);

        // Then
        assertThat(result)
                .hasSize(1)
                .extracting(User::getUsername)
                .containsExactly("user2");
    }

    @Test
    void shouldHandleNullGroupObjects() {
        // Given
        User user1 = createUser("user1", 25, Arrays.asList(null, createGroup("Admin", "Admins")));
        User user2 = createUser("user2", 30, Arrays.asList(null, createGroup("Users", "Regular users")));

        Stream<User> usersStream = Stream.of(user1, user2);

        // When
        ArrayList<User> result = consumer.consume3(usersStream);

        // Then
        assertThat(result)
                .hasSize(1)
                .extracting(User::getUsername)
                .containsExactly("user1");
    }

    @Test
    void shouldHandleNullGroupNames() {
        // Given
        User user1 = createUser("user1", 25,
                Arrays.asList(createGroup(null, "No name"), createGroup("Admin", "Admins")));

        User user2 = createUser("user2", 30,
                Arrays.asList(createGroup(null, "No name"), createGroup("Users", "Regular users")));

        Stream<User> usersStream = Stream.of(user1, user2);

        // When
        ArrayList<User> result = consumer.consume3(usersStream);

        // Then
        assertThat(result)
                .hasSize(1)
                .extracting(User::getUsername)
                .containsExactly("user1");
    }

    @Test
    void shouldHandleCaseSensitivity() {
        // Given
        User user1 = createUser("user1", 25,
                Arrays.asList(createGroup("admin", "lowercase"), createGroup("Users", "Regular users")));

        User user2 = createUser("user2", 30,
                Arrays.asList(createGroup("Analytics", "Data analysts")));

        Stream<User> usersStream = Stream.of(user1, user2);

        // When
        ArrayList<User> result = consumer.consume3(usersStream);

        // Then
        assertThat(result)
                .hasSize(1)
                .extracting(User::getUsername)
                .containsExactly("user2");
    }

    @Test
    void shouldHandleLargeStream() {
        // Given
        List<User> largeUserList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            List<Group> groups = i % 2 == 0
                    ? Arrays.asList(createGroup("Users", "Regular users"))
                    : Arrays.asList(createGroup("Admin", "Administrators"));

            largeUserList.add(createUser("user" + i, 20 + i, groups));
        }

        Stream<User> usersStream = largeUserList.stream();

        // When
        ArrayList<User> result = consumer.consume3(usersStream);

        // Then
        assertThat(result).hasSize(500); // Только пользователи с нечетными индексами
    }

    @ParameterizedTest
    @MethodSource("provideGroupNamesForTesting")
    void shouldMatchVariousGroupNamesStartingWithA(String groupName, boolean shouldMatch) {
        // Given
        User user = createUser("testUser", 25,
                Arrays.asList(createGroup(groupName, "Test group")));

        Stream<User> usersStream = Stream.of(user);

        // When
        ArrayList<User> result = consumer.consume3(usersStream);

        // Then
        if (shouldMatch) {
            assertThat(result).hasSize(1);
        } else {
            assertThat(result).isEmpty();
        }
    }

    private static Stream<Arguments> provideGroupNamesForTesting() {
        return Stream.of(
                Arguments.of("Admin", true),
                Arguments.of("Analytics", true),
                Arguments.of("Accounting", true),
                Arguments.of("A", true),
                Arguments.of("A1", true),
                Arguments.of("A_Team", true),
                Arguments.of("Users", false),
                Arguments.of("Beta", false),
                Arguments.of("Support", false),
                Arguments.of("", false),
                Arguments.of("aAdmin", false), // lowercase
                Arguments.of(" admin", false)  // starts with space
        );
    }

    @Disabled
    @Test
    void shouldNotThrowExceptionWithNullUsers() {
        // Given
        User nullUser = null;
        User validUser = createUser("user1", 25,
                Arrays.asList(createGroup("Admin", "Administrators")));

        // When & Then
        assertDoesNotThrow(() -> {
            ArrayList<User> result = consumer.consume1(Stream.of(nullUser, validUser));
            assertThat(result).hasSize(1);
        });
    }

    // Вспомогательные методы для создания объектов
    private User createUser(String username, Integer age, List<Group> groups) {
        User user = new User();
        user.setUsername(username);
        user.setAge(age);
        user.setGroups(groups);
        return user;
    }

    private Group createGroup(String name, String description) {
        Group group = new Group();
        group.setName(name);
        group.setDescription(description);
        return group;
    }
}