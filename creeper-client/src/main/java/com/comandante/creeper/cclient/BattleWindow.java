package com.comandante.creeper.cclient;

import javax.swing.JFrame;
import java.awt.HeadlessException;

public class BattleWindow extends JFrame {

    private final BattlePanel battlePanel;

    public BattleWindow(BattlePanel battlePanel)  {
        this.battlePanel = battlePanel;
        setTitle("Battle");
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        add(battlePanel);
    }
}
