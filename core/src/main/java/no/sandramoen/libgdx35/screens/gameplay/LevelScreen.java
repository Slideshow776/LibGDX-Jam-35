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
        // audio
        AssetLoader.ambianceMusic.setLooping(true);
        AssetLoader.ambianceMusic.setPosition(MathUtils.random(0f, 40f));
        AssetLoader.ambianceMusic.setVolume(BaseGame.musicVolume * 1f);
        AssetLoader.ambianceMusic.play();

        AssetLoader.herdMusic.setLooping(true);
        AssetLoader.herdMusic.setPosition(MathUtils.random(0f, 40f));
        AssetLoader.herdMusic.setVolume(0f);
        AssetLoader.herdMusic.play();

        // actors
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
        bridges.add(new Bridge(
            new Vector2(12, 11f),
            new Vector2(1, 4),
            mainStage
        ));

        sheep = new Array<Sheep>();
        for (int i = 0; i < NUM_SHEEP / 4; i++)
            sheep.add(new Sheep(new Vector2(12, 7.5f), mainStage));
        for (int i = 0; i < NUM_SHEEP / 4; i++)
            sheep.add(new Sheep(new Vector2(10, 2f), mainStage));
        for (int i = 0; i < NUM_SHEEP / 4; i++)
            sheep.add(new Sheep(new Vector2(3, 4f), mainStage));
        for (int i = 0; i < NUM_SHEEP / 4; i++)
            sheep.add(new Sheep(new Vector2(2, 1.25f), mainStage));

        player = new Player(new Vector2(14, 2), mainStage);

        initialize_gui();
        //GameUtils.playLoopingMusic(AssetLoader.levelMusic);

        winArea = new WinArea(new Vector2(0, BaseGame.WORLD_HEIGHT - 0.5f), new Vector2(BaseGame.WORLD_WIDTH, 1f), mainStage);
        winArea.setDebug(true);
        overlay = new Overlay(mainStage);
    }


    @Override
    public void update(float delta) {
        update_sheep();
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


    private void update_sheep() {
        // set avoidance behaviour
        float player_distance = 100f;
        // collision checks
        for (int i = 0; i < sheep.size; i++) {
            // guard rails
            sheep.get(i).updateBehaviour(player.get_center_position(), sheep);

            if (sheep.get(i).get_center_position().dst(player.get_center_position()) < player_distance)
                player_distance = sheep.get(i).get_center_position().dst(player.get_center_position());

            // collision checks
            boolean is_on_a_bridge = false;

            for (Bridge bridge : bridges) {
                // left guard-rail
                Vector2 temp = sheep.get(i).preventOverlap(bridge.left_rail);
                if (temp != null) sheep.get(i).accelerateAtAngle(temp.angleDeg());

                // right guard-rail
                temp = sheep.get(i).preventOverlap(bridge.right_rail);
                if (temp != null) sheep.get(i).accelerateAtAngle(temp.angleDeg());

                if (sheep.get(i).overlaps(bridge))
                    is_on_a_bridge = true;

            }

            if (sheep.get(i).overlaps(water) && !is_on_a_bridge) { // death check
                sheep.get(i).die();
                sheep.removeIndex(i);
                sheep_killed++;
                kill_label.setText(String.valueOf(sheep_killed));
            }

            if (sheep.get(i).overlaps(winArea)) { // win check
                sheep.get(i).herded();
                sheep.removeIndex(i);
                sheep_herded++;
                score_label.setText(String.valueOf(sheep_herded));
                checkWinCondition();
            }
        }

        if (player_distance < 100) {
            float volume = MathUtils.clamp(BaseGame.soundVolume * (0.5f / player_distance * 0.7f),0f, BaseGame.soundVolume);
            AssetLoader.herdMusic.setVolume(volume);
        } else {
            AssetLoader.herdMusic.setVolume(0f);
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
            .row()
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
