//
//  ContentView.swift
//  iosApp
//
//  Created by Alexandre Delattre on 19/12/2019.
//

import SwiftUI
import app
import URLImage


//struct ListPicturesView: View {
//    var viewModel: ListPicturesViewModel
//
//    @ObservedObject var observer: VmLiveDataObserver<ListPicturesViewModel>
//
//    var body: some View {
//        let pictures: [HubblePicture] = observer.viewModel.pictures.value as? [HubblePicture] ?? []
//        let loading = observer.viewModel.loading.value?.boolValue ?? false
//
//        print("Rendering view \(pictures.count)")
//        print("Disposables \(observer.disposables.count)")
//
//        return ListPicturesViewContent(
//            filterBinding: bindingFor(ld: observer.viewModel.filter),
//            pictures: pictures, loading: loading
//        ).accessibility(value: Text(observer.disposables.description))
//            .observe(observer: observer)
//    }
//}

struct ListPicturesView: View {
    var viewModel: ListPicturesViewModel

    @ObservedObject var observer: LiveDataObserver

    var body: some View {
        let pictures: [HubblePicture] = viewModel.pictures.value as? [HubblePicture] ?? []
        let loading = viewModel.loading.value?.boolValue ?? false

        return ListPicturesViewContent(
            filterBinding: bindingFor(ld: viewModel.filter),
            pictures: pictures, loading: loading
        ).accessibility(value: Text(observer.disposables.description))
            .observe(observer: observer)
    }
}

fileprivate struct ListPicturesViewContent : View {
    let filterBinding: Binding<String>
    let pictures: [HubblePicture]
    let loading: Bool
    
    var body: some View {
        ZStack {
            VStack {
                FilterBar(filterBinding: filterBinding)
                
                List(self.pictures) { p in
                    HubblePictureRow(picture: p)
                }.opacity(self.loading ? 0.0 : 1.0)
            }
            
            CircleActivityView()
                .opacity(self.loading ? 1.0 : 0.0)
                .frame(width: 50.0, height: 50.0)
        }
        .navigationBarTitle("Hubble pictures")
        
        
    }
}

fileprivate struct FilterBar : View {
    let filterBinding: Binding<String>
    
    var body: some View {
        HStack {
            TextField("Filter", text: self.filterBinding.animation())
                
                .foregroundColor(.secondary)
            
            if (filterBinding.wrappedValue != "") {
                Button(action: {
                    self.filterBinding.wrappedValue = ""
                }) {
                    Image(systemName: "xmark.circle.fill")
                        .foregroundColor(.secondary)
                }
            }
            
            
        }
        .padding(EdgeInsets(top: 8, leading: 6, bottom: 8, trailing: 6))
        .background(Color(.secondarySystemBackground))
        .cornerRadius(10.0)
        .padding(.horizontal)
       
    }
}

fileprivate struct HubblePictureRow : View {
    var picture: HubblePicture
    
    var body: some View {
        NavigationLink(destination: createPictureDetailView(title: picture.name, id: picture.id)) {
            HStack {
                Text(self.picture.name)
            }
        }.onAppear {
            print("Row appeared \(self.picture.name)")
        }
    }
}

func createListPictureView() -> ListPicturesView {
    createListPictureView(viewModel: Container().listPicturesViewModel())
}

func createListPictureViewMock() -> ListPicturesView {
    createListPictureView(viewModel: Container().mockListPicturesViewModel())
}

func createListPictureView(viewModel: ListPicturesViewModel) -> ListPicturesView {
    
    let observer = LiveDataObserver([
        viewModel.pictures.eraseType(),
        viewModel.loading.eraseType()
    ])
    
    return ListPicturesView(viewModel: viewModel, observer: observer)
    
    //return ListPicturesView(observer: observer)
}

extension HubblePicture : Identifiable {}

#if DEBUG
struct ListPicturesContentView_Previews: PreviewProvider {
    
    static var previews: some View {
        NavigationView {
            ListPicturesViewContentDemo()
        }
        
    }
}

struct ListPicturesViewContentDemo : View {
    @State var filter: String = ""
    
    var body: some View {
        ListPicturesViewContent(
            filterBinding: $filter,
            pictures: [
                HubblePicture(id: "1", name: "Test 1", mission: "Hubble"),
                HubblePicture(id: "2", name: "Test 2",
                              mission: "Hubble"),
                HubblePicture(id: "3", name: "Test 3", mission: "Hubble"),
                 HubblePicture(id: "4", name: "Test 4", mission: "Hubble")
            ],
            loading: false
        )
    }
}
#endif
