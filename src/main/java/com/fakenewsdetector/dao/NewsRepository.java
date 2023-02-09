package com.fakenewsdetector.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fakenewsdetector.model.News;

public interface NewsRepository extends JpaRepository<News, Long> {
	
}
