package javazoo.forum.controller;

import javazoo.forum.bindingModel.UserBindingModel;
import javazoo.forum.bindingModel.UserEditBindingModel;
import javazoo.forum.entity.Role;
import javazoo.forum.entity.User;
import javazoo.forum.repository.RoleRepository;
import javazoo.forum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@Transactional
public class UserController {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("view", "user/register");

        return "base-layout";
    }

    @PostMapping("/register")
    public String registerProcess(UserBindingModel userBindingModel, Model model){

        List<String> error = validateRegisterFields(userBindingModel);
        if (!error.isEmpty()){

           model.addAttribute("error", error);
           model.addAttribute("username", userBindingModel.getUsername());
           model.addAttribute("email", userBindingModel.getEmail());
           model.addAttribute("fullName", userBindingModel.getFullName());
           model.addAttribute("view", "user/register");
           return "base-layout";
        }

        if(isEmailInUseOrUsernameTaken(userBindingModel,error)){
            model.addAttribute("error", error);
            model.addAttribute("username", userBindingModel.getUsername());
            model.addAttribute("email", userBindingModel.getEmail());
            model.addAttribute("fullName", userBindingModel.getFullName());
            model.addAttribute("view", "user/register");
            return "base-layout";
        }

       BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

       String databaseImagePath ="/images/default.png";

       User user = new User(
               userBindingModel.getUsername(),
               userBindingModel.getEmail(),
               userBindingModel.getFullName(),
               bCryptPasswordEncoder.encode(userBindingModel.getPassword()),
               databaseImagePath
       );

       Role userRole = this.roleRepository.findByName("ROLE_USER");

       user.addRole(userRole);

       this.userRepository.saveAndFlush(user);

       return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("view", "user/login");

        return "base-layout";
    }

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logoutPage(HttpServletRequest request, HttpServletResponse response){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth != null){
            new SecurityContextLogoutHandler().logout(request,response, auth);
        }

        return "redirect:login?logout";
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public String profilePage(Model model){
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                                                                    .getAuthentication()
                                                                    .getPrincipal();

        User user = this.userRepository.findByUsername(principal.getUsername());

        model.addAttribute("user", user);
        model.addAttribute("view", "user/profile");

        return "base-layout";
    }

    @GetMapping("/edit/{id}")
    public String editProfile(@PathVariable Integer id, Model model) {

        User user = this.userRepository.findOne(id);

        model.addAttribute("user", user);
        model.addAttribute("view", "user/edit");

        return "base-layout";
    }

    @PostMapping("/edit/{id}")
    public String editProfileProcess(@PathVariable Integer id,
                                     UserEditBindingModel userBindingModel,
                                     RedirectAttributes redirectAttributes) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User user = this.userRepository.findByUsername(principal.getUsername());


        List<String> error = new ArrayList<>();

        String databaseImagePath = "/images/default.png";

        String[] allowedContentTypes = {
                "image/png",
                "image/jpeg",
                "image/jpg",
                "image/gif"
        };

        boolean isContentTypeAllowed = Arrays.asList(allowedContentTypes).contains(userBindingModel.getImage().getContentType());

        if (!userBindingModel.getImage().getOriginalFilename().equals("")&&isContentTypeAllowed) {
            String imagePath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\images\\";
            String filename = userBindingModel.getImage().getOriginalFilename();
            String savePath = imagePath + filename;
            File imageFile = new File(savePath);
            try {
                userBindingModel.getImage().transferTo(imageFile);
                databaseImagePath = "/images/" + filename;
            } catch (IOException e) {
                error.add(e.getMessage());
            }
        }else if(!userBindingModel.getImage().getOriginalFilename().equals("")&&!isContentTypeAllowed){
            error.add("The file you tried to upload is not an image!");
        }

        error.addAll(validateUserEdit(userBindingModel,user));
        if(!error.isEmpty()){
            redirectAttributes.addFlashAttribute("error", error);
            return "redirect:/edit/"+id;
        }

        user.setFullName(userBindingModel.getFullName());
        user.setEmail(userBindingModel.getEmail());
        user.setImagePath(databaseImagePath);

        this.userRepository.saveAndFlush(user);

        return "redirect:/profile";
    }

    private List<String> validateUserEdit(UserBindingModel userBindingModel,User principal){
        List<String> error = new ArrayList<>();

        if (!StringUtils.isEmpty(userBindingModel.getPassword())
                && !StringUtils.isEmpty(userBindingModel.getConfirmPassword())) {

            if (userBindingModel.getPassword().equals((userBindingModel.getConfirmPassword()))) {
                BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

                principal.setPassword(bCryptPasswordEncoder.encode(userBindingModel.getPassword()));
            }else {
                error.add("Passwords don't match!");
            }
        }
        if(userBindingModel.getFullName().equals("")){
            error.add("Please enter a valid full name!");
        }

        if(userBindingModel.getEmail().equals("")){
            error.add("Please enter a valid email!");
        }

        if(isEmailTaken(userBindingModel.getEmail(),principal)){
            error.add("This email is already in use by other user!");

        }

        return error;
    }

    private boolean isEmailTaken(String email, User principal){
        List<User> users = userRepository.findAll();
        for(User user :users){
            if(!principal.getUsername().equals(user.getUsername()) && user.getEmail().equals(email)){
                return true;
            }
        }
        return false;
    }
    private List<String> validateRegisterFields(UserBindingModel bindingModel)
    {
        List<String> error = new ArrayList<>();

        if(bindingModel.getUsername().equals("")){
            error.add("Please enter a valid username!");
        }

        if(bindingModel.getEmail().equals("")){
            error.add("Please enter a valid email!");
        }

        if(bindingModel.getFullName().equals("")){
            error.add("Please enter your full name!");
        }

        if(bindingModel.getPassword().equals("")||bindingModel.getConfirmPassword().equals("")){
            error.add("Please enter a password and confirm it!");
        }

        if (!bindingModel.getPassword().equals(bindingModel.getConfirmPassword())){
            error.add("Passwords don't match!");
        }

        return error;
    }

    private boolean isEmailInUseOrUsernameTaken(UserBindingModel bindingModel,List<String> error){
        List<User> users = userRepository.findAll();
        for (User user:users) {
            if (user.getUsername().equals(bindingModel.getUsername())){
                error.add("This username is already taken!");
                return true;
            }
            if(user.getEmail().equals(bindingModel.getEmail())){
                error.add("This email is already in use by other user!");
                return true;
            }
        }

        return false;
    }

}
