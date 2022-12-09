@SpringBootApplication
public class BookmarkBeanExample {

    public static void main(String[] args) {
		SpringApplication.run(BookmarkBeanExample.class, args);
	}

    @Bean
    public BookmarkCapture bookmarkCapture() {
	    return new BookmarkCapture();
    }

    @Override
    public ReactiveTransactionManager reactiveTransactionManager(Driver driver, ReactiveDatabaseSelectionProvider databaseSelectionProvider) {
	    BookmarkCapture bookmarkCapture = bookmarkCapture();
	    return new ReactiveNeo4jTransactionManager(driver, databaseSelectionProvider, Neo4jBookmarkManager.create(bookmarkCapture));
    }
}
