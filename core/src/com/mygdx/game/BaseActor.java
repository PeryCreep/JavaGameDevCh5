package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;

/**
 * Extend the Actor class to include graphics and collision detection.
 * Actor class stores data such as position and rotation.
 */
public class BaseActor extends Actor {
    private Animation<TextureRegion> animation;
    private float elapsedTime;
    private boolean animationPaused;
    private Vector2 velocityVec, accelerateVec;
    private float acceleration, maxSpeed, deceleration;
    private Polygon boundaryPolygon;


    public BaseActor(float x, float y, Stage s) {
        super();

        setPosition(x, y);
        s.addActor(this);

        animation = null;
        elapsedTime = 0;
        animationPaused = false;

        velocityVec = new Vector2(0, 0);
        accelerateVec = new Vector2(0, 0);

        acceleration = 0;
        maxSpeed = 1000;
        deceleration = 0;
    }


    public void setAnimation(Animation<TextureRegion> anim) {
        animation = anim;
        TextureRegion tr = animation.getKeyFrame(0);
        float w = tr.getRegionWidth();
        float h = tr.getRegionHeight();
        setSize(w, h);
        setOrigin(w / 2, h / 2);

        if (boundaryPolygon == null)
            setBoundaryRectangle();
    }

    public void setAnimationPaused(boolean paused) {
        animationPaused = paused;
    }

    public void act(float dt) {
        super.act(dt);

        if (!animationPaused) {
            elapsedTime += dt;
        }
    }

    public boolean overlaps(BaseActor other){
        Polygon poly1 = this.getBoundaryPolygon();
        Polygon poly2 = other.getBoundaryPolygon();

        if(!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle()))
            return false;

        return Intersector.overlapConvexPolygons(poly1, poly2);
    }

    public static  ArrayList<BaseActor> getList(Stage stage, String className){
        ArrayList<BaseActor> list = new ArrayList<>();
        Class theClass = null;
        try{
            theClass = Class.forName(className);
        }catch (Exception e){
            e.printStackTrace();
        }
        for(Actor actor : stage.getActors()){
            if(theClass.isInstance(actor)){ list.add((BaseActor) actor);
            }
        }
        return list;
    }

    public static int getCountOfActor(Stage stage, String className){
        return getList(stage, className).size();
    }

    public void centerAtPosition(float x, float y){
        setPosition(x - getWidth()/2, y - getHeight()/2);
    }

    public void centerAtActor(BaseActor actor){
        centerAtPosition(actor.getX() + actor.getWidth()/2, actor.getY() + actor.getHeight()/2);
    }

    public void setOpacity(float opacity){
        this.getColor().a = opacity;
    }

    public void setBoundaryRectangle() {
        float w = getWidth();
        float h = getHeight();
        float[] vertices = {0, 0, w, 0, w, h, 0, h};
        boundaryPolygon = new Polygon(vertices);
    }

    public Vector2 preventOverlap(BaseActor other){
        Polygon poly1 = this.getBoundaryPolygon();
        Polygon poly2 = other.getBoundaryPolygon();

        if(!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle()))
            return null;

        Intersector.MinimumTranslationVector mtv = new Intersector.MinimumTranslationVector();
        boolean polygonOverlap = Intersector.overlapConvexPolygons(poly1, poly2, mtv);

        if(!polygonOverlap)
            return null;
        this.moveBy(mtv.normal.x, mtv.normal.y);
        return mtv.normal;
    }

    public void setBoundaryPolygon(int numSides) {
        float w = getWidth();
        float h = getHeight();

        float[] vertices = new float[2 * numSides];
        for (int i = 0; i < numSides; i++) {
            float angle = i * 6.28f / numSides;
            vertices[2 * i] = w / 2 * MathUtils.cos(angle) + w / 2;
            vertices[2 * i + 1] = h / 2 * MathUtils.sin(angle) + h / 2;
        }
        boundaryPolygon = new Polygon(vertices);
    }

    public Polygon getBoundaryPolygon() {
        boundaryPolygon.setPosition(getX(), getY());
        boundaryPolygon.setOrigin(getOriginX(), getOriginY());
        boundaryPolygon.setRotation(getRotation());
        boundaryPolygon.setScale(getScaleX(), getScaleY());
        return boundaryPolygon;
    }
    //========================================velocity======================================

    public void setSpeed(float speed) {
        if (velocityVec.len2() == 0)
            velocityVec.set(speed, 0);
        else
            velocityVec.setLength(speed);
    }

    public float getSpeed() {
        return velocityVec.len();
    }

    public void setMotionAngle(float angle) {
        velocityVec.setAngleDeg(angle);
    }

    public float getMotionAngle() {
        return velocityVec.angleDeg();
    }

    public boolean isMoving() {
        return (getSpeed() > 0);
    }

    //===================================velocity======================================
    //=================================================================================


    //===============================acceleration======================================
    //=================================================================================
    public void setAcceleration(float acc) {
        acceleration = acc;
    }

    public void accelerateAtAngle(float angle) {
        accelerateVec.add(new Vector2(acceleration, 0).setAngleDeg(angle));
    }

    public void accelerateForward() {
        accelerateAtAngle(getRotation());
    }

    //===================================================================================
    //===================================================================================

    public void setMaxSpeed(float ms) {
        maxSpeed = ms;
    }

    public void setDeceleration(float dec) {
        this.deceleration = dec;
    }


    //======apply Physics ==================================================================
    public void applyPhysics(float dt) {
        velocityVec.add(accelerateVec.x * dt, accelerateVec.y * dt);

        float speed = getSpeed();

        if (accelerateVec.len() == 0)
            speed -= deceleration * dt;

        speed = MathUtils.clamp(speed, 0, maxSpeed);

        setSpeed(speed);

        moveBy(velocityVec.x * dt, velocityVec.y * dt);

        accelerateVec.set(0, 0);
    }


    public Animation<TextureRegion> loadAnimationFromFiles(String[] filesNames, float frameDuration, boolean loop) {
        int fileCount = filesNames.length;
        Array<TextureRegion> textureArray = new Array<>();

        for (int i = 0; i < fileCount; i++) {
            String fileName = filesNames[i];
            Texture texture = new Texture(Gdx.files.internal(fileName));
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            textureArray.add(new TextureRegion(texture));

        }
        Animation<TextureRegion> anim = new Animation<>(frameDuration, textureArray);
        if (loop)
            anim.setPlayMode(Animation.PlayMode.LOOP);
        else
            anim.setPlayMode(Animation.PlayMode.NORMAL);

        if (animation == null)
            setAnimation(anim);
        return anim;
    }


    public Animation<TextureRegion> loadAnimationFromSheet(String fileName, int rows, int cols, float frameDuration, boolean loop) {
        Texture texture = new Texture(Gdx.files.internal(fileName));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        int frameWidth = texture.getWidth() / cols;
        int frameHeight = texture.getHeight() / rows;

        TextureRegion[][] temp = TextureRegion.split(texture, frameWidth, frameHeight);

        Array<TextureRegion> textureArray = new Array<>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                textureArray.add(temp[r][c]);
            }
        }
        Animation<TextureRegion> anim = new Animation<>(frameDuration, textureArray);

        if (loop)
            anim.setPlayMode(Animation.PlayMode.LOOP);
        else
            anim.setPlayMode(Animation.PlayMode.NORMAL);

        if (animation == null)
            setAnimation(anim);

        return anim;
    }

    public Animation<TextureRegion> loadTexture(String fileName) {
        String[] fileNames = new String[1];
        fileNames[0] = fileName;
        return loadAnimationFromFiles(fileNames, 1, true);
    }

    public boolean isAnimationFinished() {
        return animation.isAnimationFinished(elapsedTime);
    }

    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        Color c = getColor(); // used to apply tint color effect
        batch.setColor(c.r, c.g, c.b, c.a);

        if (animation != null && isVisible())
            batch.draw(animation.getKeyFrame(elapsedTime),
                    getX(), getY(), getOriginX(), getOriginY(),
                    getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
    }

}