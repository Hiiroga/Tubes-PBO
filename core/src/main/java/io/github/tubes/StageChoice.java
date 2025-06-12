package io.github.tubes;

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
        stage.addActor(rootTable);

        Label title = new Label("PILIH BABAK", skin);
        title.setFontScale(2.5f);

        TextButton backButton = new TextButton("KEMBALI", skin);
        backButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { game.setScreen(new MainMenuScreen(game)); }
        });

        rootTable.add(title).expandX().top().padTop(50);
        rootTable.row();

        Table stageTable = new Table();
        int maxLevelUnlocked = GameData.getMaxLevelUnlocked();
        for (int i = 1; i <= TOTAL_STAGES; i++) {
            final int currentStage = i;
            TextButton stageButton;
            if (currentStage <= maxLevelUnlocked) {
                stageButton = new TextButton("STAGE " + currentStage, skin);
                stageButton.addListener(new ClickListener() {
                    @Override public void clicked(InputEvent event, float x, float y) { game.setScreen(new GameScreen(game, currentStage)); }
                });
            } else {
                stageButton = new TextButton("[ TERKUNCI ]", skin);
                stageButton.setDisabled(true);
            }
            stageTable.add(stageButton).width(250).height(50).pad(5);
            stageTable.row();
        }

        rootTable.add(stageTable).expand();
        rootTable.row();
        rootTable.add(backButton).width(160).height(50).bottom().left().pad(20);
    }

    @Override public void show() {}
    @Override public void render(float delta) { Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.act(delta); stage.draw(); }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); skin.dispose(); }
}
