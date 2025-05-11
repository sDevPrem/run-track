//
//  ContentView.swift
//  run-track
//
//  Created by Prem Thakur on 08/12/24.
//

import SwiftUI
import shared

struct ContentView: View {
    @StateObject private var viewModel = LocationViewModel()
    
    var body: some View {
        VStack {
            Image(systemName: "globe")
                .imageScale(.large)
                .foregroundStyle(.tint)
            if let x = viewModel.locationInfo {
                let lat = String(format: "%f", x.locationInfo.latitude)
                let long = String(format: "%f", x.locationInfo.longitude)
                Text("lat: " + lat + " long: " + long)
            } else {
                Text("hello world")
            }
        }
        .padding()
    }
}

#Preview {
    ContentView()
}
