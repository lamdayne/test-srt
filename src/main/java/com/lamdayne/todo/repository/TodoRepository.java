package com.lamdayne.todo.repository;

import com.lamdayne.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {

    Optional<Todo> findByIdAndDeletedAtIsNull(Long id);

    Page<Todo> findByDeletedAtIsNull(Pageable pageable);

    @Query("SELECT t FROM Todo t WHERE t.deletedAt IS NULL " +
            "AND (:completed IS NULL OR t.completed = :completed) " +
            "AND (:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Todo> findTodos(@Param("completed") Boolean completed,
                         @Param("search") String search,
                         Pageable pageable);

    long countByDeletedAtIsNull();

    long countByCompletedTrueAndDeletedAtIsNull();

}
