package javazoo.forum.bindingModel;


import javax.validation.constraints.NotNull;

public class CategoryBindingModel {
    @NotNull
    private String name;
    @NotNull
    private Integer orderNo;

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
