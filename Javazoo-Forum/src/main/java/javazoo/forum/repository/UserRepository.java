package javazoo.forum.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import javazoo.forum.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);
    User findByUsername(String username);
}
