package com.ibexreader.mibooksi.adapters;

import android.net.Uri;

/**
 * Created by john on 5/27/16.
 */
public class Book {

    public int id =-1;
    public static String TYPE_PDF="pdf";
    public static String TYPE_EPUB="epub";
    public static String TYPE_OTHER="other";
    public Uri uri =  null;
    public int lastProg=0;
     public String Title="";
    public String type=TYPE_OTHER;

    public boolean isFavorite;


    public Book(int id, String title, Uri uri, boolean isfave, String type) {
        this.id = id;
        Title = title;
        this.uri = uri;
        this.isFavorite = isfave;
        setType(title);



    }


    public void setType(String name1) {
        String name = name1.toLowerCase();
        if(name.endsWith(".pdf")){
            type = TYPE_PDF;
        }else if(name.endsWith(".epub")){
            type =TYPE_EPUB;
        }else{
            type =TYPE_OTHER;

        }
    }
}
