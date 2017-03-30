package javazoo.forum.repository;


import javazoo.forum.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswersRepository extends JpaRepository<Answer, Integer>{
}
