package javazoo.forum.repository;


import javazoo.forum.entity.Answer;
import javazoo.forum.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswersRepository extends JpaRepository<Answer, Integer>{
    Page<Answer> findByQuestionOrderByCreationDateAsc(Question question, Pageable pageable);
}
