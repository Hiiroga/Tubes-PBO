package io.github.tubes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {
    private final Main game;
    private final Stage stage;
    private final Skin skin;

    public MainMenuScreen(Main game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));

        Texture bg = new Texture("bghome.png");
        Image bgImage = new Image(bg);
        bgImage.setFillParent(true);
        stage.addActor(bgImage);


        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default-font");
        Label title = new Label("HEROES RISING", labelStyle);
        title.setFontScale(2.5f);

        title.setSize(title.getPrefWidth(), title.getPrefHeight());
        title.setPosition(
            Gdx.graphics.getWidth() / 2f - title.getWidth() / 2f,
            Gdx.graphics.getHeight() * 0.75f
        );
        stage.addActor(title);

        float buttonWidth = 200;
        float buttonHeight = 60;
        float yPos = Gdx.graphics.getHeight() * 0.5f;

        TextButton playButton = new TextButton("PERMAINAN", skin);
        playButton.setSize(buttonWidth, buttonHeight);
        playButton.setPosition(Gdx.graphics.getWidth() * 0.20f - buttonWidth / 2f, yPos);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new StageChoice(game));
            }
        });
        stage.addActor(playButton);

        TextButton settingsButton = new TextButton("PENGATURAN", skin);
        settingsButton.setSize(buttonWidth, buttonHeight);
        settingsButton.setPosition(Gdx.graphics.getWidth() * 0.50f - buttonWidth / 2f, yPos);
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SettingsScreen(game));
            }
        });
        stage.addActor(settingsButton);

        TextButton exitButton = new TextButton("KELUAR", skin);
        exitButton.setSize(buttonWidth, buttonHeight);
        exitButton.setPosition(Gdx.graphics.getWidth() * 0.80f - buttonWidth / 2f, yPos);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        stage.addActor(exitButton);
    }

    @Override public void show() {}
    @Override public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }
    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
