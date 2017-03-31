package javazoo.forum.bindingModel;


import javax.validation.constraints.NotNull;

public class AnswerBindingModel {

    @NotNull
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
