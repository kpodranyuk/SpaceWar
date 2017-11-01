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
    private PlayerShip player;                          /// Игрок
    private Array<EnemyShip> enemies;                   /// Враги
    private Array<Missile> playersMissiles;             /// Снаряды игрока (выпущенные)
    private Array<Missile> enemiesMissiles;             /// Снаряды врагов (выпущенные)
    
    private Array<ObjectImage> enemiesSprites;          /// Спрайты врагов
    private Array<ObjectImage> enemiesMissilesSprites;  /// Спрайты снарядов врагов
    private int curId;                                  /// Текущий идентификатор
    
    /**
     * Уровень игры, отвечающий за разнообразие создаваемых врагов
     * EASY - мелкие враги, стреляющие прямо
     * MEDIUM - EASY + крупные враги с большим хп (не стреляют)
     * HARD и ULTRAHARD - MEDIUM + быстрые мелкие враги, стреляющие по дуге
     */
    public enum Level { EASY, MEDIUM, HARD, ULTRAHARD }
    private Level currentLevel;                         /// Текущий уровень игры
    
    /**
     * Конструктор
     */
    public GameSystem(){
        // Инициализируем поля
        curId = -1;
        enemies = new Array();
        playersMissiles = new Array();
        enemiesMissiles = new Array();
        currentLevel = Level.EASY;
        
        enemiesSprites = new Array();
        enemiesMissilesSprites = new Array();
        
        // Создаем игрока
        createPlayer();
    }
    
    /**
     * Контролировать численность идентификатора игры
     */
    private int controlIdCounter(){
        // Увеличиваем идентификатор
        this.curId+=1;
        // Если достигнут предел, обнуляем счетчик
        if (this.curId>1500){
            this.curId = 0;
        }
        return this.curId;
    }
    
    /**
     * Создать игрока
     */
    private void createPlayer(){
        // Создаем изображение и спрайт корабля игрока
        ObjectImage playersImg = new ObjectImage("ship.png", USRSHIP);
        ObjectSprite playersView = new ObjectSprite(playersImg, 34, 37, controlIdCounter());
        
        // Создаем изображение и спрайт снаряда игрока
        ObjectImage playersMissileImg = new ObjectImage("fire/greenpng.png", USRMISSILE);
        ObjectSprite playersMissileView = new ObjectSprite(playersMissileImg, 22, 11, controlIdCounter());

        // Снаряд игрока летит по прямой траектории
        StraightTrajectory playersTrajectory = new StraightTrajectory((float) 250.0, false);
        // Создаем снаряд игрока
        Missile playersMissile = new Missile(1, (float) 250.0, playersTrajectory, playersMissileView);
        // Создаем объект игрока
        player = new PlayerShip(3, (float) 200.0, playersView, new Weapon(playersMissile));
    }
    
    /**
     * Создать врага
     * @param enemiesKilled Количество убитых игроком кораблей
     * @return Отображение врага
     */
    public ObjectSprite generateEnemy(int enemiesKilled){
        // В зависимости от убитых врагов вычисляем текущий уровень игры
        controlLevel(enemiesKilled);
        // Определяем тип создаваемого игрока, основываясь на сложности игры
        int enemyIndex = MathUtils.random(1, levelToInt());        
        EnemyShip enemy = null;
        // Если первый тип, то создаем легкого врага
        if(enemyIndex==1){
            enemy = generateEasyEnemy();
        }
        // Иначе если второй тип, то создаем врага с большим хп
        else if(enemyIndex==2){
            enemy = generateHealthyEnemy();
        }
        // Иначе если третий тип, то создаем врага, стреляющего по дуге
        else if(enemyIndex==3){
            enemy = generateFastEnemy();
        }            
        // Запоминаем созданного игрока
        enemies.add(enemy);
        // Возвращаем отображение игрока
        return enemy.getView();
    }
    
    /**
     * Создать легкого врага
     * @return Враг
     */
    private EnemyShip generateEasyEnemy(){
        // Создаем отображение врага
        ObjectSprite enemiesView = null;
        // Запрашиваем имеется ли в памяти изображение врага
        ObjectImage enemiesImg = getImageOfEnemyObject(ENMSHIP);
        // Если изображения нет, то создаем и добавляем в массив
        if (enemiesImg==null){
            enemiesImg = new ObjectImage("enemies/enemy1.png", ENMSHIP);
            enemiesSprites.add(enemiesImg);
        }
        // Создаем спрайт врага
        enemiesView = new ObjectSprite(enemiesImg, 29, 38, controlIdCounter());
        
        // Повторяем то же самое с снарядом врага
        ObjectSprite enemiesMissileView = null;
        ObjectImage enemiesMissileImg = getImageOfEnemyObject(ENMMISSILE);
        if (enemiesMissileImg==null){
            enemiesMissileImg = new ObjectImage("fire/redpng.png", ENMMISSILE);
            enemiesMissilesSprites.add(enemiesMissileImg);
        }
        enemiesMissileView = new ObjectSprite(enemiesMissileImg, 22, 11, controlIdCounter());
        
        // Снаряд легкого врага летит по прямой траектории
        StraightTrajectory enemiesTrajectory = new StraightTrajectory((float) 250.0, true);
        // Создаем вражеский снаряд
        Missile enemiesMissile = new Missile(1, (float) 250.0, enemiesTrajectory, enemiesMissileView); 
        // Создаем вражеский корабль
        EnemyShip enemy = new EnemyShip(1, (float) 200.0, enemiesView, new Weapon(enemiesMissile));
        
        // Возвращаем созданного врага
        return enemy;
    }
    
    /**
     * Создать врага с большим здоровьем
     * @return Враг
     */
    private EnemyShip generateHealthyEnemy(){
        // Создаем отображение врага
        ObjectSprite enemiesView = null;
        // Запрашиваем имеется ли в памяти изображение врага
        ObjectImage enemiesImg = getImageOfEnemyObject(ENMSHIPHEALTHY);
        // Если изображения нет, то создаем и добавляем в массив
        if (enemiesImg==null){
            enemiesImg = new ObjectImage("enemies/enemy2.png", ENMSHIPHEALTHY);
            enemiesSprites.add(enemiesImg);
        }
        // Создаем спрайт врага
        enemiesView = new ObjectSprite(enemiesImg, 40, 52, controlIdCounter());
        // Создаем врага с учетом того, что у данного типа нет снарядов и большое хп
        EnemyShip enemy = new EnemyShip(6, (float) 170.0, enemiesView, new Weapon(null));
        // Возвращаем созданного врага
        return enemy;
    }
    
    /**
     * Создать врага, стреляющего по дуге
     * @return Враг
     */
    private EnemyShip generateFastEnemy(){
        // Создаем отображение врага
        ObjectSprite enemiesView = null;
        // Запрашиваем имеется ли в памяти изображение врага
        ObjectImage enemiesImg = getImageOfEnemyObject(ENMSHIPFAST);
        // Если изображения нет, то создаем и добавляем в массив
        if (enemiesImg==null){
            enemiesImg = new ObjectImage("enemies/enemy3.png", ENMSHIPFAST);
            enemiesSprites.add(enemiesImg);
        }
        // Создаем спрайт врага
        enemiesView = new ObjectSprite(enemiesImg, 19, 31, controlIdCounter());
        
        // Повторяем то же самое с снарядом врага
        ObjectSprite enemiesMissileView = null;
        ObjectImage enemiesMissileImg = getImageOfEnemyObject(ENMMISSILE);
        if (enemiesMissileImg==null){
            enemiesMissileImg = new ObjectImage("fire/redpng.png", ENMMISSILE);
            enemiesMissilesSprites.add(enemiesMissileImg);
        }
        enemiesMissileView = new ObjectSprite(enemiesMissileImg, 22, 11, controlIdCounter());
        
        // Снаряд данного врага стреляет по дуге, поэтому у него дуговая траектория
        ArcTrajectory enemiesTrajectory = new ArcTrajectory((float) 330.0, true);
        // Создаем снаряд врага
        Missile enemiesMissile = new Missile(2, (float) 330.0, enemiesTrajectory, enemiesMissileView);        
        // Создаем врага
        EnemyShip enemy = new EnemyShip(2, (float) 300.0, enemiesView, new Weapon(enemiesMissile));
        // Возвращаем созданного врага
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
        if (type == ENMSHIP || type == ENMSHIPFAST){
            Ship currentEnemy = getActiveEnemy(type, objectId);
            ObjectSprite newMissileView = new ObjectSprite (currentEnemy.getMissile().getView(), controlIdCounter());
            Missile newMissile = new Missile (currentEnemy.getMissile(), newMissileView);
            this.enemiesMissiles.add(newMissile);
            return newMissileView;
        }            
        return null;
    }
    
    /**
     * Получить активный снаряд
     * @param type Тип снаряда
     * @param missileId Идентификатор снаряда
     * @return Снаряд
     */
    public Missile getActiveMissile(ObjectType type, int missileId){
        // Проверяем идентификатор на корректность
        if (missileId<0)
            return null;
        // Если идентификатор корректный..
        // Если запрашивают пользовательский снаряд..
        if (type == USRMISSILE){
            // Для каждого активного пользовательского снаряда..
            for (Missile miss : this.playersMissiles){
                // Если идентификатор текущего снаряда совпадает с требуемым, возвращаем текущий снаряд
                if (miss.getView().getId() == missileId)
                    return miss;
            }
        }
        // Если запрашивают вражеский снаряд..
        if (type == ENMMISSILE){
            // Для каждого активного вражеского снаряда..
            for (Missile miss : this.enemiesMissiles){
                // Если идентификатор текущего снаряда совпадает с требуемым, возвращаем текущий снаряд
                if (miss.getView().getId() == missileId)
                    return miss;
            }
        }
        // Если не был найден ни один снаряд, возвращаем null
        return null;
    }
    
    /**
     * Получить активного врага
     * @param type Тип врага
     * @param enemyId Идентификатор врага
     * @return Враг
     */
    public Ship getActiveEnemy(ObjectType type, int enemyId){
        // Проверяем на корректность идентификатор
        if (enemyId<0)
            return null;
        // Если идентификатор корректный..
        // Если требуемый тип является типом вражеских кораблей..
        if (type == ENMSHIP || type == ENMSHIPHEALTHY || type == ENMSHIPFAST){
            // Для каждого активного врага в массиве врагов..
            for (Ship enemy: this.enemies){
                // Если идентификатор текущего врага совпадает с требуемым, возвращаем текущего врага
                if (enemy.getView().getId() == enemyId)
                    return enemy;
            }
        }
        // Возвращаем null, так как враг так и не нашелся
        return null;
    }
    
    /**
     * Определить убит ли корабль
     * @param type Тип корабля
     * @param shipId Идентификатор корабля
     * @param missileType Тип снаряда
     * @param missileId Идентификатор снаряда
     * @return true, если корабль убит, false иначе
     */
    public boolean isKilledShip(ObjectType type, int shipId, ObjectType missileType, int missileId ){
        // Если корабль - пользовательский, а снаряд вражеский..
        if (type == USRSHIP && this.player.getView().getId() == shipId && missileType != USRMISSILE){
            if (missileType != USRMISSILE){
                // Запрашиваем урон снаряда
                Missile m = getActiveMissile(missileType, missileId);
                // Отправляем пользователю урон от снаряда
                this.player.takeDamage(m.getDamage());
                // Уничтожаем вражеский снаряд
                this.enemiesMissiles.removeValue(m, true);
                // Возвращаем значение того, что здоровье игрока меньше либо равно 0 (мертв ли игрок)
                return this.player.getCurrentHealth() <= 0;
            }
        }
        // Иначе если корабль вражеский, а снаряд пользовательский..
        else if (type == ENMSHIP || type == ENMSHIPHEALTHY || type == ENMSHIPFAST && missileType == USRMISSILE){
            // Получаем корабль 
            Ship s = getActiveEnemy(type, shipId);
            // Получаем снаряд
            Missile m = getActiveMissile(missileType, missileId);
            if (m == null)
                return false;
            // Отправляем врагу урон от снаряда
            s.takeDamage(m.getDamage());
            // Удаляем пользовательский снаряд
            this.playersMissiles.removeValue(m, true);
            // Если враг убит, возвращаем true и удаляем его из списка
            if (s.getCurrentHealth() <= 0){
                enemies.removeValue((EnemyShip) s, true);
                return true;
            }
            // Иначе возвращаем false
            return false;
        }
        // Иначе возвращаем false
        return false;
    }
    
    /**
     * Столкновение снарядов
     * @param firstType Тип первого снаряда
     * @param firstId Идентификатор первого снаряда
     * @param secondType Тип второго снаряда
     * @param secondId Идентификатор первого снаряда
     */
    public void missilesCollision(ObjectType firstType, int firstId, ObjectType secondType, int secondId ){
        // Если первый снаряд пользовательский, а второй вражеский
        if (firstType == USRMISSILE && secondType == ENMMISSILE){
            // Удаляем первый снаряд из списка пользовательских снарядов
            this.playersMissiles.removeValue(this.getActiveMissile(firstType, firstId), true);
            System.out.println("Freed user missile with id " + firstId);
            // Уменьшаем память массива пользовательских снарядов
            this.playersMissiles.shrink();
            // Удаляем второй снаряд из списка вражеских снарядов
            this.enemiesMissiles.removeValue(this.getActiveMissile(secondType, secondId), true);
            System.out.println("Freed enemy missile with id " + secondId);
            // Уменьшаем память массива вражеских снарядов
            this.enemiesMissiles.shrink();
        }
        // Иначе если первый снаряд вражеский, а второй пользовательский
        else if (firstType == ENMMISSILE && secondType == USRMISSILE){
            // Удаляем второй снаряд из списка пользовательских снарядов
            this.playersMissiles.removeValue(this.getActiveMissile(secondType, secondId), true);
            System.out.println("Freed user missile with id " + secondId);
            // Уменьшаем память массива пользовательских снарядов
            this.playersMissiles.shrink();
            // Удаляем первый снаряд из списка вражеских снарядов
            this.enemiesMissiles.removeValue(this.getActiveMissile(firstType, firstId), true);
            System.out.println("Freed enemy missile with id " + firstId);
            // Уменьшаем память массива вражеских снарядов
            this.enemiesMissiles.shrink();
        }
    }
    
    /**
     * Объект вылетел за пределы поля
     * @param type Тип объекта
     * @param id Идентификатор объекта
     */
    public void objectLeftField(ObjectType type, int id){
        // Если тип объекта - пользовательский снаряд
        if (type == USRMISSILE){
            System.out.println("Freed user missile with id " + id);
            // Удаляем объект из массива и ужимаем занимаемую им память
            this.playersMissiles.removeValue(this.getActiveMissile(type, id), true);
            this.playersMissiles.shrink();
        }
        // Если тип объекта - вражеский снаряд
        else if (type == ENMMISSILE){
            System.out.println("Freed enemy missile with id " + id);
            // Удаляем объект из массива и ужимаем занимаемую им память
            this.enemiesMissiles.removeValue(this.getActiveMissile(type, id), true);
            this.enemiesMissiles.shrink();
        }
        // Если тип объекта - вражеский корабль
        else if (type == ENMSHIP || type == ENMSHIPHEALTHY || type == ENMSHIPFAST) {
            System.out.println("Freed enemy ship missile with id " + id);
            // Удаляем объект из массива и ужимаем занимаемую им память
            this.enemies.removeValue((EnemyShip)this.getActiveEnemy(type, id), true);
            this.enemies.shrink();
        }
    }
    
    /**
     * Получить корабль игрока
     * @return Корабль игрока
     */
    public PlayerShip getPlayer(){
        return this.player;
    }
    
    /**
     * Контролировать уровень игры
     * @param enemiesKilled Количество убитых врагов
     */
    private void controlLevel(int enemiesKilled){
        // Если 10<убитых врагов<=40, то игра на среднем уровне
        if (enemiesKilled>10 && enemiesKilled<=40) {
            currentLevel=Level.MEDIUM;
        }
        // Если 40<убитых врагов<=80, то игра на сложном уровне
        else if (enemiesKilled>40 && enemiesKilled<=80) {
            currentLevel=Level.HARD;
        }
        // Если убитых врагов>80, то игра на суперсложном уровне
        else if (enemiesKilled>80) {
            currentLevel=Level.ULTRAHARD;
        }
    }
    
    /**
     * Перевести текущий уровень игры в числовое представление
     * @return Корабль игрока
     */
    private int levelToInt(){
        // Если текущий уровень средний, вернуть 2
        if (currentLevel==Level.MEDIUM) {
            return 2;
        }
        // Иначе если текущий уровень сложнй или ультрасложный, вернуть 3
        else if (currentLevel==Level.HARD || currentLevel==Level.ULTRAHARD) {
            return 3;
        }
        // Вернуть 1
        return 1;
    }
    
    /**
     * Получить изображение вражеского объекта
     * @param type Тип вражеского объекта
     * @return Изображение вражеского объекта
     */
    private ObjectImage getImageOfEnemyObject(ObjectType type){
        // Если тип объекта - вражеский снаряд
        if (type==ENMMISSILE || type==ENMMISSILEFAST){
            // Ищем в массиве вражеских снарядов необходимый и возвращаем его
            for(ObjectImage sprite:  enemiesMissilesSprites){
                if (sprite.getObjType()==type)
                    return sprite;
            }
            // Если снаряд не нашелся, возвращаем null
            return null;
        }
        // Иначе ищем объект среди вражеских кораблей и возвращаем его
        for(ObjectImage sprite:  enemiesSprites){
            if (sprite.getObjType()==type)
                return sprite;
        }
        // Если корабль не найден, возвращаем null
        return null;
    }
}
