package io.github.tubes.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.tubes.controller.Main;
import io.github.tubes.model.*;

import java.util.ArrayList;


public class GameScreen implements Screen {
    private final Main game;
    private final Stage stage;
    private final Skin skin;
    private final int stageLevel;
    private ArrayList<Player> party;
    private Enemy enemy;
    private ArrayList<Label> partyHpLabels;
    private ArrayList<Image> partyImages;
    private ArrayList<ProgressBar> partyHealthBars;
    private Label enemyHpLabel;
    private Label messageLabel;
    private TextButton attackButton;
    private TextButton defendButton;
    private TextButton itemButton;
    private Image enemyImage;
    private ProgressBar enemyHealthBar;
    private boolean isPaused = false;
    private Dialog pauseDialog;
    private Dialog inventoryDialog;
    private Dialog targetSelectionDialog;
    private int currentPlayerIndex = 0;
    private boolean isPlayerPhase = true;
    private boolean isDefending = false;

    public GameScreen(Main game, int stageLevel) {
        this.game = game;
        this.stage = new Stage(new FitViewport(Main.VIRTUAL_WIDTH, Main.VIRTUAL_HEIGHT));
        this.stageLevel = stageLevel;
        Gdx.input.setInputProcessor(stage);
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));

        this.party = GameData.getParty();
        partyHpLabels = new ArrayList<>();
        partyImages = new ArrayList<>();
        partyHealthBars = new ArrayList<>();

        this.enemy = new Enemy(
            "Mega Monster Stage " + stageLevel,
            (int) ((100 + (stageLevel * 30)) * 2.5f),
            30 + (stageLevel * 2),
            50 + (stageLevel * 3),
            5 + stageLevel,
            stageLevel
        );

        setupUI(stageLevel);
        setupPauseDialog();
        setupInventoryDialog();
        setupTargetSelectionDialog();
        updateAllStatusUI();
        updateTurnIndicator();
    }

    private void setupPauseDialog() {
        pauseDialog = new Dialog("Game Paused", skin, "default");
        pauseDialog.getTitleLabel().setAlignment(Align.center);
        Table contentTable = pauseDialog.getContentTable();
        contentTable.pad(20);
        TextButton resumeButton = new TextButton("Resume", skin);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.pressSound.play(0.4f);
                isPaused = false;
                pauseDialog.hide();
            }
        });
        TextButton exitButton = new TextButton("Exit to Menu", skin);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.backSound.play(0.3f);
                game.setScreen(new MainMenuScreen(game));
            }
        });
        contentTable.add(resumeButton).width(200).height(50).pad(10);
        contentTable.row();
        contentTable.add(exitButton).width(200).height(50).pad(10);
    }

    private void setupInventoryDialog() {
        inventoryDialog = new Dialog("Inventory", skin, "default");
        inventoryDialog.getTitleLabel().setAlignment(Align.center);
    }

    private void populateInventoryDialog() {
        Table content = inventoryDialog.getContentTable();
        content.clear();
        content.pad(10).defaults().width(250).height(50).pad(5);
        final Inventory inv = GameData.getInventory();
        final Item potion = GameData.getItem("Potion");
        int potionQty = inv.getQuantity(potion.name);
        if (potionQty > 0) {
            TextButton usePotionButton = new TextButton("Use " + potion.name + " (" + potionQty + ")", skin);
            usePotionButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    inventoryDialog.hide();
                    populateAndShowTargetDialog();
                }
            });
            content.add(usePotionButton);
        } else {
            content.add(new Label("No items.", skin));
        }
        content.row().padTop(20);
        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.pressSound.play(0.4f);
                inventoryDialog.hide();
                showActionButtons(true);
            }
        });
        content.add(closeButton);
    }

    private void setupTargetSelectionDialog() {
        targetSelectionDialog = new Dialog("Select Target", skin, "default");
        targetSelectionDialog.getTitleLabel().setAlignment(Align.center);
    }

    private void populateAndShowTargetDialog() {
        Table content = targetSelectionDialog.getContentTable();
        content.clear();
        content.pad(10).defaults().width(200).height(50).pad(5);

        for (final Player targetHero : party) {
            TextButton targetButton = new TextButton(targetHero.getName(), skin);
            boolean canBeHealed = targetHero.getHp() < targetHero.getMaxHp();
            if (!canBeHealed) {
                targetButton.setDisabled(true);
                targetButton.getLabel().setColor(Color.DARK_GRAY);
            }
            targetButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (canBeHealed) {
                        final Item potion = GameData.getItem("Potion");
                        if (GameData.getInventory().useItem(potion.name)) {
                            game.potionEffectSound.play(0.3f);
                            if (targetHero.isDead()) {
                                targetHero.dead = false;
                                targetHero.setHp(potion.healAmount);
                                messageLabel.setText(party.get(currentPlayerIndex).getName() + " Revived " + targetHero.getName() + "!");
                            } else {
                                targetHero.setHp(targetHero.getHp() + potion.healAmount);
                                messageLabel.setText(party.get(currentPlayerIndex).getName() + " used Potion on " + targetHero.getName() + "!");
                            }
                            targetSelectionDialog.hide();
                            updateAllStatusUI();
                            nextHeroTurn();
                        }
                    } else {
                        messageLabel.setText("Cannot use on " + targetHero.getName() + ".");
                    }
                }
            });
            content.add(targetButton);
            content.row();
        }

        TextButton cancelButton = new TextButton("Cancel", skin);
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.backSound.play(0.3f);
                targetSelectionDialog.hide();
                showActionButtons(true);
            }
        });
        content.add(cancelButton).padTop(10);
        targetSelectionDialog.show(stage);
    }

    private void showActionButtons(boolean show) {
        attackButton.setVisible(show);
        defendButton.setVisible(show);
        itemButton.setVisible(show);
    }

    private ProgressBar.ProgressBarStyle createHealthBarStyle() {
        Pixmap pixmapBg = new Pixmap(10, 10, Pixmap.Format.RGBA8888);
        pixmapBg.setColor(Color.valueOf("4a1e1e"));
        pixmapBg.fill();
        TextureRegionDrawable drawableBg = new TextureRegionDrawable(new Texture(pixmapBg));
        pixmapBg.dispose();
        Pixmap pixmapKnob = new Pixmap(10, 10, Pixmap.Format.RGBA8888);
        pixmapKnob.setColor(Color.valueOf("65ff7a"));
        pixmapKnob.fill();
        TextureRegionDrawable drawableKnob = new TextureRegionDrawable(new Texture(pixmapKnob));
        pixmapKnob.dispose();
        ProgressBar.ProgressBarStyle progressBarStyle = new ProgressBar.ProgressBarStyle();
        progressBarStyle.background = drawableBg;
        progressBarStyle.knobBefore = drawableKnob;
        return progressBarStyle;
    }

    private void setupUI(int stageLevel) {
        Image bgImage = new Image(new Texture("Gameplay_bg.png"));
        bgImage.setFillParent(true);
        stage.addActor(bgImage);
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);
        Table topBar = new Table();
        TextButton pauseButton = new TextButton("||", skin);
        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.pressSound.play(0.4f);
                isPaused = true;
                pauseDialog.show(stage);
            }
        });
        topBar.add().expandX();
        topBar.add(pauseButton).top().right().size(40, 40).pad(10);
        messageLabel = new Label("The battle begins!", skin);
        messageLabel.setWrap(true);
        messageLabel.setAlignment(Align.center);
        attackButton = new TextButton("ATTACK", skin);
        defendButton = new TextButton("DEFEND", skin);
        itemButton = new TextButton("ITEM", skin);
        Table battleAreaTable = new Table();
        Table heroesTable = new Table();
        heroesTable.defaults().align(Align.left);
        String[] heroImageFiles = {"char1.png", "char2.png", "char3.png"};
        ProgressBar.ProgressBarStyle healthBarStyle = createHealthBarStyle();
        for (int i = 0; i < party.size(); i++) {
            Player p = party.get(i);
            Image pImage = new Image(new Texture(heroImageFiles[i]));
            Label pLabel = new Label("", skin);
            pLabel.setWrap(true);
            ProgressBar pHealthBar = new ProgressBar(0, p.getMaxHp(), 1, false, healthBarStyle);
            partyImages.add(pImage);
            partyHpLabels.add(pLabel);
            partyHealthBars.add(pHealthBar);
            Table heroStatusBox = new Table();
            heroStatusBox.add(pImage).size(64, 64).padRight(10);
            Table nameAndBar = new Table();
            nameAndBar.add(pLabel).width(120).left();
            nameAndBar.row();
            nameAndBar.add(pHealthBar).width(120).height(10).left().padTop(5);
            heroStatusBox.add(nameAndBar);
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
        this.enemyHealthBar = new ProgressBar(0, enemy.getMaxHp(), 1, false, healthBarStyle);
        Texture enemyTexture = new Texture("enemy1.png");
        TextureRegion enemyRegion = new TextureRegion(enemyTexture);
        enemyRegion.flip(true, false);
        this.enemyImage = new Image(enemyRegion);
        enemyTable.add(this.enemyImage).size(128, 128);
        enemyTable.row();
        enemyTable.add(enemyHpLabel).width(150);
        enemyTable.row();
        enemyTable.add(this.enemyHealthBar).width(128).height(15).padTop(5);
        battleAreaTable.add(heroesTable).expand().align(Align.left);
        battleAreaTable.add(enemyTable).expand().align(Align.right);
        Table actionTable = new Table();
        actionTable.add(attackButton).width(140).height(50).pad(5);
        actionTable.add(defendButton).width(140).height(50).pad(5);
        actionTable.add(itemButton).width(140).height(50).pad(5);
        rootTable.add(topBar).expandX().fillX();
        rootTable.row();
        rootTable.add(battleAreaTable).expand().bottom().padBottom(20);
        rootTable.row();
        Table bottomGroup = new Table();
        bottomGroup.add(messageLabel).expandX().fillX().height(40);
        bottomGroup.row();
        bottomGroup.add(actionTable);
        rootTable.add(bottomGroup).expandX().fillX().bottom().pad(10);
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
        itemButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isPlayerPhase) {
                    showActionButtons(false);
                    populateInventoryDialog();
                    inventoryDialog.show(stage);
                }
            }
        });
    }

    private void updateAllStatusUI() {
        for (int i = 0; i < party.size(); i++) {
            Player p = party.get(i);
            Label l = partyHpLabels.get(i);
            ProgressBar hb = partyHealthBars.get(i);
            hb.setValue(p.getHp());
            if (p.isDead()) {
                l.setText(p.getName() + "\n[DEFEATED]");
            } else {
                l.setText(String.format("%s\nHP: %d/%d", p.getName(), p.getHp(), p.getMaxHp()));
            }
        }
        enemyHealthBar.setValue(enemy.getHp());
        enemyHpLabel.setText(String.format("%s\nHP: %d / %d", enemy.getName(), enemy.getHp(), enemy.getMaxHp()));
    }

    private void playerTurn(boolean isAttacking) {
        if (party.get(currentPlayerIndex).isDead()) {
            nextHeroTurn();
            return;
        }
        isPlayerPhase = false;
        showActionButtons(false);
        final Player activeHero = party.get(currentPlayerIndex);
        Image activeHeroImage = partyImages.get(currentPlayerIndex);
        messageLabel.setText(activeHero.getName() + "'s turn!");
        activeHeroImage.addAction(Actions.sequence(
            Actions.moveBy(10, 0, 0.1f), Actions.moveBy(-10, 0, 0.1f),
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    if (isAttacking) {
                        game.punchSound.play(0.2f);
                        int playerDamage = MathUtils.random(activeHero.getMinDamage(), activeHero.getMaxDamage());
                        int finalDamage = Math.max(0, playerDamage - enemy.getDefense());
                        enemy.takeDamage(finalDamage);
                        messageLabel.setText(activeHero.getName() + " attacks, dealing " + finalDamage + " damage!");
                    } else {
                        game.defendSound.play(0.5f);
                        isDefending = true;
                        messageLabel.setText(activeHero.getName() + " is defending!");
                    }
                    updateAllStatusUI();
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
                showActionButtons(true);
                updateTurnIndicator();
                messageLabel.setText(party.get(currentPlayerIndex).getName() + "'s turn!");
            }
        }
    }

    private void enemyTurn() {
        messageLabel.setText(enemy.getName() + " attacks...");
        ArrayList<Player> livingHeroes = new ArrayList<>();
        for (Player p : party) {
            if (!p.isDead()) {
                livingHeroes.add(p);
            }
        }
        if (livingHeroes.isEmpty()) {
            return;
        }
        final Player target = livingHeroes.get(MathUtils.random(livingHeroes.size() - 1));
        enemyImage.addAction(Actions.sequence(
            Actions.moveBy(-10, 0, 0.1f),
            Actions.moveBy(10, 0, 0.1f),
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    game.punchSound.play(0.2f);
                    int enemyAttackPower = MathUtils.random(enemy.getMinDamage(), enemy.getMaxDamage());
                    int targetDefense = target.getDefense();
                    if (isDefending) {
                        targetDefense *= 2;
                    }
                    int finalDamage = Math.max(0, enemyAttackPower - targetDefense);
                    target.takeDamage(finalDamage);
                    if (isDefending) {
                        messageLabel.setText(enemy.getName() + " attacks " + target.getName() + ", but it was defended! " + finalDamage + " damage.");
                    } else {
                        messageLabel.setText(enemy.getName() + " attacks " + target.getName() + ", dealing " + finalDamage + " damage!");
                    }
                    isDefending = false;
                    updateAllStatusUI();
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
            if (!p.isDead()) {
                return false;
            }
        }
        return true;
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
        showActionButtons(false);
        if (playerWins) {
            final int goldGained = enemy.getGoldDrop();
            GameData.addGold(goldGained);
            messageLabel.setText("VICTORY!");
            stage.addAction(Actions.sequence(
                Actions.delay(1.0f),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        game.setScreen(new WinScreen(game, stageLevel, goldGained));
                    }
                })
            ));
        } else {
            messageLabel.setText("ALL HEROES DEFEATED...");
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
        game.playBattleMusic();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (!isPaused) {
            stage.act(Math.min(delta, 1 / 30f));
        }
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
