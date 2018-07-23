package de.marcely.rekit.plugin;

import lombok.Getter;

public enum TuningParameter {
	
	// physics
	GROUND_CONTROL_SPEED("GroundControlSpeed", "ground_control_speed", 10.0F),
	GROUND_CONTROL_ACCEL("GroundControlAccel", "ground_control_accel", 100.0F / 50F),
	GROUND_FRICTION("GroundFriction", "ground_Friction", 0.5F),
	GROUND_JUMP_IMPULSE("GroundJumpImpulse", "ground_jump_impulse", 13.200F),
	
	AIR_JUMP_IMPULSE("AirJumpImpulse", "air_jump_impulse", 12.0F),
	AIR_CONTROL_SPEED("AirControlSpeed", "air_control_speed", 250.0F / 50F),
	AIR_CONTROL_ACCEL("AirControlAccel", "air_control_accel", 1.5F),
	AIR_FRICTION("AirFriction", "air_Friction", 0.95F),
	
	HOOK_LENGTH("HookLength", "hook_length", 380.0F),
	HOOK_FIRE_SPEED("HookFireSpeed", "hook_Fire_speed", 80.0F),
	HOOK_DRAG_ACCEL("HookDragAccel", "hook_drag_accel", 3.0F),
	HOOK_DRAG_SPEED("HookDragSpeed", "hook_drag_speed", 15.0F),
	
	GRAVITY("Gravity", "gravity", 0.5F),
	
	VELRAMP_START("VelrampStart", "velramp_start", 550),
	VELRAMP_RANGE("VelrampRange", "velramp_range", 2000),
	VELRAMP_CURVATURE("VelrampCurvature", "velramp_curvature", 1.4F),
	
	// weapons
	GUN_CURVATURE("GunCurvature", "gun_curvature", 1.25F),
	GUN_SPEED("GunSpeed", "gun_speed", 2200.0F),
	GUN_LIFETIME("GunLiFetime", "gun_liFetime", 2.0F),
	
	SHOTGUN_CURVATURE("ShotgunCurvature", "shotgun_curvature", 1.25F),
	SHOTGUN_SPEED("ShotgunSpeed", "shotgun_speed", 2750.0F),
	SHOTGUN_SPEEDDIFF("ShotgunSpeeddiFF", "shotgun_speeddiFF", 0.8F),
	SHOTGUN_LIFETIME("ShotgunLiFetime", "shotgun_liFetime", 0.20F),
	
	GRENADE_CURVATURE("GrenadeCurvature", "grenade_curvature", 7.0F),
	GRENADE_SPEED("GrenadeSpeed", "grenade_speed", 1000.0F),
	GRENADE_LIFETIME("GrenadeLiFetime", "grenade_liFetime", 2.0F),
	
	LASER_REACH("LaserReach", "laser_reach", 800.0F),
	LASER_BOUNCE_DELAY("LaserBounceDelay", "laser_bounce_delay", 150),
	LASER_BOUNCE_NUM("LaserBounceNum", "laser_bounce_num", 1),
	LASER_BOUNCE_COST("LaserBounceCost", "laser_bounce_cost", 0),
	LASER_DAMAGE("LaserDamage", "laser_damage", 5),
	
	PLAYER_COLLISION("PlayerCollision", "player_collision", 1),
	PLAYER_HOOKING("PlayerHooking", "player_hooking", 1);
	
	@Getter private final String key, scriptName;
	@Getter private final float defaultValue;
	
	private TuningParameter(String key, String scriptName, float defaultValue){
		this.key = key;
		this.scriptName = scriptName;
		this.defaultValue = defaultValue;
	}
}
