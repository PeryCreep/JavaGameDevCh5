package com.mygdx.game;


import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class StarfishCollectorBeta_Original extends GameBeta {
    private Turtle turtle;
    private Starfish starfish;
    private BaseActor ocean;
    private Rock rock;

    public void initialize() {

        ocean = new BaseActor(0, 0, mainStage);
        ocean.loadTexture("water.jpg");
        ocean.setSize(800, 600);

        starfish = new Starfish(380, 380, mainStage);

        turtle = new Turtle(20, 20, mainStage);
        rock = new Rock(200, 200, mainStage);


    }

    public void update(float dt){

        turtle.preventOverlap(rock);

        if(turtle.overlaps(starfish) && !starfish.isCollected()){
            starfish.collect();
            Whirlpool whirl = new Whirlpool(0,0, mainStage);
            whirl.centerAtActor(starfish);
            whirl.setOpacity(0.25f);

            BaseActor youWinMessage = new BaseActor(0, 0, mainStage);
            youWinMessage.loadTexture("you-win.png");
            youWinMessage.centerAtPosition(400,300);
            youWinMessage.setOpacity(0);
            youWinMessage.addAction(Actions.delay(1));
            youWinMessage.addAction(Actions.after(Actions.fadeIn(1)));
        }
    }
}