package io.monkeypatch.konfetti.mvvm.livedata

interface StateChangedListener {
    fun onStateChanged(state: BaseLifecycle.State)
}

abstract class BaseLifecycle {
    abstract fun addObserver(listener: StateChangedListener)

    abstract fun removeObserver(listener: StateChangedListener)

    abstract fun currentState(): State

    /**
     * Lifecycle states. You can consider the states as the nodes in a graph and
     * [Event]s as the edges between these nodes.
     */
    enum class State {
        /**
         * Destroyed state for a LifecycleOwner. After this event, this Lifecycle will not dispatch
         * any more events. For instance, for an [android.app.Activity], this state is reached
         * **right before** Activity's [onDestroy][android.app.Activity.onDestroy] call.
         */
        DESTROYED,

        /**
         * Created state for a LifecycleOwner. For an [android.app.Activity], this state
         * is reached in two cases:
         *
         *  * after [onCreate][android.app.Activity.onCreate] call;
         *  * **right before** [onStop][android.app.Activity.onStop] call.
         *
         */
        CREATED,

        /**
         * Started state for a LifecycleOwner. For an [android.app.Activity], this state
         * is reached in two cases:
         *
         *  * after [onStart][android.app.Activity.onStart] call;
         *  * **right before** [onPause][android.app.Activity.onPause] call.
         *
         */
        STARTED;

        /**
         * Compares if this State is greater or equal to the given `state`.
         *
         * @param state State to compare with
         * @return true if this State is greater or equal to the given `state`
         */
        fun isAtLeast(state: State): Boolean {
            return compareTo(state) >= 0
        }
    }
}


actual class KLifecycle : BaseLifecycle() {
    private val observers = mutableListOf<StateChangedListener>()

    var state: State = State.CREATED
        set(value) {
            field = value
            observers.forEach { it.onStateChanged(value) }
        }

    override fun addObserver(listener: StateChangedListener) {
        observers.add(listener)
        listener.onStateChanged(state)
    }

    override fun removeObserver(listener: StateChangedListener) {
        observers.remove(listener)
    }

    override fun currentState(): State = state

}