//
//  run_trackApp.swift
//  run-track
//
//  Created by Prem Thakur on 08/12/24.
//

import SwiftUI
import GoogleMaps
import shared


@main
struct run_trackApp: App {

    init() {
        let apiKey = Bundle.main.object(forInfoDictionaryKey: "GoogleMapsApiKey") as? String ?? ""
        GMSServices.provideAPIKey(apiKey)
        KoinInitIosKt.doInitKoinIos()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
