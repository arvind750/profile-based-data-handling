package org.profiles.Model;


import jakarta.persistence.*;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int age;

    private String profileName;
    private String message;

    // Getters & setters
    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getProfileName() { return profileName; }

    public void setProfileName(String profileName) { this.profileName = profileName; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message;}

    public int getAge(){
        return age;
        }
        public void setAge(int age){
        this.age = age;
        }
    }

