package javazoo.forum.bindingModel;


import javax.validation.constraints.NotNull;

public class SubcategoryBindingModel {
    @NotNull
    private Integer categoryId;
    @NotNull
    private String name;
    @NotNull
    private Integer orderNo;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }
}
