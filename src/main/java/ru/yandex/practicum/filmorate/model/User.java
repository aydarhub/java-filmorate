package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
public class User {

    private Long id;

    @Email(message = "некорректный email")
    private String email;

    @NotBlank
    @Pattern(regexp = "^\\S+$")
    private String login;

    private String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Past(message = "дата рождения не может быть в будущем")
    private LocalDate birthday;

    private final Map<Long, FriendStatus> friends = new HashMap<>();

}
