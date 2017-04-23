package javazoo.forum.controller;


import javazoo.forum.entity.Category;
import javazoo.forum.entity.Question;
import javazoo.forum.repository.CategoryRepository;
import javazoo.forum.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.transaction.Transactional;
import java.util.List;

@Controller
@Transactional
public class HomeController {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/")
    public String index(@PageableDefault(value = 5) Pageable pageable, Model model){

        List<Category> categories = this.categoryRepository.findAllByOrderByOrderNoAsc();
        Page<Question> questions = this.questionRepository.findAllByOrderByCreationDateDesc(pageable);

        model.addAttribute("view", "home/index");
        model.addAttribute("questions", questions);
        model.addAttribute("categories", categories);
        model.addAttribute("size", 5);
        return "base-layout";
    }

    @RequestMapping("/error/403")
    public String accessDenied(Model model) {
        model.addAttribute("view", "error/403");

        return "base-layout";
    }
}
