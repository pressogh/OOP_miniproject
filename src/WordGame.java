import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Vector;

public class WordGame extends JFrame {
    Vector<Word> wordVector = new Vector<>();
    Vector<Bullet> bulletVector = new Vector<>();
    private GamePanel panel = new GamePanel();
    private UserCharacter user = new UserCharacter();
    private JTextField jtf = new JTextField();
    private GameData gameData = new GameData();

    public WordGame () {
        setTitle("WordGame");
        setSize(800, 500);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container c =  getContentPane();

        c.add(panel, BorderLayout.CENTER);

        jtf.setSize(800, 30);
        jtf.addActionListener(new TextInputListener());
        c.add(jtf, BorderLayout.SOUTH);

        ScorePanel sp = new ScorePanel();
        sp.setPreferredSize(new Dimension(200, 500));
        c.add(sp, BorderLayout.EAST);

        loadDataFromFile("words.txt");

        WordMoveThread wmt = new WordMoveThread();
        wmt.start();
        BulletMovingThread bmt = new BulletMovingThread();
        bmt.start();
        setVisible(true);
    }

    class GamePanel extends JPanel {
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            user.draw(g);
            for (int i = 0; i < bulletVector.size(); i++) {
                bulletVector.get(i).draw(g);
            }
            // for (Word item : wordVector) 이런식으로 하면 오류 발생
            for (int i = 0; i < wordVector.size(); i++) wordVector.get(i).draw(g);
            repaint();
        }
    }

    class ScorePanel extends JPanel {
        public ScorePanel() {
            setBackground(Color.GRAY);
            setLayout(new FlowLayout());

            Border marginStage = new EmptyBorder(100, 15, 20, 0);
            JLabel stageLabel = new JLabel("STAGE " + gameData.stage);
            stageLabel.setFont(new Font("Noto Sans", Font.BOLD, 20));
            stageLabel.setBorder(marginStage);
            add(stageLabel);

            Border marginName = new EmptyBorder(0, 15, 10, 0);
            Border marginNumber = new EmptyBorder(0, 30, 10, 15);
            for (Score item : gameData.score) {
                JLabel scoreNameLabel = new JLabel(item.scoreName), scoreNumberLabel = new JLabel(Integer.toString(item.scoreNumber));

                scoreNameLabel.setFont(new Font("Noto Sans", Font.BOLD, 15));
                scoreNumberLabel.setFont(new Font("Noto Sans", Font.BOLD, 15));

                scoreNameLabel.setBorder(marginName);
                scoreNumberLabel.setBorder(marginNumber);

                add(scoreNameLabel);
                add(scoreNumberLabel);
            }
        }
    }

    class WordMoveThread extends Thread {
        @Override
        public void run() {
            while (true) {
                Vector<String> deletedWord = new Vector<>();

                for (int i = 0; i < wordVector.size(); i++) {
                    wordVector.get(i).y += wordVector.get(i).speed;

                    if (Math.abs(user.y - wordVector.get(i).y) > 0) {
                        wordVector.get(i).x += (user.x - wordVector.get(i).x) * wordVector.get(i).speed / Math.abs(user.y - wordVector.get(i).y);
                    }
                    if ((wordVector.get(i).x <= user.x - 15 && wordVector.get(i).x >= user.x + 15) || wordVector.get(i).y >= user.y) {
                        deletedWord.add(wordVector.get(i).word);
                        user.life--;
                    }
                }

                // 유저 캐릭터에 몬스터 피격 시 몬스터 제거
                for (int i = 0; i < deletedWord.size(); i++) {
                    int idx = findTargetIndex(deletedWord.get(i));
                    wordVector.remove(idx);
                }

                try {
                    sleep(gameData.speed / gameData.stage);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int findTargetIndex(String target) {
        for (int i = 0; i < wordVector.size(); i++) {
            if (target.equals(wordVector.get(i).word)) return i;
        }
        return -1;
    }

    class BulletMovingThread extends Thread {
        @Override
        public void run() {
            while (true) {
                Vector<Integer> deleteBullet = new Vector<>();
                for (int i = 0; i < bulletVector.size(); i++) {
                    bulletVector.get(i).y -= 10;

                    // wordVector에서 삭제할 단어의 인덱스 검색
                    int targetIndex = findTargetIndex(bulletVector.get(i).target);
                    // 0으로 할 시 좌표가 튀는 버그가 있어 10으로 변경
                    if (Math.abs(wordVector.get(targetIndex).y - bulletVector.get(i).y) > 10) {
                        bulletVector.get(i).x += bulletVector.get(i).weight + (wordVector.get(targetIndex).x - bulletVector.get(i).x) * 20 / Math.abs(wordVector.get(targetIndex).y - bulletVector.get(i).y);
                    }
                    if ((bulletVector.get(i).x <= wordVector.get(targetIndex).x - 100 && bulletVector.get(i).x >= wordVector.get(targetIndex).x + 100) || bulletVector.get(i).y <= wordVector.get(targetIndex).y) {
                        deleteBullet.add(i);
                    }
                }

                // 격추된 bullet 삭제
                for (int item : deleteBullet) {
                    int deleteIndex = findTargetIndex(bulletVector.get(item).target);
                    if (wordVector.get(deleteIndex).item.equals("Life") && user.life < 3) {
                        user.life++;
                    }
                    wordVector.remove(deleteIndex);
                    bulletVector.remove(item);
                }
                try {
                    sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class TextInputListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String text = jtf.getText();
            Vector<String> deletedItem = new Vector<>();

            for (int i = 0; i < wordVector.size(); i++) {
                if (wordVector.get(i).word.equals(text)) {
                    deletedItem.add(text);
                }
            }

            for (String item : deletedItem) {
                // bullet 최초 생성
                bulletVector.add(new Bullet(312, 340, item));
            }
            // 테스트
            System.out.println("Word " + wordVector.size());
            jtf.setText("");
        }
    }

    public static void saveDataToFile(Vector<Word> data, String fileName)  {
        // FileWriter을 이용해 data에 있는 데이터 저장
        FileWriter out;
        try {
            out = new FileWriter(fileName);
            for (Word item : data) {
                out.write(item.word + "\n");
            }
            out.close();
        } catch (IOException e) {
            System.out.println("FileWrite Error!!");
        }
    }
    public void loadDataFromFile(String fileName) {
        // BufferedReader을 이용해 파일에서 문자열을 한줄씩 읽어옴
        BufferedReader in;
        Vector<String> res = new Vector<>();
        try {
            in = new BufferedReader(new FileReader(fileName));
            String s;

            while ((s = in.readLine()) != null) {
                res.add(s);
            }
            in.close();
        } catch (FileNotFoundException e) {
            System.out.println("No File Error!!");
        } catch (IOException e) {
            System.out.println("FileRead Error!!");
        }

        int cnt = 0;
        for (String item : res) {
            if (cnt > 9) break;
            wordVector.add(new Word(item, (int) (Math.random() * 600), (int) (Math.random() * 50)));
            cnt++;
        }
    }

    public static void main(String[] args) {
        new WordGame();
    }
}

class Word {
    String word;
    int x, y;
    int speed;
    String item;

    public Word(String word, int x, int y) {
        this.word = word;
        this.x = x;
        this.y = y;

        int randItem = (int) (Math.random() * 100) + 1;
        if (randItem >= 1 && randItem <= 10) this.item = "Life";
        else if (randItem > 10 && randItem <= 20) this.item = "Slow";
        else this.item = "None";

        this.speed = (int) (Math.random() * 4) + 1;
    }

    public void draw(Graphics g) {
        if (item.equals("None")) g.setColor(Color.BLACK);
        else if (item.equals("Life")) g.setColor(Color.GREEN);
        else if (item.equals("Slow")) g.setColor(Color.CYAN);
        g.drawString(word, x, y);
    }
}
class Bullet {
    int x, y;
    int width, height;
    int weight;
    String target;
    public Bullet(int x, int y, String target) {
        this.x = x;
        this.y = y;
        this.target = target;
        width = 5;
        height = 5;
        weight = ((int)(Math.random() * 2) > 0.5 ? -1 : 1) * 10;
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.drawOval(x, y, width, height);
    }
}

class UserCharacter {
    int x, y;
    int width, height;
    int life;
    public UserCharacter() {
        this.x = 300;
        this.y = 350;
        this.width = 30;
        this.height = 30;
        this.life = 3;
    }

    public void draw(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillOval(x, y, width, height);

        g.setColor(Color.RED);
        for (int i = 0; i < life; i++) {
            g.fillOval(x + (i < (life / 2) ? -1 * 20 * i : 20 * i) - 13, y + 50, 15, 15);
        }
    }
}

class GameData {
    int stage;
    int speed;
    Vector<Score> score = new Vector<>();
    public GameData() {
        stage = 1;
        speed = 100;
        score.add(new Score("Total Score"));
        score.add(new Score("Word Score"));
        score.add(new Score("Life Score"));
        score.add(new Score("Stage Bonus"));
    }
}
class Score {
    String scoreName;
    int scoreNumber;
    public Score(String scoreName) {
        this.scoreName = scoreName;
        this.scoreNumber = 0;
    }
}