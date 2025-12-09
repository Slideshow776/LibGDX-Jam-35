package no.sandramoen.libgdx35.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.QuadTreeFloat;
import com.github.tommyettinger.textra.TextraLabel;

import no.sandramoen.libgdx35.actors.Bridge;
import no.sandramoen.libgdx35.actors.Grass;
import no.sandramoen.libgdx35.actors.Overlay;
import no.sandramoen.libgdx35.actors.Player;
import no.sandramoen.libgdx35.actors.Sheep;
import no.sandramoen.libgdx35.actors.Water;
import no.sandramoen.libgdx35.actors.WinArea;
import no.sandramoen.libgdx35.actors.particles.EffectBurst;
import no.sandramoen.libgdx35.utils.AssetLoader;
import no.sandramoen.libgdx35.utils.BaseActor;
import no.sandramoen.libgdx35.utils.BaseGame;
import no.sandramoen.libgdx35.utils.BaseScreen;

public class LevelScreen extends BaseScreen {

    private BaseActor overlay;
    private Grass grass;
    private Water water;
    private Array<Bridge> bridges;
    private Player player;
    private QuadTreeFloat quad;
    private Array<Sheep> sheep;
    private WinArea winArea;

    private final int NUM_SHEEP = 480;
    private final float VARIANCE = 0.25f;
    private int sheep_killed = 0;
    private int sheep_herded = 0;

    private TextraLabel score_label;
    private TextraLabel kill_label;
    private TextraLabel high_score_label;


    public LevelScreen() {}


    @Override
    public void initialize() {
        grass = new Grass(mainStage);

        water = new Water(
            new Vector2(0, 11.5f),
            new Vector2(BaseGame.WORLD_WIDTH, 3),
            mainStage
        );

        bridges = new Array<Bridge>();
        bridges.add(new Bridge(
            new Vector2(3, 11f),
            new Vector2(2, 4),
            mainStage
        ));

        quad = new QuadTreeFloat(16, 8);

        sheep = new Array<Sheep>();
        for (int i = 0; i < NUM_SHEEP / 4; i++) {
            Vector2 sp = new Vector2(12f + MathUtils.randomTriangular(-VARIANCE, VARIANCE), 7.5f + MathUtils.randomTriangular(-VARIANCE, VARIANCE));
            quad.add(sheep.size, sp.x, sp.y);
            sheep.add(new Sheep(sp, mainStage));
        }
        for (int i = 0; i < NUM_SHEEP / 4; i++) {
            Vector2 sp = new Vector2(10f + MathUtils.randomTriangular(-VARIANCE, VARIANCE), 2.5f + MathUtils.randomTriangular(-VARIANCE, VARIANCE));
            quad.add(sheep.size, sp.x, sp.y);
            sheep.add(new Sheep(sp, mainStage));
        }
        for (int i = 0; i < NUM_SHEEP / 4; i++) {
            Vector2 sp = new Vector2(3f + MathUtils.randomTriangular(-VARIANCE, VARIANCE), 4f + MathUtils.randomTriangular(-VARIANCE, VARIANCE));
            quad.add(sheep.size, sp.x, sp.y);
            sheep.add(new Sheep(sp, mainStage));

        }
        for (int i = 0; i < NUM_SHEEP / 4; i++) {
            Vector2 sp = new Vector2(2 + MathUtils.randomTriangular(-VARIANCE, VARIANCE), 1.5f + MathUtils.randomTriangular(-VARIANCE, VARIANCE));
            quad.add(sheep.size, sp.x, sp.y);
            sheep.add(new Sheep(sp, mainStage));
        }
        player = new Player(new Vector2(14, 2), mainStage);

        initialize_gui();
        //GameUtils.playLoopingMusic(AssetLoader.levelMusic);

        winArea = new WinArea(new Vector2(0, BaseGame.WORLD_HEIGHT - 0.5f), new Vector2(BaseGame.WORLD_WIDTH, 1f), mainStage);
        winArea.setDebug(true);
        overlay = new Overlay(mainStage);
    }


    @Override
    public void update(float delta) {
        update_sheep(delta);
    }


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

        EffectBurst effect = new EffectBurst();
        effect.setPosition(world_position.x, world_position.y);
        effect.setScale(0.00125f);
        mainStage.addActor(effect);
        effect.start();

        return super.touchDown(screenX, screenY, pointer, button);
    }


    private void update_sheep(float delta) {
        // set avoidance behaviour
        for (int i = 0; i < sheep.size; i++) {
            sheep.get(i).updateBehaviour(player.get_center_position(), sheep, quad);
        }
        // collision checks
        for (int i = 0; i < sheep.size; i++) {
            // guard rails
            for (Bridge bridge : bridges) {
                Vector2 temp = sheep.get(i).preventOverlap(bridge.left_rail);
                if (temp != null) sheep.get(i).accelerateAtAngle(temp.angleDeg());
                temp = sheep.get(i).preventOverlap(bridge.right_rail);
                if (temp != null) sheep.get(i).accelerateAtAngle(temp.angleDeg());

                if (sheep.get(i).overlaps(water) && !sheep.get(i).overlaps(bridge)) { // death check
                    sheep.get(i).die();
                    sheep.removeIndex(i);
                    sheep_killed++;
                    kill_label.setText(String.valueOf(sheep_killed));
                } else if (sheep.get(i).overlaps(winArea)) { // win check
                    sheep.get(i).herded();
                    sheep.removeIndex(i);
                    sheep_herded++;
                    score_label.setText(String.valueOf(sheep_herded));
                    checkWinCondition();
                }
            }
        }
        if(sheep_killed + sheep_herded > 0) {
            quad.reset();
            for (int i = 0, n = sheep.size; i < n; i++) {
                Sheep s = sheep.get(i);
                quad.add(i, s.getX(Align.center), s.getY(Align.center));
            }
        }
    }


    private void checkWinCondition() {
        if (!sheep.isEmpty())
            return;

        System.out.println("a winner is you!");
    }


    private void initialize_gui() {
        // resources setup
        float label_scale = 0.5f;
        Image score_image = new Image(AssetLoader.textureAtlas.findRegion("trophy"));
        score_label = new TextraLabel("0", AssetLoader.getLabelStyle("Alegreya59white"));
        score_label.getFont().scale(label_scale);
        score_label.setColor(Color.FOREST);
        score_label.setAlignment(Align.center);

        Image kill_image = new Image(AssetLoader.textureAtlas.findRegion("skull"));
        kill_label = new TextraLabel("0", AssetLoader.getLabelStyle("Alegreya59white"));
        kill_label.getFont().scale(label_scale);
        kill_label.setColor(Color.FIREBRICK);
        kill_label.setAlignment(Align.center);

        Image high_score_image = new Image(AssetLoader.textureAtlas.findRegion("crown"));
        high_score_label = new TextraLabel("1000" + "", AssetLoader.getLabelStyle("Alegreya59white"));
        high_score_label.getFont().scale(label_scale);
        high_score_label.setColor(Color.BLACK);
        high_score_label.setAlignment(Align.center);

        // ui setup
        uiTable.defaults()
            .padTop(Gdx.graphics.getHeight() * .02f)
        ;

        Table herd_table = new Table();
        herd_table.add(score_image)
            .width(Gdx.graphics.getWidth() * 0.025f)
            .height(Gdx.graphics.getHeight() * 0.04f)
            .padTop(Gdx.graphics.getHeight() * .02f)
        ;
        herd_table.add(score_label)
            .top()
        ;

        uiTable.add(herd_table)
            .padTop(Gdx.graphics.getHeight() * .1f)
            .row();
        ;

        Table kill_table = new Table();
        kill_table.add(kill_image)
            .width(Gdx.graphics.getWidth() * 0.025f)
            .height(Gdx.graphics.getHeight() * 0.04f)
            .padTop(Gdx.graphics.getHeight() * .02f)
        ;
        kill_table.add(kill_label)
            .top()
        ;

        uiTable.add(kill_table)
            .expandY()
            .top()
        ;

        //uiTable.setDebug(true);
    }
}
