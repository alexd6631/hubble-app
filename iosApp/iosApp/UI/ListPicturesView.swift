//
//  ContentView.swift
//  iosApp
//
//  Created by Alexandre Delattre on 19/12/2019.
//

import SwiftUI
import app
import URLImage


struct ListPicturesView: View {
    var viewModel: ListPicturesViewModel
    @ObservedObject var observer: ViewModelObserver

    var body: some View {
        let pictures: [HubblePicture] = viewModel.pictures.value as? [HubblePicture] ?? []
        let loading = viewModel.loading.value?.boolValue ?? false

        return Group {
            ListPicturesViewContent(
                filter: bindingFor(ld: viewModel.filter),
                pictures: pictures, loading: loading
            ).accessibility(value: Text(observer.disposables.description))
                .observe(observer: observer)
            
            Text("Default view")
        }
        
        
    }
}

fileprivate struct ListPicturesViewContent : View {
    @Binding var filter: String
    let pictures: [HubblePicture]
    let loading: Bool
    
    var body: some View {
        ZStack {
            VStack {
                FilterBar(filter: $filter)
                
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
    @Binding var filter: String
//    let filterBinding: Binding<String>
    
    var body: some View {
        HStack {
            Image(systemName: "magnifyingglass")
                .foregroundColor(.secondary)
            TextField("Filter", text: self.$filter)
            
                .foregroundColor(.secondary)
                .disableAutocorrection(true)
            
            
            //if (filter != "") {
                Button(action: {
                    self.filter = ""
                }) {
                    Image(systemName: "xmark.circle.fill")
                        .foregroundColor(.secondary)
                }.opacity(filter != "" ? 1 : 0)
                    .animation(.easeOut(duration: 0.3))
                
            //}
            
            
        }
        .padding(EdgeInsets(top: 8, leading: 12, bottom: 8, trailing: 12))
        .background(Color(.secondarySystemBackground))
        .cornerRadius(.infinity)
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
    
    ListPicturesView(
        viewModel: viewModel,
        observer: viewModel.observer()
    )
}

extension HubblePicture : Identifiable {}

#if DEBUG
struct ListPicturesContentView_Previews: PreviewProvider {
    
    static var previews: some View {
        Group {
            previewLightAndDark {
                NavigationView {
                     ListPicturesViewContentDemo()
                }
            }.previewDisplayName("Picture list")
            
            previewLightAndDark {
                NavigationView {
                    ListPicturesViewContentDemoLoading()
                }
            }.previewDisplayName("Loading")
            
        }
    }
}

func previewLightAndDark<V : View>(view: () -> V) -> some View {
    Group {
        view()
        view().environment(\.colorScheme, .dark)
    }
}

fileprivate struct ListPicturesViewContentDemo : View {
    @State var filter: String = ""
    
    var body: some View {
        ListPicturesViewContent(
            filter: $filter,
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

fileprivate struct ListPicturesViewContentDemoLoading : View {
    @State var filter: String = ""
    
    var body: some View {
        ListPicturesViewContent(
            filter: $filter,
            pictures: [],
            loading: true
        )
    }
}
#endif
