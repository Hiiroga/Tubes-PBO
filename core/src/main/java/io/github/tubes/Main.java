package io.github.tubes;

import com.badlogic.gdx.Game;

public class Main extends Game {
    @Override
    public void create() {
        setScreen(new HomeScreen(this));
    }
}
