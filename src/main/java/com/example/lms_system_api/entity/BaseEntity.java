package com.example.lms_system_api.entity;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@MappedSuperclass
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseEntity {

    LocalDateTime created_at;
    LocalDateTime updated_at;
    LocalDateTime deleted_at;

    @PrePersist
    protected void onCreate(){
        created_at = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate(){
        updated_at = LocalDateTime.now();
    }
    @PreRemove
    protected void onDelete(){
        deleted_at = LocalDateTime.now();
    }
}
