package ui.elements;

@Getter
public class UserBage extends BaseElement {
    private String username;
    private String role;

    public UserBage(SelenideElement element) {
        super(element);
        username = element.getText().split("\n")[0];
        role = element.getText().split("\n")[1];
    }
}