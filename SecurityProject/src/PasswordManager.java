import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.json.simple.*;
import org.json.simple.parser.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

//PasswordManager class includes the actual password manager scene if the login is successful
public class PasswordManager {

    private Scene scene;
    private ListView list;

    public PasswordManager() {
        HBox labels;
        VBox vbox;
        HBox buttons;
        Button delete, add;

        list = new ListView();
        list.setPrefWidth(1000);
        list.setPrefHeight(400);

        labels = new HBox();
        labels.getChildren().addAll(new Label("ID"), new Label("URL"), new Label("USERNAME"), new Label("PASSWORD"));
        labels.setSpacing(150);
        labels.setPadding(new Insets(10, 100, 10, 50));

        vbox = new VBox();
        vbox.setSpacing(10);

        buttons = new HBox();
        buttons.setSpacing(50);

        //delete button deletes the selected row
        delete = new Button("Delete");
        delete.setOnAction(actionEvent -> {
            Row selectedRow = (Row) list.getSelectionModel().getSelectedItem();
            list.getItems().remove(selectedRow);
            delete.setVisible(false);
        });

        //delete will only be visible when a row is selected
        delete.setVisible(false);
        list.setOnMouseClicked(mouseEvent -> {
            delete.setVisible(true);
        });

        //add button adds a new row
        add = new  Button("Add");
        add.setOnAction(actionEvent -> {
            list.getItems().add(new Row());
        });

        //gets the information from the decrypted json file
        getInformation();

        buttons.getChildren().addAll(add, delete);
        vbox.getChildren().addAll(labels, list, buttons);
        scene = new Scene(vbox, 800, 500);
    }

    public Scene getScene() {
        return scene;
    }

    //gets the information from the decrypted json file where all the password information are stored
    //adds the information to the list to be shown on the screen
    private boolean getInformation() {

        JSONParser parser = new JSONParser();
        try {
            //read the file
            byte[] bytesRead = Files.readAllBytes(Paths.get("files/information.json"));
            Object jsonArray = parser.parse(new String(bytesRead, "UTF-8"));

            //add the parsed information to the list
            JSONArray storedPasswords = (JSONArray)jsonArray;

            for(int i = 0; i < storedPasswords.size(); i++) {
                JSONObject jsonObject = (JSONObject)storedPasswords.get(i);
                String id = (String)jsonObject.get("id");
                String url = (String)jsonObject.get("url");
                String username = (String)jsonObject.get("username");
                String password = (String)jsonObject.get("password");
                Information info = new Information(id, url, username, password);
                list.getItems().add(new Row(info));
            }
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //this method gets the edited information from the list
    // and returns the json object as a string
    // so that it can be encrypted in Main class
    public String getJsonString() {
        String jsonString;
        ArrayList<Information> infoArray = new ArrayList<Information>();

        //traverse the list to get the all updated information
        for (Object item: list.getItems()) {
            Row row = (Row) item;
            infoArray.add(row.getInformation());
        }

        //create the json array to have the string in same format as json
        JSONArray jsonInfoArray = new JSONArray();
        for (int i = 0;i < infoArray.size() ; i++) {
            JSONObject information = new JSONObject();
            information.put("id", infoArray.get(i).getId());
            information.put("url",  infoArray.get(i).getUrl());
            information.put("username",  infoArray.get(i).getUsername());
            information.put("password",  infoArray.get(i).getPassword());
            jsonInfoArray.add(information);
        }

        jsonString = jsonInfoArray.toString();
        return jsonString;
    }
}
