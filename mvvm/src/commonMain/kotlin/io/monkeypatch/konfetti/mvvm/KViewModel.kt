package io.monkeypatch.konfetti.mvvm


expect abstract class KViewModel() {
    protected open fun onDestroyed()
}