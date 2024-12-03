import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


//테스트용 주석
//수정


public class StopWatch {
    private static Timer timer;
    private static int elapsedSeconds = 0;

    public static void startStopWatch() {
        // 메인 프레임
        JFrame frame = new JFrame("스톱워치");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // 타이머 표시 라벨
        JLabel timerLabel = new JLabel("0 초", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 40));
        frame.add(timerLabel, BorderLayout.CENTER);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton startButton = new JButton("시작");
        JButton stopButton = new JButton("정지");
        JButton resetButton = new JButton("초기화");
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(resetButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // 버튼 동작
        startButton.addActionListener(e -> {
            if (timer != null && timer.isRunning()) return; // 이미 실행 중이면 무시
            timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    elapsedSeconds++;
                    timerLabel.setText(elapsedSeconds + " 초");
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
            elapsedSeconds = 0;
            timerLabel.setText("0 초");
        });

        frame.setVisible(true);
    }

    public class Main {
        public static void main(String[] args) {
            StopWatch.startStopWatch();
        }
    }
