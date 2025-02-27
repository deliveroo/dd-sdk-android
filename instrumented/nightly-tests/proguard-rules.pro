# Needed to make sure we don't remove any test code
-dontshrink
#-dontoptimize
#-keepattributes *Annotation*

# Required for some Kotlin-jvm implementation using reflection
-keepnames class kotlin.jvm.** { *; }

-keepnames class com.datadog.android.rum.internal.monitor.DatadogRumMonitor {
    public void resetSession();
 }

# Required because we need access to Datadog by reflection
-keepnames class com.datadog.android.Datadog {
    *;
}
# Required because we need access to GlobalRum.isRegistered by reflection
-keepnames class com.datadog.android.rum.GlobalRum {
    private java.util.concurrent.atomic.AtomicBoolean isRegistered;
}

# Required because we need access to GlobalTracer isRegistered property to reset it through reflection
-keepnames class io.opentracing.util.GlobalTracer {
    private boolean isRegistered;
}

# Required because we need access to RumContext fields by reflection
-keepnames class com.datadog.android.rum.internal.domain.RumContext {
    *;
}

# Required to be able to assert the crash related error and log events in the Monitors
-keepnames class com.datadog.android.nightly.exceptions.** { *;}
-keepnames class com.datadog.android.nightly.services.** { *; }

# Required to be able to assert the events produced by the instrumentation strategies in the Monitors
-keepnames class com.datadog.android.nightly.activities.** { *; }
-keepnames class com.datadog.android.nightly.fragments.** { *; }

# Required to be able to use ktor - local server
-keep class io.ktor.** { *; }
-keep class io.netty.** { *; }
