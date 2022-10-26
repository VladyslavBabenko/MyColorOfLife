package com.github.vladyslavbabenko.mycoloroflife.entity;

import com.github.vladyslavbabenko.mycoloroflife.enumeration.Purpose;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "t_secure_token")
public class SecureToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @Column(unique = true)
    private String token;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp timeStamp;

    @Column(updatable = false)
    private LocalDateTime expireAt;

    @Column(nullable = false)
    private Purpose purpose = Purpose.NONE;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public boolean isExpired() {
        return getExpireAt().isBefore(LocalDateTime.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SecureToken that = (SecureToken) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}