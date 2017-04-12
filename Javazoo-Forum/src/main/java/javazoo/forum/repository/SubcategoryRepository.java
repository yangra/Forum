package javazoo.forum.repository;

import javazoo.forum.entity.Category;
import javazoo.forum.entity.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SubcategoryRepository extends JpaRepository<Subcategory,Integer> {
    List<Subcategory> findByCategory(Category category);
    List<Subcategory> findAllByOrderByOrderNoAsc();
}
