package com.knightgame;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.physics.PhysicsComponent;

import javafx.geometry.Point2D;
import javafx.util.Duration;




public class PlayerComponent extends Component
{
	
    private AnimatedTexture texture;
    private AnimationChannel animIdle, animJump;
    
    private PhysicsComponent physics;

    public PlayerComponent() 
    {
        animIdle = new AnimationChannel(FXGL.image("_Idle.png"), 10, 120, 80, Duration.seconds(1), 0, 9);
        animJump = new AnimationChannel(FXGL.image("_Jump.png"), 3, 120, 80, Duration.seconds(1), 0, 2);
        
        texture = new AnimatedTexture(animIdle);;
    }

    @Override
    public void onAdded() {
    	
    	entity.getTransformComponent().setScaleOrigin(new Point2D(50, 50));
        entity.getViewComponent().addChild(texture);
       
    }
    
    @Override
    public void onUpdate(double tpf) 
    {    	
    	if (physics.isMovingY()) 
    	{
            if (texture.getAnimationChannel() != animJump) 
            {
                texture.loopAnimationChannel(animJump);
            }
        }
    	else 
        {
    		stop();
    		if (texture.getAnimationChannel() != animIdle) 
    		{
    			texture.loopAnimationChannel(animIdle);
    		}
        }
    }

    private int jumps = 3;

    public void jump()
    {
    	if(jumps > 0)
    	{
    		if(physics.isMovingY())
        	{
        		if(getEntity().getScaleX() > 0)
        		{			
        			moveLeft();
        		}
        		else
        		{
        			moveRight();
        		}
        	}
    		
    		physics.setVelocityY(-450);
    		jumps--;
    	}
    	if(getEntity().getScaleX() > 0)
    	{
    		moveRight();
    	}
    	else
    	{
    		moveLeft();
    	}
    	
    }

    public void moveLeft()
    {
    	getEntity().setScaleX(-1.5);
    	physics.setVelocityX(-300);
    }
    
    public void moveRight()
    {
    	getEntity().setScaleX(1.5);
    	physics.setVelocityX(300);
    }
    
    public void turnRight()
    {
    	if(!physics.isMovingY())
    	{
    		getEntity().setScaleX(1.5);
    	}
    }
    
    public void turnLeft()
    {
    	if(!physics.isMovingY())
    	{
    		getEntity().setScaleX(-1.5);
    	}
    }
    
    public void stop() 
    {
       physics.setVelocityX(0);
       jumps = 3;
    }
    
}

