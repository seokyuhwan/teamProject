import javax.swing.*;
import java.awt.*;

public class Login {
    private static UserDatabase userDatabase = new UserDatabase();

    public static void main(String[] args) {
        showLoginScreen();
    }
    private static void showLoginScreen() {
        JFrame frame = new JFrame("로그인");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null); // 화면 중앙에 표시
        frame.setResizable(false); // 크기 조정 불가

        JPanel panel = new JPanel(new GridBagLayout()); // GridBagLayout 사용
        frame.add(panel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // 컴포넌트 간 여백
        gbc.fill = GridBagConstraints.HORIZONTAL; // 컴포넌트 크기 확장

        // ID 레이블과 입력 필드
        JLabel userLabel = new JLabel("ID:");
        gbc.gridx = 0; // 첫 번째 열
        gbc.gridy = 0; // 첫 번째 행
        gbc.gridwidth = 1; // 한 열만 차지
        panel.add(userLabel, gbc);

        JTextField userField = new JTextField(15);
        gbc.gridx = 1; // 두 번째 열
        gbc.gridy = 0;
        panel.add(userField, gbc);

        JLabel passLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1; // 두 번째 행
        panel.add(passLabel, gbc);

        JPasswordField passField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(passField, gbc);

        JButton loginButton = new JButton("Log In");
        gbc.gridx = 0;
        gbc.gridy = 2; // 세 번째 행
        gbc.gridwidth = 1; // 한 열만 차지
        panel.add(loginButton, gbc);

        JButton registerButton = new JButton("회원가입");
        gbc.gridx = 1; // 두 번째 열
        gbc.gridy = 2;
        panel.add(registerButton, gbc);

        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            if (userDatabase.validateUser(username, password)) {
                JOptionPane.showMessageDialog(frame, "로그인 성공!");
                frame.dispose(); // 현재 창 닫기
                openMainApp();
            } else {
                JOptionPane.showMessageDialog(frame, "로그인 실패! 아이디나 비밀번호를 확인하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerButton.addActionListener(e -> {
            frame.dispose();
            showRegisterScreen();
        });

        frame.setVisible(true);
    }

    // 회원가입 화면
    private static void showRegisterScreen() {
        JFrame frame = new JFrame("회원가입");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null); // 화면 중앙에 표시
        frame.setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        frame.add(panel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // 컴포넌트 간 여백
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("아이디:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(userLabel, gbc);

        JTextField userField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(userField, gbc);

        JLabel passLabel = new JLabel("비밀번호:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passLabel, gbc);

        JPasswordField passField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(passField, gbc);

        JButton registerButton = new JButton("회원가입");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(registerButton, gbc);

        JButton backButton = new JButton("뒤로가기");
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(backButton, gbc);

        registerButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            if (userDatabase.registerUser(username, password)) {
                JOptionPane.showMessageDialog(frame, "회원가입 성공!");
                frame.dispose();
                showLoginScreen();
            } else {
                JOptionPane.showMessageDialog(frame, "이미 존재하는 아이디입니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> {
            frame.dispose();
            showLoginScreen();
        });

        frame.setVisible(true);
    }

    private static void openMainApp() {
        Client mainFrame = new Client();
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setVisible(true);
    }
}
