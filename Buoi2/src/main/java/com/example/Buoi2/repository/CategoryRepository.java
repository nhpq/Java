package com.example.Buoi2.repository;


import com.example.Buoi2.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
