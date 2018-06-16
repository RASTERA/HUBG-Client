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
import com.rastera.hubg.Sprites.*;
import com.rastera.hubg.Util.ItemList;
import com.rastera.hubg.Util.WeaponList;
import com.rastera.hubg.desktop.Communicator;
import com.rastera.Networking.Message;
import com.rastera.hubg.Scene.HUD;
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

    private OrthographicCamera gamecam; // Camera that follows the player
    private OrthographicCamera staticcam; // The camera that stays in place to draw ui
    private Viewport gamePort;
    private Viewport staticPort;
    private float defaultZoom;
    private OrthogonalTiledMapRenderer renderer; // The renderer for the map
    private TiledMapTileLayer displayLayer; // The ground layer
    private Box2DDebugRenderer b2dr; // A debug renderer to make the hit-boxes visible
    private World world; // The physics world

    private HUBGMain main;

    private HashMap<Integer, Sound> soundHashMap = new HashMap<>(); // A hashmap that contains all the sounds for the game
    private Music runningMusic;

    private Player player;
    public int ID;  // The current ID of the player
    private float dTotal = 0; // The total delta time, used to calculate when to send location update

    public Communicator conn;
    private ArrayList<String> actions = new ArrayList<>();
    private int alive = 0; // If the player is alive or not
    private int purgeCounter = 1;

    private boolean gameStart = false; // Some flags to allow parts of the game to know when to function
    private boolean paused = false;
    private boolean connecting = true;
    private boolean pausedLock = true;
    private boolean networkConnected = false;

    private ArrayList<Enemy> EnemyList = new ArrayList<>();
    private LinkedBlockingQueue<Message> GLProcess = new LinkedBlockingQueue<>();  // The queue that contains messages for the GL Thread to process.
    private HashMap<String, JSONObject> playerData;

    private float ox = -1; // The old location
    private float oy = -1;
    private float or = -1;
    private boolean inWater = false;  // If the player is in water or not
    private boolean sprint = false;
    private float rageTint = 0f;

    private float closestFraction; // The variables required for shooting
    private int raycastID; // Raycast ID is the id of the object hit
    private Vector2 raycastPoint; // The point of the object hit
    private boolean shoot = false;
    private boolean canShoot = true;
    private boolean scope = false;
    private float fireDelay = 0;

    // The data provided by the server, used to store player info to create player object
    public int[] weaponData;
    public int[] ammoInfo = {0, 0, 0};

    private com.rastera.hubg.desktop.Game parentGame;

    // Reloading
    private int reloadtime = 0;
    private boolean reloading = false;

    //MiniMap
    private Texture miniMap;
    private int miniMapTextureSize = 5760;
    private TextureRegion miniMapDisplay;

    // Item drops
    private LinkedList<Item> displayItems; // This is the item library, all of the item drops are stored here
    private LinkedBlockingQueue<Item> itemQueue; // The queue used to picked up items. A item is stored here while waiting for server reply

    //HUD
    private HUD gameHUD;

    public HUBGGame(HUBGMain main, com.rastera.hubg.desktop.Game parentGame) {
        this.main = main;
        this.parentGame = parentGame;

        // Loading the items and the weapons and initializing the lists
        ItemList.load();
        WeaponList.load();
        this.displayItems = new LinkedList<>();
        this.itemQueue = new LinkedBlockingQueue<>();

        // Creating the box2d world and the tilemap renderer for loading
        this.b2dr = new Box2DDebugRenderer();
        this.world = new World(new Vector2(0, 0), true);
        TmxMapLoader mapLoader = new TmxMapLoader();
        TiledMap map = mapLoader.load("hubg.tmx");
        this.renderer = new OrthogonalTiledMapRenderer(map, 5 / HUBGMain.PPM);  // The map is scaled down so simulate real world size
        this.displayLayer = (TiledMapTileLayer) map.getLayers().get(0);  // Getting the ground layer of the tilemap, used to check if the player is on land or not

        new B2WorldCreator(world, map); // Loads all the rectangle colliders

        // Loading the minimap Texture
        this.miniMap = new Texture(Gdx.files.internal("minimap.png"));


        // Import audio
        this.soundHashMap.put(-1001, Gdx.audio.newSound(Gdx.files.internal("sounds/shot.wav")));
        this.soundHashMap.put(-1002, Gdx.audio.newSound(Gdx.files.internal("sounds/shot.wav")));
        this.soundHashMap.put(-1003, Gdx.audio.newSound(Gdx.files.internal("sounds/shot.wav")));
        this.soundHashMap.put(-3000, Gdx.audio.newSound(Gdx.files.internal("sounds/reload.wav")));

        this.runningMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/running.wav"));

        // Loading the font and background
        HUBGGame.latoFont = new BitmapFont(Gdx.files.internal("fnt/Lato-Regular-64.fnt"), Gdx.files.internal("fnt/lato.png"), false);
        Texture loadingBG = new Texture(Gdx.files.internal("images/menu-background-2.png"));

        // Creating the cameras and viewports for the game
        this.gamecam = new OrthographicCamera();
        this.staticcam = new OrthographicCamera();
        this.gamePort = new ScreenViewport(this.gamecam);
        this.staticPort = new ScreenViewport(this.staticcam);

        // Rotating the camera to match the rotation of the box2d.
        this.gamecam.rotate(90);
        this.gamecam.update();

        //Server Loading
        Thread connect = new Thread(() -> {

            try {
                JSONObject address = com.rastera.hubg.desktop.Communicator.request(com.rastera.hubg.desktop.Communicator.RequestType.GET, null, com.rastera.hubg.desktop.Communicator.getURL(com.rastera.hubg.desktop.Communicator.RequestDestination.API) + "gameAddress");

                System.out.println(address);

                // If not in development mode then get the actual server address
                if (!com.rastera.hubg.desktop.Communicator.developmentMode) {
                    this.conn = new Communicator(address.getString("address"), address.getInt("port"), this);
                } else {
                    this.conn = new Communicator("localhost", 8080, this);
                }

                this.networkConnected = true;  // Set network connected to true to activate some network components

            } catch (Exception e) {  // If there is an issue, than quit the game
                e.printStackTrace();
                Gdx.app.exit();
                this.parentGame.rejectConnection("Unable to connect to server");
            }
        });

        // Starting the connection thread
        connect.start();

    }

    @Override
    public void show() {
        // Calculating the zoom for the given scale.
        this.gamecam.zoom = (float) (Math.pow(HUBGMain.PPM, -1) * 2);
        this.defaultZoom = this.gamecam.zoom;
        this.gamecam.update();
    }

    /**
     * Handle the the server updates. Some of the commands cannot be executed by a non GL bound thread so they are sent to the GLprocessing queue
     * @param ServerMessage The message sent by the server
     */
    public void CommandProcessor(final Message ServerMessage) {
        switch (ServerMessage.type) {
            case -3: // Message from the server, opening another JFrame

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

                // If the game haven't started, then don't update any location
                if (!this.gameStart) {
                    break;
                }

                long[] cords = (long[]) ServerMessage.message; // Casting the messages as a long array

                // Checking to make sure the ID isn't the local player
                if (this.ID == cords[3]) {
                    break;
                }
                boolean found = false;

                // Finding the enemy to be updated
                for (Enemy aEnemyList : this.EnemyList) {
                    if (aEnemyList.getId() == cords[3]) {
                        aEnemyList.updateLocation(cords);
                        found = true;
                        break;
                    }
                }

                // If not found, then it must be another player
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

                        if (this.player.damage(WeaponList.damage.get(data.getInt("weapon")))) {
                            System.out.println("Player dead");
                        }

                    }

                    if (data.getInt("attacker") != this.ID) {

                        for (Enemy aEnemyList : this.EnemyList) {
                            if (aEnemyList.getId() == data.getInt("attacker")) {
                                // Playing the sound
                                this.soundHashMap.get(data.getInt("weapon")).play(Math.min(50 / this.dist(this.player.getX(), this.player.getY(), aEnemyList.getX(), aEnemyList.getY()), 1.0f));
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
            case 19: // Add items
                this.GLProcess.add(ServerMessage);
                break;
            case 20: // Pickup item
                this.GLProcess.add(ServerMessage);
                break;
            case 21: // Remove tile item
                this.GLProcess.add(ServerMessage);
                break;
            case 22: // Add tile item
                this.GLProcess.add(ServerMessage);
                break;
            case 30: // Update the player data according to whats saved on the server
                if (player != null) {
                    player.playerWeapons = (int[]) ServerMessage.message;
                } else {
                    weaponData = (int[]) ServerMessage.message;
                }
                break;
            case 31:
                // Setting the right weapon for the enemy
                int[] data = (int[]) ServerMessage.message;
                System.out.println(Arrays.toString(data));
                for (Enemy aEnemyList : this.EnemyList) {
                    if (aEnemyList.getId() == data[0]) {
                        aEnemyList.weapon.setCurrentWeapon(data[1]);
                        break;
                    }
                }
                break;
            case 32: // Updating the ammo
                int[] dat = (int[]) ServerMessage.message;

                if (player == null) {
                    ammoInfo[0] = dat[0];
                    ammoInfo[1] = dat[1];
                    ammoInfo[2] = dat[2];
                } else {
                    player.ammo = dat[0];
                    player.gunAmmo[0] = dat[1];
                    player.gunAmmo[1] = dat[2];
                }

                break;
        }
    }

    // The hypot formula
    public float dist(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow((x1 - x2), 2) +  Math.pow((y1 - y2), 2));
    }

    /**
     * Method to pickup items and weapons
     * @param f The fixture of the item the player is picking up
     */
    public void pickupItem (Fixture f) {
        // Getting the body and then the Item
        Body it = f.getBody();
        Item pickup = (Item) it.getUserData();

        // Writing to the server and adding the item to the queue for checking
        this.conn.write(20, new long[] {(long) (it.getPosition().x * 1000), (long) (it.getPosition().y * 1000), pickup.getItemType()});
        this.itemQueue.add((Item) it.getUserData());
    }

    /**
     * Handles the server commands that requires a GL bound thread
     * @param dt Delta time
     */
    private void handleNetworking(float dt){

        // If the server is activated, than update location
        if (this.networkConnected && this.gameStart) {

            this.dTotal += dt;

            // If the sync interval is reached, send information
            if (this.dTotal >= HUBGMain.SYNC_INTERVAL) {
                this.dTotal -= HUBGMain.SYNC_INTERVAL;

                // See if there are any changes in location. If there are changes, then send data
                if (this.ox != this.player.b2body.getPosition().x || this.oy != this.player.b2body.getPosition().y || this.or != this.getCameraRotation()) {
                    this.conn.write(10, new long[]{(long) (this.player.b2body.getPosition().x * 1000f), (long) (this.player.b2body.getPosition().y * 1000), (long) (this.getCameraRotation() * 1000f), this.ID});
                }

                // Setting the old coordinates to the current cords
                this.ox = this.player.b2body.getPosition().x;
                this.oy = this.player.b2body.getPosition().y;
                this.or = this.player.b2body.getAngle();
            }
        }

        // Process the commands
        if (!this.GLProcess.isEmpty()) {
            try {
                Message pMessage = this.GLProcess.take();

                switch (pMessage.type) {
                    case -9000: // Clear gc
                        System.gc();
                        break;
                    case 1: // Starting the game
                        JSONObject positionJSON;
                        JSONObject user;
                        long[] position;

                        // Looping through the players recieved and creates enemies or current player
                        for (String p : (ArrayList<String>) pMessage.message) {
                            user = new JSONObject(p);

                            // Parsing JSON data
                            positionJSON = user.getJSONObject("position");
                            position = new long[] {positionJSON.getLong("x"), positionJSON.getLong("y"), positionJSON.getLong("r"), positionJSON.getLong("id")};

                            try {
                                // If the current id is the id of the id currently being processed, then make player
                                if (user.getInt("id") == this.ID) {
                                    if (this.player == null) { // If there are no players, than create stuff
                                        this.player = new Player(this.world, this, position);
                                        this.conn.write(14, null);
                                        this.conn.write(16, null);
                                        this.gamecam.position.x = this.player.b2body.getPosition().x;
                                        this.gamecam.position.y = this.player.b2body.getPosition().y;
                                        this.gamecam.rotate(position[2] * MathUtils.radiansToDegrees);

                                        this.gameHUD = new HUD(this.main.batch, this.staticPort, this.player, this, HUBGGame.latoFont);
                                        this.world.setContactListener(new collisionListener(this.gameHUD)); // Adding the custom processors for inputs and collision
                                        Gdx.input.setInputProcessor(new customInputProcessor(this.gameHUD));
                                        this.connecting = false;
                                        this.player.playerWeapons = weaponData;
                                        this.player.ammo = ammoInfo[0];
                                        this.player.gunAmmo[0] = ammoInfo[1];
                                        this.player.gunAmmo[1] = ammoInfo[2];
                                    }
                                } else if (!this.hasEnemy(user.getInt("id"))) { // Creating a new enemy
                                    this.EnemyList.add(new Enemy(this.world, this, user.getString("name"), position));
                                }
                            } catch (Exception e) {}
                        }

                        this.gameStart = true; // Starting the game

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
                    case 19: // Adding every single item that can be picked up
                        ConcurrentHashMap<Long, ArrayList<long[]>> itemHashmap = (ConcurrentHashMap<Long, ArrayList<long[]>>) pMessage.message;
                        System.out.println(itemHashmap);
                        for (Map.Entry<Long, ArrayList<long[]>> entry: itemHashmap.entrySet()) {
                            for (long[] positionData : entry.getValue()) {
                                System.out.println((float) positionData[0]/1000 + " " +  (float) positionData[1]/1000 + " " + entry.getKey().intValue());

                                this.displayItems.add(new Item((float) positionData[0]/1000,  (float) positionData[1]/1000, entry.getKey().intValue(), this.world));
                            }
                        }
                        break;

                    case 20: // Picking up item
                        Boolean res = (Boolean) pMessage.message;

                        if (this.itemQueue.size() != 0) {
                            Item processingItem = this.itemQueue.take();

                            if (res) { // If the request ro pick up was accepted
                                switch (processingItem.getItemType()) {
                                    case -1004: // Ammo
                                        player.ammo += 30;
                                        break;
                                    case -1005: // Health
                                        player.incHealth(30);
                                        this.conn.write(14, this.player.getHealth());
                                        break;
                                    case -1006: // Energy
                                        player.incEnergy(20);
                                        this.conn.write(16, this.player.getEnergy());
                                        break;
                                    default: // Weapon
                                        if (player.playerWeapons[0] == 0) {
                                            player.playerWeapons[0] = processingItem.getItemType();
                                        } else if (player.playerWeapons[1] == 0) {
                                            player.playerWeapons[1] = processingItem.getItemType();
                                        } else if (gameHUD.getBoxSelected() != 2) {
                                            player.ammo += player.gunAmmo[gameHUD.getBoxSelected()];
                                            player.gunAmmo[gameHUD.getBoxSelected()] = 0;
                                            conn.write(22, new long[] {player.playerWeapons[gameHUD.getBoxSelected()], (long) (player.b2body.getPosition().x * 1000), (long) (player.b2body.getPosition().y * 1000)});

                                            player.playerWeapons[gameHUD.getBoxSelected()] = processingItem.getItemType();
                                        } else {
                                            player.ammo += player.gunAmmo[0];
                                            player.gunAmmo[0] = 0;
                                            conn.write(22, new long[] {player.playerWeapons[0], (long) (player.b2body.getPosition().x * 1000), (long) (player.b2body.getPosition().y * 1000)});

                                            player.playerWeapons[0] = processingItem.getItemType();
                                        }
                                }

                                // Update information with server
                                conn.write(30, player.playerWeapons);
                                conn.write(32, new int[] {player.ammo, player.gunAmmo[0], player.gunAmmo[1]});
                            }
                        }
                        break;

                    case 21: // Finding and remvoing the item from the world
                        long[] target = (long[]) pMessage.message;
                        Item finder;

                        for (int i = 0; i < this.displayItems.size(); i++) {
                            finder = this.displayItems.get(i);

                            if ((long) (finder.body.getPosition().x * 1000) == target[0] && (long) (finder.body.getPosition().y * 1000) == target[1] && finder.getItemType() == target[2]) {
                                this.displayItems.remove(i);
                                this.world.destroyBody(finder.body);
                                break;
                            }
                        }
                        break;

                    case 22: // Adding the item to the world
                        long[] data = (long[]) pMessage.message;
                        displayItems.add(new Item((float) data[1] / 1000f, (float) data[2] / 1000f, (int) data[0], world));
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // If enemy is found
    public boolean hasEnemy(int id) {
        for (Enemy e : this.EnemyList) {
            if (e.getId() == id) {
                return true;
            }
        }

        return false;
    }

    // Calculate the raycast
    public int calculateBullet (float range) {
        range += this.player.getWidth(); // Range of the cast
        this.closestFraction = 99999; // We want the min distance
        this.raycastPoint = new Vector2(this.player.getLocation().x + range * MathUtils.cos(this.player.getAngle()), this.player.getLocation().y + range * MathUtils.sin(this.player.getAngle()));
        this.raycastID = -1; // The object hit

        // Callback after the ray cast is hit
        RayCastCallback callback = (fixture, point, normal, fraction) -> {
            if ( fraction < this.closestFraction && (int) fixture.getUserData() != -1 && (int) fixture.getUserData() > -1000) {
                this.closestFraction = fraction;
                this.raycastPoint.set(point);
                this.raycastID = (Integer) fixture.getUserData();
            }
            return 1;
        };

        // Ray case
        this.world.rayCast(callback, this.player.getLocation(), new Vector2(this.player.getLocation().x + range * MathUtils.cos(this.player.getAngle()), this.player.getLocation().y + range * MathUtils.sin(this.player.getAngle())));

        return this.raycastID;
    }

    public float getCameraRotation() {
        return MathUtils.atan2(this.gamecam.up.y, this.gamecam.up.x);
    }

    public void handleInput(float dt) {
        // Resetting the velocity
        this.player.b2body.setLinearVelocity(0, 0);
        Vector2 movement = this.player.b2body.getPosition();
        float r = 0;

        // Moving the robot using impulses and selecting weapons
        Vector2 impulse = new Vector2();

        if (!this.paused) {
            r = this.getCameraRotation();

            if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
                selectGun(0);
            }

            if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
                selectGun(1);
            }

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

        // Sprinting and scope
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

                scope = gamecam.zoom > defaultZoom;

                if (!paused && (Gdx.input.isKeyPressed(Input.Keys.SPACE)) && gamecam.zoom < player.weapon.getScopeSize() / HUBGMain.PPM + defaultZoom) {
                    gamecam.zoom += 0.01;
                } else if (gamecam.zoom > defaultZoom && (!Gdx.input.isKeyPressed(Input.Keys.SPACE) || paused)) {
                    gamecam.zoom -= 0.01;
                }

            }
        }

        this.sprint = this.player.getEnergy() > 0 && !this.paused && (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT));

        // Sprinting
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

        // Update camera location
        this.gamecam.position.x = movement.x;
        this.gamecam.position.y = movement.y;
        this.gamecam.rotate(-r);

        this.player.b2body.setTransform(movement, this.getCameraRotation());

        // Pausing the game
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            if (this.pausedLock) {
                this.paused = !this.paused;
            }
            this.pausedLock = false;
        } else {
            this.pausedLock = true;
        }

        // Firing weapon
        this.fireDelay += dt*1000;

        if (reloading) {
            reloadtime += dt * 1000;
        }

        if (player.weapon.active) {

            if (reloadtime >= WeaponList.reloadTime.get(player.playerWeapons[gameHUD.getBoxSelected()])) {

                // Reload
                int currentammo = player.ammo;
                player.ammo = Math.max(0, player.ammo - WeaponList.rounds.get(player.playerWeapons[gameHUD.getBoxSelected()]) + player.gunAmmo[gameHUD.getBoxSelected()]);
                if (player.ammo == 0) {
                    if (currentammo != 0) {
                        player.gunAmmo[gameHUD.getBoxSelected()] = Math.min(currentammo, WeaponList.rounds.get(player.playerWeapons[gameHUD.getBoxSelected()]));
                    }
                } else {
                    player.gunAmmo[gameHUD.getBoxSelected()] = WeaponList.rounds.get(player.playerWeapons[gameHUD.getBoxSelected()]);
                }

                reloading = false;
                reloadtime = 0;

                // Playing reload sound
                this.soundHashMap.get(-3000).play(1);

                conn.write(32, new int[] {player.ammo, player.gunAmmo[0], player.gunAmmo[1]}); // update server
            }

            if (Gdx.input.isKeyPressed(Input.Keys.R) && player.gunAmmo[gameHUD.getBoxSelected()] < WeaponList.rounds.get(player.playerWeapons[gameHUD.getBoxSelected()]) && player.ammo != 0) {
                if (!reloading) {
                    this.actions.add("Reloading weapon...");
                }

                reloading = true;
            }

            if (this.fireDelay >= player.weapon.getFireRate()) {
                this.canShoot = true;
            }
            // Shooting weapon
            if (Gdx.input.isKeyPressed(Input.Keys.ENTER) && this.canShoot && player.gunAmmo[gameHUD.getBoxSelected()] > 0) {
                this.fireDelay = 0;
                player.gunAmmo[gameHUD.getBoxSelected()] -= 1;

                conn.write(32, new int[] {player.ammo, player.gunAmmo[0], player.gunAmmo[1]});

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
                        }.toString()); // Update server with hits
                    }
                } catch (Exception e) {
                    Main.errorQuit(e);
                }

                this.canShoot = false;
                this.shoot = true;
            } else {
                this.shoot = false;
            }
        } else {
            reloadtime = 0;
            reloading = false;
        }
    }

    // Switching weapons
    public void selectGun(int boxID) {
        if (player.weapon.getCurrentWeapon() != player.playerWeapons[boxID]) {
            player.weapon.setCurrentWeapon(player.playerWeapons[boxID]);
            conn.write(31, new int[] {ID, player.playerWeapons[boxID]});

            gameHUD.a.active = boxID == 0;
            gameHUD.b.active = boxID == 1;
        }
    }

    public void update(float dt) {
        this.handleNetworking(dt);

        try {
            // Checks if the player is in water
            this.inWater = this.displayLayer.getCell((int) (this.player.b2body.getPosition().x / 10), (int) (this.player.b2body.getPosition().y / 10)).getTile().getId() == 208;
        } catch (Exception e) {

        }

        if (this.gameStart) {
            // Update everything in the game
            this.world.step(1/60f, 6, 2); // Number of times to calculate physics

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

        // Clearing the screen
        Gdx.gl.glClearColor(0, 0 ,1 ,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (this.gameStart && this.player != null) {
            // Rendering the map
            this.renderer.render(new int[] {0});

            // Drawing the player and the enemy
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

            this.renderer.render(new int[] {2}); // Render the second layer that supposed to cover the player

            // Rage
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            ShapeRenderer sr = new ShapeRenderer();

            sr.setColor(Color.WHITE);
            sr.setProjectionMatrix(this.staticcam.combined);
            sr.begin(ShapeRenderer.ShapeType.Filled); // Draws a filled rectangle that covers the screen for red shift effect
            sr.setColor(new Color(255, 0, 0, this.rageTint));
            sr.rect(this.staticPort.getScreenWidth() / -2, this.staticPort.getScreenHeight() / -2, this.staticPort.getScreenWidth(), this.staticPort.getScreenHeight());
            sr.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);

            // Drawing the minimap
            this.main.batch.begin();
            int miniMapSize = 300;
            int minMapPadding = 10;
            this.main.batch.draw(this.miniMap, this.staticPort.getScreenWidth() / 2 - minMapPadding - miniMapSize, this.staticPort.getScreenHeight() / 2 - minMapPadding - miniMapSize, miniMapSize, miniMapSize);
            this.main.batch.end();

            // Drawing the red square on the minimap
            sr.setColor(Color.RED);
            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.rect(this.staticPort.getScreenWidth() / 2 - minMapPadding - miniMapSize + this.player.b2body.getPosition().x / 10000 * miniMapSize, this.staticPort.getScreenHeight() / 2 - minMapPadding - miniMapSize + this.player.b2body.getPosition().y / 10000 * miniMapSize, 5/2, 5/2, 5, 5, 1f, 1f, MathUtils.radiansToDegrees * this.player.getAngle());
            sr.line(this.staticPort.getScreenWidth() / 2 - minMapPadding - miniMapSize + this.player.b2body.getPosition().x / 10000 * miniMapSize + 1, this.staticPort.getScreenHeight() / 2 - minMapPadding - miniMapSize + this.player.b2body.getPosition().y / 10000 * miniMapSize + 1, this.staticPort.getScreenWidth() / 2 - minMapPadding - miniMapSize + this.player.b2body.getPosition().x / 10000 * miniMapSize + 10 * (float) Math.cos(this.player.b2body.getAngle()) + 1, this.staticPort.getScreenHeight() / 2 - minMapPadding - miniMapSize + this.player.b2body.getPosition().y / 10000 * miniMapSize + 10 * (float) Math.sin(this.player.b2body.getAngle()) + 1);
            sr.end();

            // Drawing the position and the compass for the UI
            main.batch.setProjectionMatrix(this.staticcam.combined);
            this.main.batch.begin();

            HUBGGame.latoFont.getData().setScale(0.2f);
            HUBGGame.latoFont.draw(this.main.batch, String.format("X: %d | Y: %d | R: %d | Alive: %d | Ammo: %d", (int) this.player.b2body.getPosition().x - 5000, (int) this.player.b2body.getPosition().y - 5000, (int) this.normalizeAngle((this.player.b2body.getAngle() * -360 / (2 * Math.PI)) + 90), this.alive, this.player.ammo), this.staticPort.getScreenWidth() / 2 - minMapPadding - miniMapSize + 10, this.staticPort.getScreenHeight() / 2 - minMapPadding - miniMapSize + 20);

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

            // Drawing a weapon related graphics
            if (this.shoot) {
                drawScopeLine(sr, Color.WHITE);
            }

            if (this.scope && this.player != null && this.player.weapon.getCurrentWeapon() != 0) {
                this.calculateBullet(400);
                drawScopeLine(sr, Color.RED);
            }

            this.gameHUD.draw(this.main.batch, this.staticcam);

            //this.b2dr.render(this.world, this.gamecam.combined);
        }


        // Drawing the paused and the connecting screen
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

            this.main.batch.end();
        }
    }

    // Drawing the scope line for the shot
    public void drawScopeLine(ShapeRenderer sr, Color color) {
        sr.setColor(color);
        sr.setProjectionMatrix(this.gamecam.combined);
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.line(this.player.getLocation().x, this.player.getLocation().y, this.raycastPoint.x, this.raycastPoint.y);
        sr.end();
    }

    // Formatting the 90 degree angle markers
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

    // Making the angle positive since libgdx angle has a range of -pi <= 0 <= pi
    public double normalizeAngle(double angle) {
        angle = angle % 360;

        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }

    @Override
    public void resize(int width, int height) {
        // Resizing the viewports to display the graphics properly
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
        renderer.dispose();
        world.dispose();
        runningMusic.dispose();
        miniMap.dispose();
        this.parentGame.exitGame();
    }
}
