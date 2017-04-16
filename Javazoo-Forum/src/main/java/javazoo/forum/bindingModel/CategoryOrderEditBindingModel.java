package javazoo.forum.bindingModel;


import javax.validation.constraints.NotNull;

public class CategoryOrderEditBindingModel {

    @NotNull
    private String list;

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }
}
