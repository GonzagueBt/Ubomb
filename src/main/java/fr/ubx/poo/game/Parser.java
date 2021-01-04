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
        switch (code) {
            case 'P' :
                return WorldEntity.Player;
            case '_' :
                return WorldEntity.Empty;
            case 'S' :
                return WorldEntity.Stone;
            case 'T':
                return WorldEntity.Tree;
            case 'B':
                return WorldEntity.Box;
            case 'K':
                return WorldEntity.Key;
            case 'W':
                return WorldEntity.Princess;
            case 'H':
                return WorldEntity.Heart;
            case '-':
                return WorldEntity.BombNumberDec;
            case '+':
                return WorldEntity.BombNumberInc;
            case '<' :
                return WorldEntity.BombRangeDec;
            case '>' :
                return WorldEntity.BombRangeInc;
            case 'n':
                return WorldEntity.DoorNextClosed;
            case 'V' :
                return WorldEntity.DoorPrevOpened;
            case 'M': //provisoire
                return WorldEntity.Monster;
            default:
                return null;
        }
    }

    public static void main(String[] args){
        String level = "src/main/resources/sample/level1.txt";
        WorldEntity[][] table = start(level);
        for(int i=0 ; i<12; i++){
            for(int j=0 ; j<12 ; j++){
                System.out.print(table[i][j]);
            }
            System.out.println();
        }
    }
}
