package fr.ubx.poo.game;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import static fr.ubx.poo.game.WorldEntity.fromCode;


public class Parser{

    /**
     * start is the main methods to read a file and use the information
     * @param filePath is the position of the file in the folder tree
     * @return a array of Wolrdentity thanks to the method arrayToTable
     */
    public static WorldEntity[][] start(String filePath){
        ArrayList<ArrayList<Character>> level = parse(filePath);
        return arrayToTable(level);
    }

    /**
     * parser allow to parse a file of charcater who represent a level
     * @return an ArrayList of ArrayList with all the line of the file composed with characters
     */
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
            while((lineFile =br.readLine()) != null){ // each line of the file is turn into arraylist
                ArrayList<Character> line = new ArrayList<>();
                while(lineFile.length()!=0){
                    Character one = lineFile.charAt(0); // we take the first characater
                    line.add(one); // we add it to the arralist
                    lineFile= lineFile.substring(1); // we only keep characters on the files that we don't already read
                }
                lineNumber++;
                level.add(line); // level is an arraylist of arraylist who represents each line of the file
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

    /**
     * arrayToTable allow to transform an arrayList obtained with the parser into an array of WorldEntity
     * @param level an arrayList of character corresponding to the level
     * @return the array of WolrdEntitty of the level
     */
    private static WorldEntity[][] arrayToTable(ArrayList<ArrayList<Character>> level){
        int largeur = level.get(0).size();
        int longueur = level.size();
        WorldEntity[][] world = new WorldEntity[longueur][largeur];
        for(int i=0 ; i< longueur ; i++){
            for(int j=0 ; j<largeur ; j++){
                Optional<WorldEntity> entity = fromCode(level.get(i).get(j));
                world[i][j] = entity.get();
            }
        }
        return world;
    }

}
