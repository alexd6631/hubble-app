//
//  LazyView.swift
//  iosApp
//
//  Created by Alexandre Delattre on 21/12/2019.
//

import SwiftUI


struct LazyView<Content: View>: View {
    let build: () -> Content
    init(_ build: @autoclosure @escaping () -> Content) {
        self.build = build
    }
    var body: Content {
        build()
    }
}
