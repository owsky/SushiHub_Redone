# SushiHub_Redone
 
This is a Kotlin version of my Software Engineering project [SushiHub](https://github.com/owsky/SushiHub)


The purpose of this app is to facilitate the ordering process at a 'All-you-can-eat' restaurant by enabling P2P communication between the user's devices. This lets the users sync all their orders to one device, which will be shown to the waiter for the actual ordering. Communication is handled by the Nearby API, thus it requires WiFi and Bluetooth to exchange data.

The project implements the MVVM pattern and Single Activity Architecture and supports affiliated restaurants. Through this feature the system exposes an already compiled menu so it relieves the user from handling dish codes and descriptions, as it is already provided by the NoSQL database hosted through Firebase. Note: this feature is currently disabled as the server lived through its purpose but all the client's code is still available in this repository.
