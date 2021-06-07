/* Information class
* Contains the information shown on each row of the password manager
* This information includes:
* id: An id to identify the specific password/username/website combination
* url
* username
* password
 */
public class Information {
    //these variables are private to ensure encapsulation
    private String id, url, username, password;

    //constructors
    public Information(String id, String url, String username, String password) {
        this.id = id;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    // getter/setter functions for the private variables
    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

