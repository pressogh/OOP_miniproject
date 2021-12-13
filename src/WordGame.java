import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Collections;
import java.io.*;
import java.util.Comparator;
import java.util.Vector;

class StartPanel extends JFrame {

    public StartPanel() {
        setTitle("WordGame");
        setSize(800, 600);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container c = getContentPane();
        c.add(new MyPanel());

        addKeyListener(new EnterKeyListener());

        requestFocus();
        setVisible(true);
    }

    public class MyPanel extends JPanel {
        private ImageIcon icon = new ImageIcon("./plane.png");
        private Image img = icon.getImage(); // 이미지 객체
        public MyPanel() {
            setLayout(new GridLayout(14, 1, 1, 1));
            Vector<JLabel> borderVector = new Vector<>();
            for (int i = 0; i < 9; i++) {
                JLabel temp = new JLabel("                                                                 ");
                temp.setFont(new Font("Gothic", Font.BOLD, 30));
                borderVector.add(temp);
                add(borderVector.get(i));
            }

            JLabel pe = new JLabel("PRESS ENTER TO START GAME");
            pe.setFont(new Font("Gothic", Font.BOLD, 30));
            pe.setForeground(Color.BLACK);
            add(pe);

            JLabel aw = new JLabel("PRESS A TO ADD WORD");
            aw.setFont(new Font("Gothic", Font.BOLD, 30));
            aw.setForeground(Color.BLACK);
            add(aw);

            JLabel rw = new JLabel("PRESS R TO SEE RANK");
            rw.setFont(new Font("Gothic", Font.BOLD, 30));
            rw.setForeground(Color.BLACK);
            add(rw);
        }
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            System.out.println("fjeioajfe");
        }
    }

    class EnterKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            switch (key) {
                case KeyEvent.VK_ENTER:
                    setVisible(false);
                    new GetUserNamePanel();
                    break;
                case KeyEvent.VK_A:
                    setVisible(false);
                    new AddPanel();
                    break;
                case KeyEvent.VK_R:
                    setVisible(false);
                    try {
                        new RankPanel();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    break;
            }
        }
    }
}
class GetUserNamePanel extends JFrame {
    private JTextField jtf = new JTextField();
    public GetUserNamePanel() {
        setTitle("WordGame");
        setSize(800, 500);
        setLayout(new FlowLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Vector<JLabel> borderVector = new Vector<>();
        for (int i = 0; i < 4; i++) {
            JLabel temp = new JLabel("                                                                 ");
            temp.setFont(new Font("Gothic", Font.BOLD, 30));
            borderVector.add(temp);
            add(borderVector.get(i));
        }

        JLabel pe = new JLabel("ENTER USERNAME");
        pe.setFont(new Font("Gothic", Font.BOLD, 30));
        add(pe);

        jtf.setPreferredSize(new Dimension(700, 30));
        jtf.addActionListener(new UserNameListener());
        add(jtf);

        requestFocus();
        setVisible(true);
    }

    class UserNameListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String text = jtf.getText();
            if (!text.equals("")) {
                setVisible(false);
                new WordGame(text);
            }
        }
    }
}
class AddPanel extends JFrame {
    private JTextField jtf = new JTextField();
    private JLabel newText;
    public AddPanel() {
        setTitle("WordGame");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel informationButton = new JLabel("INPUT WORD AND PRESS ENTER");
        informationButton.setFont(new Font("Gothic", Font.BOLD, 30));
        informationButton.setBounds(150, 80, 800, 40);
        add(informationButton);

        jtf.setPreferredSize(new Dimension(700, 30));
        jtf.addActionListener(new TextInputListener());
        jtf.setBounds(50, 180, 700, 30);
        add(jtf);

        JButton jb = new JButton("Go Back");
        jb.setBounds(350, 350, 100, 50);
        jb.addActionListener(new GoBackListener());
        add(jb);

        newText = new JLabel("");
        newText.setFont(new Font("Gothic", Font.BOLD, 30));
        add(newText);

        requestFocus();
        setVisible(true);
    }

    class TextInputListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String text = jtf.getText();

            if (!text.equals("")) {
                newText.setText("       " + text + " added!!");
                BufferedWriter out;
                try {
                    out = new BufferedWriter(new FileWriter("./words.txt", true));
                    out.write("\n" + text);
                    out.close();
                } catch (IOException ioe) {
                    System.out.println("FileWrite Error!!");
                }
            }

            jtf.setText("");
        }
    }

    class GoBackListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            setVisible(false);
            new StartPanel();
        }
    }
}
class GameEndPanel extends JFrame {
    public GameEndPanel(GameData gameData) {
        setTitle("WordGame");
        setSize(800, 600);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container c = getContentPane();
        c.add(new MyPanel(gameData));

        addKeyListener(new EnterKeyListener());

        requestFocus();
        setVisible(true);
    }

    class MyPanel extends JPanel {
        public MyPanel(GameData gameData) {
            setLayout(new BorderLayout());
            JLabel ys = new JLabel("YOUR SCORE");
            ys.setFont(new Font("Gothic", Font.BOLD, 30));
            ys.setBounds(300, 20, 500, 30);
            ys.setForeground(Color.RED);
            add(ys);

            JLabel n = new JLabel("Name: " + gameData.userName);
            n.setFont(new Font("Gothic", Font.BOLD, 30));
            n.setBounds(300, 70, 500, 30);
            n.setForeground(Color.RED);
            add(n);

            JLabel ts = new JLabel("Total Score: " + gameData.score.get(0));
            ts.setFont(new Font("Gothic", Font.BOLD, 30));
            ts.setBounds(300, 120, 500, 30);
            ts.setForeground(Color.RED);
            add(ts);

            JLabel ws = new JLabel("Word Score: " + gameData.score.get(1));
            ws.setFont(new Font("Gothic", Font.BOLD, 30));
            ws.setBounds(300, 170, 500, 30);
            ws.setForeground(Color.RED);
            add(ws);

            JLabel ls = new JLabel("Life Score: " + gameData.score.get(2));
            ls.setFont(new Font("Gothic", Font.BOLD, 30));
            ls.setBounds(300, 220, 500, 30);
            ls.setForeground(Color.RED);
            add(ls);

            JLabel ss = new JLabel("                                    Stage Score: " + gameData.score.get(3));
            ss.setFont(new Font("Gothic", Font.BOLD, 30));
            ss.setForeground(Color.RED);
            add(ss);
        }
    }

    class EnterKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            switch (key) {
                case KeyEvent.VK_ESCAPE:
                    setVisible(false);
                    new StartPanel();
                    break;
            }
        }
    }
}
class RankPanel extends JFrame {
    public RankPanel() throws IOException {
        setTitle("WordGame");
        setSize(800, 600);
        setLayout(new GridLayout(7, 1, 1, 1));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel rs = new JLabel("RANK");
        rs.setFont(new Font("Gothic", Font.BOLD, 30));
        add(rs);

        Vector<ScoreOutput> sov = new Vector<>();
        ObjectInputStream in = new ObjectInputStream(new FileInputStream("./score.dat"));
        ScoreOutput s;
        try {
            while ((s = (ScoreOutput) in.readObject()) != null) {
                sov.add(s);
            }
        } catch (Exception e) {
            in.close();
        }

        sov.sort((p1, p2) -> {
            if (p1.totalScore > p2.totalScore) {
                return -1;
            } else {
                return 1;
            }
        });

        JLabel dis = new JLabel("순위" + "        " + "점수" + "         " + "이름");
        dis.setFont(new Font("Gothic", Font.BOLD, 30));
        add(dis);


        for (int i = 0; i < 5; i++) {
            if (i >= sov.size()) break;
            JLabel label = new JLabel(i + 1 + "               " + sov.get(i).totalScore + "         " + sov.get(i).name);
            label.setFont(new Font("Gothic", Font.BOLD, 30));
            add(label);
        }

        addKeyListener(new ESCKeyListener());

        requestFocus();
        setVisible(true);
    }
    class ESCKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            switch (key) {
                case KeyEvent.VK_ESCAPE:
                    setVisible(false);
                    new StartPanel();
                    break;
            }
        }
    }
}

public class WordGame extends JFrame {
    Container c;
    Vector<Word> wordVector = new Vector<>();
    Vector<String> bossWordVector = new Vector<>();
    Vector<String> tempVector;
    Vector<Bullet> bulletVector = new Vector<>();
    private GamePanel panel = new GamePanel();
    private JTextField jtf = new JTextField();
    private UserCharacter user = new UserCharacter();
    private GameData gameData;
    private Boss boss = new Boss();

    public WordGame (String userName) {
        gameData = new GameData(userName);
        setTitle("WordGame");
        setSize(800, 500);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        c = getContentPane();

        // ****************************************** 화면 ******************************************
        c.add(panel, BorderLayout.CENTER);

        jtf.setSize(800, 30);
        jtf.addActionListener(new TextInputListener());
        c.add(jtf, BorderLayout.SOUTH);

        ScorePanel sp = new ScorePanel();
        sp.setPreferredSize(new Dimension(200, 500));
        c.add(sp, BorderLayout.EAST);
        // *******************************************************************************************
        tempVector = loadDataFromFile("words.txt");
        // ****************************************** 쓰레드 ******************************************
        WordMoveThread wmt = new WordMoveThread();
        wmt.start();
        BulletMovingThread bmt = new BulletMovingThread();
        bmt.start();
        SlowThread st = new SlowThread();
        st.start();
        DrawFireThread dft = new DrawFireThread();
        dft.start();
        WordAddThread wat = new WordAddThread();
        wat.start();
        GameEndThread get = new GameEndThread();
        get.start();
        // *******************************************************************************************

        requestFocus();
        setVisible(true);
    }

    class GamePanel extends JPanel {
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            user.draw(g);
            boss.draw(g);

            for (int i = 0; i < bulletVector.size(); i++) {
                bulletVector.get(i).draw(g);
            }

            if (gameData.drawFire) {
                Image bulletFire = new ImageIcon("./bullet_fire.png").getImage();
                g.drawImage(bulletFire, 295, 320, 50, 50, null);
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
        }
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Word Score 계산
            gameData.score.get(1).scoreNumber = gameData.totalDeletedCount * 10;
            // Life Score 계산
            gameData.score.get(2).scoreNumber = user.life * 100;
            // Stage Score 계산
            gameData.score.get(3).scoreNumber = gameData.stage * 100;
            // Total Score 계산
            gameData.score.get(0).scoreNumber = 0;
            for (int i = 1; i < gameData.score.size(); i++) {
                gameData.score.get(0).scoreNumber += gameData.score.get(i).scoreNumber;
            }

            g.setFont(new Font("Gothic", Font.BOLD, 20));
            g.drawString("STAGE", 50, 100);
            g.drawString(Integer.toString(gameData.stage), 155, 100);

            g.setFont(new Font("Gothic", Font.BOLD, 15));
            for (int i = 0; i < gameData.score.size(); i++) {
                g.setColor(Color.BLACK);
                g.drawString(gameData.score.get(i).scoreName, 30, 150 + i * 50);
            }

            for (int i = 0; i < gameData.score.size(); i++) {
                g.setColor(Color.BLACK);
                g.drawString(gameData.score.get(i).toString(), 150, 150 + i * 50);
            }

            repaint();
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
                        try {
                            wordVector.get(i).x += (user.x - wordVector.get(i).x) * wordVector.get(i).speed / Math.abs(user.y - wordVector.get(i).y);
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println(e);
                        }
                    }
                    if ((wordVector.get(i).x <= user.x - 15 && wordVector.get(i).x >= user.x + 15) || wordVector.get(i).y >= user.y) {
                        deletedWord.add(wordVector.get(i).word);
                        user.life = user.life > 0 ? user.life - 1 : 0;
                    }
                }

                // 유저 캐릭터에 몬스터 피격 시 몬스터 제거
                for (int i = 0; i < deletedWord.size(); i++) {
                    int idx = findTargetIndex(deletedWord.get(i));
                    wordVector.remove(idx);
                }

                try {
                    sleep(gameData.speed - gameData.stage * 10);
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

    class BossThread extends Thread {
        @Override
        public void run() {
            while (true) {
                if (gameData.isBoss) {
                    for (int j = -180; j < boss.posY; j += 3) {
                        boss.y = j;
                        try {
                            sleep(100);
                        } catch (InterruptedException e) {
                            System.out.println("Boss Interrupted!!");
                        }
                    }

                    while (boss.life > 0) {
                        try {
                            sleep(5000);
                        } catch (InterruptedException e) {
                            System.out.println("Boss Interrupted!!");
                        }
                        wordVector.add(new Word(bossWordVector.get(0), (int) (Math.random() * 200) + 200, 150));
                        bossWordVector.remove(0);

                    }
                    gameData.isBoss = false;
                }
                else {
                    gameData.isBossMoving = true;

                    for (int j = boss.posY; j >= -180; j -= 3) {
                        while (true) {
                            if (bulletVector.size() <= 0) break;
                            bulletVector.remove(0);
                        }
                        while (true) {
                            if (wordVector.size() <= 0) break;
                            wordVector.remove(0);
                        }
                        boss.y = j;
                        try {
                            sleep(100);
                        } catch (InterruptedException e) {
                            System.out.println("Boss Interrupted!!");
                        }
                    }
                    System.out.println("Thread Finished!! " + gameData.isBoss);
                    gameData.isBossMoving = false;
                    gameData.stage++;

                    boss.life = 100;
                    break;
                }
            }
        }
    }

    // 총알을 움직이게 해주는 스레드
    class BulletMovingThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Vector<Integer> deleteBullet = new Vector<>();
                    for (int i = 0; i < bulletVector.size(); i++) {
                        bulletVector.get(i).y -= 8;

                        // wordVector에서 삭제할 단어의 인덱스 검색
                        int targetIndex = findTargetIndex(bulletVector.get(i).target) == -1 ? 0 : findTargetIndex(bulletVector.get(i).target);
                        // 0으로 할 시 좌표가 튀는 버그가 있어 10으로 변경
                        if (Math.abs(wordVector.get(targetIndex).y - bulletVector.get(i).y) > 10) {
                            bulletVector.get(i).x += bulletVector.get(i).weight + (wordVector.get(targetIndex).x - bulletVector.get(i).x) * 20 / Math.abs(wordVector.get(targetIndex).y - bulletVector.get(i).y);
                        }
                        if ((bulletVector.get(i).x <= wordVector.get(targetIndex).x - 100 && bulletVector.get(i).x >= wordVector.get(targetIndex).x + 100) || bulletVector.get(i).y <= wordVector.get(targetIndex).y) {
                            deleteBullet.add(i);
                        }
                    }

                    // 격추된 bullet 삭제
                    for (int i = 0; i < deleteBullet.size(); i++) {
                        int deleteIndex;
                        deleteIndex = findTargetIndex(bulletVector.get(deleteBullet.get(i)).target);
                        if (deleteIndex == -1) {
                            bulletVector.remove((int) deleteBullet.get(i));
                            continue;
                        }

                        // 아이템 기능 작동
                        if (wordVector.get(deleteIndex).item.equals("Life") && user.life < 5) {
                            user.life++;
                        } else if (wordVector.get(deleteIndex).item.equals("Slow")) gameData.isSlow = true;

                        wordVector.remove(deleteIndex);
                        bulletVector.remove((int) deleteBullet.get(i));


                        if (!gameData.isBoss && !gameData.isBossMoving) gameData.deletedCount++;
                        if (!gameData.isBossMoving) gameData.totalDeletedCount++;

                        if (gameData.isBoss) boss.life = boss.life > 0 ? boss.life - 20 : 0;

                        // 지워진 단어의 개수가 15개 이상이면 boss 등장
                        if (gameData.deletedCount % (5 * gameData.stage) == 0 && !gameData.isBoss) {
                            gameData.deletedCount = 0;
                            gameData.isBoss = true;
                            BossThread bt = new BossThread();
                            bt.start();
                            while (true) {
                                if (bulletVector.size() <= 0) break;
                                bulletVector.remove(0);
                            }
                            while (true) {
                                if (wordVector.size() <= 0) break;
                                wordVector.remove(0);
                            }
                        }
                    }
                    try {
                        sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    while (true) {
                        if (bulletVector.size() <= 0) break;
                        bulletVector.remove(0);
                    }
                }
            }
        }
    }

    // Slow 아이템의 기능 스레드
    class SlowThread extends Thread {
        @Override
        public void run() {
            int check = 0;
            while (true) {
                if (gameData.isSlow) {
                    gameData.speed = 150;

                    if (check >= 1500) {
                        gameData.isSlow = false;
                        check = 0;
                    }
                    check += 10;
                }
                // gameData.speed = 100 - gameData.stage * 10 로 하면 너무 어려움
                else gameData.speed = 100;

                try {
                    sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 우주선에서 발사되는 총알의 불꽃을 표시해주는 스레드
    class DrawFireThread extends Thread {
        @Override
        public void run() {
            while (true) {
                if (!gameData.drawFire) {
                    try {
                        sleep(500);
                        gameData.drawFire = false;
                        repaint();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // 화면에 표시되는 단어의 개수를 유지시키는 스레드
    class WordAddThread extends Thread {
        @Override
        public void run() {
            while (true) {
                System.out.println("Running"); // 이거 없으면 오류
                if (!gameData.isBoss && !gameData.isBossMoving) {
                    if (wordVector.size() < gameData.stage + 3) {
                        wordVector.add(new Word(tempVector.get(0), (int) (Math.random() * 600), (int) (Math.random() * 100)));
                        tempVector.remove(0);
                    }
                }
            }
        }
    }

    private void gameEnd() throws IOException, ClassNotFoundException {
        setVisible(false);

        Vector<ScoreOutput> sov = new Vector<>();
        ObjectInputStream in = new ObjectInputStream(new FileInputStream("./score.dat"));
        ScoreOutput s;
        try {
            while ((s = (ScoreOutput) in.readObject()) != null) {
                sov.add(s);
            }
        } catch (Exception e) {
            in.close();
        }

        ScoreOutput so = new ScoreOutput(gameData);
        sov.add(so);

        // ObjectOutputStream을 이용하여 파일에 클래스 저장
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("./score.dat"));
        for (ScoreOutput item : sov) {
            out.writeObject(item);
        }
        out.close();

        new GameEndPanel(gameData);
    }
    // 게임 종료를 판단해주는 쓰레드
    class GameEndThread extends Thread {
        @Override
        public void run() {
            while (true) {
                System.out.println("GameEnd");
                if (gameData.stage >= 4 || user.life <= 0) {
                    try {
                        gameEnd();
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
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
                // 같은 단어를 연속적으로 입력 시 발생하는 오류 해결
                boolean flag = true;
                for (int i = 0; i < bulletVector.size(); i++) {
                    if (bulletVector.get(i).target.equals(item)) {
                        flag = false;
                        break;
                    }
                }

                if (flag) {
                    // bullet 생성
                    bulletVector.add(new Bullet(312, 340, item));
                    gameData.drawFire = true;
                }
            }

            // 테스트
            System.out.println("Word " + wordVector.size());
            System.out.println("IsBossMoving " + gameData.isBossMoving);
            System.out.println("IsBoss " + gameData.isBoss);
            jtf.setText("");
        }
    }

    public Vector<String> loadDataFromFile(String fileName) {
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

        res.sort(Comparator.comparingInt(String::length));
        for (int i = res.size() - 200; i < res.size(); i++) {
            bossWordVector.add(res.get(i));
            res.remove(i);
        }
        Collections.shuffle(bossWordVector);
        Collections.shuffle(res);
        return res;
    }

    public static void main(String[] args) {
        new StartPanel();
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
        g.setFont(new Font("Gothic", Font.BOLD, 14));
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
        width = 8;
        height = 8;
        weight = ((int)(Math.random() * 2) > 0.5 ? -1 : 1) * 10;
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(x, y, width, height);
    }
}
class UserCharacter {
    int x, y;
    int width, height;
    int life;
    public UserCharacter() {
        this.x = 290;
        this.y = 340;
        this.width = 60;
        this.height = 60;
        this.life = 3;
    }

    public void draw(Graphics g) {
        Image heart = new ImageIcon("./heart.png").getImage();
        Image spaceship = new ImageIcon("./spaceship.png").getImage();

        g.drawImage(spaceship, x, y, width, height, null);
        for (int i = 0; i < life; i++) {
            g.drawImage(heart, x + i * 40 - (20 + 10 * i), y + 60, 20, 20, null);
        }
    }
}
class Boss {
    int x, y;
    int width, height;
    int life;
    int posY = 40;
    public Boss() {
        this.x = 250;
        this.y = -180;
        this.width = 120;
        this.height = 120;
        this.life = 100;
    }

    public void draw(Graphics g) {
        Image heart = new ImageIcon("./heart.png").getImage();
        Image spaceship = new ImageIcon("./Boss.png").getImage();

        g.drawImage(spaceship, x, y, width, height, null);
        g.setColor(Color.BLACK);
        g.fillRect(x - 40, y - 30, 200, 20);
        g.setColor(Color.RED);
        g.fillRect(x - 40, y - 30, life * 2, 20);
    }
}
class GameData {
    int stage;
    int speed;
    int deletedCount, totalDeletedCount;
    boolean isSlow;
    boolean isBoss;
    boolean drawFire;
    boolean isBossMoving;
    String userName;
    Vector<Score> score = new Vector<>();
    public GameData(String userName) {
        this.userName = userName;
        stage = 1;
        speed = 100;
        deletedCount = 0;
        totalDeletedCount = 0;
        isSlow = false;
        isBoss = false;
        drawFire = false;
        isBossMoving = false;
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
    public String toString() {
        return Integer.toString(scoreNumber);
    }
}
class ScoreOutput implements Serializable {
    int totalScore, wordScore, lifeScore, stageScore;
    String name;
    public ScoreOutput(GameData g) {
        totalScore = g.score.get(0).scoreNumber;
        wordScore = g.score.get(1).scoreNumber;
        lifeScore = g.score.get(2).scoreNumber;
        stageScore = g.score.get(3).scoreNumber;
        name = g.userName;
    }
}