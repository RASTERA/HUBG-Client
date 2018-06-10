package com.rastera.hubg.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.rastera.hubg.HUBGMain;
import com.rastera.Networking.Communicator;
import com.rastera.Networking.Message;
import com.rastera.hubg.Scene.HUD;
import com.rastera.hubg.Sprites.Brick;
import com.rastera.hubg.Sprites.Enemy;
import com.rastera.hubg.Sprites.Player;
import com.rastera.hubg.Util.Rah;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class HUBGGame implements Screen {

    private OrthographicCamera gamecam;
    private OrthographicCamera staticcam;
    private float defaultZoom;
    private Viewport gamePort;
    private Viewport staticPort;
    private HUBGMain main;

    private BitmapFont latoFont;
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private TextureAtlas weaponAtlas;
    private OrthogonalTiledMapRenderer renderer;
    private TiledMapTileLayer displayLayer;
    private HashMap<String, Sound> soundHashMap = new HashMap<>();
    private Box2DDebugRenderer b2dr;
    private World world;
    private Player player;
    private int ID;
    private float dTotal = 0;
    public Communicator conn;

    private boolean gameStart = false;
    private boolean paused = false;
    private boolean connecting = true;
    private boolean pausedLock = true;
    private boolean networkConnected = false;
    private ArrayList<Enemy> EnemyList = new ArrayList<Enemy>();
    private LinkedBlockingQueue<Message> GLProcess = new LinkedBlockingQueue<Message>();

    private float ox = -1;
    private float oy = -1;
    private float or = -1;
    private boolean inWater = false;

    private float closestFraction;
    private int raycastID;
    private Vector2 raycastPoint;
    private boolean shoot = false;
    private boolean canShoot = true;
    private float fireDelay = 0;

    private com.rastera.hubg.desktop.Game parentGame;

    private String serverName;
    private String serverToken;

    //MiniMap

    private Texture miniMap;
    private int minMapPadding = 10;
    private int miniMapSize = 300;
    private int miniMapTextureSize = 6000;
    private TextureRegion miniMapDisplay;

    //HUD

    private HUD gameHUD;

    public HUBGGame(HUBGMain main, com.rastera.hubg.desktop.Game parentGame) {
        this.main = main;
        this.parentGame = parentGame;

        // Import audio
        this.soundHashMap.put("shot", Gdx.audio.newSound(Gdx.files.internal("sounds/shot.wav")));

        latoFont = new BitmapFont(Gdx.files.internal("fnt/Lato-Regular-64.fnt"), Gdx.files.internal("fnt/lato.png"), false);
        weaponAtlas = new TextureAtlas(Gdx.files.internal("Weapons.atlas"));
        miniMap = new Texture(Gdx.files.internal("minimap.png"));

        gamecam = new OrthographicCamera();
        staticcam = new OrthographicCamera();
        gamePort = new ScreenViewport(gamecam);
        staticPort = new ScreenViewport(staticcam);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("hubg.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 5 / HUBGMain.PPM);
        displayLayer = (TiledMapTileLayer) map.getLayers().get(0);

        b2dr = new Box2DDebugRenderer();
        world = new World(new Vector2(0, 0), true);

        //////////////////TESTING
        Brick b = new Brick(world, map, new Rectangle(10, 10, 100 / HUBGMain.PPM, HUBGMain.PPM));
        //////////////////////////

        gamecam.rotate(90);
        gamecam.update();

        //Server Loading
        try {
            conn = new Communicator(new byte[]{127, 0, 0, 1}, 25565, this);
            networkConnected = true;
        } catch (Exception e) {
            e.printStackTrace();
            this.player = new Player(world, this, new float[] {1000, 1000, 0, 0});
            gameStart = true;
            connecting = false;
            gameHUD = new HUD(main.batch, staticPort, player, latoFont);
        }
    }

    @Override
    public void show() {
        gamecam.zoom = (float) (Math.pow(HUBGMain.PPM, -1) * 2);
        defaultZoom = gamecam.zoom;

        gamecam.update();
    }

    public void CommandProcessor(final Message ServerMessage) {
        switch (ServerMessage.type) {
            case -2:

                if (((String) ServerMessage.message).equals("success")) {
                    System.out.println("Connection Accepted");
                    connecting = false;
                } else {

                    Gdx.app.exit();
                    this.parentGame.rejectConnection((String) ServerMessage.message);
                }

                break;

            case -1:
                this.serverName = (String) ServerMessage.message;

                this.serverToken = com.rastera.hubg.desktop.Communicator.getServerAuthToken(this.serverName);

                System.out.println("Token: " + serverToken);

                conn.write(-2, this.serverToken);

                break;

            case 0:
                this.ID = (Integer) ServerMessage.message;
                System.out.println("Current ID: " + this.ID);
                break;
            case 1:
                GLProcess.add(ServerMessage);
                break;
            case 10:
                if (!gameStart) {
                    break;
                }
                float[] cords = (float[]) ServerMessage.message;

                if (this.ID == cords[3]) {
                    break;
                }
                boolean found = false;

                for (Enemy aEnemyList : EnemyList) {
                    if (aEnemyList.getId() == cords[3]) {
                        aEnemyList.updateLocation(cords);
                        found = true;


                        break;
                    }
                }

                if (!found) {
                    ArrayList<float[]> a = new ArrayList<float[]>();
                    a.add(cords);
                    GLProcess.add(Rah.messageBuilder(1, a));
                }
                break;
            case 11:

                if (((int[]) ServerMessage.message)[0] == this.ID) {
                    // weapon lookup here

                    if (player.damage(1)) {
                        System.out.println("Player dead");
                    }

                }
        }
    }

    private void handleNetworking(float dt){
        if (networkConnected && gameStart) {

            dTotal += dt;

            if (dTotal >= HUBGMain.SYNC_INTERVAL) {
                dTotal -= HUBGMain.SYNC_INTERVAL;

                if (ox != player.b2body.getPosition().x || oy != player.b2body.getPosition().y || or != getCameraRotation()) {
                    conn.write(10, new float[]{player.b2body.getPosition().x, player.b2body.getPosition().y, getCameraRotation(), ID});
                }

                ox = player.b2body.getPosition().x;
                oy = player.b2body.getPosition().y;
                or = player.b2body.getAngle();
            }
        }

        if (!GLProcess.isEmpty()) {
            try {
                Message pMessage = GLProcess.take();

                switch (pMessage.type) {
                    case 1:
                        System.out.println("Start game:" + pMessage.message);

                        for (float[] p : (ArrayList<float[]>) pMessage.message) {
                            if (p[3] == this.ID) {
                                this.player = new Player(world, this, p);
                                gamecam.position.x = player.b2body.getPosition().x;
                                gamecam.position.y = player.b2body.getPosition().y;
                                gamecam.rotate(p[2] * MathUtils.radiansToDegrees);

                                gameHUD = new HUD(main.batch, staticPort, player, latoFont);
                            } else {
                                EnemyList.add(new Enemy(world, this, "Karl", p));
                            }
                        }

                        gameStart = true;
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public int calculateBullet (float range) {
        range += player.getWidth();
        closestFraction = 99999;
        raycastPoint = new Vector2(player.getLocation().x + range * MathUtils.cos(player.getAngle()), player.getLocation().y + range * MathUtils.sin(player.getAngle()));
        raycastID = -1;

        RayCastCallback callback = (fixture, point, normal, fraction) -> {
            if ( fraction < closestFraction && (int) fixture.getUserData() != -1) {
                closestFraction = fraction;
                raycastPoint.set(point);
                raycastID = (Integer) fixture.getUserData();
            }

            return 1;
        };

        world.rayCast(callback, player.getLocation(), new Vector2(player.getLocation().x + range * MathUtils.cos(player.getAngle()), player.getLocation().y + range * MathUtils.sin(player.getAngle())));

        return raycastID;
    }

    public float getCameraRotation() {
        return MathUtils.atan2(gamecam.up.y, gamecam.up.x);
    }

    public void handleInput(float dt) {
        player.b2body.setLinearVelocity(0, 0);
        Vector2 movement = player.b2body.getPosition();
        float r = 0;

        Vector2 impulse = new Vector2();

        if (!paused) {
            r = getCameraRotation();

            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                impulse.add(new Vector2(-200 * player.b2body.getMass() * MathUtils.cos(r), -200 * player.b2body.getMass() * MathUtils.sin(r)));
            }

            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                impulse.add(new Vector2(200 * player.b2body.getMass() * MathUtils.cos(r), 200 * player.b2body.getMass() * MathUtils.sin(r)));
            }

            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                impulse.add(new Vector2(-200 * MathUtils.cos(r - MathUtils.PI / 2), -200 * player.b2body.getMass() * MathUtils.sin(r - MathUtils.PI / 2)));
            }

            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                impulse.add(new Vector2(-200 * MathUtils.cos(r + MathUtils.PI / 2), -200 * player.b2body.getMass() * MathUtils.sin(r + MathUtils.PI / 2)));
            }

            if (inWater) {
                impulse.scl(0.1f);
            }

            player.b2body.applyLinearImpulse(impulse, player.b2body.getWorldCenter(), true);

            r = 0;

            if (Gdx.input.isKeyPressed(Input.Keys.E)) {
                r -= 90 * dt;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
                r += 90 * dt;
            }
        }

        if (!paused && Gdx.input.isKeyPressed(Input.Keys.SPACE) && gamecam.zoom < 2000 / HUBGMain.PPM + defaultZoom) {
            gamecam.zoom += 0.02;
        } else if (gamecam.zoom > defaultZoom && (!Gdx.input.isKeyPressed(Input.Keys.SPACE) || paused)) {
            gamecam.zoom -= 0.02;
        }

        gamecam.position.x = movement.x;
        gamecam.position.y = movement.y;
        gamecam.rotate(-r);

        player.b2body.setTransform(movement, getCameraRotation());

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            if (pausedLock) {
                System.out.println("TOGGLE");
                paused = !paused;
            }
            pausedLock = false;
        } else {
            pausedLock = true;
        }

        fireDelay += dt;
        if (fireDelay >= 0.3) {
            canShoot = true;
            fireDelay -= 0.3;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.P) && canShoot) {
            this.soundHashMap.get("shot").play((float) Math.random(), 1f, -1);

            int enemyID = calculateBullet(400);

            if (networkConnected && enemyID != -1) {
                conn.write(11, new int[] {enemyID, this.ID});
            } else if (networkConnected) {
                conn.write(11, new int[] {-1, this.ID});
            }

            canShoot = false;
            shoot = true;
        } else {
            shoot = false;
        }


    }

    public void update(float dt) {
        handleNetworking(dt);

        try {
            if (displayLayer.getCell((int) (player.b2body.getPosition().x / 10), (int) (player.b2body.getPosition().y / 10)).getTile().getId() == 208) {
                inWater = true;
            } else {
                inWater = false;
            }
        } catch (Exception e) {

        }

        //System.out.println(player.b2body.getPosition().x + " " +player.b2body.getPosition().y);
        world.step(1/60f, 6, 2);

        if (gameStart) {
            handleInput(dt);
            player.update(dt);
            for (Enemy e : EnemyList) {
                e.step(dt);
            }

            gameHUD.update(staticPort);
        }

        gamecam.update();
        renderer.setView(gamecam);
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0, 0 ,1 ,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        if (gameStart) {

            main.batch.setProjectionMatrix(gamecam.combined);
            main.batch.begin();
            player.draw(main.batch);

            for (Enemy e : EnemyList) {
                e.draw(main.batch);
            }

            main.batch.end();

            main.batch.setProjectionMatrix(staticcam.combined);
            main.batch.begin();
            main.batch.draw(miniMap, staticPort.getScreenWidth() / 2 - minMapPadding - miniMapSize, staticPort.getScreenHeight() / 2 - minMapPadding - miniMapSize, miniMapSize, miniMapSize);
            main.batch.end();

            ShapeRenderer sr = new ShapeRenderer();
            sr.setProjectionMatrix(staticcam.combined);
            sr.setColor(Color.RED);
            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.rect(staticPort.getScreenWidth() / 2 - minMapPadding - miniMapSize + player.b2body.getPosition().x / 10000 * miniMapSize, staticPort.getScreenHeight() / 2 - minMapPadding - miniMapSize + player.b2body.getPosition().y / 10000 * miniMapSize, 5/2, 5/2, 5, 5, 1f, 1f, MathUtils.radiansToDegrees * player.getAngle());
            sr.end();

            main.batch.begin();

            latoFont.getData().setScale(0.2f);
            latoFont.draw(main.batch, String.format("X: %d | Y: %d | R: %d", (int) player.b2body.getPosition().x, (int) player.b2body.getPosition().y, (int) normalizeAngle((player.b2body.getAngle() * 360 / Math.PI))), staticPort.getScreenWidth() / 2 - minMapPadding - miniMapSize + 10, staticPort.getScreenHeight() / 2 - minMapPadding - miniMapSize + 20);

            main.batch.end();

            if (shoot) {
                sr.setColor(Color.WHITE);
                sr.setProjectionMatrix(gamecam.combined);
                sr.begin(ShapeRenderer.ShapeType.Line);
                sr.line(player.getLocation().x, player.getLocation().y, raycastPoint.x, raycastPoint.y);
                sr.end();
            }
        }

        b2dr.render(world, gamecam.combined);

        gameHUD.draw(main.batch, staticcam);

        if (paused) {

            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            ShapeRenderer sr = new ShapeRenderer();
            sr.setColor(Color.WHITE);
            sr.setProjectionMatrix(staticcam.combined);

            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.setColor(new Color(0, 0, 0, 0.5f));

            sr.rect(staticPort.getScreenWidth() / -2, staticPort.getScreenHeight() / -2, staticPort.getScreenWidth(), staticPort.getScreenHeight());
            sr.end();

            Gdx.gl.glDisable(GL20.GL_BLEND);

            main.batch.begin();

            centerText(main.batch, latoFont, 0.5f, "PAUSED", 0, 0);

            main.batch.end();
        }

        if (connecting) {

            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            ShapeRenderer sr = new ShapeRenderer();
            sr.setColor(Color.WHITE);
            sr.setProjectionMatrix(staticcam.combined);

            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.setColor(new Color(0, 0, 0, 0.5f));

            sr.rect(staticPort.getScreenWidth() / -2, staticPort.getScreenHeight() / -2, staticPort.getScreenWidth(), staticPort.getScreenHeight());

            sr.end();

            Gdx.gl.glDisable(GL20.GL_BLEND);

            main.batch.begin();

            centerText(main.batch, latoFont, 0.5f, "CONNECTING TO SERVER", staticPort.getScreenWidth() / 2, staticPort.getScreenHeight() / 2);

            main.batch.end();
        }
    }

    public void centerText(Batch batch, BitmapFont font, float size, String text, int x, int y) {

        font.getData().setScale(size);

        GlyphLayout layout = new GlyphLayout(font, text);

        font.draw(batch, text, x - layout.width / 2, y + layout.height / 2);

    }

    public double normalizeAngle(double angle) {
        angle = angle % 360;

        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }

    public TextureAtlas getWeaponAtlas() {
        return weaponAtlas;
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
        staticPort.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        System.out.println("lol");
        this.parentGame.exitGame();
    }
}
