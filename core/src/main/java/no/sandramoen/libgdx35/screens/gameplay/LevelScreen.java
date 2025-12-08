package no.sandramoen.libgdx35.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.github.tommyettinger.textra.TextraLabel;

import no.sandramoen.libgdx35.actors.Bridge;
import no.sandramoen.libgdx35.actors.Grass;
import no.sandramoen.libgdx35.actors.Overlay;
import no.sandramoen.libgdx35.actors.Player;
import no.sandramoen.libgdx35.actors.Water;
import no.sandramoen.libgdx35.utils.AssetLoader;
import no.sandramoen.libgdx35.utils.BaseActor;
import no.sandramoen.libgdx35.utils.BaseGame;
import no.sandramoen.libgdx35.utils.BaseScreen;

public class LevelScreen extends BaseScreen {

    private Grass grass;
    private Water water;
    private Bridge bridge;
    private Player player;
    private BaseActor overlay;

    private TextraLabel score_label;
    private TextraLabel high_score_label;


    public LevelScreen() {}


    @Override
    public void initialize() {
        grass = new Grass(mainStage);

        water = new Water(
            new Vector2(0, 5.5f),
            new Vector2(BaseGame.WORLD_WIDTH, 3),
            mainStage
        );

        bridge = new Bridge(
            new Vector2(3, 5f),
            new Vector2(1, 4),
            mainStage
        );

        player = new Player(new Vector2(10, 2), mainStage);

        initialize_gui();
        //GameUtils.playLoopingMusic(AssetLoader.levelMusic);

        overlay = new Overlay(mainStage);
    }


    @Override
    public void update(float delta) {}


    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.Q) {
            Gdx.app.exit();
        }
        return super.keyDown(keycode);
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector2 world_position = mainStage.screenToStageCoordinates(new Vector2(screenX, screenY));
        player.touch_position = world_position;
        System.out.println("world touch down at: " + world_position);
        //System.out.println("angle: " + new Vector2(player.getX(), player.getY()).angleDeg(world_position));
        return super.touchDown(screenX, screenY, pointer, button);
    }


    private void initialize_gui() {
        // resources setup
        float label_scale = 1.0f;
        Image score_image = new Image(AssetLoader.textureAtlas.findRegion("clock"));
        score_label = new TextraLabel("0s", AssetLoader.getLabelStyle("Alegreya40white"));
        score_label.getFont().scale(label_scale);
        score_label.setColor(Color.BLACK);
        score_label.setAlignment(Align.center);

        Image high_score_image = new Image(AssetLoader.textureAtlas.findRegion("crown"));
        high_score_label = new TextraLabel("1000" + "s", AssetLoader.getLabelStyle("Alegreya40white"));
        high_score_label.getFont().scale(label_scale);
        high_score_label.setColor(Color.BLACK);
        high_score_label.setAlignment(Align.center);

        // ui setup
        uiTable.defaults()
            .padTop(Gdx.graphics.getHeight() * .02f)
        ;

        /*uiTable.add()
            .expandY()
            .top()
        ;*/

        //uiTable.setDebug(true);
    }
}
