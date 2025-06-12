package io.github.tubes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;

public class GameScreen implements Screen {
    private final Main game;
    private final Stage stage;
    private final Skin skin;
    private final TextureAtlas atlas;
    private final int stageLevel;
    private ArrayList<Player> party;
    private ArrayList<Label> partyHpLabels;
    private ArrayList<Image> partyImages;
    private Enemy enemy;
    private Label enemyHpLabel;
    private Label messageLabel;
    private TextButton attackButton;
    private TextButton defendButton;
    private Image enemyImage;
    private int currentPlayerIndex = 0;
    private boolean isPlayerPhase = true;
    private boolean isDefending = false;

    public GameScreen(Main game, int stageLevel) {
        this.game = game;
        this.atlas = game.atlas;
        this.stage = new Stage(new FitViewport(Main.VIRTUAL_WIDTH, Main.VIRTUAL_HEIGHT));
        this.stageLevel = stageLevel;
        Gdx.input.setInputProcessor(stage);
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));

        party = new ArrayList<>();
        party.add(new Player("Knight", 120, 15, 20, 95, 1));
        party.add(new Player("Rogue", 85, 18, 25, 95, 1));
        party.add(new Player("Archer", 80, 20, 30, 95, 1));

        partyHpLabels = new ArrayList<>();
        partyImages = new ArrayList<>();

        this.enemy = new Enemy(
            "Mega Monster Stage " + stageLevel,
            (int) ((100 + (stageLevel * 30)) * 2.5f),
            40 + (stageLevel * 2),
            50 + (stageLevel * 3),
            100,
            stageLevel
        );

        setupUI(stageLevel);
        updateAllHpLabels();
        updateTurnIndicator();
    }

    private void setupUI(int stageLevel) {
        Image bgImage = new Image(new Texture("Gameplay_bg.png"));
        bgImage.setFillParent(true);
        stage.addActor(bgImage);

        Table rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        messageLabel = new Label("Pertarungan dimulai!", skin);
        messageLabel.setWrap(true);
        messageLabel.setAlignment(Align.center);
        attackButton = new TextButton("SERANG", skin);
        defendButton = new TextButton("BERTAHAN", skin);

        Table battleAreaTable = new Table();

        Table heroesTable = new Table();
        heroesTable.defaults().align(Align.left);
        String[] heroImageFiles = {"char1.png", "char2.png", "char3.png"};
        for (int i = 0; i < party.size(); i++) {
            Image pImage = new Image(new Texture(heroImageFiles[i]));
            Label pLabel = new Label("", skin);
            pLabel.setWrap(true);
            partyImages.add(pImage);
            partyHpLabels.add(pLabel);
            Table heroStatusBox = new Table();
            heroStatusBox.add(pImage).size(64, 64).padRight(10);
            heroStatusBox.add(pLabel).width(120);
            if (i == 1) {
                heroesTable.add(heroStatusBox).padLeft(50).padBottom(10);
            } else {
                heroesTable.add(heroStatusBox).padLeft(10).padBottom(10);
            }
            heroesTable.row();
        }

        Table enemyTable = new Table();
        enemyHpLabel = new Label("", skin);
        enemyHpLabel.setAlignment(Align.center);
        Texture enemyTexture = new Texture("enemy1.png");
        TextureRegion enemyRegion = new TextureRegion(enemyTexture);
        enemyRegion.flip(true, false);
        this.enemyImage = new Image(enemyRegion);
        enemyTable.add(this.enemyImage).size(128, 128);
        enemyTable.row();
        enemyTable.add(enemyHpLabel).width(150);

        battleAreaTable.add(heroesTable).expand().align(Align.left);
        battleAreaTable.add(enemyTable).expand().align(Align.right);

        Table actionTable = new Table();
        actionTable.add(attackButton).width(180).height(50).pad(5);
        actionTable.add(defendButton).width(180).height(50).pad(5);


        rootTable.add().expandY();
        rootTable.row();

        rootTable.add(battleAreaTable).expandX().fillX();
        rootTable.row();

        rootTable.add(messageLabel).expandX().fillX().height(60);
        rootTable.row();

        rootTable.add(actionTable).expandX().bottom().padBottom(10);


        attackButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isPlayerPhase) playerTurn(true);
            }
        });
        defendButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isPlayerPhase) playerTurn(false);
            }
        });
    }

    private void playerTurn(boolean isAttacking) {
        if (party.get(currentPlayerIndex).isDead()) {
            nextHeroTurn();
            return;
        }
        isPlayerPhase = false;
        attackButton.setVisible(false);
        defendButton.setVisible(false);
        final Player activeHero = party.get(currentPlayerIndex);
        Image activeHeroImage = partyImages.get(currentPlayerIndex);
        messageLabel.setText(activeHero.getName() + " beraksi!");
        activeHeroImage.addAction(Actions.sequence(
            Actions.moveBy(10, 0, 0.1f),
            Actions.moveBy(-10, 0, 0.1f),
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    if (isAttacking) {
                        int playerDamage = MathUtils.random(activeHero.getMinDamage(), activeHero.getMaxDamage());
                        enemy.takeDamage(playerDamage);
                        messageLabel.setText(activeHero.getName() + " menyerang, " + playerDamage + " kerusakan!");
                    } else {
                        messageLabel.setText(activeHero.getName() + " bersiap!");
                    }
                    updateAllHpLabels();
                    if (enemy.isDead()) {
                        endGame(true);
                    } else {
                        nextHeroTurn();
                    }
                }
            })
        ));
    }

    private void nextHeroTurn() {
        currentPlayerIndex++;
        if (currentPlayerIndex >= party.size()) {
            stage.addAction(Actions.sequence(
                Actions.delay(1.0f),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        enemyTurn();
                    }
                })
            ));
        } else {
            if (party.get(currentPlayerIndex).isDead()) {
                nextHeroTurn();
            } else {
                isPlayerPhase = true;
                attackButton.setVisible(true);
                defendButton.setVisible(true);
                updateTurnIndicator();
                messageLabel.setText("Giliran " + party.get(currentPlayerIndex).getName());
            }
        }
    }

    private void enemyTurn() {
        messageLabel.setText(enemy.getName() + " menyerang...");
        ArrayList<Player> livingHeroes = new ArrayList<>();
        for (Player p : party) {
            if (!p.isDead()) {
                livingHeroes.add(p);
            }
        }
        if (livingHeroes.isEmpty()) return;
        final Player target = livingHeroes.get(MathUtils.random(livingHeroes.size() - 1));
        enemyImage.addAction(Actions.sequence(
            Actions.moveBy(-10, 0, 0.1f),
            Actions.moveBy(10, 0, 0.1f),
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    int enemyDamage = MathUtils.random(enemy.getMinDamage(), enemy.getMaxDamage());
                    target.takeDamage(enemyDamage);
                    messageLabel.setText(enemy.getName() + " menyerang " + target.getName() + ", " + enemyDamage + " kerusakan!");
                    updateAllHpLabels();
                    if (isPartyWiped()) {
                        endGame(false);
                    } else {
                        startPlayerPhase();
                    }
                }
            })
        ));
    }

    private void startPlayerPhase() {
        currentPlayerIndex = -1;
        nextHeroTurn();
    }

    private boolean isPartyWiped() {
        for (Player p : party) {
            if (!p.isDead()) return false;
        }
        return true;
    }

    private void updateAllHpLabels() {
        for (int i = 0; i < party.size(); i++) {
            Player p = party.get(i);
            Label l = partyHpLabels.get(i);
            if (p.isDead()) {
                l.setText(p.getName() + "\n[KALAH]");
            } else {
                l.setText(String.format("%s\nHP: %d/%d", p.getName(), p.getHp(), p.getMaxHp()));
            }
        }
        enemyHpLabel.setText(String.format("Lv. %d %s\nHP: %d / %d", enemy.getLevel(), enemy.getName(), enemy.getHp(), enemy.getMaxHp()));
    }

    private void updateTurnIndicator() {
        for (int i = 0; i < partyImages.size(); i++) {
            if (i == currentPlayerIndex) {
                partyImages.get(i).setColor(1f, 1f, 1f, 1f);
            } else {
                partyImages.get(i).setColor(0.7f, 0.7f, 0.7f, 1f);
            }
        }
    }

    private void endGame(boolean playerWins) {
        isPlayerPhase = false;
        attackButton.setVisible(false);
        defendButton.setVisible(false);
        if (playerWins) {
            final int expGained = enemy.getExpDrop();
            final int goldGained = enemy.getGoldDrop();
            for (Player p : party) {
                p.addExp(expGained);
            }
            party.get(0).addGold(goldGained);
            messageLabel.setText("KAMU MENANG!");
            stage.addAction(Actions.sequence(
                Actions.delay(1.0f),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        game.setScreen(new WinScreen(game, stageLevel, expGained, goldGained));
                    }
                })
            ));
        } else {
            messageLabel.setText("SEMUA HERO KALAH...");
            stage.addAction(Actions.sequence(
                Actions.delay(1.0f),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        game.setScreen(new LoseScreen(game));
                    }
                })
            ));
        }
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(delta, 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
