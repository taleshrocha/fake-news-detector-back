package com.fakenewsdetector.model;

import java.util.*;
import java.text.Normalizer;
import java.util.Objects;
import java.lang.Math;
import java.lang.ArithmeticException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

import org.apache.commons.text.similarity.CosineDistance;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.commons.text.similarity.JaroWinklerDistance;

import com.fakenewsdetector.dao.NewsRepository;

@Entity
public class News {
	private @Id @GeneratedValue Long id;
	private String author;
	private String date;
	private @Lob @Column String content;
	private @Lob @Column String processedContent;
	private Double cosineRate;

	public News() {
	}

	public News(String author, String date, String content) {
		this.author = author;
		this.date = date;
		this.content = content;
		this.processedContent = processContent(this.content);
		this.cosineRate = null;
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

	public Double getCosineRate() {
		return cosineRate;
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

	public void setCosineRate(Double cosineRate) {
		this.cosineRate = cosineRate;
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

	public double processCosineRate(List<News> allNews) {
		CosineDistance cosineDistance = new CosineDistance();
		double rate = 0.0;

		for (News news : allNews) {
			rate = Math.max(
					cosineDistance
							.apply(this.getProcessedContent(), news.getProcessedContent()),
					rate);
		}

		return rate;
	}

	// ### Override ###

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
				&& Objects.equals(this.cosineRate, news.cosineRate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.author, this.date, this.content, this.cosineRate);
	}

	@Override
	public String toString() {
		return "News {"
				+ "id=" + this.id
				+ ", author=" + this.author
				+ ", date=" + this.date
				+ ", content=" + this.content
				+ ", cosineRate=" + this.cosineRate
				+ "}";
	}
}
