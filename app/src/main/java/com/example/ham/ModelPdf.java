package com.example.ham;

public class ModelPdf {
    String uid ,id ,title,description,categoryId,pdfUrl,audioUrl,group,imageUrl,isChild;
    boolean favorite;
    long timestamp;

    public ModelPdf() {
    }

    public ModelPdf(String uid, String id, String title, String description, String categoryId, String pdfUrl, String audioUrl, String group, String imageUrl, boolean favorite, long timestamp, String isChild) {
        this.uid = uid;
        this.id = id;
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.pdfUrl = pdfUrl;
        this.audioUrl = audioUrl;
        this.group = group;
        this.imageUrl = imageUrl;
        this.favorite = favorite;
        this.timestamp = timestamp;
        this.isChild=isChild;
    }

    public String getIsChild() {
        return isChild;
    }

    public void setIsChild(String isChild) {
        this.isChild = isChild;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

