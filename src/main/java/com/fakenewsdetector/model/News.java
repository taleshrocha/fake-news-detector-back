package com.fakenewsdetector.model;

import java.util.*;
import java.text.Normalizer;
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
	private String processedContent;
	private Double fakeRate;

	public News() {
	}

	public News(String author, String date, String content) {
		this.author = author;
		this.date = date;
		this.content = content;
		this.processedContent = processContent(this.content);
	}

	// ### Gets and Setters ###

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

	public String getProcessedContent() {
		return processedContent;
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

	public void setProcessedContent(String processedContent) {
		this.processedContent = processedContent;
	}

	public void setFakeRate(Double fakeRate) {
		this.fakeRate = fakeRate;
	}

	// ### Methods ###

	public String processContent(String content) {
		content = Normalizer.normalize(content, Normalizer.Form.NFD);
		content = content
				.replaceAll("[^\\x00-\\x7F]", "")
				.replaceAll("\\b\\w{1,3}\\b\\s?", "")
				.replaceAll("[^a-zA-Z0-9\\s]", "")
				.toLowerCase();

		String aux;

		ArrayList<String> words = new ArrayList<>(Arrays.asList(content.split("\\s+")));
		Collections.sort(words);
		content = "";

		// Remove repeated strings
		while (!words.isEmpty()) {
			if (content == "")
				content += words.get(0);
			else
				content += " " + words.get(0);

			aux = words.get(0);
			words.remove(0);

			while (words.contains(aux)) {
				words.remove(words.indexOf(aux));
			}
		}

		return content;
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
