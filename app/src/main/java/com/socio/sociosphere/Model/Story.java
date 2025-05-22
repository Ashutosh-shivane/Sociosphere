package com.socio.sociosphere.Model;

import java.util.ArrayList;
import java.util.Map;

public class Story {

    private String sotryBy;
    private long storyAt;

    ArrayList<UserStories> list;

    public Story() {
    }


    public String getSotryBy() {
        return sotryBy;
    }

    public void setSotryBy(String sotryBy) {
        this.sotryBy = sotryBy;
    }

    public long getStoryAt() {
        return storyAt;
    }

    public void setStoryAt(long storyAt) {
        this.storyAt = storyAt;
    }

    public ArrayList<UserStories> getList() {
        return list;
    }

    public void setList(ArrayList<UserStories> list) {
        this.list = list;
    }

    public void setStories(ArrayList<UserStories> stories) {
        this.list = stories;

    }

    public ArrayList<UserStories> getStories() {
        return list;
    }

}
