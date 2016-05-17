package org.grails.plugins.perf4j

/**
 *  Global cache for profiling options of controllers.
 */
@groovy.transform.CompileStatic
class ControllerProfiledOptionsCache {
    // profiling options for each controller which has a "profiled" property of type Closure (controller name is map key)
    // this is needed to cache the options, so we don't have to execute the closure upon each request
    private Map<String, Map<String, Map>> profilingOptions = [:].asSynchronized()

    boolean hasOptionsForController(String controllerName) {
        profilingOptions.containsKey(controllerName)
    }

    void evaluateDSL(String controllerName, Closure callable) {
        // run closure with builder as delegate
        def builder = new ProfiledOptionsBuilder()
        callable.delegate = builder
        callable.resolveStrategy = Closure.DELEGATE_ONLY
        callable.call()
        profilingOptions[controllerName] = builder.profiledMap as Map
    }

    Map getOptions(String controllerName, String actionName) {
        profilingOptions[controllerName]?.get(actionName)
    }

    void invalidateCacheForController(String controllerName) {
        profilingOptions.remove(controllerName)
    }
}
