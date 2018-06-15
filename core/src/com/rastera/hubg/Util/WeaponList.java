package com.rastera.hubg.Util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;
import java.util.Scanner;

public class WeaponList {
    public static HashMap<Integer, Integer> accuracy = new HashMap<>();
    public static HashMap<Integer, Integer> rounds = new HashMap<>();
    public static HashMap<Integer, Integer> rof = new HashMap<>();
    public static HashMap<Integer, Integer> reloadTime = new HashMap<>();
    public static HashMap<Integer, Integer> damage = new HashMap<>();
    public static HashMap<Integer, Integer> scope = new HashMap<>();
    public static HashMap<Integer, Texture> graphics = new HashMap<>();
    public static HashMap<Integer, Integer> ammoHashMap = new HashMap<>();

    public static Texture blank;  // A blank texture to "deselect" weapon. Replaces the current texture with a empty one

    public static void load() {
        blank = new Texture(Gdx.files.internal("blank.png"));
        Scanner loader = new Scanner(Gdx.files.internal("weaponData.txt").read());
        String[] data;
        int id;

        // Same as the Item List. However, this one loads weapons and contains a lot more information.

        loader.nextLine();

        while (loader.hasNext()){
            data = loader.nextLine().replaceAll("\n","").split(",");
            id = Integer.parseInt(data[0]);

            accuracy.put(id, Integer.parseInt(data[1]));
            rounds.put(id, Integer.parseInt(data[2]));
            rof.put(id, Integer.parseInt(data[3]));
            reloadTime.put(id, Integer.parseInt(data[4]));
            damage.put(id, Integer.parseInt(data[5]));
            scope.put(id, Integer.parseInt(data[6]));
            graphics.put(id, new Texture(Gdx.files.internal(data[7])));
            ammoHashMap.put(id, 0);
        }
    }
}
