package io.monkeypatch.konfetti.mvvm

import androidx.lifecycle.ViewModel

actual abstract class KViewModel : ViewModel() {
    protected actual open fun onDestroyed() { }
    override fun onCleared() {
        onDestroyed()
    }


}