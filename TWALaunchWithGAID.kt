class LauncherActivity : AppCompatActivity() {

    var mClient: CustomTabsClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch(Dispatchers.IO) {     // Launches a coroutine in the IO dispatcher and gets GA_ID
            retrieveGAID()?.let { gaid ->
                launch(Dispatchers.Main) {
                    CustomTabsClient.bindCustomTabsService(
                        this@LauncherActivity,
                        "com.android.chrome",
                        getConnection("https://domain.example?gps_adid=$gaid")
                    );
                }
            }
        }
    }

    private fun retrieveGAID(): String? {   // To retrieve GA_ID using AdvertisingIdClient dependency
        return try {
            val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(this)
            adInfo.id
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun getConnection(url: String): CustomTabsServiceConnection {
        return object : CustomTabsServiceConnection() {
            override fun onServiceDisconnected(componentName: ComponentName) {
                mClient = null
            }

            override fun onCustomTabsServiceConnected(
                name: ComponentName,
                client: CustomTabsClient
            ) {
                mClient = client
                mClient!!.warmup(0)
                val customTabsSession: CustomTabsSession =
                    mClient!!.newSession(CustomTabsCallback()) ?: return
                val intentBuilder = TrustedWebActivityIntentBuilder(Uri.parse(url))
                val intent = intentBuilder.build(customTabsSession)
                intent.launchTrustedWebActivity(this@LauncherActivity)
            }
        }
    }
}