package models;

import java.io.Serializable;

/**
 * User Model Class
 * Represents a user (student or admin) in the system
 * Implements Serializable for network transmission
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;
    private String username;
    private String role; // STUDENT or ADMIN
    private boolean isConnected;
    private String ipAddress;

    public User() {
    }

    public User(String userId, String username, String role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.isConnected = false;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", isConnected=" + isConnected +
                ", ipAddress='" + ipAddress + '\'' +
                '}';
    }
}
