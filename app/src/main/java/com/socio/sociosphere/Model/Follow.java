//package com.socio.sociosphere.Model;
//
//public class Follow2 {
//
//    int profile;
//
//    public Follow2(int profile) {
//        this.profile = profile;
//    }
//
//    public int getProfile() {
//        return profile;
//    }
//
//    public void setProfile(int profile) {
//        this.profile = profile;
//    }
//}


package com.socio.sociosphere.Model;

public class Follow {

    private String followedBy;
    private long followedAt;

    public Follow() {
    }

    public String getFollowedBy() {
        return followedBy;
    }

    public void setFollowedBy(String followedBy) {
        this.followedBy = followedBy;
    }

    public long getFollowedAt() {
        return followedAt;
    }

    public void setFollowedAt(long followedAt) {
        this.followedAt = followedAt;
    }
}

