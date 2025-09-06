package group;

import lombok.Data;

import java.util.List;

@Data
class User {
    private String username;
    private Integer age;
    private List<Group> groups;
}
