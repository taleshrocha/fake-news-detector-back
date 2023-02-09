package com.fakenewsdetector.model;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class News {
	private @Id @GeneratedValue Long id;
	private String author;
	private String date;
	private String content;
	private Double fakeRate;

	public News(){}

	public News(String author, String date, String content, Double fakeRate) {
		this.author = author;
		this.date = date;
		this.content = content;
		this.fakeRate = fakeRate;
	}

	public Long getId() {
		return id;
	}

	public String getAuthor() {
		return author;
	}

	public String getDate() {
		return date;
	}

	public String getContent() {
		return content;
	}

	public Double getFakeRate() {
		return fakeRate;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setFakeRate(Double fakeRate) {
		this.fakeRate = fakeRate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof News))
			return false;

		News news = (News) o;

		return Objects.equals(this.id, news.id)
				&& Objects.equals(this.author, news.author)
				&& Objects.equals(this.date, news.date)
				&& Objects.equals(this.content, news.content)
				&& Objects.equals(this.fakeRate, news.fakeRate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.author, this.date, this.content, this.fakeRate);
	}

	@Override
	public String toString() {
		return "News {"
				+ "id=" + this.id
				+ ", author=" + this.author
				+ ", date=" + this.date
				+ ", content=" + this.content
				+ ", fakeRate=" + this.fakeRate
				+ "}";
	}
}
