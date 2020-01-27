//
//  PseudoCodeView.swift
//  iosApp
//
//  Created by Alexandre Delattre on 24/01/2020.
//

import SwiftUI

struct AppData {}

class ViewModel: ObservableObject {
    @Published var data: AppData? = nil
}

struct SomeView: View {
    @ObservedObject var viewModel: ViewModel
    
    var body: some View {
        ZStack {
            if viewModel.data == nil {
                ProgressIndicator()
            } else {
                DataList(data: viewModel.data!)
            }
        }
    }
}

struct ProgressIndicator: View {
    var body: some View {
        Text("")
    }
}

struct DataList: View {
    let data: AppData
    var body: some View {
        Text("")
    }
}
