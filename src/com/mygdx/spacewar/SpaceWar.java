package com.mygdx.spacewar;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import static com.mygdx.spacewar.ObjectImage.ObjectType.ENMSHIP;
import static com.mygdx.spacewar.ObjectImage.ObjectType.ENMSHIPHEALTHY;
import static com.mygdx.spacewar.SpaceWar.GAMESTATE.GAMEOVER;
import static com.mygdx.spacewar.SpaceWar.GAMESTATE.PAUSED;
import static com.mygdx.spacewar.SpaceWar.GAMESTATE.PLAY;
import java.util.Iterator;

/**
 * Панель игры
 * @author Katie
 */
public class SpaceWar extends ApplicationAdapter {
    
    public enum GAMESTATE { PAUSED, GAMEOVER, PLAY };
    private GAMESTATE state;
    private GAMESTATE lastState;
    private GameSystem system;
    
    private OrthographicCamera camera;  /// Камера
    private SpriteBatch batch;          /// Batch

    private Texture img;                /// Изображение фона
    private TextureRegion back;         /// Фон

    private ObjectSprite playersView;   /// Отображение игрока
    
    private Array<ObjectSprite> enemies;   /// Массив врагов
    private Array<ObjectSprite> enemysMissiles;   /// Массив врагов
    private Array<ObjectSprite> playersMissiles;  /// Массив снарядов героя
    private Array<ObjectSprite> bonuses;  /// Массив бонусов
    
    private static long respawnTime = 2200;
    private long lastDropTime;          /// Время последнего выпадения врага
    
    private static final long bonusDeltaTime = 6000;
    private long lastBonusTime;
    
    private static final long shootDeltaTime = 350;
    private long lastShootTime;
    
    private static final long spacePushedDeltaTime = 150;
    private long spacePushedTime;

    private int enemiesDestroyed;          /// Количество сбитых врагов
    private BitmapFont gameFont;            /// Шрифт
    private BitmapFont infoFont;            /// Шрифт

    @Override
    public void create () {
        // Создаем камеру
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 450);

        // Настраиваем бэкграунд
        img = new Texture("background.png");
        back = new TextureRegion(img, 0, 0, 1366, 768);
        // Создаем шрифт и задаем ему цвет
        gameFont = new BitmapFont();
        gameFont.setColor(Color.WHITE);
        
        infoFont = new BitmapFont();
        infoFont.setColor(Color.WHITE);
        infoFont.getData().setScale(2, 2);

        startRoutine();
    }
    
    @Override
    public void pause(){
        //this.lastState = this.state;
        this.state = PAUSED;
        spacePushedTime = TimeUtils.nanosToMillis(TimeUtils.nanoTime());
    }

    @Override
    public void render () {
        // Задаем цвет очистки
        Gdx.gl.glClearColor(1, 0, 0, 1);
        // Очищаем экран
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Обновляем камеру
        camera.update();
        if (null != state) switch (state) {
            case PLAY:
                // Сообщаем SpriteBatch использовать систему координат камеры. (матрицу проекции)
                // SpriteBatch нарисует все, что будет находиться в заданных координатах.
                batch.setProjectionMatrix(camera.combined);
                // Начинаем сессию
                batch.begin();
                // Отрисовываем фон
                batch.draw(back, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                // Отрисовываем количество сбитых кораблей
                gameFont.draw(batch, "Enemies killed: " + enemiesDestroyed + ". HP: " + system.getPlayer().getCurrentHealth(), 0, 450);
                // Отрисовываем корабль героя
                batch.draw(playersView.getTexture(), playersView.rect.x, playersView.rect.y);
                // Отрисовываем все имеющиеся корабли врага
                for(ObjectSprite enemy: enemies) {
                    batch.draw(enemy.getTexture(), enemy.rect.x, enemy.rect.y);
                }   
                for(ObjectSprite enemyMissile: this.enemysMissiles) {
                    batch.draw(enemyMissile.getTexture(), enemyMissile.rect.x, enemyMissile.rect.y);
                }   
                for(ObjectSprite bonus: this.bonuses){
                    batch.draw(bonus.getTexture(), bonus.rect.x, bonus.rect.y);
                }
                for(ObjectSprite playersMissile: this.playersMissiles) {
                    batch.draw(playersMissile.getTexture(), playersMissile.rect.x, playersMissile.rect.y);
                }   
                // Завершаем сессию
                batch.end();
                // Если нужно опустить корабль - опускаем с заданной скоростью
                if(Gdx.input.isKeyPressed(Keys.DOWN))
                    playersView.rect.y -= system.getPlayer().getSpeed() * Gdx.graphics.getDeltaTime();
                // Если нужно поднять корабль - поднимаем с заданной скоростью
                if(Gdx.input.isKeyPressed(Keys.UP))
                    playersView.rect.y += system.getPlayer().getSpeed() * Gdx.graphics.getDeltaTime();
                // Если нужно замедлить корабль - замедляем с заданной скоростью
                if(Gdx.input.isKeyPressed(Keys.LEFT))
                    playersView.rect.x -= system.getPlayer().getSpeed() * Gdx.graphics.getDeltaTime();
                // Если нужно ускорить корабль - ускоряем с заданной скоростью
                if(Gdx.input.isKeyPressed(Keys.RIGHT))
                    playersView.rect.x += system.getPlayer().getSpeed() * Gdx.graphics.getDeltaTime();
                if(Gdx.input.isKeyPressed(Keys.X)&& TimeUtils.nanosToMillis(TimeUtils.nanoTime()) - lastShootTime > shootDeltaTime)
                    shoot();
                if(Gdx.input.isKeyPressed(Keys.SPACE) && TimeUtils.nanosToMillis(TimeUtils.nanoTime()) - this.spacePushedTime > SpaceWar.spacePushedDeltaTime)
                    this.pause();
                controlPlayerPosition();
                // Если пришло время - респавним новые корабли
                long timePassed = TimeUtils.nanosToMillis(TimeUtils.nanoTime()) - lastDropTime;
                if(timePassed > respawnTime)
                    spawnEnemy();
                timePassed = TimeUtils.nanosToMillis(TimeUtils.nanoTime()) - this.lastBonusTime;
                if(timePassed > bonusDeltaTime && this.bonuses.size == 0){
                    showBonus();
                }  
                controlEnemiesSprites();
                controlEnemiesMissilesPosition();
                controlPlayerMissilesPosition();
                controlBonusesPosition();                              
                break;
            case PAUSED:
                // Сообщаем SpriteBatch использовать систему координат камеры. (матрицу проекции)
                // SpriteBatch нарисует все, что будет находиться в заданных координатах.
                batch.setProjectionMatrix(camera.combined);
                // Начинаем сессию
                batch.begin();
                // Отрисовываем фон
                batch.draw(back, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                // Отрисовываем сообщение о том, что игра приостановлена
                infoFont.draw(batch, "GAME PAUSED", 300, (float) 150);
                batch.end();
                // Отрисовываем корабль героя
                if(Gdx.input.isKeyPressed(Keys.SPACE) && TimeUtils.nanosToMillis(TimeUtils.nanoTime()) - this.spacePushedTime > SpaceWar.spacePushedDeltaTime){
                    spacePushedTime = TimeUtils.nanosToMillis(TimeUtils.nanoTime());
                    this.state = this.lastState;
                    //this.lastState = PAUSED;
                }   
                break;
            case GAMEOVER:
                // Сообщаем SpriteBatch использовать систему координат камеры. (матрицу проекции)
                // SpriteBatch нарисует все, что будет находиться в заданных координатах.
                batch.setProjectionMatrix(camera.combined);
                // Начинаем сессию
                batch.begin();
                // Отрисовываем фон
                batch.draw(back, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                // Отрисовываем сообщение о том, что игра приостановлена
                infoFont.draw(batch, "GAME OVER", 300, (float) 150);
                infoFont.draw(batch, "YOUR SCORE IS " + this.enemiesDestroyed, 260, (float) 100);
                infoFont.draw(batch, "PRESS ENTER TO START OVER", 170, (float) 50);
                batch.end();
                this.lastState = GAMEOVER;
                this.state = GAMEOVER;
                if(Gdx.input.isKeyPressed(Keys.ENTER)){
                    startRoutine();
                }
                break;
            default:
                break;                
        }
    }
    
    private void startRoutine(){
        if (bonuses!=null)
            bonuses.clear();
        else
            bonuses = new Array<ObjectSprite>();
        if (enemies!=null) 
            enemies.clear();
        else
            this.enemies = new Array<ObjectSprite>();
        if (enemysMissiles!=null) 
            enemysMissiles.clear();
        else
            this.enemysMissiles = new Array<ObjectSprite>();
        if (playersMissiles!=null) 
            playersMissiles.clear(); 
        else
            this.playersMissiles = new Array<ObjectSprite>();
        
        this.lastDropTime = 0;          /// Время последнего выпадения врага
        this.lastShootTime = 0;
        this.spacePushedTime = 0;
        this.enemiesDestroyed = 0;          /// Количество сбитых врагов   
        this.lastBonusTime = 0;
        
        // Создаем модель
        system = new GameSystem();

        // Загружаем изображение геройского корабля и задаем его размеры
        playersView = system.getPlayer().getView();
        playersView.rect.y = 450 / 2 - playersView.rect.height / 2;
        playersView.rect.x = 20;    
        
        respawnTime = 2200;
        
        // Создаем первый корабль
        spawnEnemy();

        // Загрузка звукового эффекта падающей капли и фоновой "музыки" дождя 
        //dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        //rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

        // Сразу же воспроизводиться музыка для фона
        //rainMusic.setLooping(true);
        //rainMusic.play();

        // Выделяем память под batch
        batch = new SpriteBatch();
        state = PLAY;
        lastState = PLAY;
    }
    
    private void showBonus(){
        // Задаем ему начальную позицию
        ObjectSprite bonus = system.createHealthBouns();
        if (bonus!=null){
            bonus.rect.x = 800;//
            bonus.rect.y = MathUtils.random(0, 450-bonus.rect.height - 15);//480;
            // Добавляем его в массив
            bonuses.add(bonus);
            lastBonusTime = TimeUtils.nanosToMillis(TimeUtils.nanoTime());
        }
    }

    @Override
    public void dispose () {
        img.dispose();
        batch.dispose();
        gameFont.dispose();
        infoFont.dispose();
        enemies.clear();
        enemysMissiles.clear();
        playersMissiles.clear();  
    }
    
    private void spawnEnemy() {
        // Создаем нового врага
        ObjectSprite newEnemy = system.generateEnemy(this.enemiesDestroyed);
        // Задаем ему начальную позицию
        newEnemy.rect.x = 800;//
        newEnemy.rect.y = MathUtils.random(0, 450-newEnemy.rect.height - 15);//480;
        // Добавляем его в массив
        enemies.add(newEnemy);
        // Изменяем время "выпада" врага
        lastDropTime = TimeUtils.nanosToMillis(TimeUtils.nanoTime());
        if (newEnemy.getObjType()!=ENMSHIPHEALTHY){
            ObjectSprite newMissile = system.makeShoot(newEnemy.getObjType(), newEnemy.getId());
            newMissile.rect.x = 800 - newEnemy.rect.width;
            newMissile.rect.y = newEnemy.rect.y + newMissile.rect.height;
            enemysMissiles.add(newMissile);
        }
    }
    
    private void shoot() {
        ObjectSprite newMissile = system.makeShoot(playersView.getObjType(), playersView.getId());
        newMissile.rect.x = playersView.rect.x + playersView.rect.width/2;
        newMissile.rect.y = playersView.rect.y + newMissile.rect.height;
        this.playersMissiles.add(newMissile);
        lastShootTime = TimeUtils.nanosToMillis(TimeUtils.nanoTime());
    }
    
    private void controlPlayerPosition(){
        // Если при сдвиге корабль вылетел за пределы поля - возвращаем его в систему координат
        if(playersView.rect.y < 0)
            playersView.rect.y = 0;
        // Отнимаем от высоты 15 - чтобы не залезать на строку с счетом
        if(playersView.rect.y > 450 - playersView.rect.height - 15) 
            playersView.rect.y = 450 - playersView.rect.height - 15;
        
        // Если при сдвиге корабль вылетел за пределы поля - возвращаем его в систему координат
        if(playersView.rect.x < 0)
            playersView.rect.x = 0;
        // Не даем кораблю выйти более чем на 10% от ширины игрового поля
        if(playersView.rect.x > 800*0.10) 
            playersView.rect.x = (float) (800*0.10);
    }
    
    // Может пересечься либо с пользовательским кораблем, либо с пользовательским снарядом
    private void controlEnemiesSprites() {
        // Задаем итератор для массива врагов
        Iterator<ObjectSprite> iter = enemies.iterator();
        // Для каждого врага..
        while(iter.hasNext()) {
            ObjectSprite curEnemy = iter.next();
            // Направляем его на "левый вылет"
            curEnemy.rect.x -= system.getActiveEnemy(curEnemy.getObjType(), curEnemy.getId()).getSpeed() * Gdx.graphics.getDeltaTime();
            // Если корабль вылетел за пределы поля - удаляем из массива
            if(curEnemy.rect.x + curEnemy.rect.width < 0) {
                system.objectLeftField(curEnemy.getObjType(), curEnemy.getId());
                iter.remove();
                continue;
            }                
            // Если корабль столкнулся с кораблем героя - игра закончена
            if(curEnemy.rect.overlaps(playersView.rect)) {
                //dropSound.play();
                // TODO изменить с учетом поиска информации о том как завершить игровой процесс
                system.objectLeftField(curEnemy.getObjType(), curEnemy.getId());
                iter.remove();
                System.out.println("GAME IS OVER");//this.enemies.removeValue(enemy, true);
                this.state = GAMEOVER;
                //continue;
            }
            /*for(ObjectSprite playerM: this.playersMissiles) {
                if(curEnemy.rect.overlaps(playerM.rect)) {
                    //dropSound.play();
                    if (system.isKilledShip(curEnemy.getObjType(), curEnemy.getId(), playerM.getObjType(), playerM.getId())){
                        iter.remove();
                        this.playersMissiles.removeValue(playerM, true);
                        this.enemiesDestroyed+=1;
                        break;
                    }                                
                }
            }*/
        }
    }
    
    // Может пересечься либо с пользовательским кораблем, либо с пользовательским снарядом
    private void controlEnemiesMissilesPosition() {
        // Задаем итератор для массива снарядов
        Iterator<ObjectSprite> iterM = this.enemysMissiles.iterator();
        // Для каждого снаряда..
        while(iterM.hasNext()) {
            ObjectSprite curM = iterM.next();
            // Направляем его на "левый вылет"
            curM.rect = system.getActiveMissile(curM.getObjType(), curM.getId()).getTrajectory().calculatePosition(curM.rect, true, Gdx.graphics.getDeltaTime());
            // Если корабль вылетел за пределы поля - удаляем из массива
            if(curM.rect.x + curM.rect.width < 0) {
                system.objectLeftField(curM.getObjType(), curM.getId());
                iterM.remove();
                continue;
            }
            // Если снаряд столкнулся с кораблем героя
            if(curM.rect.overlaps(playersView.rect)) {
                //dropSound.play();
                //dropsGathered++;
                iterM.remove();
                if(system.isKilledShip(playersView.getObjType(), playersView.getId(), curM.getObjType(), curM.getId())){
                    System.out.println("GAME IS OVER");//this.enemies.removeValue(enemy, true);
                    this.state = GAMEOVER;
                }
                continue;
            }
            for(ObjectSprite playerM: this.playersMissiles) {
                if(curM.rect.overlaps(playerM.rect)) {
                    //dropSound.play();
                    system.missilesCollision(curM.getObjType(), curM.getId(), playerM.getObjType(), playerM.getId());
                    iterM.remove();
                    this.playersMissiles.removeValue(playerM, true);
                    break;
                }
            }
        }
    }
    
    // Может пересечься либо с вражеским кораблем, либо с вражеским снарядом
    private void controlPlayerMissilesPosition() {
        Iterator<ObjectSprite> iterPM = this.playersMissiles.iterator();
        // Для каждого снаряда..
        while(iterPM.hasNext()) {
            boolean wasBreak = false;
            ObjectSprite curM = iterPM.next();
            // Направляем его на "правый вылет"
            curM.rect = system.getPlayer().getMissile().getTrajectory().calculatePosition(curM.rect, false, Gdx.graphics.getDeltaTime());
            // Если снаряд вылетел за пределы поля - удаляем из массива
            if(curM.rect.x + curM.rect.width >800){
                system.objectLeftField(curM.getObjType(), curM.getId());
                iterPM.remove();
                curM = null;
                continue;
            }
            // Если снаряд столкнулся с врагом
            for(ObjectSprite enemy: this.enemies) {
                if(curM.rect.overlaps(enemy.rect)) {
                    //dropSound.play();
                    if(system.isKilledShip(enemy.getObjType(), enemy.getId(), curM.getObjType(), curM.getId())){
                        this.enemies.removeValue(enemy, true);
                        enemiesDestroyed++;
                        controlRespawnSpeed();
                    }
                    iterPM.remove();
                    wasBreak = true;
                    break;
                }
            }
            // Если снаряд не столкнулся с врагом, а столкнулся с другим снарядом
            /*if(!wasBreak){
                for(ObjectSprite enemyM: this.enemysMissiles) {
                    if(curM.rect.overlaps(enemyM.rect)) {
                        //dropSound.play();
                        system.missilesCollision(curM.getObjType(), curM.getId(), enemyM.getObjType(), enemyM.getId());
                        iterPM.remove();
                        this.enemysMissiles.removeValue(enemyM, true);
                        break;
                    }
                }   
            }
            */
        }
    }
    
    // Может пересечься с кораблем пользователя
    private void controlBonusesPosition() {
        Iterator<ObjectSprite> iterB = this.bonuses.iterator();
        // Для каждого бонуса..
        while(iterB.hasNext()) {
            ObjectSprite curB = iterB.next();
            // Направляем его на "левый вылет"
            curB.rect = system.getBonusWithId(curB.getId()).getTrajectory().calculatePosition(curB.rect, true, Gdx.graphics.getDeltaTime());
            // Если бонус вылетел за пределы поля - удаляем из массива
            if(curB.rect.x + curB.rect.width < 0){
                system.objectLeftField(curB.getObjType(), curB.getId());
                iterB.remove();
                curB = null;
                continue;
            }
            // Если бонус столкнулся с игроком
            if(curB.rect.overlaps(this.playersView.rect)) {
                system.caughtBonus(curB.getId());
                iterB.remove();
                curB = null;
                break;
            }
        }
    }
    
    private void controlRespawnSpeed(){
        if (enemiesDestroyed>10) {
            respawnTime=1600;
        }
        else if (enemiesDestroyed>25) {
            respawnTime=1000;
        }
        else if (enemiesDestroyed>50) {
            respawnTime=500;
        }
    }
}
