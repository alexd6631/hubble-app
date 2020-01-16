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

class ViewModelObserver : ObservableObject {
    let subject = ObservableObjectPublisher()
    
    let objectWillChange: AnyPublisher<(), Never>
    //var objectWillChange =
    
    let liveDatas: [MvvmKLiveData<AnyObject>]
    var disposables: [MvvmDisposable] = []
    
    init(_ vm: BaseViewModel) {
        print("LiveDataObserver inited")
        self.liveDatas = vm.liveDataList
    
        objectWillChange = subject.handleEvents(
            receiveSubscription: {
                s in print("Subscription \(s)")
        }, receiveCancel: {
            print("Cancel")
        }).eraseToAnyPublisher()
    }
    
    /*init(_ liveDatas: [MvvmKLiveData<AnyObject>]) {
        print("LiveDataObserver inited")
        self.liveDatas = liveDatas
    }*/
    
    func startObserving() {
        stopObserving()
        self.disposables = liveDatas.map { [unowned self] ld in ld.observeForever { _ in
            print("objectWillChange")
            self.subject.send()
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


extension MvvmKLiveData{
    @objc func eraseType() ->  MvvmKLiveData<AnyObject>{
        self as! MvvmKLiveData<AnyObject>
    }
}

extension View {
    func observe(observer: ViewModelObserver) -> some View {
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

extension BaseViewModel {
    func observer() -> ViewModelObserver {
        ViewModelObserver(self)
    }
}
