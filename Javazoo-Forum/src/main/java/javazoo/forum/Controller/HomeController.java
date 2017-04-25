package javazoo.forum.controller;


import javazoo.forum.entity.Category;
import javazoo.forum.entity.Question;
import javazoo.forum.entity.Tag;
import javazoo.forum.repository.CategoryRepository;
import javazoo.forum.repository.QuestionRepository;
import javazoo.forum.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Transactional
public class HomeController {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @GetMapping("/")
    public String index(@PageableDefault(value = 5) Pageable pageable, Model model){

        List<Category> categories = this.categoryRepository.findAllByOrderByOrderNoAsc();
        Page<Question> questions = this.questionRepository.findAllByOrderByCreationDateDesc(pageable);

        List<Tag> allTags = this.tagRepository.findAll();
        allTags.sort((Tag t1,Tag t2)-> t2.getQuestions().size()-t1.getQuestions().size());
        List<Tag> tags = allTags.stream().limit(20).collect(Collectors.toList());



        model.addAttribute("view", "home/index");
        model.addAttribute("questions", questions);
        model.addAttribute("categories", categories);
        model.addAttribute("tags", tags);
        model.addAttribute("size", 5);


        return "base-layout";
    }

    @RequestMapping("/error/403")
    public String accessDenied(Model model) {
        model.addAttribute("view", "error/403");

        return "base-layout";
    }


}
