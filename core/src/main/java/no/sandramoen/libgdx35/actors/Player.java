package no.sandramoen.libgdx35.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import no.sandramoen.libgdx35.utils.BaseActor;
import no.sandramoen.libgdx35.utils.BaseGame;


public class Player extends BaseActor {

    public Vector2 touch_position = new Vector2();

    private float movementSpeed = 12f;
    private float movementAcceleration = movementSpeed * 6f;


    public Player(Vector2 position, Stage stage) {
        super(position.x, position.y, stage);
        loadImage("whitePixel");
        setColor(new Color(0xec6827FF));

        // body
        setSize(0.25f, 0.25f);
        centerAtPosition(position.x, position.y);
        touch_position.set(getX(), getY());
        setOrigin(Align.center);
        setBoundaryPolygon(8, 0.9f);

        setWorldBounds(BaseGame.WORLD_WIDTH + 0.5f, BaseGame.WORLD_HEIGHT);

        // movement
        setAcceleration(movementAcceleration);
        setMaxSpeed(movementSpeed);
        setDeceleration(movementAcceleration);
    }


    @Override
    public void act(float delta) {
        super.act(delta);

        sampleTouch();
        pollKeyboard();

        applyPhysics(delta);
        boundToWorld();
    }


    public boolean isMoving() {
        return getSpeed() > BaseGame.MOVEMENT_THRESHOLD;
    }


    private void sampleTouch() {
        float distance = get_center_position().dst(touch_position);

        if (distance > BaseGame.MOVEMENT_THRESHOLD) {
            Vector2 direction = touch_position.cpy().sub(get_center_position());
            float angle_to_touch = direction.angleDeg();
            accelerateAtAngle(angle_to_touch);
        }
    }


    private void pollKeyboard() {
        if (Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP))
            accelerateAtAngle(90f);
        if (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT))
            accelerateAtAngle(180f);
        if (Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN))
            accelerateAtAngle(270f);
        if (Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT))
            accelerateAtAngle(0f);
    }
}
