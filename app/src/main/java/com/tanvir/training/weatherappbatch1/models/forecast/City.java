package com.tanvir.training.weatherappbatch1.models.forecast;

import com.google.gson.annotations.SerializedName;

public class City{

	@SerializedName("country")
	private String country;

	@SerializedName("coord")
	private Coord coord;

	@SerializedName("sunrise")
	private int sunrise;

	@SerializedName("timezone")
	private int timezone;

	@SerializedName("sunset")
	private int sunset;

	@SerializedName("name")
	private String name;

	@SerializedName("id")
	private int id;

	@SerializedName("population")
	private int population;

	public String getCountry(){
		return country;
	}

	public Coord getCoord(){
		return coord;
	}

	public int getSunrise(){
		return sunrise;
	}

	public int getTimezone(){
		return timezone;
	}

	public int getSunset(){
		return sunset;
	}

	public String getName(){
		return name;
	}

	public int getId(){
		return id;
	}

	public int getPopulation(){
		return population;
	}
}