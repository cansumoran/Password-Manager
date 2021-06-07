import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

//Row class is an HBox where the information for the specific url/username/password is displayed

public class Row extends HBox {

    private TextField id, url, username, password;

    //constructors
    public Row() {
        id = new TextField();
        url = new TextField();
        username = new TextField();
        password = new TextField();
        this.setSpacing(10);
        this.getChildren().addAll(id, url, username, password);
    }

    public Row(Information information) {
        id = new TextField(information.getId());
        url = new TextField(information.getUrl());
        username = new TextField(information.getUsername());
        password = new TextField(information.getPassword());
        this.setSpacing(10);
        this.getChildren().addAll(id, url, username, password);
    }

    public Information getInformation() {
        return new Information(id.getText(), url.getText(), username.getText(), password.getText());
    }
}
