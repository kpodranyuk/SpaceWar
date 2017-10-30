/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.spacewar;

import com.badlogic.gdx.math.Rectangle;

/**
 * Дуговая траектория
 * @author Katie
 */
public class ArcTrajectory extends Trajectory {

    /**
     * Конструктор дуговой траектории
     * @param speed Скорость движения
     * @param leftDirected Является ли траектория направленной влево
     */
    public ArcTrajectory(float speed, boolean leftDirected) {
        super(speed, leftDirected);
    }

    /**
     * Вычислить новую позицию согласно траектории
     * @param cuPos Текущая позиция
     * @return Новая позиция
     */
    @Override
    public Rectangle calculatePosition(Rectangle cuPos, boolean toLeft, float deltaTime) {
        float deltaX = cuPos.x;
        if(toLeft)
            cuPos.x -= this.getSpeed() * deltaTime;
        else
            cuPos.x += this.getSpeed() * deltaTime;
        deltaX=Math.abs(800-cuPos.x);
        cuPos.y -= (float) (deltaX/6) * deltaTime;//this.getSpeed() * deltaTime;
        return cuPos;
    }
    
}
