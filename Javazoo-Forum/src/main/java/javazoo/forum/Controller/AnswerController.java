package javazoo.forum.controller;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.NestedParticle;
import javazoo.forum.bindingModel.AnswerBindingModel;
import javazoo.forum.entity.Answer;
import javazoo.forum.entity.Question;
import javazoo.forum.entity.User;
import javazoo.forum.repository.AnswersRepository;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Controller
@Transactional
public class AnswerController {

    @Autowired
    private AnswersRepository answersRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QuestionRepository questionRepository;




    @GetMapping("question/{qId}/answer/create")
    @PreAuthorize("isAuthenticated()")
    public String create(Model model, @PathVariable Integer qId){
        model.addAttribute("view", "answer/create");
        model.addAttribute("qId", qId);
        return "base-layout";

    }

    @PostMapping("question/{qId}/answer/create")
    @PreAuthorize("isAuthenticated()")
    public String createProcess(AnswerBindingModel answerBindingModel,
                                @PathVariable Integer qId,
                                RedirectAttributes redirectAttributes){

        if(answerBindingModel.getContent().equals("")){
            List<String> error = new ArrayList<>();
            error.add("Please enter a valid content!");
            redirectAttributes.addFlashAttribute("error", error);
            return "redirect:/question/"+qId+"/answer/create/";
        }

        UserDetails user = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User userEntity = this.userRepository.findByUsername(user.getUsername());
        Question questionEntity = this.questionRepository.findOne(qId);

        Answer answerEntity = new Answer(
              answerBindingModel.getContent(),
                userEntity,
                questionEntity
        );

        this.answersRepository.saveAndFlush(answerEntity);

        return "redirect:/question/{qId}";
    }

    @GetMapping("question/{qId}/answer/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String edit(@PathVariable Integer id,@PathVariable Integer qId, Model model){

        if(!this.answersRepository.exists(id)){
            return "redirect:/";
        }
        Answer answer = this.answersRepository.findOne(id);

        if(!isUserAuthorOrAdmin(answer)){
            return "redirect:/question/"+qId;
        }

        model.addAttribute("view", "answer/edit");
        model.addAttribute("answer", answer);
        model.addAttribute("qId", qId);
        return "base-layout";
    }

    @PostMapping("question/{qId}/answer/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editProcess(@PathVariable Integer id,
                              @PathVariable Integer qId,
                              AnswerBindingModel answerBindingModel,
                              RedirectAttributes redirectAttributes){

        if (!this.answersRepository.exists(id)){
            return "redirect:/question/{qId}";
        }


        Answer answer = this.answersRepository.findOne(id);

        if(!isUserAuthorOrAdmin(answer)){
            return "redirect:/question/"+qId;
        }

        if(answerBindingModel.getContent().equals("")){
            List<String> error = new ArrayList<>();
            error.add("Please enter a valid new content!");
            redirectAttributes.addFlashAttribute("error", error);
            return "redirect:/question/"+qId+"/answer/edit/"+id;
        }

        answer.setContent(answerBindingModel.getContent());

        this.answersRepository.saveAndFlush(answer);

        return "redirect:/question/{qId}";
    }

    @GetMapping("question/{qId}/answer/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String delete(Model model, @PathVariable Integer id, @PathVariable Integer qId ){

        if (!this.answersRepository.exists(id)){
            return "redirect:/";
        }
        Answer answer = this.answersRepository.findOne(id);

        if(!isUserAuthorOrAdmin(answer)){
            return "redirect:/question/"+qId;
        }

        model.addAttribute("answer",answer );
        model.addAttribute("qId", qId);
        model.addAttribute("view", "answer/delete");

        return  "base-layout";
    }

    @PostMapping("question/{qId}/answer/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String deleteProcess(@PathVariable Integer id, @PathVariable Integer qId){

        if(!this.answersRepository.exists(id)){
            return "redirect:/";
        }
        Answer answer = this.answersRepository.findOne(id);


        if(!isUserAuthorOrAdmin(answer)){
            return "redirect:/question/"+qId;
        }

        this.answersRepository.delete(answer);

        return "redirect:/question/{qId}";

    }




    private boolean isUserAuthorOrAdmin(Answer answer) {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User userEntity = this.userRepository.findByUsername(user.getUsername());

        return userEntity.isAdmin() || userEntity.isAuthor(answer);
    }

}
