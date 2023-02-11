package com.fakenewsdetector.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.fakenewsdetector.model.News;
import com.fakenewsdetector.controller.NewsController;

@Component
public class NewsModelAssembler implements RepresentationModelAssembler<News, EntityModel<News>> {

  @Override
  public EntityModel<News> toModel(News news) {
    return EntityModel.of(news,
        linkTo(methodOn(NewsController.class).one(news.getId())).withSelfRel(),
        linkTo(methodOn(NewsController.class).all()).withRel("All the News"));
  }
}
