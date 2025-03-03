package User;

import DTO.TradableDTO;

import java.util.Set;
import java.util.TreeMap;
import Exception.DataValidationException;

public class UserManager {
    private static UserManager instance;
    private TreeMap<String, User> users = new TreeMap<>();

    private UserManager() {}

    public static UserManager getInstance(){
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public void init(String[] usersIn) throws DataValidationException {
        if (usersIn == null) {
            throw new DataValidationException("init(String[] usersIn): usersIn cannot be null!");
        }

        for (String userId : usersIn) {
            users.put(userId, new User(userId));
        }
    }

    public User getUserById(String userId) throws DataValidationException {
        if (userId == null || !users.containsKey(userId)) {
            throw new DataValidationException("User not found.");
        }
        return users.get(userId);
    }

    public void updateTradable(String userId, TradableDTO o) {
        if (userId == null || userId.isEmpty()) {
            throw new DataValidationException("User ID cannot be null or empty.");
        }

        if (o == null) {
            throw new DataValidationException("TradableDTO cannot be null.");
        }

        User user = users.get(userId);
        if (user == null) {
            throw new DataValidationException("User with ID " + userId + " does not exist.");
        }
        user.updateTradable(o);
    }

    @Override
    public String toString(){
        StringBuilder returnStr = new StringBuilder();

        for (String user: users.keySet()){
            returnStr.append(String.format("%s", users.get(user).toString()));
        }

        return returnStr.toString();
    }
}
