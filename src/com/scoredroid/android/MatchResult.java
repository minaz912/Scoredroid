package com.scoredroid.android;
/**
 * 
 * @author burak
 *
 */
public class MatchResult {

	private long id;

	private String homeTeam;
	
	private String awayTeam;
	
	private String score;

	
	public MatchResult() {
		// TODO Auto-generated constructor stub
	}
	
	public MatchResult(long id, String homeTeam, String awayTeam, String score){
		
		this.id = id;
		this.homeTeam = homeTeam;
		this.awayTeam = awayTeam;
		this.score = score;
		
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getHomeTeam() {
		return homeTeam;
	}

	public void setHomeTeam(String homeTeam) {
		this.homeTeam = homeTeam;
	}

	public String getAwayTeam() {
		return awayTeam;
	}

	public void setAwayTeam(String awayTeam) {
		this.awayTeam = awayTeam;
	}	
}
