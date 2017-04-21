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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

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
    public String registerProcess(UserBindingModel userBindingModel){
       if (!userBindingModel.getPassword().equals(userBindingModel.getConfirmPassword())){
           return "redirect: /register";
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
    public String editProfileProcess(UserEditBindingModel userBindingModel) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User user = this.userRepository.findByUsername(principal.getUsername());

        if (!StringUtils.isEmpty(userBindingModel.getPassword())
                && !StringUtils.isEmpty(userBindingModel.getConfirmPassword())) {

            if (userBindingModel.getPassword().equals((userBindingModel.getConfirmPassword()))) {
                BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

                user.setPassword(bCryptPasswordEncoder.encode(userBindingModel.getPassword()));
            }
        }

        String databaseImagePath = null;

        String[] allowedContentTypes = {
                "image/png",
                "image/jpeg",
                "image/jpg"
        };

        boolean isContentTypeAllowed = Arrays.asList(allowedContentTypes).contains(userBindingModel.getImage().getContentType());

        if (isContentTypeAllowed) {
            String imagePath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\images\\";
            String filename = userBindingModel.getImage().getOriginalFilename();
            String savePath = imagePath + filename;
            File imageFile = new File(savePath);
            try {
                userBindingModel.getImage().transferTo(imageFile);
                databaseImagePath = "/images/" + filename;
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        user.setFullName(userBindingModel.getFullName());
        user.setEmail(userBindingModel.getEmail());
        user.setImagePath(databaseImagePath);

        this.userRepository.saveAndFlush(user);

        return "redirect:/";
    }

}
