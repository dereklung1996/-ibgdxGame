package com.mygdx.Game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

public class Player {

    public enum State { IDLE, MOVE_LEFT_RIGHT, MOVE_UP, MOVE_DOWN }
    public int StateSize = State.values().length;
    private State currState;
    private State prevState;

    private Rectangle rect;

    private Texture texture;
    private TextureRegion currTextureRegion;
    private ArrayList<Animation<TextureRegion>> animations;

    // SPRITE SHEET STATICS
    public static final int DOG_SHEET_ROW = 4;
    public static final int DOG_SHEET_COL = 9;
    public static final int DOG_WALK_DOWN_FRAME_ROW = 0;
    public static final int DOG_WALK_HORIZONTAL_FRAME_ROW = 1;
    public static final int DOG_WALK_UP_FRAME_ROW = 2;
    public static final int DOG_IDLE_FRAME_ROW = 4;
    private float dogFrameSpeed = 0.2f;

    // constructor
    public Player(float x, float y, float w, float h)
    {
        animations = new ArrayList<>(StateSize);
        this.rect = new Rectangle(x, y, w, h);
        // Rectangle to logically represent the dog sprite
        // load texture
        texture = new Texture("dog.png");
        this.loadAnimations();
    }
    /*
     * Some hard coded way to pull the sprite sheet data.
     */
    private void loadAnimations()
    {
        TextureRegion[] tempRegion;
        // split into texture region array
        TextureRegion tempSplit[][] = TextureRegion.split(texture, texture.getWidth() / DOG_SHEET_ROW,
                texture.getHeight() / DOG_SHEET_COL);
        // Idle
        tempRegion = new TextureRegion[DOG_SHEET_ROW - 2];
        int index = 0;
        for (int i = 2; i < DOG_SHEET_ROW; i++) tempRegion[index++] = tempSplit[DOG_IDLE_FRAME_ROW][i];
        animations.add(new Animation<TextureRegion>(dogFrameSpeed, tempRegion));
        // Walk Left Right
        tempRegion = new TextureRegion[DOG_SHEET_ROW];
        index = 0;
        for (int i = 0; i < DOG_SHEET_ROW; i++) tempRegion[index++] = tempSplit[DOG_WALK_HORIZONTAL_FRAME_ROW][i];
        animations.add(new Animation<TextureRegion>(dogFrameSpeed, tempRegion));
        // Walk Up
        tempRegion = new TextureRegion[DOG_SHEET_ROW];
        index = 0;
        for (int i = 0; i < DOG_SHEET_ROW; i++) tempRegion[index++] = tempSplit[DOG_WALK_UP_FRAME_ROW][i];
        animations.add(new Animation<TextureRegion>(dogFrameSpeed, tempRegion));
        // Walk Down
        tempRegion = new TextureRegion[DOG_SHEET_ROW];
        index = 0;
        for (int i = 0; i < DOG_SHEET_ROW; i++) tempRegion[index++] = tempSplit[DOG_WALK_DOWN_FRAME_ROW][i];
        animations.add(new Animation<TextureRegion>(dogFrameSpeed, tempRegion));
    }

    public TextureRegion getAnimation(State state, float st)
    {
        return animations.get(state.ordinal()).getKeyFrame(st, true);
    }

    public State getCurrState() {
        return currState;
    }

    public Rectangle getRect()
    {
        return rect;
    }
}
