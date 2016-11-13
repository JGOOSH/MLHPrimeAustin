import java.util.*;

public class UserObjectParser {

    public class User {
        private String username;
        private String name;
        private String phoneNumber;
        private String address;
        private String age;
        private String bio;
        private LinkedList<String> interests;

        public User() {
            this.username = "";
            this.name = "";
            this.phoneNumber = "";
            this.address = "";
            this.age = "";
            this.bio = "";
            this.interests = new LinkedList<>();
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

        public void addInterest(String interest) {
            this.interests.add(interest);
        }

        public void addInterest(int index, String interest) {
            this.interests.add(index, interest);
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

        public LinkedList<String> getInterests() {
            return this.interests;
        }
    }

    private HashMap<String, User> users;

    public UserObjectParser() {
        this.users = new HashMap<>();
    }

    public UserObjectParser(String totUsers) {
        this();
        LinkedList<ArrayList<String>> tokens = new LinkedList<>();
        int start = -1;
        tokens.add(new ArrayList<>());
        for(int i = 0; i < totUsers.length(); i++) {
            if(totUsers.charAt(i) == ']') {
                if(i+1 < totUsers.length() && totUsers.charAt(i+1) == ']') {
                    tokens.add(new ArrayList<>());
                }
            } else if(totUsers.charAt(i) == '\'') {
                if(start == -1) {
                    start = i+1;
                } else {
                    tokens.getLast().add(totUsers.substring(start, i));
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
            for (int i = 6; i < token.size(); i++) {
                user.addInterest(token.get(i));
            }
            this.users.put(username, user);
        }
    }

    public User getUserObject(String username) {
        return this.users.get(username);
    }
}