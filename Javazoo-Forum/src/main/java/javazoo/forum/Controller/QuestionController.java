package javazoo.forum.Controller;

import javazoo.forum.bindingModel.QuestionBindingModel;
import javazoo.forum.entity.Question;
import javazoo.forum.entity.User;
import javazoo.forum.repository.QuestionRepository;
import javazoo.forum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class QuestionController {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/question/create")
    @PreAuthorize("isAuthenticated()")
    public String create(Model model){
        model.addAttribute("view", "question/create");

        return "base-layout";
    }

    @PostMapping("/question/create")
    @PreAuthorize("isAuthenticated()")
    public String createProcess(QuestionBindingModel questionBindingModel){
        UserDetails user = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User userEntity = this.userRepository.findByUsername(user.getUsername());

        Question questionEntity = new Question(
                        questionBindingModel.getTitle(),
                        questionBindingModel.getContent(),
                        userEntity
        );

        this.questionRepository.saveAndFlush(questionEntity);

        return "redirect:/";
    }

    @GetMapping("/question/{id}")
    public String details(Model model, @PathVariable Integer id){
        if (!this.questionRepository.exists(id)){
            return "redirect:/";
        }

        Question question = this.questionRepository.findOne(id);

        model.addAttribute("question", question);
        model.addAttribute("view", "question/details");

        return "base-layout";
    }

    @GetMapping("/question/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String edit(@PathVariable Integer id, Model model){
        if(!this.questionRepository.exists(id)){
            return "redirect:/";
        }

        Question question = this.questionRepository.findOne(id);

        model.addAttribute("view", "question/edit");
        model.addAttribute("question", question);

        return "base-layout";
    }

    @PostMapping("question/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editProcess(@PathVariable Integer id, QuestionBindingModel questionBindingModel){
        if (!this.questionRepository.exists(id)){
            return "redirect:/";
        }

        Question question = this.questionRepository.findOne(id);

        question.setContent(questionBindingModel.getContent());
        question.setTitle(questionBindingModel.getTitle());

        this.questionRepository.saveAndFlush(question);

        return "redirect:/question/" + question.getId();
    }
}
