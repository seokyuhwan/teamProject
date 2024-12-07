import javax.swing.*;
import java.util.HashMap;

public class Main extends JFrame {
    DataHandle data = new DataHandle();
    HashMap<String, Integer> NameAndTimes = data.getRecipe();
    HashMap<String, String> RecipeTexts = data.getRecipeText();

    public Main() {
        setTitle("나의 요리장");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        Recipe recipePanel = new Recipe(this, NameAndTimes, RecipeTexts);
        add(recipePanel);
        setVisible(true);
    }

    public void showImageViewer(String recipeName) {
        JFrame imageViewerFrame = new JFrame(recipeName);
        imageViewerFrame.setSize(800, 600);
        imageViewerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ImageViewer imageViewerPanel = new ImageViewer(recipeName, RecipeTexts);
        imageViewerFrame.add(imageViewerPanel);
        imageViewerFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
    }
}
