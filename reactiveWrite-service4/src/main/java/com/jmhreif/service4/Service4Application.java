package com.jmhreif.service4;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.neo4j.driver.Driver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.core.ReactiveDatabaseSelectionProvider;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;
import org.springframework.data.neo4j.core.transaction.ReactiveNeo4jTransactionManager;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE;

@SpringBootApplication
//@EnableReactiveNeo4jAuditing
public class Service4Application {

	public static void main(String[] args) {
		SpringApplication.run(Service4Application.class, args);
	}

	@Bean
	public ReactiveTransactionManager reactiveTransactionManager(Driver driver, ReactiveDatabaseSelectionProvider databaseNameProvider) {
		return new ReactiveNeo4jTransactionManager(driver, databaseNameProvider);
	}

}

@RestController
@RequestMapping("/neo")
@AllArgsConstructor
class ReviewController {
	private final ReviewRepository reviewRepo;

	@GetMapping
	String liveCheck() { return "Service4 is up"; }

	@GetMapping("/reviews")
	Flux<Review> getReviews() { return reviewRepo.findFirst1000By(); }

	@GetMapping("/reviews/{book_id}")
	Flux<Review> getBookReviews(@PathVariable String book_id) { return reviewRepo.findReviewsByBook(book_id); }

	@PutMapping("/save")
	Mono<Review> save(@RequestBody Review reviewMono) {
		return reviewRepo.save(reviewMono);
	}
}

interface ReviewRepository extends ReactiveCrudRepository<Review, String> {

	Flux<Review> findFirst1000By();

	@Query("MATCH (r:Review)-[rel:WRITTEN_FOR]->(b:Book {book_id: $book_id}) RETURN r;")
	Flux<Review> findReviewsByBook(String book_id);
}

@Data
@Node
class Review {
	@Id
	@GeneratedValue(UUIDStringGenerator.class)
	private String review_id;

	@NonNull
	private String book_id, review_text, user_id;
	@NonNull
	private Integer rating;

	private Integer n_comments = 0, n_votes = 0;

	@JsonFormat(without = {ADJUST_DATES_TO_CONTEXT_TIME_ZONE})
	private ZonedDateTime started_at, read_at;

//	@CreatedDate
//	@JsonFormat(without = {ADJUST_DATES_TO_CONTEXT_TIME_ZONE})
//	private Instant date_added;
//	@LastModifiedDate
//	@JsonFormat(without = {ADJUST_DATES_TO_CONTEXT_TIME_ZONE})
//	private Instant date_updated;
}