package com.fakenewsdetector.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.fakenewsdetector.assembler.NewsModelAssembler;
import com.fakenewsdetector.dao.NewsRepository;
import com.fakenewsdetector.exception.NewsNotFoundException;
import com.fakenewsdetector.model.News;

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

  // newNews.setLevenRate(newNews.processLevenRate(allNews));
  // newNews.setJaroRate(newNews.processJaroRate(allNews));
  @PutMapping("/news/cosine")
  public ResponseEntity<?> cosineAll() {

    // Get all the news considered fake (the base)
    List<News> base = newsRepository.findByBaseEquals(true).stream()
        .collect(Collectors.toList());

    // Get all News that are not base, change the cosineRate and save
    newsRepository.findByBaseEquals(false)
        .forEach(news -> {
          news.setCosineRate(news.processCosineRate(base));
          newsRepository.save(news);
        });

    return ResponseEntity.noContent().build();
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

  @DeleteMapping("/news/{id}")
  public ResponseEntity<?> removeNews(@PathVariable Long id) {

    newsRepository.deleteById(id);

    return ResponseEntity.noContent().build();
  }
}
