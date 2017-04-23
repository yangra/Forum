package javazoo.forum.repository;


import javazoo.forum.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
    Page<Question> findByCategoryOrderByCreationDateDesc(Category category, Pageable pageable);
    Page<Question> findBySubcategoryOrderByCreationDateDesc(Subcategory subcategory, Pageable pageable);
    Page<Question> findAllByOrderByCreationDateDesc(Pageable pageable);
}
