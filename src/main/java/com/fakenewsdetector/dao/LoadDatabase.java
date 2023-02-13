package com.fakenewsdetector.dao;

import java.io.FileReader;
import java.util.List;
import java.io.FileNotFoundException;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

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

		try {
			FileReader filereader = new FileReader("./fake-news-data.csv");
			CSVParser parser = new CSVParserBuilder().withSeparator(',').build();

			CSVReader csvReader = new CSVReaderBuilder(filereader).withCSVParser(parser).build();

			// Gets all CSV rows in a List<String[]>
			List<String[]> allData = csvReader.readAll();

			// Removes the CSV index line.
			allData.remove(0);

			return args -> {
				for (String[] row : allData) {
					if (row[0] != "") {
						newsRespository.save(new News(row[2], row[3], row[1], true));
					}
				}
				newsRespository.findAll().forEach(news -> log.info("PRELOADED " + news));
			};
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return args -> {
			newsRespository.save(new News("Tales", "2023/02/09", "AHHH", true));
			newsRespository.save(new News("a", "2023/02/09", "AHHH", true));
			newsRespository.save(new News("b", "2023/02/09", "AHHH", true));
			newsRespository.findAll().forEach(news -> log.info("PRELOADED " + news));
		};
	}
}
