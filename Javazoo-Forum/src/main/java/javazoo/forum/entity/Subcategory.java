package javazoo.forum.entity;


import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "subcategories")
public class Subcategory {

    private Integer id;
    private String name;
    private Category category;
    private Integer orderNo;
    private Set<Question> questions;

    public Subcategory() {
        this.questions = new HashSet<>();
    }

    public Subcategory(String name, Category category){
        this.name = name;
        this.orderNo = 5;
        this.category = category;

        this.questions = new HashSet<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne()
    @JoinColumn(nullable = false, name="categoryId")
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Column(nullable = false)
    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    @OneToMany(mappedBy = "subcategory")
    public Set<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<Question> questions) {
        this.questions = questions;
    }
}
