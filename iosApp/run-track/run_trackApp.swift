//
//  run_trackApp.swift
//  run-track
//
//  Created by Prem Thakur on 08/12/24.
//

import SwiftUI
import GoogleMaps

@main
struct run_trackApp: App {

    init() {
        let apiKey = Bundle.main.object(forInfoDictionaryKey: "GoogleMapsApiKey") as? String ?? ""
        GMSServices.provideAPIKey(apiKey)
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
