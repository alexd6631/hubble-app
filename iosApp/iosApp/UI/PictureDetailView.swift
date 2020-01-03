//
//  PictureDetailView.swift
//  iosApp
//
//  Created by Alexandre Delattre on 21/12/2019.
//
import app
import SwiftUI
import URLImage

struct PictureDetailView : View {

    let title: String
    let viewModel: PictureDetailViewModel
    @ObservedObject var observer: LiveDataObserver
    
    @State var descriptionIsPresented = false
    
    var body: some View {
        print("Rendering detail view \(title)")
        return ZStack {
            viewModel.detail.value.map { p in PictureDetailContentView(picture: p) }
            
            if viewModel.detail.value == nil {
                CircleActivityView().frame(width: 50, height: 50)
            }
        }
        .sheet(isPresented: $descriptionIsPresented) {
            PictureInfoView(detail: self.viewModel.detail.value?.pictureDescription ?? "")
        }
        .navigationBarTitle(title)
        .navigationBarItems(trailing: navigationBarItem())
        .observe(observer: observer)
        .onAppear {
            print("Detail view appear")
        }
        .onDisappear {
            print("Detail view disappear")
        }
    }
    
    func navigationBarItem() -> some View {
        HStack {
            if (viewModel.detail.value != nil && viewModel.detail.value?.pictureDescription != nil) {
                Button(action: {
                    self.descriptionIsPresented = true
                }, label: {
                    Image(systemName: "info.circle.fill")
                })
            }
        }
    }
}

fileprivate struct PictureInfoView : View  {
    let detail: String
     var body: some View {
        VStack(alignment: .leading) {
            HStack {
                Text("Information").bold().font(.largeTitle)
                Spacer()
            }.padding(.vertical)
           
            ScrollView {
                 Text(detail)
            }
            }.padding()
        
    }
}

func createPictureDetailView(title: String, id: String) -> PictureDetailView {
    let viewModel = Container().pictureDetailViewModel(id: id)
    let observer = LiveDataObserver([
        viewModel.detail.eraseType()
    ])
    
    return PictureDetailView(title: title, viewModel: viewModel, observer: observer)
}

fileprivate struct PictureDetailContentView : View {
    var picture: HubblePictureDetail
    
    @State var scale: CGFloat = 1.0
    
    @State private var offset: CGSize = .zero
    
    var body: some View {
        let url = picture.imageUrl.map { URL(string: $0)! }
        
        return ZStack {
            if url != nil {
                URLImage(url!) { proxy in
                    proxy.image.resizable().aspectRatio(contentMode: .fit)
                        
                        .gesture(DragGesture().onChanged { value in
                            self.offset.width += value.translation.width
                            self.offset.height += value.translation.height
                            
                        }).offset(x: self.offset.width, y: self.offset.height)
                        
                        .gesture(MagnificationGesture()
                            .onChanged { value in
                                self.scale *= value
                            }
                    ).scaleEffect(self.scale)
                }//
            } else {
                Text("No associated image")
            }
        }
        
        
        
    }
}


#if DEBUG
struct SheetPreview_Previews: PreviewProvider {
    
    static var previews: some View {
        NavigationView {
            PictureInfoView(detail: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")
        }
        
    }
}

#endif
