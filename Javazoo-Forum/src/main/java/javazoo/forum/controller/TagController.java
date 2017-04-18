package javazoo.forum.controller;

import javazoo.forum.entity.Tag;
import javazoo.forum.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class TagController {

    @Autowired
    private TagRepository tagRepository;

    @GetMapping("/tag/{name}")
    public String questionsWithTag(Model model, @PathVariable String name){

        Tag tag = this.tagRepository.findByName(name);

        if(tag==null){
            return "redirect:/";
        }

        model.addAttribute("view", "tag/questions");
        model.addAttribute("tag", tag);

        return "base-layout";
    }

}
