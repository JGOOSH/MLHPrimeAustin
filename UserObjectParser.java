import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class UserObjectParser {

    public class User {
        private String username;
        private String name;
        private String phoneNumber;
        private String address;
        private String age;
        private String bio;

        public User() {
            this.username = "";
            this.name = "";
            this.phoneNumber = "";
            this.address = "";
            this.age = "";
            this.bio = "";
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public void setBio(String bio) {
            this.bio = bio;
        }

        public String getUsername() {
            return this.username;
        }

        public String getName() {
            return this.name;
        }

        public String getPhoneNumber() {
            return this.phoneNumber;
        }

        public String getAddress() {
            return this.address;
        }

        public String getAge() {
            return this.age;
        }

        public String getBio() {
            return this.bio;
        }

        @Override
        public String toString() {
            return this.username+" "+this.name+" "+this.phoneNumber+" "+this.address+" "+this.age+" "+this.bio;
        }
    }

    private HashMap<String, User> users;

    private String results;

    private void sendGet() throws Exception {
        String url = "http://127.0.0.1:5000/curUser";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = in.readLine()) != null) {
            result.append(line);
        }
        this.results = result.toString();
    }

    public UserObjectParser() {
        try {
            sendGet();
        } catch(Exception e) {
            System.err.println(e);
        }
        this.users = new HashMap<>();

        LinkedList<ArrayList<String>> tokens = new LinkedList<>();
        int start = -1;
        tokens.add(new ArrayList<>());
        for(int i = 0; i < this.results.length(); i++) {
            if(this.results.charAt(i) == ']') {
                if(i+1 < this.results.length() && this.results.charAt(i+1) == ']') {
                    tokens.add(new ArrayList<>());
                }
            } else if(this.results.charAt(i) == '\'') {
                if(start == -1) {
                    start = i+1;
                } else {
                    tokens.getLast().add(this.results.substring(start, i));
                    start = -1;
                }
            }
        }
        for(ArrayList<String> token : tokens) {
            String username = token.get(0);
            User user = new User();
            user.setUsername(username);
            user.setName(token.get(1));
            user.setPhoneNumber(token.get(2));
            user.setAge(token.get(3));
            user.setAddress(token.get(4));
            user.setBio(token.get(5));
            this.users.put(username, user);
        }
    }

    public User getUserObject(String username) {
        return this.users.get(username);
    }
}