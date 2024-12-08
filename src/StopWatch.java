import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StopWatch {
    private static Timer timer;
    private static int remainingSeconds;

    public static void startStopWatch() {
        // 메인 프레임 설정
        JFrame frame = new JFrame("카운트다운 스톱워치");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // 타이머 표시 라벨
        JLabel timerLabel = new JLabel("00:00", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 50));
        frame.add(timerLabel, BorderLayout.CENTER);

        // 설정 패널 (분/초 설정)
        JPanel inputPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        JLabel minutesLabel = new JLabel("분:", SwingConstants.RIGHT);
        JLabel secondsLabel = new JLabel("초:", SwingConstants.RIGHT);
        SpinnerNumberModel minutesModel = new SpinnerNumberModel(0, 0, 59, 1); // 0~59
        SpinnerNumberModel secondsModel = new SpinnerNumberModel(0, 0, 59, 1); // 0~59
        JSpinner minutesSpinner = new JSpinner(minutesModel);
        JSpinner secondsSpinner = new JSpinner(secondsModel);
        JButton setButton = new JButton("설정");

        inputPanel.add(minutesLabel);
        inputPanel.add(minutesSpinner);
        inputPanel.add(new JLabel()); // 빈 칸
        inputPanel.add(secondsLabel);
        inputPanel.add(secondsSpinner);
        inputPanel.add(setButton);
        frame.add(inputPanel, BorderLayout.NORTH);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton startButton = new JButton("시작");
        JButton stopButton = new JButton("정지");
        JButton resetButton = new JButton("초기화");
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(resetButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // 버튼 동작 설정
        setButton.addActionListener(e -> {
            int minutes = (int) minutesSpinner.getValue();
            int seconds = (int) secondsSpinner.getValue();
            remainingSeconds = minutes * 60 + seconds;

            if (remainingSeconds > 0) {
                timerLabel.setText(formatTime(remainingSeconds));
            } else {
                JOptionPane.showMessageDialog(frame, "0보다 큰 시간을 설정하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        startButton.addActionListener(e -> {
            if (timer != null && timer.isRunning()) return; // 이미 실행 중이면 무시
            if (remainingSeconds <= 0) {
                JOptionPane.showMessageDialog(frame, "먼저 시간을 설정하세요.", "오류", JOptionPane.WARNING_MESSAGE);
                return;
            }
            timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (remainingSeconds > 0) {
                        remainingSeconds--;
                        timerLabel.setText(formatTime(remainingSeconds));
                    } else {
                        timer.stop();
                        JOptionPane.showMessageDialog(frame, "시간 종료!", "알림", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            });
            timer.start();
        });

        stopButton.addActionListener(e -> {
            if (timer != null) {
                timer.stop();
            }
        });

        resetButton.addActionListener(e -> {
            if (timer != null) {
                timer.stop();
            }
            remainingSeconds = 0;
            timerLabel.setText("00:00");
            minutesSpinner.setValue(0);
            secondsSpinner.setValue(0);
        });

        frame.setVisible(true);
    }

    // 초를 "분:초" 형식의 문자열로 변환하는 메서드
    private static String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public static void main(String[] args) {
        startStopWatch();
    }
}
