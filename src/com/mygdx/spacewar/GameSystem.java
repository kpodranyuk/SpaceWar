/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.spacewar;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.mygdx.spacewar.ObjectSprite.ObjectType;
import static com.mygdx.spacewar.ObjectSprite.ObjectType.ENMMISSILE;
import static com.mygdx.spacewar.ObjectSprite.ObjectType.ENMSHIP;
import static com.mygdx.spacewar.ObjectSprite.ObjectType.USRMISSILE;
import static com.mygdx.spacewar.ObjectSprite.ObjectType.USRSHIP;
import java.util.ArrayList;

/**
 * Игровая система, управляющая процессом игры
 * @author Katie
 */
public class GameSystem {
    private PlayerShip player;              // Игрок
    private Array<EnemyShip> enemies;       // Враги
    private Array<Missile> playersMissiles; // Снаряды игрока (выпущенные)
    private Array<Missile> enemiesMissiles; // Снаряды врагов (выпущенные)
    private int curId;
    
    /**
     * Конструктор
     */
    public GameSystem(){
        curId = -1;
        enemies = new Array();
        playersMissiles = new Array();
        enemiesMissiles = new Array();
        
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
        ObjectSprite playersView = new ObjectSprite("ship.png", 34, 37, USRSHIP, controlIdCounter());
        ObjectSprite playersMissileView = new ObjectSprite("fire/greenpng.png", 22, 11, USRMISSILE, controlIdCounter());
        StraightTrajectory playersTrajectory = new StraightTrajectory((float) 170.0, false);
        Missile playersMissile = new Missile(3, (float) 170.0, playersTrajectory, playersMissileView);
        player = new PlayerShip(1, (float) 200.0, playersView, new Weapon(playersMissile));
        //playersMissiles.add(playersMissile);
    }
    
    /**
     * Создать врага
     * @return Отображение врага
     */
    public ObjectSprite generateEnemy(){
        //int enemyIndex = MathUtils.random(0, enemies.size()-1);
        ObjectSprite enemiesView = new ObjectSprite("enemies/enemy1.png", 29, 38, ENMSHIP, controlIdCounter());
        ObjectSprite enemiesMissileView = new ObjectSprite("fire/redpng.png", 22, 11, ENMMISSILE, controlIdCounter());
        StraightTrajectory enemiesTrajectory = new StraightTrajectory((float) 200.0, true);
        Missile enemiesMissile = new Missile(1, (float) 250.0, enemiesTrajectory, enemiesMissileView);        
        EnemyShip enemy = new EnemyShip(3, (float) 200.0, enemiesView, new Weapon(enemiesMissile));
        enemies.add(enemy);
        //enemiesMissiles.add(enemiesMissile);
        return enemiesView;
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
        if (type == ENMSHIP){
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
        else if (type == ENMSHIP && missileType == USRMISSILE){
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
