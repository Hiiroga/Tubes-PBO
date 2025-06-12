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
        rootTable.pad(15);
        stage.addActor(rootTable);

        // DIUBAH: Info Gold sekarang sendirian di atas
        Table topBar = new Table();
        Label goldLabel = new Label("Gold: " + GameData.getGold(), skin);
        topBar.add(goldLabel).left().expandX();

        Label title = new Label("SELECT STAGE", skin);
        title.setFontScale(2.5f);

        Table bottomButtons = new Table();
        TextButton backButton = new TextButton("BACK", skin);
        backButton.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) { game.setScreen(new MainMenuScreen(game)); }
        });

        TextButton storeButton = new TextButton("STORE", skin);
        storeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new StoreScreen(game));
            }
        });

        bottomButtons.add(backButton).width(160).height(50).left();
        bottomButtons.add().expandX(); // Spacer
        bottomButtons.add(storeButton).width(160).height(50).right();
        // ----------------------------------------------------

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
                stageButton = new TextButton("[ LOCKED ]", skin);
                stageButton.setDisabled(true);
            }
            stageTable.add(stageButton).width(250).height(50).pad(5);
            stageTable.row();
        }

        // Susun ulang tata letak utama
        rootTable.add(topBar).expandX().fillX().top();
        rootTable.row();
        rootTable.add(title).padTop(20).padBottom(20);
        rootTable.row();
        rootTable.add(stageTable).expand();
        rootTable.row();
        rootTable.add(bottomButtons).expandX().fillX().bottom();
    }

    @Override
    public void show() {
        game.playLobbyMusic();
    }

    @Override public void render(float delta) { Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.act(delta); stage.draw(); }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); skin.dispose(); }
}
