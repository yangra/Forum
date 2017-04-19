package javazoo.forum.entity;


import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.lang.invoke.SerializedLambda;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private Set<Role> roles;
    private Set<Question> questions;
    private Set<Answer> answers;
    private String imagePath;
    private Date creationDate;

    public User(String username, String email, String fullName, String password, String imagePath){
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.imagePath = imagePath;
        this.creationDate = new Date();

        this.roles = new HashSet<>();
        this.questions = new HashSet<>();
        this.answers = new HashSet<>();
    }

    public User(){

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
        public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name="username",length = 30, unique = true, nullable = false)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "email", unique = true, nullable = false)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "fullName", nullable = false)
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Column(name="password",length = 60,nullable = false)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles")
    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role role){
        this.roles.add(role);
    }

    @OneToMany(mappedBy = "author")
    public Set<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<Question> questions) {
        this.questions = questions;
    }

    @OneToMany(mappedBy = "author")
    public Set<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(Set<Answer> answers) {
        this.answers = answers;
    }

    @Transient
    public boolean isAdmin(){
        return this.getRoles()
                .stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
    }

    @Transient
    public boolean isAuthor(Question question) {
        return Objects.equals(this.getId(),
        question.getAuthor().getId());
    }

    @Transient
    public boolean isAuthor(Answer answer) {
        return Objects.equals(this.getId(),
        answer.getAuthor().getId());
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Column
    @Type(type = "timestamp")
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
