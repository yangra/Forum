package javazoo.forum.controller.admin;

import javazoo.forum.bindingModel.UserEditBindingModel;
import javazoo.forum.entity.Answer;
import javazoo.forum.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.StringUtils;
import javazoo.forum.entity.Role;
import javazoo.forum.entity.User;
import javazoo.forum.repository.AnswersRepository;
import javazoo.forum.repository.QuestionRepository;
import javazoo.forum.repository.RoleRepository;
import javazoo.forum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswersRepository answersRepository;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/")
    public String listUsers(@PageableDefault(value = 20) Pageable pageable, Model model) {
        Page<User> users = this.userRepository.findAll(pageable);

        model.addAttribute("users", users);
        model.addAttribute("view", "admin/user/list");
        model.addAttribute("size", 20);
        return "base-layout";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        if (!this.userRepository.exists(id)) {
            return "redirect:/admin/users/";
        }

        User user = this.userRepository.findOne(id);
        List<Role> roles = this.roleRepository.findAll();

        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        model.addAttribute("view", "admin/user/edit");

        return "base-layout";
    }

    @PostMapping("/edit/{id}")
    public String editProcess(@PathVariable Integer id,
                              UserEditBindingModel userEditBindingModel,
                              RedirectAttributes redirectAttributes) {
        if (!this.userRepository.exists(id)) {
            return "redirect:/admin/users/";
        }

        User user = this.userRepository.findOne(id);

        List<String> errors = validateAdminUserEdit(userEditBindingModel, user);

        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:/admin/users/edit/" + id;
        }

        user.setFullName(userEditBindingModel.getFullName());
        user.setEmail(userEditBindingModel.getEmail());

        Set<Role> roles = new HashSet<>();

        for (Integer roleId : userEditBindingModel.getRoles()) {
            roles.add(this.roleRepository.findOne(roleId));
        }

        user.setRoles(roles);

        this.userRepository.saveAndFlush(user);

        return "redirect:/admin/users/";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, Model model) {
        if (!this.userRepository.exists(id)) {
            return "redirect:/admin/users/";
        }

        User user = this.userRepository.findOne(id);

        model.addAttribute("user", user);
        model.addAttribute("view", "admin/user/delete");

        return "base-layout";
    }

    @PostMapping("/delete/{id}")
    public String deleteProcess(@PathVariable Integer id) {
        if (!this.userRepository.exists(id)) {
            return "redirect:/admin/users/";
        }

        User user = this.userRepository.findOne(id);

        for (Answer answer : user.getAnswers()) {
            this.answersRepository.delete(answer);
        }

        for (Question question : user.getQuestions()) {
            this.questionRepository.delete(question);
        }

        this.userRepository.delete(user);

        return "redirect:/admin/users/";
    }

    private List<String> validateAdminUserEdit(UserEditBindingModel userEditBindingModel, User principal) {

        List<String> errors = new ArrayList<>();
        if (userEditBindingModel.getFullName().equals("")) {
            errors.add("Full name cannot be empty!");
        }

        if (userEditBindingModel.getEmail().equals("")) {
            errors.add("Email cannot be empty!");
        }

        if (userEditBindingModel.getUsername().equals("")) {
            errors.add("Username cannot be empty!");
        }

        if (!StringUtils.isEmpty(userEditBindingModel.getPassword())
                && !StringUtils.isEmpty(userEditBindingModel.getConfirmPassword())) {

            if (userEditBindingModel.getPassword().equals((userEditBindingModel.getConfirmPassword()))) {
                BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

                principal.setPassword(bCryptPasswordEncoder.encode(userEditBindingModel.getPassword()));
            } else {
                errors.add("The passwords don't match!");
            }
        }

        errors.addAll(emailInUseOrUsernameTaken(
                userEditBindingModel.getUsername(),
                userEditBindingModel.getEmail(),
                principal));

        return errors;
    }

    private List<String> emailInUseOrUsernameTaken(String username, String email, User principal) {
        List<String> errors = new ArrayList<>();
        List<User> users = this.userRepository.findAll();
        for (User user : users) {
            if (!principal.getEmail().equals(user.getEmail()) && user.getUsername().equals(username)) {
                errors.add("This username is already taken!");
            }

            if (!principal.getUsername().equals(user.getUsername()) && user.getEmail().equals(email)) {
                errors.add("This email is already in use by other user!");
            }
        }
        return errors;
    }
}
