/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.spacewar;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

/**
 * Отображение игрового объекта
 * @author Katie
 */
public class ObjectSprite {
    private Texture texture;    /// Текстура 
    public Rectangle rect;     /// Прямоугольник отображения

    /**
     * Возможные типы объекта
     */
    public enum ObjectType { MISSILE, USRSHIP, ENMSHIP, ENMSHIPHEALTHY }
    private ObjectType type;    /// Тип объекта
    
    /**
     * Конструктор отображения объекта
     * @param img Путь к файлу с изображением объекта
     * @param width Ширина объекта на игровом поле
     * @param height Высота объекта на игровом поле
     * @param type Тип объекта
     */
    public ObjectSprite(String img, int width, int height, ObjectType type){
        if (img == null || img.equals("") || width <= 0 || height <= 0 || type == null)
            throw new Error("Can't create view of object");
        this.texture = new Texture(img);
        this.rect = new Rectangle();
        this.rect.width = width;
        this.rect.height = height;
        
        this.rect.x = 0;
        this.rect.y = 0;
        
        this.type = type;
    }
    
    /**
     * Конструктор копирования
     * @param other Другое отображение
     */
    public ObjectSprite(ObjectSprite other){
        if (other == null)
            throw new Error("Can't create copy of null ObjectSprite");
        this.rect = other.getRectangle();
        this.texture = other.getTexture();
        this.type = other.getObjType();
    }
    
    /**
     * Получить текстуру объекта
     * @return Текустура объекта
     */
    public Texture getTexture(){
        return new Texture (this.texture.getTextureData());
    }

    /**
     * Получить форму объекта
     * @return Форма объекта
     */
    public Rectangle getRectangle(){
        return new Rectangle (this.rect);
    }

    /**
     * Получить тип объекта
     * @return Тип объекта
     */
    public ObjectType getObjType(){
        return this.type;
    }
}
