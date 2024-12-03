import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.io.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.Socket;

public class Client extends JFrame {
    private JPanel mainPanel, recipeListPanel, recipeDetailPanel, findPanel, addButtonPanel;
    private CardLayout cardLayout;

    public Client() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        recipeListPanel = new FoodList(mainPanel, cardLayout);
        recipeDetailPanel = new RecipeDetail();

        mainPanel.add(recipeListPanel, "RecipeList");
        mainPanel.add(recipeDetailPanel, "RecipeDetail");

        findPanel = new find();
        add(findPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        addButtonPanel = new addPanel();
        add(addButtonPanel, BorderLayout.SOUTH);

        setSize(600, 600);
        setVisible(true);
    }

    class find extends JPanel {
        public find() {
            setLayout(new BorderLayout());
            add(new search(), BorderLayout.CENTER);
            add(new filter(), BorderLayout.SOUTH);
        }
    }

    class FoodList extends JPanel {
        private JTable recipeTable;
        private JPanel parentPanel;
        private CardLayout cardLayout;

        public FoodList(JPanel parentPanel, CardLayout cardLayout) {
            this.parentPanel = parentPanel;
            this.cardLayout = cardLayout;

            String[] header = {"요리"};
            String[][] contents = RecipeListData();

            DefaultTableModel tableModel = new DefaultTableModel(contents, header) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            recipeTable = new JTable(tableModel);
            recipeTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int selectedRow = recipeTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        String selectedRecipe = (String) recipeTable.getValueAt(selectedRow, 0);
                        Recipe recipe = RecipeDetailData(selectedRecipe);
                        ((RecipeDetail) recipeDetailPanel).updateRecipeDetail(recipe);
                        cardLayout.show(parentPanel, "RecipeDetail");
                    }
                }
            });
            JScrollPane scrollTable = new JScrollPane(recipeTable);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            add(scrollTable);
        }
        private String[][] RecipeListData() {
            try (Socket socket = new Socket("localhost", 8080);
                 ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {
                output.writeObject(null);
                output.flush();
                Object response = input.readObject();
                if (response instanceof List) {
                    List<String> recipeNames = (List<String>) response;
                    String[][] data = new String[recipeNames.size()][1];
                    for (int i = 0; i < recipeNames.size(); i++) {
                        data[i][0] = recipeNames.get(i);
                    }
                    return data;
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return new String[0][0];
        }

        private Recipe RecipeDetailData(String recipeName) {
            try (Socket socket = new Socket("localhost", 8080);
                 ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {
                output.writeObject(recipeName);
                Object response = input.readObject();
                return (Recipe) response;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class RecipeDetail extends JPanel {
        //TODO 수정필요 iamgeViewer랑같이 수정되야함
        private JLabel recipeTitle;
        private JTextArea recipeDescription;

        public RecipeDetail() {
            setLayout(new BorderLayout());
            recipeTitle = new JLabel("요리 제목", SwingConstants.CENTER);
            recipeDescription = new JTextArea("요리 설명");
            recipeDescription.setEditable(false);
            recipeDescription.setLineWrap(true);
            recipeDescription.setWrapStyleWord(true);
            add(recipeTitle, BorderLayout.NORTH);
            add(new JScrollPane(recipeDescription), BorderLayout.CENTER);
        }
        public void updateRecipeDetail(Recipe recipe) {
            if (recipe != null) {
                recipeTitle.setText(recipe.getFoodName());
                recipeDescription.setText("조리 시간: " + recipe.getTime() + "\n" +
                        "재료: " + String.join(", ", recipe.getItems()) + "\n\n" +
                        "조리 방법:\n" + String.join("\n", recipe.getSteps()));
            }
        }
    }
    class addPanel extends JPanel {
        private JButton addRecipe;
        private JButton downloadRecipe;

        public addPanel() {
            addRecipe = new JButton("Add Recipe");
            addRecipe.addActionListener(e -> {
                // TODO: 새로운 요리 추가를 위한 PANEL창 띄우기 새로운 Frame 만들어야함
            });
            downloadRecipe = new JButton("Download");
            downloadRecipe.addActionListener(e -> {
                // TODO: 레시피 다운
            });
            add(addRecipe);
            add(downloadRecipe);
        }
    }

    class search extends JPanel {
        private JTextField Search_Input;
        private JButton Search_Button;
        public search() {
            Search_Input = new JTextField(30);
            add(Search_Input);
            Search_Button=new JButton("검색");
            Search_Button.addActionListener(e -> {
                String Search_Content=Search_Input.getText();
                Search_Input.setText("");
            });
            add(Search_Button);
        }
    }

    class filter extends JPanel {
        public filter() {
            setBackground(Color.YELLOW); // 임시 색상
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Client());
    }
}
