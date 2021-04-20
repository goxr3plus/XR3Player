package com.goxr3plus.xr3player.models.lists;

public class DatabaseListFactory {

    private DatabaseListFactory() {
    }

    public static DatabaseList hated() {
        return new DatabaseList("HatedMediaListOriginal");
    }

    public static DatabaseList disliked() {
        return new DatabaseList("DislikedMediaListOriginal");
    }

    public static DatabaseList liked() {
        return new DatabaseList("LikedMediaListOriginal");
    }

    public static DatabaseList loved() {
        return new DatabaseList("LovedMediaListOriginal");
    }


}
