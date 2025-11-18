package network;

/*
 * @Author: NgojcDev
 */

import network.KeyHandler;
import data.DataGame;
import interfaces.ISession;

public class MyKeyHandler extends KeyHandler {

    @Override
    public void sendKey(ISession session) {
        super.sendKey(session);
        DataGame.sendDataImageVersion((MySession) session);
        DataGame.sendVersionRes((MySession) session);
    }

}
