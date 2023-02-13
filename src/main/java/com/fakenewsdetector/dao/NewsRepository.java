package com.fakenewsdetector.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fakenewsdetector.model.News;

public interface NewsRepository extends JpaRepository<News, Long> {
  List<News> findByBaseEquals(boolean value);

}
