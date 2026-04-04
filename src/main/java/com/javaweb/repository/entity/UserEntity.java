package com.javaweb.repository.entity;
import java.time.LocalDateTime;

public class UserEntity 
{
    private Integer user_id;
    private String fullname;
    private String email;
    private String phone;
    private String password_hash;
    private Integer role_id;
    private boolean is_active;
    private LocalDateTime created_at;

    // Getter setter
    public Integer getUser_id() { return user_id; }
    public void setUser_id(Integer user_id) { this.user_id = user_id; }
    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword_hash() { return password_hash; }
    public void setPassword_hash(String password_hash) { this.password_hash = password_hash; }
    public Integer getRole_id() { return role_id; }
    public void setRole_id(Integer role_id) { this.role_id = role_id; }
    public boolean isIs_active() { return is_active; }
    public void setIs_active(boolean is_active) { this.is_active = is_active; }
    public LocalDateTime getCreated_at() { return created_at; }
    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }

}
