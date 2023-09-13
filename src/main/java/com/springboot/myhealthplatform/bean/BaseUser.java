package com.springboot.myhealthplatform.bean;

/**
 * Classe astratta di riferimento per le classi Doctor e Patient: contiene gli attributi
 * base dell'utente.
 */
public abstract class BaseUser {

	int id;
	String name;
	String surname;
	String email;
	String password;
	boolean active;
	// active = true - il profilo funziona
	// active = false - profilo chiuso

	// COSTRUTTORI

	public BaseUser() {
	}

	public BaseUser(String name, String surname, String email, String password, boolean active) {
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.password = password;
		this.active = active;
	}

	// GETTER

	public int getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getSurname() {
		return this.surname;
	}

	public String getEmail() {
		return this.email;
	}

	public String getPassword() {
		return this.password;
	}

	public boolean getActive() {
		return this.active;
	}


	// SETTER


	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setPassword(String password) {
		this.password = password;
	}


}



