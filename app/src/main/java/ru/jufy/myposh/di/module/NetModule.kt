package ru.jufy.myposh.di.module

import android.app.Application
import android.os.Build
import android.util.Log
import com.google.gson.*
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.jufy.myposh.BuildConfig
import ru.jufy.myposh.di.PerApplication
import ru.jufy.myposh.model.data.server.ApiService
import ru.jufy.myposh.model.data.server.interceptors.AuthenticationInterceptor
import ru.jufy.myposh.model.interactor.AuthInteractor
import ru.jufy.myposh.model.storage.UserPreferences
import ru.jufy.myposh.model.system.ResourceManager
import ru.jufy.myposh.presentation.global.ErrorHandler
import ru.terrakok.cicerone.Router
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Type
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

/**
 * Created by rolea on 14.09.2017.
 */

@Module
class NetModule {

    private val DEBUG_BASE_URL = "https://posh.jwma.ru/"
    private val BASE_URL = "https://art.posh.space/"
    private var mApplication: Application? = null

    val androidVersion: String
        get() {
            val release = Build.VERSION.RELEASE
            val sdkVersion = Build.VERSION.SDK_INT
            return "Android SDK: $sdkVersion ($release)"
        }

    // creating a Certificate
    private val certificate: Certificate?
        get() {
            var certificate: Certificate? = null
            val certificateFactory = loadCertificateAuthorityFromResources()
            val inputStream = caFromResources
            try {
                certificate = certificateFactory?.generateCertificate(inputStream)
            } catch (e: CertificateException) {
                e.printStackTrace()
            }

            return certificate
        }


    @Provides
    @PerApplication
    internal fun provideErrorHandler(interactor: AuthInteractor, resourceManager: ResourceManager, router: Router)
            = ErrorHandler(interactor, resourceManager, router)

    // loading CAs from Resources
    // saving your certificate.crt on raw package in your resources// FIXME:insert certificate for mgts when https will be available
    //mApplication.getResources().openRawResource(R.raw.sendflowers);
    private val caFromResources: InputStream?
        get() = null

    @Provides
    @PerApplication
    internal fun provideHttpCache(application: Application): Cache {
        mApplication = application
        val cacheSize = 10 * 1024 * 1024

        return Cache(application.cacheDir, cacheSize.toLong())
    }

    @Provides
    @PerApplication
    internal fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.serializeNulls()

        gsonBuilder.registerTypeAdapter(Date::class.java, object : JsonDeserializer<Date> {
            override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Date {
                val dateFormatList = ArrayList<DateFormat>()
                dateFormatList.add(SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
                dateFormatList.add(SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS"))
                dateFormatList.add(SimpleDateFormat("yyyy-MM-dd"))
                for (date in dateFormatList) {
                    try {
                        val res = json?.getAsString()?.replace("\"", "")
                        return date.parse(res)
                    } catch (e: ParseException) {
                        //Crashlytics.logException(e)
                        Log.e("GsonMapperDate", e.message)
                    }

                }
                return Calendar.getInstance().time
            }
        })
        //excluding realm fields from gson
        /* gsonBuilder.setExclusionStrategies(object : ExclusionStrategy {
             override fun shouldSkipField(f: FieldAttributes): Boolean {
                 return f.declaringClass == RealmObject::class.java
             }

             override fun shouldSkipClass(clazz: Class<*>): Boolean {
                 return false
             }
         })*/


        return gsonBuilder.create()
    }

    @Provides
    @PerApplication
    internal fun provideOkhttpClient(cache: Cache, userPreferences: UserPreferences): OkHttpClient {
        val client = OkHttpClient.Builder()
        // adding header "X-DEVICE-ID" to all requests


        // adding interceptor to dynamically generate token header
        val interceptor = AuthenticationInterceptor(userPreferences)
        client.addInterceptor(interceptor)


        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()

            logging.level = HttpLoggingInterceptor.Level.BODY

            client.addInterceptor(logging)
        }


        client.connectTimeout(20, TimeUnit.SECONDS)
        client.writeTimeout(20, TimeUnit.SECONDS)
        client.readTimeout(20, TimeUnit.SECONDS)
        client.cache(null)
        return client.build()

    }


    /*Network service*/
    @Provides
    @PerApplication
    fun providesApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }


    @Provides
    @PerApplication
    internal fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        val url = BASE_URL
                //if (BuildConfig.DEBUG )DEBUG_BASE_URL else BASE_URL
        //val url = BASE_URL
        return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(url)
                .client(okHttpClient)
                .build()
    }


    // creating an SSLSocketFactory that uses our TrustManager
    private fun createSSLSocketFactory(managerFactory: TrustManagerFactory): SSLContext? {
        val PROTOCOL = "TLS"
        var sslContext: SSLContext? = null
        try {
            sslContext = SSLContext.getInstance(PROTOCOL)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        try {
            assert(sslContext != null)
            sslContext!!.init(null, managerFactory.trustManagers, null)
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        }

        return sslContext
    }

    // creating a TrustManager that trusts the CAs in our KeyStore
    private fun createTrustManagerCAs(keyStore: KeyStore): TrustManagerFactory? {
        val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
        var managerFactory: TrustManagerFactory? = null
        try {
            managerFactory = TrustManagerFactory.getInstance(tmfAlgorithm)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        try {
            assert(managerFactory != null)
            managerFactory!!.init(keyStore)
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        }

        return managerFactory
    }

    // creating a KeyStore containing our trusted CAs
    private fun createKeyStoreTrustedCAs(certificate: Certificate): KeyStore? {
        val ALIAS_CA = "ca"
        val keyStoreType = KeyStore.getDefaultType()
        var keyStore: KeyStore? = null
        try {
            keyStore = KeyStore.getInstance(keyStoreType)
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        }

        try {
            assert(keyStore != null)
            keyStore!!.load(null, null)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: CertificateException) {
            e.printStackTrace()
        }

        try {
            keyStore!!.setCertificateEntry(ALIAS_CA, certificate)
        } catch (e: KeyStoreException) {
            e.printStackTrace()
        }

        return keyStore
    }

    // loading CAs from an InputStream
    private fun loadCertificateAuthorityFromResources(): CertificateFactory? {
        val CERT_TYPE = "X.509"
        val certificateAuthority = caFromResources
        var certificateFactory: CertificateFactory? = null
        try {
            certificateFactory = CertificateFactory.getInstance(CERT_TYPE)
        } catch (e: CertificateException) {
            e.printStackTrace()
        }

        try {
            assert(certificateFactory != null)
            certificateFactory!!.generateCertificate(certificateAuthority)
        } catch (e: CertificateException) {
            e.printStackTrace()
        } finally {
            try {
                certificateAuthority!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        return certificateFactory
    }

}
