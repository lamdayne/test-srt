package com.lamdayne.todo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "todos")
public class Todo extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Builder.Default
    private Boolean completed = Boolean.FALSE;

}
