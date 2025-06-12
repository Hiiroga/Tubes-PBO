package io.github.tubes;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Main extends Game {

    public static final float VIRTUAL_WIDTH = 800;
    public static final float VIRTUAL_HEIGHT = 480;
    public TextureAtlas atlas;

    public Music lobbyMusic;
    public Music battleMusic;

    @Override
    public void create() {
        atlas = new TextureAtlas("textures.atlas");
        for (Texture texture : atlas.getTextures()) {
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }

        lobbyMusic = Gdx.audio.newMusic(Gdx.files.internal("lobby.mp3"));
        lobbyMusic.setLooping(true);
        lobbyMusic.setVolume(0.2f);

        battleMusic = Gdx.audio.newMusic(Gdx.files.internal("battle.mp3"));
        battleMusic.setLooping(true);
        battleMusic.setVolume(0.1f);

        GameData.load();

        this.setScreen(new HomeScreen(this));
    }

    public void playLobbyMusic() {
        if (battleMusic.isPlaying()) {
            battleMusic.stop();
        }
        if (!lobbyMusic.isPlaying()) {
            lobbyMusic.play();
        }
    }

    public void playBattleMusic() {
        if (lobbyMusic.isPlaying()) {
            lobbyMusic.stop();
        }
        if (!battleMusic.isPlaying()) {
            battleMusic.play();
        }
    }

    @Override
    public void pause() {
        super.pause();
        GameData.save();
    }

    @Override
    public void dispose() {
        super.dispose();
        GameData.save();
        if (atlas != null) {
            atlas.dispose();
        }
        lobbyMusic.dispose();
        battleMusic.dispose();
    }
}
