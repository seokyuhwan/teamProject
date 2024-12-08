package teamProject;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;

public class Recipe extends JPanel {
    private JTextField timeField;
    private JTextField searchField;
    private JButton resetButton;
    private JButton searchButton;
    private JButton uploadButton;
    private JButton timerButton;
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
        inputPanel.add(new JLabel("조리 시간:"));
        inputPanel.add(timeField);
        inputPanel.add(searchButton);
        inputPanel.add(resetButton);

        add(inputPanel, BorderLayout.NORTH);

        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));

        
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
    /*
    //업로드 기능 새로구현예정
    private void uploadRecipe() {
        JFrame newFrame = new JFrame("레시피 업로드");
        newFrame.setSize(400, 300);
        newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        newFrame.setVisible(true);
    }*/
    // 레시피 업로드 기능
    private void uploadRecipe() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("레시피 파일 선택");
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("텍스트 파일", "txt"));

        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectedFile != null) {
                String fileName = selectedFile.getName();
                String destinationPath = "C:/Users/Jieun/Documents/레시피/" + fileName;
                File destinationFile = new File(destinationPath);

                try {
                    // 파일 복사
                    Files.copy(selectedFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    JOptionPane.showMessageDialog(this, "레시피가 업로드되었습니다.", "업로드 완료", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "파일 업로드 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        }
     }
}
