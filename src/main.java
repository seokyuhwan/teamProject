import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class main extends JFrame {
    private List<String> recipeNames = new ArrayList<>();

    public main() {
        setTitle("나의 요리장");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        // 요리 목록 로드
        loadRecipesFromFolder("C:/Users/Jieun/Documents/레시피");
        
        Recipe recipePanel = new Recipe();
        add(recipePanel);

        setVisible(true);
    }

    // 폴더에서 텍스트 파일을 읽어 요리 목록 생성
    private void loadRecipesFromFolder(String folderPath) {
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".txt")) {
                    String recipeName = file.getName().replace(".txt", ""); // 파일 이름에서 확장자 제거
                    recipeNames.add(recipeName);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "레시피 폴더를 찾을 수 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showImageViewer(String recipeName) {
        JFrame imageViewerFrame = new JFrame(recipeName);
        imageViewerFrame.setSize(800, 600);
        imageViewerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        ImageViewer imageViewerPanel = new ImageViewer(recipeName);
        imageViewerFrame.add(imageViewerPanel);

        imageViewerFrame.setVisible(true);
    }

    class Recipe extends JPanel {
        private JTextField timeField;   // 조리 시간 입력 필드
        private JButton resetButton;    // 새로고침 버튼
        private JButton searchButton;   // 검색 버튼
        private JButton uploadButton;   // 업로드 버튼
        private Component filterPanel;
        private JList<String> recipeList;

        public Recipe() {
            setLayout(new BorderLayout());

            // 상단에 검색 필드 추가
            JPanel inputPanel = new JPanel();
            inputPanel.setLayout(new FlowLayout());

            resetButton = new JButton("새로고침");
            resetButton.addActionListener(e -> resetFilters());
           
            timeField = new JTextField(5);
            timeField.setToolTipText("조리 시간 입력 (분)");

            searchButton = new JButton("검색");
            searchButton.addActionListener(e -> filterRecipes());

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

            // 레시피 목록을 JList에 추가
            recipeList = new JList<>(recipeNames.toArray(new String[0]));
            recipeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            // 음식 선택 이벤트
            recipeList.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    String selectedRecipe = recipeList.getSelectedValue();
                    if (selectedRecipe != null) {
                        ((main) SwingUtilities.getWindowAncestor(this)).showImageViewer(selectedRecipe);
                    }
                }
            });

            JScrollPane scrollPane = new JScrollPane(recipeList);
            add(scrollPane, BorderLayout.CENTER);
        }

        // 레시피 목록 필터링
        private void filterRecipes() {
            String timeText = timeField.getText();

            // 조리 시간이 입력되면 필터링된 레시피 리스트
            List<String> filteredRecipes = new ArrayList<>();

            // 레시피 폴더에서 파일을 필터링하여 추가
            for (String recipeName : recipeNames) {
                if (!timeText.isEmpty()) {
                    // 텍스트 파일에서 조리 시간을 찾고 필터링
                    File recipeFile = new File("C:/Users/Jieun/Documents/레시피/" + recipeName + ".txt");
                    if (recipeFile.exists()) {
                        try (BufferedReader reader = new BufferedReader(new FileReader(recipeFile))) {
                            String line;
                            boolean timeMatch = false;
                            while ((line = reader.readLine()) != null) {
                                if (line.contains("조리 시간") && line.contains(timeText)) {
                                    timeMatch = true;
                                    break;
                                }
                            }
                            if (timeMatch) {
                                filteredRecipes.add(recipeName);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            // 필터링된 레시피로 JList 갱신
            recipeList.setListData(filteredRecipes.toArray(new String[0]));
        }

        // 리셋 필터
        private void resetFilters() {
            timeField.setText("");
            // 폴더에서 파일 목록을 다시 읽어오기
            recipeNames.clear();  // 기존 목록을 비우고
            loadRecipesFromFolder("C:/Users/Jieun/Documents/레시피");  // 폴더에서 파일 목록을 다시 로드

            // 전체 레시피 목록으로 JList 갱신
            recipeList.setListData(recipeNames.toArray(new String[0]));
        }


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

    class ImageViewer extends JPanel {
        private int currentImageIndex = 0;
        private List<ImageIcon> images;
        private JLabel imageLabel;
        private JTextArea recipeTextArea;
        private String recipeName; // recipeName 필드 추가

        public ImageViewer(String recipeName) {
            this.recipeName = recipeName; // 생성자에서 recipeName 초기화
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
            recipeTextArea.setText(loadRecipeText(recipeName));
            add(new JScrollPane(recipeTextArea), BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            JButton prevButton = new JButton("이전 사진 보기");
            JButton nextButton = new JButton("다음 사진 보기");

            prevButton.addActionListener(e -> showPreviousImage());
            nextButton.addActionListener(e -> showNextImage());

            // "레시피 다운" 버튼 추가
            JButton downloadButton = new JButton("레시피 다운");
            downloadButton.addActionListener(e -> downloadRecipe());

            buttonPanel.add(prevButton);
            buttonPanel.add(nextButton);
            buttonPanel.add(downloadButton); // 버튼을 패널에 추가
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
            File imageFolder = new File("C:/Users/Jieun/Documents/레시피/" + recipeName); // 이미지 폴더 경로
            if (imageFolder.exists() && imageFolder.isDirectory()) {
                for (File file : imageFolder.listFiles()) {
                    String fileName = file.getName().toLowerCase();
                    if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")) {
                        // 이미지 로드
                        ImageIcon icon = new ImageIcon(file.getAbsolutePath());
                        Image image = icon.getImage();

                        // 원하는 크기로 이미지 조정 (예: 400x300)
                        Image scaledImage = image.getScaledInstance(400, 300, Image.SCALE_SMOOTH);

                        // 크기가 조정된 이미지를 다시 ImageIcon으로 변환
                        imageList.add(new ImageIcon(scaledImage));
                    }
                }
            } else {
                System.out.println("이미지 폴더를 찾을 수 없습니다: " + imageFolder.getAbsolutePath());
            }
            return imageList;
        }

        private String loadRecipeText(String recipeName) {
            StringBuilder content = new StringBuilder();
            File recipeFile = new File("C:/Users/Jieun/Documents/레시피/" + recipeName + ".txt");
            if (recipeFile.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(recipeFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                } catch (IOException e) {
                    content.append("조리법을 불러오는 중 오류가 발생했습니다.");
                }
            } else {
                content.append("조리법 파일을 찾을 수 없습니다.");
            }
            return content.toString();
        }

        // 레시피 다운로드 메서드
        private void downloadRecipe() {
            String filePath = "C:/Users/Jieun/Documents/레시피/" + recipeName + ".txt";
            File recipeFile = new File(filePath);
            
            if (recipeFile.exists()) {
                try {
                    // 다운로드 경로 선택
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("레시피 다운로드 위치 선택");
                    fileChooser.setSelectedFile(new File(recipeName + ".txt"));
                    
                    int userSelection = fileChooser.showSaveDialog(this);
                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        File destinationFile = fileChooser.getSelectedFile();
                        // 레시피 파일을 선택한 위치로 복사
                        copyFile(recipeFile, destinationFile);
                        JOptionPane.showMessageDialog(this, "레시피가 다운로드되었습니다.", "다운로드 완료", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "파일 다운로드 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(this, "레시피 파일이 존재하지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        }

        // 파일 복사 메서드
        private void copyFile(File sourceFile, File destinationFile) throws IOException {
            try (InputStream in = new FileInputStream(sourceFile);
                 OutputStream out = new FileOutputStream(destinationFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new main());
    }
}

