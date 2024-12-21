package com.example.ham;

public class ModelPdf1 extends ModelPdf{
    String uid ,id ,title,description,pdfUrl,group,imageUrl;
    long timestamp;

    public ModelPdf1() {
    }

    public ModelPdf1(String description, String id, String title, String uid , String pdfUrl , long timestamp, String group, String pdfBook) {
        this.uid = uid;
        this.id = id;
        this.title = title;
        this.description = description;

        this.pdfUrl = pdfUrl;

        this.group=group;
        this.imageUrl=pdfBook;


        this.timestamp = timestamp;
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


    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
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


