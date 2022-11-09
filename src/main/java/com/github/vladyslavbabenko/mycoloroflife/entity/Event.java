package com.github.vladyslavbabenko.mycoloroflife.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * Event entity.
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "t_event")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @Column(length = 16)
    private String dateTimeOfCreation = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));

    @NotEmpty(message = "{validation.title.not.empty}")
    @Size(min = 1, max = 100, message = "{validation.title.length}")
    @Column(length = 100, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    @NotEmpty(message = "{validation.description.not.empty}")
    @Size(min = 1, max = 65535, message = "{validation.text.length}")
    private String text;

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<User> users;
}