//
//  ViewModelObserver.swift
//  iosApp
//
//  Created by Alexandre Delattre on 19/12/2019.
//

import Foundation
import app
import SwiftUI
import Combine

extension BaseViewModel : ObservableObject {
    
    public var objectWillChange: AnyPublisher<(), Never> {
        get {
            if let observer = self.swiftData as? ViewModelObserver {
                return observer.objectWillChange
            } else {
                let observer = ViewModelObserver(self)
                self.swiftData = observer
                return observer.objectWillChange
            }
        }
    }
}

class ViewModelObserver {
    private let subject = ObservableObjectPublisher()
    
    private(set) var objectWillChange: AnyPublisher<(), Never> = Empty().eraseToAnyPublisher()
    //var objectWillChange =
    private var subscriptionCount = 0
    
    private let liveDatas: [MvvmKLiveData<AnyObject>]
    private var disposables: [MvvmDisposable] = []
    
    init(_ vm: BaseViewModel) {
        self.liveDatas = vm.liveDataList
    
        objectWillChange = subject.handleEvents(
            receiveSubscription: { [weak self] s in
                if let self = self {
                    self.subscriptionCount += 1
                    if self.subscriptionCount == 1 {
                        self.startObserving()
                    }
                }
                
                
        }, receiveCancel: { [weak self] in
            if let self = self {
                self.subscriptionCount -= 1
                if self.subscriptionCount == 0 {
                    self.stopObserving()
                }
            }
        }).eraseToAnyPublisher()
    }
    
    func startObserving() {
        stopObserving()
        self.disposables = liveDatas.map { [unowned self] ld in ld.observeForever { _ in
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
        }.onDisappear() {
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
