I'll create a comprehensive documentation for the SharePlate Android application.

# SharePlate Documentation

## Overview
SharePlate is an Android application designed to facilitate food and non-food item donations within communities. The app connects donors with recipients, promoting community engagement and reducing waste through an easy-to-use platform.

## Technical Stack
- **Platform**: Android (minimum SDK 24)
- **Languages**: Java & Kotlin
- **Backend**: Firebase
  - Authentication
  - Cloud Firestore
  - Cloud Storage
- **Key Dependencies**:
  - Firebase BoM
  - AndroidX components
  - Material Design components
  - Glide for image loading

## Project Structure

### Core Components

1. **Authentication System**
- Secure login and signup functionality
- Google Sign-in integration
- Profile management system

2. **Donation Management**
- Food Items

```53:66:SharePlateApp/app/src/main/java/com/shareplateapp/DonationItem.java
    public String getOwnerProfileImageUrl() { return ownerProfileImageUrl; }
    public long getCreatedAt() { return createdAt; }
    public String getDonateType() { return donateType; }
    public String getFormattedCreationDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.US);
        return sdf.format(new Date(createdAt));
    }

    // Setters
    public void setDocumentId(String documentId) { this.documentId = documentId; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
```


- Non-Food Items

```8:120:SharePlateApp/app/src/main/java/com/shareplateapp/NonFoodItem.java
public class NonFoodItem implements Serializable {

    private String name;
    private String category;
    private String description;
    private String quantity;
    private String pickupTime;
    private String location;
    private int imageResourceId;
    private String imageUrl;
    private String ownerUsername;
    private String documentId;
    private String status; // "active" or "completed"
    private long createdAt;
    private String ownerProfileImageUrl;
    private String donateType;

    public NonFoodItem(String name, String category, String description, String quantity,
                       String pickupTime, String location, int imageResourceId, String imageUrl,
                       String ownerUsername, String ownerProfileImageUrl, String donateType) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.quantity = quantity;
        this.pickupTime = pickupTime;
        this.location = location;
        this.imageResourceId = imageResourceId;
        this.imageUrl = imageUrl;
        this.ownerUsername = ownerUsername;
        this.ownerProfileImageUrl = ownerProfileImageUrl;
        this.donateType = donateType;
        this.status = "active";
        this.createdAt = System.currentTimeMillis();
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public String getLocation() {
        return location;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getFormattedCreationDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.US);
        return sdf.format(new Date(createdAt));
    }

    public String getOwnerProfileImageUrl() {
        return ownerProfileImageUrl;
    }

    public String getDonateType() {
        return donateType;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
```


3. **Community Features**
- Events and Campaigns management

```1:12:SharePlateApp/app/src/main/java/com/shareplateapp/Campaign.java
package com.shareplateapp;

public class Campaign extends Event {
    
    public Campaign(String name, String desc, String date, String time, String typeOfEvents, 
                   String seatAvailable, String location, int img, String imageUrl, String ownerImageUrl) {
        super(name, desc, date, time, typeOfEvents, seatAvailable, location, img, imageUrl, ownerImageUrl);
    }

    // Add any Campaign-specific methods or fields here
}

```


### Repository Pattern
The app implements the Repository pattern for data management:

1. **DonationItemRepository**

```16:87:SharePlateApp/app/src/main/java/com/shareplateapp/DonationItemRepository.java
public class DonationItemRepository {
    private static final String TAG = "DonationItemRepository";
    private static final String COLLECTION_NAME = "allDonationItems";
    private final FirebaseFirestore db;

    // Define the interface for callbacks
    public interface OnDonationCompleteListener {
        void onDonationSuccess();
        void onDonationFailure(Exception e);
    }

    public DonationItemRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public interface OnDonationItemsLoadedListener {
        void onDonationItemsLoaded(List<DonationItem> items);
        void onError(Exception e);
    }

...
                            String status = document.getString("status");
                            String ownerProfileImageUrl = document.getString("ownerProfileImageUrl");
                            String donateType = document.getString("donateType");

                            int imageResourceId = R.drawable.placeholder_image;
                            Long resourceIdLong = document.getLong("imageResourceID");
                            if (resourceIdLong != null) {
                                imageResourceId = resourceIdLong.intValue();
                            }

                            String imageUrl = document.getString("imageUrl");

                            Long createdAt = document.getLong("createdAt");

                            if (name != null && !name.isEmpty()) {
                                DonationItem item = new DonationItem(
                                        name,
                                        foodCategory != null ? foodCategory : "",
                                        description != null ? description : "",
                                        category != null ? category : "",
                                        expiredDate != null ? expiredDate : "",
                                        quantity != null ? quantity : "",
                                        pickupTime != null ? pickupTime : "",
                                        location != null ? location : "",
                                        imageResourceId,
                                        imageUrl,
                                        ownerUsername != null ? ownerUsername : "Anonymous",
                                        donateType,
                                        ownerProfileImageUrl != null ? ownerProfileImageUrl : ""
                                );
                                // Set the document ID and status
                                item.setDocumentId(document.getId());
                                item.setStatus(status != null ? status : "active");
                                if (createdAt != null) {
                                    item.setCreatedAt(createdAt);
                                }
```


2. **NonFoodItemRepository**

```11:110:SharePlateApp/app/src/main/java/com/shareplateapp/NonFoodItemRepository.java
public class NonFoodItemRepository {
    private static final String COLLECTION_NAME = "allNonFoodItems";
    private final FirebaseFirestore db;

    public NonFoodItemRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public interface OnNonFoodItemsLoadedListener {
        void onNonFoodItemsLoaded(List<NonFoodItem> items);
        void onError(Exception e);
    }

    public void getAllNonFoodItems(OnNonFoodItemsLoadedListener listener) {
        db.collection(COLLECTION_NAME)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<NonFoodItem> items = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            String name = document.getString("name");
                            String category = document.getString("category");
                            String description = document.getString("description");
                            String quantity = document.getString("quantity");
                            String pickupTime = document.getString("pickupTime");
                            String location = document.getString("location");
                            String ownerUsername = document.getString("ownerUsername");
                            String status = document.getString("status");

                            int imageResourceId = R.drawable.placeholder_image;
                            Long resourceIdLong = document.getLong("imageResourceID");
                            if (resourceIdLong != null) {
                                imageResourceId = resourceIdLong.intValue();
                            }

                            String imageUrl = document.getString("imageUrl");

                            Long createdAt = document.getLong("createdAt");

                            if (name != null && !name.isEmpty()) {
                                NonFoodItem item = new NonFoodItem(
                                        name,
                                        category != null ? category : "",
                                        description != null ? description : "",
                                        quantity != null ? quantity : "",
                                        pickupTime != null ? pickupTime : "",
                                        location != null ? location : "",
                                        imageResourceId,
                                        imageUrl != null ? imageUrl : "",
                                        ownerUsername != null ? ownerUsername : "Anonymous",
                                        document.getString("ownerProfileImageUrl"),
                                        document.getString("donateType")
                                );
                                // Set the document ID and status
                                item.setDocumentId(document.getId());
                                item.setStatus(status != null ? status : "active");
...
            nonFoodData.put("imageUrl", item.getImageUrl());
            nonFoodData.put("ownerUsername", item.getOwnerUsername());
            nonFoodData.put("status", item.getStatus());
            nonFoodData.put("createdAt", System.currentTimeMillis());

            db.collection(COLLECTION_NAME)
                    .add(nonFoodData)
                    .addOnSuccessListener(documentReference -> {
                        String docId = documentReference.getId();
                        // Set the document ID in the item
                        item.setDocumentId(docId);
                        // No need to update the document with its ID since we can use the Firestore ID
                        System.out.println("Document added with ID: " + docId);
                    })
                    .addOnFailureListener(e -> {
                        System.err.println("Error adding document: " + e);
                    });
        } catch (Exception e) {
            System.err.println("Error creating donation data: " + e);
        }
```


## Key Features

### 1. Item Donation
- Support for both food and non-food items
- Image upload capability
- Detailed item information
- Real-time status tracking

### 2. Community Engagement
- Campaign creation and participation
- Event management
- Community-wide notifications
- Search functionality

### 3. User Interface
- Material Design implementation
- Responsive layouts
- Custom animations
- Dark mode support

## Firebase Integration

### Configuration
Firebase configuration is managed through:

```1:39:SharePlateApp/app/google-services.json
{
  "project_info": {
    "project_number": "1092408262320",
    "project_id": "shareplate-da99a",
    "storage_bucket": "shareplate-da99a.firebasestorage.app"
  },
  "client": [
    {
      "client_info": {
        "mobilesdk_app_id": "1:1092408262320:android:60e21f672d66f6bda7c575",
        "android_client_info": {
          "package_name": "com.example.shareplate"
        }
      },
      "oauth_client": [
        {
          "client_id": "1092408262320-ujt32titu09id3dli8q6rf3gp9cn500h.apps.googleusercontent.com",
          "client_type": 3
        }
      ],
      "api_key": [
        {
          "current_key": "AIzaSyCchGmLOwyojIg50nYerwe27kEqZl5W6gQ"
        }
      ],
      "services": {
        "appinvite_service": {
          "other_platform_oauth_client": [
            {
              "client_id": "1092408262320-ujt32titu09id3dli8q6rf3gp9cn500h.apps.googleusercontent.com",
              "client_type": 3
            }
          ]
        }
      }
    }
  ],
  "configuration_version": "1"
}
```


### Storage Rules
- Secure file upload/download
- Image storage for donation items
- Profile picture storage

## Setup Instructions

1. **Prerequisites**
   - Android Studio
   - JDK 17 or higher
   - Firebase account

2. **Installation Steps**
   ```bash
   # Clone the repository
   git clone [repository-url]
   
   # Open in Android Studio
   # Sync Gradle files
   # Configure Firebase
   ```

3. **Firebase Setup**
   - Create a new Firebase project
   - Add Android app to Firebase project
   - Download and add google-services.json
   - Enable required Firebase services (Authentication, Firestore, Storage)

## Security Considerations

1. **Data Privacy**
- User data encryption
- Secure file storage
- Permission-based access control

2. **Android Permissions**

```5:10:SharePlateApp/app/src/main/AndroidManifest.xml
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-feature android:name="android.hardware.camera" android:required="false" />
```


## Testing

The project includes both unit tests and instrumentation tests:

```1:17:SharePlateApp/app/src/test/java/com/shareplateapp/ExampleUnitTest.java
package com.shareplateapp;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
}
```


## Contributing Guidelines

1. **Code Style**
- Follow Android best practices
- Implement MVVM architecture
- Use Kotlin for new features
- Maintain proper documentation

2. **Pull Request Process**
- Create feature branch
- Update documentation
- Submit PR with detailed description
- Ensure all tests pass

## License
This project is licensed under the Apache License 2.0.
