package vn.youmed.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.vertx.core.json.JsonObject;

public class Clazz {

	@JsonProperty("_id")
	private String _id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("maximum")
	private int maximum;

	@JsonProperty("total")
	private int total;

	@JsonProperty("specility_id")
	private String speciality_id;

	public Clazz() {
	}

	public Clazz(JsonObject jsonObject) {
		this._id = jsonObject.getString("_id");
		this.name = jsonObject.getString("name");
		this.maximum = jsonObject.getInteger("maximum");
		this.total = jsonObject.getInteger("total");
		this.speciality_id = jsonObject.getString("speciality_id");

	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSpeciality_id() {
		return speciality_id;
	}

	public void setSpeciality_id(String speciality_id) {
		this.speciality_id = speciality_id;
	}

	public String get_id() {
		return _id;
	}

	public int getMaximum() {
		return maximum;
	}

	public void setMaximum(int maximum) {
		this.maximum = maximum;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

}