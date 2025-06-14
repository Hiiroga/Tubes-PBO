package io.github.tubes.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.tubes.model.GameData;
import io.github.tubes.controller.Main;
import io.github.tubes.model.Item;

public class StoreScreen implements Screen {
    private final Stage stage;
    private final Skin skin;
    private final Label goldLabel;
    private final Label feedbackLabel;
    private final Main game;

    public StoreScreen(final Main game) {
        this.game = game;
        stage = new Stage(new FitViewport(Main.VIRTUAL_WIDTH, Main.VIRTUAL_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        Image bgImage = new Image(new Texture("bghome.png"));
        bgImage.setFillParent(true);
        stage.addActor(bgImage);

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.pad(20);
        stage.addActor(rootTable);

        Label titleLabel = new Label("STORE", skin);
        titleLabel.setFontScale(2.5f);
        rootTable.add(titleLabel).expandX().top().padBottom(10);
        rootTable.row();

        goldLabel = new Label("Gold: " + GameData.getGold(), skin);
        rootTable.add(goldLabel).padBottom(10);
        rootTable.row();

        feedbackLabel = new Label("", skin);
        rootTable.add(feedbackLabel).pad(10);
        rootTable.row();

        Table itemList = new Table();
        itemList.defaults().pad(10);

        final Item potion = GameData.getItem("Potion");
        Label potionName = new Label(potion.name, skin);
        Label potionDesc = new Label(potion.description, skin);
        Label potionCost = new Label("Price: " + potion.cost + " G", skin);
        TextButton buyPotionButton = new TextButton("BUY", skin);
        buyPotionButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (GameData.getGold() >= potion.cost) {
                    game.buySound.play(0.9f);
                    GameData.addGold(-potion.cost);
                    GameData.getInventory().addItem(potion.name, 1);
                    updateGold();
                    feedbackLabel.setText("Bought 1 Potion!");
                } else {
                    feedbackLabel.setText("Not enough gold!");
                }
            }
        });

        Table itemEntry = new Table();
        itemEntry.add(potionName).left();
        itemEntry.add(potionCost).expandX().right();
        itemEntry.row();
        itemEntry.add(potionDesc).left().colspan(2).padTop(5);
        itemEntry.row();
        itemEntry.add(buyPotionButton).center().width(100).padTop(10).colspan(2);

        itemList.add(itemEntry);

        rootTable.add(itemList).expand().fill();
        rootTable.row();

        TextButton backButton = new TextButton("BACK", skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.backSound.play(0.3f);
                game.setScreen(new StageChoice(game));
            }
        });
        rootTable.add(backButton).bottom().left();
    }

    private void updateGold() {
        goldLabel.setText("Gold: " + GameData.getGold());
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
