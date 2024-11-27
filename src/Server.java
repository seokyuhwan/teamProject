import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends JFrame {
    private int port;
    private ServerSocket serverSocket;
    private JTextArea logArea;
    private JButton startButton;
    private JButton stopButton;
    private DataHandle dataHandle;
    private boolean running;

    public Server(int port) {
        this.port = port;
        this.dataHandle = new DataHandle(); // JSON 데이터 로드
        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("Recipe Server");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);

        JPanel controlPanel = new JPanel();
        startButton = new JButton("Start Server");
        stopButton = new JButton("Stop Server");
        stopButton.setEnabled(false);

        startButton.addActionListener(e -> startServer());
        stopButton.addActionListener(e -> stopServer());

        controlPanel.add(startButton);
        controlPanel.add(stopButton);
        add(scrollPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }
    private void startServer() {
        if (running) return;

        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                running = true;
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                appendLog("Server started on port " + port);

                while (running) {
                    Socket socket = serverSocket.accept();
                    appendLog("Client connected: " + socket.getInetAddress());
                    new Thread(() -> handleClient(socket)).start();
                }
            } catch (IOException e) {
                appendLog("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    private void stopServer() {
        running = false;
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        appendLog("Server stopped.");
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            appendLog("Error while stopping server: " + e.getMessage());
        }
    }

    private void handleClient(Socket socket) {
        try (ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream())) {

            Object request = input.readObject();
            if (request instanceof String) {
                String recipeName = (String) request;
                Recipe recipe = dataHandle.findRecipeByName(recipeName);
                if (recipe != null) {
                    output.writeObject(recipe);
                    appendLog("Sent recipe details for: " + recipeName);
                } else {
                    output.writeObject(null);
                    appendLog("Recipe not found: " + recipeName);
                }
            } else {
                List<String> recipeNames = dataHandle.getRecipeNames();
                output.writeObject(recipeNames);
                appendLog("Sent recipe list to client.");
            }
        } catch (IOException | ClassNotFoundException e) {
            appendLog("Client handling error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void appendLog(String message) {
        SwingUtilities.invokeLater(() -> logArea.append(message + "\n"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Server server = new Server(8080);
            server.setVisible(true);
        });
    }
}

class DataHandle {
    private List<Recipe> recipes = new ArrayList<>();

    public DataHandle() {
        loadRecipes();
    }

    private void loadRecipes() {
        JSONParser parser = new JSONParser();

        try (FileReader reader = new FileReader("./src/data.json")) {
            JSONArray jsonArray = (JSONArray) parser.parse(reader);
            for (Object obj : jsonArray) {
                JSONObject jsonObject = (JSONObject) obj;
                String foodName = (String) jsonObject.get("음식명");
                List<String> items = (JSONArray) jsonObject.get("재료");
                long time = (long) jsonObject.get("조리시간");
                List<String> steps = (JSONArray) jsonObject.get("조리방법");

                Recipe recipe = new Recipe(foodName, items, (int) time, steps);
                recipes.add(recipe);
            }
            System.out.println("Recipes loaded successfully.");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public Recipe findRecipeByName(String name) {
        return recipes.stream()
                .filter(recipe -> recipe.getFoodName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public List<String> getRecipeNames() {
        return recipes.stream()
                .map(Recipe::getFoodName)
                .toList();
    }
}

class Recipe implements Serializable {
    private String foodName;
    private List<String> items;
    private int time;
    private List<String> steps;

    public Recipe(String foodName, List<String> items, int time, List<String> steps) {
        this.foodName = foodName;
        this.items = items;
        this.time = time;
        this.steps = steps;
    }

    public String getFoodName() {
        return foodName;
    }

    public int getTime() {
        return time;
    }

    public List<String> getSteps() {
        return steps;
    }

    public List<String> getItems() {
        return items;
    }
}
