/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package bot;

import author_ngojc.DragonRun;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class BotManager implements Runnable {

    public static BotManager i;
    
    public List<Bot> bot =  new ArrayList<>();
    
    
    public static BotManager gI(){
        if(i == null){
            i = new BotManager();
        }
            return i;
    }
       @Override
    public void run() {
        while (DragonRun.isRunning) {
            try {
                long st = System.currentTimeMillis();
                for (Bot bot : this.bot) {
                    bot.update();
                }
                Thread.sleep(150 - (System.currentTimeMillis() - st));
            } catch (Exception ignored) {
            }

        }
    }
}