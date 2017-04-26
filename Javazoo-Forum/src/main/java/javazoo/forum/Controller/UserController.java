package javazoo.forum.Controller;

import javazoo.forum.bindingModel.UserBindingModel;
import javazoo.forum.bindingModel.UserEditBindingModel;
import javazoo.forum.entity.Role;
import javazoo.forum.entity.User;
import javazoo.forum.repository.RoleRepository;
import javazoo.forum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Controller
@Transactional
public class UserController {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("view", "user/register");

        return "base-layout";
    }

    @PostMapping("/register")
    public String registerProcess(UserBindingModel userBindingModel, Model model) {

        List<String> errors = validateRegisterFields(userBindingModel);
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("username", userBindingModel.getUsername());
            model.addAttribute("email", userBindingModel.getEmail());
            model.addAttribute("fullName", userBindingModel.getFullName());
            model.addAttribute("view", "user/register");
            return "base-layout";
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        String databaseImagePath = "/images/default.png";

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
    public String login(Model model) {
        model.addAttribute("view", "user/login");

        return "base-layout";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return "redirect:login?logout";
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public String profilePage(Model model) {
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


        List<String> errors = new ArrayList<>();

        String databaseImagePath = "/images/default.png";

        String[] allowedContentTypes = {
                "image/png",
                "image/jpeg",
                "image/jpg",
                "image/gif"
        };

        boolean isContentTypeAllowed = Arrays.asList(allowedContentTypes).contains(userBindingModel.getImage().getContentType());

        if (!userBindingModel.getImage().getOriginalFilename().equals("") && isContentTypeAllowed) {
            String imagePath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\images\\";
            String filename = userBindingModel.getImage().getOriginalFilename();
            String savePath = imagePath + filename;
            File imageFile = new File(savePath);
            try {
                // copy the new file to the images folder
                userBindingModel.getImage().transferTo(imageFile);
                // set the user profile to the new image
                databaseImagePath = "/images/" + filename;
            } catch (IOException e) {
                errors.add(e.getMessage());
            }

        } else if (!userBindingModel.getImage().getOriginalFilename().equals("") && !isContentTypeAllowed) {
            errors.add("The file you tried to upload is not an image!");
        }

        errors.addAll(validateUserEdit(userBindingModel, user));
        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:/edit/" + id;
        }

        user.setFullName(userBindingModel.getFullName());
        user.setEmail(userBindingModel.getEmail());
        user.setImagePath(databaseImagePath);

        this.userRepository.saveAndFlush(user);

        return "redirect:/profile";
    }

    private List<String> validateUserEdit(UserBindingModel userBindingModel, User principal) {
        List<String> errors = new ArrayList<>();

        if (!StringUtils.isEmpty(userBindingModel.getPassword())
                && !StringUtils.isEmpty(userBindingModel.getConfirmPassword())) {

            if (userBindingModel.getPassword().equals((userBindingModel.getConfirmPassword()))) {
                BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

                principal.setPassword(bCryptPasswordEncoder.encode(userBindingModel.getPassword()));
            } else {
                errors.add("Passwords don't match!");
            }
        }
        if (userBindingModel.getFullName().equals("")) {
            errors.add("Please enter a valid full name!");
        }

        if (userBindingModel.getEmail().equals("")) {
            errors.add("Please enter a valid email!");
        }

        if (isEmailTaken(userBindingModel.getEmail(), principal)) {
            errors.add("This email is already in use by other user!");

        }

        return errors;
    }

    private boolean isEmailTaken(String email, User principal) {
        List<User> users = this.userRepository.findAll();
        for (User user : users) {
            if (!principal.getUsername().equals(user.getUsername()) && user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    private List<String> validateRegisterFields(UserBindingModel bindingModel) {
        List<String> errors = new ArrayList<>();

        if (bindingModel.getUsername().equals("")) {
            errors.add("Please enter a valid username!");
        }

        if (bindingModel.getEmail().equals("")) {
            errors.add("Please enter a valid email!");
        }

        if (bindingModel.getFullName().equals("")) {
            errors.add("Please enter your full name!");
        }

        if (bindingModel.getPassword().equals("") || bindingModel.getConfirmPassword().equals("")) {
            errors.add("Please enter a password and confirm it!");
        }

        if (!bindingModel.getPassword().equals(bindingModel.getConfirmPassword())) {
            errors.add("Passwords don't match!");
        }

        if (this.userRepository.findByEmail(bindingModel.getEmail()) != null) {
            errors.add("This email is already in use by other user!");
        }

        if (this.userRepository.findByUsername(bindingModel.getUsername()) != null) {
            errors.add("This username is already taken!");
        }

        return errors;
    }

    @GetMapping("/images/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serverFile(@PathVariable String filename, Model model){

        try{
            Resource file = loadAsResource(filename);
            return ResponseEntity
                    .ok()
                    .body(file);
        }catch(Exception e){
            model.addAttribute("message", e.getMessage());
            return null;
        }
    }

    private Resource loadAsResource(String filename) throws Exception {

        Path file = getPath().resolve(filename);
        Resource resource = new UrlResource(file.toUri());
        if(resource.exists() || resource.isReadable()) {
            return resource;
        }
        return null;
    }

    private Path getPath() throws IOException{
        Path path = Paths.get( new File(".").getCanonicalPath() +"/src/main/resources/static/images/");
        return path;
    }

    private String uploadFile( MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            String type = file.getContentType();
            String suffix = "." + type.split("/")[1];
            String fileName = UUID.randomUUID().toString() + suffix;

            Files.copy(file.getInputStream(), getPath().resolve(fileName));

            String requestPath = "/images/";
            return requestPath + fileName;

        }
        return null;
    }
}
