package javazoo.forum.Controller;

import javazoo.forum.entity.Category;
import javazoo.forum.entity.Question;
import javazoo.forum.entity.Tag;
import javazoo.forum.repository.CategoryRepository;
import javazoo.forum.repository.QuestionRepository;
import javazoo.forum.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class TagController {

    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/tag/{name}")
    public String questionsWithTag(Model model, @PathVariable String name){

        Tag tag = this.tagRepository.findByName(name);

        if(tag==null){
            return "redirect:/";
        }

        List<Category> categories = this.categoryRepository.findAllByOrderByOrderNoAsc();
       List<Question> questions = this.questionRepository.findAllByOrderByCreationDateDesc();

        model.addAttribute("view", "tag/questions");
        model.addAttribute("tag", tag);
        model.addAttribute("categories",categories );
        model.addAttribute("questions", questions);


        return "base-layout";
    }

}
