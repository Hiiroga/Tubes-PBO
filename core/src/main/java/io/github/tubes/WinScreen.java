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

public class WinScreen implements Screen {
    private final Stage stage;

    public WinScreen(final Main game, final int completedStage, int expGained, int goldGained) {
        stage = new Stage(new FitViewport(Main.VIRTUAL_WIDTH, Main.VIRTUAL_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

        GameData.unlockNextLevel(completedStage);

        Image bgImage = new Image(new Texture("bghome.png"));
        bgImage.setFillParent(true);
        stage.addActor(bgImage);

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        Label winLabel = new Label("KAMU MENANG!", skin);
        winLabel.setFontScale(3.0f);

        Label expLabel = new Label("EXP Diperoleh: " + expGained, skin);
        expLabel.setFontScale(1.5f);
        Label goldLabel = new Label("Gold Diperoleh: " + goldGained, skin);
        goldLabel.setFontScale(1.5f);

        TextButton continueButton = new TextButton("LANJUTKAN", skin);
        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new StageChoice(game));
            }
        });

        table.add(winLabel).padBottom(40);
        table.row();
        table.add(expLabel).padBottom(10);
        table.row();
        table.add(goldLabel).padBottom(30);
        table.row();
        table.add(continueButton).width(250).height(60);
    }

    @Override public void show() {}
    @Override public void render(float delta) { Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f)); stage.draw(); }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); }
}
