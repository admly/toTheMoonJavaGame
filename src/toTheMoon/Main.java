package toTheMoon;

import com.sun.javafx.geom.Rectangle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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
import org.apache.commons.lang3.time.StopWatch;
import sun.misc.IOUtils;
import toTheMoon.res.ResourcesManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class Main extends Application
{

    private Timeline gameLoop;
    private ResourcesManager resourcesManager;
    private Image space;
    private AbstractSpriteModel ship;
    private ArrayList<AbstractSpriteModel> enemyList;
    private double lastTime;
    private int enemyWaveCounter = 0;
    private ArrayList<AbstractSpriteModel> enemyList2;
    private ArrayList<AbstractSpriteModel> moneyList = new ArrayList<AbstractSpriteModel>();
    public static void main(String[] args)
    {
        launch(args);
    }
    private AtomicInteger score = new AtomicInteger(0);
    private ModelFactory modelFactory = new ModelFactory();
    private boolean gameOver = false;
    private String newHighScore;

    @Override
    public void start(Stage stage)
    {
        ArrayList<String> input = new ArrayList<String>();

        stage.setTitle( "Collect the Money Bags!" );
        Group root = new Group();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        Canvas canvas = new Canvas( 512, 512 );
        root.getChildren().add(canvas);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        Font font = Font.font( "Helvetica", FontWeight.BOLD, 24 );
        gc.setFont(font);
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);

        ship = getShip();
        space = new Image(getClass().getResourceAsStream( "res/space.png" ));
        initHandlers(input, scene);
        moneyList = generateInitialArraysWithEnemiesAndMoneybags("Moneybag"); //creates list with money sprites, each with random x,y position
        enemyList = generateInitialArraysWithEnemiesAndMoneybags("Enemy");

        gameLoop = new Timeline();
        gameLoop.setCycleCount( Timeline.INDEFINITE );
        final long timeStart = System.currentTimeMillis();
        final StopWatch stopwatch = new StopWatch();
        stopwatch.start();

        KeyFrame kf = new KeyFrame(
               Duration.seconds(0.017),                // 60 FPS
                new EventHandler<ActionEvent>()
                {
                    public void handle(ActionEvent ae)
                    {
                        enemyWaveCounter++;
                        double time = (System.currentTimeMillis() - timeStart) / 100000.0;
                        double currentTime = System.currentTimeMillis()/1000;
                        double multiplier = 0.1;
                        generateSingleBagAtRandomCoordinates();
                        checkForCollisions(enemyList, enemyList2, ship, moneyList, score);
                        if(gameOver==true){
                            endGameSequence(root, score);
                        }
                        generateNextEnemyWave(stopwatch);
                        steerTheShip(ship, input);
                        moveEnemies(enemyList, enemyList2);
                        renderTheScene(timeStart, time, gc, space,
                                ship, moneyList, enemyList, score, enemyList2,
                                700- enemyWaveCounter); //700 to make timer stop at 1
                    }
                });

        gameLoop.getKeyFrames().add(kf);
        gameLoop.play();
        stage.show();
    }

    private void endGameSequence(Group root, AtomicInteger score) {
        newHighScore = HighScoreChecker.checkHighScore(score);
        Canvas endGame = new Canvas(512,512);
        GraphicsContext endGc = endGame.getGraphicsContext2D();
        Font font = Font.font( "Helvetica", FontWeight.BOLD, 24 );
        endGc.setFont(font);
        endGc.setFill(Color.GREEN);
        endGc.setStroke(Color.BLACK);
        endGc.setLineWidth(1);
        root.getChildren().add(endGame);
        endGc.clearRect(0, 0, 512,512);
        endGc.drawImage(space, 0, 0);

        String pointsText ="GAME OVER. SCORE: " + score.get()*100;
        endGc.fillText(pointsText, 100, 256);
        endGc.strokeText(pointsText, 100, 256);
        endGc.fillText(newHighScore, 50,300);
        endGc.strokeText(newHighScore, 50, 300);

        gameLoop.stop();
    }

    /**
     * Jak sama nazwa wskazuje, generuje pojednyczny worek w zasadzie co kazdą falę przeciwnikow
     */
    private void generateSingleBagAtRandomCoordinates(){
        if(enemyWaveCounter >600) {
            moneyList.add(modelFactory.getModel("Moneybag"));
        }
    }

    /**
     * When enemyWaveCounter, which is being increased every game loop, reach 600 (about 10 sec)
     * method generates new enemy array.
     * @param stopwatch
     */
    private void generateNextEnemyWave(StopWatch stopwatch) {
        if(enemyWaveCounter >600){
            enemyWaveCounter =0;
            enemyList2 = generateInitialArraysWithEnemiesAndMoneybags("Enemy");
        }
    }

    /**
     * Method returns ship sprite.
     * @return
     */
    private AbstractSpriteModel getShip() {
        return modelFactory.getModel("Ship");
    }

    /**
     * Method creates new handler for key press and release
     * @param input
     * @param theScene
     */
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

    private void renderTheScene(double timeStart, double time, GraphicsContext gc, Image space,
                                AbstractSpriteModel ship, ArrayList<AbstractSpriteModel> moneyList, ArrayList<AbstractSpriteModel> enemyList,
                                AtomicInteger score, ArrayList<AbstractSpriteModel> enemyList2, int nextWaveTime) {
        gc.clearRect(0, 0, 512,512);
        gc.drawImage(space, 0, 0);
        ship.render(time, gc);

        for (AbstractSpriteModel money : moneyList)
            money.render(gc);

        for (AbstractSpriteModel enemy : enemyList)
            enemy.render(time, gc);

        if (enemyList2 != null) { // list is not created until the first next wave of enemies
            for (AbstractSpriteModel enemy : enemyList2)
                enemy.render(time, gc);
        }

        String pointsText = "Cash: $" + (100 * score.get());
        gc.fillText(pointsText, 360, 36);
        gc.strokeText(pointsText, 360, 36);

        String waveTimer = "NEXT WAVE IN: " + String.valueOf(nextWaveTime).substring(0,1);
        gc.fillText(waveTimer, 36,36);
        gc.strokeText(waveTimer, 36, 36);
    }

    private void checkForCollisions(ArrayList<AbstractSpriteModel> enemyList, ArrayList<AbstractSpriteModel> enemyList2,
                                    AbstractSpriteModel ship, ArrayList<AbstractSpriteModel> moneyList,
                                    AtomicInteger score){
        Iterator<AbstractSpriteModel> moneybagIter = moneyList.iterator();
        while (moneybagIter.hasNext())
        {
            AbstractSpriteModel money = moneybagIter.next();
            if (ship.intersects(money))
            {
                moneybagIter.remove();
                score.getAndIncrement();
            }
        }
        Iterator<AbstractSpriteModel> enemyIter1 = enemyList.iterator();
        while (enemyIter1.hasNext())
        {
            AbstractSpriteModel enemy = enemyIter1.next();
            if (ship.intersects(enemy))
            {
                enemyIter1.remove();
                gameOver = true;
            }
        }
        if(enemyList2 != null){
        Iterator<AbstractSpriteModel> enemyIter2 = enemyList2.iterator();
        while (enemyIter2.hasNext())
        {
            AbstractSpriteModel enemy = enemyIter2.next();
            if (ship.intersects(enemy))
            {
                enemyIter2.remove();
                gameOver = true;
            }
        }
        }
    }

    private void steerTheShip(AbstractSpriteModel ship, ArrayList<String> input){
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

    private ArrayList<AbstractSpriteModel> generateInitialArraysWithEnemiesAndMoneybags(String arrayType){
        ArrayList<AbstractSpriteModel> modelList = new ArrayList<AbstractSpriteModel>();
            for (int i = 0; i < 15; i++) {
                if(arrayType.equals("Moneybag")) {
                    modelList.add(modelFactory.getModel("Moneybag"));
                }else if(arrayType.equals("Enemy")){
                    modelList.add(modelFactory.getModel("Enemy"));
                }
        }
        return modelList;
    }

    private void moveEnemies(ArrayList<AbstractSpriteModel> enemyList, ArrayList<AbstractSpriteModel> enemyList2) {
        for (AbstractSpriteModel enemy : enemyList) {
            enemy.setVelocity(0, 0);
            enemy.addVelocity(-30, 0);
            enemy.update();
        }
        if (enemyList2 != null) { // list is not created until the first next wave of enemies
            for (AbstractSpriteModel enemy : enemyList2) {
                enemy.setVelocity(0, 0);
                enemy.addVelocity(-30, 0);
                enemy.update();
            }
        }
    }
}





