/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.spacewar;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.mygdx.spacewar.ObjectImage.ObjectType;
import static com.mygdx.spacewar.ObjectImage.ObjectType.ENMMISSILE;
import static com.mygdx.spacewar.ObjectImage.ObjectType.ENMMISSILEFAST;
import static com.mygdx.spacewar.ObjectImage.ObjectType.ENMSHIP;
import static com.mygdx.spacewar.ObjectImage.ObjectType.ENMSHIPFAST;
import static com.mygdx.spacewar.ObjectImage.ObjectType.ENMSHIPHEALTHY;
import static com.mygdx.spacewar.ObjectImage.ObjectType.USRMISSILE;
import static com.mygdx.spacewar.ObjectImage.ObjectType.USRSHIP;
import java.util.ArrayList;

/**
 * Игровая система, управляющая процессом игры
 * @author Katie
 */
public class GameSystem {
    private PlayerShip player;                          // Игрок
    private Array<EnemyShip> enemies;                   // Враги
    private Array<Missile> playersMissiles;             // Снаряды игрока (выпущенные)
    private Array<Missile> enemiesMissiles;             // Снаряды врагов (выпущенные)
    
    private Array<ObjectImage> enemiesSprites;         // Спрайты врагов
    private Array<ObjectImage> enemiesMissilesSprites; // Спрайты снарядов врагов
    private int curId;
    
    public enum Level { EASY, MEDIUM, HARD, ULTRAHARD }
    private Level currentLevel;
    /**
     * Конструктор
     */
    public GameSystem(){
        curId = -1;
        enemies = new Array();
        playersMissiles = new Array();
        enemiesMissiles = new Array();
        currentLevel = Level.EASY;
        
        enemiesSprites = new Array();
        enemiesMissilesSprites = new Array();
        
        createPlayer();
    }
    
    private int controlIdCounter(){
        this.curId+=1;
        if (this.curId>1500){
            this.curId = 0;
        }
        return this.curId;
    }
    
    private void createPlayer(){
        ObjectImage playersImg = new ObjectImage("ship.png", USRSHIP);
        ObjectSprite playersView = new ObjectSprite(playersImg, 34, 37, controlIdCounter());
        
        ObjectImage playersMissileImg = new ObjectImage("fire/greenpng.png", USRMISSILE);
        ObjectSprite playersMissileView = new ObjectSprite(playersMissileImg, 22, 11, controlIdCounter());

        StraightTrajectory playersTrajectory = new StraightTrajectory((float) 170.0, false);
        Missile playersMissile = new Missile(1, (float) 170.0, playersTrajectory, playersMissileView);
        player = new PlayerShip(1, (float) 200.0, playersView, new Weapon(playersMissile));
        //playersMissiles.add(playersMissile);
    }
    
    /**
     * Создать врага
     * @param enemiesKilled Количество убитых игроком кораблей
     * @return Отображение врага
     */
    public ObjectSprite generateEnemy(int enemiesKilled){
        controlLevel(enemiesKilled);
        int enemyIndex = MathUtils.random(1, levelToInt());        
        EnemyShip enemy = null;
        if(enemyIndex==1){
            enemy = generateEasyEnemy();
        }
        else if(enemyIndex==2 || enemyIndex==3){
            enemy = generateHealthyEnemy();
        }
            
        enemies.add(enemy);
        //enemiesMissiles.add(enemiesMissile);
        return enemy.getView();
    }
    
    private EnemyShip generateEasyEnemy(){
        ObjectSprite enemiesView = null;
        ObjectImage enemiesImg = getImageOfEnemyObject(ENMSHIP);
        if (enemiesImg==null){
            enemiesImg = new ObjectImage("enemies/enemy1.png", ENMSHIP);
            enemiesSprites.add(enemiesImg);
        }
        enemiesView = new ObjectSprite(enemiesImg, 29, 38, controlIdCounter());
        
        ObjectSprite enemiesMissileView = null;
        ObjectImage enemiesMissileImg = getImageOfEnemyObject(ENMMISSILE);
        if (enemiesMissileImg==null){
            enemiesMissileImg = new ObjectImage("fire/redpng.png", ENMMISSILE);
            enemiesMissilesSprites.add(enemiesMissileImg);
        }
        enemiesMissileView = new ObjectSprite(enemiesMissileImg, 22, 11, controlIdCounter());
        
        StraightTrajectory enemiesTrajectory = new StraightTrajectory((float) 200.0, true);
        Missile enemiesMissile = new Missile(1, (float) 250.0, enemiesTrajectory, enemiesMissileView);        
        EnemyShip enemy = new EnemyShip(1, (float) 200.0, enemiesView, new Weapon(enemiesMissile));
        return enemy;
    }
    
    private EnemyShip generateHealthyEnemy(){
        ObjectSprite enemiesView = null;
        ObjectImage enemiesImg = getImageOfEnemyObject(ENMSHIPHEALTHY);
        if (enemiesImg==null){
            enemiesImg = new ObjectImage("enemies/enemy2.png", ENMSHIPHEALTHY);
            enemiesSprites.add(enemiesImg);
        }
        enemiesView = new ObjectSprite(enemiesImg, 40, 52, controlIdCounter());
        EnemyShip enemy = new EnemyShip(4, (float) 170.0, enemiesView, new Weapon(null));
        return enemy;
    }
    
    /**
     * Сделать выстрел
     * @param type Тип объекта, инициирующего выстрел
     * @param objectId Id объекта, инициирующего выстрел
     * @return Отображение снаряда
     */
    public ObjectSprite makeShoot(ObjectType type, int objectId){
        if (type == USRSHIP){
            ObjectSprite newMissileView = new ObjectSprite (this.player.getMissile().getView(), controlIdCounter());
            Missile newMissile = new Missile (this.player.getMissile(), newMissileView);
            this.playersMissiles.add(newMissile);
            return newMissileView;
        }
        if (type == ENMSHIP){
            Ship currentEnemy = getActiveEnemy(ENMSHIP, objectId);
            ObjectSprite newMissileView = new ObjectSprite (currentEnemy.getMissile().getView(), controlIdCounter());
            Missile newMissile = new Missile (currentEnemy.getMissile(), newMissileView);
            this.enemiesMissiles.add(newMissile);
            return newMissileView;
        }            
        return null;
    }
    
    public Missile getActiveMissile(ObjectType type, int missileId){
        if (missileId<0)
            return null;
        if (type == USRMISSILE){
            for (Missile miss : this.playersMissiles){
                if (miss.getView().getId() == missileId)
                    return miss;
            }
        }
        if (type == ENMMISSILE){
            for (Missile miss : this.enemiesMissiles){
                if (miss.getView().getId() == missileId)
                    return miss;
            }
        }
        return null;
    }
    
    public Ship getActiveEnemy(ObjectType type, int enemyId){
        if (enemyId<0)
            return null;
        if (type == ENMSHIP || type == ENMSHIPHEALTHY){
            for (Ship enemy: this.enemies){
                if (enemy.getView().getId() == enemyId)
                    return enemy;
            }
        }
        return null;
    }
    
    public boolean isKilledShip(ObjectType type, int shipId, ObjectType missileType, int missileId ){
        if (type == USRSHIP && this.player.getView().getId() == shipId && missileType != USRMISSILE){
            if (missileType != USRMISSILE){
                Missile m = getActiveMissile(missileType, missileId);
                this.player.takeDamage(m.getDamage());
                this.enemiesMissiles.removeValue(m, true);
                return this.player.getHealth() <= 0;
            }
        }
        else if (type == ENMSHIP || type == ENMSHIPHEALTHY && missileType == USRMISSILE){
            Ship s = getActiveEnemy(type, shipId);
            Missile m = getActiveMissile(missileType, missileId);
            if (m == null)
                return false;
            s.takeDamage(m.getDamage());
            this.playersMissiles.removeValue(m, true);
            if (s.getHealth() <= 0){
                enemies.removeValue((EnemyShip) s, true);
                return true;
            }
            return false;
        }
        return false;
    }
    
    public void missilesCollision(ObjectType firstType, int firstId, ObjectType secondType, int secondId ){
        if (firstType == USRMISSILE && secondType == ENMMISSILE){
            this.playersMissiles.removeValue(this.getActiveMissile(firstType, firstId), true);
            System.out.println("Freed user missile with id " + firstId);
            this.playersMissiles.shrink();
            this.enemiesMissiles.removeValue(this.getActiveMissile(secondType, secondId), true);
            System.out.println("Freed enemy missile with id " + secondId);
            this.enemiesMissiles.shrink();
        }
        else if (firstType == ENMMISSILE && secondType == USRMISSILE){
            this.playersMissiles.removeValue(this.getActiveMissile(secondType, secondId), true);
            System.out.println("Freed user missile with id " + secondId);
            this.playersMissiles.shrink();
            this.enemiesMissiles.removeValue(this.getActiveMissile(firstType, firstId), true);
            System.out.println("Freed enemy missile with id " + firstId);
            this.enemiesMissiles.shrink();
        }
    }
    
    public void objectLeftField(ObjectType type, int id){
        if (type == USRMISSILE){
            System.out.println("Freed user missile with id " + id);
            this.playersMissiles.removeValue(this.getActiveMissile(type, id), true);
            this.playersMissiles.shrink();
        }
        else if (type == ENMMISSILE){
            System.out.println("Freed enemy missile with id " + id);
            this.enemiesMissiles.removeValue(this.getActiveMissile(type, id), true);
            this.enemiesMissiles.shrink();
        }
        else if (type == ENMSHIP) {
            System.out.println("Freed enemy ship missile with id " + id);
            this.enemies.removeValue((EnemyShip)this.getActiveEnemy(type, id), true);
            this.enemies.shrink();
        }
    }
    /**
     * Получить корабль игрока
     * @return
     */
    public PlayerShip getPlayer(){
        return this.player;
    }
    
    private void controlLevel(int enemiesKilled){
        if (enemiesKilled>10) {
            currentLevel=Level.MEDIUM;
        }
        else if (enemiesKilled>40) {
            currentLevel=Level.HARD;
        }
        else if (enemiesKilled>80) {
            currentLevel=Level.ULTRAHARD;
        }
    }
    
    private int levelToInt(){
        if (currentLevel==Level.MEDIUM) {
            return 2;
        }
        else if (currentLevel==Level.HARD || currentLevel==Level.ULTRAHARD) {
            return 3;
        }
        return 1;
    }
    
    private ObjectImage getImageOfEnemyObject(ObjectType type){
        if (type==ENMMISSILE || type==ENMMISSILEFAST){
            for(ObjectImage sprite:  enemiesMissilesSprites){
                if (sprite.getObjType()==type)
                    return sprite;
            }
            return null;
        }
        for(ObjectImage sprite:  enemiesSprites){
            if (sprite.getObjType()==type)
                return sprite;
        }
        return null;
    }
    
    /**
     * Получить корабль по его типу
     * TODO Доделать метод
     * @param type Тип корабля
     * @return Корабль с данным типом
     */
    /*public Ship getShip(ObjectType type){
        if (type == USRSHIP)
            return this.player;
        if (type == ENMSHIP)
            return this.enemies.get(0);
        return null;
    }*/
}
