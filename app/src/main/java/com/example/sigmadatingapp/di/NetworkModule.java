package com.example.sigmadatingapp.di;


import static com.example.sigmadatingapp.storage.AppConstants.SHARED_PREF_NAME;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.sigmadatingapp.api.ApiInterface;
import com.example.sigmadatingapp.api.AuthenticationInterceptor;
import com.example.sigmadatingapp.storage.SharedPreferencesStorage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {
    @Singleton
    @Provides
    Gson provideGsonBuilder() {
        return new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreference(@ApplicationContext Context context) {
        return context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    SharedPreferencesStorage provideSharedPreferenceStorage(SharedPreferences preferences) {
        return new SharedPreferencesStorage(preferences);
    }

    @Singleton
    @Provides
    OkHttpClient provideOkHttpClient(SharedPreferences preferences) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder client = new OkHttpClient.Builder().addInterceptor(interceptor).authenticator(new AuthenticationInterceptor());
        return client.build();
    }

    @Singleton
    @Provides
    Retrofit.Builder provideRetrofit(Gson gson, OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl("")
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson));
    }

    @Singleton
    @Provides
    ApiInterface provideApiService(Retrofit.Builder retrofit) {
        return retrofit
                .build()
                .create(ApiInterface.class);
    }
}
