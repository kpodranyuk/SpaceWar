/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.spacewar;

/**
 * Отображение игрового объекта
 * @author Katie
 */
public class ObjectSprite {
    private ObjectImage view;   /// Изображение спрайта
    private int id;
    
    /**
     * Конструктор спрайта объекта
     * @param view Отображение объекта
     * @param id Идентификатор объекта
     */
    public ObjectSprite(ObjectImage view, int id){
        if (view == null)
            throw new Error("Can't create sprite of object");
        this.view = view;
        this.id = id;
    }
    
    /**
     * Конструктор копирования
     * @param other Другое отображение
     * @param newId Id для создаваемого объекта
     */
    public ObjectSprite(ObjectSprite other, int newId){
        if (other == null)
            throw new Error("Can't create copy of null ObjectSprite");
        if (newId < 0)
            throw new Error("Can't create object with negative id");
        this.view = other.getImage();
        this.id=newId;
    }
    
    /**
     * Получить отображение объекта
     * @return Отображение объекта
     */
    public ObjectImage getImage(){
        return this.view;
    }
    
    /**
     * Получить Id объекта
     * @return Id объекта
     */
    public int getId(){
        return this.id;
    }
}
