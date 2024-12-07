import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ImageViewer extends JPanel {
    private int currentImageIndex = 0;
    private List<ImageIcon> images;
    private JLabel imageLabel;
    private JTextArea recipeTextArea;
    private String recipeName;
    private HashMap<String, String> RecipeTexts;

    public ImageViewer(String recipeName, HashMap<String, String> RecipeTexts) {
        this.recipeName = recipeName;
        this.RecipeTexts = RecipeTexts;
        setLayout(new BorderLayout());
        images = loadImages(recipeName);
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        if (!images.isEmpty()) {
            imageLabel.setIcon(images.get(0));
        }
        add(imageLabel, BorderLayout.NORTH);
        recipeTextArea = new JTextArea();
        recipeTextArea.setEditable(false);
        recipeTextArea.setText(RecipeTexts.get(recipeName));
        add(new JScrollPane(recipeTextArea), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        JButton prevButton = new JButton("이전 사진 보기");
        JButton nextButton = new JButton("다음 사진 보기");
        prevButton.addActionListener(e -> showPreviousImage());
        nextButton.addActionListener(e -> showNextImage());
        JButton downloadButton = new JButton("레시피 다운");
        downloadButton.addActionListener(e -> downloadRecipe());
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(downloadButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void showPreviousImage() {
        if (images.isEmpty()) return;
        currentImageIndex = (currentImageIndex - 1 + images.size()) % images.size();
        imageLabel.setIcon(images.get(currentImageIndex));
    }

    private void showNextImage() {
        if (images.isEmpty()) return;
        currentImageIndex = (currentImageIndex + 1) % images.size();
        imageLabel.setIcon(images.get(currentImageIndex));
    }

    private List<ImageIcon> loadImages(String recipeName) {
        List<ImageIcon> imageList = new ArrayList<>();
        File imageFolder = new File("./src/Recipes/" + recipeName);
        if (imageFolder.exists() && imageFolder.isDirectory()) {
            for (File file : imageFolder.listFiles()) {
                String fileName = file.getName().toLowerCase();
                if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")) {
                    ImageIcon icon = new ImageIcon(file.getAbsolutePath());
                    Image image = icon.getImage();
                    Image scaledImage = image.getScaledInstance(400, 300, Image.SCALE_SMOOTH);
                    imageList.add(new ImageIcon(scaledImage));
                }
            }
        } else {
            System.out.println("이미지 폴더를 찾을 수 없습니다: " + imageFolder.getAbsolutePath());
        }
        return imageList;
    }

    private void downloadRecipe() {
        String filePath = "./src/Recipes/" + recipeName + ".txt";
        File recipeFile = new File(filePath);
        if (recipeFile.exists()) {
            try {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("레시피 다운로드 위치 선택");
                fileChooser.setSelectedFile(new File(recipeName + ".txt"));

                int userSelection = fileChooser.showSaveDialog(this);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File destinationFile = fileChooser.getSelectedFile();
                    copyFile(recipeFile, destinationFile);
                    JOptionPane.showMessageDialog(this, "레시피가 다운로드되었습니다.", "다운로드 완료", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "파일 다운로드 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "레시피 파일이 존재하지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void copyFile(File sourceFile, File destinationFile) {
        try (java.io.InputStream in = new java.io.FileInputStream(sourceFile);
             java.io.OutputStream out = new java.io.FileOutputStream(destinationFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
