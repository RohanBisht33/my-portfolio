package com.portfolio.repository;

import com.portfolio.model.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    List<Blog> findByFeaturedTrue();
    List<Blog> findAllByOrderByDateDesc();
    List<Blog> findTop5ByOrderByDateDesc();
}
