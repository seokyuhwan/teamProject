import java.io.*;
import java.util.HashMap;

public class UserDatabase {
    private static final String FILE_PATH = "users.txt";
    private HashMap<String, String> userMap = new HashMap<>();

    public UserDatabase() {
        loadUsers();
    }

    public boolean registerUser(String username, String password) {
        if (userMap.containsKey(username)) {
            return false; // 이미 존재하는 사용자
        }
        userMap.put(username, password);
        saveUsers();
        return true;
    }

    // 사용자 검증
    public boolean validateUser(String username, String password) {
        return password.equals(userMap.get(username));
    }

    // 데이터 저장
    private void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (String username : userMap.keySet()) {
                writer.write(username + ":" + userMap.get(username));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUsers() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    userMap.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
