package no.sandramoen.libgdx35.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;

import no.sandramoen.libgdx35.utils.BaseActor;

public class Bridge extends BaseActor {
    public BaseActor left_rail;
    public BaseActor right_rail;

    public Bridge(Vector2 position, Vector2 size, Stage stage) {
        super(position.x, position.y, stage);

        loadImage("whitePixel");
        setSize(size.x, size.y);
        setColor(new Color(0xdbb88dFF));

        setBoundaryRectangle(1f);
        setDebug(true);

        // guard rails
        left_rail = new BaseActor(getX(), getY(), stage);
        left_rail.loadImage("whitePixel");
        left_rail.setColor(new Color(0x987b57FF));
        left_rail.setSize(size.x * 0.1f, size.y);
        left_rail.setBoundaryRectangle(1f);
        stage.addActor(left_rail);

        right_rail = new BaseActor(0, 0, stage);
        right_rail.loadImage("whitePixel");
        right_rail.setColor(new Color(0x987b57FF));
        right_rail.setSize(size.x * 0.1f, size.y);
        right_rail.setPosition(getX() + size.x - right_rail.getWidth(), getY());
        right_rail.setBoundaryRectangle(1f);
        stage.addActor(right_rail);
    }
}
