package javazoo.forum.repository;


import javazoo.forum.entity.Answer;
import javazoo.forum.entity.Category;
import javazoo.forum.entity.Question;
import javazoo.forum.entity.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
    List<Question> findByCategoryOrderByCreationDateDesc(Category category);
    List<Question> findBySubcategoryOrderByCreationDateDesc(Subcategory subcategory);
    List<Question> findAllByOrderByCreationDateDesc();
}
