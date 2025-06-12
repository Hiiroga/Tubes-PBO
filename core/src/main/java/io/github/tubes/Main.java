package io.github.tubes;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Main extends Game {

    public static final float VIRTUAL_WIDTH = 800;
    public static final float VIRTUAL_HEIGHT = 480;

    public TextureAtlas atlas;

    @Override
    public void create() {
        atlas = new TextureAtlas("textures.atlas");

        for (Texture texture : atlas.getTextures()) {
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
        GameData.load();
        this.setScreen(new HomeScreen(this));
    }

}
