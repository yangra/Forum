package javazoo.forum.controller;

import javazoo.forum.entity.Category;
import javazoo.forum.entity.Question;
import javazoo.forum.entity.Subcategory;
import javazoo.forum.repository.CategoryRepository;
import javazoo.forum.repository.QuestionRepository;
import javazoo.forum.repository.SubcategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubcategoryRepository subcategoryRepository;

    @Autowired
    private QuestionRepository questionRepository;


    @GetMapping("categories/{id}")
    public String openCategory(@PathVariable Integer id, Model model){

        List<Category> categories = this.categoryRepository.findAllByOrderByOrderNoAsc();
        List<Subcategory> subcategories = this.subcategoryRepository.findAllByOrderByOrderNoAsc();
        Category category = this.categoryRepository.findOne(id);
        List<Question> questions = this.questionRepository.findByCategoryOrderByCreationDateDesc(category);

        model.addAttribute("categories", categories);
        model.addAttribute("questions", questions);
        model.addAttribute("subcategories", subcategories);
        model.addAttribute("catId", id);
        model.addAttribute("view", "categories/categories");
        return "base-layout";
    }

    @GetMapping("categories/{catId}/{subId}")
    public String openSubCategory(@PathVariable Integer catId, @PathVariable Integer subId, Model model){

        List<Category> categories = this.categoryRepository.findAllByOrderByOrderNoAsc();
        List<Subcategory> subcategories = this.subcategoryRepository.findAllByOrderByOrderNoAsc();
        Subcategory subcategory = this.subcategoryRepository.findOne(subId);
        List<Question> questions = this.questionRepository.findBySubcategoryOrderByCreationDateDesc(subcategory);

        model.addAttribute("categories", categories);
        model.addAttribute("subcategories", subcategories);
        model.addAttribute("questions", questions);
        model.addAttribute("catId", catId);
        model.addAttribute("subId", subId);
        model.addAttribute("view", "categories/categories");
        return "base-layout";
    }

    @RequestMapping(value = "/subcategories" )
    @ResponseBody
    public List getSubcategories(@RequestParam Integer catId) {
        List<Sub> subcats = new ArrayList<>();
        Category category= this.categoryRepository.findOne(catId);
        List<Subcategory> subs = this.subcategoryRepository.findByCategory(category);
        for(int i = 0; i<subs.size();i++){
            Sub sub = new Sub();
            sub.setId(subs.get(i).getId());
            sub.setName(subs.get(i).getName());
            subcats.add(sub);
        }
        return subcats;

    }

    private class Sub{
        private String name;
        private Integer id;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }
}
