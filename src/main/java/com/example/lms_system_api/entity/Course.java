package com.example.lms_system_api.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "COURSES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "update courses set deleted_at = now() where id = ?")
@SQLRestriction("deleted_at is null")
public class Course extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    Long id;
    @Column(name = "NAME", nullable = false)
    String name;
    @Column(name = "DESCRIPTION")
    String description;
}
