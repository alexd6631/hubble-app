//
//  LiveDataObserver.swift
//  iosApp
//
//  Created by Alexandre Delattre on 19/12/2019.
//

import Foundation
import app
import SwiftUI
import Combine

class LiveDataObserver : ObservableObject {
    let objectWillChange = ObservableObjectPublisher()
    
    let liveDatas: [MvvmKLiveData<AnyObject>]
    var disposables: [MvvmDisposable] = []
    init(_ liveDatas: [MvvmKLiveData<AnyObject>]) {
        print("LiveDataObserver inited")
        self.liveDatas = liveDatas
    }
    
    func startObserving() {
        stopObserving()
        self.disposables = liveDatas.map { [unowned self] ld in ld.observeForever { _ in
            print("objectWillChange")
            self.objectWillChange.send()
            }
        }
    }
    
    func stopObserving() {
        disposables.forEach {
            d in d.dispose()
        }
        disposables = []
    }
    
    deinit {
        print("LiveDataObserver deinited")
        stopObserving()
    }
}

class VmLiveDataObserver<T> : LiveDataObserver {
    let viewModel: T
    
    init(_ liveDatas: [MvvmKLiveData<AnyObject>], viewModel: T) {
        self.viewModel = viewModel
        super.init(liveDatas)
        
    }
}

extension MvvmKLiveData{
    @objc func eraseType() ->  MvvmKLiveData<AnyObject>{
        self as! MvvmKLiveData<AnyObject>
    }
}

extension View {
    func observe(observer: LiveDataObserver) -> some View {
        onAppear {
            observer.startObserving()
            print("Appear")
        }.onDisappear() {
            print("Disappear")
            observer.stopObserving()
        }
    }
}

func bindingFor(ld: MvvmKMutableLiveData<NSString>) -> Binding<String> {
    Binding(get: {
        ld.value! as String
    }, set: { (v: String) in
        ld.value = v as NSString
    })
}
