package server.endpoints;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import server.auth.SecurityService;
import server.classification.ArticleClassifier;
import server.classification.Categories;
import server.endpoints.inputmodels.ArticleInputModel;
import server.endpoints.inputmodels.CommentInputModel;
import server.endpoints.outputmodels.ArticleListOutputModel;
import server.endpoints.outputmodels.ArticleOutputModel;
import server.entities.ArticleEntity;
import server.entities.CommentEntity;
import server.entities.UpvoteEntity;
import server.entities.UserEntity;
import server.repositories.ArticleRepository;
import server.repositories.CommentRepository;
import server.repositories.UpvoteRepository;
import server.repositories.UserRepository;
import server.services.ArticleService;
import server.services.NotificationService;
import server.utilities.StorageManager;

@RestController
public class ArticleController {
	
	@Autowired
	private StorageManager sm;
	
	@Autowired 
	private SecurityService secService;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private ArticleRepository articleRepo;
	
	@Autowired
	private CommentRepository commentRepo;
	
	@Autowired
	private UpvoteRepository upvoteRepo;
	
	@Autowired
	private ArticleClassifier articleClass;

	@Autowired
	private ArticleService articleService;
	
	@Autowired
	private NotificationService notificationService;
	
	//add a new article for the current user
	@PostMapping("/article/new")
	public ResponseEntity<Object> addArticle(@RequestBody ArticleInputModel input) {
		
		if (input.getText() == null || input.getTitle() == null
			|| input.getText().equals("") || input.getTitle().equals("")) {
			return new ResponseEntity<>("Title and text can't be null or empty", HttpStatus.BAD_REQUEST);
		}
		
		try {
			UserEntity currentUser = secService.currentUser();
			
			ArticleEntity article = new ArticleEntity();
			article.setTitle(input.getTitle());
			article.setText(input.getText());
			article.setMediafile(sm.storeFile(input.getMedia()));
			article.setUser(currentUser);
			article.setDateTime();
			Categories category = articleClass.classify(article, articleRepo.findAll());
			article.setCategories(category);
			articleRepo.save(article);
			
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (IOException e) {
			return new ResponseEntity<>("Could not save media file", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	//add a new comment to the article specified by articleId, with commentator the current user
	@PostMapping("/article/comment")
	public ResponseEntity<Object> addComment(@RequestBody CommentInputModel input) {
		
		UserEntity currUser = secService.currentUser();
		Optional<ArticleEntity> optArticle = articleRepo.findById(input.getArticleId());
		if (optArticle.isPresent()) {
			ArticleEntity refArticle = optArticle.get();
			CommentEntity comment = new CommentEntity();
			comment.setText(input.getText());
			comment.setArticle(refArticle);
			comment.setUser(currUser);
			comment.setDateTime();
			commentRepo.save(comment);
			String notificationMessage = "? ? commented on your post";
			notificationService.addNotification(refArticle.getUser(), currUser, refArticle, notificationMessage);
			return new ResponseEntity<>(HttpStatus.OK);
		}
		else {
			return new ResponseEntity<>("Could not find specified article", HttpStatus.NOT_FOUND);
		}
		
	}
	
	//upvote an article specified by its article id
	//can be easily transformed to also upvote comments in the future
	@PostMapping("/article/upvote")
	public ResponseEntity<Object> upvote(@RequestParam Long articleId) {
		
		UserEntity currUser = secService.currentUser();
		Optional<ArticleEntity> optArticle = articleRepo.findById(articleId);
		if (optArticle.isPresent()) {
			ArticleEntity refArticle = optArticle.get();
			if (upvoteRepo.findByArticleAndUser(refArticle, currUser) != null) {
				return new ResponseEntity<>("Article already upvoted", HttpStatus.CONFLICT);
			}
			UpvoteEntity upvote = new UpvoteEntity(currUser, refArticle);
			upvoteRepo.save(upvote);
			String notificationMessage = "? ? upvoted your post";
			notificationService.addNotification(refArticle.getUser(), currUser, refArticle, notificationMessage);
			try {
				return new ResponseEntity<>(articleService.convertUpvoteToOutput(upvote), HttpStatus.OK);
			} catch (IOException e) {
				return new ResponseEntity<>("Could not return saved upvote", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		else {
			return new ResponseEntity<>("Could not find specified article", HttpStatus.NOT_FOUND);
		}
		
	}
	
	//gets a list of articles for a given user
	//or for the active user, if no parameter is specified
	@GetMapping("/articles")
	public ResponseEntity<Object> getArticles(@RequestParam(defaultValue = "") String email) {
		
		UserEntity refUser;
		if (email.equals("")) {
			refUser = secService.currentUser();
		}
		else {
			refUser = userRepo.findByEmail(email);
		}
		
		if (refUser == null) {
			return new ResponseEntity<>("No user with specified email found", HttpStatus.BAD_REQUEST);
		}
		
		//this instead of refUser.getArticles() because we want to sort
		List<ArticleEntity> articles = articleRepo.findByUserOrderByDateTimeDesc(refUser);
		try {
			ArticleListOutputModel output = new ArticleListOutputModel();
			for (ArticleEntity a : articles) {
				ArticleOutputModel outA = articleService.convertArticleToOutput(a);
				output.addArticle(outA);
			}
			return new ResponseEntity<>(output, HttpStatus.OK);
		} catch (IOException e) {
			return new ResponseEntity<>("Could not load media file", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/article")
	public ResponseEntity<Object> getArticle(@RequestParam Long id) {
		ArticleEntity article = articleRepo.findById(id).orElse(null);
		if (article == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		try {
			ArticleOutputModel output = articleService.convertArticleToOutput(article);
			return new ResponseEntity<>(output, HttpStatus.OK);
		} catch (IOException e) {
			return new ResponseEntity<>("Could not load media file", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
		
	//gets a list of upvoted articles for a given user
	//or for the active user, if no parameter is specified
	@GetMapping("/upvoted")
	public ResponseEntity<Object> upvotes(@RequestParam(defaultValue = "") String email) {
		
		UserEntity refUser;
		if (email.equals("")) {
			refUser = secService.currentUser();
		}
		else {
			refUser = userRepo.findByEmail(email);
		}
		
		List<UpvoteEntity> upvoted = upvoteRepo.findByUser(refUser);
		List<ArticleEntity> articles = new ArrayList<>();
		for (UpvoteEntity u : upvoted) {
			articles.add(u.getArticle());
		}
		
		try {
			ArticleListOutputModel output = new ArticleListOutputModel();
			for (ArticleEntity a : articles) {
				ArticleOutputModel outA = articleService.convertArticleToOutput(a);
				output.addArticle(outA);
			}
			return new ResponseEntity<>(output, HttpStatus.OK);
		} catch (IOException e) {
			return new ResponseEntity<>("Could not load media file", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	//gets the feed for the active user in chronological order
	@GetMapping("/feed")
	public ResponseEntity<Object> getFeed() {
		
		UserEntity currUser = secService.currentUser();
		List<ArticleEntity> articles = articleService.getPersonalisedFeed(currUser);
		ArticleListOutputModel output = new ArticleListOutputModel();
		try {
			for (ArticleEntity a : articles) {
				ArticleOutputModel outA = articleService.convertArticleToOutput(a);
				output.addArticle(outA);
			}
			return new ResponseEntity<>(output, HttpStatus.OK);
		} catch (IOException e) {
			return new ResponseEntity<>("Could not load media file", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

}
