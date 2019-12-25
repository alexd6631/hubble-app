package io.monkeypatch.konfetti.mvvm

actual abstract class KViewModel {
    protected actual open fun onDestroyed() {
    }
}