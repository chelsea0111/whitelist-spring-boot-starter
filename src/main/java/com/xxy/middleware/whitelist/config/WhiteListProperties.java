package com.xxy.middleware.whitelist.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("xxy.middleware.whitelist")
public class WhiteListProperties {
    private String users;

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }
}
