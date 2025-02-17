package com.knightgame;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.Viewport;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.virtual.VirtualButton;
import com.almasb.fxgl.physics.PhysicsComponent;
import java.util.Random;
import javafx.scene.text.Text;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import java.util.Map;
import static com.almasb.fxgl.dsl.FXGL.*;

public class KnightGameApp extends GameApplication 
{
	Entity player;
	
    @Override
    protected void initSettings(GameSettings settings) 
    {
        settings.setWidth(600);	
        settings.setHeight(800);
        settings.setTitle("Hop Knight");
        settings.setDeveloperMenuEnabled(true);
        settings.setVersion("");
    }

    @Override
    protected void initInput() 
    {
    	getInput().addAction(new UserAction("Jump") 
    	{
            @Override
            protected void onActionBegin() 
            {
                player.getComponent(PlayerComponent.class).jump();
                play("Jump.wav");
                
            }
            
        }, KeyCode.SPACE, VirtualButton.UP);
    	
    	
    	getInput().addAction(new UserAction("Left") 
    	{
            @Override
            protected void onAction() 
            {
                player.getComponent(PlayerComponent.class).turnLeft();
            }
        }, KeyCode.A, VirtualButton.LEFT);
    	
    	getInput().addAction(new UserAction("Right") 
    	{
            @Override
            protected void onAction() 
            {
                player.getComponent(PlayerComponent.class).turnRight();
            }
            
        }, KeyCode.D, VirtualButton.RIGHT);
    }    
    
    @Override
    protected void initPhysics() 
    {
    	getPhysicsWorld().setGravity(0,1000);
    	Viewport viewport = getGameScene().getViewport();
    
    	onCollisionOneTimeOnly(EntityType.PLAYER, EntityType.SPAWN_PLATFORM, (player, platform) ->
    	{
    		Random rand = new Random(System.currentTimeMillis());
    		int x = rand.nextInt(getAppWidth()-144);
    		if(x < 0)
    		{
    			x = rand.nextInt(getAppWidth()-144);
    		}
    		spawnPlatform(x, player.getY()-100);
    		inc("Score", 1);
    		
    		int xSpike = rand.nextInt(getAppWidth()-144);
    		if(xSpike < 0)
    		{
    			xSpike = rand.nextInt(getAppWidth()-144);;
    		}
    		if(rand.nextInt(3) == 0)
    		{
    			int spikeCount = 2;
    			
    			for(int i = 0; i < spikeCount; i++) 
    			{
    				spawnSpike(xSpike + (i*33) ,player.getCenter().getY()-getAppHeight());	
    			}	
    		}
        	viewport.setBounds(0,-Integer.MAX_VALUE , getAppWidth(), (int)platform.getY()+400);
    	});
    	
    	onCollision(EntityType.PLAYER, EntityType.WALL, (player, wall) ->
    	{
    		if(player.getScaleX() > 0)
    		{
    			 player.getComponent(PlayerComponent.class).moveLeft();
    		}
    		else
    		{
    			 player.getComponent(PlayerComponent.class).moveRight();
    		}
    	});	
    	
    	onCollision(EntityType.PLAYER, EntityType.SPIKE, (player, spike) ->
    	{
    		restart();
    	});
    	
    	onCollisionOneTimeOnly(EntityType.PLAYER, EntityType.PLATFORM, (player, plat) ->
    	{	
    		showMessage("How to play \n\n"
    				+ "A / D : change jump direction while standing\n\n"
    				+ "SPACE : Jump forward and backward (up to 3 times)\n\n"
    				+ "SPIKE RAIN !!!!");
    	});
    	
    	onCollisionBegin(EntityType.RAND_PLATFORM, EntityType.SPIKE, (plat, spike) ->
    	{
    		spike.removeFromWorld();
    	});
    }
    
    public void spawnPlatform(double x, double y)
    {
    	getGameWorld().spawn("Rand_Platform", x, y);
    	getGameWorld().spawn("Rand_Spawner", x, y);
    }
    
    public void spawnSpike(double x, double y)
    {
    	getGameWorld().spawn("Spike", x, y);
    }
    
    @Override
    protected void onPreInit() 
    {
        getSettings().setGlobalMusicVolume(0.25);
        loopBGM("bgm1.mp3");
    }
    
    @Override 
    protected void initGameVars(Map<String, Object> vars)
    {
    	 vars.put("Score", 0);
    }
    
    @Override
    protected void onUpdate(double tpf) 
    {
    	Viewport viewport = getGameScene().getViewport();
    	if (player.getY() > viewport.getVisibleArea().getMaxY())
    	{
    		restart();
    	}
    }

    @Override
    protected void initUI() 
    {
        Text textPixels = new Text();
        textPixels.setTranslateX(getAppCenter().getX());
        textPixels.setTranslateY(100);

        textPixels.textProperty().bind(getWorldProperties().intProperty("Score").asString());
        textPixels.scaleXProperty().setValue(8);
        textPixels.scaleYProperty().setValue(8);

        getGameScene().addUINode(textPixels);
    }

    public void restart()
    {
    	if(getGameWorld().getEntitiesByType(EntityType.SPIKE).size()>0)
    	{
    		getGameWorld().removeEntities(getGameWorld().getEntitiesByType(EntityType.SPIKE));
    	}
    	
    	Viewport viewport = getGameScene().getViewport();
    	player.getComponent(PlayerComponent.class).stop();
     	player.getComponent(PhysicsComponent.class).overwritePosition(new Point2D(10, 1000));
    	getGameWorld().removeEntities(getGameWorld().getEntitiesByType(EntityType.SPAWN_PLATFORM));
    	getGameWorld().removeEntities(getGameWorld().getEntitiesByType(EntityType.RAND_PLATFORM));
    	inc("Score", -getWorldProperties().getInt("Score"));
    	viewport.setBounds(0, -Integer.MAX_VALUE, getAppWidth(), 15000);
    	spawnPlatform(400, 1000);
    	
    	showMessage("YOU DIED");
    }
    

    @Override
    protected void initGame() 
    {
    	getGameWorld().addEntityFactory(new GameFactory());
    	getGameWorld().spawn("Background");   	
    	FXGL.setLevelFromMap("tmx/tile.tmx");
    	getGameWorld().spawn("Wall");
    	player = getGameWorld().spawn("Player",0,1000);
    	
    	spawnPlatform(player.getX()+300, player.getY());
    	    	
    	Viewport viewport = getGameScene().getViewport();
    	viewport.bindToEntity(player, getAppWidth()/2.5,getAppHeight()/2);	
    	viewport.setBounds(0, -Integer.MAX_VALUE, getAppWidth(), 15000);
    	
    }
    
    public static void main(String[] args) 
    {
        launch(args);
    }
}