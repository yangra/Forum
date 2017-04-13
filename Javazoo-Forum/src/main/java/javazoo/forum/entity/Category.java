package javazoo.forum.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
public class Category {
    private Integer id;
    private String name;
    private Integer orderNo;
    private Set<Subcategory> subcategories;
    private Set<Question> questions;

    public Category(){
        this.subcategories = new HashSet<>();
        this.questions = new HashSet<>();
    }

    public Category(String name, Integer orderNo){
        this.name = name;
        this.orderNo = orderNo;

        this.subcategories = new HashSet<>();
        this.questions= new HashSet<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(nullable = false, unique = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(nullable = false)
    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    @OneToMany(mappedBy = "category")
    public Set<Subcategory> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(Set<Subcategory> subcategories) {
        this.subcategories = subcategories;
    }

    @OneToMany(mappedBy = "category")
    public Set<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<Question> questions) {
        this.questions = questions;
    }
}
