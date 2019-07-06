package com.example.projetcci;

/**
 * Constants for APIs
 */
public final class Constants {

    //TMDB API
    public static final String BASE_URL = "https://api.themoviedb.org/3";
    public static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w500";
    public static final String API_KEY = "d8cff699df1fc04a54ef922fa6ea35bc";

    //Custom API for comments
    public static final String CREATE_COMMENT_BASE_URL = "https://projetcci.tk/api/v1/Api.php?apicall=createcomment";

    //Custom API for quotes
    public static final String CREATE_QUOTE_BASE_URL = "https://projetcci.tk/api/v1/Api.php?apicall=createquote";
    public static final String GET_QUOTES_BASE_URL = "https://projetcci.tk/api/v1/Api.php?apicall=getquotesbyid&id=";
}