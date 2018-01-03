package toTheMoon.res;

import javafx.scene.image.Image;

public class Resources {


    private Image shipImg[] = new Image[4];
    private Image enemyImg[] = new Image[6];

    public Resources() {
        try {
            for (int i = 0; i < shipImg.length; i++) {
                shipImg[i] = new Image(getClass().getResourceAsStream("ship/f" + i + ".png"));
            }
            for (int i = 1; i < enemyImg.length+1; i++) {
                enemyImg[i-1] = new Image(getClass().getResourceAsStream("enemy/Example/e_f" + i + ".png"));
            }

        } catch (Exception e) {
            System.out.println("Problem in loading resources");
        }
    }

    public Image[] getShipImg() {
        return shipImg;
    }

    public void setShipImg(Image[] shipImg) {
        this.shipImg = shipImg;
    }

    public void setEnemyImg(Image[] enemyImg) {
        this.enemyImg = enemyImg;
    }

    public Image[] getEnemyImg() {
        return enemyImg;
    }
}
