package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class MenuScreen extends BaseScreen {
    public void initialize() {
        BaseActor ocean = new BaseActor(0, 0, mainStage);
        ocean.loadTexture("water.jpg");
        ocean.setSize(800, 600);

        BaseActor title = new BaseActor(0, 0, mainStage);
        title.loadTexture("starfish-collector.png");
//        title.centerAtPosition(400, 300);
//        title.moveBy(0, 100);

        TextButton startButton = new TextButton("Start", BaseGame.textButtonStyle);

        startButton.addListener(
                new EventListener() {
                    @Override
                    public boolean handle(Event event) {
                        if (!(event instanceof InputEvent) || !(((InputEvent) event).getType().equals(InputEvent.Type.touchDown)))
                            return false;
                        StarfishGame.setActiveScreen(new LevelScreen());
                        return false;
                    }
                }
        );

        TextButton quitButton = new TextButton("Quit", BaseGame.textButtonStyle);

        quitButton.addListener(new EventListener() {
            @Override
            public boolean handle(Event e) {
                if (!(e instanceof InputEvent) || !(((InputEvent) e).getType().equals(InputEvent.Type.touchDown)))
                    return false;
                Gdx.app.exit();
                return false;
            }
        });

        uiTable.add(title).colspan(2);
        uiTable.row();
        uiTable.add(startButton);
        uiTable.add(quitButton);
    }

    public void update(float dt) {
        if (Gdx.input.isKeyPressed(Keys.S))
            StarfishGame.setActiveScreen(new LevelScreen());
    }

    public boolean keyDown(int KeyCode) {
        if (Gdx.input.isKeyPressed(Keys.ENTER))
            StarfishGame.setActiveScreen(new LevelScreen());
        if (Gdx.input.isKeyPressed(Keys.ESCAPE))
            Gdx.app.exit();
        return false;
    }
}
