package com.infotech4it.flare.views.models;


import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

//Extender class
public class BlogPostId {

    @Exclude
    public String BlogPostId;

    public <T extends com.infotech4it.flare.views.models.BlogPostId> T withId(@NonNull final String id){
        this.BlogPostId = id;
        return (T) this;
    }

}
