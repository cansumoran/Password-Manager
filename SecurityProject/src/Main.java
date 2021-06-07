import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/* main class includes the encryption/decryption of the password information
* it also controls the login page and overall scene transition from login to password manager
 */
public class Main extends Application {

    private PasswordField password;
    private BouncyCastleProvider provider;
    private PasswordManager manager;

    //this method is called when the javafx application is initialized
    public void start(Stage primaryStage) {
        Label loginPrompt;
        Button login;
        Scene loginScene;
        GridPane grid;

        //add bouncy castle as provider
        provider = new BouncyCastleProvider();

        //create login page
        password = new PasswordField();
        loginPrompt = new Label();
        login = new Button("Login");

        /*this variable checks if the login is done for the first time
        * this is done by checking if an encrypted password information file exists
        * if the file exists, it is not the first time that the user is logging in
        * therefore the password is used to decrypt the encrypted password information
        * if it is indeed the first time the user is logging in,
        * the entered password is used to encrypt a new password information file
         */
        boolean firstLogin = false;
        if(Security.encryptedFileExists()) {
            loginPrompt.setText("Please enter password:");
        }
        //logging in for the first time
        else {
            loginPrompt.setText("Welcome! Please set a password:");
            firstLogin = true;
        }

        boolean finalFirstLogin = firstLogin;

        //login button
        login.setOnAction(actionEvent -> {
            //the user can't enter empty password
            if(password.getText().equals("")) {
                Dialog emptyPassword = new Dialog();
                Label label = new Label("YOU DIDN'T ENTER PASSWORD");
                emptyPassword.getDialogPane().getButtonTypes().add(ButtonType.OK);
                emptyPassword.getDialogPane().setContent(label);
                emptyPassword.show();
            }
            else {
                //if it is first time logging in OR if the password is correct, the password manager opens
                if(!finalFirstLogin && Security.decrypt(provider, password.getText()) || finalFirstLogin) {
                    manager = new PasswordManager();
                    primaryStage.setScene(manager.getScene());
                }
                //wrong password, shows a warning to the user that the password is incorrect
                else {
                    Dialog wrongPassword = new Dialog();
                    Label label = new Label("WRONG PASSWORD");
                    wrongPassword.getDialogPane().getButtonTypes().add(ButtonType.OK);
                    wrongPassword.getDialogPane().setContent(label);
                    wrongPassword.show();
                }
            }
        });

        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.add(loginPrompt, 0, 0);
        grid.add(password, 1, 0);
        grid.add(login, 1, 1);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        loginScene = new Scene(grid, 700, 150);
        primaryStage.setTitle("Password Manager");
        primaryStage.setScene(loginScene);

        //when closing the program, encrypts the new updated information and deletes the decrypted version
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>()
        {
            @Override
            public void handle(WindowEvent windowEvent) {
                if(manager == null) {
                    Security.deleteDecryptedFile();
                }
                else {
                    Security.deleteDecryptedFile();
                    Security.encrypt(provider, password.getText(), manager.getJsonString());
                }
            }
        });
        primaryStage.show();
    }
}
