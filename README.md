Use of Rest APIs is a common pattern to Android applications. Many applications use Android's builtin HttpClient library by default, and Google Gson library to convert raw JSON response to Java Objects. The Spring Android framework has a module RestTemplate that provides a easy-to-use RestClient.

To test Rest based Android applications in isolation, we can provide a custom RequestFactory, that overrides default RestTemplate behavior. This way, we can test-driven develop an entire Android Application, even when server-side counterpart does not yet exist.

See FakeWeb-Android Sample (https://github.com/fabiolnm/FakeWeb-Android-Sample-Project) for a working example.
