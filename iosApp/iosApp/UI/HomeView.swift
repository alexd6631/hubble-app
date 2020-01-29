//
//  HomeView.swift
//  iosApp
//
//  Created by Alexandre Delattre on 21/12/2019.
//

import SwiftUI


struct HomeView: View {
    var body: some View {
        NavigationView {
            createListPictureView()
        }.navigationViewStyle(StackNavigationViewStyle())
    }
}
