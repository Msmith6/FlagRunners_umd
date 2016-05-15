package com.comsci436.flagrunners;

import java.util.HashSet;

/**
 * Created by Yoolbin on 2016-05-13.
 */
public class Group {
    private int team_size, open_spot;
    private String group_name, password, current_username;
    private HashSet<String> userList;

    public Group(){
        userList = new HashSet<String>();
    }

    public int getTeam_size() {
        return team_size;
    }

    public void setTeam_size(int team_size) {
        this.team_size = team_size;
    }

    public int getOpen_spot() {
        return open_spot;
    }

    public void setOpen_spot(int open_spot) {
        this.open_spot = open_spot;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCurrent_username() {
        return current_username;
    }

    public void setCurrent_username(String current_username) {
        this.current_username = current_username;
    }

    public HashSet<String> getUserList() {
        return userList;
    }

    public void setUserList(HashSet<String> userList) {
        this.userList = userList;
    }
    public void addUserList(String s){
        userList.add(s);
    }
}