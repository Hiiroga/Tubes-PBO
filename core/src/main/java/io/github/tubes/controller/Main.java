package io.github.tubes.controller;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import io.github.tubes.model.GameData;
import io.github.tubes.view.HomeScreen;

public class Main extends Game {

    public static final float VIRTUAL_WIDTH = 800;
    public static final float VIRTUAL_HEIGHT = 480;
    public TextureAtlas atlas;

    public Music lobbyMusic;
    public Music battleMusic;

    public Sound pressSound;
    public Sound backSound;
    public Sound potionEffectSound;
    public Sound buySound;
    public Sound punchSound;
    public Sound defendSound;

    @Override
    public void create() {

        lobbyMusic = Gdx.audio.newMusic(Gdx.files.internal("lobby.mp3"));
        lobbyMusic.setLooping(true);
        lobbyMusic.setVolume(0.5f);

        battleMusic = Gdx.audio.newMusic(Gdx.files.internal("battle.mp3"));
        battleMusic.setLooping(true);
        battleMusic.setVolume(0.5f);

        pressSound = Gdx.audio.newSound(Gdx.files.internal("button.mp3"));
        backSound = Gdx.audio.newSound(Gdx.files.internal("back.wav"));
        potionEffectSound = Gdx.audio.newSound(Gdx.files.internal("potion_effect.wav"));
        buySound = Gdx.audio.newSound(Gdx.files.internal("buy.wav"));
        punchSound = Gdx.audio.newSound(Gdx.files.internal("sword.mp3"));
        defendSound = Gdx.audio.newSound(Gdx.files.internal("defend.mp3"));

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

        pressSound.dispose();
        backSound.dispose();
        potionEffectSound.dispose();
        buySound.dispose();
        punchSound.dispose();
        defendSound.dispose();
    }
}
