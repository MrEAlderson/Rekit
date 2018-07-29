package de.marcely.rekit.entity;

import de.marcely.rekit.TWWorld;
import de.marcely.rekit.network.server.Client;
import de.marcely.rekit.plugin.TuningParameter;
import de.marcely.rekit.plugin.entity.EntityType;
import de.marcely.rekit.plugin.entity.Player;
import de.marcely.rekit.plugin.entity.Weapon;
import de.marcely.rekit.plugin.player.Emote;
import de.marcely.rekit.plugin.player.HookState;
import de.marcely.rekit.plugin.player.Team;
import de.marcely.rekit.snapshot.SnapshotObjectType;
import de.marcely.rekit.snapshot.object.SnapshotObjectCharacter;
import de.marcely.rekit.snapshot.object.SnapshotObjectPlayerInput;
import de.marcely.rekit.util.MathUtil;
import de.marcely.rekit.util.Vector2;

public class EntityPlayer extends TWEntity implements Player {
	
	private static final float TEE_SIZE = 28.0F;
	
	private static final int CORE_EVENT_GROUND_JUMP = 0x01;
	private static final int CORE_EVENT_AIR_JUMP = 0x02;
	private static final int CORE_EVENT_HOOK_LAUNCH = 0x04;
	private static final int CORE_EVENT_HOOK_ATTACH_PLAYER = 0x08;
	private static final int CORE_EVENT_HOOK_ATTACH_GROUND = 0x10;
	private static final int CORE_EVENT_HOOK_HIT_NO_HOOK = 0x20;
	private static final int CORE_EVENT_HOOK_RETRACT = 0x40;
	
	public Client client;
	
	public Vector2 viewPos;
	private Vector2 velo;
	private Vector2 hookPos;
	private Vector2 hookDir;
	private int hookTick;
	private HookState hookState;
	private int jumped;
	private int direction;
	private int angle;
	private int hookedPlayer;
	private int triggeredEvents;
	private int inputsAmount;
	private int lastAction;
	private int emoteStopTick;
	private int reckoningTick;
	private int attackTick;
	private int reloadTimer;
	private int lastNoAmmoSound;
	private int damageTakenTick;
	private int damageTaken;
	private byte playerFlags;
	private SnapshotObjectPlayerInput input;
	public Emote emote;
	public Weapon weapon;
	public int health, armor, ammo;
	public boolean isAlive;
	public boolean spawning = false;
	public Team team;
	
	public EntityPlayer(TWWorld world, int id){
		super(world, id);
	}

	@Override
	public EntityType getType(){
		return EntityType.PLAYER;
	}

	@Override
	public float getProximityRadius(){
		return 28F;
	}

	@Override
	public void tick(){
		tickCore(true);
		
		/*
		 *             var rDiv3 = ProximityRadius / 3.0f;

            if (GameContext.Collision.GetTileFlags(Position.x + rDiv3, Position.y - rDiv3).HasFlag(TileFlags.DEATH) ||
                GameContext.Collision.GetTileFlags(Position.x + rDiv3, Position.y + rDiv3).HasFlag(TileFlags.DEATH) ||
                GameContext.Collision.GetTileFlags(Position.x - rDiv3, Position.y - rDiv3).HasFlag(TileFlags.DEATH) ||
                GameContext.Collision.GetTileFlags(Position.x - rDiv3, Position.y + rDiv3).HasFlag(TileFlags.DEATH) ||
                GameLayerClipped(Position))
            {
                Die(Player.ClientId, Weapon.WORLD);
            }

            HandleWeapons();
		 */
	}
	
	public void tickDefered(){
		tickCore(false);
		/*
		 *             ReckoningCore.Move();
            ReckoningCore.Quantize();

            Core.Move();
            Core.Quantize();
            Position = Core.Position;

            var events = Core.TriggeredEvents;
            var mask = GameContext.MaskAllExceptOne(Player.ClientId);

            if (events.HasFlag(CoreEvents.GROUND_JUMP))
                GameContext.CreateSound(Position, Sound.PLAYER_JUMP, mask);

            if (events.HasFlag(CoreEvents.HOOK_ATTACH_PLAYER))
                GameContext.CreateSound(Position, Sound.HOOK_ATTACH_PLAYER, GameContext.MaskAll());

            if (events.HasFlag(CoreEvents.HOOK_ATTACH_GROUND))
                GameContext.CreateSound(Position, Sound.HOOK_ATTACH_GROUND, mask);

            if (events.HasFlag(CoreEvents.HOOK_HIT_NOHOOK))
                GameContext.CreateSound(Position, Sound.HOOK_NOATTACH, mask);

            if (Player.Team == Team.SPECTATORS)
                Position = new Vector2(Input.TargetX, Input.TargetY);

            {
                var predicted = new SnapObj_Character();
                var current = new SnapObj_Character();

                ReckoningCore.Write(predicted);
                Core.Write(current);

                if (ReckoningTick + Server.TickSpeed * 3 < Server.Tick || !predicted.Compare(current))
                {
                    ReckoningTick = Server.Tick;
                    Core.FillTo(SendCore);
                    Core.FillTo(ReckoningCore);
                }
            }
		 */
	}
	
	public void tickPaused(){
		this.attackTick++;
		this.reckoningTick++;
		this.damageTakenTick++;
		
		if(this.lastAction != -1)
			this.lastAction++;
		
		if(this.emoteStopTick > 1)
			this.emoteStopTick++;
		
		/*
		 *             if (Weapons[(int) ActiveWeapon].AmmoRegenStart > -1)
                Weapons[(int) ActiveWeapon].AmmoRegenStart++;
		 */
	}
	
	private void tickCore(boolean useInput){
		this.triggeredEvents = 0x00;
		
		boolean isGrounded = false;
		/*
		 * if (Collision.IsTileSolid(Position.x + TEE_SIZE / 2, Position.y + TEE_SIZE / 2 + 5))
                isGrounded = true;
            else if (Collision.IsTileSolid(Position.x - TEE_SIZE / 2, Position.y + TEE_SIZE / 2 + 5))
                isGrounded = true;
		 */
		
		final Vector2 targetDir = this.input.target.normalize();
		final float maxSpeed = this.world.getTuningParameterValue(isGrounded ? TuningParameter.GROUND_CONTROL_SPEED : TuningParameter.AIR_CONTROL_SPEED);
		final float accel = this.world.getTuningParameterValue(isGrounded ? TuningParameter.GROUND_CONTROL_ACCEL : TuningParameter.AIR_CONTROL_ACCEL);
		final float friction = this.world.getTuningParameterValue(isGrounded ? TuningParameter.GROUND_FRICTION : TuningParameter.AIR_FRICTION);
		
		this.velo.addY(world.getTuningParameterValue(TuningParameter.GRAVITY));
		
		if(useInput){
			this.direction = this.input.direction;
			
			double angle = this.input.target.getX() == 0 ?
					Math.atan(this.input.target.getY()) :
					Math.atan(this.input.target.getY() / this.input.target.getX());
					
			if(this.input.target.getX() < 0)
				angle += Math.PI;
			
			if(this.input.jump){
				if((this.jumped & 1) == 0){
					if(isGrounded){
						triggeredEvents |= CORE_EVENT_GROUND_JUMP;
						this.velo.subtractY(this.world.getTuningParameterValue(TuningParameter.GROUND_JUMP_IMPULSE));
						this.jumped |= 1;
					
					}else{
						triggeredEvents |= CORE_EVENT_AIR_JUMP;
						this.velo.subtractY(this.world.getTuningParameterValue(TuningParameter.AIR_JUMP_IMPULSE));
						this.jumped |= 3;
					}
				}
			
			}else
				this.jumped &= ~1;
			
			if(this.input.hook){
				if(this.hookState == HookState.IDLE){
					this.hookState = HookState.FLYING;
					this.hookPos = this.pos.clone().add(targetDir.multiply(TEE_SIZE, TEE_SIZE).multiply(1.5F, 1.5F));
					this.hookDir = targetDir;
					this.hookedPlayer = -1;
					this.hookTick = 0;
					this.triggeredEvents |= CORE_EVENT_HOOK_LAUNCH;
				}
			
			}else{
				this.hookedPlayer = -1;
				this.hookState = HookState.IDLE;
				this.hookPos = this.pos;
			}
			
			if(this.direction < 0)
				this.velo.setX(MathUtil.saturateAdd(-maxSpeed, maxSpeed, this.velo.getX(), -accel));
			else if(this.direction > 0)
				this.velo.setX(MathUtil.saturateAdd(-maxSpeed, maxSpeed, this.velo.getX(), accel));
			else
				velo.multiplyX(friction);
			
			if(isGrounded)
				jumped &= ~2;
			
			if(this.hookState == HookState.IDLE){
				this.hookedPlayer = -1;
				this.hookPos = this.pos;
			
			}else if(this.hookState.ordinal() >= HookState.RETRACT_START.ordinal()
					&& this.hookState.ordinal() < HookState.RETRACT_END.ordinal()){
				this.hookState = HookState.values()[this.hookState.ordinal()+1];
			
			}else if(this.hookState == HookState.RETRACT_END){
				this.hookState = HookState.RETRACTED;
				this.triggeredEvents |= CORE_EVENT_HOOK_RETRACT;
			
			}else if(this.hookState == HookState.FLYING){
				float c = this.world.getTuningParameterValue(TuningParameter.HOOK_FIRE_SPEED);
				Vector2 newHookPos = this.hookPos.add(this.hookDir).multiply(c, c);
				
				if(this.pos.distance(newHookPos) > this.world.getTuningParameterValue(TuningParameter.HOOK_LENGTH)){
					c = this.world.getTuningParameterValue(TuningParameter.HOOK_LENGTH);
					
					this.hookState = HookState.RETRACT_START;
					newHookPos = this.pos.clone().add(newHookPos.clone().subtract(this.pos).normalize()).multiply(c, c);
				}
				
				boolean goingToHitGround = false;
				boolean goingToRetract = false;
				/*
				 *  var hitFlags = Collision.IntersectLine(HookPosition, newHookPos, 
                    out newHookPos, out var _);

                if (hitFlags != TileFlags.NONE)
                {
                    if (hitFlags.HasFlag(TileFlags.NOHOOK))
                        goingToRetract = true;
                    else
                        goingToHitGround = true;
                }

                if (World.Tuning["PlayerHooking"] > 0)
                {
                    var distance = 0f;
                    for (var i = 0; i < World.CharacterCores.Length; i++)
                    {
                        var characterCore = World.CharacterCores[i];
                        if (characterCore == null || characterCore == this)
                            continue;

                        var closestPoint = Math.ClosestPointOnLine(HookPosition, newHookPos,
                            characterCore.Position);
                        if (Math.Distance(characterCore.Position, closestPoint) < TEE_SIZE + 2f)
                        {
                            if (HookedPlayer == -1 || Math.Distance(HookPosition, characterCore.Position) < distance)
                            {
                                TriggeredEvents |= CoreEvents.HOOK_ATTACH_PLAYER;
                                HookState = HookState.GRABBED;
                                HookedPlayer = i;
                                distance = Math.Distance(HookPosition, characterCore.Position);
                                break;
                            }  
                        }
                    }
                }

                if (HookState == HookState.FLYING)
                {
                    if (goingToHitGround)
                    {
                        TriggeredEvents |= CoreEvents.HOOK_ATTACH_GROUND;
                        HookState = HookState.GRABBED;
                    }
                    else if (goingToRetract)
                    {
                        TriggeredEvents |= CoreEvents.HOOK_HIT_NOHOOK;
                        HookState = HookState.RETRACT_START;
                    }

                    HookPosition = newHookPos;
                }
				 * 
				 */
			}
			
			if(this.hookState == HookState.GRABBED){
				/*
				 *                 if (HookedPlayer != -1)
                {
                    var characterCore = World.CharacterCores[HookedPlayer];
                    if (characterCore != null)
                        HookPosition = characterCore.Position;
                    else
                    {
                        HookedPlayer = -1;
                        HookState = HookState.RETRACTED;
                        HookPosition = Position;
                    }
                }

                if (HookedPlayer == -1 && Math.Distance(HookPosition, Position) > 46.0f)
                {
                    var hookVel = (HookPosition - Position).Normalized * World.Tuning["HookDragAccel"];
                    if (hookVel.y > 0)
                        hookVel.y *= 0.3f;

                    if (hookVel.x < 0 && Direction < 0 || hookVel.x > 0 && Direction > 0)
                        hookVel.x *= 0.95f;
                    else
                        hookVel.x *= 0.75f;

                    var newVel = vel + hookVel;
                    if (newVel.Length < World.Tuning["HookDragSpeed"] || newVel.Length < vel.Length)
                        vel = newVel;
                }

                HookTick++;

                // 60 = 1.25s
                if (HookedPlayer != -1 && (HookTick > 60 || World.CharacterCores[HookedPlayer] == null))
                {
                    HookedPlayer = -1;
                    HookState = HookState.RETRACTED;
                    HookPosition = Position;
                }
				 */
			}
			
			if(this.world.getTuningParameterValue(TuningParameter.PLAYER_COLLISION) > 0 ||
			   this.world.getTuningParameterValue(TuningParameter.PLAYER_HOOKING) > 0){
				/**
				 *                 for (var i = 0; i < World.CharacterCores.Length; i++)
                {
                    var characterCore = World.CharacterCores[i];
                    if (characterCore == null || characterCore == this)
                        continue;

                    var distance = Math.Distance(Position, characterCore.Position);
                    var direction = (Position - characterCore.Position).Normalized;

                    if (World.Tuning["PlayerCollision"] > 0 &&
                        distance < TEE_SIZE * 1.25f &&
                        distance > 0)
                    {
                        var a = (TEE_SIZE * 1.45f - distance);
                        var velocity = 0.5f;

                        if (vel.Length > 0.0001)
                            velocity = 1 - (Math.Dot(vel.Normalized, direction) + 1) / 2;

                        vel += direction * a * (velocity * 0.75f);
                        vel *= 0.85f;
                    }

                    if (World.Tuning["PlayerHooking"] > 0 &&
                        HookedPlayer == i)
                    {
                        if (distance > TEE_SIZE * 1.50f)
                        {
                            var hookAccel = World.Tuning["HookDragAccel"] *
                                (distance / World.Tuning["HookLength"]);
                            float dragSpeed = World.Tuning["HookDragSpeed"];

                            characterCore.Velocity = new Vector2(
                                Math.SaturatedAdd(-dragSpeed, dragSpeed, 
                                    characterCore.Velocity.x, hookAccel*direction.x*1.5f),
                                Math.SaturatedAdd(-dragSpeed, dragSpeed, 
                                    characterCore.Velocity.y, hookAccel*direction.y*1.5f)
                            );

                            vel.x = Math.SaturatedAdd(-dragSpeed, dragSpeed, vel.x, 
                                -hookAccel * direction.x * 0.25f);
                            vel.y = Math.SaturatedAdd(-dragSpeed, dragSpeed, vel.y, 
                                -hookAccel * direction.y * 0.25f);
                        }
                    }
                }
				 */
			}
			
			if(this.velo.length() > 6000)
				this.velo = this.velo.normalize().multiply(6000, 6000);
		}
	}

	@Override
	public void doSnapshot(Client client){
		/*
		 *             var id = Player.ClientId;

            if (!Server.Translate(ref id, snappingClient))
                return;

            if (NetworkClipped(snappingClient))
                return;

            var character = Server.SnapObject<SnapObj_Character>(id);
            if (character == null)
                return;

            if (ReckoningTick == 0 || GameWorld.IsPaused)
            {
                character.Tick = 0;
                Core.Write(character);
            }
            else
            {
                character.Tick = ReckoningTick;
                SendCore.Write(character);
            }

            if (EmoteStopTick < Server.Tick)
            {
                EmoteStopTick = -1;
                Emote = Emote.NORMAL;
            }
		 */
		
		final SnapshotObjectCharacter snap = this.world.getServer().snapBuilder.newObject(client.getId(), SnapshotObjectType.OBJECT_CHARACTER);
		
		if(this.reckoningTick == 0 || this.world.isPaused()){
			snap.tick = 0;
			
		
		}else{
			snap.tick = this.reckoningTick;
			
		}
		
		if(this.emoteStopTick < this.world.getServer().getCurrentTick()){
			this.emoteStopTick = -1;
			this.emote = Emote.NORMAL;
		}
		
		snap.pos = this.pos;
		snap.velo = this.velo;
		snap.emote = this.emote;
		snap.ammoCount = 0;
		snap.health = 0;
		snap.armor = 0;
		snap.weapon = this.weapon;
		snap.attackTick = this.attackTick;
		snap.direction = this.input.direction;
		snap.hookState = this.hookState;
		snap.hookPos = this.hookPos;
		
		if(client == null || client.getId() == this.client.getId()){
			snap.health = this.health;
			snap.armor = this.armor;
			
			/*
			 *                 if (Weapons[(int) ActiveWeapon].Ammo > 0)
                    character.AmmoCount = Weapons[(int) ActiveWeapon].Ammo;
			 */
		}
		
		/*
		 *   if (character.Emote == Emote.NORMAL)
            {
                if (250 - ((Server.Tick - LastAction) % 250) < 5)
                    character.Emote = Emote.BLINK;
            }


            if (character.HookedPlayer != -1)
            {
                if (!Server.Translate(ref character.HookedPlayer, snappingClient))
                    character.HookedPlayer = -1;
            }
		 */
		
		snap.playerFlags = this.playerFlags;
	}

	@Override
	public Emote getEmote(){
		return this.emote;
	}

	@Override
	public Weapon getWeapon(){
		return this.weapon;
	}

	@Override
	public int getHealth(){
		return this.health;
	}

	@Override
	public int getArmor(){
		return this.armor;
	}

	@Override
	public int getAmmo(){
		return this.ammo;
	}

	@Override
	public boolean isAlive(){
		return this.isAlive;
	}
	
	public void reset(){
		this.emote = Emote.NORMAL;
		this.weapon = Weapon.HAMMER;
		this.health = 5;
		this.ammo = 5;
		this.armor = 5;
		this.team = Team.RED;
		this.input = new SnapshotObjectPlayerInput();
		this.velo = new Vector2(0, 0);
		this.hookState = HookState.IDLE;
		this.hookPos = new Vector2(0, 0);
	}
	
	public void respawn(){
		if(this.team != Team.SPECTATOR)
			this.spawning = true;
	}
}
