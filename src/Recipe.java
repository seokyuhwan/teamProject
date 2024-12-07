import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Recipe extends JPanel {
    private JTextField timeField;
    private JTextField searchField;
    private JButton resetButton;
    private JButton searchButton;
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
        inputPanel.add(new JLabel("조리 시간:"));
        inputPanel.add(timeField);
        inputPanel.add(searchButton);
        inputPanel.add(resetButton);

        add(inputPanel, BorderLayout.NORTH);

        JPanel uploadPanel = new JPanel();
        uploadPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

        uploadButton = new JButton("레시피 업로드");
        uploadButton.addActionListener(e -> uploadRecipe());

        uploadPanel.add(uploadButton);
        add(uploadPanel, BorderLayout.EAST);

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
        JFrame newFrame = new JFrame("레시피 업로드");
        newFrame.setSize(400, 300);
        newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        newFrame.setVisible(true);
    }
}
