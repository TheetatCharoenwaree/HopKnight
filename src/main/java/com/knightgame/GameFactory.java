package com.knightgame;


import static com.almasb.fxgl.dsl.FXGL.*;

import com.almasb.fxgl.dsl.views.ScrollingBackgroundView;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.almasb.fxgl.texture.Texture;

import javafx.geometry.*;

public class GameFactory implements EntityFactory
{
	
	@Spawns("Player")
	public Entity newPlayer(SpawnData data)
	{
		PhysicsComponent physics = new PhysicsComponent();
		physics.setBodyType(BodyType.DYNAMIC);
		physics.setFixtureDef(new FixtureDef().friction(0.0f));
	
		return entityBuilder(data)
			.type(EntityType.PLAYER)
	        .bbox(new HitBox(new Point2D(40,42.5),BoundingShape.box(25,37.5)))
	        .with(physics)
	        .with(new CollidableComponent(true))
	        .with(new IrremovableComponent())
	        .with(new PlayerComponent())
	        .scale(1.5,1.5)
	        .build();   
	}
	
	@Spawns("Background")
    public Entity newBackground(SpawnData data)
    {
		Texture t = getAssetLoader().loadTexture("bg1.png");
		t.resize(getAppWidth(), getAppHeight());
        return entityBuilder(data)
                .view(new ScrollingBackgroundView(t, Orientation.VERTICAL))
                .with(new IrremovableComponent())
                .zIndex(-1)
                .build();	
    }
	
	@Spawns("Platform")
    public Entity newPlatform(SpawnData data) 
	{
        return entityBuilder(data)
                .type(EntityType.PLATFORM)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new PhysicsComponent())
                .with(new CollidableComponent(true))
                .build();
    }
	
	@Spawns("Rand_Platform")
    public Entity newRandPlatform(SpawnData data) 
	{
        return entityBuilder(data)
                .type(EntityType.RAND_PLATFORM)
                .view("RandPlatform.png")
                .bbox(new HitBox(BoundingShape.box(144, 16)))
                .with(new PhysicsComponent())
                .with(new CollidableComponent(true))
                .build();
    }
	
	@Spawns("Rand_Spawner")
    public Entity newRandSpawner(SpawnData data) 
	{
		 return entityBuilder(data)
                .type(EntityType.SPAWN_PLATFORM)
                .bbox(new HitBox(new Point2D(2,0),BoundingShape.box(140, 10)))
                .with(new PhysicsComponent())
                .with(new CollidableComponent(true))
                .build();
	}
	
	@Spawns("Wall")
	public Entity newWall(SpawnData data)
	{
		return entityBuilder()
				.type(EntityType.WALL)
				.bbox(new HitBox(new Point2D(0,-Integer.MAX_VALUE),BoundingShape.box(1,Integer.MAX_VALUE*1.5)))
				.bbox(new HitBox(new Point2D(getAppWidth(),-Integer.MAX_VALUE),BoundingShape.box(100, Integer.MAX_VALUE*1.5)))
				.with(new PhysicsComponent())
				.with(new CollidableComponent(true))
				.build();
	}
	
	@Spawns("Spike")
    public Entity newSpike(SpawnData data) 
	{
		PhysicsComponent physics = new PhysicsComponent();
		physics.setBodyType(BodyType.DYNAMIC);
		
        return entityBuilder(data)
                .type(EntityType.SPIKE)
                .viewWithBBox("spike.png")
                .with(new CollidableComponent(true))
                .with(physics)
                .scale(2,-2)
                .build();
    }
	
}