package de.marcely.rekit.plugin.voting;

import lombok.Getter;

public class Vote {
	
	@Getter private final VotingType type;
	@Getter private final String value, reason;
	
	public Vote(VotingType type, String value, String reason){
		this.type = type;
		this.value = value;
		this.reason = reason;
	}
}