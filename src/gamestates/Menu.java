package gamestates;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import main.Game;
import ui.MenuButton;
import ultiz.LoadSave;

public class Menu extends State implements Statemethods {
	
	private MenuButton[] buttons = new MenuButton[4];
	private BufferedImage backgroundImg, backgroundImgBehind;
	private int menuX, menuY, menuWidth, menuHeight;

	public Menu(Game game) {
		super(game);
		loadButtons();
		// loadBackground();
		backgroundImgBehind = LoadSave.GetSpriteAtlas(LoadSave.MENU_BACKGROUND_IMG);
		
	}

	// private void loadBackground() {
	// 	backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.MENU_BACKGROUND);
	// 	menuWidth = (int) (backgroundImg.getWidth() * Game.SCALE); 
	// 	menuHeight = (int) (backgroundImg.getHeight() * Game.SCALE); 
	// 	menuX = Game.GAME_WIDTH / 2 - menuWidth / 2;
	// 	menuY = (int) (45 * Game.SCALE);
	// }

	private void loadButtons() {
		buttons[0] = new MenuButton(Game.GAME_WIDTH / 2, (int) (160 * Game.SCALE), 0, Gamestate.PLAYING);
		buttons[1] = new MenuButton(Game.GAME_WIDTH / 2, (int) (230 * Game.SCALE), 1, Gamestate.OPTIONS);
		buttons[2] = new MenuButton(Game.GAME_WIDTH / 2, (int) (300 * Game.SCALE), 2, Gamestate.QUIT);
		buttons[3] = new MenuButton(Game.GAME_WIDTH / 2, (int) (370 * Game.SCALE), 3, Gamestate.GUIDES);
	}

	@Override
	public void update() {
		for(MenuButton mb : buttons) 
			mb.update();
	}

	@Override
	public void draw(Graphics g) {

		g.drawImage(backgroundImgBehind, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
		// g.drawImage(backgroundImg, menuX, menuY, menuWidth, menuHeight, null);
		
		for(MenuButton mb : buttons)
			mb.draw(g);
		
	}	

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		for(MenuButton mb : buttons) {
			if(isIn(e, mb)) {
				mb.setMousePressed(true);
				break;
			}
		}
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		for(MenuButton mb : buttons) {
			if(isIn(e, mb)) {
				if (mb.isMousePressed())
					mb.applyGamestate();
				if (mb.getState() == Gamestate.PLAYING)
					game.getAudioPlayer().setLevelSong(game.getPlaying().getLevelManager().getLevelIndex());
				break;
			}
		}
		
		resetButtons();
		
	}

	private void resetButtons() {
		for(MenuButton mb : buttons)
			mb.resetBools();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		for(MenuButton mb : buttons)
			mb.setMouseOver(false);
		
		for(MenuButton mb : buttons)
			if(isIn(e, mb)) {
				mb.setMouseOver(true);
				break;
			}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
