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
    @ObservedObject var viewModel: PictureDetailViewModel
    
    var body: some View {
        PictureDetailContentView(
            title: title,
            detail: viewModel.detail.value
        )
    }

}


fileprivate struct PictureDetailContentView : View {
    let title: String
    let detail: HubblePictureDetail?
    @State var descriptionIsPresented = false
    
    var body: some View {
        ZStack {
            detail.map { p in PictureDetailImageView(picture: p) }
            
            if detail == nil {
                CircleActivityView().frame(width: 50, height: 50)
            }
        }
        .sheet(isPresented: $descriptionIsPresented) {
            PictureInfoView(
                title: self.title,
                detail: self.detail?.pictureDescription ?? ""
            )
        }
        .navigationBarTitle(Text(title), displayMode: .inline)
        .navigationBarItems(trailing: navigationBarItem())
    }
    
    func navigationBarItem() -> some View {
           Group {
               if (detail != nil && detail?.pictureDescription != nil) {
                   Button(action: {
                       self.descriptionIsPresented = true
                   }, label: {
                    HStack {
                        Image(systemName: "info.circle.fill")
                        Text("Infos")
                    }
                   })
               }
           }
       }
}

fileprivate struct PictureInfoView : View  {
    let title: String
    let detail: String
    
    @Environment(\.presentationMode) var presentationMode
    
    var body: some View {
        NavigationView {
            ScrollView {
                Text(detail)
            }.padding()
                .navigationBarTitle(Text(title), displayMode: .inline)
                .navigationBarItems(leading: Button(action: {
                    self.presentationMode.wrappedValue.dismiss()
                }) {
                    Image(systemName: "xmark")
                })
        }
    }
}

func createPictureDetailView(title: String, id: String) -> PictureDetailView {
    let viewModel = Container().pictureDetailViewModel(id: id)
    
    return PictureDetailView(
        title: title,
        viewModel: viewModel
    )
}

fileprivate struct PictureDetailImageView : View {
    var picture: HubblePictureDetail
    
    @State var scale: CGFloat = 1.0
    
    @State private var offset: CGSize = .zero
    
    var body: some View {
        let url = picture.imageUrl.map { URL(string: $0)! }
        
        return ZStack {
            if url != nil {
                URLImage(url!, incremental: true) { proxy in
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

fileprivate struct PictureDetailContentViewDemo : View {
    let title: String
    
    var body: some View {
        PictureDetailContentView(title: title, detail: HubblePictureDetail(pictureDescription: "Some description", imageUrl: "https://imgsrc.hubblesite.org/hvi/uploads/image_file/image_attachment/31930/STScI-H-p1960a-f3840x2160.tif"))
    }
}

fileprivate struct PictureDetailContentViewLoading : View {
    
    var body: some View {
        PictureDetailContentView(title: "Sample title very very very long", detail: nil)
    }
}

struct PictureDetailPreview : PreviewProvider {
     
    static var previews: some View {
        Group {
            
            NavigationView {                    PictureDetailContentViewDemo(title: "Sample title very very very long")
            }
            
            NavigationView {                    PictureDetailContentViewLoading()
            }
            
            NavigationView {                    PictureDetailContentViewDemo(title: "DarkMode")
            }.environment(\.colorScheme, .dark)
        }
        
    }
}

struct SheetPreview_Previews: PreviewProvider {
    
    private static func pictureInfoView() -> some View {
        PictureInfoView(title: "Info", detail: "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")
    }
    
    static var previews: some View {
        Group {
            NavigationView {
                pictureInfoView()
            }
            
            NavigationView {
                pictureInfoView()
            }.environment(\.colorScheme, .dark)
        }
        
        
    }
}

#endif
