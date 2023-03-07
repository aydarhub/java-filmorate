package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class Film {

    private Long id;

    @NotBlank(message = "название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания - 200 символов")
    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной")
    private int duration;

    private final List<Long> likes;

    private final LinkedHashSet<Genre> genres;

    @NotNull
    private Mpa mpa;

}
