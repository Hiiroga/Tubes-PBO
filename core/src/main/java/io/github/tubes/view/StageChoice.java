package io.github.tubes.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.tubes.controller.Main;
import io.github.tubes.model.GameData;

public class StageChoice implements Screen {
    private final Main game;
    private final Stage stage;
    private final Skin skin;
    private final int TOTAL_STAGES = 5;

    public StageChoice(Main game) {
        this.game = game;
        this.stage = new Stage(new FitViewport(Main.VIRTUAL_WIDTH, Main.VIRTUAL_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));

        Image bgImage = new Image(new Texture("bghome.png"));
        bgImage.setFillParent(true);
        stage.addActor(bgImage);

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.pad(15);
        stage.addActor(rootTable);

        Label title = new Label("SELECT STAGE", skin);
        title.setFontScale(2.5f);

        Table bottomButtons = new Table();

        TextButton backButton = new TextButton("BACK", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                event.stop();
                game.backSound.play();
                game.setScreen(new MainMenuScreen(game));
            }
        });

        TextButton storeButton = new TextButton("STORE", skin);
        storeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                event.stop();
                game.pressSound.play();
                game.setScreen(new StoreScreen(game));
            }
        });

        Label goldLabel = new Label("Gold: " + GameData.getGold(), skin);

        Table storeColumn = new Table();
        storeColumn.add(goldLabel).padBottom(5).row();
        storeColumn.add(storeButton).width(160).height(50).right();

        bottomButtons.add(backButton).width(160).height(50).left();
        bottomButtons.add().expandX();
        bottomButtons.add(storeColumn).right();

        Table stageTable = new Table();
        int maxLevelUnlocked = GameData.getMaxLevelUnlocked();
        for (int i = 1; i <= TOTAL_STAGES; i++) {
            final int currentStage = i;
            TextButton stageButton;
            if (currentStage <= maxLevelUnlocked) {
                stageButton = new TextButton("STAGE " + currentStage, skin);
                stageButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        game.pressSound.play();
                        game.setScreen(new GameScreen(game, currentStage));
                    }
                });
            } else {
                stageButton = new TextButton("[ LOCKED ]", skin);
                stageButton.setDisabled(true);
            }
            stageTable.add(stageButton).width(250).height(50).pad(5);
            stageTable.row();
        }

        rootTable.row().padTop(10);
        rootTable.add(title).padBottom(20);
        rootTable.row();
        rootTable.add(stageTable).expand();
        rootTable.row();
        rootTable.add(bottomButtons).expandX().fillX().bottom();
    }

    @Override public void show() { game.playLobbyMusic(); }
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
