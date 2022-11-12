package com.cs371m.mypod.api

import android.content.Context
import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

interface PodchaserAPI {

    companion object Factory {
        fun create(): ApolloClient {

            val authHeader = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiI5N2I4ZTM0NS1mYzFkLTQ5YzItOGM1Ny04YTA4NjNkMjIyZjUiLCJqdGkiOiIyN2ExNGFhMjM0Y2I4OTIzYmFmNTI4MTI1OGY2ZDM2YTdjMzdhZjliNDhjOWNlNjU4ODkyOGM2YzNmY2E5MjE2ZDRlNDAwMWY0YmNlMzhjMyIsImlhdCI6MTY2ODIwMzg0MS45MDYxMjIsIm5iZiI6MTY2ODIwMzg0MS45MDYxMjcsImV4cCI6MTY5OTczOTg0MS44OTcxMDksInN1YiI6IiIsInNjb3BlcyI6WyIqIl19.kCpn6lWu01RbDqFBeNQ8oVCnQR9OSB5rM9OAJvSPpA26HCLTP0RC2j9n8j4OCqfzHaSjfR0_L3Cqm72PxVKlS4xfjdiJSWpdF1UNszEJ4sM2PrdWMLijufaJyqpQtR_XcFBN3Y0GlrZU2Tw0hz5GG4I3mOafSIMDZGBs4iblUH2Ky62DXmaeRQi19tr4AamSSbv5sLP8HFKseMz22PWoCG2ZWyL6SvSaH38jq_04Rb5OAP475D23vOs1r1hyUllPff5sr8xz4kdc2xaT_cA9JQDbOaO9gMV6TnQMQaDsIYIlHndZejzQb9CtZ4BNSUTsFKn5dMwtZrCfRrmCSmM45PPGzuSKehNLPwF2orXO-PYp_CBw_xSwX9rCJRFKxHI6gTt462kmzPXwQqs8C1GZpzjJrzQZImVtXabIcTdbxF8jPxmy81BVQfJBq0XbCyHYh0TEA1Fyg1gtThksTsh2Cgnd21-RicDwdBcbX2EJOG401YUkN_iwl9IUTNw4A5jGYUhjgg7oiQExgZk3JHwjyo8G013LgM8QiQJTzXXaYR22H--XsrEcrVUKsdnltUpc1NwtTRxAxBQ4hgNn2Se5NGHFvF8JSSOBwH6LVZg2eLy9ShrOTJsbglBPt4N6BG4ntYFdFLHsytcf6-YGoyucGId8QO9CoY6X1u27aN3WMEg";
            val interceptor = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val original = chain.request();
                    val builder = original.newBuilder().method(original.method, original.body);
                    builder.header("Authorization", authHeader);
                    chain.proceed(builder.build());
                }
                .build();

            val apolloClient = ApolloClient.Builder()
                .serverUrl("https://api.podchaser.com/graphql")
                .okHttpClient(interceptor)
                .build();
            return apolloClient;
        }
    }

}