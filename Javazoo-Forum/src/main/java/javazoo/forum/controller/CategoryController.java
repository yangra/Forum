package javazoo.forum.controller;

import javazoo.forum.entity.Category;
import javazoo.forum.entity.Question;
import javazoo.forum.entity.Subcategory;
import javazoo.forum.repository.CategoryRepository;
import javazoo.forum.repository.QuestionRepository;
import javazoo.forum.repository.SubcategoryRepository;
import javazoo.forum.viewModel.SubcategoryViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        List<Question> questions = new ArrayList<>();
        for(Subcategory subcategory:category.getSubcategories()) {
            List<Question> subQuestions = this.questionRepository.findAllBySubcategory(subcategory);
            for(int i=0;i<subQuestions.size();i++){
                questions.add(subQuestions.get(i));
            }
        }
//        Collections.sort(questions, (Question q1, Question q2) -> q2.getCreationDate().compareTo(q1.getCreationDate()));
//        Collections.sort(questions, new Comparator<Question>() {
//            public int compare(Question o1, Question o2) {
//                return o2.getCreationDate().compareTo(o1.getCreationDate());
//            }
//        });

        questions = questions.stream().sorted(Comparator.comparing(Question::getCreationDate).reversed()).collect(Collectors.toList());

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
        List<SubcategoryViewModel> subcategories = new ArrayList<>();
        Category category = this.categoryRepository.findOne(catId);
        List<Subcategory> subs = this.subcategoryRepository.findByCategory(category);
        for(int i = 0; i<subs.size();i++){
            SubcategoryViewModel sub = new SubcategoryViewModel();
            sub.setId(subs.get(i).getId());
            sub.setName(subs.get(i).getName());
            subcategories.add(sub);
        }
        return subcategories;

    }

}
