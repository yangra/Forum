package javazoo.forum.Controller;

import javazoo.forum.bindingModel.AnswerBindingModel;
import javazoo.forum.entity.Answer;
import javazoo.forum.entity.User;
import javazoo.forum.repository.AnswersRepository;
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
public class AnswerController {

    @Autowired
    private AnswersRepository answersRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/answer/create")
    @PreAuthorize("isAuthenticated()")
    public String create(Model model){
        model.addAttribute("view", "answer/create");

        return "base-layout";

    }

    @PostMapping("/answer/create")
    @PreAuthorize("isAuthenticated()")
    public String createProcess(AnswerBindingModel answerBindingModel){

        UserDetails user = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User userEntity = this.userRepository.findByUsername(user.getUsername());

        Answer answerEntity = new Answer(
              answerBindingModel.getContent(),
                userEntity
        );

        this.answersRepository.saveAndFlush(answerEntity);

        return "redirect:/";
    }

    @GetMapping("/answer/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String edit(@PathVariable Integer id, Model model){

        if(!this.answersRepository.exists(id)){
            return "redirect:/";
        }
        Answer answer = this.answersRepository.findOne(id);
        model.addAttribute("view", "answer/edit");
        model.addAttribute("answer", answer);

        return "base-layout";
    }
    @PostMapping("answer/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editProcess(@PathVariable Integer id, AnswerBindingModel answerBindingModel){
        if (!this.answersRepository.exists(id)){
            return "redirect:/";
        }

        Answer answer = this.answersRepository.findOne(id);

        answer.setContent(answerBindingModel.getContent());


        this.answersRepository.saveAndFlush(answer);

        return "redirect:/answer/" + answer.getId();
    }

}
