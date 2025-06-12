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

public class LoseScreen implements Screen {
    private final Stage stage;

    public LoseScreen(final Main game) {
        stage = new Stage(new FitViewport(Main.VIRTUAL_WIDTH, Main.VIRTUAL_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

        Image bgImage = new Image(new Texture("bghome.png"));
        bgImage.setFillParent(true);
        stage.addActor(bgImage);

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        Label loseLabel = new Label("KAMU KALAH", skin);
        loseLabel.setFontScale(3.0f);

        Label infoLabel = new Label("Coba lagi lain kali!", skin);
        infoLabel.setFontScale(1.5f);

        TextButton backButton = new TextButton("KEMBALI KE MENU", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        table.add(loseLabel).padBottom(20);
        table.row();
        table.add(infoLabel).padBottom(40);
        table.row();
        table.add(backButton).width(300).height(60);
    }

    @Override public void show() {}
    @Override public void render(float delta) { Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f)); stage.draw(); }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { stage.dispose(); }
}
