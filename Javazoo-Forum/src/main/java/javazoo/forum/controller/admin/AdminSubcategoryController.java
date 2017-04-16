package javazoo.forum.controller.admin;

import javazoo.forum.bindingModel.CategoryOrderEditBindingModel;
import javazoo.forum.bindingModel.SubcategoryBindingModel;
import javazoo.forum.entity.Category;
import javazoo.forum.entity.Subcategory;
import javazoo.forum.repository.CategoryRepository;
import javazoo.forum.repository.QuestionRepository;
import javazoo.forum.repository.SubcategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/admin/subcategories")
public class AdminSubcategoryController {

    @Autowired
    private SubcategoryRepository subcategoryRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/")
    public String list(Model model){

        List<Category> categories = this.categoryRepository.findAllByOrderByOrderNoAsc();
        List<Subcategory> subcategories = this.subcategoryRepository.findAllByOrderByOrderNoAsc();

        model.addAttribute("categories", categories);
        model.addAttribute("subcategories", subcategories);
        model.addAttribute("view", "admin/subcategory/list");
        return "base-layout";
    }

    @PostMapping("/")
    public String changeOrderSubcategories(CategoryOrderEditBindingModel categoryOrderEditBindingModel){

        int[] order = Arrays.stream(categoryOrderEditBindingModel.getList().split(",")).mapToInt(Integer::parseInt).toArray();
        for(int i = 1; i<=order.length;i++){
            Subcategory subcategory = this.subcategoryRepository.findOne(order[i-1]);
            subcategory.setOrderNo(i);
            this.subcategoryRepository.saveAndFlush(subcategory);
        }

        return "redirect:/admin/subcategories/";
    }

    @GetMapping("/create")
    public String create(Model model){

        List<Category> categories = this.categoryRepository.findAll();

        model.addAttribute("categories", categories);
        model.addAttribute("view", "admin/subcategory/create");

        return "base-layout";
    }

    @PostMapping("/create")
    public String createProcess(SubcategoryBindingModel subcategoryBindingModel){
        if(StringUtils.isEmpty(subcategoryBindingModel.getName())){
            return "redirect:/admin/subcategories/create";
        }


        Category category = this.categoryRepository.findOne(subcategoryBindingModel.getCategoryId());
        Subcategory subcategory = new Subcategory(subcategoryBindingModel.getName(),category);

        this.subcategoryRepository.saveAndFlush(subcategory);

        return "redirect:/admin/subcategories/";
    }
}
