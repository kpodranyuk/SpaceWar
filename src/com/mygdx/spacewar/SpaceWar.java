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
import static com.mygdx.spacewar.ObjectSprite.ObjectType.ENMSHIP;
import java.util.Iterator;

/**
 * Панель игры
 * @author Katie
 */
public class SpaceWar extends ApplicationAdapter {
    private GameSystem system;
    
    private OrthographicCamera camera;  /// Камера
    private SpriteBatch batch;          /// Batch

    private Texture img;                /// Изображение фона
    private TextureRegion back;         /// Фон

    private ObjectSprite playersView;   /// Отображение игрока
    //private Rectangle heroShipRect;     /// Прямоугольник героя
    //private Texture heroShip;           /// Изображение героя

    //private Texture enemyShip;          /// Изображение врага        
    private Array<ObjectSprite> enemies;   /// Массив врагов
    private Array<ObjectSprite> enemysMissiles;   /// Массив врагов
    private Array<ObjectSprite> playersMissiles;  /// Массив снарядов героя
    
    private static final long respawnTime = 2200;
    private long lastDropTime;          /// Время последнего выпадения врага
    
    private static final long shootDeltaTime = 220;
    private long lastShootTime;

    private int dropsGathered;          /// Количество сбитых врагов
    private BitmapFont font;            /// Шрифт

    @Override
    public void create () {
        // Создаем камеру
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 450);

        // Настраиваем бэкграунд
        img = new Texture("background.png");
        back = new TextureRegion(img, 0, 0, 1366, 768);
        // Создаем шрифт и задаем ему цвет
        font = new BitmapFont();
        font.setColor(Color.WHITE);

        // Создаем модель
        system = new GameSystem();

        // Загружаем изображение геройского корабля и задаем его размеры
        playersView = system.getPlayer().getView();
        playersView.rect.y = 450 / 2 - playersView.rect.height / 2;
        playersView.rect.x = 20;
        /*heroShip = new Texture(Gdx.files.internal("ship.png"));
        heroShipRect = new Rectangle();
        heroShipRect.width = 184;
        heroShipRect.height = 200;
        heroShipRect.y = 450 / 2 - heroShipRect.height / 2;
        heroShipRect.x = 20;*/  
        
        
        // Задаем изображение вражеского корабля
        this.enemies = new Array<ObjectSprite>();
        this.enemysMissiles = new Array<ObjectSprite>();
        this.playersMissiles = new Array<ObjectSprite>();
        //enemies.add(system.getShip(ObjectSprite.ObjectType.ENMSHIP).getView());
        //enemyShip = new Texture(Gdx.files.internal("enemies/enemy1.png"));
        // Выделяем память под массив вражеских кораблей
        //enemies = new Array<Rectangle>();
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
    }

    @Override
    public void render () {
        // Задаем цвет очистки
        Gdx.gl.glClearColor(1, 0, 0, 1);
        // Очищаем экран
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Обновляем камеру
        camera.update();

        // Сообщаем SpriteBatch использовать систему координат камеры. (матрицу проекции)
        // SpriteBatch нарисует все, что будет находиться в заданных координатах.
        batch.setProjectionMatrix(camera.combined);
        // Начинаем сессию 
        batch.begin();
        // Отрисовываем фон
        batch.draw(back, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        // Отрисовываем количество сбитых кораблей
        font.draw(batch, "Enemies killed: " + dropsGathered, 0, 450);
        // Отрисовываем корабль героя
        batch.draw(playersView.getTexture(), playersView.rect.x, playersView.rect.y);
        // Отрисовываем все имеющиеся корабли врага
        for(ObjectSprite enemy: enemies) {
            batch.draw(enemy.getTexture(), enemy.rect.x, enemy.rect.y);
        }
        
        for(ObjectSprite enemyMissile: this.enemysMissiles) {
            batch.draw(enemyMissile.getTexture(), enemyMissile.rect.x, enemyMissile.rect.y);
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
        if(Gdx.input.isKeyPressed(Keys.SPACE))
            this.pause();

        // Если при сдвиге корабль вылетел за пределы поля - возвращаем его в систему координат
        if(playersView.rect.y < 0)
            playersView.rect.y = 0;
        // Отнимаем от высоты 15 - чтобы не залезать на строку с счетом
        if(playersView.rect.y > 450 - playersView.rect.height - 15) 
            playersView.rect.y = 450 - playersView.rect.height - 15;
        
        // Если при сдвиге корабль вылетел за пределы поля - возвращаем его в систему координат
        if(playersView.rect.x < 0)
            playersView.rect.x = 0;
        // Отнимаем от высоты 15 - чтобы не залезать на строку с счетом
        if(playersView.rect.x > 800*0.10) 
            playersView.rect.x = (float) (800*0.10);

        // Если пришло время - респавним новые корабли
        long timePassed = TimeUtils.nanosToMillis(TimeUtils.nanoTime()) - lastDropTime;
        if(timePassed > respawnTime)
            spawnEnemy();

        // Задаем итератор для массива врагов
        Iterator<ObjectSprite> iter = enemies.iterator();
        // Для каждого врага..
        while(iter.hasNext()) {
            ObjectSprite curEnemy = iter.next();
            // Направляем его на "левый вылет"
            curEnemy.rect.x -= system.getShip(curEnemy.getObjType()).getSpeed() * Gdx.graphics.getDeltaTime();
            // Если корабль вылетел за пределы поля - удаляем из массива
            if(curEnemy.rect.x + curEnemy.rect.width < 0) 
                iter.remove();
            // Если корабль столкнулся с кораблем героя - добавляем очки и удаляем вражеский из массива
            if(curEnemy.rect.overlaps(playersView.rect)) {
                //dropSound.play();
                dropsGathered++;
                iter.remove();
             }
        }
        
        
        // Задаем итератор для массива врагов
        Iterator<ObjectSprite> iterM = this.enemysMissiles.iterator();
        // Для каждого врага..
        while(iterM.hasNext()) {
            ObjectSprite curM = iterM.next();
            // Направляем его на "левый вылет"
            curM.rect.x -= system.getShip(ENMSHIP).getSpeed() * Gdx.graphics.getDeltaTime();
            // Если корабль вылетел за пределы поля - удаляем из массива
            if(curM.rect.x + curM.rect.width < 0) 
                iterM.remove();
            // Если корабль столкнулся с кораблем героя - добавляем очки и удаляем вражеский из массива
            if(curM.rect.overlaps(playersView.rect)) {
                //dropSound.play();
                dropsGathered++;
                iterM.remove();
             }
        }
        
        Iterator<ObjectSprite> iterPM = this.playersMissiles.iterator();
        // Для каждого врага..
        while(iterPM.hasNext()) {
            boolean wasBreak = false;
            ObjectSprite curM = iterPM.next();
            // Направляем его на "левый вылет"
            curM.rect.x += system.getPlayer().getMissile().getSpeed() * Gdx.graphics.getDeltaTime();
            // Если корабль вылетел за пределы поля - удаляем из массива
            if(curM.rect.x + curM.rect.width >800){
                iterPM.remove();
                wasBreak = true;
            }
            if(!wasBreak){
                for(ObjectSprite enemy: this.enemies) {
                    if(curM.rect.overlaps(enemy.rect)) {
                        //dropSound.play();
                        dropsGathered++;
                        iterPM.remove();
                        this.enemies.removeValue(enemy, true);
                        wasBreak = true;
                        break;
                    }
                }
            }
            if(!wasBreak){
                for(ObjectSprite enemyM: this.enemysMissiles) {
                    if(curM.rect.overlaps(enemyM.rect)) {
                        //dropSound.play();
                        //dropsGathered++;
                        iterPM.remove();
                        this.enemysMissiles.removeValue(enemyM, true);
                        break;
                    }
                }   
            }
            
        }
    }

    @Override
    public void dispose () {
        img.dispose();
        //heroShip.dispose();
        //enemyShip.dispose();
        batch.dispose();
        font.dispose();
        enemies.clear();
        enemysMissiles.clear();
        playersMissiles.clear();  
    }
    
    private void spawnEnemy() {
        // Создаем нового врага
        ObjectSprite newEnemy = system.generateEnemy();
        //Rectangle enemy = new Rectangle();
        // Задаем ему размеры
        //enemy.width = 66;
        //enemy.height = 93;
        // Задаем ему начальную позицию
        newEnemy.rect.x = 800;//
        newEnemy.rect.y = MathUtils.random(0, 450-newEnemy.rect.height - 15);//480;
        // Добавляем его в массив
        enemies.add(newEnemy);
        // Изменяем время "выпада" врага
        lastDropTime = TimeUtils.nanosToMillis(TimeUtils.nanoTime());
        
        ObjectSprite newMissile = system.getEnemiesMissile(newEnemy.getObjType());
        newMissile.rect.x = 800 - newEnemy.rect.width;
        newMissile.rect.y = newEnemy.rect.y + newEnemy.rect.height / 2;
        enemysMissiles.add(newMissile);
    }
    
    private void shoot() {
        ObjectSprite newMissile = system.getPlayer().getMissile().getView();
        newMissile.rect.x = playersView.rect.x + playersView.rect.width/2;
        newMissile.rect.y = playersView.rect.y;
        this.playersMissiles.add(newMissile);
        lastShootTime = TimeUtils.nanosToMillis(TimeUtils.nanoTime());
    }
}
