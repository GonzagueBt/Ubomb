package fr.ubx.poo.game;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class Parser{

    public static WorldEntity[][] start(String filePath){
        ArrayList<ArrayList<Character>> level = parse(filePath);
        return arrayToTable(level);
    }

    private static ArrayList<ArrayList<Character>> parse(String filePath){
        BufferedReader br = null;
        FileReader fr;
        int lineNumber = 1;
        try {
            fr = new FileReader(filePath);
            br = new BufferedReader(fr);
            // Reads the first line and puts everything in the String lineFile
            String lineFile;
            ArrayList<ArrayList<Character>> level = new ArrayList<>();
            while((lineFile =br.readLine()) != null){
                ArrayList<Character> line = new ArrayList<>();
                while(lineFile.length()!=0){
                    Character one = lineFile.charAt(0);
                    line.add(one);
                    lineFile= lineFile.substring(1);
                }
                lineNumber++;
                level.add(line);
            }
            return level;
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException(
                    "Exception during the read of the file " + filePath + " at line " + lineNumber);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static WorldEntity[][] arrayToTable(ArrayList<ArrayList<Character>> level){
        int largeur = level.get(0).size();
        int longueur = level.size();
        WorldEntity[][] world = new WorldEntity[longueur][largeur];
        for(int i=0 ; i< longueur ; i++){
            for(int j=0 ; j<largeur ; j++){
                WorldEntity entity = processParse(level.get(i).get(j));
                world[i][j] = entity;
            }
        }
        return world;
    }

    private static WorldEntity processParse (char code){
        return switch (code) {
            case 'P' -> WorldEntity.Player;
            case '_' -> WorldEntity.Empty;
            case 'S' -> WorldEntity.Stone;
            case 'T' -> WorldEntity.Tree;
            case 'B' -> WorldEntity.Box;
            case 'K' -> WorldEntity.Key;
            case 'W' -> WorldEntity.Princess;
            case 'H' -> WorldEntity.Heart;
            case '-' -> WorldEntity.BombNumberDec;
            case '+' -> WorldEntity.BombNumberInc;
            case '<' -> WorldEntity.BombRangeDec;
            case '>' -> WorldEntity.BombRangeInc;
            case 'n' -> WorldEntity.DoorCloseNext;
            case 'V' -> WorldEntity.DoorPrevOpened;
            case 'M' -> WorldEntity.Monster;
            default -> null;
        };
    }

}
