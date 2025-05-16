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

public class StageChoice implements Screen {
    private final Main game;
    private final Stage stage;
    private final Skin skin;

    public StageChoice(Main game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));

        Texture bg = new Texture("bghome.png");
        Image bgImage = new Image(bg);
        bgImage.setFillParent(true);
        stage.addActor(bgImage);

        // Title
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default-font");
        Label title = new Label("PILIH BABAK", labelStyle);
        title.setFontScale(2.2f);
        title.setSize(title.getPrefWidth(), title.getPrefHeight());
        title.setPosition(
            Gdx.graphics.getWidth() / 2f - title.getWidth() / 2f,
            Gdx.graphics.getHeight() * 0.75f
        );
        stage.addActor(title);

        float buttonWidth = 100;
        float buttonHeight = 30;
        float spacing = 20;
        float startY = Gdx.graphics.getHeight() * 0.55f;

        // Stage Buttons 1–5
        for (int i = 1; i <= 5; i++) {
            TextButton stageButton = new TextButton("STAGE " + i, skin);
            stageButton.setSize(buttonWidth, buttonHeight);
            stageButton.setPosition(
                Gdx.graphics.getWidth() / 2f - buttonWidth / 2f,
                startY - (i - 1) * (buttonHeight + spacing)
            );

            final int selectedStage = i;
            stageButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // Replace with your actual gameplay screen for each stage
                    System.out.println("Stage " + selectedStage + " dipilih.");
                    // Contoh: game.setScreen(new GameScreen(game, selectedStage));
                }
            });
            stage.addActor(stageButton);
        }

        // Back button
        TextButton backButton = new TextButton("KEMBALI", skin);
        backButton.setSize(160, 50);
        backButton.setPosition(20, 20);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(backButton);
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
