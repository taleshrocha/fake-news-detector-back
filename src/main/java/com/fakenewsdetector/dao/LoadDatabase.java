package com.fakenewsdetector.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fakenewsdetector.model.News;


@Configuration
class LoadDatabase {
	private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

	@Bean
	CommandLineRunner initDatabase(NewsRepository newsRespository) {
		return args -> {
			newsRespository.save(new News("Tales", "2023/02/09", "AHHH"));
			newsRespository.save(new News("a", "2023/02/09", "AHHH"));
			newsRespository.save(new News("b", "2023/02/09", "AHHH"));
			newsRespository.findAll().forEach(news -> log.info("PRELOADED " + news));
		};
	}
}
