package javazoo.forum.bindingModel;


import javazoo.forum.entity.Category;
import javazoo.forum.entity.Subcategory;

import javax.validation.constraints.NotNull;

public class QuestionBindingModel {
    @NotNull
    private String title;

    @NotNull
    private String content;

    @NotNull
    private Integer categoryId;

    @NotNull
    private Integer subcategoryId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getSubcategoryId() {
        return subcategoryId;
    }

    public void setSubcategoryId(Integer subcategoryId) {
        this.subcategoryId = subcategoryId;
    }
}
