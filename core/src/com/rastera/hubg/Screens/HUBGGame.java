package com.rastera.hubg.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
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
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.rastera.hubg.HUBGMain;
import com.rastera.hubg.Util.ItemList;
import com.rastera.hubg.desktop.Communicator;
import com.rastera.Networking.Message;
import com.rastera.hubg.Scene.HUD;
import com.rastera.hubg.Sprites.Brick;
import com.rastera.hubg.Sprites.Enemy;
import com.rastera.hubg.Sprites.Item;
import com.rastera.hubg.Sprites.Player;
import com.rastera.hubg.Tools.B2WorldCreator;

import com.rastera.hubg.collisionListener;
import com.rastera.hubg.customInputProcessor;
import com.rastera.hubg.desktop.Main;
import com.rastera.hubg.desktop.Util;
import org.json.JSONObject;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class HUBGGame implements Screen {

    public static BitmapFont latoFont;

    private OrthographicCamera gamecam;
    private OrthographicCamera staticcam;
    private float defaultZoom;
    private Viewport gamePort;
    private Viewport staticPort;
    private HUBGMain main;

    private TextureAtlas weaponAtlas;
    private OrthogonalTiledMapRenderer renderer;
    private TiledMapTileLayer displayLayer;
    private HashMap<String, Sound> soundHashMap = new HashMap<>();
    private Music runningMusic;
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
    private ArrayList<Enemy> EnemyList = new ArrayList<>();
    private LinkedBlockingQueue<Message> GLProcess = new LinkedBlockingQueue<>();

    private float ox = -1;
    private float oy = -1;
    private float or = -1;
    private boolean inWater = false;
    private boolean sprint = false;
    private float rageTint = 0f;

    private float closestFraction;
    private int raycastID;
    private Vector2 raycastPoint;
    private boolean shoot = false;
    private boolean canShoot = true;
    private float fireDelay = 0;

    private com.rastera.hubg.desktop.Game parentGame;

    // Loading

    //MiniMap

    private Texture miniMap;
    private int miniMapTextureSize = 6000;
    private TextureRegion miniMapDisplay;
    private LinkedList<Item> displayItems;
    private LinkedBlockingQueue<Item> itemQueue;

    private B2WorldCreator creator;
    //HUD

    private HUD gameHUD;

    public HUBGGame(HUBGMain main, com.rastera.hubg.desktop.Game parentGame) {
        this.main = main;
        this.parentGame = parentGame;
        ItemList.load();
        this.displayItems = new LinkedList<>();
        this.itemQueue = new LinkedBlockingQueue<>();

        /////
        this.b2dr = new Box2DDebugRenderer();
        this.world = new World(new Vector2(0, 0), true);

        this.miniMap = new Texture(Gdx.files.internal("minimap.png"));

        this.weaponAtlas = new TextureAtlas(Gdx.files.internal("Weapons.atlas"));

        TmxMapLoader mapLoader = new TmxMapLoader();
        TiledMap map = mapLoader.load("hubg.tmx");
        this.renderer = new OrthogonalTiledMapRenderer(map, 5 / HUBGMain.PPM);
        this.displayLayer = (TiledMapTileLayer) map.getLayers().get(0);

        creator = new B2WorldCreator(world, map);

        //////////////////TESTING
        Brick b = new Brick(this.world, map, new Rectangle(10, 10, 100 / HUBGMain.PPM, HUBGMain.PPM));
        //////////////////////////

        /////

        // Import audio
        this.soundHashMap.put("1911", Gdx.audio.newSound(Gdx.files.internal("sounds/shot.wav")));
        this.soundHashMap.put("AK47", Gdx.audio.newSound(Gdx.files.internal("sounds/shot.wav")));

        this.runningMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/running.wav"));

        HUBGGame.latoFont = new BitmapFont(Gdx.files.internal("fnt/Lato-Regular-64.fnt"), Gdx.files.internal("fnt/lato.png"), false);
        Texture loadingBG = new Texture(Gdx.files.internal("images/menu-background-2.png"));

        this.gamecam = new OrthographicCamera();
        this.staticcam = new OrthographicCamera();
        this.gamePort = new ScreenViewport(this.gamecam);
        this.staticPort = new ScreenViewport(this.staticcam);

        this.gamecam.rotate(90);
        this.gamecam.update();

        //Server Loading
        Thread connect = new Thread(() -> {

            try {
                JSONObject address = com.rastera.hubg.desktop.Communicator.request(com.rastera.hubg.desktop.Communicator.RequestType.GET, null, com.rastera.hubg.desktop.Communicator.getURL(com.rastera.hubg.desktop.Communicator.RequestDestination.API) + "gameAddress");

                System.out.println(address);

                if (!com.rastera.hubg.desktop.Communicator.developmentMode) {
                    this.conn = new Communicator(address.getString("address"), address.getInt("port"), this);
                } else {
                    this.conn = new Communicator("localhost", 8080, this);
                }

                this.networkConnected = true;

            } catch (Exception e) {
                e.printStackTrace();
                this.player = new Player(this.world, this, new long[] {1000000, 1000000, 0, 0, 100});
                Item test = new Item(1000, 1000, -1001, this.world);
                this.displayItems.add(test);
                this.displayItems.add(new Item(1000, 1000, -1001, this.world));
                this.gameStart = true;
                this.connecting = false;
                this.gameHUD = new HUD(main.batch, this.staticPort, this.player, this, HUBGGame.latoFont);
                Gdx.input.setInputProcessor(new customInputProcessor(this.gameHUD));
                this.world.setContactListener(new collisionListener(this.gameHUD));
            }
        });

        connect.start();

    }

    @Override
    public void show() {
        this.gamecam.zoom = (float) (Math.pow(HUBGMain.PPM, -1) * 2);
        this.defaultZoom = this.gamecam.zoom;

        this.gamecam.update();
    }

    public void CommandProcessor(final Message ServerMessage) {
        switch (ServerMessage.type) {

            case -3: // Message

                if (((String) ServerMessage.message).contains("killed by")) {
                    Gdx.app.exit();
                }

                Thread msg = new Thread(() -> JOptionPane.showMessageDialog(((String) ServerMessage.message).contains("killed by") ? Util.checkParent(this.parentGame.getParent()) : null, ServerMessage.message, "Message from server", JOptionPane.INFORMATION_MESSAGE));

                msg.start();

                break;

            case -2: // Determine if handshake is successful
                if (ServerMessage.message.equals("success")) {
                    System.out.println("Connection Accepted");

                } else {
                    Gdx.app.exit();
                    this.parentGame.rejectConnection((String) ServerMessage.message);
                }

                break;

            case -1: // Get server name and issue token
                String serverName = (String) ServerMessage.message;

                String serverToken = Communicator.getServerAuthToken(serverName);

                System.out.println("Token: " + serverToken);

                this.conn.write(-2, serverToken);

                break;

            case 0: // Assign UID
                this.ID = (Integer) ServerMessage.message;
                System.out.println("Current ID: " + this.ID);
                break;

            case 1: // Add player
                this.GLProcess.add(ServerMessage);
                break;

            case 10: // Update position
                if (!this.gameStart) {
                    break;
                }
                long[] cords = (long[]) ServerMessage.message;

                //System.out.println(cords[0] + " " + cords[1] + " " + (int) cords[3]);

                if (this.ID == cords[3]) {
                    break;
                }
                boolean found = false;

                for (Enemy aEnemyList : this.EnemyList) {
                    if (aEnemyList.getId() == cords[3]) {
                        aEnemyList.updateLocation(cords);
                        found = true;

                        break;
                    }
                }

                if (!found) {
                    ArrayList<long[]> a = new ArrayList<>();
                    a.add(cords);
                    this.GLProcess.add(Util.messageBuilder(1, a));
                }
                break;

            case 11: // Bullet

                try {
                    JSONObject data = new JSONObject((String) ServerMessage.message);

                    if (data.getInt("enemy") == this.ID) {
                        // weapon lookup here

                        if (this.player.damage(1)) {
                            System.out.println("Player dead");
                        }

                    }

                    if (data.getInt("attacker") != this.ID) {

                        for (Enemy aEnemyList : this.EnemyList) {
                            if (aEnemyList.getId() == data.getInt("attacker")) {

                                this.soundHashMap.get(data.getString("weapon")).play(Math.min(50 / this.dist(this.player.getX(), this.player.getY(), aEnemyList.getX(), aEnemyList.getY()), 1.0f));

                                break;
                            }
                        }


                    }

                } catch (Exception e) {
                    Main.errorQuit(e);
                }

                break;

            case 12: // Action log
                this.actions.add((String) ServerMessage.message);

                System.out.println(this.actions);

                break;

            case 13: // Players alive
                this.alive = (int) ServerMessage.message;

                break;

            case 14: // Set health
                this.player.setHealth((float) ServerMessage.message);

                break;

            case 15: // Remove player
                this.GLProcess.add(ServerMessage);
                break;

            case 16: // Set energy
                this.player.setEnergy((float) ServerMessage.message);

                break;

            case 19:
                this.GLProcess.add(ServerMessage);
                break;
            case 20:
                this.GLProcess.add(ServerMessage);
                break;
            case 21:
                this.GLProcess.add(ServerMessage);
                break;

        }
    }

    public float dist(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow((x1 - x2), 2) +  Math.pow((y1 - y2), 2));
    }

    public void pickupItem (Fixture f) {
        Body it = f.getBody();
        Item pickup = (Item) it.getUserData();

        this.conn.write(20, new long[] {(long) (it.getPosition().x * 1000), (long) (it.getPosition().y * 1000), pickup.getItemType()});
//        if (ItemList.itemType(pickup.getItemType()) == "weapon") {
//            if
//        }
            conn.write(20, new long[] {(long) (it.getPosition().x * 1000), (long) (it.getPosition().y * 1000), pickup.getItemType()});

        this.itemQueue.add((Item) it.getUserData());
        // ADD ITEM TO INV

        // ADD SERVER COMMAND TO CHECK SHEIT


    }

    private void handleNetworking(float dt){
        if (this.networkConnected && this.gameStart) {

            this.dTotal += dt;

            if (this.dTotal >= HUBGMain.SYNC_INTERVAL) {
                this.dTotal -= HUBGMain.SYNC_INTERVAL;

                if (this.ox != this.player.b2body.getPosition().x || this.oy != this.player.b2body.getPosition().y || this.or != this.getCameraRotation()) {
                    this.conn.write(10, new long[]{(long) (this.player.b2body.getPosition().x * 1000f), (long) (this.player.b2body.getPosition().y * 1000), (long) (this.getCameraRotation() * 1000f), this.ID});
                }

                this.ox = this.player.b2body.getPosition().x;
                this.oy = this.player.b2body.getPosition().y;
                this.or = this.player.b2body.getAngle();
            }
        }

        if (!this.GLProcess.isEmpty()) {
            try {
                Message pMessage = this.GLProcess.take();

                switch (pMessage.type) {
                    case 1:
                        System.out.println("Start game:" + pMessage.message);

                        for (long[] p : (ArrayList<long[]>) pMessage.message) {
                            System.out.println(p[3]);

                            if (p[3] == this.ID) {

                                if (this.player == null) {
                                    this.player = new Player(this.world, this, p);
                                    this.conn.write(14, null);
                                    this.conn.write(16, null);
                                    this.gamecam.position.x = this.player.b2body.getPosition().x;
                                    this.gamecam.position.y = this.player.b2body.getPosition().y;
                                    this.gamecam.rotate(p[2] * MathUtils.radiansToDegrees);

                                    this.gameHUD = new HUD(this.main.batch, this.staticPort, this.player, this, HUBGGame.latoFont);
                                    this.world.setContactListener(new collisionListener(this.gameHUD));
                                    Gdx.input.setInputProcessor(new customInputProcessor(this.gameHUD));
                                    this.connecting = false;
                                }

                            } else if (!this.hasEnemy((int) p[3])) {
                                this.EnemyList.add(new Enemy(this.world, this, "Karl", p));
                            }
                        }

                        this.gameStart = true;

                        if (this.player == null) {
                            Main.errorQuit("Player not found");
                        }

                        break;

                    case 15: // Remove player

                        for (int i = 0; i < this.EnemyList.size(); i++) {
                            if (this.EnemyList.get(i).getId() == (int) pMessage.message) {
                                this.world.destroyBody(this.EnemyList.get(i).b2body);
                                this.EnemyList.remove(i);
                                break;
                            }
                        }

                        break;

                    case 19:
                        ConcurrentHashMap<Long, ArrayList<long[]>> itemHashmap = (ConcurrentHashMap<Long, ArrayList<long[]>>) pMessage.message;
                        System.out.println(itemHashmap);
                        for (Map.Entry<Long, ArrayList<long[]>> entry: itemHashmap.entrySet()) {
                            for (long[] position : entry.getValue()) {
                                System.out.println((float) position[0]/1000 + " " +  (float) position[1]/1000 + " " + entry.getKey().intValue());

                                this.displayItems.add(new Item((float) position[0]/1000,  (float) position[1]/1000, entry.getKey().intValue(), this.world));
                            }
                        }
                        break;

                    case 20:
                        System.out.println(pMessage.type);
                        Boolean res = (Boolean) pMessage.message;

                        if (this.itemQueue.size() != 0) {
                            Item processingItem = this.itemQueue.take();
                            this.displayItems.remove(processingItem);
                            this.world.destroyBody(processingItem.body);

                            if (res) {

                            }
                        }

                        break;

                    case 21:
                        long[] target = (long[]) pMessage.message;
                        Item finder;

                        System.out.println("asdasdasd " + Arrays.toString(target));
                        for (int i = 0; i < this.displayItems.size(); i++) {
                            finder = this.displayItems.get(i);

                            if ((long) (finder.body.getPosition().x * 1000) == target[0] && (long) (finder.body.getPosition().y * 1000) == target[1] && finder.getItemType() == target[2]) {
                                this.displayItems.remove(i);
                                this.world.destroyBody(finder.body);
                                break;
                            }
                        }

                        for (Item anItemQueue : this.itemQueue) {
                            finder = anItemQueue;

                            if ((long) (finder.body.getPosition().x * 1000) == target[0] && (long) (finder.body.getPosition().y * 1000) == target[1] && finder.getItemType() == target[2]) {
                                this.itemQueue.remove(finder);
                                this.world.destroyBody(finder.body);
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

        for (Enemy e : this.EnemyList) {
            if (e.getId() == id) {
                return true;
            }
        }

        return false;
    }

    public int calculateBullet (float range) {
        range += this.player.getWidth();
        this.closestFraction = 99999;
        this.raycastPoint = new Vector2(this.player.getLocation().x + range * MathUtils.cos(this.player.getAngle()), this.player.getLocation().y + range * MathUtils.sin(this.player.getAngle()));
        this.raycastID = -1;

        RayCastCallback callback = (fixture, point, normal, fraction) -> {
            if ( fraction < this.closestFraction && (int) fixture.getUserData() != -1 && (int) fixture.getUserData() > -1000) {
                this.closestFraction = fraction;
                this.raycastPoint.set(point);
                this.raycastID = (Integer) fixture.getUserData();
            }

            return 1;
        };

        this.world.rayCast(callback, this.player.getLocation(), new Vector2(this.player.getLocation().x + range * MathUtils.cos(this.player.getAngle()), this.player.getLocation().y + range * MathUtils.sin(this.player.getAngle())));

        return this.raycastID;
    }

    public float getCameraRotation() {
        return MathUtils.atan2(this.gamecam.up.y, this.gamecam.up.x);
    }

    public void handleInput(float dt) {
        this.player.b2body.setLinearVelocity(0, 0);
        Vector2 movement = this.player.b2body.getPosition();
        float r = 0;

        Vector2 impulse = new Vector2();

        if (!this.paused) {
            r = this.getCameraRotation();

            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                impulse.add(new Vector2((this.sprint ? 2 : 1) * -60 * this.player.b2body.getMass() * MathUtils.cos(r), (this.sprint ? 2 : 1) * -60 * this.player.b2body.getMass() * MathUtils.sin(r)));
            }

            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                impulse.add(new Vector2((this.sprint ? 2 : 1) * 60 * this.player.b2body.getMass() * MathUtils.cos(r), (this.sprint ? 2 : 1) * 60 * this.player.b2body.getMass() * MathUtils.sin(r)));
            }

            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                impulse.add(new Vector2(-200 * MathUtils.cos(r - MathUtils.PI / 2), -200 * this.player.b2body.getMass() * MathUtils.sin(r - MathUtils.PI / 2)));
            }

            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                impulse.add(new Vector2(-200 * MathUtils.cos(r + MathUtils.PI / 2), -200 * this.player.b2body.getMass() * MathUtils.sin(r + MathUtils.PI / 2)));
            }

            if (this.inWater) {
                impulse.scl(0.1f);
            }

            this.player.b2body.applyLinearImpulse(impulse, this.player.b2body.getWorldCenter(), true);

            r = 0;

            if (Gdx.input.isKeyPressed(Input.Keys.E)) {
                r -= 90 * dt;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
                r += 90 * dt;
            }
        }

        if (this.sprint && this.player.getEnergy() > 0) {
            this.player.decEnergy(0.1f);

            this.conn.write(16, this.player.getEnergy());

            if (this.gamecam.zoom > 0.6 * this.defaultZoom) {
                this.gamecam.zoom -= 0.01;
            }
        } else {
            if (this.gamecam.zoom < this.defaultZoom) {
                this.gamecam.zoom += 0.01;
            } else {

                if (!paused && (Gdx.input.isKeyPressed(Input.Keys.SPACE)) && gamecam.zoom < player.weapon.getScopeSize() / HUBGMain.PPM + defaultZoom) {
                    gamecam.zoom += 0.01;
                } else if (gamecam.zoom > defaultZoom && (!Gdx.input.isKeyPressed(Input.Keys.SPACE) || paused)) {
                    gamecam.zoom -= 0.01;
                }

            }
        }


        this.sprint = this.player.getEnergy() > 0 && !this.paused && (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));

        if (this.sprint) {
            if (!this.runningMusic.isPlaying()) {
                System.out.println("Playing super dank music");
                this.runningMusic.play();
            }

            if (this.rageTint < 0.3) {
                this.rageTint += 0.01;
            }
        } else {
            if (this.rageTint > 0) {
                this.rageTint -= 0.01;
            }

            this.runningMusic.stop();
        }

        this.gamecam.position.x = movement.x;
        this.gamecam.position.y = movement.y;
        this.gamecam.rotate(-r);

        this.player.b2body.setTransform(movement, this.getCameraRotation());

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            if (this.pausedLock) {
                System.out.println("TOGGLE");
                this.paused = !this.paused;
            }
            this.pausedLock = false;
        } else {
            this.pausedLock = true;
        }

        this.fireDelay += dt;
        if (this.fireDelay >= 0.3) {
            this.canShoot = true;
            this.fireDelay -= 0.3;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER) && this.canShoot) {
            this.soundHashMap.get(this.player.weapon.getCurrentWeapon()).play();

            int enemyID = this.calculateBullet(400);

            try {
                if (this.networkConnected) {
                    this.conn.write(11, new JSONObject() {
                        {
                            this.put("enemy", enemyID);
                            this.put("attacker", HUBGGame.this.ID);
                            this.put("weapon", HUBGGame.this.player.weapon.getCurrentWeapon());
                        }
                    }.toString());
                }
            } catch (Exception e) {
                Main.errorQuit(e);
            }

            this.canShoot = false;
            this.shoot = true;
        } else {
            this.shoot = false;
        }


    }

    public void update(float dt) {
        this.handleNetworking(dt);

        try {
            this.inWater = this.displayLayer.getCell((int) (this.player.b2body.getPosition().x / 10), (int) (this.player.b2body.getPosition().y / 10)).getTile().getId() == 208;
        } catch (Exception e) {

        }


        if (this.gameStart) {

            //System.out.println(player.b2body.getPosition().x + " " +player.b2body.getPosition().y);
            this.world.step(1/60f, 6, 2);

            this.handleInput(dt);
            this.player.update(dt);
            for (Enemy e : this.EnemyList) {
                e.step(dt);
            }

            this.gameHUD.update(this.staticPort);

            this.gamecam.update();
            this.renderer.setView(this.gamecam);
        }


    }

    @Override
    public void render(float delta) {
        this.update(delta);

        Gdx.gl.glClearColor(0, 0 ,1 ,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (this.gameStart && this.player != null) {

            this.renderer.render();

            this.main.batch.setProjectionMatrix(this.gamecam.combined);
            this.main.batch.begin();
            this.player.draw(this.main.batch);

            for (Enemy e : this.EnemyList) {
                e.draw(this.main.batch);
            }

            for (Item i : this.displayItems) {
                i.draw(this.main.batch);
            }

            this.main.batch.end();

            this.main.batch.setProjectionMatrix(this.staticcam.combined);


            // Rage
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            ShapeRenderer sr = new ShapeRenderer();
            sr.setColor(Color.WHITE);
            sr.setProjectionMatrix(this.staticcam.combined);

            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.setColor(new Color(255, 0, 0, this.rageTint));

            sr.rect(this.staticPort.getScreenWidth() / -2, this.staticPort.getScreenHeight() / -2, this.staticPort.getScreenWidth(), this.staticPort.getScreenHeight());
            sr.end();

            Gdx.gl.glDisable(GL20.GL_BLEND);


            this.main.batch.begin();
            int miniMapSize = 300;
            int minMapPadding = 10;
            this.main.batch.draw(this.miniMap, this.staticPort.getScreenWidth() / 2 - minMapPadding - miniMapSize, this.staticPort.getScreenHeight() / 2 - minMapPadding - miniMapSize, miniMapSize, miniMapSize);
            this.main.batch.end();

            //sr = new ShapeRenderer();
            sr.setProjectionMatrix(this.staticcam.combined);
            sr.setColor(Color.RED);
            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.rect(this.staticPort.getScreenWidth() / 2 - minMapPadding - miniMapSize + this.player.b2body.getPosition().x / 10000 * miniMapSize, this.staticPort.getScreenHeight() / 2 - minMapPadding - miniMapSize + this.player.b2body.getPosition().y / 10000 * miniMapSize, 5/2, 5/2, 5, 5, 1f, 1f, MathUtils.radiansToDegrees * this.player.getAngle());
            sr.end();

            this.main.batch.begin();

            HUBGGame.latoFont.getData().setScale(0.2f);
            HUBGGame.latoFont.draw(this.main.batch, String.format("X: %d | Y: %d | R: %d | Alive: %d", (int) this.player.b2body.getPosition().x - 5000, (int) this.player.b2body.getPosition().y - 5000, (int) this.normalizeAngle((this.player.b2body.getAngle() * -360 / (2 * Math.PI)) + 90), this.alive), this.staticPort.getScreenWidth() / 2 - minMapPadding - miniMapSize + 10, this.staticPort.getScreenHeight() / 2 - minMapPadding - miniMapSize + 20);

            for (int i = 0; i < this.actions.size(); i++) {
                HUBGGame.latoFont.draw(this.main.batch, this.actions.get(i), this.staticPort.getScreenWidth() / 2 - minMapPadding - miniMapSize + 10, this.staticPort.getScreenHeight() / 2 - minMapPadding - miniMapSize - 10 - 15 * i);
            }

            int compassTicks = this.staticPort.getScreenWidth() / 200;
            int angle = (int) this.normalizeAngle((this.player.b2body.getAngle() * -360 / (2 * Math.PI)) + 90);

            if (compassTicks % 2 == 0) {
                compassTicks ++;
            }

            for (int compassX = compassTicks / -2; compassX <= compassTicks / 2; compassX ++) {
                Util.centerText(this.main.batch, HUBGGame.latoFont, 0.3f, this.formatAngle((int) this.normalizeAngle(angle - angle % 5 + compassX * 5)),compassX * 100 + angle % 5 * -20, this.staticPort.getScreenHeight() / 2 - 20);
            }

            this.purgeCounter = (this.purgeCounter + 1) % 1000;

            if (this.purgeCounter == 0 && this.actions.size() > 0) {
                this.actions.remove(0);
            }

            this.main.batch.end();

            if (this.shoot) {
                sr.setColor(Color.WHITE);
                sr.setProjectionMatrix(this.gamecam.combined);
                sr.begin(ShapeRenderer.ShapeType.Line);
                sr.line(this.player.getLocation().x, this.player.getLocation().y, this.raycastPoint.x, this.raycastPoint.y);
                sr.end();
            }

            this.gameHUD.draw(this.main.batch, this.staticcam);

            this.b2dr.render(this.world, this.gamecam.combined);
        }



        if (this.paused) {

            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            ShapeRenderer sr = new ShapeRenderer();
            sr.setColor(Color.WHITE);
            sr.setProjectionMatrix(this.staticcam.combined);

            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.setColor(new Color(0, 0, 0, 0.5f));

            sr.rect(this.staticPort.getScreenWidth() / -2, this.staticPort.getScreenHeight() / -2, this.staticPort.getScreenWidth(), this.staticPort.getScreenHeight());
            sr.end();

            Gdx.gl.glDisable(GL20.GL_BLEND);

            this.main.batch.begin();

            Util.centerText(this.main.batch, HUBGGame.latoFont, 0.5f, "PAUSED", 0, 0);

            this.main.batch.end();
        }

        if (this.connecting) {


            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            ShapeRenderer sr = new ShapeRenderer();
            sr.setColor(Color.WHITE);
            sr.setProjectionMatrix(this.staticcam.combined);

            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.setColor(new Color(0, 0, 0, 0.5f));

            sr.rect(this.staticPort.getScreenWidth() / -2, this.staticPort.getScreenHeight() / -2, this.staticPort.getScreenWidth(), this.staticPort.getScreenHeight());

            sr.end();

            Gdx.gl.glDisable(GL20.GL_BLEND);

            this.main.batch.begin();

            /*

            int size = Math.min(staticPort.getScreenWidth(), staticPort.getScreenHeight());

            main.batch.begin();
            main.batch.draw(loadingBG, staticPort.getScreenWidth() / -2, staticPort.getScreenHeight() / -2, size, size);

            latoFont.setColor(Color.BLACK); */

            Util.centerText(this.main.batch, HUBGGame.latoFont, 0.5f, "CONNECTING TO SERVER", this.staticPort.getScreenWidth() / 2, this.staticPort.getScreenHeight() / 2);

            //latoFont.setColor(Color.WHITE);

            this.main.batch.end();
        }
    }

    public String formatAngle(int angle) {

        switch (angle) {
            case 0:
                return "N";
            case 90:
                return "E";
            case 180:
                return "S";
            case 270:
                return "W";
            default:
                return "" + angle;
        }

    }

    public double normalizeAngle(double angle) {
        angle = angle % 360;

        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }

    public TextureAtlas getWeaponAtlas() {
        return this.weaponAtlas;
    }

    @Override
    public void resize(int width, int height) {
        this.gamePort.update(width, height);
        this.staticPort.update(width, height);
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
