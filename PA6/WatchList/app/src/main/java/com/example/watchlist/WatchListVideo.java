package com.example.watchlist;

public class WatchListVideo {
    private String title;
    private String type;
    private boolean watched;
    private int drawable_id;

    public WatchListVideo() {
        this.title = "Unknown";
        this.type = "";
        this.watched = false;
        this.drawable_id = 0;
    }

    public WatchListVideo(String title, String type, boolean watched, int drawable_id) {
        this.title = title;
        this.type = type;
        this.watched = watched;
        this.drawable_id = drawable_id;
    }

    @Override
    public String toString() {
        return this.title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean hasWatched() {
        return watched;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    public int getDrawable() {
        return drawable_id;
    }

    public void setDrawable(R.drawable drawable) {
        this.drawable_id = drawable_id;
    }
}
