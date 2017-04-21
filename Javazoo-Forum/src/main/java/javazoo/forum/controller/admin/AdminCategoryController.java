package javazoo.forum.controller.admin;

import javazoo.forum.bindingModel.CategoryBindingModel;
import javazoo.forum.bindingModel.CategoryOrderEditBindingModel;
import javazoo.forum.entity.Answer;
import javazoo.forum.entity.Category;
import javazoo.forum.entity.Question;
import javazoo.forum.entity.Subcategory;
import javazoo.forum.repository.AnswersRepository;
import javazoo.forum.repository.CategoryRepository;
import javazoo.forum.repository.QuestionRepository;
import javazoo.forum.repository.SubcategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private SubcategoryRepository subcategoryRepository;
    @Autowired
    private QuestionRepository questionRepository;


    @GetMapping("/")
    public String list(Model model) {
        List<Category> categories = this.categoryRepository.findAllByOrderByOrderNoAsc();

        model.addAttribute("categories", categories);
        model.addAttribute("view", "admin/category/list");

        return "base-layout";
    }

    @PostMapping("/")
    public String changeOrder(CategoryOrderEditBindingModel categoryOrderEditBindingModel) {
        int[] order = Arrays.stream(categoryOrderEditBindingModel.getList().split(",")).mapToInt(Integer::parseInt).toArray();
        for (int i = 1; i <= order.length; i++) {
            Category category = this.categoryRepository.findOne(order[i - 1]);
            category.setOrderNo(i);
            this.categoryRepository.saveAndFlush(category);
        }

        return "redirect:/admin/categories/";
    }

    @GetMapping("/create")
    public String create(Model model) {

        model.addAttribute("view", "admin/category/create");

        return "base-layout";
    }

    @PostMapping("/create")
    public String createProcess(CategoryBindingModel categoryBindingModel, RedirectAttributes redirectAttributes) {

        List<String> errors = validateCategoryName(categoryBindingModel);

        if(!errors.isEmpty()){
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:/admin/categories/create";
        }

        Category category = new Category(categoryBindingModel.getName());

        this.categoryRepository.saveAndFlush(category);

        return "redirect:/admin/categories/";
    }

    @GetMapping("/edit/{id}")
    public String edit(Model model, @PathVariable Integer id) {
        if (!this.categoryRepository.exists(id)) {
            return "redirect:/admin/categories/";
        }

        Category category = this.categoryRepository.findOne(id);

        model.addAttribute("category", category);
        model.addAttribute("view", "admin/category/edit");

        return "base-layout";
    }

    @PostMapping("/edit/{id}")
    public String editProcess(@PathVariable Integer id,
                              CategoryBindingModel categoryBindingModel,
                              RedirectAttributes redirectAttributes) {

        if (!this.categoryRepository.exists(id)) {
            return "redirect:/admin/categories/";
        }

        List<String> errors = validateCategoryName(categoryBindingModel);

        if(!errors.isEmpty()){
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:/admin/categories/edit/"+id;
        }

        Category category = this.categoryRepository.findOne(id);
        category.setName(categoryBindingModel.getName());

        this.categoryRepository.saveAndFlush(category);

        return "redirect:/admin/categories/";
    }

    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable Integer id) {
        if (!this.categoryRepository.exists(id)) {
            return "redirect:/admin/categories/";
        }

        Category category = this.categoryRepository.findOne(id);

        model.addAttribute("category", category);
        model.addAttribute("view", "admin/category/delete");

        return "base-layout";
    }

    @PostMapping("/delete/{id}")
    public String deleteProcess(@PathVariable Integer id) {
        if (!this.categoryRepository.exists(id)) {
            return "redirect:/admin/categories/";
        }

        Category category = this.categoryRepository.findOne(id);

        for (Question question : category.getQuestions()) {
            this.questionRepository.delete(question);
        }

        for (Subcategory subcategory : category.getSubcategories()) {
            this.subcategoryRepository.delete(subcategory);
        }

        this.categoryRepository.delete(category);

        return "redirect:/admin/categories/";
    }

    private List<String> validateCategoryName(CategoryBindingModel categoryBindingModel) {
        List<String> errors = new ArrayList<>();

        if (StringUtils.isEmpty(categoryBindingModel.getName())) {
            errors.add("Category name cannot be empty!");
        }

        if (categoryRepository.findByName(categoryBindingModel.getName()) != null) {
            errors.add("Category with this name already exists!");
        }

        return errors;
    }
}
