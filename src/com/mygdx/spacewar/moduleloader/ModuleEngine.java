/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.spacewar.moduleloader;

import com.mygdx.spacewar.bot.Bot;
import com.mygdx.spacewar.api.GameSystemAPI;
import java.io.File;

/**
 *
 * @author Katie
 */
public class ModuleEngine {
    
    private static Bot bot = null;
    
    public void loadBot(String modulePath, GameSystemAPI api) {
        
        ModuleLoader loader = new ModuleLoader(modulePath, ClassLoader.getSystemClassLoader());
        
        /**
         * Получаем список доступных модулей.
         */
        File dir = new File(modulePath);
        String[] modules = dir.list();
        if (modules == null) {
            System.out.println("Module path does not denote a folder");
            return;
        }
        /**
         * Загружаем и исполняем бота.
         */
        for (String module : modules) {
            if (module.endsWith(".class")) {
                try {
                    String moduleName = module.split(".class")[0];
                    if (moduleName.equals("BotAPI") == false) {
                        System.out.print("Executing loading module: ");
                        System.out.println(moduleName);

                        Class clazz = loader.loadClass("com.mygdx.spacewar.bot." + moduleName);
                        bot = (Bot) clazz.newInstance();
                        // bot.run(api);
                        System.out.println("Bot loaded");
                    }

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
}
