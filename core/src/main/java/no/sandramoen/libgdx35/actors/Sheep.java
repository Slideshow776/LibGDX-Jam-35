package no.sandramoen.libgdx35.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.libgdx35.utils.BaseActor;
import no.sandramoen.libgdx35.utils.BaseGame;

public class Sheep extends BaseActor {

    private final float PLAYER_DISTANCE_THRESHOLD = 3;
    private final float BORDER_DISTANCE_THRESHOLD = 0.5f;

    private float walkingSpeed = 0.125f + MathUtils.random(-0.05f, 0.05f);
    private float runningSpeed = 0.75f + MathUtils.random(-0.05f, 0.05f);
    private float movementAcceleration = runningSpeed * 0.75f + MathUtils.random(-0.05f, 0.05f);


    public Sheep(Vector2 position, Stage stage) {
        super(position.x, position.y, stage);
        loadImage("whitePixel");
        setColor(new Color(0xf2d1d3FF));

        // body
        setSize(
            0.25f + MathUtils.random(-0.05f, 0.05f),
            0.25f + MathUtils.random(-0.05f, 0.05f)
        );
        centerAtPosition(position.x, position.y);
        setOrigin(Align.center);
        setBoundaryPolygon(8, 0.9f);

        //setWorldBounds(BaseGame.WORLD_WIDTH + 0.5f, BaseGame.WORLD_HEIGHT);

        // movement
        setAcceleration(movementAcceleration);
        setMaxSpeed(runningSpeed);
        setDeceleration(movementAcceleration);
    }


    @Override
    public void act(float delta) {
        super.act(delta);
        applyPhysics(delta);
    }


    public boolean isMoving() {
        return getSpeed() > BaseGame.MOVEMENT_THRESHOLD;
    }


    public void updateAvoidanceBehaviour(float delta, Vector2 player_position, Array<Sheep> sheep) {
        setMaxSpeed(runningSpeed);
        Vector2 away_from_player = get_center_position().cpy().sub(player_position);
        float distance_to_player = away_from_player.len();

        // avoid borders
        if (get_center_position().x < BORDER_DISTANCE_THRESHOLD) { // left border
            accelerateAtAngle(0);
            return;
        } else if (get_center_position().x > BaseGame.WORLD_WIDTH - BORDER_DISTANCE_THRESHOLD) { // right border
            accelerateAtAngle(180);
            return;
        } else if (get_center_position().y < BORDER_DISTANCE_THRESHOLD) { // bottom border
            accelerateAtAngle(90);
            return;
        }/* else if (get_center_position().y > BaseGame.WORLD_HEIGHT - BORDER_DISTANCE_THRESHOLD) { // top border
            accelerateAtAngle(270);
            return;
        }*/

        // avoid player
        if (distance_to_player < PLAYER_DISTANCE_THRESHOLD) {
            accelerateAtAngle(away_from_player.angleDeg());
        }

        // avoid other sheep
        for (int i = 0; i < sheep.size; i++) {
            if (sheep.get(i) == this)
                continue;

            Vector2 collision_normal_vector = preventOverlap(sheep.get(i));
            if (collision_normal_vector != null) {
                setMaxSpeed(walkingSpeed);
                sheep.get(i).accelerateAtAngle(MathUtils.random(0f, 360f));
            }
        }
    }


    public void die() {
        isCollisionEnabled = false;
        remove();
    }

    public void herded() {
        isCollisionEnabled = false;
        remove();
    }
}
