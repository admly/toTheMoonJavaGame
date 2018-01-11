package toTheMoon;

import toTheMoon.res.ResourcesManager;

public class ModelFactory {
    public  AbstractSpriteModel getModel(String modelType){

        if(modelType == null){
            return null;
        }
        if(modelType.equals("Ship")){
            AbstractSpriteModel player = new Player();
            player.setPosition(0, 256);
            ResourcesManager resourcesManager = new ResourcesManager();
            player.frames = resourcesManager.getShipImg();
            player.duration = 0.001;
            return player;
        }
        if(modelType.equals("Enemy")){
            AbstractSpriteModel enemy = new Enemy();
            double px = 450+(450 * Math.random() + 20);
            double py =450  * Math.random() + 20;
            enemy.setPosition(px,py);
            ResourcesManager resourcesManager = new ResourcesManager();
            enemy.frames = resourcesManager.getEnemyImg();
            enemy.duration = 0.001;
            return enemy;

        }
        if(modelType.equals("Moneybag")){
            AbstractSpriteModel moneybag = new Moneybag();
            moneybag.setImage(getClass().getResourceAsStream("res/moneybag.png"));
            double px = 450 * Math.random() + 20;
            double py = 450  * Math.random() + 20;
            moneybag.setPosition(px,py);
            return moneybag;
        }
        return null;


    }



}


