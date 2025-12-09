package no.sandramoen.libgdx35.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.QuadTreeFloat;
import no.sandramoen.libgdx35.utils.BaseActor;
import no.sandramoen.libgdx35.utils.BaseGame;

public class Sheep extends BaseActor {

    private final float BORDER_DISTANCE_THRESHOLD = 0.5f;
    private final float PLAYER_DISTANCE_THRESHOLD = 5;
    private final float ALIGNMENT_THRESHOLD = 1.0f;
    private final float COHESION_THRESHOLD = 1.2f;

    private float walkingSpeed = 0.125f + MathUtils.random(-0.05f, 0.05f);
    private float runningSpeed = 0.5f + MathUtils.random(-0.05f, 0.05f);
    private float movementAcceleration = runningSpeed * 0.75f + MathUtils.random(-0.05f, 0.05f);
    private static final FloatArray results = new FloatArray(256);


    public Sheep(Vector2 position, Stage stage) {
        super(position.x, position.y, stage);
        loadImage("whitePixel");
        setColor(new Color(0xf2d1d3FF));

        // body
        setSize(
            0.11f + MathUtils.random(-0.05f, 0.05f),
            0.2f
        );
        setSize(getWidth(), getWidth() * 2f + + MathUtils.random(-0.05f, 0.05f));
        centerAtPosition(position.x, position.y);
        setOrigin(Align.center);
        setBoundaryRectangle(0.5f);
        //setBoundaryPolygon(8, 0.9f);

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


    public void die() {
        isCollisionEnabled = false;
        remove();
    }


    public void herded() {
        isCollisionEnabled = false;
        remove();
    }


    public void updateBehaviour(Vector2 player_position, Array<Sheep> sheep, QuadTreeFloat quad) {
        boolean isPlayerClose = get_center_position().dst(player_position) < PLAYER_DISTANCE_THRESHOLD;
        setMaxSpeed(isPlayerClose ? runningSpeed : walkingSpeed);

        avoidBorders();
        avoidPlayer(player_position);

        if (isPlayerClose) {
            applyAlignment(sheep, quad);
            applyCohesion(sheep, quad);
        } else {
            wander();
        }
        applySeparation(sheep, quad);

        setRotation(getMotionAngle() - 90);
    }


    private void avoidBorders() {
        if (get_center_position().x < BORDER_DISTANCE_THRESHOLD) { // left border
            accelerateAtAngle(0);
            setRotation(getMotionAngle() - 90);
        } else if (get_center_position().x > BaseGame.WORLD_WIDTH - BORDER_DISTANCE_THRESHOLD) { // right border
            accelerateAtAngle(180);
            setRotation(getMotionAngle() - 90);
        } else if (get_center_position().y < BORDER_DISTANCE_THRESHOLD) { // bottom border
            accelerateAtAngle(90);
            setRotation(getMotionAngle() - 90);
        }/* else if (get_center_position().y > BaseGame.WORLD_HEIGHT - BORDER_DISTANCE_THRESHOLD) { // top border
            accelerateAtAngle(270);
            setRotation(getMotionAngle() - 90);
            return;
        }*/
    }


    private void avoidPlayer(Vector2 player_position) {
        Vector2 away_from_player = get_center_position().cpy().sub(player_position);
        float distance_to_player = away_from_player.len();

        if (distance_to_player < PLAYER_DISTANCE_THRESHOLD) {
            accelerateAtAngle(away_from_player.angleDeg());
            setRotation(getMotionAngle() - 90);
        }
    }


    private void applyAlignment(Array<Sheep> sheep, QuadTreeFloat quad) {
        Vector2 alignment = new Vector2();
        int alignmentCount = 0;

        results.clear();
        quad.query(getX(Align.center), getY(Align.center), ALIGNMENT_THRESHOLD, results);
        for (int i = 0, n = results.size; i < n; i+= 4) {
            int idx = (int)results.get(i);
            Sheep other = sheep.get(idx);
            if (other == this)
                continue;
            alignment.add(other.velocityVec);
            alignmentCount++;
        }

        if (alignmentCount > 0) {
            alignment.scl(1f / alignmentCount);
            accelerateAtAngle(alignment.angleDeg());
        }
    }


    private void applyCohesion(Array<Sheep> sheep, QuadTreeFloat quad) {
        Vector2 cohesion = new Vector2();
        int cohesionCount = 0;
        results.clear();
        quad.query(getX(Align.center), getY(Align.center), COHESION_THRESHOLD, results);
        for (int i = 0, n = results.size; i < n; i+= 4) {
            int idx = (int)results.get(i);
            Sheep other = sheep.get(idx);
            if (other == this)
                continue;
            cohesion.add(other.get_center_position());
            cohesionCount++;
        }

        if (cohesionCount > 0) {
            cohesion.scl(1f / cohesionCount);
            Vector2 toCenter = cohesion.sub(get_center_position());
            accelerateAtAngle(toCenter.angleDeg());
        }
    }


    private void applySeparation(Array<Sheep> sheep, QuadTreeFloat quad) {
        results.clear();
        quad.query(getX(Align.center), getY(Align.center), getHeight(), results);
        for (int i = 0, n = results.size; i < n; i+= 4) {
            int idx = (int)results.get(i);
            Sheep other = sheep.get(idx);
            if (other == this)
                continue;
            Vector2 normal = preventOverlap(other);
            if (normal != null)
                accelerateAtAngle(normal.angleDeg());
        }
    }


    private void wander() {
        if (MathUtils.random() < 0.1f) {
            float random_angle = MathUtils.random(-10f, 10f);
            accelerateAtAngle(getMotionAngle() + random_angle);
        }
    }
}
