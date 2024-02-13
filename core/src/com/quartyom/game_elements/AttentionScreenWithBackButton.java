package com.quartyom.game_elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Align;
import com.quartyom.LayThePath;
import com.quartyom.interfaces.QuEvent;

public class AttentionScreenWithBackButton extends QuScreen {

    final LayThePath game;
    final String back_screen;

    Label attentionLabel;
    TextField infoField;
    Button backButton;


    public AttentionScreenWithBackButton(final LayThePath game, String filename, final String back_screen) {
        this.game = game;
        this.back_screen = back_screen;

        attentionLabel = new Label(game, game.locale.get("Attention"));

        infoField = new TextField(game,
                Gdx.files.internal("texts/" + game.userData.locale + "/"+ filename + ".txt")
                        .readString());

        backButton = new Button("in_main_menu", game, new QuEvent() {
            @Override
            public void execute() {
                game.setScreen(back_screen);
            }
        });
        backButton.setNinePatch(6).setLabel(game.locale.get("Back"));

    }

    @Override
    public void resize(int width, int height) {
        attentionLabel.resize(game.upperButtonCornerX, game.upperButtonCornerY, game.buttonW,
                game.buttonH, Align.center);

        int font_size = (int) (game.HEIGHT * (1.0f / 32.0f));
        infoField.resize(game.upperButtonCornerX,
                game.upperButtonCornerY - game.downMargin + game.buttonH, game.buttonW, font_size);
        // gap
        backButton.resize(game.upperButtonCornerX,
                game.upperButtonCornerY - game.downMargin * 5, game.buttonW, game.buttonH);
    }

    @Override
    public void render(float delta) {
        Gdx.gl20.glClear(Gdx.gl20.GL_COLOR_BUFFER_BIT);

        attentionLabel.draw();
        infoField.draw();
        backButton.draw();

        backButton.update();

        if (game.isBackButtonPressed) {
            game.isBackButtonPressed = false;
            game.setScreen(back_screen);
        }
    }

}
