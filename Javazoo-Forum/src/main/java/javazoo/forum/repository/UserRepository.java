package javazoo.forum.repository;

/**
 * Created by danie on 3/24/2017.
 */

import org.springframework.data.jpa.repository.JpaRepository;
import javazoo.forum.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);
}
