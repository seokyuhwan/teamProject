import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.List;

public class Recipe extends JPanel {
    private JTextField timeField;
    private JTextField searchField;
    private JButton resetButton;
    private JButton searchButton, timerButton;
    private JButton uploadButton;
    private JList<String> recipeList;
    private Main mainFrame;
    private HashMap<String, Integer> NameAndTimes;
    private HashMap<String, String> RecipeTexts;

    public Recipe(Main mainFrame, HashMap<String, Integer> NameAndTimes, HashMap<String, String> RecipeTexts) {
        this.mainFrame = mainFrame;
        this.NameAndTimes = NameAndTimes;
        this.RecipeTexts = RecipeTexts;

        setLayout(new BorderLayout());
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        searchField = new JTextField(10);
        timeField = new JTextField(5);
        timeField.setToolTipText("조리 시간 입력 (분)");

        searchButton = new JButton("검색");
        searchButton.addActionListener(e -> filterRecipes());

        resetButton = new JButton("새로고침");
        resetButton.addActionListener(e -> resetFilters());

        inputPanel.add(new JLabel("이름:"));
        inputPanel.add(searchField);
        inputPanel.add(new JLabel("최대조리 시간:"));
        inputPanel.add(timeField);
        inputPanel.add(searchButton);
        inputPanel.add(resetButton);

        add(inputPanel, BorderLayout.NORTH);

        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));

        JPanel uploadPanel = new JPanel();
        uploadPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        //레시피 업로드 버튼
        uploadButton = new JButton("레시피 업로드");

        uploadButton.addActionListener(e -> uploadRecipe());
        sidePanel.add(uploadButton);

        sidePanel.add(Box.createVerticalStrut(20));

        // 타이머 버튼
        timerButton = new JButton("타이머");

        timerButton.addActionListener(e -> StopWatch.startStopWatch());
        sidePanel.add(timerButton);

        add(sidePanel, BorderLayout.EAST);

        recipeList = new JList<>(NameAndTimes.keySet().toArray(new String[0]));
        recipeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recipeList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedRecipe = recipeList.getSelectedValue();
                if (selectedRecipe != null) {
                    mainFrame.showImageViewer(selectedRecipe);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(recipeList);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void filterRecipes() {
        try {
            String timeText = timeField.getText().trim();
            int time = Integer.MAX_VALUE;
            if (!timeText.isEmpty()) {
                time = Integer.parseInt(timeText);
            }
            String searchText = searchField.getText().toLowerCase();
            ArrayList<String> filteredRecipes = new ArrayList<>();

            for (String key : NameAndTimes.keySet()) {
                int value = NameAndTimes.get(key);
                if ((value <= time || timeText.isEmpty()) && (searchText.isEmpty() || key.toLowerCase().contains(searchText))) {
                    filteredRecipes.add(key);
                }
            }

            recipeList.setListData(filteredRecipes.toArray(new String[0]));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "유효한 숫자를 입력하세요.", "입력 오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetFilters() {
        timeField.setText("");
        searchField.setText("");
        recipeList.setListData(NameAndTimes.keySet().toArray(new String[0]));
    }
    private void uploadRecipe() {
        JFrame uploadFrame = new JFrame("레시피 업로드");
        uploadFrame.setSize(500, 500);
        uploadFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        uploadFrame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField nameField = new JTextField(10);
        JTextField timeField = new JTextField(10);
        JTextArea contextArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(contextArea);

        JButton uploadImageButton = new JButton("이미지 업로드");
        JButton enter = new JButton("저장");

        DefaultListModel<String> imageListModel = new DefaultListModel<>();
        JList<String> imageList = new JList<>(imageListModel);
        JScrollPane imageScrollPane = new JScrollPane(imageList);

        inputPanel.add(new JLabel("레시피 이름: "));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("조리 시간: "));
        inputPanel.add(timeField);

        uploadFrame.add(inputPanel, BorderLayout.NORTH);
        uploadFrame.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(uploadImageButton, BorderLayout.NORTH);
        bottomPanel.add(imageScrollPane, BorderLayout.CENTER);
        bottomPanel.add(enter, BorderLayout.SOUTH);

        uploadFrame.add(bottomPanel, BorderLayout.SOUTH);
        uploadFrame.setVisible(true);
        List<File> uploadedImages = new ArrayList<>();

        uploadImageButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.setFileFilter(new FileNameExtensionFilter("이미지 파일", "jpg", "jpeg", "png"));
            int result = fileChooser.showOpenDialog(uploadFrame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File[] selectedFiles = fileChooser.getSelectedFiles();
                for (File file : selectedFiles) {
                    uploadedImages.add(file);
                    imageListModel.addElement(file.getName());
                }
                JOptionPane.showMessageDialog(uploadFrame, "이미지가 추가되었습니다!", "성공", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        enter.addActionListener(e -> {
            String name = nameField.getText().trim();
            String time = timeField.getText().trim();
            String context = contextArea.getText().trim();

            if (name.isEmpty() || time.isEmpty() || context.isEmpty()) {
                JOptionPane.showMessageDialog(uploadFrame, "모든 필드를 채워주세요!", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (NameAndTimes.containsKey(name)) {
                JOptionPane.showMessageDialog(uploadFrame, "이미 존재하는 레시피 이름입니다. 다른 이름을 입력하세요.", "중복 오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                File recipesDir = new File("./src/Recipes/" + name);
                if (!recipesDir.exists()) {
                    recipesDir.mkdirs();
                }

                File recipeFile = new File("./src/Recipes/", name + ".txt");
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(recipeFile))) {
                    writer.write("레시피 이름: " + name + "\n");
                    writer.write("조리 시간: " + time + "분\n");
                    writer.write(context);
                }

                if (!uploadedImages.isEmpty()) {
                    int imageCounter = 1;
                    for (File image : uploadedImages) {
                        String newFileName = name + imageCounter + ".jpg";
                        File destImage = new File(recipesDir, newFileName);
                        Files.copy(image.toPath(), destImage.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        imageCounter++;
                    }
                }

                int cookTime = Integer.parseInt(time);
                NameAndTimes.put(name, cookTime);
                RecipeTexts.put(name, context);
                recipeList.setListData(NameAndTimes.keySet().toArray(new String[0]));

                JOptionPane.showMessageDialog(uploadFrame, "레시피가 저장되었습니다!", "성공", JOptionPane.INFORMATION_MESSAGE);

                uploadFrame.dispose();

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(uploadFrame, "파일 저장 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(uploadFrame, "유효한 숫자를 입력하세요 (조리 시간).", "입력 오류", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
