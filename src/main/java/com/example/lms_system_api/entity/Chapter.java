package com.example.lms_system_api.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "CHAPTERS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@SQLDelete(sql = "update chapters set deleted_at = now() where id = ?")
@SQLRestriction("deleted_at is null")
public class Chapter extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    Long id;

    @Column(name = "NAME", nullable = false)
    String name;

    @Column(name = "DESCRIPTION", nullable = false)
    String description;

    @Column(name = "SORT_ORDER", nullable = false)
    int order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COURSE_ID", nullable = false)
    Course course;
}
