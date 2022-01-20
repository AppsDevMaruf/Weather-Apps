package com.tanvir.training.weatherappbatch1.models.forecast;

import com.google.gson.annotations.SerializedName;

public class Sys{

	@SerializedName("pod")
	private String pod;

	public String getPod(){
		return pod;
	}
}