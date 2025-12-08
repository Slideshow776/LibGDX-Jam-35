package no.sandramoen.libgdx35.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;

import no.sandramoen.libgdx35.utils.BaseActor;

public class Water extends BaseActor {
    public Water(Vector2 position, Vector2 size, Stage stage) {
        super(position.x, position.y, stage);

        loadImage("whitePixel");

        setSize(size.x, size.y);

        setColor(new Color(0x4b60a6FF));
    }
}
