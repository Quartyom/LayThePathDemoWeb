package com.quartyom;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.utils.Json;
import com.quartyom.game_elements.DrawingQueue;
import com.quartyom.game_elements.FontHolder;
import com.quartyom.game_elements.Locale;
import com.quartyom.game_elements.QuFPS;
import com.quartyom.game_elements.QuGame;
import com.quartyom.game_elements.SoundHolder;
import com.quartyom.interfaces.Sender;
import com.quartyom.screens.Level.LevelScreen;

import java.util.Random;

public class LayThePath extends QuGame {
    public Sender sender;
    public LayThePath(Sender sender){
        this.sender = sender;
    }

    public SpriteBatch batch;
    public FontHolder fontHolder;
    public GlyphLayout glyphLayout;
    public SoundHolder soundHolder;
    public TextureAtlas fieldAtlas, buttonsAtlas, slidersAtlas;
    // public Json json;
    public Locale locale;
    public Random random;

    public UserData userData;
    public final QuFPS quFPS = new QuFPS();
    public DrawingQueue drawingQueue;

    public boolean isBackButtonPressed;

    public void create() {
        batch = new SpriteBatch();
        glyphLayout = new GlyphLayout();
        soundHolder = new SoundHolder();
        fieldAtlas = new TextureAtlas("textures/field.atlas");
        buttonsAtlas = new TextureAtlas("textures/buttons.atlas");
        slidersAtlas = new TextureAtlas("textures/sliders.atlas");

        // json = new Json();

        userData = new UserData();

        locale = new Locale(this);
        locale.set(userData.locale);
        fontHolder = new FontHolder(this, "fonts/OpenSans-Light.ttf");

        random = new Random();

        drawingQueue = new DrawingQueue();

        addScreens();
        setScreen("level");

        Gdx.input.setCatchKey(Input.Keys.BACK, true);
        Gdx.input.setInputProcessor(this);
    }

    private void addScreens(){
        this.addDefault("level", new LevelScreen(this));
    }

    @Override
    public void render() {
        batch.begin();
        super.render(); // important!
        drawingQueue.draw();
        batch.end();
        quFPS.update();

        if (isBackButtonPressed) {
            setScreen(null);    // установит Screen по умолчанию
        }
    }

    @Override
    public void dispose() {
        super.dispose(); // important!

        batch.dispose();
        fontHolder.dispose();
        soundHolder.dispose();

        fieldAtlas.dispose();
        buttonsAtlas.dispose();
        slidersAtlas.dispose();
    }

    public int HOW_MANY_BUTTONS = 6;
    public float padding = 0.8f;

    public float downMargin, buttonActualSizeX, buttonActualSizeY, upperButtonCenterX = 0, upperButtonCenterY;
    public float upperButtonCornerX, upperButtonCornerY, buttonW, buttonH;

    @Override
    public void resize(int width, int height) {
        WIDTH = width;
        HALF_WIDTH = WIDTH / 2;
        HEIGHT = height;
        HALF_HEIGHT = HEIGHT / 2;

        batch.getProjectionMatrix().setToOrtho2D(-HALF_WIDTH, -HALF_HEIGHT, WIDTH, HEIGHT);

        downMargin = HEIGHT / HOW_MANY_BUTTONS;

        buttonActualSizeX = HALF_WIDTH * padding;
        buttonActualSizeY = downMargin / 2 * padding;

        upperButtonCenterY = HALF_HEIGHT - downMargin / 2;

        upperButtonCornerX = upperButtonCenterX - buttonActualSizeX;
        upperButtonCornerY = upperButtonCenterY - buttonActualSizeY;

        buttonW = buttonActualSizeX * 2;
        buttonH = buttonActualSizeY * 2;

        super.resize(width, height);
    }

    @Override
    public boolean keyDown(int i) {
        if (i == Input.Keys.BACK) {
            isBackButtonPressed = true;
            return true;
        }
        return false;
    }

    public void sendStat(){
//        SocketHints socketHints = new SocketHints();
//        socketHints.connectTimeout = 4000;
        try {
            if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
                Socket socket = Gdx.net.newClientSocket(Net.Protocol.TCP, "localhost", 8003, null);
                String string = quFPS.minFPS + ";" + quFPS.avgFPS + "\n";
                socket.getOutputStream().write(string.getBytes());
            }
            else {
                sender.send(quFPS.minFPS, quFPS.avgFPS);
            }
        }
        catch (Exception e) {}


    }


}
