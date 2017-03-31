package javazoo.forum.repository;


import javazoo.forum.entity.Answer;
import javazoo.forum.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
}
