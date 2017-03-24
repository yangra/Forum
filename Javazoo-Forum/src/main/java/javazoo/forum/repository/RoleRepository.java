package javazoo.forum.repository;

/**
 * Created by danie on 3/24/2017.
 */

import org.springframework.data.jpa.repository.JpaRepository;
import javazoo.forum.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String name);
}