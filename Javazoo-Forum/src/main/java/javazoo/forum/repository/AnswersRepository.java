package javazoo.forum.repository;


import javazoo.forum.entity.Answer;
import javazoo.forum.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswersRepository extends JpaRepository<Answer, Integer>{
    List<Answer> findByQuestion(Question question);
}
