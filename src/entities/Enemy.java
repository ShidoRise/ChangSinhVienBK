package entities;

import static ultiz.HelpMethods.*;
import static ultiz.Constants.EnemyConstants.*;
import static ultiz.Constants.Directions.*;
import static ultiz.Constants.*;

import java.awt.geom.Rectangle2D;
import java.lang.Math;

import audio.AudioPlayer;
import main.Game;

public abstract class Enemy extends Entity {
    protected int enemyType;
        
    protected boolean firstUpdate = true;

    protected int walkDir = LEFT;
    protected int tileY;
    protected float attackDistance = Game.TILES_SIZE;
    protected boolean attackChecked;

    public Enemy(float x, float y, int width, int height, int enemyType){
        super(x, y, width, height);
        this.enemyType = enemyType;
        walkSpeed = Game.SCALE * 0.35f;
    }

    protected void firstUpdateCheck(int [][] lvlData){
        if(!IsEntityOnFloor(hitbox, lvlData)){
            inAir = true;
        }
        firstUpdate = false;
    }

    protected void updateInAir(int [][] lvlData){
        if(CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)){
            hitbox.y += airSpeed;
            airSpeed += GRAVITY;
        }
        else{
            inAir = false;
            hitbox.y=GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
            tileY = (int)(hitbox.y/Game.TILES_SIZE);
        }
    }

    protected void move(int [][] lvlData){
        float xSpeed=0;

        if(walkDir == LEFT){
            xSpeed = -walkSpeed;
        }
        else{
            xSpeed = walkSpeed;
        }
        if(CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData)){
            if(IsFloor(hitbox, xSpeed, lvlData)){
                hitbox.x += xSpeed;
                return;
            }
        }
        changeWalkDir();
    }
    
    protected void turnTowardsPlayer(Player player){
        if(player.hitbox.x>hitbox.x){
            walkDir=RIGHT;
        }
        else{
            walkDir=LEFT;
        }
    }

    protected boolean canSeePlayer(int [][] lvlData, Player player){
        int playerTileY = (int) (player.getHitbox().y / Game.TILES_SIZE);
        if(playerTileY == tileY){
            if(isPlayerInRange(player)){
                if(isSideClear(lvlData, hitbox, player.hitbox, tileY)) return true;
            }
        }
        return false;
    }

    protected boolean isPlayerInRange(Player player) {
        int absValue =(int) Math.abs(player.hitbox.x - hitbox.x);
        return absValue <= attackDistance*5;
    }

    protected boolean isPlayerCloseForAttack(Player player){
        int absValue =(int) Math.abs(player.hitbox.x - hitbox.x);
        return absValue <= attackDistance;
    }

    protected void newState(int enemyState){
        this.state = enemyState;
        aniTick = 0;
        aniIndex = 0;
    }

    protected void CheckEnemyHit(Rectangle2D.Float attackBox, Player player) {
        if(attackBox.intersects(player.getHitbox())) {
            player.getPlaying().getGame().getAudioPlayer().playEffect(AudioPlayer.TOUCH_PROFESSOR_FX);
            if (player.getHealth() > 1) 
                player.blinkAni();
            player.changeHealth(GetEnemyDmg(enemyType));
            attackChecked = true;
        }
        
    }

    protected void updateAnimationTick() {
        aniTick++;
        if(aniTick >= ANI_SPEED){
            aniTick = 0;
            aniIndex++;
            if(aniIndex >= GetSpriteAmount(enemyType, state)){
                aniIndex = 0;
                if(state == ATTACK) state = IDLE_LEFT;
            }
        }
    }
    

    protected void changeWalkDir() {
        if(walkDir == LEFT){
            walkDir = RIGHT;
            state = RUN_RIGHT;
        } 
        else{
            walkDir = LEFT;
            state = RUN_LEFT;
        } 
    }

    public void resetEnemy() {
        hitbox.x = x;
        hitbox.y = y;
        firstUpdate = true;
        airSpeed = 0;
    }

}
