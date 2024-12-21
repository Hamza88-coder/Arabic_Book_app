package com.example.ham;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitInstance {

    // Base URL de l'API
    private static final String BASE_URL = "https://api.dictionaryapi.dev/api/v2/entries/";

    // Instance Retrofit unique (Singleton)
    private static Retrofit retrofitInstance;

    /**
     * Méthode pour obtenir une instance Retrofit unique.
     * Utilisation de la synchronisation pour garantir que l'instance est thread-safe.
     */
    private static Retrofit getInstance() {
        if (retrofitInstance == null) {
            synchronized (RetrofitInstance.class) {
                if (retrofitInstance == null) {
                    retrofitInstance = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }
        return retrofitInstance;
    }

    /**
     * Retourne l'interface DictionaryApi associée à Retrofit.
     * @return Une instance de DictionaryApi.
     */
    public static DictionaryApi getDictionaryApi() {
        return getInstance().create(DictionaryApi.class);
    }
}
