package javazoo.forum.controller;

import javazoo.forum.entity.Category;
import javazoo.forum.entity.Question;
import javazoo.forum.entity.Tag;
import javazoo.forum.repository.CategoryRepository;
import javazoo.forum.repository.QuestionRepository;
import javazoo.forum.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class TagController {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/tag/{id}")
    public String questionsWithTag(Model model, @PathVariable Integer id) {

        Tag tag = this.tagRepository.findOne(id);

        if (tag == null) {
            return "redirect:/";
        }

        List<Category> categories = this.categoryRepository.findAllByOrderByOrderNoAsc();

        List<Tag> allTags = this.tagRepository.findAll();
        allTags.sort((Tag t1, Tag t2) -> t2.getQuestions().size() - t1.getQuestions().size());
        List<Tag> tags = allTags.stream().limit(20).collect(Collectors.toList());

        List<Question> questions = tag.getQuestions()
                .stream()
                .sorted(Comparator.comparing(q -> q.getCreationDate()))
                .collect(Collectors.toList());
        Collections.reverse(questions);

        model.addAttribute("view", "tag/questions");
        model.addAttribute("questions", questions);
        model.addAttribute("tag", tag);
        model.addAttribute("tags", tags);
        model.addAttribute("categories", categories);

        return "base-layout";
    }

}
