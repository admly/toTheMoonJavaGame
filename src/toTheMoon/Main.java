package toTheMoon;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;
import toTheMoon.res.Resources;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main extends Application
{
    private Timeline gameLoop;
    Resources resources;
    ArrayList<Sprite> moneyList;
    Image space;
    Sprite ship;
    private ArrayList<Sprite> enemyList;

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage stage)
    {
        ArrayList<String> input = new ArrayList<String>();

        stage.setTitle( "Collect the Money Bags!" );
        Group root = new Group();
        Scene scene = new Scene( root );
        stage.setScene( scene );
        Canvas canvas = new Canvas( 512, 512 );
        root.getChildren().add( canvas );

        GraphicsContext gc = canvas.getGraphicsContext2D();

        Font font = Font.font( "Helvetica", FontWeight.BOLD, 24 );
        gc.setFont(font);
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);

        ship = getShip();
        space = new Image(getClass().getResourceAsStream( "res/space.png" ));
        initHandlers(input, scene);
        moneyList = generateMoneyArray(); //creates list with money sprites, each with random x,y position
        enemyList = generateEnemyArray();

        IntValue score = new IntValue(0);

        gameLoop = new Timeline();
        gameLoop.setCycleCount( Timeline.INDEFINITE );
        final long timeStart = System.currentTimeMillis();

        KeyFrame kf = new KeyFrame(
               Duration.seconds(0.017),                // 60 FPS
                new EventHandler<ActionEvent>()
                {
                    public void handle(ActionEvent ae)
                    {
                        double time = (System.currentTimeMillis() - timeStart) / 100000.0;
                        double multiplier = 0.1;
                        checkForCollisionsWithMoney(moneyList, ship, score);
                        steerTheShip(ship, input, multiplier);
                        moveEnemies(enemyList);
                        checkForCollisionsWithMoney(moneyList, ship, score);
                        renderTheScene(timeStart, time, gc, space, ship, moneyList, enemyList, score );

                    }
                });


        gameLoop.getKeyFrames().add(kf);
        gameLoop.play();
        stage.show();





    }

    private Sprite getShip() {
        Sprite ship = new Sprite();
        ship.setPosition(0, 256);
        Resources resources = new Resources();
        ship.frames = resources.getShipImg();
        ship.duration = 0.001;
        return ship;
    }

    private void initHandlers(ArrayList<String> input, Scene theScene) {
        theScene.setOnKeyPressed(
                new EventHandler<KeyEvent>()
                {
                    public void handle(KeyEvent e)
                    {
                        String code = e.getCode().toString();
                        if ( !input.contains(code) )
                            input.add( code );
                    }
                });

        theScene.setOnKeyReleased(
                new EventHandler<KeyEvent>()
                {
                    public void handle(KeyEvent e)
                    {
                        String code = e.getCode().toString();
                        input.remove( code );
                    }
                });
    }

    private ArrayList<Sprite> generateMoneyArray() {
        ArrayList<Sprite> moneyList = new ArrayList<Sprite>();
        for (int i = 0; i < 15; i++)
        {
            Sprite moneybag = new Sprite();
            moneybag.setImage(getClass().getResourceAsStream("res/moneybag.png"));
            double px = 450 * Math.random() + 20;
            double py = 450  * Math.random() + 20;
            moneybag.setPosition(px,py);
            moneyList.add(moneybag);
        }
        return moneyList;
    }

    private void renderTheScene(double timeStart, double time, GraphicsContext gc, Image space,
                                Sprite ship, ArrayList<Sprite> moneyList, ArrayList<Sprite> enemyList,
                                IntValue score) {
        gc.clearRect(0, 0, 512,512);
        gc.drawImage(space, 0, 0);
        ship.render(time, gc);

        for (Sprite money : moneyList)
            money.render(gc);

        for (Sprite enemy : enemyList)
            enemy.render(time, gc);

        String pointsText = "Cash: $" + (100 * score.value);
        gc.fillText(pointsText, 360, 36);
        gc.strokeText(pointsText, 360, 36);

        String waveTimer = "NEXT WAVE IN:";
        gc.fillText(waveTimer, 36,36);
        gc.strokeText(waveTimer, 36, 36);
    }

    private void renderTheEnemies(double time, GraphicsContext gc){
        for (Sprite enemy : enemyList)
            enemy.render(time, gc);

    }

    private void checkForCollisionsWithMoney(ArrayList<Sprite> moneyList, Sprite ship, IntValue score) {
        Iterator<Sprite> moneybagIter = moneyList.iterator();
        while (moneybagIter.hasNext())
        {
            Sprite money = moneybagIter.next();
            if (ship.intersects(money))
            {
                moneybagIter.remove();
                score.value++;
            }
        }
    }

    private void steerTheShip(Sprite ship, ArrayList<String> input, double t){
        ship.setVelocity(0,0);
        if (input.contains("LEFT"))
            ship.addVelocity(-20,0);
        if (input.contains("RIGHT"))
            ship.addVelocity(20,0);
        if (input.contains("UP"))
            ship.addVelocity(0,-20);
        if (input.contains("DOWN"))
            ship.addVelocity(0,20);
        ship.update();
    }

    private ArrayList<Sprite> generateEnemyArray(){
        ArrayList<Sprite> enemyList = new ArrayList<Sprite>();
        for (int i = 0; i < 15; i++)
        {
            Sprite enemy = new Sprite();
            double px = 400+(450 * Math.random() + 20);
            double py = 450  * Math.random() + 20;
            enemy.setPosition(px,py);
            Resources resources = new Resources();
            enemy.frames = resources.getEnemyImg();
            enemy.duration = 0.001;
            enemyList.add(enemy);
        }
        return enemyList;

    }

    private void moveEnemies(ArrayList<Sprite> enemyList){
        for(Sprite enemy : enemyList){
            enemy.setVelocity(0,0);
            enemy.addVelocity(-20,0);
            enemy.update();

        }


    }


}





