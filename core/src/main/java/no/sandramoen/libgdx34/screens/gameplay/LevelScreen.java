package no.sandramoen.libgdx35.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.github.tommyettinger.textra.TextraLabel;

import no.sandramoen.libgdx35.actors.Background;
import no.sandramoen.libgdx35.actors.Overlay;
import no.sandramoen.libgdx35.utils.AssetLoader;
import no.sandramoen.libgdx35.utils.BaseActor;
import no.sandramoen.libgdx35.utils.BaseGame;
import no.sandramoen.libgdx35.utils.BaseScreen;

public class LevelScreen extends BaseScreen {

    private Background background;
    private BaseActor overlay;

    private TextraLabel score_label;
    private TextraLabel high_score_label;


    public LevelScreen() {}


    @Override
    public void initialize() {
        background = new Background(mainStage);

        initialize_gui();
        //GameUtils.playLoopingMusic(AssetLoader.levelMusic);

        overlay = new Overlay(mainStage);
    }


    @Override
    public void update(float delta) {}


    @Override
    public boolean keyDown(int keycode) {
        return super.keyDown(keycode);
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
        high_score_label = new TextraLabel(BaseGame.high_score + "s", AssetLoader.getLabelStyle("Alegreya40white"));
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
