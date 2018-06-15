package com.rastera.hubg.Util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;
import java.util.Scanner;

public class ItemList {
    // A class that stores everything about each item
    public static HashMap<Integer, Texture> itemGraphics = new HashMap<>();
    public static HashMap<Integer, String> itemName = new HashMap<>();
    public static HashMap<Integer, String> itemDescription = new HashMap<>();
    public static HashMap<Integer, String> itemType = new HashMap<>();


    public static void load() {
        Scanner loader = new Scanner(Gdx.files.internal("itemData.txt").read());
        String[] data;

        // Loading the data using a scanner and putting them into their hashmaps

        loader.nextLine();

        while (loader.hasNext()){
            data = loader.nextLine().replaceAll("\n","").split(",");

            itemGraphics.put(Integer.parseInt(data[0]), new Texture(Gdx.files.internal(data[3])));
            itemName.put(Integer.parseInt(data[0]), data[1]);
            itemDescription.put(Integer.parseInt(data[0]), data[2]);
            itemType.put(Integer.parseInt(data[0]), data[4]);
        }
    }
}




