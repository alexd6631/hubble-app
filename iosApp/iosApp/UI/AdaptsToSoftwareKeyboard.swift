//
//  AdaptsToSoftwareKeyboard.swift
//  iosApp
//
//  Created by Alexandre Delattre on 24/01/2020.
//
import SwiftUI
import Combine

struct AdaptsToSoftwareKeyboard: ViewModifier {
    
    @State var currentHeight: CGFloat = 0
    
    func body(content: Content) -> some View {
        content
            .padding(.bottom, self.currentHeight)
            .edgesIgnoringSafeArea(self.currentHeight == 0 ? Edge.Set() : .bottom)
            .onReceive(Publishers.Merge(keyboardWillOpen, keyboardWillHide)
                .print()
                .receive(on: RunLoop.main), perform: { height in
                    self.currentHeight = height
            })
            //.onAppear(perform: subscribeToKeyboardEvents)
    }
    
    private let keyboardWillOpen = NotificationCenter.default
        .publisher(for: UIResponder.keyboardWillShowNotification)
        .map { $0.userInfo![UIResponder.keyboardFrameEndUserInfoKey] as! CGRect }
        .map { $0.height }
    
    private let keyboardWillHide =  NotificationCenter.default
        .publisher(for: UIResponder.keyboardWillHideNotification)
        .map { _ in CGFloat.zero }
    
    private func subscribeToKeyboardEvents() {
        _ = Publishers.Merge(keyboardWillOpen, keyboardWillHide)
            .print()
            .subscribe(on: RunLoop.main)
            .assign(to: \.self.currentHeight, on: self)
    }
}
