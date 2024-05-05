package com.mygdx.dog;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class dog extends ApplicationAdapter {
	SpriteBatch batch;
	private Texture dogTexture;
	private Texture slimeTexture;

	// Rectangles
	private Rectangle dogRect;
	private Rectangle slimeRect;
	private int dogSpeed = 5;
	private int slimeSpeed = 200;
	
	private Animation<TextureRegion> dogIdleAnimation;
	private Animation<TextureRegion> dogWalkLeftRightAnimation;
	private Animation<TextureRegion> dogWalkUpAnimation;
	private Animation<TextureRegion> dogWalkDownAnimation;

	private Animation<TextureRegion> slimeIdleAnimation;
	private Animation<TextureRegion> slimeMoveAnimation;
	private int slimeState;
	private static final int IDLE = 0;
	private static final int UP = 1;
	private static final int RIGHT = 2;
	private static final int DOWN = 3;
	private static final int LEFT = 4;
	private static final int MOVE = 1;
	private SpriteBatch spriteBatch;

	float stateTime;
	private static final int WIDTH = 1200;
	private static final int HEIGHT = 720;

	// dog sprite sheet
	private static final int DOG_WIDTH = 128, DOG_HEIGHT = 128;

	// slime sprite sheet
	private static final int SLIME_WIDTH = 32, SLIME_HEIGHT = 25;
	private static final int SLIME_SHEET_ROW_= 8;
	private static final int SLIME_SHEET_COL = 3;
	private static final int SLIME_IDLE_FRAMES = 3;
	private static final int SLIME_MOVE_FRAMES = 3;

	float attackPeriod = 3.0f;
	float cooldownPeriod = 3.5f;

	boolean attacking = false;

	boolean game = true;

	// player enum
	Player.State state;
	BitmapFont font;
	private float slimeFrameSpeed = 0.2f;
	boolean dogFlip = false;
	Player player;
	ShapeRenderer shapeRenderer;
	float fx, fy;

	OrthographicCamera camera;


	@Override
	public void create () {

		camera = new OrthographicCamera();
		camera.setToOrtho(false, WIDTH, HEIGHT);
		font = new BitmapFont();
		font.getData().setScale(1.0f);
		spriteBatch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

		// create player
		player = new Player(0,0,128, 128);

		int index = 0;


		slimeTexture = new Texture("slime.png");
		TextureRegion temp[][] = TextureRegion.split(slimeTexture, slimeTexture.getWidth() / SLIME_SHEET_ROW_,
				slimeTexture.getHeight() / SLIME_SHEET_COL);

		// Idle
		TextureRegion[] slimeIdleFrames = new TextureRegion[SLIME_IDLE_FRAMES];
		index = 0;
		for (int i = 0; i < SLIME_IDLE_FRAMES; i++) slimeIdleFrames[index++] = temp[0][i];
		slimeIdleAnimation = new Animation<TextureRegion>(slimeFrameSpeed, slimeIdleFrames);

		// Move
		TextureRegion[] slimeMoveFrames = new TextureRegion[SLIME_MOVE_FRAMES];
		index = 0;
		for (int i = 5; i < SLIME_MOVE_FRAMES + 5; i++) slimeMoveFrames[index++] = temp[1][i];
		slimeMoveAnimation = new Animation<TextureRegion>(slimeFrameSpeed, slimeMoveFrames);

		slimeRect = new Rectangle();
		slimeRect.x = (float) WIDTH / 5 - (float) SLIME_WIDTH / 2;
		slimeRect.y = (float) HEIGHT / 3 - (float) SLIME_HEIGHT / 2;
		slimeRect.width = SLIME_WIDTH * 5;
		slimeRect.height = SLIME_HEIGHT * 5;

		stateTime = 0;
		attackPeriod = 3.0f;

		state = Player.State.IDLE;
	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0, 0); // clear screen


		if(!game)
		{
			spriteBatch.begin();
			font.draw(spriteBatch, "YOU LOSE LOSER!", WIDTH/2, HEIGHT/2);
			spriteBatch.end();
			return;
		}
		stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time.
		TextureRegion currentDog;
		TextureRegion currentSlime;

		// get current frame of animation for the current stateTime
		currentDog = player.getAnimation(state, stateTime);

		if(slimeState == MOVE)
		{
			currentSlime = slimeMoveAnimation.getKeyFrame(stateTime, true);
		}
		else
		{
			currentSlime = slimeIdleAnimation.getKeyFrame(stateTime, true);

		}

		// begin a new batch and do the draw

		//camera.update();
		//spriteBatch.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(1,1,0,1);
		shapeRenderer.line(player.getRect().x + DOG_WIDTH/2, player.getRect().y + DOG_HEIGHT/2, fx + DOG_WIDTH/2, fy+ DOG_HEIGHT/2);
		shapeRenderer.rect(WIDTH/2, HEIGHT/2, 50,50);
		shapeRenderer.end();

		fx = fy = 0;
		spriteBatch.begin();
		spriteBatch.draw(currentSlime, slimeRect.x, slimeRect.y, slimeRect.width, slimeRect.height);
		spriteBatch.draw(currentDog, dogFlip ? player.getRect().x + player.getRect().width : player.getRect().x, player.getRect().y,
				dogFlip ? -player.getRect().width : player.getRect().width, player.getRect().height);

		String str = "DEBUG: " + stateTime + " " + attackPeriod;
		font.draw(spriteBatch, "Arrow Keys to Move. Space to sprint!", 10, 40);
		font.draw(spriteBatch, str, 10, 80);

		spriteBatch.end();

		state = Player.State.IDLE;

		Vector2 direction = new Vector2();

		// process user input
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
		{
			//player.getRect().x -= dogSpeed * Gdx.graphics.getDeltaTime();
			direction.x = -1;
			state = Player.State.MOVE_LEFT_RIGHT;
			dogFlip = true;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
		{
			//player.getRect().x += dogSpeed * Gdx.graphics.getDeltaTime();
			direction.x = 1;
			state = Player.State.MOVE_LEFT_RIGHT;
			dogFlip = false;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.DOWN))
		{
			//player.getRect().y -= dogSpeed * Gdx.graphics.getDeltaTime();
			direction.y = -1;
			state = Player.State.MOVE_DOWN;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.UP))
		{
			//player.getRect().y += dogSpeed * Gdx.graphics.getDeltaTime();
			direction.y = 1;
			state = Player.State.MOVE_UP;
		}
		direction.nor();

		player.getRect().x += direction.x * dogSpeed;
		player.getRect().y += direction.y * dogSpeed;

		fx = player.getRect().x + (200 * direction.x);
		fy = player.getRect().y + (200 * direction.y);

		//camera.position.set(player.getRect().x, player.getRect().y, 0);

//		if(Gdx.input.isKeyPressed(Input.Keys.SPACE))
//		{
//			dogSpeed = 350;
//		}
//		else
//		{
//			dogSpeed = 200;
//		}

		// dog bounds
//		if(dogRect.x < 0) dogRect.x = 0;
//		if(dogRect.x > WIDTH - DOG_WIDTH) dogRect.x = WIDTH - DOG_WIDTH;
//		if(dogRect.y < 0) dogRect.y = 0;
//		if(dogRect.y > HEIGHT - DOG_HEIGHT) dogRect.y = HEIGHT - DOG_HEIGHT;
//
//		// calculate distance to player and move it close to the player
//		// find when slime can attack
//		if(!attacking && stateTime > attackPeriod)
//		{
//			attacking = true;
//		}
//
//		if(attacking)
//		{
//			Vector2 direction = new Vector2();
//			direction.x = dogRect.x - slimeRect.x;
//			direction.y = dogRect.y - slimeRect.y;
//			direction.nor();
//			slimeRect.x += direction.x * 10;
//			slimeRect.y += direction.y * 10;
//			slimeState = MOVE;
//		}
//		if(stateTime > cooldownPeriod && attacking)
//		{
//			attacking = false;
//			slimeState = IDLE;
//			stateTime -= cooldownPeriod;
//		}
//
//		if(slimeRect.overlaps(dogRect))
//		{
//			game = false;
//		}



	}
	
	@Override
	public void dispose () {
		spriteBatch.dispose();
		dogTexture.dispose();
		slimeTexture.dispose();
	}
}
