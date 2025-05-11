//
//  ContentViewModel.swift
//  run-track
//
//  Created by Prem Thakur on 11/05/25.
//

import shared

class LocationViewModel: ObservableObject, LocationTrackingManagerLocationCallback {
    @Published var locationInfo: LocationTrackingInfo? = nil

    private var locationManager: LocationTrackingManager = DefaultLocationTrackingManager()

    init() {
        locationManager.setCallback(locationCallback: self)
    }

    func onLocationUpdate(results: [LocationTrackingInfo]) {
        DispatchQueue.main.async {
            self.locationInfo = results.last
        }
    }
}

