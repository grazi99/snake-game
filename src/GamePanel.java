import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.*;
import java.util.Scanner;
import java.lang.Integer;
import java.util.Random;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class GamePanel extends JPanel implements ActionListener{

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGTH = 600;
    static final int UNIT_SIZE = 35;
    static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGTH)/UNIT_SIZE;
    static final int DELAY = 75;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    boolean isHighscore=false;
    int highscore;
    Timer timer;
    Timer restart;
    Random random;
    JButton button;

    GamePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGTH));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame(){
        newApple();
        running=true;
        for(int i=0; i<bodyParts; i++){
            x[i]=0;
            y[i]=0;
        }
        timer = new Timer(DELAY, this);
        timer.restart();
    };
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    };

    public void draw(Graphics g){
       if(running){
           g.setColor(Color.yellow);
           g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
           for(int i = 0; i<bodyParts; i++){
               if(i==0){
                   g.setColor(Color.green);
                   g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
               }
               else{
                   g.setColor(new Color(random.nextInt((255)),random.nextInt((255)),random.nextInt((255))));
                   g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
               }
           }
       } else{
           gameOver(g);
       }
    };
    public void newApple(){
        appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
        appleY= random.nextInt((int)(SCREEN_HEIGTH/UNIT_SIZE))*UNIT_SIZE;
    };
    public void move(){
        for(int i = bodyParts ; i>0 ; i--){
            x[i] = x[i-1];
            y[i] = y[i-1];
        } 
        switch(direction){
            case 'U':
            y[0] = y[0] - UNIT_SIZE;
            break;
            case 'D':
            y[0] = y[0] + UNIT_SIZE;
            break;
            case 'L':
            x[0] = x[0] - UNIT_SIZE;
            break;
            case 'R':
            x[0] = x[0] + UNIT_SIZE;
            break;
        }
    };
    public void checkApple(){
        if(x[0]==appleX && y[0]==appleY){
            bodyParts++;
            applesEaten++;
            newApple();
        }
    };
    public void checkCollisions(){
        //si la cabeza toca con el cuerpo
        for(int i = bodyParts; i>0 ; i--){
            if((x[0]==x[i] && y[0]==y[i])){
                running = false;
            }
        }
        //si la cabeza toca las paredes
        
        if(x[0]<0){
            running=false;
        }
        if(x[0]>SCREEN_WIDTH-UNIT_SIZE){
            running=false;
        }
        if(y[0]<0){
            running=false;
        }
        if(y[0]>SCREEN_HEIGTH-UNIT_SIZE){
            running=false;
        }
        if(!running){
            timer.stop();
        }
    };
    public void gameOver(Graphics g){
        File file = new File("./lib/highscores.txt");
        if(file.exists()){
            char data[] = new char[200];
            try (FileReader reader = new FileReader("./lib/highscores.txt")) {
                reader.read(data);
                String dataString = String.valueOf(data);
                String substrings[] = dataString.split(",");
                highscore=Integer.parseInt(substrings[0]);
                for(int i = 1; i<substrings.length-1;i++){
                    if(Integer.parseInt(substrings[i])>highscore){
                        highscore=Integer.parseInt(substrings[i]);
                    }
                }            
                FileWriter writer = new FileWriter(file, true);
                writer.write(String.valueOf(applesEaten)+",");
                writer.close();
                //Scanner scanner = new Scanner(reader);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else{
            try {
                file.createNewFile();
                FileWriter writer = new FileWriter(file);
                writer.write(String.valueOf(applesEaten)+",");
                writer.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        //hasta aca file managment
        g.setColor(Color.blue);
        g.setFont(new Font("Italica", Font.BOLD, 15));
        FontMetrics metrics = getFontMetrics(g.getFont());
        if(applesEaten>highscore){
        g.drawString("¡Felicitaciones!, estableciste un nuevo record: "+applesEaten, (SCREEN_WIDTH-metrics.stringWidth("Felicidades, nuevo record: "+applesEaten))/2, SCREEN_HEIGTH/2);
        } else{
            g.drawString("Partida terminada. Tu puntuacion fue de: "+applesEaten, (SCREEN_WIDTH-metrics.stringWidth("Partida terminada. Tu puntuacion fue de: "+applesEaten))/2, SCREEN_HEIGTH/2-15);
        }
        button = new JButton("Reiniciar");
        button.setBounds((SCREEN_WIDTH-metrics.stringWidth("Reiniciar"))/2,SCREEN_HEIGTH/2+10,200,60);  
        this.add(button);
        button.addActionListener(new ActionListener(){  
            public void actionPerformed(ActionEvent e){  
                direction='R';
                applesEaten=0;
                bodyParts=6;
                button.setVisible(false);
                repaint();
                startGame();
            }  
        });
          
    };

    @Override
    public void actionPerformed(ActionEvent e) {
        if(running){
            move();
            checkApple();
            checkCollisions();
        }
        repaint();        
    }
    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed (KeyEvent e){
            switch(e.getKeyCode()){
                case KeyEvent.VK_LEFT:
                    if(direction!='R'){
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if(direction!='L'){
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if(direction!='D'){
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if(direction!='U'){
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}
