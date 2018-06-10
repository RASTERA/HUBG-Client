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
import com.rastera.hubg.desktop.Main;
import org.json.JSONObject;

import javax.swing.*;
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
    private ArrayList<String> actions = new ArrayList<>();
    private int alive = 0;
    private int purgeCounter = 1;

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

    // Loading

    private Texture loadingBG;

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
        this.soundHashMap.put("1911", Gdx.audio.newSound(Gdx.files.internal("sounds/shot.wav")));
        this.soundHashMap.put("AK47", Gdx.audio.newSound(Gdx.files.internal("sounds/shot.wav")));

        latoFont = new BitmapFont(Gdx.files.internal("fnt/Lato-Regular-64.fnt"), Gdx.files.internal("fnt/lato.png"), false);
        weaponAtlas = new TextureAtlas(Gdx.files.internal("Weapons.atlas"));
        miniMap = new Texture(Gdx.files.internal("minimap.png"));

        loadingBG = new Texture(Gdx.files.internal("images/menu-background-2.png"));

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
            JSONObject address = com.rastera.hubg.desktop.Communicator.request(com.rastera.hubg.desktop.Communicator.RequestType.GET, null, com.rastera.hubg.desktop.Communicator.getURL(com.rastera.hubg.desktop.Communicator.RequestDestination.API) + "gameAddress");

            System.out.println(address);

            conn = new Communicator(address.getString("address"), address.getInt("port"), this);
            networkConnected = true;
        } catch (Exception e) {
            e.printStackTrace();
            this.player = new Player(world, this, new long[] {1000000, 1000000, 0, 0, 100});
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

            case -3: // Message

                if (((String) ServerMessage.message).contains("killed by")) {
                    Gdx.app.exit();
                }

                Thread msg = new Thread(() -> {
                    JOptionPane.showMessageDialog(((String) ServerMessage.message).contains("killed by") ? com.rastera.hubg.desktop.Rah.checkParent(this.parentGame.getParent()) : null, (String) ServerMessage.message, "Message from server", JOptionPane.INFORMATION_MESSAGE);

                });

                msg.start();

                break;

            case -2: // Determine if handshake is successful
                if (((String) ServerMessage.message).equals("success")) {
                    System.out.println("Connection Accepted");

                } else {
                    Gdx.app.exit();
                    this.parentGame.rejectConnection((String) ServerMessage.message);
                }

                break;

            case -1: // Get server name and issue token
                this.serverName = (String) ServerMessage.message;

                this.serverToken = com.rastera.hubg.desktop.Communicator.getServerAuthToken(this.serverName);

                System.out.println("Token: " + serverToken);

                conn.write(-2, this.serverToken);

                break;

            case 0: // Assign UID
                this.ID = (Integer) ServerMessage.message;
                System.out.println("Current ID: " + this.ID);
                break;

            case 1: // Add player
                GLProcess.add(ServerMessage);
                break;

            case 10: // Update position
                if (!gameStart) {
                    break;
                }
                long[] cords = (long[]) ServerMessage.message;

                //System.out.println(cords[0] + " " + cords[1] + " " + (int) cords[3]);

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
                    ArrayList<long[]> a = new ArrayList<long[]>();
                    a.add(cords);
                    GLProcess.add(Rah.messageBuilder(1, a));
                }
                break;

            case 11: // Bullet

                try {
                    JSONObject data = new JSONObject((String) ServerMessage.message);

                    if (data.getInt("enemy") == this.ID) {
                        // weapon lookup here

                        if (player.damage(1)) {
                            System.out.println("Player dead");
                        }

                    }

                    if (data.getInt("attacker") != this.ID) {

                        for (Enemy aEnemyList : EnemyList) {
                            if (aEnemyList.getId() == data.getInt("attacker")) {

                                soundHashMap.get(data.getString("weapon")).play(Math.min(50 / dist(this.player.getX(), this.player.getY(), aEnemyList.getX(), aEnemyList.getY()), 1.0f));

                                break;
                            }
                        }


                    }

                } catch (Exception e) {
                    Main.errorQuit(e);
                }

                break;

            case 12: // Action log
                actions.add((String) ServerMessage.message);

                System.out.println(actions);

                break;

            case 13: // Players alive
                alive = (int) ServerMessage.message;

                break;

            case 14: // Set health
                this.player.setHealth((float) ServerMessage.message);

                break;

            case 15: // Remove player
                GLProcess.add(ServerMessage);

                break;

        }
    }

    public float dist(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow((x1 - x2), 2) +  Math.pow((y1 - y2), 2));
    }

    private void handleNetworking(float dt){
        if (networkConnected && gameStart) {

            dTotal += dt;

            if (dTotal >= HUBGMain.SYNC_INTERVAL) {
                dTotal -= HUBGMain.SYNC_INTERVAL;

                if (ox != player.b2body.getPosition().x || oy != player.b2body.getPosition().y || or != getCameraRotation()) {
                          conn.write(10, new long[]{(long) (player.b2body.getPosition().x * 1000f), (long) (player.b2body.getPosition().y * 1000), (long) (getCameraRotation() * 1000f), this.ID});
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

                        for (long[] p : (ArrayList<long[]>) pMessage.message) {
                            System.out.println(p[3]);

                            if (p[3] == this.ID) {

                                if (this.player == null) {
                                    this.player = new Player(world, this, p);
                                    this.conn.write(14, null);
                                    gamecam.position.x = player.b2body.getPosition().x;
                                    gamecam.position.y = player.b2body.getPosition().y;
                                    gamecam.rotate(p[2] * MathUtils.radiansToDegrees);

                                    gameHUD = new HUD(main.batch, staticPort, player, latoFont);

                                    connecting = false;
                                }

                            } else if (!hasEnemy((int) p[3])) {
                                EnemyList.add(new Enemy(world, this, "Karl", p));
                            }
                        }

                        gameStart = true;

                        if (this.player == null) {
                            Main.errorQuit("Player not found");
                        }

                        break;

                    case 15: // Remove player

                        for (int i = 0; i < EnemyList.size(); i++) {
                            if (EnemyList.get(i).getId() == (int) pMessage.message) {
                                world.destroyBody(this.EnemyList.get(i).b2body);
                                this.EnemyList.remove(i);
                                break;
                            }
                        }

                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean hasEnemy(int id) {

        for (Enemy e : EnemyList) {
            if (e.getId() == id) {
                return true;
            }
        }

        return false;
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
            this.soundHashMap.get(this.player.weapon.getCurrentWeapon()).play();

            int enemyID = calculateBullet(400);

            try {
                if (networkConnected) {
                    conn.write(11, new JSONObject() {
                        {
                            put("enemy", enemyID);
                            put("attacker", ID);
                            put("weapon", player.weapon.getCurrentWeapon());
                        }
                    }.toString());
                }
            } catch (Exception e) {
                Main.errorQuit(e);
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
            latoFont.draw(main.batch, String.format("X: %d | Y: %d | R: %d | Alive: %d", (int) player.b2body.getPosition().x, (int) player.b2body.getPosition().y, (int) normalizeAngle((player.b2body.getAngle() * 360 / (2 * Math.PI))), alive), staticPort.getScreenWidth() / 2 - minMapPadding - miniMapSize + 10, staticPort.getScreenHeight() / 2 - minMapPadding - miniMapSize + 20);

            int compassTicks = staticPort.getScreenWidth() / 100;
            int angle = (int) normalizeAngle(player.b2body.getAngle() * 360 / (2 * Math.PI));

            if (compassTicks % 2 == 0) {
                compassTicks ++;
            }

            for (int compassX = compassTicks / -2; compassX <= compassTicks / 2; compassX ++) {
                centerText(main.batch, latoFont, 0.2f, "" + (angle + compassX * 10),angle + compassX * 100, staticPort.getScreenHeight() / 2 - 20);
            }

            for (int i = 0; i < actions.size(); i++) {
                latoFont.draw(main.batch, actions.get(i), staticPort.getScreenWidth() / 2 - minMapPadding - miniMapSize + 10, staticPort.getScreenHeight() / 2 - minMapPadding - miniMapSize - 10 - 15 * i);
            }

            purgeCounter = (purgeCounter + 1) % 1000;

            if (purgeCounter == 0 && actions.size() > 0) {
                actions.remove(0);
            }

            main.batch.end();

            if (shoot) {
                sr.setColor(Color.WHITE);
                sr.setProjectionMatrix(gamecam.combined);
                sr.begin(ShapeRenderer.ShapeType.Line);
                sr.line(player.getLocation().x, player.getLocation().y, raycastPoint.x, raycastPoint.y);
                sr.end();
            }

            gameHUD.draw(main.batch, staticcam);
        }

        b2dr.render(world, gamecam.combined);

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

            /*

            int size = Math.min(staticPort.getScreenWidth(), staticPort.getScreenHeight());

            main.batch.begin();
            main.batch.draw(loadingBG, staticPort.getScreenWidth() / -2, staticPort.getScreenHeight() / -2, size, size);

            latoFont.setColor(Color.BLACK); */

            centerText(main.batch, latoFont, 0.5f, "CONNECTING TO SERVER", staticPort.getScreenWidth() / 2, staticPort.getScreenHeight() / 2);

            //latoFont.setColor(Color.WHITE);

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
