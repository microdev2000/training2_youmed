package vn.youmed.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.vertx.core.json.JsonObject;

public class Student {
	@JsonProperty("_id")
	private String _id;

	@JsonProperty("firstName")
	private String firstName;

	@JsonProperty("lastName")
	private String lastname;

	@JsonProperty("class_id")
	private String clazz_id;

	@JsonProperty("speciality_id")
	private String speciality_id;

	public Student() {
	}

	public Student(JsonObject jsonObject) {
		this._id = jsonObject.getString("_id");
		this.firstName = jsonObject.getString("firstName");
		this.lastname = jsonObject.getString("lastName");
		this.clazz_id = jsonObject.getString("class");
		this.speciality_id = jsonObject.getString("speciality_id");

	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getClazz_id() {
		return clazz_id;
	}

	public void setClazz_id(String clazz_id) {
		this.clazz_id = clazz_id;
	}

	public String getSpeciality_id() {
		return speciality_id;
	}

	public void setSpeciality_id(String speciality_id) {
		this.speciality_id = speciality_id;
	}

}
