package group;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Проверка на null: Всегда проверяем, что группы не null и не пусты
 * Проверка имени группы: Убеждаемся, что group и group.getName() не null
 * Фильтрация: Используем anyMatch() для проверки хотя бы одной группы, начинающейся с "A"
 * Сбор в ArrayList: Используем подходящий коллектор для возврата ArrayList
 */
public class SomeConsumer {

    public ArrayList<User> consume2(Stream<User> usersStream) {
        return usersStream
                .filter(Objects::nonNull)
                .filter(user -> user.getGroups() != null && !user.getGroups().isEmpty())
                .filter(user -> user.getGroups().stream()
                        .anyMatch(group ->
                                group != null &&
                                        group.getName() != null &&
                                        group.getName().startsWith("A")))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public ArrayList<User> consume1(Stream<User> usersStream) {
        return usersStream
                .filter(user -> user.getGroups() != null)
                .filter(user -> user.getGroups().stream()
                        .anyMatch(group -> group.getName() != null &&
                                group.getName().startsWith("A")))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public ArrayList<User> consume3(Stream<User> usersStream) {
        return usersStream
                .filter(user -> user.getGroups() != null)
                .filter(user -> user.getGroups().stream()
                        .anyMatch(group ->
                                group != null &&
                                        group.getName() != null &&
                                        group.getName().startsWith("A")))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}