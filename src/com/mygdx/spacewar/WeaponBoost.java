/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.spacewar;

import com.badlogic.gdx.utils.Array;

/**
 *
 * @author Katie
 */
public class WeaponBoost extends Bonus{

    private final int activeWeapons;  /// Количество орудий для установления активными
    private int oldShipWeaponsCount;  /// Начальное количество активных орудий на корабле

    public WeaponBoost(BonusType type, ObjectSprite sprite, StraightTrajectory traj) {
        super(type, sprite, traj);
        this.activeWeapons = 3;
    }
    
    @Override
    public void activate(Ship shipToTakeBonus) {
        Missile wps = shipToTakeBonus.getMissile(0);
        
        oldShipWeaponsCount = shipToTakeBonus.getDefaultWeaponsCount();
        shipToTakeBonus.addActiveWeapon(new Weapon(wps));
        shipToTakeBonus.addActiveWeapon(new Weapon(wps));
    }
    
    public void deactivate(Ship shipToTakeBonus) {
        shipToTakeBonus.deleteLastWeapons(activeWeapons-oldShipWeaponsCount);
        //shipToTakeBonus.setActiveWeaponsCount(oldShipWeaponsCount);
    }
}
