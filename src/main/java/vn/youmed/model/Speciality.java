package vn.youmed.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.vertx.core.json.JsonObject;

public class Speciality {
	@JsonProperty("_id")
	private String _id;

	@JsonProperty("name")
	private String name;

	public Speciality() {
	}

	public Speciality(JsonObject jsonObject) {
		this._id = jsonObject.getString("_id");
		this.name = jsonObject.getString("name");

	}

	public String get_id() {
		return _id;
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

}
