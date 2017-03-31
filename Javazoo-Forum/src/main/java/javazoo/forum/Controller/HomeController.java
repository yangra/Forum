package javazoo.forum.controller;


import javazoo.forum.entity.Question;
import javazoo.forum.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private QuestionRepository questionRepository;

    @GetMapping("/")
    public String index(Model model){

        List<Question> questions = this.questionRepository.findAll();

        model.addAttribute("view", "home/index");
        model.addAttribute("questions", questions);

        return "base-layout";
    }
}
