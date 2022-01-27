package pink.madis.bigaar.sample

import retrofit2.Call
import retrofit2.http.GET

interface SpaceX {
    @GET("/v5/launches/latest")
    fun getLaunches(): Call<String>
}
