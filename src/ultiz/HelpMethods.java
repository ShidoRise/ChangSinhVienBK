package ultiz;

import static ultiz.Constants.EnemyConstants.*;
import static ultiz.Constants.ObjectConstants.*;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entities.Professor;
import main.Game;
import objects.Book;
import objects.GameAddict;
import objects.Girl;
import objects.Projectile;
import objects.TestPosition;

public class HelpMethods {
	
	public static boolean CanMoveHere(float x, float y, float width, float height, int[][] lvlData) {
		
		if (!IsSolid(x, y, lvlData)) 
			if (!IsSolid(x + width, y + height, lvlData))
				if (!IsSolid(x + width, y, lvlData))
					if (!IsSolid(x, y + height, lvlData))
						return true;
		return false;
		
	}
	
	private static boolean IsSolid(float x, float y, int[][] lvlData) {
		int maxWdith = lvlData[0].length * Game.TILES_SIZE;
		if (x < 0 || x >= maxWdith)
			return true;
		if (y < 0 || y >= Game.GAME_HEIGHT)
			return true;
		
		float xIndex = x / Game.TILES_SIZE;
		float yIndex = y / Game.TILES_SIZE;
		return IsTileSolid((int)xIndex, (int) yIndex, lvlData);
	}

	public static boolean IsTileSolid(int xTile, int yTile, int[][] lvlData){
		int value = lvlData[yTile][xTile];
		
		if (value >= 50 || value < 0 || value != 3)
			return true;
		return false;
	}

	public static boolean IsProjectileHittingLevel(Projectile p, int[][] lvlData){
		return IsSolid(p.getHitbox().x+p.getHitbox().width/2, p.getHitbox().y+p.getHitbox().height/2, lvlData);
	}
	
	public static float GetEntityXNextToWall(Rectangle2D.Float hitbox, float xSpeed) {
		int currentTile = (int) ((hitbox.x) / Game.TILES_SIZE);
		if ( xSpeed > 0) {
			// Right
			int tileXPos = currentTile * Game.TILES_SIZE;
			int xOffset = (int) (Game.TILES_SIZE - hitbox.width);
			return tileXPos + xOffset - 1;
		} else {
			// Left
			return currentTile * Game.TILES_SIZE;
		}
		
	}
	
	public static float GetEntityYPosUnderRoofOrAboveFloor(Rectangle2D.Float hitbox, float airSpeed) {
		int currentTile = (int) ((hitbox.y+hitbox.height) / Game.TILES_SIZE);
		if (airSpeed > 0) {
			// Falling - touching floor
			int tileYPos = currentTile * Game.TILES_SIZE;
			int yOffSet = (int) (Game.TILES_SIZE - hitbox.height);
			return tileYPos + yOffSet - 1;
		} else {
			// Jumping 
			return currentTile * Game.TILES_SIZE;
		}
		
	}
	
	public static boolean IsEntityOnFloor(Rectangle2D.Float hitbox, int[][] lvlData) {
		// Check the pixel below bottomleft and bottomright
		if (!IsSolid(hitbox.x, hitbox.y + hitbox.height + 1, lvlData)) 
			if (!IsSolid(hitbox.x + hitbox.width, hitbox.y + hitbox.height + 1, lvlData))
				return false;
		
		return true;
			
	}
	
	public static boolean IsFloor(Rectangle2D.Float hitbox, float xSpeed, int[][] lvlData){
		if (xSpeed > 0)
			return IsSolid(hitbox.x + hitbox.width, hitbox.y + hitbox.height + 1, lvlData);
		else
			return IsSolid(hitbox.x + xSpeed + xSpeed, hitbox.y + hitbox.height + 1, lvlData);
	}

	public static boolean CanGirlSeePlayer(int[][] lvlData, Rectangle2D.Float firstHitbox, Rectangle2D.Float secondHitbox, int yTile){
		int firstXTile = (int)(firstHitbox.x/Game.TILES_SIZE);
		int secondXTile = (int)(secondHitbox.x/Game.TILES_SIZE);
		if(firstXTile > secondXTile){
			return IsAllTilesClear(secondXTile, firstXTile, yTile, lvlData);
		}
		else{
			return IsAllTilesClear(firstXTile, secondXTile, yTile, lvlData);
		}
	}

	public static boolean IsAllTilesClear(int xStart, int xEnd, int y, int[][] lvlData){
		for(int i=0; i<xEnd-xStart;i++){
			if(IsTileSolid(xStart+i, y, lvlData))
				return false;
		}
		return true;
	}

	public static boolean IsAllTileWalkable(int xStart, int xEnd, int y, int[][] lvlData){
		if(IsAllTilesClear(xStart, xEnd, y, lvlData))
			for(int i = 0; i<xEnd-xStart;i++){
			
				if(!IsTileSolid(xStart + i, y+1, lvlData)){
					return false;
				}
			}
		return true;
	}

	public static boolean isSideClear(int[][] lvlData, Rectangle2D.Float firstHitbox, Rectangle2D.Float secondHitbox, int yTile){
		int firstXTile = (int)(firstHitbox.x/Game.TILES_SIZE);
		int secondXTile = (int)(secondHitbox.x/Game.TILES_SIZE);
		if(firstXTile > secondXTile){
			return IsAllTileWalkable(secondXTile, firstXTile, yTile, lvlData);
		}
		else{
			return IsAllTileWalkable(firstXTile, secondXTile, yTile, lvlData);
		}
	}

	public static int[][] GetLevelData(BufferedImage img) {
		int[][] lvlData = new int[img.getHeight()][img.getWidth()];
		for (int j = 0; j < img.getHeight(); j++) 
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getRed();
				if (value >= 50) 
					value = 0;
				lvlData[j][i] = value;
			}
		return lvlData;
		
	}

	public static ArrayList<Professor> GetProfessors(BufferedImage img){
		ArrayList<Professor> list = new ArrayList<>();
		for (int j = 0; j < img.getHeight(); j++) 
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getGreen();
				if (value == PROFESSOR) 
					list.add(new Professor(i*Game.TILES_SIZE, j*Game.TILES_SIZE));
			}
		return list;
	}

	public static Point GetPlayerSpawn(BufferedImage img) {
		for (int j = 0; j < img.getHeight(); j++) 
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getGreen();
				if (value == 100) 
					return new Point(i * Game.TILES_SIZE, j * Game.TILES_SIZE);
			}
		return new Point(Game.TILES_SIZE, Game.TILES_SIZE);
	}

	public static ArrayList<Book> GetBooks(BufferedImage img){
		ArrayList<Book> list = new ArrayList<>();
		for (int j = 0; j < img.getHeight(); j++) 
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getBlue();
				if (value == KNOWLEDGE_BOOK) 
					list.add(new Book(i * Game.TILES_SIZE, j * Game.TILES_SIZE, value));
			}
		return list;
	}

	public static ArrayList<TestPosition> GetTestPos(BufferedImage img){
		ArrayList<TestPosition> list = new ArrayList<>();
		for (int j = 0; j < img.getHeight(); j++) 
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getBlue();
				if (value == TEST_POSITION) 
					list.add(new TestPosition(i * Game.TILES_SIZE, j * Game.TILES_SIZE, value));
			}
		return list;
	}

	public static ArrayList<GameAddict> GetTraps(BufferedImage img){
		ArrayList<GameAddict> list = new ArrayList<>();
		for (int j = 0; j < img.getHeight(); j++) 
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getBlue();
				if (value == TRAP) 
					list.add(new GameAddict(i * Game.TILES_SIZE+15, j * Game.TILES_SIZE+15, value));
			}
		return list;
	}

	public static ArrayList<Girl> GetGirls(BufferedImage img){
		ArrayList<Girl> list = new ArrayList<>();
		for (int j = 0; j < img.getHeight(); j++) 
			for (int i = 0; i < img.getWidth(); i++) {
				Color color = new Color(img.getRGB(i, j));
				int value = color.getBlue();
				if (value == GIRL_LEFT || value == GIRL_RIGHT) 
					list.add(new Girl(i * Game.TILES_SIZE, j * Game.TILES_SIZE, value));
			}
		return list;
	}

}
