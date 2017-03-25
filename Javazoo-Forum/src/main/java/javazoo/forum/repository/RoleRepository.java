package javazoo.forum.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import javazoo.forum.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String name);
}