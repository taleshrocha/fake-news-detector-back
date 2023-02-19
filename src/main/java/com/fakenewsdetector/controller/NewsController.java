package com.fakenewsdetector.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fakenewsdetector.assembler.NewsModelAssembler;
import com.fakenewsdetector.dao.NewsRepository;
import com.fakenewsdetector.exception.AlgoNotFoundException;
import com.fakenewsdetector.exception.NewsNotFoundException;
import com.fakenewsdetector.model.News;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

@RestController
public class NewsController {

  private final NewsRepository newsRepository;
  private final NewsModelAssembler newsAssembler;

  NewsController(NewsRepository newsRepository, NewsModelAssembler newsAssembler) {
    this.newsRepository = newsRepository;
    this.newsAssembler = newsAssembler;
  }

  @GetMapping("/news")
  public CollectionModel<EntityModel<News>> all() {

    List<EntityModel<News>> news = newsRepository.findAll().stream()
        .map(newsAssembler::toModel)
        .collect(Collectors.toList());

    return CollectionModel.of(news,
        linkTo(methodOn(NewsController.class).all()).withSelfRel());

  }

  @GetMapping("/news/base/{value}")
  public CollectionModel<EntityModel<News>> base(@PathVariable boolean value) {

    List<EntityModel<News>> news = newsRepository.findByBaseEquals(value).stream()
        .map(newsAssembler::toModel)
        .collect(Collectors.toList());

    return CollectionModel.of(news,
        linkTo(methodOn(NewsController.class).all()).withSelfRel());
  }

  @PutMapping("/news/blah/{algo}")
  public ResponseEntity<?> algoAll(@PathVariable String algo) {
    System.out.println("algo: " + algo);

    // Get all the news considered fake (the "base")
    List<News> base = newsRepository.findByBaseEquals(true).stream()
        .collect(Collectors.toList());

    // Update all the News that are not in "base", change the {algo}Rate and save
    newsRepository.findByBaseEquals(false)
        .forEach(news -> {
          System.out.println("2algo: " + algo);
          if (algo.equals("cosine")) {
            news.setCosineRate(news.processCosineRate(base));
          } else if (algo.equals("leven")) {
            news.setLevenRate(news.processLevenRate(base));
          } else if (algo.equals("jaro")) {
            news.setJaroRate(news.processJaroRate(base));
          } else {
            throw new AlgoNotFoundException(algo);
          }
          newsRepository.save(news);
        });

    return ResponseEntity.noContent().build();
  }

  @PutMapping("/news/base/{value}/{id}")
  public EntityModel<News> updateBase(@PathVariable boolean value, @PathVariable Long id) {

    News news = newsRepository.findById(id)
        .orElseThrow(() -> new NewsNotFoundException(id));
    news.setBase(value);
    newsRepository.save(news);

    return newsAssembler.toModel(news);
  }

  @GetMapping("/news/{id}")
  public EntityModel<News> one(@PathVariable Long id) {

    News news = newsRepository.findById(id)
        .orElseThrow(() -> new NewsNotFoundException(id));

    return newsAssembler.toModel(news);
  }

  @PostMapping("/news")
  public ResponseEntity<?> newNews(@RequestBody News newNews) {
    newNews.setBase(false);
    newNews.setProcessedContent(newNews.processContent(newNews.getContent()));
    EntityModel<News> entityModel = newsAssembler.toModel(newsRepository.save(newNews));

    return ResponseEntity
        .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
        .body(entityModel);
  }

  @PostMapping("/news/{algo}")
  public ResponseEntity<?> getAlgo(@RequestBody News news, @PathVariable String algo) {

    // Get all the news considered fake (the "base")
    List<News> base = newsRepository.findByBaseEquals(true).stream()
        .collect(Collectors.toList());

    news.setProcessedContent(news.processContent(news.getContent()));

    double med = 0.0;
    int count = 0;
    if (algo.contains("cosine")) {
      news.setCosineRate(news.processCosineRate(base));
      med += news.getCosineRate();
      count++;
    }
    if (algo.contains("leven")) {
      news.setLevenRate(news.processLevenRate(base));
      med += news.getLevenRate();
      count++;
    }
    if (algo.contains("jaro")) {
      news.setJaroRate(news.processJaroRate(base));
      med += news.getJaroRate();
      count++;
    }
    if (!(algo.contains("jaro") || algo.contains("leven") || algo.contains("cosine"))) {
      throw new AlgoNotFoundException(algo);
    }

    return ResponseEntity.ok(med / count);

  }

  @PostMapping("/news/csv")
  public ResponseEntity<?> postFile(@RequestParam("file") MultipartFile multipartfile) {

    try {
      Reader reader = new InputStreamReader(multipartfile.getInputStream());
      CSVParser parser = new CSVParserBuilder().withSeparator(',').build();

      CSVReader csvReader = new CSVReaderBuilder(reader).withCSVParser(parser).build();

      // Gets all CSV rows in a List<String[]>
      List<String[]> allData = csvReader.readAll();

      // Removes the CSV index line.
      allData.remove(0);

      for (String[] row : allData) {
        if (row[0] != "") {
          newsRepository.save(new News(row[2], row[3], row[1], true));
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      return ResponseEntity.internalServerError().build();
    }

    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/news/{id}")
  public ResponseEntity<?> removeNews(@PathVariable Long id) {

    newsRepository.deleteById(id);

    return ResponseEntity.noContent().build();
  }
}
