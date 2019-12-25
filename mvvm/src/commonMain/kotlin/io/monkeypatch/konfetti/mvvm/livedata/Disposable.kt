package io.monkeypatch.konfetti.mvvm.livedata

interface Disposable {
    fun dispose()
}

fun disposable(f: () -> Unit): Disposable = object : Disposable {
    override fun dispose() = f()
}